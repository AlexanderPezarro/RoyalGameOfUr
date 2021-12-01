package rgou.model.element;

import com.google.gson.Gson;
import rgou.model.Savable;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

//this is pretty much just for saving/loading purposes
public class PathBoard extends Board {

    public static final String PREFIX = "Path";

    public PathBoard() {
        super(PREFIX, false);
    }

    public PathBoard(String path) throws IOException {
        super(path, PREFIX);
        setBothPaths(path);
    }

    @Override
    public String toJsonString() {
        return pathsToJsonString();
    }
}
