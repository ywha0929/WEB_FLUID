./sign.sh output/*.apk test-key.jks "000000"
adb -s 13201FDD40024R install output/*.apk
adb -s 08191JEC212484 install output/*.apk
