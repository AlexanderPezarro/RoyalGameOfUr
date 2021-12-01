package rgou.model.profile;

import rgou.model.game.Game;

import java.time.LocalDateTime;

public class UserCompletedGame {
    private boolean win;
    private static final double ND_NULL_VALUE =  1.1;
    // One of these next two fields must be null
    private Game.PlayerType playerType = null; // if AI game
    private double difficultyNetwork = ND_NULL_VALUE; // W/L ratio of other player, can't be higher

    //These difficulties would be EASY, MEDIUM, HARD, LOCAL

    public UserCompletedGame(boolean win, Game.PlayerType playerType){
        this.win = win;
        this.playerType = playerType;
    }

    public UserCompletedGame(boolean win, double difficultyNetwork){
        this.win = win;
        this.difficultyNetwork = difficultyNetwork;
    }

    public UserCompletedGame(String fileText) throws Exception{
        this.win = Boolean.parseBoolean(fileText.split(",")[0]);
        String difficulty = fileText.split(",")[1];
        try{
            setDifficultyNetwork(Double.parseDouble(difficulty));
        }
        catch (Exception e){
            setPlayerType(playerType);
        }

    }

    public String toString(){
        String out = Boolean.toString(win) + ",";
        if(playerType != null) return out + playerType;
        if(difficultyNetwork != ND_NULL_VALUE) return out + difficultyNetwork;
        return "";
    }

    private void setDifficultyNetwork(double difficultyNetwork){
        if(difficultyNetwork >= 0 && difficultyNetwork <= 1){
            this.difficultyNetwork = difficultyNetwork;
        }
    }

    private void setPlayerType(Game.PlayerType playerType){
        this.playerType = playerType;
    }

    public boolean isWin(){
        return this.win;
    }
}
