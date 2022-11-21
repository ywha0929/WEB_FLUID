#!/bin/bash

function build_notify()
{
	(zenity --info --display=:0.0 --timeout=10 --title="Notification for DUI analysis" --text="<b><span color=\"green\">#### analysis completed successfully ####</span></b>") &
	(sleep 2 && wmctrl -F -r "Notification for DUI analysis" -b add,above)
}

if [ $# -eq 2 ]
then
	mkdir -p results
	java -cp './libs/sootclasses-trunk-jar-with-dependencies.jar':'./build' DuiCHATool $1 $2 > analysis.log
	(build_notify)
else
	echo "USAGE: ./execute.sh [TARGET_APP_PACKAGE_NAME] [TARGET_APP_JAR]"
fi
