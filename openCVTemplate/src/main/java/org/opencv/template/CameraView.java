package org.opencv.template;

/**
 * Created by arcueid on 16/05/14.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.util.Log;
import android.util.Size;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;


public class CameraView extends SurfaceView implements SurfaceHolder.Callback, Camera.PreviewCallback{

    private Camera mCam;

    private SurfaceHolder mHolder;

    private Bitmap mBitmap;

    public String mSavePath;

    private DoSyncCameraPreview mDoySyncCameraPreview = null;

    /**
     * コンストラクタ
     */
    public CameraView(Context context, Camera cam) {
        super(context);

        mCam = cam;

        // サーフェスホルダーの取得とコールバック通知先の設定
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void savePreviewImage( String savePath )
    {
        mCam.stopPreview();
        mSavePath = savePath;
        mCam.setPreviewCallback(this);
        mCam.startPreview();
    }

    public void setPreviewSyncCallback(DoSyncCameraPreview callback)
    {
        this.mDoySyncCameraPreview = callback;
    }

    /**
     * SurfaceView 生成
     */
    public void surfaceCreated(SurfaceHolder holder) {
        try {
         //カメラインスタンスに、画像表示先を設定
        mCam.setPreviewDisplay(holder);
            //mCam.setPreviewCallbackWithBuffer(this);
            //mCam.setPreviewCallback(this);

        Camera.Parameters parameters = mCam.getParameters();
        List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();
        List<Camera.Size> pictureSizes = parameters.getSupportedPictureSizes();

        // プレビューサイズとピクチャーサイズが同一比率の解像度を選択する
        for (Camera.Size picSiz: pictureSizes)
        {
            for (Camera.Size preSiz: previewSizes)
            {
                if(picSiz.width * preSiz.height == preSiz.width * picSiz.height)
                {
                    // ここでpreSizとpicSizが同一の比率と判定できます
                    parameters.setPreviewSize(preSiz.width, preSiz.height);
//                    int bufferSize = preSiz.width * preSiz.height * ImageFormat.getBitsPerPixel(parameters.getPreviewFormat())/8;
//                    mFrameBuffer = new byte[bufferSize];
//
//                    //バッファ変更
//                    mCam.addCallbackBuffer(mFrameBuffer);
                }
            }
        }

        mCam.setParameters(parameters);

        // プレビュー開始
        mCam.startPreview();

        } catch (IOException e) {
        //
        }
    }

    /**
     * SurfaceView 破棄
     */
    public void surfaceDestroyed(SurfaceHolder holder) {
        mCam.setPreviewCallback(null);
    }

    /**
     * SurfaceHolder が変化したときのイベント
     */
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // 画面回転に対応する場合は、ここでプレビューを停止し、
        // 回転による処理を実施、再度プレビューを開始する。

        Camera.Parameters parameters = mCam.getParameters();
        List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();
        Camera.Size optimalSize = getOptimalPreviewSize(sizes,width,height);
        parameters.setPreviewSize(optimalSize.width,optimalSize.height);

        mCam.startPreview();
    }

    //アスペクト比を保持した最適なサイズを返す(これ以外のサイズで表示しようとすると画像が崩れる）
    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {

        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio=(double)h / w;

        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    public void onPreviewFrame(byte[] data, Camera camera)
    {
        Log.d( "camera", "onPreviewFrame");
        mCam.stopPreview();
        mCam.setPreviewCallback(null);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mSavePath);
            int previewWidth = camera.getParameters().getPreviewSize().width;
            int previewHeight = camera.getParameters().getPreviewSize().height;
            Bitmap previewBmp = getBitmapImageFromYUV(data,previewWidth, previewHeight);
            previewBmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            //fos.write(data);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mDoySyncCameraPreview.onSaveFinished();

//スタートさせない
        //mCam.startPreview();


//        mCam.addCallbackBuffer(mFrameBuffer);
//
//        Camera.Parameters parameters = mCam.getParameters();
//        Camera.Size size = parameters.getPreviewSize();
//
//        //試しにグレースケール
//        for( int i = 0; i < mBitmapBuffer.length; i++)
//        {
//            int gray = data[i] & 0xff;
//            mBitmapBuffer[i] = 0xff000000 | gray << 16 | gray << 8 | gray;
//        }
//
//        mBitmap.setPixels( mBitmapBuffer, 0, size.width, 0, 0, size.width, size.height);
//
//        Rect srcRect = new Rect(0, 0, size.width, size.height);
//        Canvas canvas = mHolder.lockCanvas();
//        Rect dstRect = new Rect(0, 0, canvas.getWidth(), canvas.getHeight());
////        canvas.drawBitmap(mBitmap, 0, 0, null);
//        canvas.drawBitmap( mBitmap, srcRect, dstRect, null);
//        mHolder.unlockCanvasAndPost(canvas);
    }

    public Bitmap getBitmapImageFromYUV(byte[] data, int width, int height) {
        YuvImage yuvimage = new YuvImage(data, ImageFormat.NV21, width, height, null);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        yuvimage.compressToJpeg(new Rect(0, 0, width, height), 80, baos);
        byte[] jdata = baos.toByteArray();
        BitmapFactory.Options bitmapFatoryOptions = new BitmapFactory.Options();
        bitmapFatoryOptions.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bmp = BitmapFactory.decodeByteArray(jdata, 0, jdata.length, bitmapFatoryOptions);
        return bmp;
    }

}