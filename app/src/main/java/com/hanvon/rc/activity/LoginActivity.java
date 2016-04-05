package com.hanvon.rc.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hanvon.rc.R;
import com.hanvon.rc.application.HanvonApplication;
import com.hanvon.rc.utils.ClearEditText;
import com.hanvon.rc.utils.ConnectionDetector;
import com.hanvon.rc.utils.LogUtil;
import com.hanvon.userinfo.RequestTask;
import com.hanvon.userinfo.ResultCallBack;
import com.hanvon.userinfo.UserInfoMessage;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * @Desc:
 * @Auth: chenxzhuang
 * @Time: 2016/3/22 0022.
 */
public class LoginActivity  extends Activity implements View.OnClickListener{
    private TextView TVSkip;
    private ClearEditText ETUserName;
    private ClearEditText ETPassWord;
    private Button BTLogin;
    private TextView TVRegist;
    private TextView TVForgetPassword;
    private String strUserName;
    private String strPassWord;

    private ProgressDialog pd;

    private ImageView LLQQUser;
    private ImageView LLWXUser;

    private int userflag = 0;
    public static LoginActivity instance = null;
    public String flag;   // 0 从其他界面跳转 1 从云信息登陆跳转  2 从上传界面跳转

    private static final int MSG_USERID_FOUND = 1;
    private static final int MSG_LOGIN = 2;
    private static final int MSG_AUTH_CANCEL = 3;
    private static final int MSG_AUTH_ERROR= 4;
    private static final int MSG_AUTH_COMPLETE = 5;
    private static final int MSG_CLIENT_ERROR= 6;

    private String openid;
    private String figureurl;
    private String nickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //	ShareSDK.initSDK(this);
        //  instance = this;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);

        TVSkip = (TextView) findViewById(R.id.login_back);
        ETUserName = (ClearEditText) findViewById(R.id.login_username);
        ETPassWord = (ClearEditText) findViewById(R.id.login_password);
        BTLogin = (Button) findViewById(R.id.login_loginbtn);
        TVRegist = (TextView) findViewById(R.id.login_registerbtn);
        TVForgetPassword = (TextView) findViewById(R.id.login_rememberpwd);

        LLQQUser = (ImageView)findViewById(R.id.login_qq);
        LLWXUser = (ImageView)findViewById(R.id.login_weixin);

        TVSkip.setOnClickListener(this);
        ETUserName.setOnClickListener(this);
        ETPassWord.setOnClickListener(this);
        BTLogin.setOnClickListener(this);
        TVRegist.setOnClickListener(this);
        TVForgetPassword.setOnClickListener(this);
        LLQQUser.setOnClickListener(this);
        LLWXUser.setOnClickListener(this);

        //  StatisticsUtils.IncreaseLoginPage();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_back:
                goHome();
                break;
            case R.id.login_loginbtn:
                strPassWord = ETPassWord.getText().toString();
                strUserName = ETUserName.getText().toString();
                if (strPassWord.equals("") || strUserName.equals("")){
                    Toast.makeText(LoginActivity.this, "用户名或者密码不允许为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                //      StatisticsUtils.IncreaseHvnLogin();
                LogUtil.i("username:" + strUserName + ", passwd:" + strPassWord);
                InputMethodManager m=(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                m.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                judgeUserIsOk();
                break;
            case R.id.login_registerbtn:
                //    StatisticsUtils.IncreaseRegister();
                LogUtil.i("INTO Create user Before");
                Intent intent = new Intent(LoginActivity.this, RegisterUserGetCodePhone.class);
                startActivity(intent);
                LoginActivity.this.finish();
                break;
            case R.id.login_rememberpwd:
                Intent intent1 = new Intent(LoginActivity.this, RememberPassword.class);
                LoginActivity.this.startActivity(intent1);
                LoginActivity.this.finish();
                break;

            case R.id.login_qq:
                //    StatisticsUtils.IncreaseQQLogin();
                //   QQUserLogin();
                break;

            case R.id.login_weixin:
                //    StatisticsUtils.IncreaseWXLogin();
                //    weiXinUserLogin();
                break;

            default:
                break;
        }
    }

    private void goHome() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        LoginActivity.this.startActivity(intent);
        LoginActivity.this.finish();
    }

    public void judgeUserIsOk(){
        if (new ConnectionDetector(LoginActivity.this).isConnectingTOInternet()) {
            pd = ProgressDialog.show(LoginActivity.this, "", "正在登录......");
            try {
                userLogin();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(LoginActivity.this, "网络连接不可用，请检查网络后再试", Toast.LENGTH_SHORT).show();
        }

    }

    private ResultCallBack callBack = new ResultCallBack() {
        @Override
        public void back(int type, JSONObject json) {
            LogUtil.i("===json:"+json.toString());
            switch(type){
                case UserInfoMessage.USER_LOGIN_TYPE:
                    try {
                        if (json.get("code").equals("0")) {
                            LogUtil.i("***************************");
                            JSONObject paramJson=new JSONObject();
                            paramJson.put("user", strUserName);
                            new RequestTask(UserInfoMessage.USER_GET_USERINFO_TYPE,callBack).execute(paramJson);
                        } else if (json.get("code").equals("520")){
                            pd.dismiss();
                            Toast.makeText(LoginActivity.this, "服务器异常，请稍后再试!", Toast.LENGTH_SHORT).show();
                        } else {
                            pd.dismiss();
                            Toast.makeText(LoginActivity.this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        pd.dismiss();
                        Toast.makeText(getApplication(), "网络连接超时", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                    break;
                case UserInfoMessage.USER_GET_USERINFO_TYPE:
                    pd.dismiss();
                    try {
                        if (json.getString("code").equals("0") ){
                            boolean isHasNick = true;
                            String nickname = json.getString("nickname");
                            if (nickname.equals("null")){
                                nickname ="";
                            }
                            if(json.getString("isActive").equals("1")){
                                HanvonApplication.isActivity = true;
                            }else{
                                HanvonApplication.isActivity = false;
                            }
                            if(nickname.equals("")){
                                isHasNick = false;
                            }
                            String username = json.getString("user");
                            SharedPreferences mSharedPreferences=getSharedPreferences("BitMapUrl", Activity.MODE_MULTI_PROCESS);
                            SharedPreferences.Editor mEditor = mSharedPreferences.edit();
                            mEditor.putString("nickname", nickname);
                            mEditor.putString("username", username);
                            mEditor.putBoolean("isActivity", HanvonApplication.isActivity);
                            HanvonApplication.hvnName = username;
                            HanvonApplication.strName = nickname;
                            mEditor.putBoolean("isHasNick", isHasNick);

                            mEditor.putString("passwd", strPassWord);
                            mEditor.putInt("flag", 0);
                            mEditor.putInt("status", 1);
                            mEditor.commit();
                            finish();
                        }
                    } catch (Exception e) {
                        pd.dismiss();
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };

    public void userLogin() throws JSONException {
        JSONObject paramJson = new JSONObject();
        paramJson.put("uid", HanvonApplication.AppDeviceId);
        paramJson.put("sid",HanvonApplication.AppSid);
        paramJson.put("user", strUserName);
        paramJson.put("pwd", strPassWord);
        new RequestTask(UserInfoMessage.USER_LOGIN_TYPE,callBack).execute(paramJson);
    }

    @Override
    protected void onDestroy() {
        LogUtil.i("INTO onDestroy!!!!!!!!");
        super.onDestroy();

        if (pd != null){
            pd.dismiss();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK )
        {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            this.finish();
        }
        return false;
    }
}
