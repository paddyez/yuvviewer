#!/bin/bash
# Abbrechen bei Fehlern
set -e
set -o pipefail
cwd="${PWD}"
logfile="${cwd}/logfile.log"
# Log-Datei initialisieren
echo "--- Build started at $(date) ---" > "$logfile"
# JAVA_HOME Erkennung
if [ -z "$JAVA_HOME" ]; then
    if type javac >/dev/null 2>&1; then
        JAVA_HOME=$(readlink -f "$(which javac)" | sed "s:/bin/javac::")
        echo "Found JAVA_HOME: $JAVA_HOME" | tee -a "$logfile"
    else
        echo "Error: JAVA_HOME not set and javac not found in PATH" | tee -a "$logfile" >&2
        exit 1
    fi
fi
# OS Erkennung für JNI Includes
OS_NAME=$(uname -s | tr '[:upper:]' '[:lower:]')
case "$OS_NAME" in
    linux*)   OS_INCLUDE="linux" ;;
    darwin*)  OS_INCLUDE="darwin" ;;
    msys*|cygwin*|mingw*) OS_INCLUDE="win32" ;;
    *)        OS_INCLUDE="linux" ;;
esac
# Compiler Flags
# -shared: Erzeugt eine Shared Library
# -fPIC: Position Independent Code (erforderlich für Shared Libraries auf vielen Architekturen)
# -O3: Hohe Optimierungsstufe
# -flto: Link Time Optimization
flags=('-shared' '-fPIC' '-std=c17' '-Wall' '-Wextra' '-pedantic' '-O3' '-Wconversion' '-DNDEBUG' '-flto' '-lm' '-msse4.1')
gui_src='src/main/java/org/yuvViewer/gui/YUVViewer.java'
native_dir='src/main/c'
libcalc='libcalc.so'
options=("${flags[@]}" "-I${JAVA_HOME}/include" "-I${JAVA_HOME}/include/${OS_INCLUDE}" '-o' "${cwd}/${native_dir}/${libcalc}")
function create_native_library() {
    echo "Creating native library with JNI..." | tee -a "$logfile"
    # JNI Header generieren
    echo "  Generating JNI header..." | tee -a "$logfile"
    javac -sourcepath src/main/java -h "${native_dir}" "${gui_src}" >> "$logfile" 2>&1
    # In das C-Verzeichnis wechseln
    pushd "${native_dir}" > /dev/null
    # Compiler wählen
    COMPILER=""
    if type clang >/dev/null 2>&1; then
        COMPILER="clang"
    elif type gcc >/dev/null 2>&1; then
        COMPILER="gcc"
    fi
    if [ -n "$COMPILER" ]; then
        echo "  Compiling using $COMPILER..." | tee -a "$logfile"
        $COMPILER "${options[@]}" YUVViewerImplementation.c >> "$logfile" 2>&1
    else
        echo "Error: Neither clang nor gcc found" | tee -a "$logfile" >&2
        exit 1
    fi
    # Bibliothek verteilen
    echo "  Distributing ${libcalc} to build locations..." | tee -a "$logfile"
    if [ -n "${LD_LIBRARY_PATH}" ] && [ -d "${LD_LIBRARY_PATH}" ]; then
        echo "  Installing ${cwd}/${native_dir}/${libcalc} to ${LD_LIBRARY_PATH}..." | tee -a "$logfile"
        sudo mv "${cwd}/${native_dir}/${libcalc}" "${LD_LIBRARY_PATH}/"
    fi
    popd > /dev/null
}
function cleanup() {
    echo "Cleaning up..." | tee -a "$logfile"
    if [ -f "gradlew" ]; then
        ./gradlew clean >> "$logfile" 2>&1 || true
    fi
    if [ -f "pom.xml" ]; then
        mvn clean >> "$logfile" 2>&1 || true
    fi
    if [ -f "build.xml" ]; then
        ant clean >> "$logfile" 2>&1 || true
    fi
    # Manuelle Reste entfernen
    find src -name "*.class" -delete
    rm -f src/main/c/org_yuvViewer_gui_YUVViewer.h
    echo "Cleanup finished." | tee -a "$logfile"
}
# Hauptablauf
if [ "$1" == "clean" ]; then
    cleanup
    exit 0
fi
create_native_library
echo "Building Java application..." | tee -a "$logfile"
# Passendes Build-Tool finden und ausführen
if [ -f "gradlew" ] || [ -f "build.gradle.kts" ] || [ -f "build.gradle" ]; then
    BUILD_TOOL="gradle"
    if [ -f "gradlew" ]; then BUILD_CMD="./gradlew"; else BUILD_CMD="gradle"; fi
    echo "  Using $BUILD_TOOL..." | tee -a "$logfile"
    $BUILD_CMD compileJava jar >> "$logfile" 2>&1
    for f in build/libs/*.jar; do
        if [ -f "$f" ] && [[ ! "$f" =~ (sources|javadoc) ]]; then
            JAR_FILE="$f"
            break
        fi
    done
elif [ -f "pom.xml" ]; then
    BUILD_TOOL="maven"
    echo "  Using $BUILD_TOOL..." | tee -a "$logfile"
    mvn verify -DskipTests >> "$logfile" 2>&1
    for f in target/*.jar; do
        if [ -f "$f" ] && [[ ! "$f" =~ (sources|javadoc) ]]; then
            JAR_FILE="$f"
            break
        fi
    done
elif [ -f "build.xml" ]; then
    BUILD_TOOL="ant"
    echo "  Using $BUILD_TOOL..." | tee -a "$logfile"
    ant run >> "$logfile" 2>&1
    # Ant run führt die App meist direkt aus, daher hier evtl. Ende
    exit 0
else
    echo "Error: No supported build tool configuration (Gradle, Maven, Ant) found." | tee -a "$logfile" >&2
    exit 1
fi
if [ -f "$JAR_FILE" ]; then
    echo "Running application: $JAR_FILE" | tee -a "$logfile"
    # Wenn wir in einer CI-Umgebung oder ohne Display sind, überspringen wir den eigentlichen Start
    if [ -n "$DISPLAY" ] || [ -n "$WAYLAND_DISPLAY" ]; then
        java -jar "$JAR_FILE"
    else
        echo "No display detected. To run the application, use: java -jar $JAR_FILE"
    fi
else
    echo "Error: JAR file not found at $JAR_FILE" | tee -a "$logfile" >&2
    exit 1
fi