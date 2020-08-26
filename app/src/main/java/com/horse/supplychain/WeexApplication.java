package com.horse.supplychain;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.StrictMode;

import com.android.volley.manager.RequestManager;
import com.horse.supplychain.common.GifImage;
import com.horse.supplychain.common.ImageAdapter;
import com.horse.supplychain.common.RequestModule;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import org.apache.weex.InitConfig;
import org.apache.weex.WXEnvironment;
import org.apache.weex.WXSDKEngine;
import org.apache.weex.appfram.storage.WXStorageModule;
import org.apache.weex.common.WXException;

public class WeexApplication extends Application {
    private static final String TAG = "WeexApplication";

    //当前登录用户
    private WXStorageModule storageModule = new WXStorageModule();

    public WXStorageModule getLoginUser(){
        return storageModule;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
        {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            //builder.detectFileUriExposure();
        }

        RequestManager.getInstance().init(this);

        InitConfig config = new InitConfig.Builder().setImgAdapter(new ImageAdapter(getApplicationContext())).build();
        /**
         * 底层的初始化是异步执行的，尤其是初始化JS引擎部分的代码（WXBridgeManager），是相当耗时的
         * 因此，在调用完初始化之后，Activity第一次调用的时候，一定要增加是否已经初始化完成的判断
         * 如果没有完成初始化，适当的增加延迟等待的代码
         **/
        WXSDKEngine.initialize(this, config);

        initImageLoader(getApplicationContext());

        try {
            WXSDKEngine.registerModule("PayWebBase", RequestModule.class);
            //WXSDKEngine.registerModule("picker", WXPickersModule.class);
            WXSDKEngine.registerComponent("gifimage", GifImage.class, false);

            WXEnvironment.setOpenDebugLog(true);
        } catch (WXException e) {
            e.printStackTrace();
        }
    }

    private void initImageLoader(Context applicationContext) {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(applicationContext)
                .threadPriority(Thread.NORM_PRIORITY-2)
                .denyCacheImageMultipleSizesInMemory()
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .writeDebugLogs()
                .build();

        ImageLoader.getInstance().init(config);
    }

}
