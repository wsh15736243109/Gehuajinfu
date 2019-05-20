package com.itboye.gehuajinfu.util;

import android.app.Activity;
import android.graphics.Bitmap;
import com.bumptech.glide.Glide;

import java.util.concurrent.ExecutionException;


/**
 * 图片加载类
 */
public class XGlideLoader {

    public static Bitmap getUrlBitmap(Activity context, String url) {
        try {
            return Glide.with(context)
                    .load(url)
                    .asBitmap() //必须
                    .centerCrop()
                    .into(500, 500)
                    .get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }
}
