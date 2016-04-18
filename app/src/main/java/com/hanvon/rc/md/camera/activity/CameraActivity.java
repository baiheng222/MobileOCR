package com.hanvon.rc.md.camera.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
//import android.hardware.camera2.CameraManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.provider.MediaStore;
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
import com.hanvon.rc.bcard.ChooseMorePicturesActivity;
import com.hanvon.rc.md.camera.CameraManager;
import com.hanvon.rc.md.camera.PreviewDataManager;
import com.hanvon.rc.md.camera.draw.DrawManager;
import com.hanvon.rc.presentation.CropActivity;
import com.hanvon.rc.utils.FileUtil;
import com.hanvon.rc.md.camera.DensityUtil;
import com.hanvon.rc.md.camera.activity.ModeCtrl.UserMode;

import java.io.File;

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
    private ImageView mGallery;
    private ImageView mCapture;
    private TextView mCancel;

    private RelativeLayout relativeLayoutUserMode;

    private SurfaceView mSurfaceView;
    private DrawManager drawManager;

    private CAHandlerManager caHandlerManager;


    private float startX;

    private boolean isSurfaceCreated;


    private ModeCtrl modeCtrl;

    public static CameraActivity cameraActivity;
    public static CameraActivity getCameraActivity() {
        return cameraActivity;
    }

    public static final String FILE_SAVE_DIR_NAME = "savedpic";
    public static final String FILE_SAVE_PATH = "/MobileOCR/";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.camera);


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

        mContext = this;
        isSurfaceCreated = false;

        cameraActivity = this;
        modeCtrl = new ModeCtrl();
        this.caHandlerManager = new CAHandlerManager(this);


        initView();
    }

    private void initView()
    {
        drawManager = (DrawManager) findViewById(R.id.draw_manager);
        mLight = (ImageView) findViewById(R.id.iv_light);
        mGallery = (ImageView) findViewById(R.id.iv_gallery);
        mGallery.setOnClickListener(this);
        mCapture = (ImageView) findViewById(R.id.iv_capture);
        mCapture.setOnClickListener(this);
        mCancel = (TextView) findViewById(R.id.tv_cancel);
        mCancel.setOnClickListener(this);

        this.relativeLayoutUserMode = (RelativeLayout) this
                .findViewById(R.id.hanvon_camera_usermode);


    }



    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        // TODO Auto-generated method stub
        // DrawManager.setPromptText("touch");
        if (mCameraManager.isTakePicture() || ModeCtrl.isBCardScanningStop()
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

    @Override
    protected void onPause()
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
        super.onPause();
        Log.i(TAG, "<<<<<<<<<<<<<<<<<<<<<<<<<<<---onPause--->>>>>>>>>>>>>>>>>>");

        System.gc();


    }

    @Override
    protected void onResume()
    {
        super.onResume();

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
    public void surfaceCreated(SurfaceHolder holder)
    {
        // TODO Auto-generated method stub
        Log.i(TAG, "$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$surfaceCreated");
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
                    mCameraManager.initCamera(0, holder);
                }

                if (mCameraManager.isOpenSuccess())
                {
                    mCameraManager.setTouchView(50, 100);
                    mCameraManager.setFocusModeAutoCycle(1750);
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

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {
        Log.i(TAG, "$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$surfaceChanged");
        // TODO Auto-generated method stub

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
        // TODO Auto-generated method stub
        isSurfaceCreated = false;
        Log.i(TAG, "$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$surfaceDestroyed");
    }



    @Override
    public void onPictureTaken(byte[] data, Camera camera)
    {
        // TODO Auto-generated method stub
        Log.i(TAG,
                "set picture size:"
                        + String.valueOf(this.mCameraManager
                        .getPictureSize().width)
                        + "_"
                        + String.valueOf(this.mCameraManager
                        .getPictureSize().height));
        Camera.Parameters param = this.mCameraManager.getCameraParameters();
        Log.i(TAG,
                "real picture size:"
                        + String.valueOf(param.getPictureSize().width) + "_"
                        + String.valueOf(param.getPictureSize().height));

        new ThreadSaveJPG(cameraActivity, data).start();
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
                requestTakePicture();
                break;

            case R.id.tv_cancel:
                this.finish();
                break;

            case R.id.iv_gallery:
                Log.d(TAG, "start ChooseMorPictureActivity");
                Intent sysIntent = new Intent();
                sysIntent.setClass(CameraActivity.this, ChooseMorePicturesActivity.class);
                sysIntent.putExtra("parentActivity", "cameraActivity");
                startActivity(sysIntent);
                //startActivityForResult(sysIntent, REQ_SYS_PICTURE);
                break;
        }
    }


    public void requestTakePicture()
    {
        Log.d(TAG, "requestTakePicture");
        if (mCameraManager != null)
        {
            if (!mCameraManager.isTakePicture())
            {
                Log.d(TAG, "not takeing picture");
                mCameraManager.setTakePicture(true);

                if (mCameraManager.takePicture(true, false, this, true))
                {
                    Log.d(TAG, "captuer !!!!!!!");
                }
                else
                {
                    Log.d(TAG, "capteru failed !!!1");
                }
                /*
                switch (ModeCtrl.getUserMode())
                {
                    case SHOPPING:
                        this.messageToInvisbleButton();
                        this.messageToTextViewUpData("正在拍照,请保持稳定...");
                        if (cameraManager.takePicture(true, false, this, true)) {

                        } else {
                            this.messageToSetVisbleButton(UserMode.SHOPPING);
                            this.messageToTextViewUpData("");
                            cameraManager.setTakePicture(false);
                            this.messageToRestartUserMode();
                        }

                        break;
                    case BCARD:
                        if (!ModeCtrl.isBCardScanningStop()) {
                            ModeCtrl.setBCardScanningStop(true);
                            threadBCardScanning.setQuit(true);
                            threadBCardScanning = null;
                        }
                        this.messageToInvisbleButton();
                        this.messageToTextViewUpData("正在拍照,请保持稳定...");
                        if (cameraManager.takePicture(true, false, this, true)) {

                        } else {
                            //this.messageToSetVisbleButton(UserMode.BCARD);
                            this.messageToTextViewUpData("");
                            cameraManager.setTakePicture(false);
                            this.messageToRestartUserMode();
                        }
                        Log.i(TAG, "cameraManager.tackPicture");

                        break;
                    default:
                        break;
                }
                */
            }
            else
            {
                Log.d(TAG, "null camera");
            }
        }
    }

    public void messageToRestartPreview()
    {
        if (caHandlerManager != null) {
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
            Message message = Message.obtain(caHandlerManager,
                    CAHandlerManager.JPG_SAVE_COMPLETE);

            Bundle b = new Bundle();
            b.putString("filepath", path);
            message.setData(b);
            caHandlerManager.sendMessage(message);
        }
    }

    public void jpgSaveComplete(String path)
    {
        Log.i(TAG, "in func jpgSaveComplete, path is " + path);
        Intent intent = new Intent();
        intent.setClass(mContext, CropActivity.class);
        intent.putExtra("parentActivity", "CameraActivity");
        intent.putExtra("path", path);

        Log.i(TAG, "Start CropActivity !!!1");
        startActivity(intent);
    }

}
