package com.dsm.spiralmoon.pbangchajja;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.EmptyStackException;
import java.util.Timer;
import java.util.TimerTask;

public class IntroActivity extends AppCompatActivity {

    private Context context;
    public static Activity introActivity;
    short count = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        introActivity = IntroActivity.this;
        context = this;

        Timer timer = new Timer();
        timer.schedule(timerTask, 0, 1000);
    }

    TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            if(count != 0)
                count--;
            else
            {
                this.cancel();

                UserPref pref = new UserPref(context);


                if(pref.getUserId() == null) { //최초 실행
                    Intent intent = new Intent(IntroActivity.this, SetActivity.class);
                    startActivity(intent);
                }
                else {
                    Intent intent = new Intent(IntroActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        }
    };
}