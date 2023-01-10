package Ex2_A;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Ex2_1 {

    /**
     * For each method we get start time and finish time and subtract them to get
     * the running time.
     * @param args arguments
     */
    public static void main(String[] args) {
        String[] names = createTextFiles(1000, 2, 1000);

        long start = System.currentTimeMillis();
        int noThreads = getNumOfLines(names);
        long finish = System.currentTimeMillis();
        System.out.println((finish - start) + " Milliseconds - Lines:  " + noThreads);

        start = System.currentTimeMillis();
        int withThreads = getNumOfLinesThreads(names);
        finish = System.currentTimeMillis();
        System.out.println((finish - start) + " Milliseconds - Lines with threads: " + withThreads);

        start = System.currentTimeMillis();
        int withThreadPool = getNumOfLinesThreadPool(names);
        finish = System.currentTimeMillis();
        System.out.println((finish - start) + " Milliseconds - Lines with thread pool: " + withThreadPool);

        System.out.println("Finished");
    }


    /**
     * First, we create a new fixedThreadPool, 1 thread for each file. Secondly, we submit
     * the tasks to the pool, and it assigns every thread a file to read. Then, we
     * go over the futures list to sum all the results of the threads. Finally,
     * we shut down the threadPool and return the lines counted.
     * @param fileNames array of all the file names
     * @return number of lines
     */
    public static int getNumOfLinesThreadPool(String[] fileNames) {
        int lines = 0;
        ExecutorService pool = Executors.newFixedThreadPool(fileNames.length);
        List<Future<Integer>> futures = new ArrayList<>();
        for (String fileName : fileNames)
            futures.add(pool.submit(new threadPoolRead(fileName)));
        try {
            for (Future<Integer> f : futures)
                lines += f.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        pool.shutdown();

        return lines;
    }

    /**
     * First, we create a threads array in the length of fileNames, so that for
     * each thread there is a file to read. We initialize the array with new threads
     * and start them immediately. Then, for each thread we wait for it finish using
     * join, and sum its result into lines. Finally, we return the lines counted.
     * @param fileNames array of all the file names
     * @return number of lines
     */
    public static int getNumOfLinesThreads(String[] fileNames) {
        int lines = 0;
        threadReader[] threads = new threadReader[fileNames.length];
        for(int i=0;i<fileNames.length;i++) {
            threads[i] = new threadReader(fileNames[i]);
            threads[i].start();
        }
        try {
            for(threadReader t : threads) {
                t.join();
                lines += t.getLines();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return lines;
    }


    /**
     * We go over all the file names in a loop, counting each file's number of
     * lines. Then, we return the lines counted.
     * @param fileNames array of all the file names
     * @return number of lines
     */
    public static int getNumOfLines(String[] fileNames) {
        int lines = 0;
        for (String name : fileNames) {
            try (BufferedReader r = new BufferedReader(new FileReader(name))) {
                while (r.readLine() != null)
                    lines++;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return lines;
    }

    /**
     * We loop n times, each time creating a new file called File_i.txt. And, writing
     * a random amount of lines of "Hello World". Finally, we print the sum of all
     * the lines, so we know to check if the other methods are correct. And we return
     * the file names.
     * @param n number of files to create
     * @param seed seed for Random object
     * @param bound max amount of lines in a file
     * @return string array of file names
     */
    public static String[] createTextFiles(int n, int seed, int bound) {
        String[] names = new String[n];
        Random rand = new Random(seed);
        int sumOfLines = 0;
        int numOfLines;
        for (int i = 0; i < n; i++) {
            names[i] = ("file_" + (i + 1) + ".txt");
            numOfLines = rand.nextInt(bound);
            sumOfLines += numOfLines;
            StringBuilder toWrite = new StringBuilder();
            try {
                FileWriter w = new FileWriter(names[i]);
                toWrite.append("Hello World\n".repeat(Math.max(0, numOfLines - 1)));
                toWrite.append("Hello World");
                w.write(String.valueOf(toWrite));
                w.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Sum of lines: " + sumOfLines);
        return names;
    }
}
