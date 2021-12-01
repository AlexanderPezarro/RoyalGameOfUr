package rgou.model.profile;

import rgou.model.game.Game;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.nio.file.Path;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

public class Profile {
    private String name;
    private List<UserCompletedGame> completedGames;
    private static String profilePath = "Profiles/"; //ALL FILES (EXCEPT CURRENT!!!) ARE ASSUMED TO BE PROFILES
    private static final String CURRENT_USER_FILENAME = "CURRENT!!!.txt";
    public static final int MAX_PROFILES = 16 - 1; // arbitrary but MUST be square - 1 (for neatness)
    private LocalDateTime timeCreated;
    public static String dateTimeFormat = "yyyy-MM-dd HH:mm:ss";

    /**
     * Constructor for new profile
     * @param name
     * @param completedGames
     */
    public Profile(String name, List<UserCompletedGame> completedGames) {
        setName(name);
        timeCreated = LocalDateTime.now();
        this.completedGames = completedGames;
    }

    /**
     * Reads profile from file
     * @param fileString
     */
    public Profile(String fileString) {
        String[] fileSplit = fileString.split("\t");
        this.setName(fileSplit[0]);
        this.setTimeCreated(fileSplit[1]);
        this.completedGames = new ArrayList<>();
        for (int i = 2; i < fileSplit.length; i++) {
            try {
                completedGames.add(new UserCompletedGame(fileSplit[i]));
            } catch (Exception e) {
                System.out.println("Invalid format: " + fileSplit[i]);
            }
        }
    }

    public void setName(String name) {
        //remove special characaters from file
        this.name = name.replaceAll("[^a-zA-Z0-9]", "");
    }

    private void setTimeCreated(String time){
        this.timeCreated = LocalDateTime.parse(time, DateTimeFormatter.ofPattern(dateTimeFormat));
    }

    public String getTimeCreated(){
        return DateTimeFormatter.ofPattern(dateTimeFormat).format(timeCreated);
    }

    public void addCompletedGame(boolean win, Game.PlayerType playerType) {
        completedGames.add(new UserCompletedGame(win, playerType));
    }

    public void addCompletedGame(boolean win, float difficultyNetwork) {
        completedGames.add(new UserCompletedGame(win, difficultyNetwork));
    }

    /**
     * Save profile to file
     * @throws IOException directory does not exist
     */
    public void toFile() throws IOException {
        verifyProfilePath();
        //Convert it so it fits to file
        String fileString = name + "\t" + getTimeCreated();
        for (UserCompletedGame g : completedGames) {
            fileString += "\t" + g.toString();
        }
        //Create new file with user's username
        if (!Files.exists(Path.of(profilePath))) {
            Files.createDirectory(Path.of(profilePath));
        }
        Files.write(Path.of(profilePath + name + ".txt"), fileString.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Reads profile from file
     * @param filename
     * @return
     */
    public static Profile fromFile(String filename) {
        verifyProfilePath();
        if(!filename.endsWith(".txt")){
            filename += ".txt";
        }
        // Don't include the file storing current profile
        if(filename.equalsIgnoreCase(CURRENT_USER_FILENAME)){
            return null;
        }
        try{
        return new Profile(Files.readString(Path.of(profilePath + filename)));}
        catch (Exception e){
            return  null;
        }
    }

    public List<UserCompletedGame> getCompletedGames() {
        return this.completedGames;
    }

    public List<UserCompletedGame> getCompletedGames(boolean win){
        return this.getCompletedGames().stream().filter(x -> x.isWin() == win).collect(Collectors.toList());
    }

    public String getName() {
        return this.name;
    }

    public void setCurrentUser() throws IOException {
        verifyProfilePath();
        Files.write(Path.of(profilePath + CURRENT_USER_FILENAME), getName().getBytes(StandardCharsets.UTF_8));
    }

    public static Profile getCurrentUser() {
        verifyProfilePath();
        try {

            return fromFile(Files.readString(Path.of(profilePath + CURRENT_USER_FILENAME)));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     *
     * @return all profiles from given directory
     */
    public static List<Profile> loadAllProfiles() {
        verifyProfilePath();
        return Arrays.stream(new File(profilePath)
                                                .listFiles())
                                                .filter(x -> x.getName().endsWith(".txt"))
                                                .map(x -> fromFile(x.getName()))
                                                .filter(x -> x != null)
                                                .collect(Collectors.toList());
    }

    /**
     * If Profiles directory does not exist, create it
     */
    public static void verifyProfilePath(){
        if(!new File(profilePath).exists()){
            new File(profilePath).mkdir();
        }
    }

}
