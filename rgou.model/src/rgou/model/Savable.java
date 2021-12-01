package rgou.model;

import rgou.model.element.Board;
import rgou.model.element.PathBoard;
import rgou.model.rules.Ruleset;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class that will be extended by Board, Ruleset and PathBoard
 * Used for saving and loading
 */
public abstract class Savable {
    // For saving and parsing

    public final String FILENAME_PREFIX;
    public final static String CURRENT_PATH = "CURRENT!!!.txt";
    public final static String FILENAME_SUFFIX = ".json";
    private String filePath = null;
    protected String name;

    public Savable(String FILENAME_PREFIX) {
        this.FILENAME_PREFIX = FILENAME_PREFIX;
    }

    public String getDirPath(){
        return FILENAME_PREFIX + "s/";
    }

    /**
     * Verifies that this savable's target directory exists. If not it creates it
     */
    public void verifyDirPath() {
        if (!new File(getDirPath()).exists()) {
            new File(getDirPath()).mkdir();
        }
    }

    /**
     * Sets current instance of this savable
     * @throws IOException
     */
    public void setCurrent() throws IOException {
        verifyDirPath();
        Files.write(Path.of(getDirPath() + CURRENT_PATH), new File(getFilePath()).getName().getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Gets path of current savable
     * @return
     */
    public String getCurrentPath() {
        verifyDirPath();
        try {
            return getDirPath() + Files.readString(Path.of(getDirPath() + CURRENT_PATH));
        } catch (Exception e) {
            return null;
        }
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return this.filePath.replace("\\", "/");
    }

    public void saveToFile(String s) throws IOException {
        verifyDirPath();
        Files.write(Path.of(getDirPath() + getNewFilename()), s.getBytes(StandardCharsets.UTF_8));
    }

    /**
     *
     * @return new unique filename
     */
    private String getNewFilename() {
        verifyDirPath();
        List<String> names = Arrays.stream(new File(getDirPath()).listFiles()).map(x -> x.getName()).collect(Collectors.toList());
        int i = 0;
        String name = null;
        boolean dupe = true;
        while (dupe) {
            dupe = false;
            name = FILENAME_PREFIX + i + FILENAME_SUFFIX;
            for (String n : names) {
                if (name.equalsIgnoreCase(n)) {
                    dupe = true;
                    break;
                }
            }
            i++;
        }
        return name;
    }

    /**
     *
     * @return list of all occurrences of this savable in target directory
     */
    public List<Savable> getAllFromDir() {
        verifyDirPath();
        for (File f : new File(getDirPath()).listFiles()) {
            if (f.getName().equals(CURRENT_PATH)) {
                continue;
            }
        }
        return Arrays.stream(new File(getDirPath()).listFiles())
                .filter(x -> !x.getName().equals(CURRENT_PATH))
                .map(x -> {
                    try {
                        if (this instanceof Ruleset) {
                            return new Ruleset(x.getPath());
                        } else if (this instanceof Board && !(this instanceof PathBoard)) {
                            return new Board(x.getPath());
                        } else if (this instanceof PathBoard) {
                            return new PathBoard(x.getPath());
                        }
                        return null;
                    } catch (Exception e) {
                        return null;
                    }
                })
                .filter(x -> x != null)
                .collect(Collectors.toList());
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        if (this.name == null) {
            this.name = getFilePath().replaceFirst(getDirPath(), "");
            if (this.name.endsWith(FILENAME_SUFFIX)) {
                this.name = this.name.substring(0, this.name.length() - FILENAME_SUFFIX.length());
            }
        }
        return this.name;
    }
}
