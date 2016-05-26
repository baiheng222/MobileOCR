package com.hanvon.rc.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.hanvon.rc.application.HanvonApplication;
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
 * @Time: 2016/4/27 0027.
 */
public class SoftUpdate extends Activity implements DialogInterface.OnClickListener {

    private String UpdateUrl;
    private Context mContext;
    private final int UPDATA_CLIENT = 0;
    private final int GET_UNDATAINFO_ERROR = 1;
    private final int DOWN_ERROR = 2;
    private String version;
    public static ProgressDialog pd;
    private int flag;

    private SharedPreferences mDefaultPreference;

    /* 下载包安装路径 */
    private static final String savePath = "/sdcard/updatedemo/";

    public SoftUpdate(Context context,int flag) {
        this.mContext = context;
        this.flag = flag;
    }

    public void onBackPressed() {
        super.onBackPressed();
    }

    public String getVersion(){
        PackageManager packageManager = mContext.getPackageManager();
        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(mContext.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        version = packInfo.versionName;
        LogUtil.i(version);
        return version;
    }

    public void checkVersion(){

        if (new ConnectionDetector(mContext).isConnectingTOInternet()) {
            if (flag == 1){
                pd = ProgressDialog.show(mContext, "", "正在进行版本检查......");
            }
            version = getVersion();
            getNewVersionFromServer();
        } else {
            Toast.makeText(mContext, "网络连接不可用，请检查网络后再试", Toast.LENGTH_SHORT).show();
        }
    }

    public void getNewVersionFromServer(){
        JSONObject JSuserInfoJson = new JSONObject();
        try {
            JSuserInfoJson.put("uid", HanvonApplication.AppDeviceId);
            JSuserInfoJson.put("sid", HanvonApplication.AppSid);
            JSuserInfoJson.put("ver", version);
            JSuserInfoJson.put("type", 1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new RequestTask(UserInfoMessage.SOFT_UPDATE_TYPE,callBack).execute(JSuserInfoJson);
    }

    private ResultCallBack callBack = new ResultCallBack() {
        @Override
        public void back(int i, JSONObject json) {
            LogUtil.i(json.toString());
            if (json.equals(null)){
                return;
            }
            try {
                if (json.getString("code").equals("0")){
                    LogUtil.i("有更新的版本，是否需要升级？");
                    UpdateUrl = json.getString("result");
                    Message msg = new Message();
                    msg.what = UPDATA_CLIENT;
                    handler.sendMessage(msg);

                    SharedPreferences.Editor editor = Settings.mSharedPref.edit();
                    editor.putBoolean("hasUpdate", true);
                    editor.commit();
                }else if (json.getString("code").equals("9120")){
                    if(flag == 1){
                        pd.dismiss();
                        Toast.makeText(mContext, "已是最新版本，不需要升级",Toast.LENGTH_LONG).show();
                    }
                    SharedPreferences.Editor editor = Settings.mSharedPref.edit();
                    editor.putBoolean("hasUpdate", false);
                    editor.commit();
                }else if (json.getString("code").equals("9100")){
                    if(flag == 1) {
                        pd.dismiss();
                    }
                    LogUtil.i("请求错误");
                }else{
                    if(flag == 1) {
                        pd.dismiss();
                    }
                   Toast.makeText(mContext,"请求出现错误!",Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
    Handler handler=new Handler(){
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATA_CLIENT:
                    showUpdataDialog();
                    break;
                case GET_UNDATAINFO_ERROR:
                    //服务器超时
                    Toast.makeText(mContext, "获取服务器更新信息失败",Toast.LENGTH_LONG).show();
                    break;
                case DOWN_ERROR:
                    Toast.makeText(mContext, "下载新版本失败",Toast.LENGTH_LONG).show();
                    break;
            }
        };
    };
    public void showUpdataDialog() {
        AlertDialog.Builder builer = new AlertDialog.Builder(mContext);
        builer.setTitle("版本升级");
        builer.setMessage("有最新的版本，是否需要下载？");
        builer.setPositiveButton("下载", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                LogUtil.i("下载apk,更新");
                new UpdateAppService(mContext).CreateInform(UpdateUrl);
            }
        });
        //当点取消按钮时进行登录
        builer.setNegativeButton("以后再说", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                /*****************/
                if(flag == 1) {
                    pd.dismiss();
                }
                Settings.setKeyVersionUpdate(false);
            }
        });
        AlertDialog dialog = builer.create();
        dialog.show();
    }

    @Override
    public void onClick(DialogInterface arg0, int arg1) {
        // TODO Auto-generated method stub
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
