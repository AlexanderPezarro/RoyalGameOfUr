package rgou.view.game;

import rgou.model.profile.Profile;
import rgou.view.Player;
import rgou.model.element.Board;
import rgou.model.element.Cell;
import rgou.model.game.Game;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.common.collect.Multimap;
import rgou.model.rules.Ruleset;
import rgou.view.gui.GUI;
import rgou.view.gui.GUIStandards;
import rgou.view.networking.NetworkPlayer;

/**
 * This class is the panel used to play a game
 */
public class GamePanel extends JPanel {

    private boolean isNetworkGame;// whether it is a network game or not
    private boolean isHost;// whether ths user is the host of the game or not
    private Dimension size;// size of the screen
    private static final long serialVersionUID = 1L;

    private final Game model;// the model

    // These objects represent components of the panel
    private final BoardPanel boardPanel;// the board
    private final Dice player1Dice;// dice for player 1
    private final Dice player2Dice;// dice for player 2
    private final JButton homeButton;// home button
    private final StatsBox statsBox;// box showing all stats and messages

    // These objects are used to enable gameplay
    private Player player1;// player 1
    private Player player2;// player 2
    private PlayerBox player1Box;// box for player 1
    private PlayerBox player2Box;// box for player 2
    private Player currentPlayer;// keeps track of the current player
    private Player movePlayer;// keeps track of the player that made the last move
    private boolean moveHasBeenSent;// keeps track of whether the move has been sent to the opponent network player

    private final GridBagConstraints c = new GridBagConstraints();// used to arrange the components on the panel

    // These objects are used by the dice objects
    private ArrayList<Integer> diceRollNumbers;// list of numbers of the dice rolls
    private int diceTotal;// total of the dice roll
    private int numberOfReRolls;// number of times a player gets to roll the dice again in one turn
    private boolean diceHasBeenRolled;// keeps track of whether the dice has been rolled

    private boolean gameIsOver;// keeps track of whether the game is over

    /**
     * Class constructor
     * @param size size of the screen
     * @param isNetworkGame true if it is a network game, false otherwise
     * @param isHost true if the user is the host, false otherwise
     * @param board the model board used to play the game
     * @param ruleSet the rule set for the board
     * @param player1 player 1
     * @param player2 player 2
     */
    public GamePanel(Dimension size, boolean isNetworkGame, boolean isHost, Board board, Ruleset ruleSet, Player player1, Player player2) {
        super();

        // Set values
        this.size = size;
        this.isNetworkGame = isNetworkGame;
        this.isHost = isHost;
        this.player1 = player1;
        this.player2 = player2;
        this.currentPlayer = player1;
        this.movePlayer = this.currentPlayer;
        this.numberOfReRolls = 0;
        this.moveHasBeenSent = false;
        this.diceHasBeenRolled = false;
        this.gameIsOver = false;

        // Configure panel details
        setBackground(GUIStandards.gameBackgroundColour);
        setLayout(new GridBagLayout());

        int padding = 20;
        setBorder(new EmptyBorder(padding, padding, padding, padding));

        // Create a model object
        this.model = new Game(board, ruleSet);

        // Create the board
        boardPanel = new BoardPanel(this, model.getBoard());
        boardPanel.setOpaque(false);

        // Create the home button to quit the game
        homeButton = new JButton("Quit Game");
        homeButton.setBackground(GUIStandards.buttonColour);
        homeButton.setForeground(GUIStandards.buttonFontColour);
        homeButton.setFont(GUIStandards.buttonFont);
        boardPanel.resetAllButtons();

        // Set dice values
        diceTotal = 0;

        // Create the navigation panel
        JPanel navigationPanel = new JPanel();
        navigationPanel.add(homeButton);
        navigationPanel.setOpaque(false);

        // Create the player panel which will contain the player boxes and stats box
        JPanel playerPanel = new JPanel();
        playerPanel.setLayout(new GridLayout(1, 3, 30, 0));

        // Player 1 box
        player1Box = new PlayerBox(1, this, player1, size, !isNetworkGame);
        playerPanel.add(player1Box);

        // Stats box
        statsBox = new StatsBox(this);
        playerPanel.add(statsBox);

        // PLayer 2 box
        player2Box = new PlayerBox(2, this, player2, size, !isNetworkGame);
        playerPanel.add(player2Box);
        playerPanel.setOpaque(false);

        // Set dice for each player
        player1Dice = new Dice(ruleSet.getDices(true), size);
        player2Dice = new Dice(ruleSet.getDices(false), size);

        // use the grid bag layout to organise the component panels
        c.fill = GridBagConstraints.BOTH;

        setConstraints(0, 0, 50, 1, 1, 0.05);
        add(navigationPanel, c);

        setConstraints(0, 1, 50, 4, 1, 0.25);
        add(playerPanel, c);

        // Filler used to space things out
        JPanel filler = new JPanel();
        filler.setOpaque(false);
        setConstraints(0, 5, 50, 1, 1, 0.05);
        add(filler, c);

        setConstraints(0, 15, 5, 10, 0.1, 0.50);
        add(player1Dice, c);

        setConstraints(5, 15, 40, 10, 0.8, 0.50);
        add(boardPanel, c);

        setConstraints(45, 15, 5, 10, 0.1, 0.50);
        add(player2Dice, c);
    }

    /**
     * Sets contraints for the grid bag layout
     * @param gridx x position
     * @param gridY y position
     * @param gridWidth width of component
     * @param gridHeight height of component
     * @param weightX horizontal weight of component
     * @param weightY vertical weight of component
     */
    public void setConstraints(int gridx, int gridY, int gridWidth, int gridHeight, double weightX, double weightY) {
        c.gridx = gridx;
        c.gridy = gridY;
        c.gridwidth = gridWidth;
        c.gridheight = gridHeight;
        c.weightx = weightX;
        c.weighty = weightY;
    }

    /**
     * Prepares the game so that it is ready to play
     */
    public void prepareToPlay() {
        setPlayerButtons();
        playMoveIfAI();
    }

    /**
     * Decides which buttons should be enabled based on whose turn it is and if the current player is a network player
     * or AI
     */
    private void setPlayerButtons() {
        if (!isNetworkGame) {//local game
//            System.out.println("Local game");
            if (player1.isAI() || player2.isAI()) {// if there is an AI player
                if (currentPlayer.isAI()) {// if the current player is AI then disable buttons
                    statsBox.disableRollButton();
                    boardPanel.resetAllButtons();
                } else {// else enable them
                    statsBox.enableRollButton();
                }
            }
        } else {
            System.out.println("Network Game");
            //the host is always player 1, the client is always player 2 even for his own Game object
            if (currentPlayer.equals(player1) == isHost) {
                statsBox.enableRollButton();
                System.out.println("Dice enabled for " + currentPlayer.getName());
            } else {
                System.out.println("Dice disabled for " + currentPlayer.getOpponent().getName());
                statsBox.disableRollButton();
                boardPanel.resetAllButtons();
            }
        }
    }

    /**
     * Plays a move if the current player is an AI
     */
    public void playMoveIfAI() {
        if (currentPlayer.isAI()) {
            JOptionPane.showMessageDialog(this, currentPlayer.getName() + ", the AI, will play a move now");

            if (!diceHasBeenRolled) {
                setDiceRollNumbers();
                rollDice();
            }

            Multimap<Cell, Cell> validMoves = model.getValidMoves(model.isPlayerOneTurn(), diceTotal);

            if (!validMoves.isEmpty()) {
                int[][] move = currentPlayer.getAIMove(diceTotal, model);

                Cell startCell = boardPanel.getCellAt(move[0][0], move[0][1]);
                Cell endCell = boardPanel.getCellAt(move[1][0], move[1][1]);
                playMove(startCell, endCell);
            }
        }
    }

    /**
     * Adds a message to the chatbox in the stats box
     *
     * @param msg the message
     */
    public void addMessageChatbox(String msg) {
        statsBox.pushMessage("OTHER: " + msg);
    }

    /**
     * Changes the boolean that keeps track of whether the dice has been rolled
     *
     * @param diceHasBeenRolled true if the dice has been rolled, false otherwise
     */
    public void setDiceHasBeenRolled(boolean diceHasBeenRolled) {
        this.diceHasBeenRolled = diceHasBeenRolled;
    }


    /**
     * Returns the panel size
     * @return the panel size
     */
    public Dimension getPanelSize() {
        return this.size;
    }

    /**
     * Adds the change panel action listener to the home button
     * @param al the action listener
     */
    public void addChangePanelListener(ActionListener al) {
        homeButton.addActionListener(al);
        homeButton.setActionCommand("home");
    }

    /**
     * Sets the values for the dice roll by using the model to generate a list of random numbers which are either 1 or 0
     */
    public void setDiceRollNumbers() {
        diceRollNumbers = model.rollDice(model.isPlayerOneTurn());
    }

    /**
     * Sets the values for the dice roll using a given set of values.
     * This is mainly used in network play to display the dice roll of the opponent network player
     * @param numbers the list of numbers
     */
    public void setDiceRollNumbers(ArrayList<Integer> numbers) {
        diceRollNumbers = numbers;
    }

    /**
     * Displays the current turn in the message box
     */
    private void displayCurrentTurn() {
        statsBox.pushMessage(currentPlayer.getName() + "'s Turn");
    }

    /**
     * Sets the message sent by the opponent player in the message box
     * @param player the player who sent the message
     */
    public void setMsgPlayer(NetworkPlayer player) {
        statsBox.setMsgBoxPlayer(player);
    }

    /**
     * Changes turns in the model and updates the current player
     */
    private void changeTurns() {
        model.changeTurn();
        currentPlayer = currentPlayer.getOpponent();
        moveHasBeenSent = false;// set to false so that moves can now be sent again
    }



    public void decrementNumberOfReRolls() {
        numberOfReRolls = Math.max(0, numberOfReRolls - 1);
    }

    /**
     * This method rolls the dice and sends the dice rolls to the opponent network player
     */
    public void rollDice() {
        Dice playerDice;
        decrementNumberOfReRolls();// reduces number of dice rolls for that turn since the user has clicked on roll dice

        // determine whose dice to roll based on the turn
        if (model.isPlayerOneTurn()) {
            playerDice = player1Dice;
        } else {
            playerDice = player2Dice;
        }

        // send dice roll numbers to opponent network player
        if (isHost == model.isPlayerOneTurn()) {
            currentPlayer.sendDiceRollToOpponent(diceRollNumbers);
        }

        // update the dice images to display current values
        playerDice.updateDiceValues(diceRollNumbers);
        diceTotal = playerDice.getDiceTotal();// get the dice total

        // update the message box
        statsBox.pushMessage(currentPlayer.getName() + " rolled a " + diceTotal + "!");
        setDiceHasBeenRolled(true);// dice has been rolled
        updateBoardButtons();// update the board buttons to display available moves
        setPlayerButtons();// set player buttons (such as disabling roll dice button to prevent second roll)
    }

    /**
     * This method updates the board panel grid of buttons to enable the current player to play a move
     */
    private void updateBoardButtons() {
        boardPanel.resetAllButtons();// first reset all buttons to clear all previous moves

        // If dice roll was 0 then there is no move to play
        if (diceTotal == 0) {
            // If the current player has no more dice rolls then change turns
            if (numberOfReRolls == 0) {
                setDiceHasBeenRolled(false);// to indicate that the dice can be rolled again for the next player
                statsBox.pushMessage(currentPlayer.getName() + "'s turn is skipped");// update message box
                changeTurns();// change turns
                displayCurrentTurn();// show change of turns in message box
                statsBox.enableRollButton();// enable the roll dice button so that the next player can roll the dice

                setPlayerButtons();// update which buttons can be clicked
                playMoveIfAI();// if the current player is an AI then play an AI move
            }
        } else {// else there could be moves to play
            // first get valid moves for current dice total from model
            Multimap<Cell, Cell> moves = model.getValidMoves(model.isPlayerOneTurn(), diceTotal);

            // if no legal moves can be played then change turns
            if (moves.isEmpty()) {
                statsBox.pushMessage("No legal moves!");// update message box
                changeTurns();// change turns
                displayCurrentTurn();// show change in message box
                statsBox.enableRollButton();// enable button for next player

                setPlayerButtons();// update which buttons can be clicked
                playMoveIfAI();// play a move if current player is an AI
            } else {
                // otherwise there a moves to be played and so update the board buttons
                boardPanel.setStartButtons(moves);
            }
        }
    }

    /**
     * Updates the board buttons with a given set of moves
     * @param moves the moves
     */
    private void updateBoardButtons(Multimap<Cell, Cell> moves) {
        boardPanel.resetAllButtons();
        boardPanel.setStartButtons(moves);
    }

    /**
     * This method ends the game when it is over
     * @param winner the winning player
     */
    private void gameOver(Player winner) {
        JOptionPane.showMessageDialog(this, "Game Over! " + winner.getName() + " won the game!");

        Profile p = Profile.getCurrentUser();// get the current profile of the user

        // Update profile details if exists
        if(p != null) {
            if(currentPlayer.getName().equals(p.getName())) {
                if(currentPlayer.equals(player1)) {
                    p.addCompletedGame(true, player2Box.getPlayerType());
                }
                else{
                    p.addCompletedGame(true, player1Box.getPlayerType());
                }
            }

            //save details to file
            try {
                p.toFile();
            } catch (Exception e) {}
        }

        gameIsOver = true;// set it to true
        boardPanel.removeAll();// clear the board panel

        // Use a JLabel to add an image to the board panel to indicate that the game is over
        JLabel message = new JLabel();
        Image image = getImageFromFile("game_over", size.height / 2);
        message.setIcon(new ImageIcon(image));
        message.setBounds(size.width / 2 - image.getWidth(null) + 50, size.height / 2 - image.getHeight(null), image.getWidth(null), image.getHeight(null));
        boardPanel.add(message);
        boardPanel.repaint();

    }

    /**
     * Updates the piece finished counter in the player box
     * @param endCell the cell the piece moved to
     */
    private void updatePlayerBox(Cell endCell) {
        // if cell is the last cell of the path
        List<Cell> path = model.getBoard().getPath(currentPlayer.equals(player1));
        if (path.get(path.size() -1).equals(endCell)) {
            // increment the counter of the player box of the current player
            if (currentPlayer.equals(player1)) {
                player1Box.incrementPiecesFinished();
            } else {
                player2Box.incrementPiecesFinished();
            }
        }
    }

    /**
     * Plays a move in the board, This method is recursive in situations where some cell abilities require second moves
     * to be played before the turn is changed
     *
     * @param startCell the start of the move
     * @param endCell the end of the move
     */
    public void playMove(Cell startCell, Cell endCell) {

        movePlayer = currentPlayer;// set the current player as the player who played the last move
        // update the message box
        statsBox.pushMessage(currentPlayer.getName() + " moved piece from (" + startCell.getY() + ", " + startCell.getX() + ") to (" + endCell.getY() + ", " + endCell.getX() + ")");

        if(endCell.getPieces(!model.isPlayerOneTurn()) > 0) {// update the message box if a capture happened
            statsBox.pushMessage(currentPlayer.getName() + " captured " + currentPlayer.getOpponent().getName() + "'s piece(s)!");
        }

        setDiceHasBeenRolled(false);// once a move is played, the dice can be rolled again. so set it to false

        // Send s move to the opponent if it has not been sent already
        if(isHost == model.isPlayerOneTurn() && !moveHasBeenSent) {
            /**
             * the boolean condition: isHost == model.isPlayerOneTurn()
             * is based on the fact that both the host and client play the game with their own engine
             * where the host is always player 1 and the client is always player 2.
             * So, for the host, player 1 is used to send and receive moves, and for the client it is player 2
             * This means that the player 2 object of the host game and the player 1 object of the client game
             * are dummy objects and should not send moves. Hence a move is only sent:
             * - if the user is the host and it is player 1's (the host's) turn
             * - if the user is the client and it is player 2's (the client's) turn
             */
            currentPlayer.sendMoveToOpponent(convertCellsToCoords(startCell, endCell));
            moveHasBeenSent = true;
        }

        // Update the player box if a piece has finished its journey
        updatePlayerBox(endCell);

        // else if this is not a winning move then play the move
        // Call the model to play the move and return secondary moves if the end cell had a special ability
        Multimap<Cell, Cell> moves = model.playMove(startCell, endCell);

        // Update the board panel
        boardPanel.updateBoard(model.getBoard());
        boardPanel.resetAllButtons();// reset all buttons to prevent wrong clicks

        // Call model to check if player 1 won
        if (model.won(true)) {
            gameOver(player1);
            return;
        }
        else if (model.won(false)) {// else check if player 2 won
            gameOver(player2);
            return;
        }

        // If end cell has a reroll on it
        if(endCell.isReroll()) {
            if(endCell.isUsed()) {//if it has already been used
                // update the message box
                statsBox.pushMessage("Piece landed on a re-roll square but it has been used already");
            }

            else {
                // then update the message box
                statsBox.pushMessage("Piece landed on a re-roll square");
                numberOfReRolls++;// increment number of rolls the player has
                JOptionPane.showMessageDialog(this, "The dice can be rolled " + numberOfReRolls + " more times!!");
                statsBox.enableRollButton();// enable the roll dice button for the reroll
            }
        }

        // if end cell has a teleport ability
        if(endCell.isTeleport()) {
            if(endCell.isUsed()) {// if it has been used
                // update message box
                statsBox.pushMessage("Piece landed on a teleport square but it has been used already");
            }

            else {
                // else the player can now move the piece again to almost any square ahead of it
                moveHasBeenSent = false;// set it to false so that the move can be sent
                //update the messahge box
                statsBox.pushMessage("Piece landed on a teleport square");
                JOptionPane.showMessageDialog(this, "The piece can now be teleported to valid squares within " + model.getRuleset().getTeleportAmount() + " tiles");
                updateBoardButtons(moves);// update the board buttons with the secondary moves for the teleport
            }
        }

        // if end cell has the random move ability
        if(endCell.isRandMove()) {
            if(endCell.isUsed()) {
                statsBox.pushMessage("Piece landed on a random move square but it has been used already");
            }

            else {
                // update the message box and inform the player
                statsBox.pushMessage("Piece landed on a random move square");
                JOptionPane.showMessageDialog(this, "The piece will now be moved to a random square!");

                // if the random move if on the user's turn then play the move
                // otherwise this random move is for the opponent network player
                if(isHost == currentPlayer.equals(player1) || !isNetworkGame) {
                    if(!moves.isEmpty()) {// if there is a secondary move then play it
                        // get the next end cell the piece will be randomly moved to
                        Cell nextCell = new ArrayList<>(moves.get(endCell)).get(0);
                        playMove(endCell, nextCell);// play the move (recursive call)
                        currentPlayer.sendMoveToOpponent(convertCellsToCoords(endCell, nextCell));//send the move
                    }
                }
            }
        }

        // if this cell ahs the move N square ahead ability
        else if (endCell.getMoveN() != 0) {
            if(endCell.isUsed()) {
                statsBox.pushMessage("Piece landed on a move square but it has been used already");
            }

            else {
                // update the message box and inform the player
                statsBox.pushMessage("Piece landed on a move square");
                JOptionPane.showMessageDialog(this, "The piece will now be moved " + endCell.getMoveN() + " squares!");
                if(!moves.isEmpty()) {
                    // play the move (recursive call)
                    playMove(endCell, new ArrayList<>(moves.get(endCell)).get(0));
                }
            }
        }

        // if the cell has the reset ability
        else if(endCell.isReset()) {
            if(endCell.isUsed()) {
                statsBox.pushMessage("Piece landed on a reset square but it has been used already");
            }

            else {
                // update the player and message box
                statsBox.pushMessage("Piece landed on a reset square");
                JOptionPane.showMessageDialog(this, "The piece will now be moved back to the start square!");
                if(!moves.isEmpty()) {
                    // play the move (recursive call)
                    playMove(endCell, new ArrayList<>(moves.get(endCell)).get(0));
                }
            }
        }

        // if after any of the special moves the game is over then return
        if(gameIsOver) {
            return;
        }

        if(numberOfReRolls > 0) {// if the user can still roll the dice
            moveHasBeenSent = false;// set to false so that the user's next move can be sent
            statsBox.enableRollButton();// enable the roll dice button
        } else if (moves.isEmpty()) {// else if there are no secondary moves
            changeTurns();// the player has nothing to play so change turns
            statsBox.enableRollButton();// enable the roll dice button
            displayCurrentTurn();// update the message box
        } else {
            if (movePlayer.equals(currentPlayer)) {// if the player to play the last move is the current player
                // then it means the player can still play a move, so update the board buttons to allow a move to
                // be played
                updateBoardButtons(moves);
            } else {
                // else the current player has changed so reset all buttons to prevent invalid moves being played by
                // the other player
                boardPanel.resetAllButtons();
            }
        }

        setPlayerButtons();// set the player buttons
        playMoveIfAI();// play a move if the current player is an AI

    }

    /**
     * Plays a move given the coordinates of the start and end positions.
     * this is used in network games where moves are sent as coordinates
     * @param move the coordinates of the move
     */
    public void playMove(int[][] move) {
        // convert the coordinates to cells
        Cell startCell = boardPanel.getCellAt(move[0][0], move[0][1]);
        Cell endCell = boardPanel.getCellAt(move[1][0], move[1][1]);

        playMove(startCell, endCell);// call the other play move method with the cells
    }

    /**
     * Converts the start and end cell to coordinates
     * @param startCell the start
     * @param endCell the end
     * @return An array of coordinates
     */
    public int[][] convertCellsToCoords(Cell startCell, Cell endCell) {
        return new int[][]{boardPanel.getCoordsOfCell(startCell), boardPanel.getCoordsOfCell(endCell)};
    }

    /**
     * This method is used to load an image from a local file
     * @param imageName image file name
     * @param imageHeight height of the image returned
     * @return an Image object
     */
    public static Image getImageFromFile(String imageName, double imageHeight) {

        try {
            // read the image
            BufferedImage fileImage = ImageIO.read(new File("images/" + imageName + ".png"));
            int height = fileImage.getHeight();

            // calculate the new width of the image based on the scaling factor of the height
            double divisor = height / imageHeight;

            int newWidth = (int) (fileImage.getWidth() / divisor);
            // return a scaled instance of the image
            return fileImage.getScaledInstance(newWidth, (int) imageHeight, Image.SCALE_SMOOTH);
        } catch (IOException e) {
            // do nothing
        }

        return null;
    }

    /**
     * This class is a panel used to represent the dice of a player
     */
    private class Dice extends JPanel {
        private List<Image> dice1s;// there a 3 different orientations for rolling a 1
        private Image dice0;// the image for rolling a 0

        //width and height of dice panel
        private int totalWidth;
        private int totalHeight;

        private int heightPerDice;// height of each dice

        private int diceTotal;// total value of the dice roll
        private ArrayList<Integer> numbers;// the dice roll values

        public Dice(int numberOfDice, Dimension size) {

            // set the dimensions
            totalHeight = (int) (size.height * 0.45);
            totalWidth = (int) (size.width * 0.1);
            setDiceHeight(numberOfDice);

            //set the height of each dice
            setDiceImages(heightPerDice);

            // set panel configurations
            setOpaque(false);
            setLayout(null);

            //set initial dice values
            numbers = new ArrayList<>();
            for (int i = 0; i < numberOfDice; i++) {
                numbers.add(0);
            }
            // update the dice images to display dice values
            updateDiceValues(numbers);

        }

        /**
         * Sets the height of the dice based on panel width and height
         * @param numberOfDice
         */
        private void setDiceHeight(int numberOfDice) {
            // determine the shorter side and use that to set the dice image height
            if (totalWidth * numberOfDice > totalHeight) {
                heightPerDice = totalHeight / numberOfDice;
            } else {
                heightPerDice = totalWidth;
            }
        }

        /**
         * Set the images of the dice
         * @param height height of each dice
         */
        private void setDiceImages(int height) {
            // load all dice images for 1
            dice1s = new ArrayList<>();
            for (int i = 1; i < 4; i++) {
                dice1s.add(getImageFromFile("dice1_state" + i, height));
            }
            // load the image for getting a 0
            dice0 = getImageFromFile("dice0", height);
        }

        /**
         * Update the dice images based on the numbers
         * @param numbers the numbers
         */
        private void updateDiceValues(ArrayList<Integer> numbers) {
            removeAll();// remove all previous dice
            diceTotal = numbers.stream().mapToInt(i -> i).sum();// get the dice total

            // change the dice height and images if the number of dice changes
            if (heightPerDice != totalHeight / numbers.size()) {
                setDiceHeight(numbers.size());
                setDiceImages(heightPerDice);
            }


            // for each dice value
            for (int i = 0; i < numbers.size(); i++) {
                int number = numbers.get(i);

                //create a new label for the dice
                JLabel dice = new JLabel();

                //set its image based on the value
                if (number == 1) {
                    // for a 1 pick a random image from the three
                    int stateNumber = (int) Math.floor(Math.random() * 3);
                    dice.setIcon(new ImageIcon(dice1s.get(stateNumber)));
                } else if (number == 0) {
                    // set the image for a 0
                    dice.setIcon(new ImageIcon(dice0));
                }

                //set the bounds and add the dice image
                dice.setBounds(20, heightPerDice * i, heightPerDice, heightPerDice);
                add(dice);
            }

            //repaint the dice panel
            repaint();
        }

        /**
         * Returns the dice total
         * @return the dice total
         */
        public int getDiceTotal() {
            return diceTotal;
        }
    }
}
