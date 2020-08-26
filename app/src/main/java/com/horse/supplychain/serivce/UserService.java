package com.horse.supplychain.serivce;

import android.app.Activity;
import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.android.volley.manager.LoadController;
import com.android.volley.manager.RequestManager;
import com.horse.supplychain.util.DataToUtils;
import com.horse.supplychain.util.HttpUtil;
import com.horse.supplychain.util.LogUtil;
import com.horse.supplychain.util.MD5Util;
import com.horse.supplychain.util.ToastUtil;

import java.util.HashMap;
import java.util.Map;

public class UserService {
    private static String TAG="UserService";

    private Context context;

    public UserService(Context context)
    {
        this.context = context;
    }


    public boolean verifyUserInfo(String sUrl, String sUsername, String sPassword)  {
        //String sUrl = "http://192.168.50.168:8781/api/login/loginCheck";

        //String sUrl = configUtil.loginUrl;

        LogUtil.i(TAG, sUrl);

        //密码MD5加密
        sPassword = MD5Util.encryptMD5(sPassword);

        String sJson = "{'loginName':'" + sUsername +"', 'loginPassword':'" + sPassword + "'}";

        String loginResult = HttpUtil.post(sUrl,sJson);
        LogUtil.d(TAG, loginResult);

        if(loginResult == null || loginResult.isEmpty())
        {
            ToastUtil.showToast((Activity) context, "网络异常！");
            return  false;
        }

        Map<String,Object> objectMap = JSON.parseObject(loginResult,Map.class);
        String returnCode = (String)objectMap.get("returnCode");
        String returnMsg = (String)objectMap.get("returnMsg");

        if(returnCode.equals("000000"))
        {
            return true;
        }
        else
        {
            ToastUtil.showToast((Activity) context, returnMsg);
            //Toast.makeText(this, returnMsg, Toast.LENGTH_SHORT).show();
        }

        return false;
    }

    public LoadController verifyUserInfo(String sUrl, String sUsername, String sPassword, RequestManager.RequestListener requestListener) {
        sPassword = MD5Util.encryptMD5(sPassword);

        final Map<String, Object> info = new HashMap<String, Object>();
        info.put("loginName",sUsername);
        info.put("loginPassword", sPassword);
        LogUtil.d(TAG,"sUrl: " + sUrl);
        String params = DataToUtils.mapToJSONStr(info);
        LogUtil.d(TAG, "params:" + params);
        return RequestManager.getInstance().post(sUrl, params, requestListener, 0);
    }

}
