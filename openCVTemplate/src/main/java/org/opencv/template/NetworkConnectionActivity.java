package org.opencv.template;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import org.opencv.template.model.NetworkConnectionModel;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by arakitaku on 2016/05/14.
 */
public class NetworkConnectionActivity extends Activity {

    public static final String TAG = NetworkConnectionActivity.class.getSimpleName();

    NetworkConnectionModel networkConnectionModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_connection);

        networkConnectionModel = new NetworkConnectionModel();
        networkConnectionModel.uploadImage()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean success) {
                        Log.d(TAG, "upload success: " + success);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.e(TAG, "upload error: ", throwable);
                    }
                });
    }

}
