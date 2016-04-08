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
import com.hanvon.rc.utils.ConnectionDetector;
import com.hanvon.rc.utils.LogUtil;
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
 * @Time: 2016/3/23 0023.
 */
public class ResetPasswd  extends Activity implements View.OnClickListener {

    private ClearEditText CEnewPwd;
    private ClearEditText CEensureNewPwd;

    private TextView TVensure;
    private ImageView IVback;

    private String strNewPwd;
    private String strEnsurePwd;
    private String strAuthCode;
    private String strUsername;
    ProgressDialog pd;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.reset_newpwd);

        CEnewPwd = (ClearEditText)findViewById(R.id.resetpwd_newpwd);
        CEensureNewPwd = (ClearEditText)findViewById(R.id.resetpwd_ensurenewpwd);
        TVensure = (TextView)findViewById(R.id.resetpwd_ensure);
        IVback = (ImageView)findViewById(R.id.rmbpwd_back);

        CEnewPwd.setOnClickListener(this);
        CEensureNewPwd.setOnClickListener(this);
        TVensure.setOnClickListener(this);
        IVback.setOnClickListener(this);

        Intent intent = getIntent();
        if (intent != null){
            strAuthCode = intent.getStringExtra("authcode");
            strUsername = intent.getStringExtra("username");
        }
    }
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch(v.getId()){
            case R.id.resetpwd_ensure:
                strNewPwd = CEnewPwd.getText().toString();
                strEnsurePwd = CEensureNewPwd.getText().toString();

                if ((strNewPwd.length() < 6) || (strNewPwd.length() > 16)){
                    Toast.makeText(ResetPasswd.this, "密码应为6-16位字母和数字组合!", Toast.LENGTH_SHORT).show();
                    return ;
                } else {
                    Pattern pN = Pattern.compile("[0-9]{6,16}");
                    Matcher mN = pN.matcher(strNewPwd);
                    Pattern pS = Pattern.compile("[a-zA-Z]{6,16}");
                    Matcher mS = pS.matcher(strNewPwd);
                    if((mN.matches()) || (mS.matches())){
                        Toast.makeText(ResetPasswd.this,"请输入符合规则的密码!", Toast.LENGTH_SHORT).show();
                        return ;
                    }
                }

                if (!strEnsurePwd.equals(strNewPwd)){
                    Toast.makeText(ResetPasswd.this, "两次输入的密码不一致!", Toast.LENGTH_SHORT).show();
                    return ;
                }
                if (new ConnectionDetector(ResetPasswd.this).isConnectingTOInternet()) {
                    pd = ProgressDialog.show(ResetPasswd.this, "", "正在进行密码重置......");
                    resetPasswdForUser();
                } else {
                    Toast.makeText(ResetPasswd.this, "网络连接不可用，请检查网络后再试", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.rmbpwd_back:
                startActivity(new Intent(ResetPasswd.this, LoginActivity.class));
                this.finish();
                break;
        }
    }

    private ResultCallBack callBack = new ResultCallBack() {

        @Override
        public void back(int type, JSONObject json) {
            pd.dismiss();
            switch(type){
                case UserInfoMessage.USER_PHONE_RSETPWD_TYPE:
                    LogUtil.i(json.toString());
                    try {
                        if (json.get("code").equals("0")){
                            Toast.makeText(ResetPasswd.this, "密码重置成功，请重新登录", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(ResetPasswd.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }else if (json.get("code").equals("420")){
                            Toast.makeText(ResetPasswd.this, "输入的 json 串不合法", Toast.LENGTH_SHORT).show();
                        }else if (json.get("code").equals("421")){
                            Toast.makeText(ResetPasswd.this, "请求存在非法为空项，如： uid,sid,ver 或其它", Toast.LENGTH_SHORT).show();
                        }else if (json.get("code").equals("425")){
                            Toast.makeText(ResetPasswd.this, " 验证码错误或已过期", Toast.LENGTH_SHORT).show();
                        }else if (json.get("code").equals("520")){
                            Toast.makeText(ResetPasswd.this, " 服务器有异常发生 ", Toast.LENGTH_SHORT).show();
                        }else if (json.get("code").equals("427")){
                            Toast.makeText(ResetPasswd.this, " 验证码正确,但重置密码失败", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };
    public void resetPasswdForUser(){

        JSONObject JSuserInfoJson = new JSONObject();
        try {
            JSuserInfoJson.put("uid", HanvonApplication.AppDeviceId);
            JSuserInfoJson.put("sid", HanvonApplication.AppSid);
            JSuserInfoJson.put("ver", HanvonApplication.AppVer);
            JSuserInfoJson.put("user", strUsername);
            JSuserInfoJson.put("phone", strUsername);
            JSuserInfoJson.put("repwd", strNewPwd);
        } catch (JSONException e) {
            pd.dismiss();
            e.printStackTrace();
        }
        new RequestTask(UserInfoMessage.USER_PHONE_RSETPWD_TYPE,callBack).execute(JSuserInfoJson);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if(keyCode == KeyEvent.KEYCODE_BACK )
        {
            startActivity(new Intent(ResetPasswd.this, LoginActivity.class));
            this.finish();
        }
        return false;
    }
}
