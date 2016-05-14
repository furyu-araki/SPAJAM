package org.opencv.template;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by arakitaku on 2016/05/14.
 */
public class MemberInputActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_input);

        startActivity(new Intent(this, NetworkConnectionActivity.class));
    }
}
