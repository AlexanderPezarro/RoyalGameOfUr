import rgou.model.element.Board;
import rgou.model.element.Cell;
import rgou.model.rules.Ruleset;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class SavingExample {
    public static void main(String[] args) throws IOException {
        // paths example
        Board b = new Board();
        b.setBothPaths("initial_paths.json");
        System.out.println(b.getPathsString());
        Files.write(Path.of("saved_paths.json"), b.pathsToJsonString().getBytes());
//        System.exit(0);

        System.out.println("---------------------");

        // ruleset example
        Files.write(Path.of("test_ruleset.json"), new Ruleset().toJsonString().getBytes());
        System.out.println(new Ruleset("test_ruleset.json"));

        System.out.println("--------------------");

        // board saving example:
        b = new Board();
        Cell[][] cs = b.getCells();
        for (Cell[] c : cs) {
            for (Cell cell : c) {
                if (Math.random() > .5) { cell.setSurprise(true); }
            }
        }
        Files.write(Path.of("test_board.json"), b.toJsonString().getBytes());
        System.out.println(new Board("test_board.json"));
    }
}
