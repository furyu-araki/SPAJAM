package org.opencv.template.model;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.ResponseHeaderOverrides;

import org.opencv.template.Constants;

import java.io.File;
import java.net.URL;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
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
                    PutObjectRequest por = new PutObjectRequest(Constants.getPictureBucket(), "image0.jpg", new File(DIRECTORY + "/image0.jpg"));
                    s3Client.putObject(por);
                    subscriber.onNext(true);
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    public Observable<String> downloadImage() {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                    ResponseHeaderOverrides override = new ResponseHeaderOverrides();
                    override.setContentType("image/jpeg");
                    GeneratePresignedUrlRequest urlRequest = new GeneratePresignedUrlRequest(Constants.getPictureBucket(), "image0.jpg");
                    urlRequest.setExpiration(new Date(System.currentTimeMillis() + 3600000));
                    urlRequest.setResponseHeaders(override);
                    URL url = s3Client.generatePresignedUrl(urlRequest);
                    subscriber.onNext(url.toString());
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }
}
