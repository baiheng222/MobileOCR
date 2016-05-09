package com.hanvon.rc.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.hanvon.rc.login.LoginActivity;
import com.hanvon.rc.activity.MainActivity;
import com.hanvon.rc.application.HanvonApplication;
import com.hanvon.userinfo.RequestTask;
import com.hanvon.userinfo.ResultCallBack;
import com.hanvon.userinfo.UserInfoMessage;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @Desc:
 * @Auth: chenxzhuang
 * @Time: 2016/4/6 0006.
 */
public class LoginUtils {

    private Context mContext;
    private int userflag;
    private String openid;
    private String figureurl;
    private String nickname;

    private String qqOpenId = "";
    private String wxOpenId = "";
    private String wbOpenId = "";


    private int accountFlag = 0;  //0 主账户  1 从账户qq 2 从账户 wx 3 从账户微博
    private int accountCount = 0;

    public LoginUtils(Context mcontext, int userflag) {
        this.mContext = mcontext;
        this.userflag = userflag;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public void setFigureurl(String url) {
        this.figureurl = url;
    }

    public void setNickName(String nickname) {
        this.nickname = nickname;
    }

    public void LoginToHvn() {
        GetUserInfo();
    }



    private ResultCallBack callBack = new ResultCallBack() {
        @Override
        public void back(int type, JSONObject json) {
            LogUtil.i("===json:" + json.toString());
            switch (type) {
                case UserInfoMessage.USER_GET_USERINFO_TYPE:
                    try {
                        if (json.getString("code").equals("0")) {
                            String nickname = json.getString("nickname");
                            String username = json.getString("user");
                            HanvonApplication.isActivity = true;
                            SharedPreferences mSharedPreferences = mContext.getSharedPreferences("BitMapUrl", Activity.MODE_MULTI_PROCESS);
                            SharedPreferences.Editor mEditor = mSharedPreferences.edit();
                            mEditor.putString("username", username);
                            mEditor.putBoolean("isActivity", HanvonApplication.isActivity);
                            HanvonApplication.hvnName = username;
                            if (nickname.equals("null")) {
                                nickname = "";
                            }

                            HanvonApplication.strName = nickname;
                            mEditor.putString("nickname", nickname);
                            mEditor.putString("figureurl", figureurl);
                            mEditor.putInt("flag", userflag);
                            mEditor.putInt("status", 1);
                            mEditor.commit();

                            mContext.startActivity(new Intent(mContext, MainActivity.class));
                            LoginActivity.instance.finish();
                        } else if (json.getString("code").equals("426")) {
                            RegisterToHvn();
                        } else if (json.getString("code").equals("520")) {
                            LoginActivity.pd.dismiss();
                            Toast.makeText(mContext, "服务器忙，请稍后重试", Toast.LENGTH_SHORT).show();
                        } else {
                            LoginActivity.pd.dismiss();
                            Toast.makeText(mContext, "注册汉王云失败，请稍后重试", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        LoginActivity.pd.dismiss();
                        e.printStackTrace();
                    }
                    break;
                case UserInfoMessage.USER_QQ_REGISTER_TYPE:
                case UserInfoMessage.USER_WX_REGISTER_TYPE:
                    try {
                        if (json.getString("code").equals("0") || json.getString("code").equals("422")) {
                            String qqName = json.getString("username");
                            HanvonApplication.isActivity = true;
                            SharedPreferences mSharedPreferences = mContext.getSharedPreferences("BitMapUrl", Activity.MODE_MULTI_PROCESS);
                            SharedPreferences.Editor mEditor = mSharedPreferences.edit();
                            HanvonApplication.hvnName = qqName;
                            HanvonApplication.strName = nickname;
                            mEditor.putString("username", qqName);
                            mEditor.putBoolean("isActivity", HanvonApplication.isActivity);
                            mEditor.putString("nickname", nickname);
                            mEditor.putString("figureurl", figureurl);
                            mEditor.putInt("flag", userflag);
                            mEditor.putInt("status", 1);
                            mEditor.commit();

                            UploadDeviceStat();
                            break;
                        }
                    } catch (JSONException e) {
                        LoginActivity.pd.dismiss();
                        e.printStackTrace();
                    }
                case UserInfoMessage.USER_DEVICE_UPLOAD_TYPE:
                  //  GetUserInfo();
                    mContext.startActivity(new Intent(mContext, MainActivity.class));
                    LoginActivity.instance.finish();
                    break;
            }
        }
    };

    public void GetUserInfo() {
        JSONObject paramJson = new JSONObject();
        try {
            if (userflag == 1) {
                paramJson.put("user", "qq_" + SHA1Util.encodeBySHA(openid));
            } else if (userflag == 2) {
                paramJson.put("user", "wx_" + SHA1Util.encodeBySHA(openid));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtil.i(paramJson.toString());
        new RequestTask(UserInfoMessage.USER_GET_USERINFO_TYPE, callBack).execute(paramJson);
    }

    public void RegisterToHvn() {
        JSONObject paramJson = new JSONObject();
        try {
            paramJson.put("devid", HanvonApplication.AppDeviceId);
            paramJson.put("openId", openid);
            paramJson.put("nickName", nickname);
            paramJson = StatisticsUtils.StatisticsJson(paramJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        LogUtil.i(paramJson.toString());

        if (userflag == 1) {
            new RequestTask(UserInfoMessage.USER_QQ_REGISTER_TYPE, callBack).execute(paramJson);
        } else if (userflag == 2) {
            new RequestTask(UserInfoMessage.USER_WX_REGISTER_TYPE, callBack).execute(paramJson);
        }
    }

    public void UploadDeviceStat() {
        JSONObject devinfo = new JSONObject();
        try {
            devinfo.put("userid", HanvonApplication.hvnName);
            devinfo.put("devid", HanvonApplication.AppDeviceId);
            devinfo.put("devModel", "Android");
            devinfo.put("softName", HanvonApplication.AppSid);
            devinfo.put("osName", android.os.Build.MODEL);
            devinfo.put("osVer", android.os.Build.VERSION.RELEASE);
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
        new RequestTask(UserInfoMessage.USER_DEVICE_UPLOAD_TYPE, callBack).execute(devinfo);
    }
}
