package rgou.model.rules;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import rgou.model.Savable;
import rgou.model.profile.Profile;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Ruleset extends Savable {
    private int dices1;
    private int dices2;
    private int pieces1;
    private int pieces2;
    private boolean exactFinish;
    private boolean shareSpace;
    private boolean jumpOccupied;
    private int teleportAmount;
    private boolean teleportSpecialHops;

    // These static variables are just for validation + consistency
    public static final int MIN_DICES = 1;
    public static final int MAX_DICES = 8;

    public static final int MIN_PIECES = 1;
    public static final int MAX_PIECES = 10;

    public static final int MIN_TELEPORT = 1;
    public static final int MAX_TELEPORT = 8;


    public Ruleset() {
        super("Ruleset");
        dices1 = 4;
        dices2 = 4;
        pieces1 = 7;
        pieces2 = 7;
        teleportAmount = 4;
        name = "DefaultRuleset";
    }

    public Ruleset(String path) throws IOException {
        super("Ruleset");
        Gson g = new Gson();
        Ruleset r = g.fromJson(new JsonReader(new FileReader(path)), Ruleset.class);
        this.dices1 = r.dices1;
        this.dices2 = r.dices2;
        this.pieces1 = r.pieces1;
        this.pieces2 = r.pieces2;
        this.exactFinish = r.exactFinish;
        this.shareSpace = r.shareSpace;
        this.jumpOccupied = r.jumpOccupied;
        this.teleportAmount = r.teleportAmount;
        this.teleportSpecialHops = r.teleportSpecialHops;
        this.name = r.name;
        this.setFilePath(path);
    }

    public String toJsonString() {
        Gson g = new Gson();
        return g.toJson(this);
    }

    @Override
    public String toString() {
        return "Ruleset{" +
                "dices1=" + dices1 +
                ", dices2=" + dices2 +
                ", pieces1=" + pieces1 +
                ", pieces2=" + pieces2 +
                ", exactFinish=" + exactFinish +
                ", shareSpace=" + shareSpace +
                ", jumpOccupied=" + jumpOccupied +
                '}';
    }


    public int getDices(boolean player1) {
        return (player1) ? dices1 : dices2;
    }

    public void setDices(boolean player1, int dices) {
        if (player1) {
            this.dices1 = dices;
        } else {
            this.dices2 = dices;
        }
    }

    public int getPieces(boolean player1) {
        return (player1) ? pieces1 : pieces2;
    }

    public void setPieces(boolean player1, int pieces1) {
        if (player1) {
            this.pieces1 = pieces1;
        } else {
            this.pieces2 = pieces1;
        }
    }

    public boolean isExactFinish() {
        return exactFinish;
    }

    public void setExactFinish(boolean exactFinish) {
        this.exactFinish = exactFinish;
    }

    public boolean isShareSpace() {
        return shareSpace;
    }

    public void setShareSpace(boolean shareSpace) {
        this.shareSpace = shareSpace;
    }

    public boolean isJumpOccupied() {
        return jumpOccupied;
    }

    public void setJumpOccupied(boolean jumpOccupied) {
        this.jumpOccupied = jumpOccupied;
    }

    public int getTeleportAmount() {
        return teleportAmount;
    }

    public void setTeleportAmount(int teleportAmount) {
        this.teleportAmount = teleportAmount;
    }

    public boolean isTeleportSpecialHops() {
        return teleportSpecialHops;
    }

    public void setTeleportSpecialHops(boolean teleportSpecialHops) {
        this.teleportSpecialHops = teleportSpecialHops;
    }
}

