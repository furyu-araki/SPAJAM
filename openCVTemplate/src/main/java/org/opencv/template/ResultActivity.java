package org.opencv.template;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * Created by arakitaku on 2016/05/14.
 */
public class ResultActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        //SurfaceViewをセットする
        LinearLayout l = new LinearLayout(this);
        setContentView(l);
        SurfaceView surfaceView = new ParaparaSurfaceView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        surfaceView.setLayoutParams(params);
        l.addView(surfaceView);

    }

}