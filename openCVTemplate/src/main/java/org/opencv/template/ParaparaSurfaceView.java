package org.opencv.template;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Environment;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by umama on 2016/05/14.
 */
public class ParaparaSurfaceView extends SurfaceView implements Callback, Runnable {

    private TestApplication application;

    private SurfaceHolder mHolder;
    private Thread mLooper;

    private List<Bitmap> mImages = new ArrayList<Bitmap>();

    public ParaparaSurfaceView(Context context) {
        super(context);

        application = (TestApplication) context.getApplicationContext();

        mHolder = getHolder();
        mHolder.addCallback(this);

        File dir = new File(application.DIRECTORY);
        if(dir.exists())
        {
            String fileName = "/";

            for ( int i = 1; i < application.getMemberCount() + 1; i++ )
            {
                String imageFileName = fileName + i + ".jpg";

                File file = new File(application.DIRECTORY + imageFileName);
                if (file.exists())
                {
                    mImages.add(BitmapFactory.decodeFile(file.getPath()));
                } else {
                    break;
                }
            }
        }
    }

    //サーフェイス変化で実行される
    @Override
    public void surfaceChanged(SurfaceHolder holder, int f, int w, int h) {
    }

    //サーフェイス生成で実行される
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mLooper = new Thread(this);
        mLooper.start();
    }

    //サーフェイス破棄で実行される
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mLooper = null;
    }

    public void run() {
        //描画処理
        while( mLooper != null ) {
            for (Bitmap i : mImages) {
                Canvas c = mHolder.lockCanvas();
                if (c == null) {
                    return;
                }

                Bitmap bitmap = Bitmap.createScaledBitmap(i, 960, 720, false);
                Paint paint = new Paint();
                c.drawBitmap(bitmap, 0, 0, paint);

                mHolder.unlockCanvasAndPost(c);

                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}