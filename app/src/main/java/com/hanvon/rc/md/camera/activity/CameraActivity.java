package com.hanvon.rc.md.camera.activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Camera;
//import android.hardware.camera2.CameraManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hanvon.rc.R;
import com.hanvon.rc.activity.BaseActivity;
import com.hanvon.rc.activity.MainActivity;
import com.hanvon.rc.bcard.ChooseMorePicturesActivity;
import com.hanvon.rc.bcard.PreviewPicActivity;
import com.hanvon.rc.md.camera.CameraManager;
import com.hanvon.rc.md.camera.PreviewDataManager;
import com.hanvon.rc.md.camera.draw.DrawManager;
import com.hanvon.rc.presentation.CropActivity;
import com.hanvon.rc.utils.CustomDialog;
import com.hanvon.rc.utils.FileUtil;
import com.hanvon.rc.md.camera.DensityUtil;
import com.hanvon.rc.md.camera.activity.ModeCtrl.UserMode;
import com.hanvon.rc.utils.InfoMsg;
import com.hanvon.rc.utils.LogUtil;
import com.hanvon.rc.utils.StatisticsUtils;
import com.hanvon.rc.widget.BadgeView;
import com.jaeger.library.StatusBarUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import android.hardware.Camera.Size;

/**
 * Created by baiheng222 on 16-3-21.
 */
public class CameraActivity extends Activity implements OnClickListener, Camera.PictureCallback, SurfaceHolder.Callback
{
    private final String TAG = "CameraActivity";

    private Context mContext;

    private CameraManager mCameraManager;
    private PreviewDataManager mPreviewDataManager;

    private ImageView mLight;
    private TextView mTvLight;
    private ImageView mGallery;
    private ImageView mCapture;
    private TextView mCancel;
    private BadgeView mSubSuperscript;

    private TextView mSingleCap;
    private TextView mMultiCap;

    private RelativeLayout relativeLayoutUserMode;

    private SurfaceView mSurfaceView;
    private DrawManager drawManager;

    private CAHandlerManager caHandlerManager;


    private float startX;

    private boolean isSurfaceCreated;
    private boolean isTakingPicture;
    private boolean isCameraFlashOpen;

    private ModeCtrl modeCtrl;

    public static CameraActivity cameraActivity;
    public static CameraActivity getCameraActivity() {
        return cameraActivity;
    }

    private int recoMode;
    private int capMode;
    private int multiCapNum;

    private ArrayList<String> picturesPathForSave;


    public static final String FILE_SAVE_DIR_NAME = "savedpic";
    public static final String FILE_SAVE_PATH = "/MobileOCR/";

    private static final int CAPTURE_SINGLE = 1;
    private static final int CAPTURE_MULTI = 2;

    private static final String FLASH_PREFS = "flash_prefs";
    private SharedPreferences prefs;
    private static final int FLASH_ON = 1;
    private static final int FLASH_OFF = 2;
    private static final int FLASH_AUTO = 3;
    private int mFlashMode;

    private int mColor;
    private int mAlpha = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.camera2);

        creatDir();

        initData();

        initView();

        StatisticsUtils.IncreaseCameraPage();
        mColor = getResources().getColor(R.color.activity_default_color);
        StatusBarUtil.setColor(CameraActivity.this, mColor, mAlpha);
    }

    private void initData()
    {
        mContext = this;
        isSurfaceCreated = false;
        isTakingPicture = false;
        isCameraFlashOpen = false;

        picturesPathForSave = new ArrayList<String>();

        cameraActivity = this;
        modeCtrl = new ModeCtrl();
        this.caHandlerManager = new CAHandlerManager(this);

        Intent intent = getIntent();
        recoMode = intent.getIntExtra("recomode", InfoMsg.RECO_MODE_QUICK_RECO);
        LogUtil.i("recomode is " + recoMode);
        capMode = CAPTURE_SINGLE;
        multiCapNum = 0;

        prefs = getSharedPreferences(FLASH_PREFS, Activity.MODE_PRIVATE);
        mFlashMode = prefs.getInt("flashmode", FLASH_OFF);
        LogUtil.i("mFlashMode is " + mFlashMode);
    }

    private void creatDir()
    {
        if (!FileUtil.isExistSDCard()) // 是否存在sdCard
        {
            Toast.makeText(this, "该手机没有SD卡", Toast.LENGTH_LONG).show();
            CameraActivity.this.finish();
        }
        else // 获取sdcard目录
        {
            String sdCardPath = FileUtil.getSDCadrPath();
            // 创建文件的保存路径
            //File f = new File(sdCardPath + "/universcan/MyGallery/未分类");
            File f = new File(sdCardPath + FILE_SAVE_PATH + FILE_SAVE_DIR_NAME);
            if (!f.exists())
            {
                f.mkdirs();
            }

            File f1 = new File(sdCardPath + "/MyCamera");
            if (!f1.exists())
            {
                f1.mkdirs();
            }

            File f2 = new File(sdCardPath + "/MyTemp");
            if (!f2.exists())
            {
                f2.mkdirs();
            }
        }
    }

    private void initView()
    {
        drawManager = (DrawManager) findViewById(R.id.draw_manager);
        mLight = (ImageView) findViewById(R.id.iv_light);
        mLight.setOnClickListener(this);
        mTvLight = (TextView) findViewById(R.id.tv_light);
        mGallery = (ImageView) findViewById(R.id.iv_gallery);
        mGallery.setOnClickListener(this);
        mCapture = (ImageView) findViewById(R.id.iv_capture);
        mCapture.setOnClickListener(this);
        mCancel = (TextView) findViewById(R.id.tv_cancel);
        mCancel.setOnClickListener(this);

        mSubSuperscript = new BadgeView(this, mGallery);
        //mSubSuperscript.setBackgroundResource(R.drawable.badge_ifaux);
        mSubSuperscript.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
        mSubSuperscript.setText("5");
        mSubSuperscript.setAlpha(1f);
        mSubSuperscript.setBadgeMargin(0, 0);
        //mSubSuperscript.show();

        mSingleCap = (TextView) findViewById(R.id.hanvon_camera_scanning) ;
        mSingleCap.setOnClickListener(this);
        mSingleCap.setTextColor(getResources().getColor(R.color.white));
        mMultiCap = (TextView) findViewById(R.id.hanvon_camera_bcard);
        mMultiCap.setOnClickListener(this);


        this.relativeLayoutUserMode = (RelativeLayout) this.findViewById(R.id.hanvon_camera_usermode);

        if (recoMode == InfoMsg.RECO_MODE_QUICK_RECO)
        {
            relativeLayoutUserMode.setVisibility(View.GONE);
        }
        else
        {
            LogUtil.i("show mode choice");
            relativeLayoutUserMode.setVisibility(View.VISIBLE);

        }

        setFlashstateImage();

    }



    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        // TODO Auto-generated method stub
        // DrawManager.setPromptText("touch");
        if ((null != mCameraManager && mCameraManager.isTakePicture()) || ModeCtrl.isBCardScanningStop()
                || ModeCtrl.isBCardScanningStop())
        {
            return false;
        }



        if (event.getAction() == MotionEvent.ACTION_DOWN)
        {

            startX = event.getRawX();

        }
        else if (event.getAction() == MotionEvent.ACTION_MOVE)
        {
			/*
			 * float endX = event.getRawX(); if (Math.abs(endX - startX) > 150)
			 */
            // relativeLayoutUserMode.setTranslationY(endX-startX);
			/*
			 * if (Math.abs(endX - startX) > 150) { if (endX > startX) { if
			 * (ModeCtrl.getUserMode() == UserMode.SHOPPING) {
			 * this.userModeSwitchToScanning(); }else if(ModeCtrl.getUserMode()
			 * == UserMode.SCANNING){ this.userModeSwitchToBCard(); }else
			 * if(ModeCtrl.getUserMode() == UserMode.BCARD){
			 * //this.userModeSwitchToBCard(); }
			 *
			 * } else {
			 *
			 * if (ModeCtrl.getUserMode() == UserMode.SHOPPING) {
			 * //this.userModeSwitchToBCard(); }else if(ModeCtrl.getUserMode()
			 * == UserMode.SCANNING){ this.userModeSwitchToImageShopping();
			 * }else if(ModeCtrl.getUserMode() == UserMode.BCARD){
			 * this.userModeSwitchToScanning(); } } return true; }
			 */
        }
        else if (event.getAction() == MotionEvent.ACTION_UP)
        {

            float endX = event.getRawX();
            if (Math.abs(endX - startX) > 150) {
                int step = DensityUtil.dip2px(this, 50);
                if (endX > startX)
                {
                    relativeLayoutUserMode.setTranslationX(0);
					/*
					 * if (ModeCtrl.getUserMode() == UserMode.SHOPPING)
					 * {
					 * this.userModeSwitchToBCard(); } else
					 */
                    if (ModeCtrl.getUserMode() == UserMode.SCANNING)
                    {
                        //this.userModeSwitchToBCard();
                    }
                    else if (ModeCtrl.getUserMode() == UserMode.BCARD)
                    {
                        // this.userModeSwitchToScanning();
                    }

                }
                else
                {

                    relativeLayoutUserMode.setTranslationX((-step));
					/*
					 * if (ModeCtrl.getUserMode() == UserMode.SHOPPING) { //
					 * this.userModeSwitchToScanning(); } else
					 */
                    if (ModeCtrl.getUserMode() == UserMode.SCANNING)
                    {
                        // this.userModeSwitchToBCard();
                    }
                    else if (ModeCtrl.getUserMode() == UserMode.BCARD)
                    {
                        //this.userModeSwitchToScanning();
                    }
                }
                return true;
            }
            else if (ModeCtrl.getUserMode() == UserMode.BCARD)
            {
                if (mCameraManager != null)
                {
                    mCameraManager.setOnTouchFocus(event);
                }
            }

        }

		/*
		 * if (onTouchManager != null) { if (!this.isScanningStop) {
		 * onTouchManager.onTouchEvent(event); }
		 *
		 * }
		 */

        return super.onTouchEvent(event);

    }

    @Override
    protected void onDestroy()
    {
        // TODO Auto-generated method stub
        super.onDestroy();
        Log.i(TAG, "<<<<<<<<<<<<<<<<<<<<<<<<<<<---onDestroy--->>>>>>>>>>>>>>>>>>");
    }

    private void reSetCamera()
    {
        LogUtil.i("resetCamera");


        isTakingPicture = false;

        if (mCameraManager != null)
        {
            //this.releaseScanningResource();
            //this.releaseBCardResource();
            mCameraManager.release();
            mCameraManager = null;
            mPreviewDataManager.release();
            mPreviewDataManager = null;
            if (!this.isSurfaceCreated)
            {
                SurfaceView surfaceView = (SurfaceView) findViewById(R.id.sv_preview);
                SurfaceHolder surfaceHolder = surfaceView.getHolder();
                surfaceHolder.removeCallback(this);
            }
        }
    }

    @Override
    protected void onPause()
    {
        reSetCamera();
        super.onPause();
        System.gc();
    }


    private void initCamera()
    {
        isTakingPicture = false;
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.sv_preview);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        mPreviewDataManager = new PreviewDataManager();
        mCameraManager = new CameraManager(this, mPreviewDataManager);
        if (this.isSurfaceCreated)
        {
            if (null != mCameraManager)
            {
                mCameraManager.initCamera(0, surfaceHolder);
            }
        }
        else
        {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        LogUtil.i("on Resume  initCamera");
        initCamera();

        Intent intent = this.getIntent();
        String msg = intent.getStringExtra("message");
        if (null != msg)
        {
            LogUtil.i("!!!!! msg is " + msg);
        }
        if (null != msg && msg.equals("recapture"))
        {
            LogUtil.i("receive recapture msg!!!");
            resetMultiCapState();
        }

        if (null !=msg && msg.equals("userdelall"))
        {
            LogUtil.i("receive userdelall msg !!!!");
            resetMultiCapStateByUerDel();
        }

        if (null !=msg && msg.equals("backmsg"))
        {
            LogUtil.i("recievie backmsg in fucn onResume!!!!");

            picturesPathForSave = intent.getStringArrayListExtra("filelist");

            if (null == picturesPathForSave)
            {
                LogUtil.i("receive null !!!!!1");
                return;
            }

            for (int i =0; i < picturesPathForSave.size(); i++)
            {
                LogUtil.i("file path " + i + " is " + picturesPathForSave.get(i));
            }
            resetMultiCapStateByBackBtn();
        }


    }


    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        setIntent(intent);
        LogUtil.i("onNewIntent called !!!!");
        //processExtraData();
    }


    private void processExtraData()
    {
        LogUtil.i("processExtraData called !!!");
        Intent intent = this.getIntent();
        String msg = intent.getStringExtra("message");

        if (null !=msg && msg.equals("userdelall"))
        {
            LogUtil.i("receive userdelall msg !!!!");
            resetMultiCapStateByUerDel();
        }
    }

    public void showNoRightOpenCamera()
    {
        new CustomDialog.Builder(CameraActivity.this)
                .setTitle("摄像头打开失败")
                .setMessage("请在设置选项中开启摄像头权限")
                .setPositiveButton(R.string.bc_str_confirm,
                        new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                LogUtil.i("!!!!! kill process");
                                Intent sysIntent = new Intent(CameraActivity.this, MainActivity.class);
                                sysIntent.putExtra("key", "value");
                                //sysIntent.putExtra("exitProgram", "exitProgram");
                                Bundle bundle = new Bundle();
                                //bundle.putString("key", "value");
                                sysIntent.putExtra("msg", bundle);
                                startActivity(sysIntent);
                                CameraActivity.this.finish();
                                //android.os.Process.killProcess(android.os.Process.myPid());
                                //System.exit(0);
                            }
                        }).show();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        boolean isCameraOnped = false;
       LogUtil.i("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$surfaceCreated");
        if (holder == null)
        {
            Log.e(TAG, "*** WARNING *** surfaceCreated() gave us a null surface!");
        }
        if (!this.isSurfaceCreated)
        {
            isSurfaceCreated = true;
            if (Camera.getNumberOfCameras() > 0)
            {
                if (null != mCameraManager)
                {
                    try
                    {
                        isCameraOnped = mCameraManager.initCamera(0, holder);
                    }
                    catch (RuntimeException e)
                    {
                        LogUtil.i("!!!! open camera error !!!!!");
                        showNoRightOpenCamera();
                    }
                }

                if (mCameraManager.isOpenSuccess())
                {
                    LogUtil.i("setFocusMode!!!!!");
                    mCameraManager.setTouchView(50, 100);
                    mCameraManager.setFocusModeAutoCycle(1750); //modify by at2015-07-01
                    mCameraManager.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                    //mCameraManager.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                    //add 2016-05-31

                    //LogUtil.i("set preview size and picture size to 1920 x 1080");
                    Camera.Parameters parameters = mCameraManager.getCameraParameters();

                    printSupportFocusMode(parameters);

                    printSupportFlashMode(parameters);

                    List<Size> picsizes = printSupportPictureSize(parameters);
                    List<Size> previewsizes = printSupportPreviewSize(parameters);


                    Size previewS = CameraPicturSize.getInstance().getPreviewSize(previewsizes, 800);

                    if (null != previewS)
                    {
                        LogUtil.i("!!!!! setPreviewSize to " + previewS.width + " * " + previewS.height);
                        parameters.setPreviewSize(previewS.width, previewS.height);
                    }
                    else
                    {
                        LogUtil.i("!!! getPreview size errr, use default !!!!!!!");
                    }



                    SharedPreferences sp = getSharedPreferences("cameraSize", MODE_PRIVATE);
                    int width = sp.getInt("width", 0);
                    int height = sp.getInt("height", 0);
                    if (width == 0 || height == 0)
                    {
                        Size pictureS = CameraPicturSize.getInstance().getPictureSize(picsizes, 800);

                        if (null != pictureS)
                        {
                            parameters.setPictureSize(pictureS.width, pictureS.height);
                            LogUtil.i("!!!!! setPictureSize to " + pictureS.width + " * " + pictureS.height);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putInt("width", pictureS.width);
                            editor.putInt("height", pictureS.height);
                            editor.commit();
                        }
                        else
                        {
                            LogUtil.i("!!! getPicture size errr, use default !!!!!!!");
                        }
                    }
                    else
                    {
                        LogUtil.i("Use saved width and height to set pic size: " + width + " * " + height);
                        parameters.setPictureSize(width, height);
                    }
                    mCameraManager.setCameraParameters(parameters);
                    //add end
                    //setFlashAuto(); //fjm add
                    /*
                    switch (ModeCtrl.getUserMode())
                    {
                        case BCARD:
                            mCameraManager.setFocusModeAutoCycle(1750);
                            break;
                        case SCANNING:
                            mCameraManager.setFocusModeAutoCycle(1000);
                            break;
                        case SHOPPING:
                            mCameraManager.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                            break;
                        default:
                            break;

                    }
                    */

                }
            }

        }
    }

    /*
    public void Size getCamerPicSize()
    {
        SharedPreferences sp = getPreferences("cameraSize", MODE_PRIVATE);
        int width = sp.getInt("width", 0);

        Size pic = new Size(0, 0);
    }
    */

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {
       LogUtil.i( "$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$surfaceChanged");
        // TODO Auto-generated method stub

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
        // TODO Auto-generated method stub
        isSurfaceCreated = false;
        LogUtil.i( "$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$surfaceDestroyed");
    }



    @Override
    public void onPictureTaken(byte[] data, Camera camera)
    {
        try
        {
            LogUtil.i("onPictureTaken ");
            LogUtil.i("set picture size:"
                            + String.valueOf(this.mCameraManager.getPictureSize().width) + "_"
                            + String.valueOf(this.mCameraManager.getPictureSize().height));
            Camera.Parameters param = this.mCameraManager.getCameraParameters();
            LogUtil.i("real picture size:" + String.valueOf(param.getPictureSize().width) + "_"
                            + String.valueOf(param.getPictureSize().height));

            //printSupportPreviewSize(mCameraManager.getCameraParameters());
            //printSupportFocusMode(mCameraManager.getCameraParameters());
            //printSupportPictureSize(mCameraManager.getCameraParameters());

            if ((recoMode == InfoMsg.RECO_MODE_EXACT_RECO) || (capMode == CAPTURE_MULTI))
            {
                reSetCamera();
            }

            new ThreadSaveJPG(cameraActivity, data).start();
        }
        catch (RuntimeException e)
        {
            LogUtil.i("catche run time exception");
            e.printStackTrace();
        }
        /*
        switch (ModeCtrl.getUserMode()) {
            case SHOPPING:

                if (new ConnectionDetector(this).isConnectingTOInternet()) {
                    new ThreadImageShoppingProcess(data, caHandlerManager).start();
                } else {
                    this.messageToToastMessage("网络连接异常，请检查网络连接");
                    this.messageToRestartPreview();
                }

                // new ThreadBitmapDecode(this, data).start();
                break;
            case BCARD:
                this.messageToTextViewUpData("");
                if (null != ThreadBCardScanning.getThreadBCardScanning()) {
                    ThreadBCardScanning.getThreadBCardScanning().setQuit(true);
                }
                new ThreadBCardRecognize(cameraActivity, data).start();
                break;
            default:
                break;
        }
        */
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.iv_capture:
                if (capMode == CAPTURE_MULTI && multiCapNum >= 9)
                {
                    Toast.makeText(CameraActivity.this, "最多只能连拍9张", Toast.LENGTH_SHORT).show();
                    return;
                }
                StatisticsUtils.IncreaseCaptureBtn();
                requestTakePicture();
                break;

            case R.id.tv_cancel:
                processBtnPress();

                break;

            case R.id.iv_gallery:
                StatisticsUtils.IncreaseInsertBtn();
                processBtnGallery();
                /*
                Log.d(TAG, "start ChooseMorPictureActivity");
                Intent sysIntent = new Intent();
                sysIntent.setClass(CameraActivity.this, ChooseMorePicturesActivity.class);
                sysIntent.putExtra("parentActivity", "cameraActivity");
                sysIntent.putExtra("recomode", recoMode);
                sysIntent.putExtra("capmode", capMode);
                startActivity(sysIntent);
                //startActivityForResult(sysIntent, REQ_SYS_PICTURE);
                */
                break;
            case R.id.iv_light:
                StatisticsUtils.IncreaseFlashBtn();
                setFlashState();
            break;

            case R.id.hanvon_camera_bcard:
                StatisticsUtils.IncreaseSerialBtn();
                switchCapMode();

                break;

            case R.id.hanvon_camera_scanning:
                StatisticsUtils.IncreaseSingleBtn();
                switchCapMode();
                break;
        }
    }

    private void processBtnGallery()
    {
        LogUtil.i("!!!!! processBtnGallery");
        if ((recoMode == InfoMsg.RECO_MODE_EXACT_RECO) && (capMode == CAPTURE_MULTI) && (multiCapNum > 0))
        {
            //multiCapNum = 0;
            //mSubSuperscript.hide();
            Intent intent = new Intent(this, ChooseMorePicturesActivity.class);
            intent.putExtra("entry", "finish_btn");
            Bundle bundle = new Bundle();
            bundle.putStringArrayList("pictures", picturesPathForSave);
            intent.putExtra("bundle", bundle);
            intent.putExtra("recomode", recoMode);
            intent.putExtra("capmode", capMode);
            intent.putExtra("parentActivity", "CameraActivity");
            startActivity(intent);
        }
        else
        {
            Log.d(TAG, "start ChooseMorPictureActivity");
            Intent sysIntent = new Intent();
            sysIntent.setClass(CameraActivity.this, ChooseMorePicturesActivity.class);
            sysIntent.putExtra("parentActivity", "cameraActivity");
            sysIntent.putExtra("recomode", recoMode);
            sysIntent.putExtra("capmode", capMode);
            startActivity(sysIntent);
        }
    }

    private void processBtnPress()
    {
        LogUtil.i("!!!!!!processBtnPress");
        if ((recoMode == InfoMsg.RECO_MODE_EXACT_RECO) && (capMode == CAPTURE_MULTI) && (multiCapNum > 0))
        {
            LogUtil.i("!!!!! enter gallery");
            //multiCapNum = 0;
            //mSubSuperscript.hide();
            Intent intent = new Intent(this, ChooseMorePicturesActivity.class);
            intent.putExtra("entry", "finish_btn");
            Bundle bundle = new Bundle();
            bundle.putStringArrayList("pictures", picturesPathForSave);
            intent.putExtra("bundle", bundle);
            intent.putExtra("recomode", recoMode);
            intent.putExtra("capmode", capMode);
            intent.putExtra("parentActivity", "CameraActivity");
            startActivity(intent);
        }
        else
        {
            this.finish();
        }
    }

    void switchCapMode()
    {
        restartPreviewAfterTackpicture();
        isTakingPicture = false;

        LogUtil.i("switchCapMode!!!!!");
        if (capMode == CAPTURE_SINGLE)
        {
            capMode = CAPTURE_MULTI;
            mSingleCap.setTextColor(getResources().getColor(R.color.silver));
            mMultiCap.setTextColor(getResources().getColor(R.color.white));
            if (multiCapNum != 0)
            {
                mSubSuperscript.show();
                mCancel.setText(getResources().getText(R.string.wb_str_done));
            }
            else
            {
                mCancel.setText(getResources().getText(R.string.bc_str_cancle));
            }

            //mSubSuperscript.show();
        }
        else
        {
            capMode = CAPTURE_SINGLE;
            mMultiCap.setTextColor(getResources().getColor(R.color.silver));
            mSingleCap.setTextColor(getResources().getColor(R.color.white));
            mCancel.setText(getResources().getText(R.string.bc_str_cancle));
            mSubSuperscript.hide();
        }

    }

    private void turnOffFlash()
    {
        if (mCameraManager != null)
        {
            //mCameraManager.setFlashOff();
            Camera.Parameters param = mCameraManager.getCameraParameters();
            mCameraManager.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            //param.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        }
        mLight.setImageResource(R.mipmap.camera_flash_off);
    }

    private void turnOnFlash()
    {
        if (mCameraManager != null)
        {
            Camera.Parameters param = mCameraManager.getCameraParameters();
            //param.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
            mCameraManager.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
        }
        mLight.setImageResource(R.mipmap.camera_flash_on);
    }

    private void setFlashAuto()
    {
        if (mCameraManager != null)
        {
            Camera.Parameters param = mCameraManager.getCameraParameters();
            //param.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
            mCameraManager.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
        }
        mLight.setImageResource(R.mipmap.camera_flash_on);
    }

    private void setFlashstateImage()
    {
        switch (mFlashMode)
        {
            case FLASH_ON:
                mLight.setImageResource(R.mipmap.camera_flash_on);
                mTvLight.setText(R.string.camera_light_on);
                break;

            case FLASH_OFF:
                mLight.setImageResource(R.mipmap.camera_flash_off);
                mTvLight.setText(R.string.camera_light_off);
                break;

        }
    }

    private void setFlashState()
    {
        /*
        if (mFlashMode < 3)
        {
            mFlashMode++;
        }
        else if (mFlashMode == 3)
        {
            mFlashMode = 1;
        }
        */

        if (mFlashMode == FLASH_ON)
        {
            mFlashMode = FLASH_OFF;
        }
        else
        {
            mFlashMode = FLASH_ON;
        }


        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("flashmode", mFlashMode);
        editor.commit();

        setFlashstateImage();

        /*
        if (isCameraFlashOpen)
        {
            turnOffFlash();
            isCameraFlashOpen = false;

        }
        else
        {
            turnOnFlash();
            isCameraFlashOpen = true;
        }*/

    }

    /*通过equalRate(Size s, float rate)保证Size的长宽比率。
      一般而言这个比率为1.333/1.7777即通常说的4:3和16:9比率。
    */
    public boolean equalRate(Size s, float rate)
    {
        float r = (float)(s.width)/(float)(s.height);
        if(Math.abs(r - rate) <= 0.2)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    /**打印支持的previewSizes
     * @param params
     */
    public  List<Size> printSupportPreviewSize(Camera.Parameters params)
    {
        LogUtil.i("SupportPreviewSize!!!!!!");
        List<Size> previewSizes = params.getSupportedPreviewSizes();
        for(int i=0; i< previewSizes.size(); i++)
        {
            Size size = previewSizes.get(i);
            if (equalRate(size, 1.33f))
            {
                LogUtil.i("4:3 previewSizes:width = " + size.width + " height = " + size.height);
            }
            else
            {
                LogUtil.i("16:9 previewSizes:width = " + size.width + " height = " + size.height);
            }
        }
        return previewSizes;
    }

    /**打印支持的pictureSizes
     * @param params
     */
    public  List<Size> printSupportPictureSize(Camera.Parameters params)
    {
        LogUtil.i("SupportPictureSize!!!!!!");
        List<Size> pictureSizes = params.getSupportedPictureSizes();
        for(int i=0; i< pictureSizes.size(); i++)
        {
            Size size = pictureSizes.get(i);
            if (equalRate(size, 1.33f))
            {
                LogUtil.i("4:3 pictureSizes:width = " + size.width + " height = " + size.height);
            }
            else
            {
                LogUtil.i("16:9 pictureSizes:width = " + size.width + " height = " + size.height);
            }
        }

        return pictureSizes;
    }
    /**打印支持的聚焦模式
     * @param params
     */
    public void printSupportFocusMode(Camera.Parameters params)
    {
        List<String> focusModes = params.getSupportedFocusModes();
        for(String mode : focusModes)
        {
            LogUtil.i("Support focusModes--" + mode);
        }
    }


    public void printSupportFlashMode(Camera.Parameters params)
    {
        List<String> flashModes = params.getSupportedFlashModes();
        for(String mode : flashModes)
        {
            LogUtil.i("Support flashModes--" + mode);
        }
    }


    public void requestTakePicture()
    {
        if (isTakingPicture)
        {
            LogUtil.i("taking pic , return");
            return;
        }
        isTakingPicture = true;

        LogUtil.i("requestTakePicture");

        if (mCameraManager != null)
        {
            if (!mCameraManager.isTakePicture())
            {
                LogUtil.i("not takeing picture");
                mCameraManager.setTakePicture(true);

                if (mCameraManager.takePicture(true, false, this, true))
                {
                   LogUtil.i("captuer !!!!!!!");
                }
                else
                {
                    //isTakingPicture = false;
                    mCameraManager.setTakePicture(false);
                    LogUtil.i("capteru failed !!!1");
                }
                //fjm add
                LogUtil.i("open flash light !!!!1");
                Camera.Parameters parameters = mCameraManager.getCameraParameters();
                if (mFlashMode == FLASH_ON)
                {
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
                }
                else if(mFlashMode == FLASH_AUTO)
                {
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
                }
                else
                {
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                }
                mCameraManager.setCameraParameters(parameters);
                //fjm add end
            }
            else
            {
                LogUtil.i("taking pic!!!!!");
            }
        }
    }

    public void messageToRestartPreview()
    {
        if (caHandlerManager != null)
        {
            LogUtil.i("send restart preview msg");
            Message message = Message.obtain(caHandlerManager,
                    CAHandlerManager.RESTART_PREVIEW);
            caHandlerManager.sendMessageDelayed(message, 10);
        }

    }

    public void messageToCrop(String path)
    {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.BUCKET_DISPLAY_NAME, FILE_SAVE_DIR_NAME);//目录名
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.Images.Media.DATA, path);
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        CameraActivity.this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        if (Build.VERSION.SDK_INT < 19) //API 19 以前的
        {
            //CameraActivity.this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + Environment.getExternalStorageDirectory()+"/universcan/MyGallery/未分类")));
            CameraActivity.this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + Environment.getExternalStorageDirectory()+FILE_SAVE_PATH+FILE_SAVE_DIR_NAME)));

        }
        else
        {
            //MediaScannerConnection.scanFile(CameraActivity.this, new String[]{Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath() + "/" + "universcan/MyGallery/未分类"}, null, null);
            MediaScannerConnection.scanFile(CameraActivity.this, new String[]{Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath()  + FILE_SAVE_PATH + FILE_SAVE_DIR_NAME}, null, null);
        }

        if (caHandlerManager != null)
        {
            Message message = Message.obtain(caHandlerManager, CAHandlerManager.JPG_SAVE_COMPLETE);

            Bundle b = new Bundle();
            b.putString("filepath", path);
            message.setData(b);
            caHandlerManager.sendMessage(message);
        }
    }

    public void jpgSaveComplete(String path)
    {
        if ((recoMode == InfoMsg.RECO_MODE_QUICK_RECO))
        {
            LogUtil.i("in func jpgSaveComplete, path is " + path);
            Intent intent = new Intent();
            intent.setClass(mContext, CropActivity.class);
            intent.putExtra("parentActivity", "CameraActivity");
            intent.putExtra("path", path);
            intent.putExtra("recomode", recoMode);

            LogUtil.i("Start CropActivity !!!1");
            startActivity(intent);
            LogUtil.i("set isTakingPicture false");
            isTakingPicture = false;
        }
        else if (recoMode == InfoMsg.RECO_MODE_EXACT_RECO && (capMode == CAPTURE_SINGLE))
        {
            Log.i(TAG, "in func jpgSaveComplete, path is " + path);
            Intent intent = new Intent();
            intent.setClass(mContext, CropActivity.class);
            intent.putExtra("parentActivity", "CameraActivity");
            intent.putExtra("path", path);
            intent.putExtra("recomode", recoMode);
            isTakingPicture = false;
            LogUtil.i("Start CropActivity !!!2");
            startActivity(intent);
        }
        else if (recoMode == InfoMsg.RECO_MODE_EXACT_RECO && (capMode == CAPTURE_MULTI))
        {
            multiCapNum++;
            mSubSuperscript.setText(String.valueOf(multiCapNum));
            mSubSuperscript.show();

            LogUtil.i("current path is "+ path);
            picturesPathForSave.add(path);

            //reSetCamera();
            try
            {
                initCamera();
            }
            catch (Exception e)
            {
                LogUtil.i("init camera error !!!");
            }

            restartPreviewAfterTackpicture();

            isTakingPicture = false;

            if (multiCapNum > 0)
            {
                LogUtil.i("set state !!!!!!!!!! to done !!!!!!");
                mCancel.setText(R.string.wb_str_done);
            }

        }
    }


    public void restartPreviewAfterTackpicture()
    {
        if (this.mCameraManager != null)
        {
            LogUtil.i("restartPreviewAfterTakpicture");
            mCameraManager.setTakePicture(false);
            mCameraManager.restartPreviewAfterTakePiture();
        }
        //this.restartUserMode();

    }

    private void deletePictures()
    {
        int size = picturesPathForSave.size();

        for (int i = 0; i < size; i++)
        {
            String filepathToDel = picturesPathForSave.get(i);
            Uri imageuri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            ContentResolver mContentResolver = CameraActivity.this.getContentResolver();
            String where = MediaStore.Images.Media.DATA + "='" + filepathToDel + "'";
            LogUtil.i("wherw is " + where);
            mContentResolver.delete(imageuri, where, null);

            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            File file = new File(filepathToDel);
            Uri uri = Uri.fromFile(file);
            intent.setData(uri);
            CameraActivity.this.sendBroadcast(intent);
        }



    }


    private void resetMultiCapStateByBackBtn()
    {
        LogUtil.i("call resetMultiCapStatByBackBtn");
        multiCapNum = picturesPathForSave.size();
        mSubSuperscript.setText(String.valueOf(multiCapNum));

        recoMode = InfoMsg.RECO_MODE_QUICK_RECO;
        switchCapMode();
    }

    private void resetMultiCapStateByUerDel()
    {
        LogUtil.i("resetMultiCapStateByUerDel called here");
        multiCapNum = 0;
        mSubSuperscript.hide();
        picturesPathForSave.clear();

        relativeLayoutUserMode.setVisibility(View.VISIBLE);
        recoMode = InfoMsg.RECO_MODE_EXACT_RECO;
        switchCapMode();
    }

    private void resetMultiCapState()
    {
        LogUtil.i("call resetMultiCapState");
        deletePictures();

        multiCapNum = 0;
        mSubSuperscript.hide();
        picturesPathForSave.clear();

        relativeLayoutUserMode.setVisibility(View.VISIBLE);
        recoMode = InfoMsg.RECO_MODE_EXACT_RECO;
        switchCapMode();
        //capMode = CAPTURE_MULTI;
    }

    private void getDisplayMetrics()
    {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        //intScreenWidth = dm.widthPixels;
        //intScreenHeight = dm.heightPixels;
        //Log.i(TAG, Integer.toString(intScreenWidth));
    }

    /*
    @Override
    protected void setStatusBar() {
        mColor = getResources().getColor(R.color.activity_default_color);
        StatusBarUtil.setColor(this, mColor);
    }
    */

}
