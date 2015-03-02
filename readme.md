Search in directories tree
==========================

Stand-alone Java application
>  java test.search.test.search.Main &lt;threads number&gt; &lt;search string&gt; &lt;search directory&gt;

>  e.g.
>  java test.search.test.search.Main 16 ".IOException" "/usr/local/httpd/logs"

 + Search for a &lt;search string&gt; in the files under specific &lt;search directory&gt;.
 + Process files within &lt;threads number&gt; worker threads.
 + Print found entries [file absolute path, absolute position of string in file] into standard output immediately after finding.
 + Print overall statistics at the end of process

[Test results](http://goo.gl/ju7dwp) (warming up and GC phases excluded).
-------------

Test performed at 4 processors computer.
Above 4 threads there no any performance advantage.


