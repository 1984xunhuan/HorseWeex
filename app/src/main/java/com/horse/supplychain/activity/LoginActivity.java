package com.horse.supplychain.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.manager.RequestManager;
import com.horse.supplychain.R;
import com.horse.supplychain.WeexApplication;
import com.horse.supplychain.serivce.ResourcePackageService;
import com.horse.supplychain.serivce.UserService;
import com.horse.supplychain.util.ConfigUtil;
import com.horse.supplychain.util.FileUtils;
import com.horse.supplychain.util.LogUtil;
import com.horse.supplychain.util.PermisionUtils;
import com.horse.supplychain.util.SharedPreferencesUtil;
import com.horse.supplychain.util.StringUtils;
import com.horse.supplychain.util.ToastUtil;
import org.json.JSONObject;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    EditText etUsername;
    EditText etPassword;
    Button btnLogin;
    ConfigUtil configUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername =  findViewById(R.id.username);
        etPassword =  findViewById(R.id.password);
        btnLogin = findViewById(R.id.submit_btn);

        new PermisionUtils().getPermission(this);

        initTask();
    }

    //初始化
    private void initTask()  {
        //1、加载配置文件
        configUtil = new ConfigUtil(this);

        //2、检查是否需要更新资源包
        LogUtil.d(TAG, "检查资源包是否需要更新");

        String sSDCardPath = Environment.getExternalStorageDirectory().getPath();
        sSDCardPath +="/horse_weex/dist/native/index.js";

        LogUtil.d(TAG, "sSDCardPath: " + sSDCardPath);

        String sCurResourcePkgVersionNo = "0";

        //判断本地是否有之前下载的资源包
        if(FileUtils.isExists(sSDCardPath))
        {
            //如果之前下载过，获取之前保存的资源包版本号
            sCurResourcePkgVersionNo = SharedPreferencesUtil.getString(LoginActivity.this,"curResourcePkgVersionNo", "0");
        }

        String sCheckResPkgUrl = configUtil.resourcePkgCheckUrl + sCurResourcePkgVersionNo;
        LogUtil.d(TAG, "sCheckResPkgUrl: " + sCheckResPkgUrl);

        ResourcePackageService checkResourcePackageService = new ResourcePackageService(LoginActivity.this, sCheckResPkgUrl);
        checkResourcePackageService.checkUpdateResourcePackage(checkResourcePkgRequestListener);
    }

    //处理登录点击事件
    public void onClickForLogin(View view) {
        //1、获取用户名和密码输入值，并初步判断
        String sUsername = etUsername.getText().toString();
        String sPassword = etPassword.getText().toString();

        if(sUsername == null || sUsername.isEmpty()||
                sPassword == null || sPassword.isEmpty())
        {
            ToastUtil.showToast(this, "用户名或密码为空！");
            return;
        }

        btnLogin.setEnabled(false);

        UserService userService = new UserService(this);
        String sCheckUserNameUrl = configUtil.loginUrl;
        userService.verifyUserInfo(sCheckUserNameUrl,sUsername, sPassword, loginRequestListener);
    }

    //处理忘记密码点击事件
    public void onClickForFindPwd(View view) {
        LogUtil.d(TAG, "onClickForFindPwd");

        Intent intent = new Intent();
        intent.setClass(this, FindPwd1Activity.class);
        this.startActivity(intent);
    }

    //处理注册点击事件
    public void onClickForRegister(View view) {
        LogUtil.d(TAG, "onClickForRegister");

        Intent intent = new Intent();
        intent.setClass(this, RegisterActivity.class);
        this.startActivity(intent);
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

    private void startMainActivity()
    {
        LogUtil.d(TAG, "startMainActivity");

        //启动weex主页面
        Intent intent = new Intent();
        intent.setClass(LoginActivity.this, MainActivity.class);
        LoginActivity.this.startActivity(intent);
    }

    //定义检测资源包监听
    RequestManager.RequestListener checkResourcePkgRequestListener = new RequestManager.RequestListener() {
        @Override
        public void onRequest() {
        }

        @Override
        public void onSuccess(String response, Map<String, String> headers, String url, int actionId) {
            try {
                LogUtil.d(TAG,"response: " + response);

                JSONObject result = StringUtils.stringToJSONObject(response);

                String returnCode = result.optString("returnCode");
                String returnMsg = result.optString("returnMsg");

                if (returnCode !=null && returnCode.equals("000000")) {
                    String data = result.optString("data");
                    LogUtil.d(TAG,"data:" + data);

                    if(data != null && !data.isEmpty()) {
                        JSONObject dataObj = StringUtils.stringToJSONObject(data);

                        String currVersionStr = dataObj.optString("currVersion");

                        if(dataObj != null && currVersionStr != null && !currVersionStr.isEmpty()) {

                            JSONObject currVersionJSONObject = StringUtils.stringToJSONObject(currVersionStr);

                            String sDownloadUrl = currVersionJSONObject.optString("downloadUrl");
                            LogUtil.d(TAG, sDownloadUrl);

                            String sVerNo = currVersionJSONObject.optString("verNo");

                            LogUtil.d(TAG, sVerNo);

                            if (sDownloadUrl != null && !sDownloadUrl.isEmpty() && sDownloadUrl.startsWith("http")) {
                                //下载资源包
                                LogUtil.d(TAG, "sDownloadUrl: " + sDownloadUrl);

                                String sSDCardPath = Environment.getExternalStorageDirectory().getPath();
                                LogUtil.d(TAG, "sSDCardPath: " + sSDCardPath);
                                sSDCardPath +="/horse_weex/";

                                FileUtils.mkFolder(LoginActivity.this,  "horse_weex");
                                String sResPkgSaveDir = sSDCardPath;

                                LogUtil.d(TAG, "sResPkgSaveDir: " + sResPkgSaveDir);

                                new ResourcePackageService(LoginActivity.this, sDownloadUrl, sResPkgSaveDir, sVerNo).execute();
                            }
                        }
                    }
                } else {
                    ToastUtil.showToast(LoginActivity.this,result.optString("returnMsg"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(String message, String url, int actionId) {
        }
    };


    //定义用户登录监听
    RequestManager.RequestListener loginRequestListener = new RequestManager.RequestListener() {
        @Override
        public void onRequest() {
        }

        @Override
        public void onSuccess(String response, Map<String, String> headers, String url, int actionId) {
            try {
                LogUtil.d(TAG,"response: " + response);

                JSONObject result = StringUtils.stringToJSONObject(response);

                String returnCode = result.optString("returnCode");
                String returnMsg = result.optString("returnMsg");

                if (returnCode !=null && returnCode.equals("000000")) {
                    String data = result.optString("data");

                    LogUtil.d(TAG,"returnMsg:" + returnMsg);
                    LogUtil.d(TAG,"data:" + data);
                    JSONObject datadata = StringUtils.stringToJSONObject(data);

                    ((WeexApplication) getApplication()).getLoginUser().setItem("__horse_login_data", data, null);
                    String industryInfo = datadata.optString("IndustryInfo");
                    String enterpriseInfo = datadata.optString("EnterpriseInfo");
                    JSONObject industryInfoString = StringUtils.stringToJSONObject(industryInfo);
                    JSONObject enterpriseInfoString = StringUtils.stringToJSONObject(enterpriseInfo);
                    String industryIdString = industryInfoString.optString("industryId");
                    String enterpriseIdString = enterpriseInfoString.optString("enterpriseId");
                    LogUtil.e("industryIdString", industryIdString);
                    LogUtil.e("enterpriseIdString", enterpriseIdString);
                    //  industryIdString=null;
                    ConfigUtil.industryIdString= industryIdString;
                    ConfigUtil.enterpriseIdString=enterpriseIdString;

                    if (industryIdString == null || industryIdString.isEmpty()) {
                        LogUtil.e(TAG,"未选择过行业");
                        LogUtil.e(TAG,"进行行业选择操作");
                    } else {
                        LogUtil.e(TAG,"选择过行业");
                        LogUtil.e(TAG,"根据nextpage的地址进行跳转,data域信息写入storage，让app端可以查询");
                    }

                    //启动weex主页面
                    startMainActivity();
                } else {
                    ToastUtil.showToast(LoginActivity.this,result.optString("returnMsg"));
                }
            } catch (Exception e) {
                //dismissDialog();
                e.printStackTrace();
            }

            btnLogin.setEnabled(true);
        }

        @Override
        public void onError(String message, String url, int actionId) {
            btnLogin.setEnabled(true);
        }
    };
}