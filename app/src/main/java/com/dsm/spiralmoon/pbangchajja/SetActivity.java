package com.dsm.spiralmoon.pbangchajja;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SetActivity extends AppCompatActivity {

    private EditText idField;
    private Button startButton;
    private UserPref pref;

    public static Activity setActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userset);

        idField = (EditText) findViewById(R.id.userset_userid);
        startButton = (Button) findViewById(R.id.userset_button);
        startButton.setOnClickListener(listener);

        pref = new UserPref(this);
        setActivity = SetActivity.this;
    }

    private Button.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            if(idField.getText().toString().equals("")) //Did not input the ID
                Toast.makeText(SetActivity.this, "아이디를 입력해주세요.", Toast.LENGTH_SHORT).show();
            else {
                pref.setUserId(idField.getText().toString());
                SearchResult.userid = pref.getUserId();

                Toast.makeText(SetActivity.this, "아이디가 " +
                        idField.getText().toString() +
                        "로 설정되었습니다.", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(SetActivity.this, MainActivity.class);
                startActivity(intent);
            }
        }
    };
}
