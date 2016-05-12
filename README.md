# opencv-template
AndroidでOpenCVを使うためのテンプレートです。

## ビルドを通すには

### OpenCVをダウンロードしてくる
http://opencv.org/downloads.html

から、OpenCV for Android をダウンロードしてください。バージョン3.1で動作確認済みです。

### Android.mkを修正

`openCVTemplate/src/main/jni/Android.mk`の

```
include /Users/arakitaku/Library/Android/OpenCV-3.1.0-android-sdk/sdk/native/jni/OpenCV.mk
```

を自分の環境に合うように変更してください。