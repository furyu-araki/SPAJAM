# opencv-template
AndroidでOpenCVを使うためのテンプレートです。

## ビルドを通すには

### OpenCVをダウンロードしてくる
http://opencv.org/downloads.html

から、OpenCV for Android をダウンロードしてください。バージョン3.1で動作確認済みです。

### `local.properties`を修正

`local.properties`ファイルに、以下の行を追加してください。

```
opencv.sdk.dir=<Your OpenCV SDK Directory>
```

`<Your OpenCV SDK Directory>`には、ダウンロードしてきた OpenCV for Android のパスを記述してください。私の場合は、`/Users/arakitaku/Library/Android/OpenCV-3.1.0-android-sdk`と記述しました。