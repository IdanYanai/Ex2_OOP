import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.Callable;

/**
 * Simple class that implements Callable to count a single file's number of lines.
 */
public class task implements Callable {
    private final String fName;

    /**
     * Constructor
     * @param fName the file name to read.
     */
    public task(String fName) {
        this.fName = fName;
    }

    /**
     * Simple line counter taken from Ex2_1.java getNumOfLines.
     * @return the number of lines.
     */
    @Override
    public Object call() {
        int lines = 0;
        try (BufferedReader r = new BufferedReader(new FileReader(fName))) {
            while (r.readLine() != null)
                lines++;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }
}
