package com.hanvon.mobileocr.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hanvon.mobileocr.R;
import com.hanvon.mobileocr.utils.CircleImageView;

/**
 * Created by baiheng222 on 16-3-15.
 */
public class MenuFragment extends BaseFragment
{
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
        //mIvLogin.setOnClickListener(this);

        TVusername = (TextView) view.findViewById(R.id.ivUserName);
        TVnickname = (TextView) view.findViewById(R.id.ivhvnUserName);

        mRlOcr = (RelativeLayout) view.findViewById(R.id.rl_ocr);
        mRlFile = (RelativeLayout) view.findViewById(R.id.rl_file);
        mRlOrders = (RelativeLayout) view.findViewById(R.id.rl_orders);
        mRlSettings = (RelativeLayout) view.findViewById(R.id.rl_settings);

        return view;
    }
}
