#!/bin/bash

set -e

JAR_URL="https://github.com/uladzimirFilipchanka/noredraw/releases/latest/download/noredraw.jar"
JAR_NAME="noredraw.jar"
DOWNLOAD_DIR="."

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

java -cp "$DOWNLOAD_DIR/$JAR_NAME":libs/* noredraw.AppRunner "$@"