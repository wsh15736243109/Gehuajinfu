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
import android.widget.Toast
import com.itboye.gehuajinfu.util.Const
import com.itboye.gehuajinfu.util.DownLoadCallBack
import com.itboye.gehuajinfu.util.ShareUtil
import com.itboye.gehuajinfu.util.ShareUtil.downLoadBitmap
import kotlinx.android.synthetic.main.activity_main.*
import android.content.Intent
import android.webkit.ValueCallback
import android.provider.MediaStore


class WebActivity : Activity() {

    var webSettings: WebSettings? = null
    var url: String? = null
    var title: String? = null
    var TAG = "web_log"
    private var mUploadCallbackBelow: ValueCallback<Uri>? = null

    private var mUploadCallbackAboveL: ValueCallback<Array<Uri>>? = null

    @SuppressLint("JavascriptInterface", "SetJavaScriptEnabled")
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
        webSettings?.loadWithOverviewMode = false
//        webSettings.setSavePassword(true);
//        webSettings.setSaveFormData(true);
        webSettings?.javaScriptCanOpenWindowsAutomatically = true
        webSettings?.loadsImagesAutomatically = true

        //设置不调用浏览器，使用本WebView
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String?) {
                super.onPageFinished(view, url)
                Log.v(TAG, "onPageFinished-------------" + url)
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                Log.v(TAG, "onPageStarted-------------" + url)
            }

            override fun onReceivedError(view: WebView, request: WebResourceRequest?, error: WebResourceError?) {
                super.onReceivedError(view, request, error)
                Log.v(TAG, "onReceivedError-------------" + Const.URL)
            }

            override fun shouldOverrideUrlLoading(view: WebView, innerUrl: String?): Boolean {
                view.loadUrl(innerUrl)
                Log.v(TAG, "shouldOverrideUrlLoading-------------" + innerUrl)
                return true
            }
        }
        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                if (newProgress == 100) {
                    web_progress.visibility = View.GONE
                } else {
                    web_progress.visibility = View.VISIBLE
                    web_progress.progress = newProgress//设置加载进度
                }
            }

            /**
             * 16(Android 4.1.2) <= API <= 20(Android 4.4W.2)回调此方法
             */
            fun openFileChooser(uploadMsg: ValueCallback<Uri>, acceptType: String, capture: String) {
                mUploadCallbackBelow = uploadMsg
//                openCamera()
                takePhoto()
            }

            override fun onShowFileChooser(webView: WebView, filePathCallback: ValueCallback<Array<Uri>>?, fileChooserParams: FileChooserParams?): Boolean {
                Log.v(TAG, "onShowFileChooser-------------" + Const.URL)
                mUploadCallbackAboveL = filePathCallback
//                openCamera()
                takePhoto()
                return true
            }


        }
        Log.v(TAG, "开始加载-------------" + Const.URL)
        webView.loadUrl(Const.URL)
//        webView.loadUrl("file:///android_asset/html/config_detail.html")
    }

    private val REQUEST_CODE: Int = 101

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, REQUEST_CODE)
    }

    //相册
    private fun takePhoto() {
        val i = Intent(Intent.ACTION_GET_CONTENT)
        i.addCategory(Intent.CATEGORY_OPENABLE)
        i.type = "image/*"
        startActivityForResult(Intent.createChooser(i, "Image Chooser"), 100)
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }

    private var imageUri: Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101||requestCode == 100) {
            imageUri = data!!.data
            //针对5.0以上, 以下区分处理方法
            if (mUploadCallbackBelow != null) {
                chooseBelow(resultCode, data)
            } else if (mUploadCallbackAboveL != null) {
                chooseAbove(resultCode, data)
            } else {
                Toast.makeText(this, "发生错误", Toast.LENGTH_SHORT).show()
            }
        }
//        else if (requestCode == 101) {
//            imageUri = data!!.data
//        }
    }

    /**
     * Android API >= 21(Android 5.0) 版本的回调处理
     *
     * @param resultCode 选取文件或拍照的返回码
     * @param data       选取文件或拍照的返回结果
     */
    private fun chooseAbove(resultCode: Int, data: Intent?) {
        if (Activity.RESULT_OK == resultCode) {
            updatePhotos()

            if (data != null) {
                // 这里是针对从文件中选图片的处理, 区别是一个返回的URI, 一个是URI[]
                val results: Array<Uri>
                val uriData = data.data
                if (uriData != null) {
                    results = arrayOf(uriData)
                    mUploadCallbackAboveL!!.onReceiveValue(results)
                } else {
                    mUploadCallbackAboveL!!.onReceiveValue(null)
                }
            } else {
                mUploadCallbackAboveL!!.onReceiveValue(arrayOf<Uri>(imageUri!!))
            }
        } else {
            mUploadCallbackAboveL!!.onReceiveValue(null)
        }
        mUploadCallbackAboveL = null
    }

    private fun updatePhotos() {
        // 该广播即使多发（即选取照片成功时也发送）也没有关系，只是唤醒系统刷新媒体文件
        val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        intent.data = imageUri
        sendBroadcast(intent)
    }

    /**
     * Android API < 21(Android 5.0)版本的回调处理
     *
     * @param resultCode 选取文件或拍照的返回码
     * @param data       选取文件或拍照的返回结果
     */
    private fun chooseBelow(resultCode: Int, data: Intent?) {
        if (Activity.RESULT_OK == resultCode) {
            updatePhotos()
            if (data != null) {
                // 这里是针对文件路径处理
                val uri = data.data
                if (uri != null) {
                    mUploadCallbackBelow!!.onReceiveValue(uri)
                } else {
                    mUploadCallbackBelow!!.onReceiveValue(null)
                }
            } else {
                // 以指定图像存储路径的方式调起相机，成功后返回data为空
                mUploadCallbackBelow!!.onReceiveValue(imageUri)
            }
        } else {
            mUploadCallbackBelow!!.onReceiveValue(null)
        }
    }

    inner class JSInterface {
        /**
         * 分享朋友
         * @param url 分享的链接
         * @param title 分享的标题
         * @param description 分享的内容
         * @param imageUrl 分享图片地址
         */
        @JavascriptInterface
        fun shareToWxFriends(url: String, title: String, description: String, imageUrl: String) {
            Log.v(TAG, "shareToWxFriends imageUrl=-------------" + imageUrl)
            downLoadBitmap(this@WebActivity, imageUrl) { bitmap ->
                ShareUtil.shareUrl(
                        this@WebActivity,
                        url,
                        title,
                        description,
                        bitmap,
                        1
                )
            }
        }

        /**
         * 分享朋友圈
         * @param url 分享的链接
         * @param title 分享的标题
         * @param description 分享的内容
         * @param imageUrl 分享图片地址
         */
        @JavascriptInterface
        fun shareToWxCircleOfFriends(url: String, title: String, description: String, imageUrl: String) {
            downLoadBitmap(this@WebActivity, imageUrl) { bitmap ->
                ShareUtil.shareUrl(
                        this@WebActivity,
                        url,
                        title,
                        description,
                        bitmap,
                        2
                )
            }

        }

        /**
         * 分享QQ
         * @param url 分享的链接
         * @param title 分享的标题
         * @param content 分享的内容
         * @param imageUrl 分享图片地址
         */
        @JavascriptInterface
        fun shareToQQ(url: String, title: String, content: String, imageUrl: String) {
            ShareUtil.shareToQQ(this@WebActivity, url, title, content, imageUrl)
        }

        @JavascriptInterface
        fun getSomeThing() {
            ShareUtil.shareUrl(
                    this@WebActivity,
                    "http://fangapp.8raw.com/#/",
                    "测试标题",
                    "测试描述",
                    BitmapFactory.decodeResource(getResources(), R.mipmap.logo),
                    1
            )
        }
    }

}
