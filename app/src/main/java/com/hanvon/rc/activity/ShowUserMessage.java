package com.hanvon.rc.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hanvon.rc.R;
import com.hanvon.rc.application.HanvonApplication;

/**
 * @Desc:
 * @Auth: chenxzhuang
 * @Time: 2016/4/1 0001.
 */
public class ShowUserMessage extends Activity implements View.OnClickListener {

    private TextView TVuserName;
    private TextView TVnickName;
    private ImageView IVmodifyNick;
    private TextView TVaccountSafety;
    private RelativeLayout RLmodifyPwd;
    private ImageView IVmodifyPwd;
    private TextView TVthirdLogin;
    private RelativeLayout RLthird;
    private ImageView IVthirdBind;
    private TextView TVLoginOut;

    private ImageView TVback;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.usermessage);

        if (HanvonApplication.userFlag != 0){
            findViewById(R.id.safety).setVisibility(View.GONE);
            findViewById(R.id.modify_pwd_id).setVisibility(View.GONE);
        }else{
            findViewById(R.id.third_login).setVisibility(View.GONE);
            findViewById(R.id.third).setVisibility(View.GONE);
        }

        if (HanvonApplication.hvnName == ""){
            findViewById(R.id.quit_login).setVisibility(View.GONE);
        }


        TVuserName = (TextView) findViewById(R.id.show_uesrname);
        TVnickName = (TextView) findViewById(R.id.show_nickname);
        IVmodifyNick = (ImageView) findViewById(R.id.modify_nickname);
        TVaccountSafety = (TextView) findViewById(R.id.safety);
        RLmodifyPwd = (RelativeLayout) findViewById(R.id.modify_pwd_id);
        IVmodifyPwd = (ImageView) findViewById(R.id.modify_password);
        TVthirdLogin = (TextView) findViewById(R.id.third_login);
        RLthird = (RelativeLayout) findViewById(R.id.third);
        IVthirdBind = (ImageView) findViewById(R.id.third_bind);
        TVLoginOut = (TextView) findViewById(R.id.quit_login);
        TVback = (ImageView)findViewById(R.id.usermessage_back);

        IVmodifyNick.setOnClickListener(this);
        IVmodifyPwd.setOnClickListener(this);
        IVthirdBind.setOnClickListener(this);
        TVLoginOut.setOnClickListener(this);
        TVback.setOnClickListener(this);

        ShowUserInfo();
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch(v.getId()){
            case R.id.modify_nickname:
                startActivity(new Intent(ShowUserMessage.this, ModifyUserName.class));
                ShowUserMessage.this.finish();
                break;
            case R.id.modify_password:
                startActivity(new Intent(ShowUserMessage.this, ModifyPassword.class));
                ShowUserMessage.this.finish();
                break;
            case R.id.third_bind:
                //startActivity(new Intent(ShowUserMessage.this, ThirdBind.class));
                //ShowUserMessage.this.finish();
                Toast.makeText(this, "此版本暂不支持该功能！", Toast.LENGTH_LONG).show();
                break;
            case R.id.quit_login:
                UserLoginOut();
                break;
            case R.id.usermessage_back:
                startActivity(new Intent(ShowUserMessage.this, MainActivity.class));
                ShowUserMessage.this.finish();
                break;
        }

    }

    public void ShowUserInfo(){
        TVuserName.setText(HanvonApplication.hvnName);
        if (!HanvonApplication.strName.equals("")){
            TVnickName.setText(HanvonApplication.strName);
        }
    }

    public void UserLoginOut(){
        HanvonApplication.strName = "";
        HanvonApplication.hvnName = "";
        HanvonApplication.BitHeadImage = null;
        SharedPreferences mSharedPreferences=getSharedPreferences("BitMapUrl", Activity.MODE_MULTI_PROCESS);
        int flag =mSharedPreferences.getInt("flag", 0);
        //	String plat = mSharedPreferences.getString("plat", "");
        //	LogUtil.i("---quit:---"+plat);
        //	HanvonApplication.plat = ;
        HanvonApplication.userFlag = flag;
        if (flag == 0){
        }else if(flag == 1){
            //	HanvonApplication.plat.removeAccount();
            //  LoginUtil loginUtil = new LoginUtil(SettingActivity.this,SettingActivity.this);
            //  loginUtil.QQLoginOut();
       //     Platform QQplat = ShareSDK.getPlatform(this, QQ.NAME);
     //       LogUtil.i("---quit:---"+QQplat);
     //       if (QQplat.isValid ()) {
      //          QQplat.removeAccount();
      //      }
        }else if (flag == 2){
            //	HanvonApplication.plat.removeAccount();
      //      Platform WXplat = ShareSDK.getPlatform(this, Wechat.NAME);
        //    LogUtil.i("---quit:---"+WXplat.toString());
       //     if (WXplat.isValid ()) {
       //         WXplat.removeAccount();
       //     }
        }
        SharedPreferences.Editor mEditor=	mSharedPreferences.edit();
        mEditor.putInt("status", 0);
        mEditor.putString("nickname", "");
        mEditor.putString("username", "");
        HanvonApplication.isActivity = false;
        mEditor.commit();

        startActivity(new Intent(ShowUserMessage.this, MainActivity.class));
        ShowUserMessage.this.finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK )
        {
            startActivity(new Intent(ShowUserMessage.this, MainActivity.class));
            ShowUserMessage.this.finish();
        }
        return false;
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

