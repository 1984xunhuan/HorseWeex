package com.horse.supplychain.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.Request;
import com.android.volley.manager.RequestManager;
import com.horse.supplychain.R;
import com.horse.supplychain.util.ConfigUtil;
import com.horse.supplychain.util.DensityUtil;
import com.horse.supplychain.util.LogUtil;
import com.horse.supplychain.util.StringUtils;
import com.horse.supplychain.util.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FindPwd2Activity extends AppCompatActivity {
    private static final String TAG = "FindPwd2Activity";

    private EditText etNewPwd;
    private EditText etReptNewPwd;
    private String checkedUserId;

    private List userInfoList = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findpwd2);

        etNewPwd = findViewById(R.id.et_newPwd);
        etReptNewPwd = findViewById(R.id.et_reptNewPwd);

        TextView tvTitle = findViewById(R.id.app_title_text);
        tvTitle.setText("找回密码");

        ImageView ivBackBtn =  findViewById(R.id.app_left_view);
        ivBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        initRadioGroupView();
    }

    public void onClickForModifyPwd(View view) {
        LogUtil.d(TAG, "onClickForModifyPwd");

        if(checkedUserId == null || checkedUserId.isEmpty())
        {
            ToastUtil.showToast(this, "请选择需要修改的用户！");
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

        String sRequestUrl = ConfigUtil.serverUrl + "/api/login/updatePwd";
        String sJson = "{\"userId\":\""+ checkedUserId +"\",\"loginPwd\":\""+sNewPwd+"\"}";

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

        RequestManager.getInstance().request(Request.Method.POST,sRequestUrl,requestData, headers, modifyPwdRequestListener,false,0,0,0);
    }

    public void initRadioGroupView(){
        RadioGroup radiogroup = findViewById(R.id.gadiogroup);

        addview(radiogroup);

        radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
             @Override
             public void onCheckedChanged(RadioGroup group, int checkedId) {
                 RadioButton tempButton = findViewById(checkedId); // 通过RadioGroup的findViewById方法，找到ID为checkedID的RadioButton
                 // 以下就可以对这个RadioButton进行处理了

                 checkedUserId = (String) userInfoList.get(checkedId);

                 LogUtil.d(TAG,"Checked UserID: " + checkedUserId);
             }
        });
    }

    //动态添加视图
    public void addview(RadioGroup radiogroup){

        String data = getIntent().getStringExtra("userInfoJsonList");

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

                    RadioButton  button=new RadioButton(this);
                    setRaidBtnAttribute(button,loginName,i);

                    radiogroup.addView(button);

                    userInfoList.add(i, userId);

                    LinearLayout.LayoutParams layoutParams= (LinearLayout.LayoutParams) button
                            .getLayoutParams();
                    layoutParams.setMargins(0, 0,  DensityUtil.dip2px(this,10), 0);//4个参数按顺序分别是左上右下
                    button.setLayoutParams(layoutParams);
                }
            }
        }
    }


    @SuppressLint("ResourceType")
    private void setRaidBtnAttribute(final RadioButton codeBtn, String btnContent, int id ){
        if( null == codeBtn ){
            return;
        }
        codeBtn.setBackgroundResource(R.drawable.radio_group_selector);
        codeBtn.setTextColor(this.getResources().getColorStateList(R.drawable.color_radiobutton));
        codeBtn.setButtonDrawable(new ColorDrawable(Color.TRANSPARENT));
        //codeBtn.setPadding(80, 0, 0, 0);           		// 设置文字距离按钮四周的距离

        codeBtn.setId( id );
        codeBtn.setText( btnContent);

        codeBtn.setGravity( Gravity.CENTER );
        codeBtn.setOnClickListener( new OnClickListener( ) {
            @Override
            public void onClick(View v) {
                //ToastUtil.showToast(FindPwd2Activity.this, codeBtn.getText().toString());
            }
        });

        LinearLayout.LayoutParams rlp = new LinearLayout.LayoutParams( LinearLayout.LayoutParams.WRAP_CONTENT , DensityUtil.dip2px(this,25) );
        codeBtn.setLayoutParams(rlp);
    }

    //修改密码网络请求
    RequestManager.RequestListener modifyPwdRequestListener = new RequestManager.RequestListener() {
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
                ToastUtil.showToast(FindPwd2Activity.this, "修改密码成功！");

                //返回登录界面
                Intent intent = new Intent();
                intent.setClass(FindPwd2Activity.this, LoginActivity.class);
                FindPwd2Activity.this.startActivity(intent);
            }
            else
            {
                ToastUtil.showToast(FindPwd2Activity.this, returnCode + "： "+ returnMsg);
            }
        }

        @Override
        public void onError(String message, String url, int actionId) {
            LogUtil.e("onError",message);
            ToastUtil.showToast(FindPwd2Activity.this, "修改密码失败： "+ message);
        }
    };
}