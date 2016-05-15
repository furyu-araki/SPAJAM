package org.opencv.template;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

/**
 * Created by arakitaku on 2016/05/14.
 */
public class MemberInputActivity extends Activity {

    private TestApplication ta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_input);

        /* getApplication()で自己アプリケーションクラスのインスタンスを拾う */
        ta = (TestApplication) this.getApplication();

        ImageButton button = (ImageButton) findViewById(R.id.button);
        // ボタンがクリックされた時に呼び出されるコールバックリスナーを登録します
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ta.setMemberCount( Integer.parseInt( ((EditText)findViewById(R.id.edittext)).getText().toString()) );
                ta.setNumberOfMember( Integer.parseInt( ((EditText)findViewById(R.id.edittext2)).getText().toString()) );

                //遷移
                Intent intent = new Intent(MemberInputActivity.this, CameraActivity.class);
                startActivity(intent);
            }
        });

    }
}
