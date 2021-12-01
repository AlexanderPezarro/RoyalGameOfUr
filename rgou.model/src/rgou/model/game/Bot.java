package rgou.model.game;

import com.google.common.collect.Multimap;
import org.w3c.dom.Node;
import rgou.model.element.Board;
import rgou.model.element.Cell;
import rgou.model.rules.Ruleset;

import java.util.*;
import java.util.stream.Stream;

public class Bot {
    public static int DEPTH = 0;

    /**
     * Node representing a game state, probability of reaching the game state from its parent node
     * and the turn count
     * Children are identified by the roll required to get to them and the x1,y1,x2,y2 move
     */
    private class Node {
        private Map<int[], Node>[] children;   // move and states maps, indexed by roll, if singleton then no randomness occured
        private Game state;
        double prob;                  // chance of arriving at this node from the previous (roll, randomMoves, rerolls)
        int turn;                     // turn count relative to current turn


        private Node(Game state, double prob, int turn) {
            this.state = state;
            this.turn = turn;
            this.prob = prob;
        }
        
        /**
         * Generate all following game states from all possible rolls and legal moves.
         *
         * @param p1    whether bot is player 1
         */
        private void addChildren(boolean p1, int depth) {
            if(depth < DEPTH) {
                int diceN = state.getDices(p1);
                children = new Map[diceN + 1];

                for (int roll = 0; roll <= diceN; roll++) {
                    children[roll] = new HashMap<>();

                    Multimap<Cell, Cell> moves = state.getValidMoves(state.isPlayerOneTurn(), roll);
                    for (Map.Entry<Cell, Cell> move : moves.entries()) {
                        Game copy = state.deepCopy();
                        addChildrenRecursive(copy, depth, move.getKey(), move.getValue(), roll);
                    }
                }
            }
        }

        /**
         * Generate child nodes when roll is known
         *
         * @param roll
         */
        private void addChildren(int roll) {
            children = new Map[1];
            children[0] = new HashMap<>();

            Multimap<Cell, Cell> moves = state.getValidMoves(state.isPlayerOneTurn(), roll);
            for (Map.Entry<Cell, Cell> move : moves.entries()) {
                Game copy = state.deepCopy();
                addChildrenRecursive(copy, 0, move.getKey(), move.getValue(), roll);
            }
        }

        /**
         * Recursively add child nodes and their corresponding children until a new turn is reached
         * by all branches
         *
         * @param state
         * @param start     move start
         * @param end       move end
         *                  e.g. 1 for decision based moves, roll probability for roll moves
         * @param roll      optional roll of move towards child, N/A for decision based moves
         *                  e.g. teleport
         */
        private void addChildrenRecursive(Game state, int depth, Cell start, Cell end, int... roll) {
            System.out.println(depth);
            int dices = state.getDices(state.isPlayerOneTurn());
            rerollN = 0;

            Multimap<Cell,Cell> followUps = state.playMove(start, end); // play move, get follow ups

            if (end.isReroll() && !end.isUsed()) { rerollN++; }

            if (followUps.isEmpty()) {
                double prob = 1;    // moved by decision
                if (roll.length > 0) {      // moved by roll
                    prob = probs.get(dices)[roll[0]];
                }

                // single children grouping for known rolls (from root) and decision branching
                int rolled = (this == root || roll.length == 0)  ?  0  :  roll[0];

                Node nextState = new Node(state.deepCopy(), prob, turn + 1);
                children[rolled].put(new int[]{start.getX(), start.getY(), end.getX(), end.getY()},
                        nextState);

                if (rerollN > 0) {  // more rerolls to go before turn change
                    rerollN--;
                    nextState.addChildren(p1, depth + 1);
                }
            }

            else {  // multi legged turn
                for (Map.Entry<Cell, Cell> move : followUps.entries()) {
                    Cell intermediaryStart = move.getKey();
                    Cell intermediaryEnd = move.getValue();

                    Game intermediaryState = state.deepCopy();
                    Multimap<Cell, Cell> intermediaryFollowUps = intermediaryState.playMove(move.getKey(), move.getValue());

                    Node intermediaryNode = new Node(intermediaryState.deepCopy(), prob, this.turn);
                    children[0].put(new int[]{intermediaryStart.getX(), intermediaryStart.getY(), intermediaryEnd.getX(), intermediaryEnd.getY()},
                            intermediaryNode);
                    intermediaryNode.addSameTurnChildren(intermediaryFollowUps, depth);
                }
            }
        }

        private void addSameTurnChildren(Multimap<Cell, Cell> intermediaryFollowUps, int depth) {
            if(depth < DEPTH) {
                children = new Map[1];
                children[0] = new HashMap<>();
                for (Map.Entry<Cell, Cell> move : intermediaryFollowUps.entries()) {
                    addChildrenRecursive(state.deepCopy(), depth + 1, move.getKey(), move.getValue());
                }
            }
        }

        private Map<int[],Node> getChildren(int roll) {
            return (children.length == 1)  ?  children[0]  :  children[1];
        }

        private Map<int[],Node>[] getChildren() {
            return children;
        }

        public Game getState() {
            return state;
        }
    }


    private boolean p1;     // isPlayer1
    public Node root;       // last known state, turn 0
    private long timeThreshold;      // time to build/update tree
    public int depth;
    int rerollN = 0;

    private Map<Integer, double[]> probs;      // probabilities of all dice rolls

    public Bot(long timeThreshold, int depth) {
        this.depth = depth;
        this.timeThreshold = timeThreshold;
        this.probs = calcAllRollProb(Ruleset.MIN_DICES, Ruleset.MAX_DICES);
    }

    /**
     *
     * @param state current game state
     * @param roll  last roll, used for faster search, if going first it can be any int
     */
    public void updateTree(Game state, int roll) {
        DEPTH = this.depth;
        root = getNewRoot(state, roll);
        root.addChildren(roll);

        long startT = System.currentTimeMillis();

        Queue<Node> q = new LinkedList<>();
        q.add(root);
        while (System.currentTimeMillis() - startT < timeThreshold) {
            if(!q.isEmpty()) {
                Node n = q.remove();
                Map<int[], Node>[] children = n.getChildren();
                if (children == null) {
                    n.addChildren(p1, DEPTH);
                    children = n.getChildren();
                }
                if(children != null) {
                    for (int i = 0; i < children.length; i++) {
                        q.addAll(children[i].values());
                    }
                }
            }
        }
    }

    public int[][] getAIMove(Game model, int roll) {
        updateTree(model, roll);
        int[] move =  getBestMove(root);

        int[] start = new int[]{move[0], move[1]};
        int[] end = new int[]{move[2], move[3]};

        return new int[][]{start, end};
    }

    private int[] getBestMove(Node rootNode) {
        double alpha = Double.MAX_VALUE;
        int[] move = new int[4];
        for (int i = 0; i < rootNode.children.length; i++) {
            for (Map.Entry<int[], Node> entry: rootNode.children[i].entrySet()) {
                double newA = expectiminimax(entry.getValue());
                if(newA < alpha) {
                    alpha = newA;
                    move = entry.getKey();
                }
            }
        }

        return move;
    }

    private double expectiminimax(Node currentNode) {

        if(currentNode.children == null) {
            return getHeuristic(currentNode);
        }

        double a = getHeuristic(currentNode);

        if(currentNode.turn == 1) {

            a = Stream.of(currentNode.children).flatMap(e -> e.entrySet().stream()).map(e -> e.getValue()).map(n -> expectiminimax(n)*n.prob).mapToDouble(i -> i).min().orElseGet(() -> Double.MAX_VALUE);
        }

        else if (currentNode.turn == 0) {

            a = Stream.of(currentNode.children).flatMap(e -> e.entrySet().stream()).map(e -> e.getValue()).map(n -> expectiminimax(n)*n.prob).mapToDouble(i -> i).max().orElseGet(() -> Double.MAX_VALUE);
        }


        return a;
    }

    private double getHeuristic(Node node) {
        Board board = node.state.getBoard();

        int totalPlayerIndexes = 0;
        int totalPlayerPieces = 0;
        int totalOpponentIndexes = 0;
        int totalOpponentPieces = 0;

        Cell[][] cells = board.getCells();

        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[0].length; j++) {
                Cell cell = cells[i][j];

                for (int k = 0; k < cell.getPieces(p1); k++) {
                    totalPlayerPieces++;
                    totalPlayerIndexes += cell.getPathI();

                    if(cell.isSafe() || cell.isReroll()) {
                        totalPlayerIndexes += 5;
                    }
                }

                for (int k = 0; k < cell.getPieces(!p1); k++) {
                    totalOpponentPieces++;
                    totalOpponentIndexes += cell.getPathI();

                    if(cell.isSafe() || cell.isReroll()) {
                        totalOpponentIndexes += 5;
                    }
                }
            }
        }

        double playerAverage = totalPlayerIndexes/ (double) totalPlayerPieces;
        double opponentAverage = totalOpponentIndexes/ (double) totalOpponentPieces;

        if(playerAverage == board.getPath(p1).size() - 1) {
            return Double.MAX_VALUE;
        }

        if(opponentAverage == board.getPath(!p1).size() - 1) {
            return Double.MAX_VALUE;
        }

        return playerAverage - (3*opponentAverage);
    }

    private Node getNewRoot(Game state, int roll) {
        if (root == null) {
            return new Node(state.deepCopy(), 1, 0);
        }
        for (Node node : root.getChildren(roll).values()) {
            if (node.getState().equals(state)) {
                return node;
            }
        }
        return new Node(state.deepCopy(), 1, 0);
    }
    
    private static Map<Integer, double[]> calcAllRollProb(int minDice, int maxDice) {
        Map<Integer, double[]> allProbs = new HashMap<>();
        for (int dice = minDice; dice < maxDice; dice++) {
            double[] diceProbs = new double[dice + 1];
            for (int i = 0; i < diceProbs.length; i++) {
                diceProbs[i] = calcRollProb(i, dice);
            }
            allProbs.put(dice, diceProbs);
        }
        return allProbs;
    }

    private static double calcRollProb(int roll, int diceN) {
        return factorial(diceN) / (double) (factorial(diceN - roll) * factorial(roll));
    }

    private static int factorial(int n) {
        if (n == 0 || n == 1) { return 1; }
        else return factorial(n - 1) * n;
    }
}
