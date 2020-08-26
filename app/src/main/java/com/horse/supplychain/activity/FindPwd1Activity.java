package com.horse.supplychain.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.Request;
import com.android.volley.manager.RequestManager;
import com.horse.supplychain.R;
import com.horse.supplychain.util.ConfigUtil;
import com.horse.supplychain.util.LogUtil;
import com.horse.supplychain.util.StringUtils;
import com.horse.supplychain.util.ToastUtil;

import java.util.HashMap;
import java.util.Map;

public class FindPwd1Activity extends AppCompatActivity {
    private static final String TAG = "FindPwd1Activity";

    private EditText etPhone;
    private EditText etVerCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findpwd1);

        etPhone =  findViewById(R.id.et_phone);
        etVerCode = findViewById(R.id.et_smsVerCode);

        TextView tvTitle = findViewById(R.id.app_title_text);
        tvTitle.setText("找回密码");

        ImageView ivBackBtn =  findViewById(R.id.app_left_view);
        ivBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void onClickForFindPwdNext(View view) {
        LogUtil.d(TAG, "onClickForFindPwdNext");

        String sInputPhone = etPhone.getText().toString();
        String sInputVerCode = etVerCode.getText().toString();

        if(sInputPhone == null || sInputPhone.isEmpty())
        {
            ToastUtil.showToast(this, "请输入手机号码！");
            return ;
        }

        if(sInputVerCode == null || sInputVerCode.isEmpty())
        {
            ToastUtil.showToast(this, "请输入验证码！");
            return ;
        }

        String sRequestUrl = ConfigUtil.serverUrl + "/api/login/selectUserInfoByPhoneNo";
        String sJson = "{\"phoneNo\":\"" + sInputPhone + "\",\"identifycode\":\"" + sInputVerCode + "\"}";

        LogUtil.d(TAG,"sRequestUrl: " + sRequestUrl);
        LogUtil.d(TAG,"sJson: " + sJson);

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type","application/json");

        JSONObject result = null;
        if(sJson != null &&sJson.length() >0){
            result = JSON.parseObject(sJson);
            LogUtil.e("result: ", JSONObject.toJSONString(result));
        }else{
            LogUtil.e("result: ", "传入串未空");
        }

        String requestData = "";
        if(result != null)
        {
            requestData = JSONObject.toJSONString(result);
        }

        RequestManager.getInstance().request(Request.Method.POST,sRequestUrl,requestData, headers, queryUserInfoByPhoneRequestListener,false,0,0,0);
    }

    public void onClickForSendVerCode(View view) {
        String sRegisterPhone = etPhone.getText().toString();

        if(sRegisterPhone == null || sRegisterPhone.isEmpty())
        {
            ToastUtil.showToast(this, "请输入手机号码！");
            return ;
        }

        String sRequestUrl = ConfigUtil.serverUrl + "/api/login/selectSmsForUsrRgtChgPwd";
        String sJson = "{\"phoneNo\":\""+ sRegisterPhone +"\",\"type\":\"PWD_CHANGE\"}";

        LogUtil.d(TAG,"sRequestUrl: " + sRequestUrl);
        LogUtil.d(TAG,"sJson: " + sJson);

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type","application/json");

        JSONObject result = null;
        if(sJson != null &&sJson.length() >0){
            result = JSON.parseObject(sJson);
            LogUtil.e("result: ", JSONObject.toJSONString(result));
        }else{
            LogUtil.e("result: ", "传入串未空");
        }

        String requestData = "";
        if(result != null)
        {
            requestData = JSONObject.toJSONString(result);
        }

        RequestManager.getInstance().request(Request.Method.POST,sRequestUrl,requestData, headers, verCodeRequestListener,false,0,0,0);
    }

    //短信验证码网络请求
    RequestManager.RequestListener verCodeRequestListener = new RequestManager.RequestListener() {
        @Override
        public void onRequest() {
            LogUtil.e("onRequest","onRequest");
        }

        @Override
        public void onSuccess(String response, Map<String, String> headers, String url, int actionId) {
            LogUtil.e("onSuccess", response);

            ToastUtil.showToast(FindPwd1Activity.this, "验证码请求成功！");
        }

        @Override
        public void onError(String message, String url, int actionId) {
            LogUtil.e("onError",message);
            ToastUtil.showToast(FindPwd1Activity.this, "验证码请求失败： "+ message);
        }
    };

    //使用当前手机号查询用户信息列表网络请求
    RequestManager.RequestListener queryUserInfoByPhoneRequestListener = new RequestManager.RequestListener() {
        @Override
        public void onRequest() {
            LogUtil.e("onRequest","onRequest");
        }

        @Override
        public void onSuccess(String response, Map<String, String> headers, String url, int actionId) {
            LogUtil.e("onSuccess", response);

            org.json.JSONObject result = StringUtils.stringToJSONObject(response);

            String returnCode = result.optString("returnCode");
            String returnMsg = result.optString("returnMsg");

            if (returnCode != null && returnCode.equals("000000")) {
                String data = result.optString("data");
                LogUtil.d(TAG, "data:" + data);

                //{"returnCode":"000000","returnMsg":null,"data":{"RETURN_DATA":[{"loginName":"15902755770","userId":"17101408060440700001"},{"loginName":"dgt","userId":"19102813383179901"}]},"action_RETURN_CODE":null}
                if (data != null && !data.isEmpty()) {
                   JSONObject dataObj = JSON.parseObject(data);

                    JSONArray returnData = dataObj.getJSONArray("RETURN_DATA");

                    if (dataObj != null && returnData != null) {

                        for(int i=0;i<returnData.size();i++) {
                            JSONObject userInfoObj = returnData.getJSONObject(i);

                            String userId = userInfoObj.getString("userId");
                            String loginName = userInfoObj.getString("loginName");

                            LogUtil.d(TAG, "userId:" + userId + ", loginName: " + loginName);
                        }

                        //找回密码第二步
                        Intent intent = new Intent();
                        intent.putExtra("userInfoJsonList", data);
                        intent.setClass(FindPwd1Activity.this, FindPwd2Activity.class);
                        FindPwd1Activity.this.startActivity(intent);
                    }
                }
            }else {
                ToastUtil.showToast(FindPwd1Activity.this, returnCode + "： " + returnMsg);
            }
        }

        @Override
        public void onError(String message, String url, int actionId) {
            LogUtil.e("onError",message);
            ToastUtil.showToast(FindPwd1Activity.this, "查询用户信息失败： "+ message);
        }
    };
}