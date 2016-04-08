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
import android.widget.TextView;
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

/**
 * @Desc:
 * @Auth: chenxzhuang
 * @Time: 2016/3/22 0022.
 */
public class RegisterUserFromPhone extends Activity implements View.OnClickListener {

    private TextView TVregistPhone;
    private ClearEditText CEauthCode;
    private Button BTensure;
    private Button BTtime;
    private ImageView IVback;

    private String strAuthCode;
    private String strPassword;
    private String strPhoneNumber;

    private ProgressDialog pd;
    JSONObject JSuserInfoJson;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.rgst_user_second);

        TVregistPhone = (TextView)findViewById(R.id.rgstuser_getrstmsg);
        CEauthCode = (ClearEditText)findViewById(R.id.registuser_getcode);
        BTensure = (Button)findViewById(R.id.rgstuser_getcodeensure);
        //	BTtime = (Button)findViewById(R.id.rgst_getcode_time);
        IVback = (ImageView)findViewById(R.id.rgstuser_back);

        CEauthCode.setOnClickListener(this);
        BTensure.setOnClickListener(this);
        //   BTtime.setOnClickListener(this);
        IVback.setOnClickListener(this);

        Intent intent = getIntent();
        if (intent != null){
            strPassword = intent.getStringExtra("password");
            strPhoneNumber = intent.getStringExtra("phone");
        }
        TVregistPhone.setText(strPhoneNumber);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch(v.getId()){
            case R.id.rgstuser_getcodeensure:
                strAuthCode = CEauthCode.getText().toString();
                if (!strAuthCode.equals("")){
                    if (new ConnectionDetector(RegisterUserFromPhone.this).isConnectingTOInternet()) {
                        pd = ProgressDialog.show(RegisterUserFromPhone.this, "", "正在进行注册......");
                        CheckAuthCodeToServer();
                    } else {
                        Toast.makeText(RegisterUserFromPhone.this, "网络连接不可用，请检查网络后再试", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            //  case R.id.rgst_getcode_time:
            //    break;
            case R.id.rgstuser_back:
                startActivity(new Intent(RegisterUserFromPhone.this, LoginActivity.class));
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
                case UserInfoMessage.USER_REGISTER_CHECKCODE_TYPE:
                    try {
                        if (json.get("code").equals("0")) {
                            registerApi();
                        }else if (json.get("code").equals("520")){
                            pd.dismiss();
                            Toast.makeText(RegisterUserFromPhone.this,"服务器异常，请稍后再试!", Toast.LENGTH_SHORT).show();
                        }else if (json.get("code").equals("425")){
                            pd.dismiss();
                            Toast.makeText(RegisterUserFromPhone.this,"验证码已过期，请重新注册!", Toast.LENGTH_SHORT).show();
                        }else{
                            pd.dismiss();
                            Toast.makeText(RegisterUserFromPhone.this,"校验失败，请稍后注册!", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        pd.dismiss();
                    }
                    break;
                case UserInfoMessage.USER_REGISTER_TYPE:
                    try {
                        if (json.get("code").equals("0")) {
                            HanvonApplication.isActivity = true;
                            SharedPreferences mSharedPreferences=getSharedPreferences("BitMapUrl", Activity.MODE_MULTI_PROCESS);
                            SharedPreferences.Editor mEditor=	mSharedPreferences.edit();
                            mEditor.putString("username", strPhoneNumber);
                            HanvonApplication.isActivity = true;
                            mEditor.putBoolean("isActivity", HanvonApplication.isActivity);
                            mEditor.putString("nickname", "");
                            HanvonApplication.hvnName = strPhoneNumber;
                            HanvonApplication.strName = "";
                            mEditor.putInt("flag", 0);
                            mEditor.putInt("status", 1);
                            mEditor.commit();

                            UploadDeviceStat();
                        } else if (json.get("code").equals("520")){
                            pd.dismiss();
                            Toast.makeText(RegisterUserFromPhone.this, "服务器异常，请稍后再试!", Toast.LENGTH_SHORT).show();
                        } else {
                            pd.dismiss();
                            Toast.makeText(RegisterUserFromPhone.this, "注册失败!", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        pd.dismiss();
                    }
                    break;
                case UserInfoMessage.USER_DEVICE_UPLOAD_TYPE:
                    startActivity(new Intent(RegisterUserFromPhone.this, MainActivity.class));
                    RegisterUserFromPhone.this.finish();
                    pd.dismiss();
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
            JSuserInfoJson.put("phone", strPhoneNumber);
            JSuserInfoJson.put("authcode", strAuthCode);
        }catch (JSONException e) {
            e.printStackTrace();
        }
        new RequestTask(UserInfoMessage.USER_REGISTER_CHECKCODE_TYPE,callBack).execute(JSuserInfoJson);
    }

    public void  registerApi(){
        LogUtil.i("user:"+strPhoneNumber+"    pwd:"+strPassword);
        JSuserInfoJson = new JSONObject();
        try {
            JSuserInfoJson.put("uid",HanvonApplication.AppDeviceId);
            JSuserInfoJson.put("sid", HanvonApplication.AppSid);
            JSuserInfoJson.put("user", strPhoneNumber);
            JSuserInfoJson.put("pwd", strPassword);
            JSuserInfoJson.put("mobile", strPhoneNumber);
            JSuserInfoJson.put("registeWay","1");
            JSuserInfoJson = StatisticsUtils.StatisticsJson(JSuserInfoJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }

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
            startActivity(new Intent(RegisterUserFromPhone.this, LoginActivity.class));
            this.finish();
        }
        return false;
    }

}