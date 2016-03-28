package com.hanvon.mobileocr.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.hanvon.mobileocr.R;
import com.hanvon.mobileocr.application.HanvonApplication;
import com.hanvon.mobileocr.utils.ClearEditText;
import com.hanvon.mobileocr.utils.ConnectionDetector;
import org.json.JSONException;
import org.json.JSONObject;

import com.hanvon.mobileocr.utils.LogUtil;
import com.hanvon.userinfo.*;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Desc:
 * @Auth: chenxzhuang
 * @Time: 2016/3/22 0022.
 */
public class RegisterUserGetCodePhone extends Activity implements View.OnClickListener {

    private ClearEditText CEphoneNumber;
    private ClearEditText CEpassword;
    private Button BTregist;
    private ImageView IVback;
    private ImageView IVregisterPhone;
    private ImageView IVregisterEmail;

    private String strPhoneNumber;
    private String strPassword;

    private ProgressDialog pd;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.register_user_phone);

        CEphoneNumber = (ClearEditText)findViewById(R.id.rgst_user);
        CEpassword = (ClearEditText)findViewById(R.id.rgst_pswd);
        BTregist = (Button)findViewById(R.id.rgst_rgstbutton);
        IVback = (ImageView)findViewById(R.id.rgstuser_back);
        IVregisterEmail = (ImageView)findViewById(R.id.register_email_button);
        IVregisterPhone = (ImageView)findViewById(R.id.register_phone_button);

        CEphoneNumber.setOnClickListener(this);
        CEpassword.setOnClickListener(this);
        BTregist.setOnClickListener(this);
        IVback.setOnClickListener(this);
        IVregisterEmail.setOnClickListener(this);
        IVregisterPhone.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch(v.getId()){
            case R.id.rgst_rgstbutton:
                strPhoneNumber = CEphoneNumber.getText().toString();
                strPassword = CEpassword.getText().toString();
                if (strPhoneNumber.equals("") || strPassword.equals("")){
                    Toast.makeText(RegisterUserGetCodePhone.this, "手机号和密码不允许为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                if ((strPassword.length() < 6) || (strPassword.length() > 16)){
                    Toast.makeText(RegisterUserGetCodePhone.this, "密码应为6-16位字母和数字组合!", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    Pattern pN = Pattern.compile("[0-9]{6,16}");
                    Matcher mN = pN.matcher(strPassword);
                    Pattern pS = Pattern.compile("[a-zA-Z]{6,16}");
                    Matcher mS = pS.matcher(strPassword);
                    if((mN.matches()) || (mS.matches())){
                        Toast.makeText(RegisterUserGetCodePhone.this,"请输入符合规则的密码!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                if (strPhoneNumber != null){
                    Pattern p = Pattern.compile("[1][358]+\\d{9}");
                    Matcher m = p.matcher(strPhoneNumber);
                    if(!m.matches() ){
                        Toast.makeText(RegisterUserGetCodePhone.this,"手机号码不合法", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                if (new ConnectionDetector(RegisterUserGetCodePhone.this).isConnectingTOInternet()) {
                    pd = ProgressDialog.show(RegisterUserGetCodePhone.this, "", "正在进行手机号检查......");
                    CheckSameName();
                } else {
                    Toast.makeText(RegisterUserGetCodePhone.this, "网络连接不可用，请检查网络后再试", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.register_email_button:
                Intent intent = new Intent(RegisterUserGetCodePhone.this, RegisterUserFromEmail.class);
                startActivity(intent);
                this.finish();
                break;
            case R.id.rgstuser_back:
                startActivity(new Intent(RegisterUserGetCodePhone.this, LoginActivity.class));
                this.finish();
                break;
            default:
                break;
        }
    }

    private ResultCallBack callBack = new ResultCallBack() {
        @Override
        public void back(int type, JSONObject json) {
            LogUtil.i("===json:" + json.toString());
            switch(type){
                case UserInfoMessage.USER_CHECKNAME_TYPE:
                    try {
                        if (json.get("code").equals("0")) {
                            SendAuthCode();
                        } else if (json.get("code").equals("422")){
                            pd.dismiss();
                            Toast.makeText(RegisterUserGetCodePhone.this, "该手机号已被注册，请直接登录!", Toast.LENGTH_SHORT).show();
                        } else if (json.get("code").equals("520")){
                            pd.dismiss();
                            Toast.makeText(RegisterUserGetCodePhone.this, "服务器异常，请稍后再试!", Toast.LENGTH_SHORT).show();
                        } else {
                            pd.dismiss();
                            Toast.makeText(RegisterUserGetCodePhone.this, "用户名重名检查失败!", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case UserInfoMessage.USER_REGISTER_GETCODE_TYPE:
                    try {
                        if (json.get("code").equals("0")) {
                            pd.dismiss();
                            Intent intent = new Intent();
                            intent.putExtra("phone", strPhoneNumber);
                            intent.putExtra("password", strPassword);
                            intent.setClass(RegisterUserGetCodePhone.this, RegisterUserFromPhone.class);
                            RegisterUserGetCodePhone.this.startActivity(intent);
                            RegisterUserGetCodePhone.this.finish();
                        } else if (json.get("code").equals("520")){
                            pd.dismiss();
                            Toast.makeText(RegisterUserGetCodePhone.this, "服务器异常，请稍后再试!", Toast.LENGTH_SHORT).show();
                        } else {
                            pd.dismiss();
                            Toast.makeText(RegisterUserGetCodePhone.this, "注册失败!", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };

    public void CheckSameName() {
        JSONObject paramJson = new JSONObject();
        try {
            paramJson.put("uid", HanvonApplication.AppDeviceId);
            paramJson.put("sid", HanvonApplication.AppSid);
            paramJson.put("data", strPhoneNumber);
        }catch (JSONException e) {
            e.printStackTrace();
        }
        new RequestTask(UserInfoMessage.USER_CHECKNAME_TYPE,callBack).execute(paramJson);
    }

    public void SendAuthCode(){
        JSONObject JSuserInfoJson = new JSONObject();
        try {
            JSuserInfoJson.put("uid", HanvonApplication.AppDeviceId);
            JSuserInfoJson.put("sid", HanvonApplication.AppSid);
            JSuserInfoJson.put("ver", HanvonApplication.AppVer);
            JSuserInfoJson.put("phone", strPhoneNumber);
        }catch (JSONException e) {
            e.printStackTrace();
        }
        new RequestTask(UserInfoMessage.USER_REGISTER_GETCODE_TYPE,callBack).execute(JSuserInfoJson);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK )
        {
            startActivity(new Intent(RegisterUserGetCodePhone.this, LoginActivity.class));
            this.finish();
        }
        return false;
    }
}

