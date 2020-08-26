package com.horse.supplychain.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Environment;
import android.util.Log;
import android.view.View;

import com.horse.supplychain.R;
import com.horse.supplychain.common.RequestModule;
import com.horse.supplychain.util.LogUtil;
import com.horse.supplychain.util.PermisionUtils;
import com.horse.supplychain.util.ToastUtil;
import com.yzq.zxinglibrary.common.Constant;

import org.apache.weex.IWXRenderListener;
import org.apache.weex.WXSDKEngine;
import org.apache.weex.WXSDKInstance;
import org.apache.weex.common.WXRenderStrategy;
import org.apache.weex.utils.WXFileUtils;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements IWXRenderListener {
    private static final String TAG = "MainActivity";

    private WXSDKInstance wXSDKInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final View view = findViewById(R.id.activity_main);

        //获取权限
        new PermisionUtils().getPermission(this);

        //初始化weex
        wXSDKInstance = new WXSDKInstance(this);
        wXSDKInstance.registerRenderListener(this);
        asyncLoadPages(view);
    }

    private void asyncLoadPages(final View view) {
        /**
         * 此处一定要判断WXSDKEngine是否已经成功初始化了，由于WXSDKEngine底层初始化的库非常多
         * 导致整个的初始化非常的耗时，并且这个初始化是异步执行的，尤其是初始化JS引擎部分的代码（WXBridgeManager）。
         * 因此有非常大的概率导致当第一次使用Week的API的时候，底层还没有完成初始化
         * 导致出现错信息 "degradeToH5|createInstance fail|wx_create_instance_error isJSFrameworkInit==false reInitCount == 1"
         * 这段耗时可以通过在程序启动的时候增加启动等待页面来人性化的忽略这部分耗时。
         **/
        if(!WXSDKEngine.isInitialized()) {
            view.postDelayed(new Runnable() {
                @Override
                public void run() {
                    asyncLoadPages(view);
                }
            }, 10);
        } else {
            loadPages();
        }
    }

    private void loadPages() {
        String pageName = "index";
        String sSDCardPath = Environment.getExternalStorageDirectory().getPath();

        LogUtil.d(TAG, "sSDCardPath: " + sSDCardPath);
        sSDCardPath +="/horse_weex/";

        String sResPkgSaveDir = sSDCardPath;
        String bundleUrl = sResPkgSaveDir + "dist/native/index.js";

        LogUtil.d(TAG, "bundleUrl: " + bundleUrl);

        if (getIntent().getData() != null) {
            String navUrl = getIntent().getData().toString();
            LogUtil.e("navUrl",navUrl);
            if (null != navUrl) {
                LogUtil.e(TAG,navUrl);
                bundleUrl = navUrl;
            } else {
                LogUtil.e(TAG,"a is null");
            }
        } else {
            LogUtil.e(TAG,"get data is null");
        }

        if(bundleUrl.startsWith("file://")){
            bundleUrl = bundleUrl.replace("file://", "");
            LogUtil.e(TAG,"bundleUrl: " + bundleUrl);
        }

        Map<String,Object> options = new HashMap<>();
        options.put(WXSDKInstance.BUNDLE_URL, sSDCardPath);
        wXSDKInstance.render(pageName, WXFileUtils.loadFileOrAsset(bundleUrl,this),options,null,WXRenderStrategy.APPEND_ASYNC);
    }

    @Override
    public void onViewCreated(WXSDKInstance instance, View view) {
        setContentView(view);
    }

    @Override
    public void onRenderSuccess(WXSDKInstance instance, int width, int height) {
        Log.i(TAG, "onRenderSuccess");
    }

    @Override
    public void onRefreshSuccess(WXSDKInstance instance, int width, int height) {
        Log.i(TAG, "onRefreshSuccess");
    }

    @Override
    public void onException(WXSDKInstance instance, String errCode, String msg) {
        Log.e(TAG, "onException：" + msg);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(wXSDKInstance!=null){
            wXSDKInstance.onActivityResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(wXSDKInstance!=null){
            wXSDKInstance.onActivityPause();
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        if(wXSDKInstance!=null){
            wXSDKInstance.onActivityStop();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(wXSDKInstance!=null){
            wXSDKInstance.onActivityDestroy();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtil.e(TAG, "requestCode = " + requestCode); // 11
        LogUtil.e(TAG, "resultCode = " + resultCode); // 10000
        LogUtil.e(TAG, "Intent = " + data);

        //处理二维码扫描结果
        if (requestCode == RequestModule.CODE_SCANNER_START) {
            //处理扫描结果（在界面上显示）
            if (null != data) {
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    return;
                }

                String result = bundle.getString(Constant.CODED_CONTENT);
                //ToastUtil.showToast(this, "解析结果:" + result);
                RequestModule.callback.invoke(result);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PermisionUtils.REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission is granted
                } else {
                    ToastUtil.showToast(this, "Until you grant the permission, we cannot display the names");
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
