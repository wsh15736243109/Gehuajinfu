package com.itboye.gehuajinfu

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.support.annotation.RequiresApi
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.util.Log
import android.view.View
import android.webkit.*
import android.widget.Toast
import com.itboye.gehuajinfu.util.Const
import com.itboye.gehuajinfu.util.ShareUtil
import com.itboye.gehuajinfu.util.ShareUtil.downLoadBitmap
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.File.separator
import com.itboye.gehuajinfu.util.ImageUtil.compressImage
import java.io.FileNotFoundException
import java.io.IOException


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
                Log.v(TAG, "onReceivedError-------------" + error.toString())
            }

            override fun shouldOverrideUrlLoading(view: WebView, innerUrl: String?): Boolean {
//                view.loadUrl("http://www.baidu.com")
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
                showSingleChoiceDialog()
            }

            override fun onShowFileChooser(webView: WebView, filePathCallback: ValueCallback<Array<Uri>>?, fileChooserParams: FileChooserParams?): Boolean {
                Log.v(TAG, "onShowFileChooser-------------" + Const.URL)
                mUploadCallbackAboveL = filePathCallback
                showSingleChoiceDialog()
                return true
            }


        }
        Log.v(TAG, "开始加载-------------" + Const.URL)
        webView.loadUrl(Const.URL)
//        webView.loadUrl("file:///android_asset/html/config_detail.html")
    }

    private val REQUEST_CODE: Int = 101
    private val REQUEST_PERMISSION_CODE: Int = 102

    private fun openCamera() {
        when (which) {
            0 -> {
                // 步骤一：创建存储照片的文件
                val path = filesDir.toString() + File.separator + "images" + File.separator
                val file = File(path, System.currentTimeMillis().toString() + "_head.jpg")
                if (!file.parentFile.exists()) {
                    var re = file.parentFile.mkdirs()
                    if (re) {

                    } else {
                        Toast.makeText(this@WebActivity, "文件创建失败", Toast.LENGTH_SHORT).show()
                    }
                }else{
                    Toast.makeText(this@WebActivity, "正在启动", Toast.LENGTH_SHORT).show()
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    //步骤二：Android 7.0及以上获取文件 Uri
                    imageUri = FileProvider.getUriForFile(this@WebActivity, packageName, file)
                } else {
                    //步骤三：获取文件Uri
                    imageUri = Uri.fromFile(file)
                }
                Log.v(TAG, "head 路径===" + imageUri?.path)
                //步骤四：调取系统拍照
                val intent = Intent("android.media.action.IMAGE_CAPTURE")
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                startActivityForResult(intent, REQUEST_CODE)
            }
            else -> {
                takePhoto()
            }
        }

    }

    //检查权限
    private fun checkPermission(): Boolean {
        //是否有权限
        val haveCameraPermission = ContextCompat.checkSelfPermission(this@WebActivity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED

        val haveWritePermission = ContextCompat.checkSelfPermission(this@WebActivity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

        return haveCameraPermission && haveWritePermission

    }

    // 请求所需权限
    @RequiresApi(api = Build.VERSION_CODES.M)
    private fun requestPermissions() {
        requestPermissions(arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_PERMISSION_CODE)
    }

    // 请求权限后会在这里回调
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_PERMISSION_CODE -> {
                var allowAllPermission = false
                for (i in grantResults.indices) {
                    if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {//被拒绝授权
                        allowAllPermission = false
                        break
                    }
                    allowAllPermission = true
                }
                if (allowAllPermission) {
                    //开始拍照或从相册选取照片
                    openCamera()
                } else {
                    Toast.makeText(this@WebActivity, "该功能需要授权方可使用", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    var which = 0
    private fun showSingleChoiceDialog() {
        var dialog: AlertDialog? = null
        var single_list = arrayOf("拍照", "相册")
        var builder = AlertDialog.Builder(this)
        builder.setTitle("")
        builder.setIcon(R.mipmap.ic_launcher)
        builder.setSingleChoiceItems(single_list, 0) { p0, which ->
            this@WebActivity.which = which
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkPermission()) {
                    openCamera()
                } else {
                    requestPermissions()
                }
            } else {
                openCamera()
            }
            dialog?.dismiss()
        }
        dialog = builder.create()
        dialog.show()
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
        if (requestCode == REQUEST_CODE || requestCode == 100) {
//            if (data != null) {
//                imageUri = data.data
//            }
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
                mUploadCallbackAboveL!!.onReceiveValue(arrayOf(imageUri!!))
            }
        } else {
            mUploadCallbackAboveL!!.onReceiveValue(null)
        }
        mUploadCallbackAboveL = null
    }

    // 该广播即使多发（即选取照片成功时也发送）也没有关系，只是唤醒系统刷新媒体文件
    private fun updatePhotos() {
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
    }

}
