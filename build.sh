#!/bin/bash
cwd="${PWD}/"
flags=('-shared' '-std=c17' '-Wall' '-Wextra' '-pedantic' '-O3' '-Wconversion' '-DNDEBUG' '-flto' '-lm')
gui='src/main/java/org/yuvViewer/gui/'
include='/include/'
jdk='java-14-openjdk-amd64'
jvm='/usr/lib/jvm/'
linux='linux/'
libcalc='libcalc.so'
utils='src/main/java/org/yuvViewer/utils/'
options=("${flags[@]}" "-I${jvm}${jdk}${include}" "-I${jvm}${jdk}${include}${linux}" '-o' "${libcalc}")

function create_native_library() {
  if [ -d "${LD_LIBRARY_PATH}" ]; then
    if type javac >/dev/null 2>&1; then
      javac -h "${gui}" "${gui}FrameAboutBox.java" \
        "${gui}MainFrame.java" \
        "${gui}SettingsDialog.java" \
        "${gui}YUVViewer.java" \
        "${utils}YUVDeclaration.java" \
        "${utils}ExtensionFileFilter.java" \
        "${utils}ExtensionUtils.java" \
        "${utils}WholeNumberTextField.java"
    else
      echo 'javac is not installed'
    fi
    cd "${gui}" || exit
    if type clang >/dev/null 2>&1; then
      echo 'Compiling using clang see logfile.log for result'
      clang "${options[@]}" YUVViewerImplementation.c >>"${cwd}logfile.log" 2>&1
    elif type gcc >/dev/null 2>&1; then
      echo 'Compiling using gcc see logfile.log for result'
      gcc "${options[@]}" YUVViewerImplementation.c >>"${cwd}logfile.log" 2>&1
    else
      echo 'clang or gcc is not installed'
    fi
    echo "Moving ${libcalc} to ${LD_LIBRARY_PATH}. Need sudo for this!"
    sudo mv "${libcalc}" "${LD_LIBRARY_PATH}"
  else
    echo '$LD_LIBRARY_PATH does not exist'
  fi
}

function add_date() {
  {
    echo '----'
    date -u +"%Y-%m-%dT%H:%M:%SZ"
    echo '----'
  } >>"${cwd}logfile.log"
}

function cleanup() {
  add_date
  {
    echo 'Started cleanup'
    gradle clean
    mvn clean
    ant clean
    # Not using force in the next two lines!
    find "${cwd}" -name '*'.class -exec rm {} \;
    rm "${cwd}src/main/java/org/yuvViewer/gui/org_yuvViewer_gui_YUVViewer.h"
    echo 'Finished cleanup'
  } >>"${cwd}logfile.log" 2>&1
}

rm -f "${cwd}logfile.log"
touch "${cwd}logfile.log"
#cleanup
add_date
echo 'Creating native library with Java Native Interface (JNI)'
create_native_library
add_date

cd "${cwd}" || exit
if type gradle >/dev/null 2>&1; then
  echo 'Compiling, bundling and executing using gradle see logfile.log for result'
  gradle compileJava jar >>"${cwd}logfile.log" 2>&1
  java -jar build/libs/yuvViewer-1.0-SNAPSHOT.jar
elif type mvn >/dev/null 2>&1; then
  echo 'Compiling, bundling and executing using mvn see logfile.log for result'
  mvn verify >>"${cwd}logfile.log" 2>&1
  java -jar target/yuvViewer-1.0-SNAPSHOT.jar
elif type ant >/dev/null 2>&1; then
  echo 'Compiling, bundling and executing using ant see logfile.log for result'
  ant run >>"${cwd}logfile.log" 2>&1
else
  echo 'Gradle, Maven and Ant not istalled. Make sure one of them exists, is in your path and is executable'
  exit 1
fi
add_date
