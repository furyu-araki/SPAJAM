package org.opencv.template.model;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class ResourceLoaderUtils {

    /**
     * ファイルのByteArrayを返す
     * @param filePath
     * @return
     */
    public static byte[] fileToByteArray(String filePath) {
        try{
            File file = new File(filePath);
            FileInputStream fileInputStream = new FileInputStream(file);
            byte[] buffer = new byte[8192];
            int bytesRead;
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            while((bytesRead = fileInputStream.read(buffer)) != -1){
                outputStream.write(buffer, 0, bytesRead);
            }

            return outputStream.toByteArray();
        } catch (Exception exception){
            Log.e("LoaderUtils", "エラー、ファイルを開くことが出来ない");
        }
        return null;
    }

    /**
     * URIから画像のバイト配列を取得する
     *
     * @param uri
     * @param context
     * @return
     */
    public static byte[] getImageByteArray(Uri uri, Context context) {
        try {
            InputStream iStream = context.getContentResolver().openInputStream(uri);
            byte[] inputData = getBytesFromInputStream(iStream);
            return inputData;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception exception){
            Log.e("LoaderUtils", "ファイルを開くことが出来ませんでした");
        }
        return null;
    }

    /**
     * InputStreamからバイト配列を取得する
     *
     * @param inputStream
     * @return
     */
    public static byte[] getBytesFromInputStream(InputStream inputStream) {
        try {
            ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];

            int len = 0;
            while ((len = inputStream.read(buffer)) != -1) {
                byteBuffer.write(buffer, 0, len);
            }
            return byteBuffer.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception exception){
            Log.e("LoaderUtils", "ファイルを開くことが出来ませんでした", exception);
        }
        return null;
    }

}