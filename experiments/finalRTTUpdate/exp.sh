adb -s 13201FDD40024R  logcat -d -s "FLUID(EXP)" | grep "FLUID(EXP)" >> file.txt
adb -s 08191JEC212484 logcat -d| grep "FLUID(EXP)">> file.txt
adb -s 08191JEC212484 shell dumpsys gfxinfo com.react_proxy framestats | grep 0, | tail -1> react_proxy_framestats.log
gcc getframeComplete.c -o getframeComplete
./getframeComplete >> file.txt
adb -s 13201FDD40024R logcat -c
adb -s 08191JEC212484 logcat -c
adb -s 08191JEC212484 logcat -c
adb -s 08191JEC212484 logcat -c