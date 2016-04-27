package com.hanvon.rc.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;


import com.hanvon.rc.R;
import com.hanvon.rc.db.DBManager;
import com.hanvon.rc.fragment.MainFragment;
import com.hanvon.rc.md.camera.activity.CameraActivity;
import com.hanvon.rc.md.camera.activity.ExactActivity;
import com.hanvon.rc.utils.LogUtil;

/**
 * Created by fanjianmin on 16-3-15.
 */
public class MainActivity extends Activity
{
    private final String TAG = "MainActivity";

    public static DBManager dbManager;

    private FrameLayout mFrameLayout;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        LogUtil.i("on Create called");

        dbManager = new DBManager(this);

        initView();
        loadLatest();

    }

    @Override
    protected void onResume()
    {
        super.onResume();
        LogUtil.i("onResume called !!!!!!1");
        //String valueStr = this.getIntent().getStringExtra("key");
        Bundle bundle = this.getIntent().getBundleExtra("msg");
        String value = null;
        if (null != bundle)
        {
            LogUtil.i("get bundle");
            value = bundle.getString("key");
            LogUtil.i("value is " + value);
        }
        //LogUtil.i("valueStr is " + valueStr);
        if (null != value)
        {

                LogUtil.i(" !!!!!!exit program");
                finish();
        }

    }

    private void initView()
    {
        mFrameLayout = (FrameLayout)findViewById(R.id.fl_content);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.mipmap.ic_launcher,
                R.string.menu_drawer_open, R.string.menu_drawer_close)
        {

            public void onDrawerClosed(View view)
            {
                Log.d(TAG, "!!!! onDrawerClosed !!!!");
            }

            public void onDrawerOpened(View view)
            {
                Log.d(TAG, "!!!! onDrawerOpened !!!!");
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    public void loadLatest()
    {
        getFragmentManager().beginTransaction().replace(R.id.fl_content, new MainFragment()).commit();
    }

    public void openMenu()
    {
        mDrawerLayout.openDrawer(Gravity.LEFT);
    }

    public void closeMenu()
    {
        mDrawerLayout.closeDrawers();
    }

    public void startCameraActivity()
    {
        Intent intet = new Intent(MainActivity.this, CameraActivity.class);
        startActivity(intet);
    }

    public void startExactActivity()
    {
        Intent intet = new Intent(MainActivity.this, ExactActivity.class);
        startActivity(intet);
    }

    public void exitProgram()
    {
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }
}
