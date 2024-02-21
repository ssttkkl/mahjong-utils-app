#!/usr/bin/env bash

cd build
if [ ! -f appimagetool-x86_64.AppImage ]; then
  wget https://github.com/AppImage/AppImageKit/releases/download/continuous/appimagetool-x86_64.AppImage
fi
chmod +x appimagetool-x86_64.AppImage

\cp -rf ../composeApp/build/compose/binaries/main/app/**/* ../composeApp/mahjong-utils-app.AppDir/
chmod +x ../composeApp/mahjong-utils-app.AppDir/bin/mahjong-utils-app
./appimagetool-x86_64.AppImage ../composeApp/mahjong-utils-app.AppDir mahjong-utils-app.AppImage
