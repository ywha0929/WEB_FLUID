git add .
git add -f FLUID_Injector/apks/*.apk FLUID_Injector/ext_apks/*.apk FLUID_Injector/output/*.apk
git add -f react-proxy/node_modules
git add -f react-proxy/node_modules/* -r
git commit -m $1
git push
