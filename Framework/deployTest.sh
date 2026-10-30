#!/bin/bash
# Framework/deployTest.sh

APP_NAME="HelloWorldServlet"
SRC_DIR="src/main/java"
BUILD_DIR="build"
LIB_DIR="lib"
OUTPUT_DIR="../Test/WEB-INF/lib"  
SERVLET_API_JAR="$LIB_DIR/servlet-api.jar"

# ── Nettoyage ─────────────────────────────────────
rm -rf "$BUILD_DIR"
mkdir -p "$BUILD_DIR/classes"

# ── Compilation ───────────────────────────────────
find "$SRC_DIR" -name "*.java" > sources.txt
javac -cp "$SERVLET_API_JAR" -d "$BUILD_DIR/classes" @sources.txt
STATUS=$?
rm -f sources.txt

if [ $STATUS -ne 0 ]; then
    echo "❌ Erreur de compilation. Abandon."
    exit 1
fi

# ── Empaquetage → .jar ────────────────────────────
cd "$BUILD_DIR/classes" || exit
jar -cvf "../../$BUILD_DIR/$APP_NAME.jar" .
cd ../..

# ── Copie dans Test/WEB-INF/lib/ ──────────────────────────
mkdir -p "$OUTPUT_DIR"
cp -f "$BUILD_DIR/$APP_NAME.jar" "$OUTPUT_DIR/"

echo ""
echo "✅ $APP_NAME.jar → Test/WEB-INF/lib/"  
echo ""