package com.hanvon.rc.activity;

import android.app.Activity;

import com.hanvon.rc.R;
import com.hanvon.rc.utils.LogUtil;
import com.jaeger.library.StatusBarUtil;

/**
 * Created by baiheng222 on 16-8-24.
 */
public class BaseActivity extends Activity
{
    @Override
    public void setContentView(int layoutResID)
    {
        super.setContentView(layoutResID);
        LogUtil.i("call setStatusBar in baseActivity");
        setStatusBar();
    }

    protected void setStatusBar()
    {
        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorPrimary));
    }
}
