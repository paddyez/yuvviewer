This is a description how it works on my Ubuntu Gnu Linux system using openjdk 14, ant, bash and clang/gcc (Comments have been added for building on OS X). Maybe a gradle and/or maven version will follow. Also a big code cleanup and improvements are planned depending on my time.

1. Clone the project or download and extract it.
2. Change to the project directory.

## Setup Enviornment

[user]@[computer]:~$ echo $LD_LIBRARY_PATH

Should return a valid path e.g.:

```bash 
echo $LD_LIBRARY_PATH
/usr/local/lib/
```

If not you might want to add

```bash export LD_LIBRARY_PATH=/usr/local/lib/```

to your .bashrc or .profile

### With Ant

Just type: ./build

### Without Ant

Assuming you are in PROJECT_DIRCTORY

```bash 
cd src/main/java/
javac (-source 14) org/yuvViewer/Main.java
cd org/yuvViewer/gui/
javah -jni YUVViewer (will not work with java version > 8 use javac -h instead see build file)
clang (or gcc) -shared (other compiler options) -I/usr/local/java/include/linux -I/usr/local/java/include/ -o libcalc.so YUVViewerImplementation.c
mv libcalc.so $LD_LIBRARY_PATH
cd PROJECT_DIRCTORY
java -cp src/main/java/:. org.yuvViewer.Main
```

#### YUV files

Obtaining YUV files:

* http://trace.eas.asu.edu/yuv/
* https://stackoverflow.com/questions/3644501/where-can-i-obtain-a-raw-yuv-file
* I added the file test_176x144.yuv (YUV4:2:0) to check that yuvViewer is ready to run. If you open the file with yuvViewer, you should see 4 colours: green (top-left), cyan (top-right), orange (bottom-left) and pink (bottom-right).

__The followning sections have not been updated for more than 5 years__ 

#### Mac OS X-Notes

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

#### Windows

1. make sure cygwin and gcc is installed on your system
2. make sure a jsdk version 1.4 is installed on your system
3. make sure system environment variable JAVA_HOME is set to you base directory where you installed jsdk (i.e. c:\j2sdk1.4_08.
4. open cygwin bash console
5. mkdir ~/java; cd ~/java
6. extract tar in home dir, will create ~/yuvViewer_nativ directory
6. cd ~/java/yuvViewer_nativ
7. javac -source 1.4 org/yuvViewer/Main.java
7. cd ~/java/yuvViewer_nativ/org/yuvViewer/gui/
8. javah -jni YUVViewer
9. gcc -mno-cygwin -I$JAVA_HOME/include -I$JAVA_HOME/include/win32 -Wl,--add-stdcall-alias -shared -o calc.dll YUVViewerImplementation.c
10. mv calc.dll $JAVA_HOME/jni/lib/
11. java -Djava.library.path=$JAVA_HOME/jni/lib -cp ~/yuvViewer_nativ org.yuvViewer.Main

##### Borland

I compiled calc.dll using Borland C++ Compiler 5.5 It is freeware, but you have to make an account. site: http://www.codegear.com/downloads/free/cppbuilder To build the calc.dll library using Borland compiler, just execute buildWinBcc.bat

I compiled calc.dll using MinGW(Minimalist GNU for Windows) I used gcc version 3.4.2. It is freeware. site: http://www.mingw.org/download.shtml To build the calc.dll library using MinGW compiler, just execute buildWinGcc.bat
