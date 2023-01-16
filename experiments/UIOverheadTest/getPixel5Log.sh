
adb -s 13201FDD40024R logcat -d | grep RunUpdate | tail -21> pixel5RunUpdateLog.log
adb -s 13201FDD40024R logcat -c
./runGetPixel5RunUpdateTimestamp.sh >> pixel5Timestamp.log