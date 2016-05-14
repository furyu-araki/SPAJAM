package org.opencv.template.model;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;

import org.opencv.template.Constants;

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

    private AmazonS3Client s3Client;


    public NetworkConnectionModel(Context context) {
        File file = new File(DIRECTORY);
        Log.d(TAG, "mkdir: " + file.mkdir());
        client = new OkHttpClient();
        s3Client = new AmazonS3Client(new BasicAWSCredentials(Constants.ACCESS_KEY_ID, Constants.SECRET_KEY));
        s3Client.setRegion(Region.getRegion(Regions.US_WEST_2));
    }

    public Observable<Boolean> uploadImage() {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                try {
                    s3Client.createBucket(Constants.getPictureBucket());
                    PutObjectRequest por = new PutObjectRequest( Constants.getPictureBucket(), "image0.jpg", new File(DIRECTORY + "/image0.jpg"));
                    s3Client.putObject(por);
                    subscriber.onNext(true);
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }
}
