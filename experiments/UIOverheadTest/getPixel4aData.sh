adb -s 08191JEC212484 shell dumpsys gfxinfo com.react_proxy framestats | grep 0, | tail -1 > react_proxy_framestats.log

./runGetFrameComplete.sh >> frameComplete.log