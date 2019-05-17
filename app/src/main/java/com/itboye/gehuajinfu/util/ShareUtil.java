package com.itboye.gehuajinfu.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.itboye.gehuajinfu.App;
import com.itboye.gehuajinfu.MainActivity;
import com.tencent.connect.share.QQShare;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;

import java.io.ByteArrayOutputStream;

/**
 * ShareUtil
 * <p>
 * Created by Mr.w on 2018/3/21.
 * <p>
 * 版本      ${version}
 * <p>
 * 修改时间
 * <p>
 * 修改内容
 */


public class ShareUtil {
    private static final int THUMB_SIZE = 150;
    static String TAG = "share_tag";

    public static void shareUrl(Activity activity, String url, String title, String description, Bitmap bitmap, int sharePosition) {
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = url;

        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = title;
        msg.description = description;

//        Bitmap thumb = BitmapFactory.decodeResource(MyApplication.getInstance().getResources(), R.drawable.logo);
        Bitmap thumbBmp = Bitmap.createScaledBitmap(bitmap, THUMB_SIZE, THUMB_SIZE, true);
        msg.thumbData = bmpToByteArrayNew(thumbBmp, false);
        thumbBmp.recycle();
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.message = msg;
        req.transaction = "share";
        if (sharePosition == 1) {
            req.scene = SendMessageToWX.Req.WXSceneSession;//微信好友
        } else if (sharePosition == 2) {
            req.scene = SendMessageToWX.Req.WXSceneTimeline;//朋友圈
        }
        App.getInstance().getIwxapi().sendReq(req);
        Log.v(TAG, "分享地址：" + url);
    }

    public static void shareUrl(Activity activity, String url, String title, String description, String transaction, Bitmap bitmap, int sharePosition) {
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = url;

        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = title;
        msg.description = description;

//        Bitmap thumb = BitmapFactory.decodeResource(MyApplication.getInstance().getResources(), R.drawable.lingshou_logo);
        Bitmap thumbBmp = Bitmap.createScaledBitmap(bitmap, THUMB_SIZE, THUMB_SIZE, true);
        msg.thumbData = bmpToByteArrayNew(thumbBmp, false);
        thumbBmp.recycle();
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.message = msg;
        req.transaction = transaction;
        if (sharePosition == 1) {
            req.scene = SendMessageToWX.Req.WXSceneSession;//微信好友
        } else if (sharePosition == 2) {
            req.scene = SendMessageToWX.Req.WXSceneTimeline;//朋友圈
        }
        App.getInstance().getIwxapi().sendReq(req);
        Log.v(TAG, "分享地址：" + url);
    }

    public static void shareToQQ(Activity activity, String url, String title, String content, String imageUrl) {
        final Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_APP);
        params.putString(QQShare.SHARE_TO_QQ_TITLE, title);
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, content);
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, url);
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, imageUrl);
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "革华金服");
//        params.putInt(QQShare.SHARE_TO_QQ_EXT_INT,  "其他附加功能");
        App.getInstance().getmTencent().shareToQQ(activity, params, new UiListener());
    }

    static class UiListener implements IUiListener {

        @Override
        public void onComplete(Object o) {
            Toast.makeText(App.getInstance(), "分享成功", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(UiError uiError) {
            Toast.makeText(App.getInstance(), "分享失败" + uiError.errorMessage, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel() {
            Toast.makeText(App.getInstance(), "分享取消", Toast.LENGTH_SHORT).show();

        }
    }

    private static String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    public static byte[] bmpToByteArrayNew(final Bitmap bmp, final boolean needRecycle) {
        int i;
        int j;
        if (bmp.getHeight() > bmp.getWidth()) {
            i = bmp.getWidth();
            j = bmp.getWidth();
        } else {
            i = bmp.getHeight();
            j = bmp.getHeight();
        }
        Bitmap localBitmap = Bitmap.createBitmap(i, j, Bitmap.Config.RGB_565);
        Canvas localCanvas = new Canvas(localBitmap);

        while (true) {
            localCanvas.drawBitmap(bmp, new Rect(0, 0, i, j), new Rect(0, 0, i, j), null);
            if (needRecycle)
                bmp.recycle();
            ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
            localBitmap.compress(Bitmap.CompressFormat.JPEG, 100,
                    localByteArrayOutputStream);
            localBitmap.recycle();
            byte[] arrayOfByte = localByteArrayOutputStream.toByteArray();
            try {
                localByteArrayOutputStream.close();
                return arrayOfByte;
            } catch (Exception e) {
                //F.out(e);
            }
            i = bmp.getHeight();
            j = bmp.getHeight();
        }
    }
}
