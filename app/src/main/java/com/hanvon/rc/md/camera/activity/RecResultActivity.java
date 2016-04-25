package com.hanvon.rc.md.camera.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hanvon.rc.R;
import com.hanvon.rc.utils.CustomDialog;
import com.hanvon.rc.utils.HvnCloudManager;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.InvalidMarkException;
import java.util.ArrayList;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

/**
 * Created by baiheng222 on 16-4-5.
 */


public class RecResultActivity extends Activity implements View.OnClickListener
{

    private static final String TAG = "RecResultActivity";
    private ImageView ivBack;
    private TextView tvSave;

    private ImageView tvShare;
    private ImageView tvCopy;
    private ImageView tvExact;
    private ImageView tvDel;
    private TextView tvTitel;

    private EditText etResult;

    private String recResult;



    private final int DLG_RETURN_YES = 0;
    private final int DLG_RETURN_NO = 1;
    private final int DLG_RETURN_OK = 2;
    private final int DLG_RETURN_CANCEL = 3;
    private final int DLG_RETURN_INIT = -1;

    private int dlgReturn = DLG_RETURN_INIT;



    private ProgressDialog pd;
    private String  strLinkPath = null;
    private Bitmap bitmapLaunch;
    private Boolean bShareClick = false;

    private final static int UPLLOAD_FILE_CLOUD_SUCCESS = 5;
    private final static int UPLLOAD_FILE_CLOUD_FAIL = 6;


    private Handler handler = new Handler()
    {
        @SuppressLint("ShowToast")
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {

                case UPLLOAD_FILE_CLOUD_SUCCESS:
                    pd.dismiss();
                    showShare();

                    break;
                case UPLLOAD_FILE_CLOUD_FAIL:
                    pd.dismiss();
                    Toast.makeText(RecResultActivity.this, "获取链接失败，不能分享!", Toast.LENGTH_SHORT).show();
                    bShareClick = false;

                    break;

                default:
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rec_result_activity);

        initData();

        initView();

        setListener();

    }

    private void initView()
    {
        ivBack = (ImageView) findViewById(R.id.iv_back);
        tvTitel = (TextView) findViewById(R.id.tv_title);
        tvSave = (TextView) findViewById(R.id.tv_save);

        tvShare = (ImageView) findViewById(R.id.iv_share);
        tvCopy = (ImageView) findViewById(R.id.iv_copy);
        tvExact = (ImageView) findViewById(R.id.iv_exact);
        tvDel = (ImageView) findViewById(R.id.iv_del);

        etResult = (EditText) findViewById(R.id.et_result);
        if (recResult != null)
        {
            etResult.setText(recResult);
        }
    }

    private void setListener()
    {
        ivBack.setOnClickListener(this);
        tvSave.setOnClickListener(this);
        tvShare.setOnClickListener(this);
        tvCopy.setOnClickListener(this);
        tvExact.setOnClickListener(this);
        tvDel.setOnClickListener(this);
    }

    private void initData()
    {
        Intent intent = getIntent();
        if (intent != null)
        {
            Bundle bundle = intent.getExtras();
            recResult = bundle.getString("textResult");
            if (null != recResult)
            {
                Log.d(TAG, "recResult is " + recResult);
            }
        }
    }


    private void showSave()
    {
        showDlg("Save file?");

    }

    private void showDel()
    {
        new CustomDialog.Builder(RecResultActivity.this)
                .setTitle("")
                .setMessage(R.string.rec_ret_del)
                .setNegativeButton(R.string.bc_str_cancle,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(
                                    DialogInterface dialog,
                                    int which) {

                            }
                        })
                .setPositiveButton(R.string.bc_str_confirm,
                        new DialogInterface.OnClickListener()
                        {

                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                overridePendingTransition(R.anim.act_delete_enter_anim,
                                        R.anim.act_delete_exit_anim);
                                RecResultActivity.this.finish();
                            }
                        }).show();

    }

    private void showDlg(String title)
    {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage(title);
        dialog.setNegativeButton(R.string.dlg_str_no, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dlgReturn = DLG_RETURN_NO;
            }
        });

        dialog.setPositiveButton(R.string.dlg_str_yes, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dlgReturn = DLG_RETURN_YES;
            }
        });

        dialog.show();
    }

    @Override
    public void onClick(View view)
    {
        switch(view.getId())
        {
            case R.id.iv_back:
                break;

            case R.id.tv_save:
                showSave();
                break;

            case R.id.iv_share:
                if (!bShareClick)
                {
                    pd = ProgressDialog.show(RecResultActivity.this, "", getString(R.string.link_mess));
                    shareRecoResult();
                }
                break;

            case R.id.iv_copy:
                break;

            case R.id.iv_del:
                showDel();
                break;

            case R.id.iv_exact:
                break;
        }
    }


    private void showShare() {
        ShareSDK.initSDK(this);
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        Log.i(TAG, "tong------strLinkPath:"+strLinkPath);
        // 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(getString(R.string.share_from_hanvon));
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl(strLinkPath);
        // text是分享文本，所有平台都需要这个字段
//		 String title = etNoteTitle.getText().toString();
//		 if(title == "")
//		 {
//			 String strContent = etScanContent.getText().toString();
//			 title = strContent;
//		 }
        oks.setText("recognize!!!");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        String curPath = getApplicationContext().getFilesDir().getPath();

        copyPhoto();
        String srcPath = curPath + "/"+"image.png";
        //String newPath= "/sdcard/app_launcher.png";
        Log.i(TAG, "tong-----------srcPath:"+srcPath);

        //copyFile(srcPath,newPath);
        oks.setImagePath(srcPath);//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl(strLinkPath);
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl(strLinkPath);

        // 启动分享GUI
        oks.show(this);
        bShareClick = false;
    }

    public void copyPhoto()
    {

        bitmapLaunch = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        FileOutputStream fos = null;
        try {
            fos = openFileOutput("image.png", Context.MODE_PRIVATE);
            bitmapLaunch.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (FileNotFoundException e) {
        } finally {
            if (fos != null) {
                try {
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                }
            }
        }
    }


    private void shareRecoResult()
    {
        final String titleStr = "识别结果";
        final String contentStr = etResult.getText().toString();
        if (null == contentStr)
        {
            Log.d(TAG, "content is null");
            return;
        }
        bShareClick = true;
        new Thread()
        {
            @Override
            public void run()
            {
                String result = null;
                HvnCloudManager hvnCloud = new HvnCloudManager();
                result = hvnCloud.ShareForSelect(titleStr, contentStr);
                Log.i(TAG, result);

                if (result == null)
                {
                    Message msg = new Message();
                    msg.what = UPLLOAD_FILE_CLOUD_FAIL;
                    handler.sendMessage(msg);
                }
                else
                {
                    strLinkPath = result;
                    Message msg = new Message();
                    msg.what = UPLLOAD_FILE_CLOUD_SUCCESS;
                    handler.sendMessage(msg);
                }
            }
        }.start();

    }

    /*
    public void UploadFilesToHvnCloudForShare()
    {
        new Thread()
        {
            @Override
            public void run()
            {
                String result = null;
                HvnCloudManager hvnCloud = new HvnCloudManager();
                result = hvnCloud.ShareForSelect();
                Log.i(TAG, result);

                if (result == null)
                {
                    Message msg = new Message();
                    msg.what = UPLLOAD_FILE_CLOUD_FAIL;
                    handler.sendMessage(msg);
                }
                else
                {
                    strLinkPath = result;
                    Message msg = new Message();
                    msg.what = UPLLOAD_FILE_CLOUD_SUCCESS;
                    handler.sendMessage(msg);
                }
            }
        }.start();
    }
    */

}
