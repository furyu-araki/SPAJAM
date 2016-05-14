package org.opencv.template;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by arakitaku on 2016/05/14.
 */
public class MemberInputActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_input);

        Button button = (Button) findViewById(R.id.button);
        // ボタンがクリックされた時に呼び出されるコールバックリスナーを登録します
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ボタンがクリックされた時に呼び出されます
                //Button button = (Button) v;

                //遷移
                Intent intent = new Intent(MemberInputActivity.this, CameraActivity.class);
                startActivity(intent);
            }
        });

    }
}
