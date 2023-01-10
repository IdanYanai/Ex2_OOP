package Ex2_A;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Class that extends Thread to count a single file's number of lines.
 */
public class threadReader extends Thread{
    private final String fName;
    private int lines;

    /**
     * Constructor.
     * @param fName file name to read.
     */
    public threadReader(String fName) {
        super();
        this.fName = fName;
        this.lines = 0;
    }

    /**
     * Simple line counter taken from Ex2_1.java getNumOfLines.
     */
    @Override
    public void run() {
        try (BufferedReader r = new BufferedReader(new FileReader(fName))) {
            while (r.readLine() != null)
                lines++;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get counted lines.
     * @return num of lines in the file.
     */
    public int getLines() {
        return lines;
    }
}
