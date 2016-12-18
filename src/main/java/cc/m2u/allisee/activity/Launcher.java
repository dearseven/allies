package cc.m2u.allisee.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import cc.m2u.allisee.R;

public class Launcher extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent t = new Intent(Launcher.this, MainActivity.class);
                startActivity(t);
                finish();
            }
        }, 1500);
    }

    public static Handler h = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };
}
