package com.itboye.gehuajinfu.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;

/**
 * @Author:Create by Mr.w
 * @Date:2018/12/8 14:17
 * @Description:
 */
public class DownloadTask extends AsyncTask<String, Object, Bitmap> {
    private final String icon_url;
    private final DownLoadCallback downLoadCallback;
    Activity activity;
    private Bitmap bitmap;

    public DownloadTask(Activity activity, String icon_url, DownLoadCallback downLoadCallback) {
        this.activity = activity;
        this.icon_url = icon_url;
        this.downLoadCallback = downLoadCallback;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Bitmap doInBackground(String... strings) {
        bitmap = ImageUtil.getMyBitmap(activity, icon_url);
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        if (downLoadCallback != null) {
            downLoadCallback.downloadFinish(bitmap);
        }
//        activity.this.bitmap = bitmap;
        //将总共下载的字节数作为结果返回

//        WxShareUtil.shareUrl(ProductCenter_message_video_kefu_Activity.this, url, title, "", bitmap, sharePosition);


    }

    public interface DownLoadCallback {
        void downloadFinish(Bitmap bitmap);
    }
}
