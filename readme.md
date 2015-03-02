Search in directories tree
==========================

(A) You should design and implement stand-alone Java application:
-----------------------------------------------------------------

> java test.search.test.search.Main <threads number> <search string> <search directory>
> e.g.
> java test.search.test.search.Main 16 ".IOException" "/usr/local/httpd/logs"

1. It should search for a <search string> in the files under specific <search directory>.
2. It should process files within <threads number> worker threads.
3. It should print found entries [file absolute path, absolute position of string in file] into standard output immediately after finding.
4. It should print overall statistics at the end of process. Statistics includes at least number of files found.
5. It should use J2SE only (no 3rd-party libraries).
6. It should work on Windows 7 x64 OS.
7. It should be as fast as possible.

(B) Make performance testing for your application.
--------------------------------------------------
1. Analyse results for [1, 2, 4, 8, 16, 32, 64] threads for files set ~10GB (e.g. c:\windows directory).
2. Explain results.


Test results could be found at http://goo.gl/ju7dwp
===================================================
(Without warming up and GC phases)

Test performed at 4 processors computer: above 4 threads there no any performance advantage.

