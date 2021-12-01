import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class RulesetsLoader {

    // Set absolute folder location
    private static String absolutePath = "Rulesets/"; // Put path here
    private String filePath = absolutePath;

    // Initialize Object
    public RulesetsLoader(String fileName) { // "Default" for default file
        this.filePath = absolutePath+fileName;
    }

    // Public Getters Global
    public void getRulesets() {
        // Returns a list of files which contain rulesets
    }

    public int getNumDice() {
        // Get the rules from a file and put it
        return Integer.valueOf(loadRules("numDice","@gameRules","int"));
    }

    public boolean getExactRoll() {
        // Get the rules from a file and put it
        return Boolean.valueOf(loadRules("needExactRoll","@gameRules"));
    }

    public int getPieces(int playerID) {
        // Get the rules from a file and put it
        String numPieces = "";
        if (playerID==0) numPieces=loadRules("numPiecesOne","@gameRules","int");
        else numPieces=loadRules("numPiecesTwo","@gameRules","int");
        return Integer.valueOf(numPieces);
    }

    // Getters for special pieces
    public Boolean getReroll(int pieceID) {
        // Get the rules from a file and put it
        return Boolean.valueOf(loadRules("reroll", "@tile"+pieceID));
    }
    public Boolean getSafe(int pieceID) {
        // Get the rules from a file and put it
        return Boolean.valueOf(loadRules("safe", "@tile"+pieceID));
    }
    public Boolean getShare(int pieceID) {
        // Get the rules from a file and put it
        return Boolean.valueOf(loadRules("shareSpace", "@tile"+pieceID));
    }
    public int getMovement(int pieceID) {
        // Get the rules from a file and put it
        return Integer.valueOf(loadRules("movement", "@tile"+pieceID,"int"));
    }
    public Boolean getCapture(int pieceID) {
        // Get the rules from a file and put it
        return Boolean.valueOf(loadRules("capture","@tile"+pieceID));
    }

    // Get Folder Path
    public static String getPath() {
        return absolutePath;
    }

    // Private Loaders
    private String loadRules(String searchTerm, String category, String type) {
        // Create a file reader and read value that is being searched for
        String output = "";
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String line = reader.readLine();
            while (line!=null){
                // Skip to correct section
                do {line = reader.readLine();} while (!line.contains(category));
                // Get searching value
                boolean found = false;
                do {line = reader.readLine();
                    if (line.contains(searchTerm)) {
                        found = true;
                        if (type.equals("boolean")) output = "true";
                        else if (type.equals("int")) {
                            int valueIndex = line.indexOf(searchTerm+":")+searchTerm.length()+1;
                            String[] values = line.substring(valueIndex).split(",");
                            output = values[0];
                        }
                    }

                } while (!line.contains(searchTerm)&&!line.isEmpty()&&!found);
                return output;
            }
            return null;
        } catch (FileNotFoundException e) {return null;} catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String loadRules(String searchTerm, String category) {
        return loadRules(searchTerm,category,"boolean");
    }

}