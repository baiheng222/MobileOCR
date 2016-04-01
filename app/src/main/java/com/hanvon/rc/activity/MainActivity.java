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
import com.hanvon.rc.fragment.MainFragment;
import com.hanvon.rc.md.camera.activity.CameraActivity;

/**
 * Created by fanjianmin on 16-3-15.
 */
public class MainActivity extends Activity
{
    private final String TAG = "MainActivity";

    private FrameLayout mFrameLayout;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        initView();
        loadLatest();

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
}
