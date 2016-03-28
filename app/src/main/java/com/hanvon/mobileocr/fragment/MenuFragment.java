package com.hanvon.mobileocr.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hanvon.mobileocr.R;
import com.hanvon.mobileocr.activity.LoginActivity;
import com.hanvon.mobileocr.application.HanvonApplication;
import com.hanvon.mobileocr.utils.CircleImageView;
import com.hanvon.mobileocr.utils.LogUtil;

/**
 * Created by baiheng222 on 16-3-15.
 */
public class MenuFragment extends BaseFragment implements View.OnClickListener {
    private final String TAG = "MenuFragment";

    private CircleImageView mIvLogin;
    private TextView TVusername;
    private TextView TVnickname;
    private RelativeLayout mRlOcr;
    private RelativeLayout mRlFile;
    private RelativeLayout mRlOrders;
    private RelativeLayout mRlSettings;


    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.menu_fragment, container, false);

        mIvLogin = (CircleImageView) view.findViewById(R.id.iv_login_icon);
        mIvLogin.setOnClickListener(this);

        TVusername = (TextView) view.findViewById(R.id.ivUserName);
        TVnickname = (TextView) view.findViewById(R.id.ivhvnUserName);

        mRlOcr = (RelativeLayout) view.findViewById(R.id.rl_ocr);
        mRlFile = (RelativeLayout) view.findViewById(R.id.rl_file);
        mRlOrders = (RelativeLayout) view.findViewById(R.id.rl_orders);
        mRlSettings = (RelativeLayout) view.findViewById(R.id.rl_settings);

        return view;
    }

    @Override
    public void onStart(){
        super.onStart();
        ShowUserInfo();
    }
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.iv_login_icon:
                Intent intent = new Intent(MenuFragment.this.getActivity(), LoginActivity.class);
                this.startActivity(intent);
                break;
        }
    }

    public void ShowUserInfo(){
        String email = "",phone = "",hvnname = "",figureurl = "",username = "";
        SharedPreferences mSharedPreferences= MenuFragment.this.getActivity().getSharedPreferences("BitMapUrl", Activity.MODE_MULTI_PROCESS);
        int flag = mSharedPreferences.getInt("flag", 0);
        HanvonApplication.userFlag = flag;
        HanvonApplication.isActivity = mSharedPreferences.getBoolean("isActivity", false);
        String nickname=mSharedPreferences.getString("nickname", "");
        boolean isHasNick = mSharedPreferences.getBoolean("isHasNick", true);
        if (flag == 0){
            username = mSharedPreferences.getString("username", "");
        }else{
            figureurl=mSharedPreferences.getString("figureurl", "");
            hvnname = mSharedPreferences.getString("username", "");
        }
        int status = mSharedPreferences.getInt("status", 0);
        LogUtil.i("flag:" + flag + "  nickname:" + nickname + "   status:" + status + "  username:" + hvnname + "  figureurl:" + figureurl);

        if (status == 1){
            if (flag == 0){
                if(!nickname.isEmpty()){
                    TVusername.setText(nickname);
                    TVnickname.setText(username);
                    HanvonApplication.hvnName = username;
                    HanvonApplication.strName = nickname;
                    mIvLogin.setBackgroundResource(R.mipmap.login);
                    LogUtil.i("hvnName:"+username+"  strName:"+nickname);
                }else{
                    TVusername.setText("");
                    TVnickname.setText(username);
                    HanvonApplication.hvnName = username;
                    HanvonApplication.strName = nickname;
                    mIvLogin.setBackgroundResource(R.mipmap.login);
                    LogUtil.i("hvnName:"+username+"  strName:"+nickname);
                }
            }
            if (flag == 1 || flag == 2){
                if(!nickname.isEmpty()){
                    TVusername.setText(nickname);
                    TVnickname.setText(hvnname);
                    HanvonApplication.strName = nickname;
                    HanvonApplication.hvnName = hvnname;
                }else{
                    TVusername.setText("");
                    TVnickname.setText(hvnname);
                    HanvonApplication.strName = nickname;
                    HanvonApplication.hvnName = hvnname;
                }
                if(!figureurl.isEmpty()){
               //     BitmapUtils  bitmapUtils = new  BitmapUtils(this);
                //    bitmapUtils.configDefaultLoadingImage(R.drawable.logicon);
                //    bitmapUtils.configDefaultBitmapMaxSize(BitmapCommonUtils.getScreenSize(
                //            this).scaleDown(3));
                //    bitmapUtils.configDefaultShowOriginal(true);
                 //   bitmapUtils.display(((ImageView)findViewById(R.id.iv_login_icon)),figureurl);
                }
            }
        }else{
            TVusername.setText("");
            TVnickname.setText("未登录");
            if (HanvonApplication.userFlag == 0){
                mIvLogin.setBackgroundResource(R.mipmap.logout);
            }else{
                mIvLogin.setImageDrawable((getResources().getDrawable(R.mipmap.logout)));
            }
            mIvLogin.setImageResource(R.mipmap.logout);
        }
    }
}
