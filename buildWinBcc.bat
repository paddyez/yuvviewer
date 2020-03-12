set path=d:\Borland\BCC55\bin\;d:\Borland\BCC55\lib\;%PATH%
cd ..\..
javah -d org/yuvViewer/gui/ -jni org.yuvViewer.gui.YUVViewer
javac org\yuvViewer\Main.java
cd org
cd yuvViewer
bcc32 -e"gui\calc.dll" -tWD -I"d:\Borland\BCC55\Include" -L"d:\Borland\BCC55\Lib" -I"c:\Program Files\Java\jdk1.6.0\include" -I"c:\Program Files\Java\jdk1.6.0\include\win32"  e:\javanet\yuvplayer\yuvviewer\yuvViewer_nativ\org\yuvViewer\gui\YUVViewerImplementation.c
