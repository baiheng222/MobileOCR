package com.hanvon.rc.login;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hanvon.rc.R;
import com.hanvon.rc.activity.MainActivity;
import com.hanvon.rc.application.HanvonApplication;
import com.hanvon.rc.orders.ModifyContacts;
import com.hanvon.rc.utils.LogUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;

/**
 * @Desc:
 * @Auth: chenxzhuang
 * @Time: 2016/4/1 0001.
 */
public class ShowUserMessage extends Activity implements View.OnClickListener {

    private TextView TVuserName;
    private TextView TVbindAccount;
    private ImageView IVmodifyBindAccount;
    private ImageView IVmodifyPwd;
    private ImageView IVmodifyContact;
    private TextView TVLoginOut;

    private ImageView TVback;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.usermessage);

        if (HanvonApplication.userFlag != 0){
            findViewById(R.id.name_id).setVisibility(View.GONE);
            findViewById(R.id.modify_pwd_id).setVisibility(View.GONE);
        }else{
            Pattern p = Pattern.compile("[1][3587]+\\d{9}");
            Matcher m = p.matcher(HanvonApplication.hvnName);
            if(m.matches() ){
                ((TextView)findViewById(R.id.bindmail)).setText("手机号");
            }
        }

        if (HanvonApplication.hvnName == ""){
            findViewById(R.id.quit_login).setVisibility(View.GONE);
        }


        TVuserName = (TextView) findViewById(R.id.show_uesrname);
        TVbindAccount = (TextView) findViewById(R.id.bind_account);
        IVmodifyBindAccount = (ImageView) findViewById(R.id.modify_bind_account);
        IVmodifyPwd = (ImageView) findViewById(R.id.modify_password);
        IVmodifyContact = (ImageView) findViewById(R.id.modify_contactway);
        TVLoginOut = (TextView) findViewById(R.id.quit_login);
        TVback = (ImageView)findViewById(R.id.usermessage_back);

        TVbindAccount.setOnClickListener(this);
        IVmodifyBindAccount.setOnClickListener(this);
        IVmodifyPwd.setOnClickListener(this);
        IVmodifyContact.setOnClickListener(this);
        TVLoginOut.setOnClickListener(this);
        TVback.setOnClickListener(this);

        ShowUserInfo();
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch(v.getId()){
            case R.id.bind_account:
            case R.id.modify_bind_account:
                Toast.makeText(this,"该版本暂不支持该功能!",Toast.LENGTH_SHORT).show();
                break;
            case R.id.modify_password:
                startActivity(new Intent(ShowUserMessage.this, ModifyPassword.class));
                ShowUserMessage.this.finish();
                break;
            case R.id.modify_contactway:
                SharedPreferences mSharedPreferences = getSharedPreferences("BitMapUrl", Activity.MODE_MULTI_PROCESS);
                String name = mSharedPreferences.getString("contactsname", "");
                String phone = mSharedPreferences.getString("contactsphone", "");
                Intent intent = new Intent();
                intent.setClass(this,ModifyContacts.class);
                intent.putExtra("name", name);
                intent.putExtra("phone",phone);
                startActivity(intent);
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
            Platform QQplat = ShareSDK.getPlatform(this, QQ.NAME);
            LogUtil.i("---quit:---" + QQplat);
            if (QQplat.isValid ()) {
                QQplat.removeAccount();
            }
        }else if (flag == 2){
            Platform WXplat = ShareSDK.getPlatform(this, Wechat.NAME);
            LogUtil.i("---quit:---" + WXplat.toString());
            if (WXplat.isValid ()) {
                WXplat.removeAccount();
            }
        }
        SharedPreferences.Editor mEditor=	mSharedPreferences.edit();
        mEditor.putInt("status", 0);
        mEditor.putString("nickname", "");
        mEditor.putString("username", "");
        HanvonApplication.isActivity = false;
        mEditor.commit();

        //startActivity(new Intent(ShowUserMessage.this, MainActivity.class));
        ShowUserMessage.this.finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK )
        {
            //startActivity(new Intent(ShowUserMessage.this, MainActivity.class));
            ShowUserMessage.this.finish();
        }
        return false;
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

