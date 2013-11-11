I compiled calc.dll using Borland C++ Compiler 5.5
It is freeware, but you have to make an acount.
site: http://www.codegear.com/downloads/free/cppbuilder
To build the calc.dll library using Borland compiler, just execute 
buildWinBcc.bat

I compiled calc.dll using MinGW(Minimalist GNU for Windows)
I used gcc version 3.4.2. It is freeware.
site: http://www.mingw.org/download.shtml
To build the calc.dll library using MinGW compiler, just execute 
buildWinGcc.bat

I added the file test_176x144.yuv (YUV4:2:0) to check that  
yuvViewer is ready to run. If you open the file with yuvViewer,
you should see 4 colours: gree(top-left), cyan(top-right),
orange(bottom-left) and pink(bottom-right).