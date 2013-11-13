cd ..\..
javah -d org/yuvViewer/gui/ -jni org.yuvViewer.gui.YUVViewer
javac org\yuvViewer\Main.java
cd org
cd yuvViewer
gcc -c -std=c11 -I"C:\Program Files\Java\jdk1.7.0_25\include" -I"C:\Program Files\Java\jdk1.7.0_25\include\win32" -o"gui\calc.o" gui\YUVViewerImplementation.c 
c:\mingw\bin\dllwrap --def gui\calc.def -o gui\calc.dll gui\calc.o