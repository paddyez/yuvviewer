This is a description how it works on my Debian Gnu Linux system. 
Comments have been added for building on OS X.

Setup Enviornment
-----------------

- On OS X step 1 and 2 can be skipped
- On MS Windows skip to Windows section

1) mkdir java in your home directory and make sure that ~/java/jni/lib exists.
2) LD_LIBRARY_PATH in .bashrc should look like this:

	if [ -z $LD_LIBRARY_PATH ]; then
	    export LD_LIBRARY_PATH=~/java/jni/lib
	fi

If it already exists just add :~/java/jni/lib

2.1) 	
	exec bash
	[user]@[computer]:~$ echo $LD_LIBRARY_PATH 

Should now look like this: /home/[user]/java/jni/lib.

3) Extract sources in ~/java

Without Ant
-----------

1) cd ~/java/yuvViewer_nativ/
2) javac -source 1.4 org/yuvViewer/Main.java
3) cd ~/java/yuvViewer_nativ/org/yuvViewer/gui/
4) javah -jni YUVViewer
5) gcc -shared -I/usr/local/java/include/linux -I/usr/local/java/include/ \
   YUVViewerImplementation.c -o libcalc.so
6) mv libcalc.so ~/java/jni/lib/
7) java -cp ~/java/yuvViewer_nativ/ org.yuvViewer.Main

With Ant
--------

1) cd ~/java/yuvViewer_nativ/org/yuvViewer/
2) ./build

Packaging
---------

1) cd ~/java/yuvViewer_nativ/
2) ./package
3) java -jar yuvViewer.jar

Mac OS X-Notes
--------------

by Chris Schaab <cschaab@vt.edu> for Mac OS X Aug 16 2004

I had some troubles with your viewer program on Mac OS X, It seems that 
Apple has taken out -shared as something that will work from gcc. Instead 
it requires a 2 step process specific to java. I modified the build file 
to work on my machine(G5 powerPC, various specific java version info in 
paths) at least and have attached it. The script to look at is named 
buildMac in this folder.

by Eric Work <work.eric@gmail.com> for Mac OS X Dec 19 2004

The buildMac script now handles different architetures as arguments, eg. 
./buildMac G4.  The package script has been added, allowing 
a .app to be built with Jar Bundler from the Developer Tools

Windows
-------

1) make sure cygwin and gcc is installed on your system
2) make sure a jsdk version 1.4 is installed on your system
3) make sure system environment variable JAVA_HOME is set to you base directory where you installed jsdk (i.e. c:\j2sdk1.4_08)
4) open cygwin bash console
5) mkdir ~/java; cd ~/java
6) extract tar in home dir, will create ~/yuvViewer_nativ directory
6) cd ~/java/yuvViewer_nativ
7) javac -source 1.4 org/yuvViewer/Main.java
7) cd ~/java/yuvViewer_nativ/org/yuvViewer/gui/
8) javah -jni YUVViewer
9) gcc -mno-cygwin -I$JAVA_HOME/include -I$JAVA_HOME/include/win32 -Wl,--add-stdcall-alias -shared -o calc.dll YUVViewerImplementation.c
10) mv calc.dll $JAVA_HOME/jni/lib/
11) java -Djava.library.path=$JAVA_HOME/jni/lib -cp ~/yuvViewer_nativ org.yuvViewer.Main
