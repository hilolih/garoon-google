#!/bin/bash
_dir=$(dirname $0)
cd $_dir

echo "[*] 1度目の同期を実行します"
java -jar GGsync.jar .

if [ $? -gt 0 ]; then
    echo "[!] 同期に失敗。GGSync.logを確認してください"
    exit 2
fi


echo "[*] 2度目の同期を実行します"
java -jar GGsync.jar .

if [ $? -gt 0 ]; then
    echo "[!] 同期に失敗。GGSync.logを確認してください"
    exit 2
fi

echo "[*] 正常終了"
