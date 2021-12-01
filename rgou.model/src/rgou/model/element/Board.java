package rgou.model.element;

import com.google.gson.*;
import com.google.gson.annotations.Expose;
import rgou.model.Savable;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;


public class Board extends Savable {
    private final int rowsN = 3;
    private final int colsN = 8;
    @Expose
    private Cell[][] cells;
    private List<Cell> path1 = new ArrayList<>();
    private List<Cell> path2 = new ArrayList<>();

    public static final String PREFIX = "Board";


    // For saving and parsing

    public Board(String cellsJsonPath, String prefix) throws IOException{
        super(prefix);
        if(cellsJsonPath == null){
            throw new IOException();
        }
        Gson g = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .create();
        setName(name);
        setFilePath(cellsJsonPath);
        try{cells = g.fromJson(Files.readString(Path.of(cellsJsonPath)), Cell[][].class);}
        catch (Exception e){
            cells = new Cell[rowsN][colsN];
            for (int i = 0; i < rowsN; i++) {
                for (int j = 0; j < colsN; j++) {
                    cells[i][j] = new Cell(j, i);
                    cells[i][j].setType(j, i);
                }
            }
            return;
        }
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[0].length; j++) {
                cells[i][j].setXY(j,i);
                cells[i][j].setType(j, i);
            }
        }
    }

    public Board(String cellsJsonPath) throws IOException{
        this(cellsJsonPath, PREFIX);
    }

    public Board(String prefix, boolean ignoreThis){
        super(prefix);
        cells = new Cell[rowsN][colsN];
        for (int i = 0; i < rowsN; i++) {
            for (int j = 0; j < colsN; j++) {
                cells[i][j] = new Cell(j, i);
                cells[i][j].setType(j, i);
            }
        }
        // Default board layout (as used by Irving Finkel https://www.mastersofgames.com/rules/royal-ur-rules.htm)
        for(Cell c: new Cell[]{cells[0][0], cells[2][0], cells[1][3], cells[0][6], cells[2][6]}){
            c.setReroll(true);
            c.setSafe(true);
        }
    }

    public Board(){
        this(PREFIX, false);
    }

    // movement verification is assumed to have been done in Game
    public void movePiece(boolean player1, Cell start, Cell end) {
        start.removePieces(player1, 1);
        end.addPieces(player1, 1);
        int oppPieces = end.getPieces(!player1);
        if (oppPieces != 0) {
            end.removePieces(!player1, end.getPieces(!player1));
            getDrawPile(!player1).addPieces(!player1, oppPieces);
        }
        System.out.println(end.getPieces(true)  + " " + end.getPieces(false));
//        fixme all cells start at the draw cell so remove piece should work for all cells
//        if (start.getType() != Cell.Type.Draw) { start.removePiece(player1); }
//        if (end.getType() != Cell.Type.Discard) { end.addPiece(player1); }
    }

    private void addPathEnds(){

        if (path1.get(0) != cells[2][4]) {
            path1.add(0, cells[2][4]);
        }
        if (path2.get(0) != cells[0][4]) {
            path2.add(0, cells[0][4]);
        }
        if (path1.get(path1.size() - 1) != cells[2][5]) {
            path1.add(cells[2][5]);
        }
        if (path2.get(path2.size() - 1) != cells[0][5]) {
            path2.add(cells[0][5]);
        }
    }

    private void addPathEnds(boolean path1bool) {
        List<Cell> path;
        if(path1bool) path = path1;
        else path = path2;

        if (path.get(0) != cells[2][4]) {
            path.add(0, cells[2][4]);
        }
        if (path.get(0) != cells[0][4]) {
            path.add(0, cells[0][4]);
        }

    }

    public String pathsToJsonString() {
        Gson g = new Gson();
        int[][][] coords = new int[2][][];

        coords[0] = getCoords(path1);
        coords[1] = getCoords(path2);

        return g.toJson(coords, int[][][].class);
    }

    private int[][] getCoords(List<Cell> path) {
        int[][] coords = new int[path.size()][2];
        for (int i = 1; i < path.size() - 1; i++) {
            coords[i] = new int[]{path.get(i).getX()
                    , path.get(i).getY()};
        }
        return coords;
    }

    public Cell getDrawPile(boolean player1) {
        return (player1) ? path1.get(0) : path2.get(0);
    }

    public Cell getDiscardPile(boolean player1) {
        return (player1) ? path1.get(path1.size() - 1) : path2.get(path2.size() - 1);
    }

    public String toJsonString() {
        Gson g = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                // JsonSerializer adapted from https://stackoverflow.com/a/13120355
                .registerTypeAdapter(Cell.class, new JsonSerializer<Cell>() {
                    @Override
                    public JsonElement serialize(Cell cell, Type type, JsonSerializationContext jsonSerializationContext) {
                        Gson g1 = new GsonBuilder()
                                .excludeFieldsWithoutExposeAnnotation()
                                .create();
                        JsonObject jo = g1.fromJson(g1.toJson(cell), JsonObject.class);
                        if (jo.get("z").getAsBoolean()) {
                            for (char c = 'a'; c <= 'z'; c++) {
                                if (jo.remove(Character.toString(c)) == null) {
                                    break;
                                }
                            }
                        }
                        return jo;
                    }
                })
                .create();
        return g.toJson(cells);
    }

    public String getPathsString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[0].length; j++) {
                if (path1.contains(cells[i][j])) {
                    sb.append("1");
                } else {
                    sb.append(" ");
                }
                if (path2.contains(cells[i][j])) {
                    sb.append("2");
                } else {
                    sb.append(" ");
                }
                sb.append(" ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 8; j++) {
                sb.append(cells[i][j].getPieces(true));
                sb.append(cells[i][j].getPieces(false)).append(" ");
            }
            sb.append("\n");
        }
        String s = sb.toString();
        return s.replaceAll("0", "_");
    }


    public List<Cell> getPath(boolean player1) {
        if (player1) {
            return path1;
        } else {
            return path2;
        }
    }

    public void setPath(boolean player1, int[][] coords) {
        List<Cell> path = new ArrayList<>(coords.length);
        if (player1) {
            path1 = path;
        } else {
            path2 = path;
        }
        for (int i = 0; i < coords.length; i++) {
            addToPath(player1, coords[i]);
        }
        addPathEnds(player1);
        indexPathCells();
    }

    public void setPath(boolean player1, List<Cell> path) {
        if (player1) {
            path1 = path;
        } else {
            path2 = path;
        }
        addPathEnds(player1);
        indexPathCells();
    }

    public void setBothPaths(String filepath) throws IOException {
        Gson g = new Gson();
        int[][][] coords = g.fromJson(Files.readString(Path.of(filepath)), int[][][].class);

        path1 = new ArrayList<>(coords[0].length);
        path2 = new ArrayList<>(coords[1].length);

        for (int i = 0; i < coords[0].length; i++) {
            addToPath(true, coords[0][i]);
        }
        for (int i = 0; i < coords[1].length; i++) {
            addToPath(false, coords[1][i]);
        }
        addPathEnds();
        indexPathCells();
    }

    private void addToPath(boolean player1, int[] coords) {
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[0].length; j++) {
                if (coords[0] == cells[i][j].getX()
                        && coords[1] == cells[i][j].getY()) {
                    if (player1) {
                        path1.add(cells[i][j]);
                    } else {
                        path2.add(cells[i][j]);
                    }
                }
            }
        }
    }

    private void indexPathCells() {
        for (int i = 0; i < path1.size(); i++) {
            path1.get(i).setPathI(i);
        }
        for (int i = 0; i < path2.size(); i++) {
            path2.get(i).setPathI(i);
        }
    }

    public int getRowsN() {
        return rowsN;
    }

    public int getColsN() {
        return colsN;
    }

    public Cell[][] getCells() {
        return cells;
    }

    public int geRemainingPieces(boolean player1) {
        List<Cell>  path = getPath(player1);
        int pieces = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            pieces += path.get(i).getPieces(player1);
        }
        return pieces;
    }
}
