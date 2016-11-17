package com.hanvon.rc.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.hanvon.rc.R;
import com.hanvon.rc.application.HanvonApplication;
import com.hanvon.rc.db.DBManager;
import com.hanvon.rc.fragment.MainFragment;
import com.hanvon.rc.login.LoginActivity;
import com.hanvon.rc.md.camera.activity.CameraActivity;
import com.hanvon.rc.utils.ConnectionDetector;
import com.hanvon.rc.utils.InfoMsg;
import com.hanvon.rc.utils.LogUtil;
import com.hanvon.rc.utils.StatisticsUtils;
import com.jaeger.library.StatusBarUtil;


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

    public static boolean isFromLogin;

    private int mStatusBarColor;
    private int mAlpha = StatusBarUtil.DEFAULT_STATUS_BAR_ALPHA;



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

        if (new ConnectionDetector(this).isConnectingTOInternet()) {
            boolean autoUpdateflag = Settings.getKeyVersionUpdate(MainActivity.this);
            if (autoUpdateflag){
                SoftUpdate updateInfo = new SoftUpdate(this,0);
                updateInfo.checkVersion();
            }
        }

        StatisticsUtils.IncreaseMainPage();
        StatisticsUtils.SetCurTimeHour();
        try {
            StatisticsUtils.UpLoadFunctionStatus1(MainActivity.this);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        /*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
                //将侧边栏顶部延伸至status bar
                mDrawerLayout.setFitsSystemWindows(true);
                //将主页面顶部延伸至status bar;虽默认为false,但经测试,DrawerLayout需显示设置
                mDrawerLayout.setClipToPadding(false);
            }
        }
        */

        /*
        mStatusBarColor = getResources().getColor(R.color.activity_default_color);
        StatusBarUtil.setColorForDrawerLayout(MainActivity.this, mDrawerLayout, mStatusBarColor, 0);
        */
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        setIntent(intent);
        LogUtil.i("onNewIntent called !!!!");
        //processExtraData();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        LogUtil.i("onResume called !!!!!!1");
        //String valueStr = this.getIntent().getStringExtra("key");
        Bundle bundle = this.getIntent().getBundleExtra("msg");
        String value = null;

        if(isFromLogin)
        {
            LogUtil.i("-----------from Login----------------");
            getFragmentManager().findFragmentById(R.id.menu_fragment).onStart();
            isFromLogin = false;
            return;
        }
        if (null != bundle)
        {
            LogUtil.i("get bundle");
            value = bundle.getString("key");
            LogUtil.i("value is " + value);
        }
        //LogUtil.i("valueStr is " + valueStr);
       if (null != value){

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
        intet.putExtra("recomode", InfoMsg.RECO_MODE_QUICK_RECO);
        startActivity(intet);
    }

    public void startExactActivity()
    {
        if (HanvonApplication.hvnName.length() > 0)
        {
            Intent intet = new Intent(MainActivity.this, CameraActivity.class);
            intet.putExtra("recomode", InfoMsg.RECO_MODE_EXACT_RECO);
            startActivity(intet);
        }
        else
        {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }

    public void exitProgram()
    {
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        StatisticsUtils.WriteBack();
        StatisticsUtils.releaseInstance();
        LogUtil.i("-------onDestory----MainActivity-------");
    }

    /*
    @Override
    protected void setStatusBar()
    {
        mStatusBarColor = getResources().getColor(R.color.activity_default_color);
        StatusBarUtil.setColorForDrawerLayout(this, (DrawerLayout)findViewById(R.id.drawerlayout), mStatusBarColor, 112);
    }
    */

}
