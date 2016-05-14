package org.opencv.template;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.audiofx.EnvironmentalReverb;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.io.FileOutputStream;

/**
 * Created by arakitaku on 2016/05/14.
 */
public class CameraActivity extends Activity {

    //カメラインスタンス
    private Camera mCamera = null;

    private CameraView mCameraView = null;

    private Activity mOwenerActivity;

    private FirebaseHelper mFirebaseHelper;

    private int mMyNumber;

    // 音関係
    private SoundPool mSoundPool;
    private int mSoundId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        mOwenerActivity = this;

        TestApplication ta = (TestApplication) mOwenerActivity.getApplication();
        mMyNumber = ta.getNumberOfMember();

        mFirebaseHelper = new FirebaseHelper(this, new FirebaseHelper.OnNextDeviceBroadcastReceiver() {
            @Override
            public void onNextDeviceBroadcastReceive(int num) {
                // 自分の端末の番号なら、シャッターを切る
                if( mMyNumber == num )
                {
                    if (mCamera != null) {
                        TestApplication ta = (TestApplication) mOwenerActivity.getApplication();
                        int nextNum = mMyNumber + 1;
                        mFirebaseHelper.broadcastNextDevice(nextNum);

                        // 撮影実行
                        mCamera.takePicture(shutterListener_, null, pictureListener_);
                    }
                }
            }
        });

        try {
            mCamera = Camera.open();
        }catch(Exception e){
            this.finish();
        }
        FrameLayout preview = (FrameLayout)findViewById(R.id.CameraView);
        mCameraView = new CameraView(this, mCamera);
        ///preview.addView( mCameraView, new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));

        //解像度からViewの表示サイズを設定する
        WindowManager wm = (WindowManager)getSystemService(WINDOW_SERVICE);
        Display disp = wm.getDefaultDisplay();
        Point size = new Point();
        disp.getSize(size);
        if( size.x < size.y )
        {
            size.y = size.x / 3 * 4;
        }
        else
        {
            size.x = size.y / 3 * 4;
        }
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size.x, size.y);
        params.gravity = Gravity.CENTER_VERTICAL;
        mCameraView.setLayoutParams(params);
        preview.addView( mCameraView, params);

        // タッチリスナー設定
        mCameraView.setOnTouchListener( ontouchListener_ );

    }

    @Override
    protected void onResume() {
        super.onResume();
        // 音声データ読み込み
        mSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        mSoundId = mSoundPool.load(getApplicationContext(), R.raw.shutter, 0);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        mSoundPool.release();
        //mCamera.release();
        //mCamera = null;
    }

    // ディスプレイの向き設定
    public void setCameraDisplayOrientation(int cameraId) {
        // カメラの情報取得
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, cameraInfo);
        // ディスプレイの向き取得
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        // プレビューの向き計算
        int result;
        if (cameraInfo.facing == cameraInfo.CAMERA_FACING_FRONT) {
            result = (cameraInfo.orientation + degrees) % 360;
            result = (360 - result) % 360; // compensate the mirror
        } else {// back-facing
            result = (cameraInfo.orientation - degrees + 360) % 360;
        }
        // ディスプレイの向き設定
        mCamera.setDisplayOrientation(result);
    }
    // 画面タッチ時のコールバック
    View.OnTouchListener ontouchListener_ = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (mCamera != null && mMyNumber == 1 ) {
                    TestApplication ta = (TestApplication) mOwenerActivity.getApplication();
                    int nextNum = mMyNumber + 1;
                    mFirebaseHelper.broadcastNextDevice(nextNum);

                    // 撮影実行
                    mCamera.takePicture(shutterListener_, null, pictureListener_);
                }
            }
            return false;
        }
    };

    // シャッターが押されたときに呼ばれるコールバック
    private Camera.ShutterCallback shutterListener_ = new Camera.ShutterCallback() {
        public void onShutter() {
        }
    };

    // JPEGイメージ生成後に呼ばれるコールバック
    private Camera.PictureCallback pictureListener_ = new Camera.PictureCallback() {
        // データ生成完了
        public void onPictureTaken(byte[] data, Camera camera) {
            // SDカードにJPEGデータを保存する
            if (data != null) {
                int rotation = mOwenerActivity.getWindowManager().getDefaultDisplay().getRotation();
                int degrees = 0; //端末の向き(度換算)
                switch (rotation) {
                    case Surface.ROTATION_0: degrees = 0; break;
                    case Surface.ROTATION_90: degrees = 90; break;
                    case Surface.ROTATION_180: degrees = 180; break;
                    case Surface.ROTATION_270: degrees = 270; break;
                }
                Matrix m = new Matrix(); //Bitmapの回転用Matrix
                m.setRotate(90-degrees);    // 向きが正しくなるように回転角度を補正
                Bitmap original = BitmapFactory.decodeByteArray(data, 0, data.length);
                //サイズを小さくする
                int w = 1000;
                int h = 1000;
                double aspectRatio = 1.0;
                double originalW = original.getWidth();
                double originalH = original.getHeight();
                if( originalW < originalH)
                {
                    aspectRatio = originalH / originalW;
                    h = (int)(aspectRatio * w);
                }
                else
                {
                    aspectRatio = originalW / originalH;
                    w = (int)(aspectRatio * h);
                }

                Bitmap streched = Bitmap.createScaledBitmap(original, w, h, true );
                Bitmap rotated = Bitmap.createBitmap( streched, 0, 0, streched.getWidth(), streched.getHeight(), m, true);

                FileOutputStream fos = null;
                try {
                    String directory = Environment.getExternalStorageDirectory().getPath();
                    TestApplication ta = (TestApplication) mOwenerActivity.getApplication();

                    String fileName =  ta.getNumberOfMember() + ".jpg";
                    String savePath = directory + "/" + fileName;

                    fos = new FileOutputStream(savePath);
                    rotated.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    //fos.write(data);
                    fos.close();
                    ta.setPictureFileName(fileName);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                original.recycle();
                rotated.recycle();

                camera.stopPreview();

                //遷移
                Intent intent = new Intent(CameraActivity.this, NetworkConnectionActivity.class);
                startActivity(intent);


            }
        }
    };
}
