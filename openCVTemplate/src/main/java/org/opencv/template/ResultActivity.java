package org.opencv.template;

import android.app.Activity;
import android.os.Bundle;
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
        l.addView(new ParaparaSurfaceView(this));
    }

}