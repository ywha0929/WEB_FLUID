./gradlew assembleRelease
/home/ohsang1213/Android/Sdk/build-tools/32.0.0/zipalign -v -p 4 ./app/build/outputs/apk/release/app-release-unsigned.apk ./temp.apk
/home/ohsang1213/Android/Sdk/build-tools/32.0.0/apksigner sign --ks ./hmsl-key.jks --out ./app/build/outputs/apk/release/app-release-signed.apk --ks-pass pass:hmsl1234 ./temp.apk
/home/ohsang1213/Android/Sdk/build-tools/32.0.0/apksigner verify ./app/build/outputs/apk/release/app-release-signed.apk
rm ./app/build/outputs/apk/release/app-release-unsigned.apk
rm ./temp.apk
echo "Output file: ./app/build/outputs/apk/release/app-release-signed.apk"
adb push ./app/build/outputs/apk/release/app-release-signed.apk /data/local/tmp/fluidlib.apk
