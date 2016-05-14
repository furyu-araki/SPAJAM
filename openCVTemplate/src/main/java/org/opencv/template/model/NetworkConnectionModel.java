package org.opencv.template.model;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.ResponseHeaderOverrides;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Downloader;

import org.opencv.template.Constants;
import org.opencv.template.TestApplication;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

/**
 * Created by arakitaku on 2016/05/14.
 */
public class NetworkConnectionModel {

    public static final String TAG = NetworkConnectionModel.class.getSimpleName();

    private TestApplication application;

    private OkHttpClient client;
    private OkHttp3Downloader downloader;

    private AmazonS3Client s3Client;


    public NetworkConnectionModel(Context context) {
        application = (TestApplication) context.getApplicationContext();
        client = new OkHttpClient();
        downloader = new OkHttp3Downloader(client);
        s3Client = new AmazonS3Client(new BasicAWSCredentials(Constants.ACCESS_KEY_ID, Constants.SECRET_KEY));
        s3Client.setRegion(Region.getRegion(Regions.US_WEST_2));
    }

    public Observable<Boolean> uploadImage() {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                try {
                    if (isExistBucket(Constants.getPictureBucket()) == false) {
                        s3Client.createBucket(Constants.getPictureBucket());
                    }
                    Log.d("fileName", application.getPictureFileName());
                    PutObjectRequest por = new PutObjectRequest(
                            Constants.getPictureBucket(),
                            application.getPictureFileName(),
                            new File(application.getPictureFilePath())
                    );
                    s3Client.putObject(por);
                    subscriber.onNext(true);
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    public Observable<Integer> downloadImages() {
        int memberCount = application.getMemberCount();
        return Observable.range(1, memberCount).flatMap(new Func1<Integer, Observable<Boolean>>() {
            @Override
            public Observable<Boolean> call(Integer imageNum) {
                return downloadImage(imageNum);
            }
        }).filter(new Func1<Boolean, Boolean>() {
            @Override
            public Boolean call(Boolean success) {
                return success;
            }
        }).count();

    }

    public Observable<Boolean> downloadImage(final int imageNum) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                InputStream inputStream = null;
                FileOutputStream fileOutputStream = null;

                try {
                    ResponseHeaderOverrides override = new ResponseHeaderOverrides();
                    override.setContentType("image/jpeg");
                    GeneratePresignedUrlRequest urlRequest = new GeneratePresignedUrlRequest(Constants.getPictureBucket(), imageNum + ".jpg");
                    urlRequest.setExpiration(new Date(System.currentTimeMillis() + 3600000));
                    urlRequest.setResponseHeaders(override);
                    URL url = s3Client.generatePresignedUrl(urlRequest);
                    Log.d(TAG, "download image url: " + url);

                    inputStream = getInputStream(url);
                    byte[] data = ResourceLoaderUtils.getBytesFromInputStream(inputStream);
                    fileOutputStream = new FileOutputStream(application.DIRECTORY + "/" + imageNum + "jpg");
                    fileOutputStream.write(data);

                    subscriber.onNext(true);
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                } finally {
                    try {
                        if (inputStream != null) {
                            inputStream.close();
                        }
                        if (fileOutputStream != null) {
                            fileOutputStream.close();
                        }
                    } catch (IOException e) {
                        subscriber.onError(e);
                    }
                }
            }
        });
    }

    /**
     * バケットが存在するかどうかをチェックする
     *
     * @param bucketName
     * @return
     */
    private boolean isExistBucket(String bucketName) {
        try {
            s3Client.listObjects(new ListObjectsRequest(bucketName, null, null, null, 0));
        } catch (AmazonServiceException ase) {
            if (ase.getStatusCode()==403) {
                return false;
            } else if (ase.getStatusCode()==404) {
                return false;
            }
        }
        return true;
    }

    /**
     * 画像データのInputStreamを返す。
     *
     * @param url
     * @return
     * @throws IOException
     */
    private InputStream getInputStream(URL url) throws IOException {
        Downloader.Response response = downloader.load(Uri.parse(url.toString()), 1);
        return response.getInputStream();
    }
}
