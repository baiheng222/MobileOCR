package com.hanvon.rc.login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hanvon.rc.R;
import com.hanvon.rc.activity.MainActivity;
import com.hanvon.rc.application.HanvonApplication;
import com.hanvon.rc.utils.ClearEditText;
import com.hanvon.rc.utils.ConnectionDetector;
import com.hanvon.rc.utils.LogUtil;
import com.hanvon.rc.utils.LoginUtils;
import com.hanvon.userinfo.RequestTask;
import com.hanvon.userinfo.ResultCallBack;
import com.hanvon.userinfo.UserInfoMessage;
import com.mob.tools.utils.UIHandler;
import com.tencent.tauth.Tencent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;


/**
 * @Desc:
 * @Auth: chenxzhuang
 * @Time: 2016/3/22 0022.
 */
public class LoginActivity  extends Activity implements Handler.Callback,
        View.OnClickListener, PlatformActionListener {
    private TextView TVSkip;
    private ClearEditText ETUserName;
    private ClearEditText ETPassWord;
    private Button BTLogin;
    private TextView TVRegist;
    private TextView TVForgetPassword;
    private String strUserName;
    private String strPassWord;

    public static ProgressDialog pd;

    private ImageView LLQQUser;
    private ImageView LLWXUser;

    private int userflag = 0;
    public static LoginActivity instance = null;

    private static final int MSG_USERID_FOUND = 1;
    private static final int MSG_LOGIN = 2;
    private static final int MSG_AUTH_CANCEL = 3;
    private static final int MSG_AUTH_ERROR= 4;
    private static final int MSG_AUTH_COMPLETE = 5;
    private static final int MSG_CLIENT_ERROR= 6;

    private String openid;
    private String figureurl;
    private String nickname;

  //  public static IWXAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  ShareSDK.initSDK(this);
        instance = this;
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

        if (HanvonApplication.mTencent == null) {
            HanvonApplication.mTencent = Tencent.createInstance("1105311110", this);
        }
    //    api = WXAPIFactory.createWXAPI(this, "wx021fab5878ea9288", true);
     //   api.registerApp("wx021fab5878ea9288");
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
                QQUserLogin();
                break;

            case R.id.login_weixin:
                //    StatisticsUtils.IncreaseWXLogin();
                    WeiXinUserLogin();
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
        paramJson.put("sid", HanvonApplication.AppSid);
        paramJson.put("user", strUserName);
        paramJson.put("pwd", strPassWord);
        new RequestTask(UserInfoMessage.USER_LOGIN_TYPE,callBack).execute(paramJson);
    }

    public void QQUserLogin(){
        if (new ConnectionDetector(LoginActivity.this).isConnectingTOInternet()) {
        }else{
            Toast.makeText(LoginActivity.this, "网络连接不可用，请检查网络后再试", Toast.LENGTH_SHORT).show();
            return;
        }
        LogUtil.i("INTO QQUserLogin!!!!!!!!");
        userflag = 1;
        pd = ProgressDialog.show(this, "", "正在登陆中，请稍后...");
        authorize(new QQ(this));
    }

    public void WeiXinUserLogin(){
        if (new ConnectionDetector(LoginActivity.this).isConnectingTOInternet()) {
        }else{
            Toast.makeText(LoginActivity.this, "网络连接不可用，请检查网络后再试", Toast.LENGTH_SHORT).show();
            return;
        }
        LogUtil.i("INTO WeixinUserLogin!!!!!!!!");
        userflag = 2;
        pd = ProgressDialog.show(this, "", "正在登陆中，请稍后...");
        authorize(new Wechat(this));
    }

    public synchronized Drawable byteToDrawable(String icon) {
        byte[] img= Base64.decode(icon.getBytes(), Base64.DEFAULT);
        Bitmap bitmap;
        if (img != null) {
            bitmap = BitmapFactory.decodeByteArray(img, 0, img.length);
            @SuppressWarnings("deprecation")
            Drawable drawable = new BitmapDrawable(bitmap);

            return drawable;
        }
        return null;
    }

    private void authorize(Platform plat) {


        if(plat.isValid()) {
            LogUtil.i("------isValid --11111111-------" + plat.isValid());
            String userId = plat.getDb().getUserId();
            if (!TextUtils.isEmpty(userId)) {
                UIHandler.sendEmptyMessage(MSG_USERID_FOUND, this);
                nickname = plat.getDb().getUserName();
                openid = plat.getDb().getUserId();
                figureurl = plat.getDb().getUserIcon();

                LogUtil.i("---nickname:" + nickname+"  openid:"+openid);

                login(plat.getName(), userId, null);
                return;
            }
        }
        LogUtil.i("------isValid --22222222222-------");
        plat.setPlatformActionListener(this);
        plat.SSOSetting(true);
        plat.showUser(null);
        plat.getDb().putExpiresIn(15*24*3600);
    }

    public void onComplete(Platform platform, int action,
                           HashMap<String, Object> res) {
        if (action == Platform.ACTION_USER_INFOR) {
            UIHandler.sendEmptyMessage(MSG_AUTH_COMPLETE, this);
            nickname = platform.getDb().getUserName();
            openid = platform.getDb().getUserId();
            figureurl = platform.getDb().getUserIcon();

            LogUtil.i("---nickname:" + nickname+"  openid:"+openid);
            login(platform.getName(), platform.getDb().getUserId(), res);
        }
        LogUtil.i(res.toString());
        nickname = platform.getDb().getUserName();
        openid = platform.getDb().getUserId();
        figureurl = platform.getDb().getUserIcon();

        LogUtil.i("---nickname:" + nickname+"  openid:"+openid);
        LoginUtils login = new LoginUtils(this,userflag);
        login.setFigureurl(platform.getDb().getUserIcon());
        login.setNickName(platform.getDb().getUserName());
        login.setOpenid(platform.getDb().getUserId());

        login.LoginToHvn();

    }

    public void onError(Platform platform, int action, Throwable t) {
        if (action == Platform.ACTION_USER_INFOR) {
            if (t.toString().contains("ClientNotExistException")){
                UIHandler.sendEmptyMessage(MSG_CLIENT_ERROR, this);
            }else{
                UIHandler.sendEmptyMessage(MSG_AUTH_ERROR, this);
            }
        }
        t.printStackTrace();
    }

    public void onCancel(Platform platform, int action) {
        if (action == Platform.ACTION_USER_INFOR) {
            UIHandler.sendEmptyMessage(MSG_AUTH_CANCEL, this);
        }
    }

    private void login(String plat, String userId, HashMap<String, Object> userInfo) {
        Message msg = new Message();
        msg.what = MSG_LOGIN;
        msg.obj = plat;
        UIHandler.sendMessage(msg, this);
    }

    public boolean handleMessage(Message msg) {
        switch(msg.what) {
            case MSG_USERID_FOUND: {
                Toast.makeText(this, R.string.userid_found, Toast.LENGTH_SHORT).show();
                LoginUtils login = new LoginUtils(this,userflag);
                login.setFigureurl(figureurl);
                login.setNickName(nickname);
                login.setOpenid(openid);

                login.LoginToHvn();
            }
            break;
            case MSG_LOGIN: {
                //	String text = getString(R.string.logining, msg.obj);
                //	Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
                System.out.println("---------------");
                //	pd.dismiss();
            }
            break;
            case MSG_AUTH_CANCEL: {
                Toast.makeText(this, R.string.auth_cancel, Toast.LENGTH_SHORT).show();
                System.out.println("-------MSG_AUTH_CANCEL--------");
                pd.dismiss();
            }
            break;
            case MSG_AUTH_ERROR: {
                Toast.makeText(this, R.string.auth_error, Toast.LENGTH_SHORT).show();
                System.out.println("-------MSG_AUTH_ERROR--------");
                pd.dismiss();
            }
            break;
            case MSG_AUTH_COMPLETE: {
                Toast.makeText(this, R.string.auth_complete, Toast.LENGTH_SHORT).show();
                System.out.println("--------MSG_AUTH_COMPLETE-------");
            }
            break;
            case MSG_CLIENT_ERROR:
                Toast.makeText(this, R.string.client_error, Toast.LENGTH_SHORT).show();
                System.out.println("-------MSG_CLIENT_ERROR--------");
                pd.dismiss();
                break;
        }
        return false;
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
