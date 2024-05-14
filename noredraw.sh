#!/bin/bash

set -e

JAR_URL="https://github.com/uladzimirFilipchanka/noredraw/releases/latest/download/noredraw.jar"
JAR_NAME="noredraw.jar"
DOWNLOAD_DIR="."
CUSTOM_DIR="./custom"

mkdir -p "$DOWNLOAD_DIR"

# Download the JAR file if it doesn't exist
if [[ ! -f "$DOWNLOAD_DIR/$JAR_NAME" ]]; then
    echo "Downloading JAR file..."
    wget -O "$DOWNLOAD_DIR/$JAR_NAME" "$JAR_URL"

    # Verify the download
    if [[ ! -f "$DOWNLOAD_DIR/$JAR_NAME" ]]; then
        echo "Error: Failed to download JAR file."
        exit 1
    fi
fi

CLASSPATH="$DOWNLOAD_DIR/$JAR_NAME"

# Check if the ./custom directory exists
if [[ -d "$CUSTOM_DIR" ]]; then
    # Find all JAR and ZIP files under the ./custom folder
    CUSTOM_FILES=$(find "$CUSTOM_DIR" \( -name "*.jar" -o -name "*.zip" \) -type f)

    for FILE in $CUSTOM_FILES; do
        CLASSPATH+=":$FILE"
    done
fi

# Run the Java application with the constructed classpath
java -cp "$CLASSPATH" noredraw.AppRunner "$@"
