package com.itboye.gehuajinfu;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.*;
import android.widget.ProgressBar;
import android.widget.Toast;

import static android.view.KeyEvent.KEYCODE_BACK;

public class MainActivity extends Activity {

    WebView mWebView;
    ProgressBar web_progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    @SuppressLint({"AddJavascriptInterface", "SetJavaScriptEnabled"})
    private void initView() {
        startActivity(new Intent(this, WebActivity.class));
        mWebView = findViewById(R.id.webView);
        web_progress = findViewById(R.id.web_progress);
        mWebView.addJavascriptInterface(new JSInterface(), "android");
        WebSettings webSettings = mWebView.getSettings();
        //设置支持JavaScript
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setDomStorageEnabled(true);//开启DOM storage API功能
//        webSettings.supportMultipleWindows();
//        webSettings.setAllowContentAccess(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSettings.setUseWideViewPort(true);
//        webSettings.setSavePassword(true);
//        webSettings.setSaveFormData(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setLoadsImagesAutomatically(true);


//        mWebView.loadUrl(Const.URL);

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                Log.d("KeithXiaoY", "开始加载");
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.d("KeithXiaoY", "加载结束");
            }

            // 链接跳转都会走这个方法
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d("KeithXiaoY", "Url：" + url);
                return super.shouldOverrideUrlLoading(view, url);
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                Log.d("KeithXiaoY", "newProgress：" + newProgress);
                if (newProgress == 100) {
                    web_progress.setVisibility(View.GONE);
                } else {
                    web_progress.setVisibility(View.VISIBLE);
                    web_progress.setProgress(newProgress);//设置加载进度
                }
            }

        });

        mWebView.loadUrl("file:///android_asset/html/config_detail.html");
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KEYCODE_BACK) && mWebView.canGoBack()) {
//            ShareUtil.shareUrl(this,"http:www.baidu.com","分享测试","分享描述", BitmapFactory.decodeResource(getResources(),R.mipmap.logo),1);
//            ShareUtil.shareUrl(this,"http:www.baidu.com","分享测试","分享描述", BitmapFactory.decodeResource(getResources(),R.mipmap.logo),2);
//            ShareUtil.shareToQQ(this);
            mWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    class JSInterface {

        @JavascriptInterface
        void getSomeThing() {
            Toast.makeText(MainActivity.this, "开始分享", Toast.LENGTH_SHORT).show();
        }

//        @JavascriptInterface
//        void shareToWxFriends() {
//            ShareUtil.shareUrl(MainActivity.this, "http:www.baidu.com", "分享测试", "分享描述", BitmapFactory.decodeResource(getResources(), R.mipmap.logo), 1);
//        }
//
//        @JavascriptInterface
//        void shareToWxCircleOfFriends() {
//            ShareUtil.shareUrl(MainActivity.this, "http:www.baidu.com", "分享测试", "分享描述", BitmapFactory.decodeResource(getResources(), R.mipmap.logo), 1);
//        }
//
//        @JavascriptInterface
//        void shareToQQ() {
//            ShareUtil.shareToQQ(MainActivity.this);
//        }
        //
    }

}
