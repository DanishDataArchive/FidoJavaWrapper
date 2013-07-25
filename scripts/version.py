#!/usr/bin/env python

import subprocess, os, sys, datetime, getpass

if __name__ == "__main__":
	version = os.popen("git describe --tag --dirty").readline().strip()

	if len(sys.argv) > 1 and len(sys.argv[1]) > 0:
		version += "-" + sys.argv[1]

	branch = os.popen("git show-branch").readline()
	branch = branch[branch.find("[") + 1 : branch.find("]")].strip()

	currentDate = str(datetime.date.today())
	currentTime = str(datetime.datetime.now().time())

	if len(version) == 0 or len(branch) == 0:
		print "Either git is not in your path or you are building outside the git tree"
		version = "Tag not found"
		branch = "Branch not found"

	user = getpass.getuser()

	f = open("../src/dk/dda/version/BuildInfo.java", "w")
	f.write('package dk.dda.version;\n\n')
	f.write('// Auto-generated, triggered by project build\n')
	f.write('public class BuildInfo {\n')
	f.write('\tpublic static String tag = new String("' + version + '");\n')
	f.write('\tpublic static String branch = new String("' + branch + '");\n')
	f.write('\tpublic static String date = new String("' + currentDate + '");\n')
	f.write('\tpublic static String time = new String("' + currentTime + '");\n')
	f.write('\tpublic static String who = new String("' + user + '");\n')
	f.write('\tpublic static String releaseName = new String(tag + (branch.length() > 0 ? " (" + branch + ")" : ""));\n')
	f.write('}\n')

	f.close()

	print "Version " + version + " from " + branch + " build on " + currentDate + " " + currentTime + " by " + user
