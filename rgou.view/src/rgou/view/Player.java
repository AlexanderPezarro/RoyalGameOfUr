package rgou.view;

import com.google.common.collect.Multimap;
import rgou.model.element.Cell;
import rgou.model.game.Bot;
import rgou.model.game.Game;
import rgou.model.profile.Profile;
import rgou.view.game.GamePanel;

import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class is used to represent a player of the game
 */
public class Player {

    // Player details
    private String name;
    private Game.PlayerType type;
    private Player opponent;
    private GamePanel gamePanel;// parent game panel on which the game is being played
    private Bot bot;

    /**
     * Class constructor
     * @param name player name
     * @param type can be either human, easy AI, medium AI, or hard AI
     */
    public Player(String name, Game.PlayerType type) {
        this.name = name;
        setType(type);
    }

    /**
     * Default class constructor
     */
    public Player() {
        if(Profile.getCurrentUser() == null) {
            this.name = "Player";
        }

        else {
            this.name = Profile.getCurrentUser().getName();// get current profile name
        }

        this.type = Game.PlayerType.HUMAN;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setGamePanel(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    public void setOpponent(Player opponent) {
        this.opponent = opponent;
    }

    public Player getOpponent() {
        return opponent;
    }

    public String getName() {
        return name;
    }

    public GamePanel getGamePanel() {
        return gamePanel;
    }

    public Game.PlayerType getType() {
        return type;
    }

    public void setType(Game.PlayerType type) {
        this.type = type;

        if(type.equals(Game.PlayerType.MEDIUM_AI)) {
            bot = new Bot(500, 2);
        }

        else if(type.equals(Game.PlayerType.HARD_AI)) {
            bot = new Bot(1000, 8);
        }
    }

    /**
     * Detemines if the player is a human
     * @return true if player is a human, false otherwise
     */
    public boolean isHuman() {
        return type.equals(Game.PlayerType.HUMAN);
    }

    /**
     * Detemines if the player is an AI
     * @return true if player is an AI, false otherwise
     */
    public boolean isAI() {
        return (type.equals(Game.PlayerType.EASY_AI) ||type.equals(Game.PlayerType.MEDIUM_AI) || type.equals(Game.PlayerType.HARD_AI));
    }

    /**
     * Returns an AI move based on the current game state and AI difficulty
     * @param diceTotal the dice roll total
     * @param game the game
     * @return a 2D array of coordinates representing the move
     */
    public int[][] getAIMove(int diceTotal, Game game) {
        if(type.equals(Game.PlayerType.EASY_AI)) {
            Multimap<Cell, Cell> moves = game.getValidMoves(game.isPlayerOneTurn(), diceTotal);

            Map.Entry<Cell, Cell> move = moves.entries().stream().findAny().get();

            return new int[][]{new int[] {move.getKey().getX(), move.getKey().getY()}, new int[]{move.getValue().getX(), move.getValue().getY()}};
        }

        else if(type.equals(Game.PlayerType.MEDIUM_AI)) {
            return bot.getAIMove(game, diceTotal);
        }

        else if(type.equals(Game.PlayerType.HARD_AI)){
            return bot.getAIMove(game, diceTotal);
        }

        return null;
    }

    /**
     * This method rolls the dice for the opponent player.
     * This is used for network games
     * @param numbers the dice roll values as a string
     */
    public void playOpponentDiceRoll(String numbers) {
        // Convert string message to list of numbers
        ArrayList<Integer> diceNumbers = new ArrayList<>(Stream.of(numbers.split("")).map(s -> Integer.parseInt(s)).collect(Collectors.toList()));
        // set the dice values and then roll the dice
        gamePanel.setDiceRollNumbers(diceNumbers);
        gamePanel.rollDice();
    }

    /**
     * Thuis method plays the move for the opponent player
     * This is only used for network games
     * @param coords the coordinates as a string
     */
    public void playOpponentMove(String coords){
        int[][] moves = convertStringToCoords(coords);// get the coordinates

        gamePanel.playMove(moves);// play the move
    }

    /**
     * Dummy method overridden by NetworkPlayer to actually send the dice roll values
     * to the opponent network player
     * @param numbers the dice roll values
     */
    public void sendDiceRollToOpponent(ArrayList<Integer> numbers) {

    }

    /**
     * Dummy method overridden by NetworkPlayer to actually send the move coordinates
     * to the opponent network player
     * @param move the 2D array of move coordinates
     */
    public void sendMoveToOpponent(int[][] move) {

    }

    /**
     * Dummy method overridden by Network player to quit the game
     * Network games require the opponent player to be alerted that the player has quit
     * while in local games that is not necessary
     */
    public void quitGame(){

    }

    /**
     * This method converts the string coordinates into a 2D array of coordinates representing a move
     * @param coords the string
     * @return the 2D array
     */
    private int[][] convertStringToCoords(String coords) {
        // there will always be 4 numbers for a move
        return new int[][]{
                new int[]{
                        Integer.parseInt(coords.substring(0, 1)),// start x
                        Integer.parseInt(coords.substring(1, 2))// start y
                },
                new int[]{
                        Integer.parseInt(coords.substring(2, 3)),// end x
                        Integer.parseInt(coords.substring(3, 4))// end y
                }
        };
    }
}
