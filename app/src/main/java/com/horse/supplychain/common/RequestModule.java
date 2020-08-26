package com.horse.supplychain.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSON;
import com.android.volley.Request;
import com.google.zxing.WriterException;
import com.horse.supplychain.util.AppUtils;
import com.horse.supplychain.activity.LoginActivity;
import com.horse.supplychain.util.ConfigUtil;
import com.horse.supplychain.util.LogUtil;
import org.apache.weex.annotation.JSMethod;
import org.apache.weex.bridge.JSCallback;
import org.apache.weex.common.WXModule;
import com.android.volley.manager.RequestManager;
import com.horse.supplychain.util.QRCodeUtil;
import com.horse.supplychain.util.SharedPreferencesUtil;
import com.horse.supplychain.util.ToastUtil;
import com.yzq.zxinglibrary.android.CaptureActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * weex调用native的方法
 *
 * */
public class RequestModule extends WXModule {
    private static final String TAG = "RequestModule";

    public static final int CODE_SCANNER_START=10001;

    public static JSCallback callback;

    @JSMethod
    public void sendRequest(String url, String jsonStr, String requestType, String showType, final JSCallback callback) {
        LogUtil.e(TAG, "sendRequest======>url: "+ url);
        LogUtil.e(TAG,"sendRequest======>jsonStr: "+jsonStr);
        LogUtil.e(TAG,"sendRequest======>ConfigUtil.serverUrl: " + ConfigUtil.serverUrl);

        JSONObject result = null;
        if(jsonStr != null &&jsonStr.length() >0){
            result = JSON.parseObject(jsonStr);
            LogUtil.e("result: ", JSONObject.toJSONString(result));
        }else{
            LogUtil.e("result: ", "传入串未空");
        }

        RequestManager.RequestListener requestListener = new RequestManager.RequestListener() {
            @Override
            public void onRequest() {
                LogUtil.e("onRequest","onRequest");
            }

            @Override
            public void onSuccess(String response, Map<String, String> headers, String url, int actionId) {
                LogUtil.e("onSuccess", response);
                callback.invoke(response);
            }

            @Override
            public void onError(String message, String url, int actionId) {
                LogUtil.e("onError",message);
            }
        };

        String sRequestUrl = ConfigUtil.serverUrl + url;
        LogUtil.d(TAG,"请求开始..sRequestUrl: " + sRequestUrl);

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type","application/json");

        String requestData = "";
        if(result != null)
        {
            requestData =JSONObject.toJSONString(result);
        }

        RequestManager.getInstance().request(Request.Method.POST,sRequestUrl,requestData, headers, requestListener,false,0,0,0);
    }

    @JSMethod
    public void reLogin(String url, String jsonStr, final JSCallback callback) {
        LogUtil.e(TAG, "reLogin======>url: "+ url);
        LogUtil.e(TAG,"reLogin======>jsonStr: "+jsonStr);
        LogUtil.e(TAG,"退出登录");

        Intent intent = new Intent(mWXSDKInstance.getContext(), LoginActivity.class);
        mWXSDKInstance.getContext().startActivity(intent);
    }

    @JSMethod
    public void getVersion(final JSCallback callback) {
        LogUtil.e(TAG,"getVersion()	 没有参数");

        JSONObject jsonObject = new JSONObject();

        String appVersionName = AppUtils.getVersionName(mWXSDKInstance.getContext());
        int appVersionCode = AppUtils.getVersionCode(mWXSDKInstance.getContext());
        String sCurResourcePkgVersionNo = SharedPreferencesUtil.getString(mWXSDKInstance.getContext(),"curResourcePkgVersionNo", "0");

        jsonObject.put("appId", ConfigUtil.appId);
        jsonObject.put("appName", ConfigUtil.appName);
        jsonObject.put("appVersionName", appVersionName);
        jsonObject.put("appVersionCode", appVersionCode);
        jsonObject.put("resourceVersion", sCurResourcePkgVersionNo);

        callback.invoke(jsonObject.toJSONString());
    }

    @JSMethod
    public void saveAsQRImage(String jsonStr,final JSCallback callback) {
        LogUtil.e(TAG,"saveAsQRImage=====>jsonStr: "+jsonStr);

        if(jsonStr.isEmpty()){
            ToastUtil.showToast((Activity) mWXSDKInstance.getContext(), "数据为空");
            return ;
        }

        try {
            Bitmap bitmap = QRCodeUtil.createQRCode(jsonStr,320);

            saveImageToGallery(mWXSDKInstance.getContext(), bitmap, callback);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    @JSMethod
    public void codeScannerSC(final JSCallback callback) {
        LogUtil.e("codeScannerSC", "调用摄像头");

        RequestModule.callback = callback;

        Intent intent = new Intent(mWXSDKInstance.getContext(), CaptureActivity.class);
        ((Activity)mWXSDKInstance.getContext()).startActivityForResult(intent,CODE_SCANNER_START);
    }

    @JSMethod
    public void clearDeviceConnect(){
        LogUtil.e(TAG, "clearDeviceConnect");
    }

    @JSMethod
    public void industryDidSelected(String jsonStr,final JSCallback callback) {
        LogUtil.e(TAG,"jsonStr: "+jsonStr);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtil.e("requestCode", String.valueOf(requestCode));
        LogUtil.e("resultCode",String.valueOf(resultCode));
    }

    public void saveImageToGallery(Context context, Bitmap bmp, JSCallback callback) {
        // 首先保存图片
        File appDir = new File(Environment.getExternalStorageDirectory(), "A");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            callback.invoke("1");
        } catch (IOException e) {
            e.printStackTrace();
            callback.invoke("1");
        }

        // 其次把文件插入到系统图库
        String path = null;
        try {
            path = MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    file.getAbsolutePath(), fileName, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            callback.invoke("1");
        }
        // 最后通知图库更新
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + path)));
        callback.invoke("0");
        // showToast("已保存到系统相册");
    }
}
