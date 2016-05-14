package org.opencv.template;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

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

    Button createAnimationButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_connection);

        createAnimationButton = (Button) findViewById(R.id.activity_network_connection_button_create);

        networkConnectionModel = new NetworkConnectionModel(getApplicationContext());
        networkConnectionModel.uploadImage()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean success) {
                        Log.d(TAG, "upload success: " + success);
                        createAnimationButton.setVisibility(View.VISIBLE);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.e(TAG, "upload error: ", throwable);
                    }
                });

        createAnimationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                networkConnectionModel.downloadImage()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<Boolean>() {
                            @Override
                            public void call(Boolean success) {
                                Log.d(TAG, "download success!!");
                                startActivity(new Intent(NetworkConnectionActivity.this, ResultActivity.class));
                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                Log.e(TAG, "download error: ", throwable);
                            }
                        });
            }
        });
    }

}
