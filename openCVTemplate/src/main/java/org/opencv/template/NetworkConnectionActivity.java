package org.opencv.template;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

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

    ImageView imageView;
    Button createAnimationButton;
    Button resultButton;

    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_connection);

        imageView = (ImageView) findViewById(R.id.activity_network_connection_imageview);
        createAnimationButton = (Button) findViewById(R.id.activity_network_connection_button_create);
        resultButton = (Button) findViewById(R.id.activity_network_connection_button_result);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("画像のアップロード中です。");
        progressDialog.show();
        networkConnectionModel = new NetworkConnectionModel(getApplicationContext());
        networkConnectionModel.uploadImage()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean success) {
                        Log.d(TAG, "upload success: " + success);
                        progressDialog.dismiss();
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
                imageView.setImageResource(R.drawable.create_progress);
                networkConnectionModel.downloadImages()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<Integer>() {
                            @Override
                            public void call(Integer count) {
                                Log.d(TAG, "downloaded num: " + count);
                                imageView.setImageResource(R.drawable.create_comprete);
                                createAnimationButton.setVisibility(View.GONE);
                                resultButton.setVisibility(View.VISIBLE);
                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                Log.e(TAG, "download error: ", throwable);
                            }
                        });
            }
        });

        resultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NetworkConnectionActivity.this, ResultActivity.class));
            }
        });
    }

}
