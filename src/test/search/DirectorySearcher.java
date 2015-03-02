package test.search;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * GKislin
 * 28.02.2015.
 */
public class DirectorySearcher {
    private static final int MAP_SIZE = Integer.MAX_VALUE;
    private final BoyerMooreSearch boyerMooreSearch;
    private final int mapStep;

    public DirectorySearcher(final String searchString, Charset charset) {
        byte[] patternBytes = searchString.getBytes(charset);
        boyerMooreSearch = new BoyerMooreSearch(patternBytes);
        mapStep = MAP_SIZE - patternBytes.length + 1;
    }


    public void run(final int threadsNumber, final Path searchDirectory) throws InterruptedException, ExecutionException {
        final AtomicInteger filesProcessed = new AtomicInteger();
        final AtomicInteger filesWithPattern = new AtomicInteger();
        final AtomicInteger patternCounter = new AtomicInteger();
        System.out.format("+++++++++ Search within %d threads ++++++++++++++\n", threadsNumber);


        long startTime = System.currentTimeMillis();

        //  http://stackoverflow.com/questions/21163108/custom-thread-pool-in-java-8-parallel-stream
        ForkJoinPool forkJoinPool = new ForkJoinPool(threadsNumber);
        forkJoinPool.submit(() -> {
            try {
                Files.walk(searchDirectory).parallel().filter(Files::isRegularFile).forEach(
                        path -> {
                            try {
                                FileChannel fc = FileChannel.open(path, StandardOpenOption.READ);
                                // @See https://bugs.openjdk.java.net/browse/JDK-6347833: workaround limit size by Integer.MAX_VALUE
                                long offset = 0;
                                int limitSize;
                                do {
                                    limitSize = (int) Math.min(fc.size() - offset, MAP_SIZE);
                                    MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, offset, limitSize);
                                    final long offsetFile = offset;
                                    if (boyerMooreSearch.matches(bb,
                                            position -> {
                                                System.out.format("File '%s', position %d\n", path, offsetFile + position);
                                                patternCounter.incrementAndGet();
                                            }
                                    )) {
                                        filesWithPattern.incrementAndGet();
                                    }
                                    offset += mapStep;
                                } while (limitSize == MAP_SIZE);
                                filesProcessed.incrementAndGet();
                            } catch (IOException e) {
                                e.printStackTrace();
                                System.exit(1);
                            }
                        }
                );
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }).get();
        forkJoinPool.shutdown();

        System.out.format("\nFiles processed: %d\n" +
                        "Files with pattern: %d\n" +
                        "Pattern counter: %d\n" +
                        "--- For %d threads processing time: %d ----\n\n", filesProcessed.get(), filesWithPattern.get(), patternCounter.get(),
                threadsNumber, System.currentTimeMillis() - startTime);
    }
}
