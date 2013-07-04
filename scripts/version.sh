#!/bin/bash

VERSION=$(git describe --tag)
DATE=$(date "+%Y-%m-%d")
TIME=$(date "+%H:%M:%S")

echo "Version $VERSION, built on $DATE $TIME"

cat > ../src/dk/dda/version/BuildInfo.java <<DELIM
package dk.dda.version;

// Auto-generated, triggered by project build
public class BuildInfo {
    public static String version = new String("$VERSION");
    public static String date = new String("$DATE");
    public static String time = new String("$TIME");
}
DELIM
