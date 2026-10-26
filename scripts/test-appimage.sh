#!/usr/bin/env bash
# Run inside an offline network namespace and Xvfb (see Desktop Distribution).
set -euo pipefail
appimage=$(realpath "$1")
work=$(mktemp -d)
cleanup() {
    if [[ -n "${app_pid:-}" ]]; then kill "$app_pid" 2>/dev/null || true; fi
    rm -rf "$work"
}
trap cleanup EXIT
cd "$work"
"$appimage" --appimage-extract > extract.log
for executable in AppRun bin/mahjong-utils-app; do
    test -x "squashfs-root/$executable"
    stat -c '%a %n' "squashfs-root/$executable"
done
# Protect existing application preferences and histories during the smoke test.
export HOME="$work/home"
mkdir -p "$HOME"
export JAVA_TOOL_OPTIONS="-Duser.home=$HOME"
./squashfs-root/AppRun > launch.log 2>&1 &
app_pid=$!
for attempt in $(seq 1 60); do
    if ! kill -0 "$app_pid" 2>/dev/null; then
        cat launch.log
        echo 'AppImage exited before showing a window' >&2
        exit 1
    fi
    window_ids=$(xwininfo -root -tree | awk '/"Riichi Mahjong Calculator"/ {print $1}')
    for window_id in $window_ids; do
        if xwininfo -id "$window_id" | grep -q 'Map State: IsViewable'; then
            sleep 5
            kill -0 "$app_pid"
            cat launch.log
            echo 'AppImage displayed a visible window and stayed alive without network access'
            exit 0
        fi
    done
    sleep 1
done
cat launch.log
xwininfo -root -tree
echo 'AppImage did not display its window within 60 seconds' >&2
exit 1
