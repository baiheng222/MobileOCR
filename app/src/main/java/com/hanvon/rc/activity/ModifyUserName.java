package com.hanvon.rc.activity;

import android.app.Activity;
import android.app.ProgressDialog;
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
 * @Time: 2016/4/1 0001.
 */
public class ModifyUserName extends Activity implements View.OnClickListener {

    private ClearEditText CEnickName;
    private String strNickName;

    private String strOldNickname;
    private String strUserName;
    private TextView TVcommit;
    private ImageView IVback;

    private ProgressDialog pd;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.modify_name);

        CEnickName = (ClearEditText)findViewById(R.id.modifyname_name);
        TVcommit = (TextView)findViewById(R.id.modifyname_ensurebtn);
        IVback =(ImageView)findViewById(R.id.modifyname_back);

        CEnickName.setOnClickListener(this);
        TVcommit.setOnClickListener(this);
        IVback.setOnClickListener(this);

        SharedPreferences mSharedPreferences=getSharedPreferences("BitMapUrl", Activity.MODE_MULTI_PROCESS);
        strUserName = mSharedPreferences.getString("username", "");
        strOldNickname = mSharedPreferences.getString("nickname", "");
        CEnickName.setText(strOldNickname);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch(v.getId()){
            case R.id.modifyname_ensurebtn:
                strNickName = CEnickName.getText().toString();
                if(strOldNickname.equals(strNickName)){
                    return;
                }
                if (strNickName.length() < 3 || strNickName.length() >= 20){
                    Toast.makeText(ModifyUserName.this, "昵称至少应为3个字母且最大为20个字母!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (new ConnectionDetector(ModifyUserName.this).isConnectingTOInternet()) {
                    pd = ProgressDialog.show(ModifyUserName.this, "", "正在加载....");
                    modifyNickName();
                } else {
                    Toast.makeText(ModifyUserName.this, "网络连接不可用，请检查网络后再试", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.modifyname_back:
                startActivity(new Intent(ModifyUserName.this, ShowUserMessage.class));
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
            switch (type) {
                case UserInfoMessage.USER_MODIFY_NICKNAME_TYPE:
                    int status = 0;
                    try {
                        status = Integer.valueOf(json.get("code").toString()).intValue();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (status == 0){
                        SharedPreferences mSharedPreferences=getSharedPreferences("BitMapUrl", Activity.MODE_MULTI_PROCESS);
                        SharedPreferences.Editor mEditor=	mSharedPreferences.edit();
                        mEditor.putString("nickname", strNickName);
                        HanvonApplication.strName = strNickName;
                        mEditor.commit();

                        startActivity(new Intent(ModifyUserName.this, ShowUserMessage.class));
                        ModifyUserName.this.finish();
                    }else if (status == 520){
                        Toast.makeText(ModifyUserName.this,"服务器异常，请稍后再试!", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(ModifyUserName.this,"发送失败，请稍后重试!", Toast.LENGTH_SHORT).show();
                    }
            }
        }
    };

    public void modifyNickName(){
        JSONObject JSuserInfoJson = new JSONObject();
        try {
            JSuserInfoJson.put("uid", HanvonApplication.AppDeviceId);
            JSuserInfoJson.put("sid", HanvonApplication.AppSid);
            JSuserInfoJson.put("ver", "");
            JSuserInfoJson.put("user", strUserName);
            JSuserInfoJson.put("nickname", strNickName);
        }catch (JSONException e) {
            e.printStackTrace();
        }
        new RequestTask(UserInfoMessage.USER_MODIFY_NICKNAME_TYPE,callBack).execute(JSuserInfoJson);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK )
        {
            startActivity(new Intent(ModifyUserName.this, ShowUserMessage.class));
            this.finish();
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
