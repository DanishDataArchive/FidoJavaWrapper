#!/bin/bash

VERSION=$(git describe --tag --dirty)
BRANCH=$(git status | grep "# On" | cut -d " " -f 4)
DATE=$(date "+%Y-%m-%d")
TIME=$(date "+%H:%M:%S")

echo "Version $VERSION from $BRANCH, built on $DATE $TIME"

cat > ../src/dk/dda/version/BuildInfo.java <<DELIM
package dk.dda.version;

// Auto-generated, triggered by project build
public class BuildInfo {
    public static String tag = new String("$VERSION");
    public static String branch = new String("$BRANCH");
    public static String date = new String("$DATE");
    public static String time = new String("$TIME");
    public static String releaseName = new String(tag + (branch.length() > 0 ? " (" + branch + ")" : ""));
}
DELIM
