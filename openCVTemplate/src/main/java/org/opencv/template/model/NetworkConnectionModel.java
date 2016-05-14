package org.opencv.template.model;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by arakitaku on 2016/05/14.
 */
public class NetworkConnectionModel {

    public static final String TAG = NetworkConnectionModel.class.getSimpleName();

    public static final MediaType JPEG = MediaType.parse("image/jpeg");

    public static final String DIRECTORY = Environment.getExternalStorageDirectory() + "/spajam";

    OkHttpClient client;


    public NetworkConnectionModel() {
        File file = new File(DIRECTORY);
        Log.d(TAG, "mkdir: " + file.mkdir());
        client = new OkHttpClient();
    }

    public Observable<Boolean> uploadImage() {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                try {
                    RequestBody body = RequestBody.create(JPEG, new File(DIRECTORY + "/image0.jpg"));
                    Request request = new Request.Builder()
                            .url("")
                            .post(body)
                            .build();
                    Response response = client.newCall(request).execute();
                    subscriber.onNext(response.isSuccessful());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
