package com.hanvon.rc.login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.hanvon.rc.R;
import com.hanvon.rc.activity.MainActivity;
import com.hanvon.rc.application.HanvonApplication;
import com.hanvon.rc.utils.ClearEditText;
import com.hanvon.rc.utils.ConnectionDetector;
import com.hanvon.rc.utils.LogUtil;
import com.hanvon.rc.utils.StatisticsUtils;
import com.hanvon.userinfo.RequestTask;
import com.hanvon.userinfo.ResultCallBack;
import com.hanvon.userinfo.UserInfoMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Desc:
 * @Auth: chenxzhuang
 * @Time: 2016/3/22 0022.
 */
public class RegisterUserFromEmail extends Activity implements View.OnClickListener {

    private ClearEditText CEemail;
    private ClearEditText CEpasswd;
    private Button BTensure;
    private ImageView IVback;
    private ImageView IVregisterPhone;

    private String strEmail;
    private String strPassword;

    private ProgressDialog pd;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.register_user_email);

        CEemail = (ClearEditText)findViewById(R.id.emailrst_emailaddr);
        CEpasswd = (ClearEditText)findViewById(R.id.emailrst_pswd);
        BTensure = (Button)findViewById(R.id.emailrst_rgstbtn);
        IVback = (ImageView)findViewById(R.id.rgstuser_back);
        IVregisterPhone = (ImageView)findViewById(R.id.emailrst_phonebtn);

        CEemail.setOnClickListener(this);
        CEpasswd.setOnClickListener(this);
        BTensure.setOnClickListener(this);
        IVback.setOnClickListener(this);
        IVregisterPhone.setOnClickListener(this);

        StatisticsUtils.IncreaseRegisterPage();

    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch(v.getId()){
            case R.id.emailrst_rgstbtn:
                strEmail = CEemail.getText().toString();
                strPassword = CEpasswd.getText().toString();

                if (strEmail == null || strPassword == null){
                    Toast.makeText(RegisterUserFromEmail.this, "不允许有空项！", Toast.LENGTH_SHORT).show();
                    return;
                }
                Pattern p = Pattern.compile("^([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)*@([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)+[\\.][A-Za-z]{2,3}([\\.][A-Za-z]{2})?$");
                Matcher m = p.matcher(strEmail);
                if(!m.matches() ){
                    Toast.makeText(RegisterUserFromEmail.this,"邮箱地址不合法", Toast.LENGTH_SHORT).show();
                    return;
                }

                if ((strPassword.length() < 6) || (strPassword.length() > 16)){
                    Toast.makeText(RegisterUserFromEmail.this, "密码应为6-16位字母和数字组合!", Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    Pattern pN = Pattern.compile("[0-9]{6,16}");
                    Matcher mN = pN.matcher(strPassword);
                    Pattern pS = Pattern.compile("[a-zA-Z]{6,16}");
                    Matcher mS = pS.matcher(strPassword);
                    if((mN.matches()) || (mS.matches())){
                        Toast.makeText(RegisterUserFromEmail.this,"密码应为6-16位字母和数字组合!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                if (new ConnectionDetector(RegisterUserFromEmail.this).isConnectingTOInternet()) {
                    pd = ProgressDialog.show(RegisterUserFromEmail.this, "", "正在进行注册......");
                    CheckSameName();
                } else {
                    Toast.makeText(RegisterUserFromEmail.this, "网络连接不可用，请检查网络后再试", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.rgstuser_back:
                startActivity(new Intent(RegisterUserFromEmail.this, LoginActivity.class));
                this.finish();
                break;
            case R.id.emailrst_phonebtn:
                StatisticsUtils.IncreasePhoneBtn();
                startActivity(new Intent(RegisterUserFromEmail.this, RegisterUserGetCodePhone.class));
                this.finish();
                break;
            default:
                break;
        }
    }

    private ResultCallBack callBack = new ResultCallBack() {
        @Override
        public void back(int type, JSONObject json) {
            LogUtil.i("========"+json.toString());
            switch(type){
                case UserInfoMessage.USER_CHECKNAME_TYPE:
                    try {
                        if (json.get("code").equals("0")) {
                            RigestUserApi();
                        } else if (json.get("code").equals("422")){
                            pd.dismiss();
                            Toast.makeText(RegisterUserFromEmail.this, "该邮箱已被注册，请直接登录!", Toast.LENGTH_SHORT).show();
                        } else if (json.get("code").equals("520")){
                            pd.dismiss();
                            Toast.makeText(RegisterUserFromEmail.this, "服务器异常，请稍后再试!", Toast.LENGTH_SHORT).show();
                        } else {
                            pd.dismiss();
                            Toast.makeText(RegisterUserFromEmail.this, "用户名重名检查失败!", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        pd.dismiss();
                        e.printStackTrace();
                    }
                    break;
                case UserInfoMessage.USER_REGISTER_TYPE:
                    try {
                        if (json.get("code").equals("0")) {
                            SendCodetoEmail();
                        }else{
                            pd.dismiss();
                            Toast.makeText(RegisterUserFromEmail.this, "用户注册失败,请稍后重试!", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        pd.dismiss();
                        e.printStackTrace();
                    }
                    break;
                case UserInfoMessage.USER_GETACTIVITY_EMAIL_TYPE:
                    try {
                        if (json.get("code").equals("0")) {
                            SharedPreferences mSharedPreferences=getSharedPreferences("BitMapUrl", Activity.MODE_MULTI_PROCESS);
                            SharedPreferences.Editor mEditor=	mSharedPreferences.edit();
                            mEditor.putString("nickname", "");
                            mEditor.putString("username", strEmail);
                            HanvonApplication.isActivity = false;
                            mEditor.putBoolean("isActivity", HanvonApplication.isActivity);
                            HanvonApplication.hvnName = strEmail;
                            HanvonApplication.strName = "";
                            mEditor.putInt("flag", 0);
                            mEditor.putInt("status", 1);
                            mEditor.commit();

                            UploadDeviceStat();
                        } else if (json.get("code").equals("520")){
                            pd.dismiss();
                            Toast.makeText(RegisterUserFromEmail.this, "服务器异常，请稍后再试!", Toast.LENGTH_SHORT).show();
                        } else {
                            pd.dismiss();
                            Toast.makeText(RegisterUserFromEmail.this, "登录失败!", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        pd.dismiss();
                        e.printStackTrace();
                    }
                    break;
                case UserInfoMessage.USER_DEVICE_UPLOAD_TYPE:
                    pd.dismiss();
                    Toast.makeText(RegisterUserFromEmail.this, "注册成功，请进入邮箱激活!", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(RegisterUserFromEmail.this, MainActivity.class));
                    RegisterUserFromEmail.this.finish();
                    break;
            }
        }
    };
    public void CheckSameName(){
        JSONObject paramJson=new JSONObject();
        try {
            paramJson.put("uid", HanvonApplication.AppDeviceId);
            paramJson.put("sid", HanvonApplication.AppSid);
            paramJson.put("data", strEmail);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new RequestTask(UserInfoMessage.USER_CHECKNAME_TYPE,callBack).execute(paramJson);
    }

    public void SendCodetoEmail(){
        JSONObject JSuserInfoJson = new JSONObject();
        try {
            JSuserInfoJson.put("user", strEmail);
        }catch (JSONException e) {
            e.printStackTrace();
        }
        new RequestTask(UserInfoMessage.USER_GETACTIVITY_EMAIL_TYPE,callBack).execute(JSuserInfoJson);
    }

    public void  RigestUserApi(){

        JSONObject JSuserInfoJson = new JSONObject();
        try {
            JSuserInfoJson.put("uid", HanvonApplication.AppDeviceId);
            JSuserInfoJson.put("sid", HanvonApplication.AppSid);
            JSuserInfoJson.put("user", strEmail);
            JSuserInfoJson.put("pwd", strPassword);
            JSuserInfoJson.put("email", strEmail);
            JSuserInfoJson.put("registeWay","0");
            JSuserInfoJson = StatisticsUtils.StatisticsJson(JSuserInfoJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtil.i("======="+JSuserInfoJson.toString());
        new RequestTask(UserInfoMessage.USER_REGISTER_TYPE,callBack).execute(JSuserInfoJson);
    }

    public void UploadDeviceStat(){

        JSONObject devinfo = new JSONObject();
        try {
            devinfo.put("userid", HanvonApplication.hvnName);
            devinfo.put("devid", HanvonApplication.AppDeviceId);
            devinfo.put("devModel", "Android");
            devinfo.put("softName", HanvonApplication.AppSid);
            devinfo.put("osName", android.os.Build.MODEL);
            devinfo.put("osVer",android.os.Build.VERSION.RELEASE);
            devinfo.put("softVer", HanvonApplication.AppVer);
            devinfo.put("longitude", HanvonApplication.curLongitude);
            devinfo.put("latitude", HanvonApplication.curLatitude);
            devinfo.put("locationCountry", HanvonApplication.curCountry);
            devinfo.put("locationProvince", HanvonApplication.curProvince);
            devinfo.put("locationCity", HanvonApplication.curCity);
            devinfo.put("locationArea", HanvonApplication.curDistrict);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new RequestTask(UserInfoMessage.USER_DEVICE_UPLOAD_TYPE,callBack).execute(devinfo);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK )
        {
            startActivity(new Intent(RegisterUserFromEmail.this, LoginActivity.class));
            this.finish();
        }
        return false;
    }

}
