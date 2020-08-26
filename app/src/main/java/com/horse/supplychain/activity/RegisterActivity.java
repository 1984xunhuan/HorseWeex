package com.horse.supplychain.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;
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

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";

    private  EditText etPhone;
    private EditText etVerCode;
    private EditText etNewPwd;
    private EditText etReptNewPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etPhone = findViewById(R.id.et_phone);
        etVerCode = findViewById(R.id.et_smsVerCode);
        etNewPwd = findViewById(R.id.et_newPwd);
        etReptNewPwd = findViewById(R.id.et_reptNewPwd);

        TextView tvTitle = findViewById(R.id.app_title_text);
        tvTitle.setText("用户注册");

        ImageView ivBackBtn =  findViewById(R.id.app_left_view);
        ivBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               finish();
            }
        });
    }

    public void onClickForSendVerCode(View view) {
        String sRegisterPhone = etPhone.getText().toString();

        if(sRegisterPhone == null || sRegisterPhone.isEmpty())
        {
            ToastUtil.showToast(this, "请输入手机号码！");
            return ;
        }

        String sRequestUrl = ConfigUtil.serverUrl + "/api/login/selectSmsForUsrRgtChgPwd";
        String sJson = "{\"phoneNo\":\""+ sRegisterPhone +"\",\"type\":\"USER_REGISTER\"}";

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



    public void onClickForRegisterUser(View view) {
        LogUtil.d(TAG,"onClickForRegisterUser ");

        String sRegisterPhone = etPhone.getText().toString();
        if(sRegisterPhone == null || sRegisterPhone.isEmpty())
        {
            ToastUtil.showToast(this, "请输入手机号码！");
            return ;
        }

        String sVerCode = etVerCode.getText().toString();
        if(sVerCode == null || sVerCode.isEmpty())
        {
            ToastUtil.showToast(this, "请输入验证码！");
            return ;
        }

        String sNewPwd = etNewPwd.getText().toString();
        if(sNewPwd == null || sNewPwd.isEmpty())
        {
            ToastUtil.showToast(this, "请输入新密码！");
            return ;
        }

        String sReptNewPwd = etReptNewPwd.getText().toString();
        if(sReptNewPwd == null || sReptNewPwd.isEmpty())
        {
            ToastUtil.showToast(this, "请再次输入新密码！");
            return ;
        }

        if(!sNewPwd.equalsIgnoreCase(sReptNewPwd)){
            ToastUtil.showToast(this, "两次输入的密码不一致！");
            return ;
        }

        String sRequestUrl = ConfigUtil.serverUrl + "/api/login/saveSignup";
        String sJson = "{\"phoneNo\":\""+ sRegisterPhone +"\",\"loginPwd\":\""+sNewPwd+"\",\"identifycode\":\""+sVerCode+"\"}";

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

        RequestManager.getInstance().request(Request.Method.POST,sRequestUrl,requestData, headers, registerUserRequestListener,false,0,0,0);
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

            ToastUtil.showToast(RegisterActivity.this, "验证码请求成功！");
        }

        @Override
        public void onError(String message, String url, int actionId) {
            LogUtil.e("onError",message);
            ToastUtil.showToast(RegisterActivity.this, "验证码请求失败： "+ message);
        }
    };

    //用户注册网络请求
    RequestManager.RequestListener registerUserRequestListener = new RequestManager.RequestListener() {
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

            if (returnCode !=null && returnCode.equals("000000")) {
                ToastUtil.showToast(RegisterActivity.this, "用户注册成功！");

                //返回登录界面
                Intent intent = new Intent();
                intent.setClass(RegisterActivity.this, LoginActivity.class);
                RegisterActivity.this.startActivity(intent);
            }
            else
            {
                ToastUtil.showToast(RegisterActivity.this, returnCode + "： "+ returnMsg);
            }
        }

        @Override
        public void onError(String message, String url, int actionId) {
            LogUtil.e("onError",message);
            ToastUtil.showToast(RegisterActivity.this, "用户注册失败： "+ message);
        }
    };
}