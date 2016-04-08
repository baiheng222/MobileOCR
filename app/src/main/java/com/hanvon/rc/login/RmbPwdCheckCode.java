package com.hanvon.rc.login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hanvon.rc.R;
import com.hanvon.rc.application.HanvonApplication;
import com.hanvon.rc.utils.ClearEditText;
import com.hanvon.rc.utils.LogUtil;
import com.hanvon.userinfo.RequestTask;
import com.hanvon.userinfo.ResultCallBack;
import com.hanvon.userinfo.UserInfoMessage;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @Desc:
 * @Auth: chenxzhuang
 * @Time: 2016/3/23 0023.
 */
public class RmbPwdCheckCode extends Activity implements View.OnClickListener {

    private TextView TVgetPhone;
    private ClearEditText CEauthCode;
    private TextView TVensure;
    private ImageView IVback;

    private String strPhone;
    private String strAuthCode;

    ProgressDialog pd;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.rmbpwd_second);

        TVgetPhone = (TextView)findViewById(R.id.rmbpwdsecode_getphone);
        CEauthCode = (ClearEditText)findViewById(R.id.rmbpwdsecode_getcode);
        TVensure = (TextView)findViewById(R.id.rmbpwdsecode_ensure);
        IVback = (ImageView)findViewById(R.id.rmbpwd_back);

        TVgetPhone.setOnClickListener(this);
        CEauthCode.setOnClickListener(this);
        TVensure.setOnClickListener(this);
        IVback.setOnClickListener(this);

        Intent intent = getIntent();
        if (intent != null){
            strPhone = intent.getStringExtra("phone");
            TVgetPhone.setText(strPhone);
        }
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()){
            case R.id.rmbpwdsecode_ensure:
                strAuthCode = CEauthCode.getText().toString();
                if (strAuthCode != null){
                    pd = ProgressDialog.show(RmbPwdCheckCode.this, "", "正在进行校验码检查......");
                    CheckAuthCodeToServer();
                }
                break;
            case R.id.rmbpwd_back:
                startActivity(new Intent(RmbPwdCheckCode.this, LoginActivity.class));
                this.finish();
                break;
        }
    }

    private ResultCallBack callBack = new ResultCallBack() {

        @Override
        public void back(int type, JSONObject json) {
            switch(type){
                case UserInfoMessage.USER_REGISTER_CHECKCODE_TYPE:
                    try {
                        if (json.get("code").equals("0")) {
                            pd.dismiss();
                            Intent intent = new Intent();
                            intent.putExtra("authcode", strAuthCode);
                            intent.putExtra("username", strPhone);
                            intent.setClass(RmbPwdCheckCode.this, ResetPasswd.class);
                            startActivity(intent);
                            finish();
                        }else if (json.get("code").equals("520")){
                            pd.dismiss();
                            Toast.makeText(RmbPwdCheckCode.this,"服务器异常，请稍后再试!", Toast.LENGTH_SHORT).show();
                        }else if (json.get("code").equals("425")){
                            pd.dismiss();
                            Toast.makeText(RmbPwdCheckCode.this,"验证码已过期，请重新注册!", Toast.LENGTH_SHORT).show();
                        }else{
                            pd.dismiss();
                            Toast.makeText(RmbPwdCheckCode.this,"校验失败，请稍后注册!", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        LogUtil.i("==========="+e.toString());
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };
    public void CheckAuthCodeToServer(){
        JSONObject JSuserInfoJson = new JSONObject();
        try {
            JSuserInfoJson.put("uid", HanvonApplication.AppDeviceId);
            JSuserInfoJson.put("sid", HanvonApplication.AppSid);
            JSuserInfoJson.put("ver", HanvonApplication.AppVer);
            JSuserInfoJson.put("phone", strPhone);
            JSuserInfoJson.put("authcode", strAuthCode);
        }catch (JSONException e) {
            e.printStackTrace();
        }
        new RequestTask(UserInfoMessage.USER_REGISTER_CHECKCODE_TYPE,callBack).execute(JSuserInfoJson);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if(keyCode == KeyEvent.KEYCODE_BACK )
        {
            startActivity(new Intent(RmbPwdCheckCode.this, LoginActivity.class));
            this.finish();
        }
        return false;
    }

}
