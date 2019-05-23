package com.itboye.gehuajinfu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initView();
    }

    private void initView() {
//        List listAll=new ArrayList();
//        List list=new ArrayList();
//        list.add(1);
//        list.add("");
//        list.add('c');
//        listAll.addAll(list);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
                startActivity(new Intent(SplashActivity.this, WebActivity.class));
            }
        }, 2000);
    }
}
