#!/bin/bash
cwd="${PWD}/"
flags=( '-shared' '-std=c17' '-Wall' '-Wextra' '-pedantic' '-O3' '-Wconversion' '-DNDEBUG' '-flto' '-lm' )
gui='src/main/java/org/yuvViewer/gui/'
include='/include/'
jdk='java-14-openjdk-amd64'
jvm='/usr/lib/jvm/'
linux='linux/'
libcalc='libcalc.so'
utils='src/main/java/org/yuvViewer/utils/'
options=( "${flags[@]}" "-I${jvm}${jdk}${include}" "-I${jvm}${jdk}${include}${linux}" '-o' "${libcalc}" )
if type ant >/dev/null 2>&1
then
	ant
else
	echo 'Apache Ant is not installed. Make shure ant exists is in your path and is executable'
	echo 'Installing Apache Ant http://ant.apache.org/manual/install.html'
fi
#javap -s -p YUVViewer
if type javac >/dev/null 2>&1
then
	javac -h "${gui}" "${gui}FrameAboutBox.java" \
	"${gui}MainFrame.java" \
	"${gui}SettingsDialog.java" \
	"${gui}YUVViewer.java" \
	"${utils}YUVDeclaration.java" \
	"${utils}ExtensionFileFilter.java" \
	"${utils}ExtensionUtils.java"  \
	"${utils}WholeNumberTextField.java"
else
	echo 'javac is not installed'
fi
	cd "${gui}" || exit
if type clang >/dev/null 2>&1
then
  echo 'Compiling using clang see logfile.log for result'
	clang "${options[@]}" YUVViewerImplementation.c > "${cwd}logfile.log" 2>&1
elif type gcc >/dev/null 2>&1
then
  echo 'Compiling using gcc see logfile.log for result'
	gcc "${options[@]}" YUVViewerImplementation.c > "${cwd}logfile.log" 2>&1
else
	echo 'clang or gcc is not installed'
fi
if [ -d "${LD_LIBRARY_PATH}" ];
then
  echo "Moving ${libcalc} to ${LD_LIBRARY_PATH}. Need sudo for this!"
  sudo mv "${libcalc}" "${LD_LIBRARY_PATH}"
else
  echo '$LD_LIBRARY_PATH does not exist'
fi
if type java >/dev/null 2>&1
then
	java -cp "${cwd}src/main/java/:." org.yuvViewer.Main
else
	echo 'java is not installed'
fi
