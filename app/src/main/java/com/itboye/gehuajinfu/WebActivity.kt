package com.itboye.gehuajinfu

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.*
import android.util.Log
import android.view.View
import android.webkit.*
import android.widget.TextView
import com.itboye.gehuajinfu.util.Const
import com.itboye.gehuajinfu.util.ShareUtil
import kotlinx.android.synthetic.main.activity_main.*


class WebActivity : Activity() {

    var webSettings: WebSettings? = null
    var isNeedShare = false
    var txt_right: TextView? = null
    var url: String? = null
    var icon_url: String? = null
    var title: String? = null
    var transaction: String? = null
    var bitmap: Bitmap? = null
    var TAG = "web"
    @SuppressLint("JavascriptInterface")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        webView.addJavascriptInterface(JSInterface(), "android")
        webSettings = webView.settings
        //设置支持JavaScript
        webSettings?.loadWithOverviewMode = true
        webSettings?.javaScriptEnabled = true
        webSettings?.setAppCacheEnabled(true)
        webSettings?.domStorageEnabled = true//开启DOM storage API功能
//        webSettings.supportMultipleWindows();
//        webSettings.setAllowContentAccess(true);
        webSettings?.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
        webSettings?.useWideViewPort = true
        webSettings?.loadWithOverviewMode = true
//        webSettings.setSavePassword(true);
//        webSettings.setSaveFormData(true);
        webSettings?.javaScriptCanOpenWindowsAutomatically = true
        webSettings?.loadsImagesAutomatically = true

        //设置不调用浏览器，使用本WebView
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String?) {
                super.onPageFinished(view, url)
            }

            override fun onReceivedError(view: WebView, request: WebResourceRequest?, error: WebResourceError?) {
                super.onReceivedError(view, request, error)
            }

            override fun shouldOverrideUrlLoading(view: WebView, url: String?): Boolean {
                return false
            }
        }
        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                Log.d(TAG, "newProgress：$newProgress")
                if (newProgress == 100) {
                    web_progress.visibility = View.GONE
                } else {
                    web_progress.visibility = View.VISIBLE
                    web_progress.progress = newProgress//设置加载进度
                }
            }

            override fun onShowFileChooser(webView: WebView, filePathCallback: ValueCallback<Array<Uri>>?, fileChooserParams: FileChooserParams?): Boolean {
                return true
            }


        }
        webView.loadUrl(Const.URL)
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        }
        super.onBackPressed()
    }

    inner class JSInterface {
//        @JavascriptInterface
//        fun getSomeThing(url: String) {
//            Toast.makeText(App.getInstance(), url, Toast.LENGTH_SHORT).show()
//        }
        /**
         * 分享朋友
         * @param url 分享的链接
         * @param title 分享的标题
         * @param description 分享的内容
         */
        @JavascriptInterface
        fun shareToWxFriends(url: String, title: String, description: String) {
            ShareUtil.shareUrl(
                    this@WebActivity,
                    url,
                    title,
                    description,
                    BitmapFactory.decodeResource(getResources(), R.mipmap.logo),
                    1
            )
        }

        /**
         * 分享朋友圈
         * @param url 分享的链接
         * @param title 分享的标题
         * @param description 分享的内容
         */
        @JavascriptInterface
        fun shareToWxCircleOfFriends(url: String, title: String, description: String) {
            ShareUtil.shareUrl(
                    this@WebActivity,
                    url,
                    title,
                    description,
                    BitmapFactory.decodeResource(getResources(), R.mipmap.logo),
                    2
            )
        }

        /**
         * 分享QQ
         * @param url 分享的链接
         * @param title 分享的标题
         * @param content 分享的内容
         * @param imageUrl 分享附带的图片路径
         */
        @JavascriptInterface
        fun shareToQQ(url: String, title: String, content: String, imageUrl: String) {
            ShareUtil.shareToQQ(this@WebActivity, url, title, content, imageUrl)
        }
    }

}
