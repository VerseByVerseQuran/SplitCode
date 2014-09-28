VerseByVerseQuran-SplitCode
===========================

This is the java source code that allows you to parse the timings files and handles rounding of milliseconds and all that jazz, and results in executing mp3splt for each file.

This program will loop through files 001 to 114.txt and for each file 
will output the mp3s in the format xxxyyy.mp3 where xxx is chapter, yyy is 
verse.

To use it correctly, create a folder called "split" and then type in "java split.Main" and it will run it on the timing files. 

you also have the source code (Main.java) incase you want to make modifications. 

This was also done as a paid project for us. 
Requires: Java Runtime Environment (JRE) To recompile you need JDK and you can type "javac Main.java" and it will create a new Main.class for you email us at support @ lightuponlight dot com if you like this project

To recompile into a JAR file,

1) javac split/Main.java
this recreates the class file

2) jar -cvfe VBV.jar split.Main split\Main.class
this repackages it into a jar.
