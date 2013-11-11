cd ..\..
javah -d org/yuvViewer/gui/ -jni org.yuvViewer.gui.YUVViewer
javac org\yuvViewer\Main.java
cd org
cd yuvViewer
set path=c:\MinGW\bin\;c:\MinGW\lib\;%PATH%
gcc -c -I"c:\Program Files\Java\jdk1.6.0\include" -I"c:\Program Files\Java\jdk1.6.0\include\win32" -o"gui\calc.o" e:\javanet\yuvplayer\yuvviewer\yuvViewer_nativ\org\yuvViewer\gui\YUVViewerImplementation.c 
c:\mingw\bin\dllwrap --def gui\calc.def -o gui\calc.dll gui\calc.o