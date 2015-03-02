package test.search;

import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;

public class DirectorySearcherTest {

    private static final int THREADS_NUMBERS[] = new int[]{1, 2, 4, 8, 16, 32, 64, 128};

    // No JUnit 3-d party
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        System.out.format("Available processors: %d\n\n", Runtime.getRuntime().availableProcessors());
        while (true) {
            DirectorySearcher directorySearcher = new DirectorySearcher("Текст в UTF-8", StandardCharsets.UTF_8);
            for (int threadNumber : THREADS_NUMBERS) {
                directorySearcher.run(threadNumber, Paths.get("c:\\Tools"));
            }
        }
    }
}