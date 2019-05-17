package com.itboye.gehuajinfu;

import android.app.Application;
import com.itboye.gehuajinfu.util.Const;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.tauth.Tencent;

import static com.itboye.gehuajinfu.util.Const.APP_ID;
import static com.itboye.gehuajinfu.util.Const.TENCENT_APP_ID;

public class App extends Application {

    static App instance;
    private IWXAPI iwxapi;

    public Tencent getmTencent() {
        return mTencent;
    }

    private Tencent mTencent;

    public static App getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        regToWx();
        initTencent();
    }

    private void regToWx() {
        iwxapi = WXAPIFactory.createWXAPI(getApplicationContext(), APP_ID, false);
        iwxapi.registerApp(APP_ID);
    }


    private void initTencent() {
        mTencent = Tencent.createInstance(TENCENT_APP_ID, this);
    }

    public IWXAPI getIwxapi() {
        return iwxapi;
    }
}
