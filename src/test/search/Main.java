package test.search;

import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;

/**
 * Params <threads number> <search string> <search directory>
 * Use UTF-8 encoding
 */
public class Main {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        int threadsNumber;
        String searchString;
        String searchDirectory;
        try {
            threadsNumber = Integer.valueOf(args[0]);
            searchString = args[1];
            searchDirectory = args[2];
            System.out.format("Search '%s' in '%s' directory within %d threads", searchString, searchDirectory, threadsNumber);
        } catch (Exception e) {
            printDescription();
            System.exit(1);
            return;
        }

        DirectorySearcher directorySearcher = new DirectorySearcher(searchString, StandardCharsets.UTF_8); // Charset.forName("Windows-1251"))
        directorySearcher.run(threadsNumber, Paths.get(searchDirectory));
    }

    private static void printDescription() {
        System.out.println("Run format:\n" +
                " java test.search.Main <threads number> <search string> <search directory>\n" +
                " e.g.\n" +
                " java test.search.test.search.Main 16 \".IOException\" \"/usr/local/httpd/logs\"\n");
    }
}
