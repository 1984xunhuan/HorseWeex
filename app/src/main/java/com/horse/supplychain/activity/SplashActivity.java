package com.horse.supplychain.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.alibaba.fastjson.JSON;
import com.android.volley.manager.RequestManager;
import com.horse.supplychain.R;
import com.horse.supplychain.serivce.APKService;
import com.horse.supplychain.util.ConfigUtil;
import com.horse.supplychain.util.LogUtil;
import com.horse.supplychain.util.ToastUtil;
import com.horse.supplychain.util.AppUtils;
import java.util.Map;

public class SplashActivity extends AppCompatActivity {
    private static final String TAG = "SplashActivity";
    private ConfigUtil configUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        initTask();
    }

    //初始化
    private void initTask()  {
        //1、加载配置文件
        configUtil = new ConfigUtil(this);

        //2、检查APK是否需要更新
        int curAPKVersionNo = AppUtils.getVersionCode(SplashActivity.this);
        String sCheckAPKUrl = configUtil.apkCheckUrl + curAPKVersionNo;

        APKService checkAPKService = new APKService(SplashActivity.this,sCheckAPKUrl);
        checkAPKService.checkUpdateAPK(checkAPKRequestListener);
    }

    //定义检测APK监听
    RequestManager.RequestListener checkAPKRequestListener = new RequestManager.RequestListener() {
        @Override
        public void onRequest() {
        }

        @Override
        public void onSuccess(String response, Map<String, String> headers, String url, int actionId) {
            try {
                LogUtil.d(TAG,"response: " + response);

                Map<String,Object> objectMap = JSON.parseObject(response,Map.class);

                if(objectMap == null || objectMap.size() == 0)
                {
                    ToastUtil.showToast(SplashActivity.this, "JSON解析异常！");
                    return ;
                }

                String returnCode = (String)objectMap.get("returnCode");
                String returnMsg = (String)objectMap.get("returnMsg");

                if(returnCode.equals("000000")) {
                    String sDownloadUrl = null;
                    String sUpdateDesc = null;
                    String sVerNo = null;

                    com.alibaba.fastjson.JSONObject dataJSONObject = (com.alibaba.fastjson.JSONObject) objectMap.get("data");

                    if (dataJSONObject != null && dataJSONObject.get("currVersion") != null) {
                        com.alibaba.fastjson.JSONObject currVersionJSONObject = (com.alibaba.fastjson.JSONObject) dataJSONObject.get("currVersion");

                        if (currVersionJSONObject.get("downloadUrl") != null) {
                            sDownloadUrl = (String) currVersionJSONObject.get("downloadUrl");
                            LogUtil.d(TAG, sDownloadUrl);

                            sUpdateDesc = (String) currVersionJSONObject.get("updateDesc");
                            LogUtil.d(TAG, sUpdateDesc);

                            sVerNo = currVersionJSONObject.get("verNo").toString();

                            LogUtil.d(TAG, sVerNo);
                        }
                    }

                    if (sDownloadUrl != null && !sDownloadUrl.isEmpty() && sDownloadUrl.startsWith("http")) {
                        String sJsonUpdateInfo = "{'url':'" + sDownloadUrl + "','versionCode':" + sVerNo + ",'updateMessage':'" + sUpdateDesc + "'}";
                        LogUtil.d(TAG,sJsonUpdateInfo);
                        //升级新版本apk
                        checkForDialog(SplashActivity.this, sDownloadUrl, sUpdateDesc);
                    }else{
                        //启动登录页面
                        startLoginActivity();
                    }
                }
                else
                {
                    ToastUtil.showToast(SplashActivity.this,returnMsg);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(String errorMsg, String url, int actionId) {
            ToastUtil.showToast(SplashActivity.this,errorMsg);

            SplashActivity.this.finish();
        }
    };

    private  void startLoginActivity()
    {
        //启动登录页面
        Intent intent = new Intent();
        intent.setClass(SplashActivity.this, LoginActivity.class);
        SplashActivity.this.startActivity(intent);
    }


    public void checkForDialog(final Context context, final String sDownloadUrl, final String sContent) {
        if (isContextValid(context)) {
            new AlertDialog.Builder(context)
                    .setTitle(R.string.android_auto_update_dialog_title)
                    .setMessage(sContent)
                    .setPositiveButton(R.string.android_auto_update_dialog_btn_download, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            APKService checkAPKService = new APKService(SplashActivity.this, sDownloadUrl);
                            checkAPKService.execute();
                        }
                    })
                    .setNegativeButton(R.string.android_auto_update_dialog_btn_cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //启动登录页面
                            startLoginActivity();
                        }
                    })
                    .setCancelable(false)
                    .show();
        }
    }

    private boolean isContextValid(Context context) {
        return context instanceof Activity && !((Activity) context).isFinishing();
    }
}
