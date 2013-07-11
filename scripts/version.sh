#!/bin/bash

VERSION=$(git describe --tag --dirty)
if [[ -n "$1" ]]; then
	VERSION="$VERSION-$1"
fi
BRANCH=$(git show-branch | cut -d "[" -f 2 | cut -d "]" -f 1)
DATE=$(date "+%Y-%m-%d")
TIME=$(date "+%H:%M:%S")
WHO=$(whoami)

echo "Version $VERSION from $BRANCH, built on $DATE $TIME by $WHO"

cat > ../src/dk/dda/version/BuildInfo.java <<DELIM
package dk.dda.version;

// Auto-generated, triggered by project build
public class BuildInfo {
    public static String tag = new String("$VERSION");
    public static String branch = new String("$BRANCH");
    public static String date = new String("$DATE");
    public static String time = new String("$TIME");
    public static String who = new String("$WHO");
    public static String releaseName = new String(tag + (branch.length() > 0 ? " (" + branch + ")" : ""));
}
DELIM
