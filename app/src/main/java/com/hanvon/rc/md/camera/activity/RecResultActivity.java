package com.hanvon.rc.md.camera.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hanvon.rc.R;
import com.hanvon.rc.activity.ChooseFileFormatActivity;
import com.hanvon.rc.activity.MainActivity;
import com.hanvon.rc.db.FileInfo;
import com.hanvon.rc.utils.CustomDialog;
import com.hanvon.rc.utils.HvnCloudManager;
import com.hanvon.rc.utils.InfoMsg;
import com.hanvon.rc.utils.LogUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

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
    private TextView tvTitle;

    private EditText etResult;

    private String recResult;



    private final int DLG_RETURN_YES = 0;
    private final int DLG_RETURN_NO = 1;
    private final int DLG_RETURN_OK = 2;
    private final int DLG_RETURN_CANCEL = 3;
    private final int DLG_RETURN_INIT = -1;

    private static final int REQUEST_FILE_FORMAT = 4;

    private int dlgReturn = DLG_RETURN_INIT;



    private ProgressDialog pd;
    private String  strLinkPath = null;
    private Bitmap bitmapLaunch;
    private Boolean bShareClick = false;
    private String picturePaht = null;


    private Boolean bReadOnlyMode = false;
    private FileInfo resultInfo;

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
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvSave = (TextView) findViewById(R.id.tv_save);


        if (bReadOnlyMode)
        {
            tvSave.setVisibility(View.GONE);
        }
        else
        {
            tvSave.setVisibility(View.VISIBLE);
        }

        tvSave.setVisibility(View.GONE);

        tvShare = (ImageView) findViewById(R.id.iv_share);
        tvCopy = (ImageView) findViewById(R.id.iv_copy);
        tvExact = (ImageView) findViewById(R.id.iv_exact);
        tvDel = (ImageView) findViewById(R.id.iv_del);

        etResult = (EditText) findViewById(R.id.et_result);
        if (recResult != null)
        {
            etResult.setText(recResult);
        }

        if (bReadOnlyMode)
        {
            tvTitle.setText(getSavedFileName());
        }
        else
        {
            tvTitle.setText(genResultFileName());
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
        bReadOnlyMode = false;
        Intent intent = getIntent();
        if (intent != null)
        {
            Bundle bundle = intent.getExtras();
            recResult = bundle.getString("textResult");
            picturePaht = bundle.getString("path");
            if (null != recResult)
            {
                LogUtil.i("recResult is " + recResult);
                LogUtil.i("picture path is " + picturePaht);
            }
            else
            {
                bReadOnlyMode = true;
                resultInfo = (FileInfo) bundle.getSerializable("info");
                LogUtil.i("received file info is : " + resultInfo.toSting());
                recResult = readFileToBuf(resultInfo.getResultPath());
                LogUtil.i("get result from txt: " + recResult);
            }
        }
    }


    private String getSavedFileName()
    {
        String filename = null;
        String name = resultInfo.getResultPath();
        filename = name.substring(name.lastIndexOf("/") + 1, name.lastIndexOf("."));
        return filename;
    }

    private String readFileToBuf(String filePath)
    {
        try
        {
            StringBuffer sb = new StringBuffer("");
            String encoding="UTF-8";
            File file=new File(filePath);
            if(file.isFile() && file.exists())//判断文件是否存在
            {
                InputStreamReader read = new InputStreamReader(new FileInputStream(file),encoding);//考虑到编码格式
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;

                while((lineTxt = bufferedReader.readLine()) != null)
                {
                    //LogUtil.i(lineTxt);
                    sb.append(lineTxt);
                }
                read.close();
                return sb.toString();
            }
            else
            {
                LogUtil.i("找不到指定的文件");
            }
        }
        catch (Exception e)
        {
            LogUtil.i("读取文件内容出错");
            e.printStackTrace();
        }

        return null;
    }

    private String genResultFilePath()
    {
        //String name = picturePaht.substring(0, picturePaht.indexOf("."));
        LogUtil.i("picturePath is " + picturePaht);
        String fullpath = picturePaht.substring(0, picturePaht.lastIndexOf("/"));

        return fullpath;
    }

    private String genResultFileName()
    {
        String name = picturePaht.substring(picturePaht.lastIndexOf("/") + 1, picturePaht.length());
        String filename = name.substring(0, name.lastIndexOf("."));
        LogUtil.i("file name without suffix is :" + filename);
        return filename;
    }

    private int saveFile(String str, String filePath)
    {
        int fileSize = 0;
        //String filePath = null;
        /*
        boolean hasSDCard = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (hasSDCard)
        {
            filePath = Environment.getExternalStorageDirectory().toString() + File.separator + "hello.txt";
        }
        else
        {
            filePath = Environment.getDownloadCacheDirectory().toString() + File.separator + "hello.txt";
        }
        */

        try
        {
            File file = new File(filePath);
            if (!file.exists())
            {
                File dir = new File(file.getParent());
                dir.mkdirs();
                file.createNewFile();
            }
            FileOutputStream outStream = new FileOutputStream(file);
            outStream.write(str.getBytes());
            outStream.close();
            fileSize = (int)file.length();
            return fileSize;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return fileSize;
    }

    private void startChooseFormatActivity()
    {
        Intent intent = new Intent(this, ChooseFileFormatActivity.class);
        intent.putExtra("resultType", InfoMsg.RESULT_TYPE_QUICK_RECO);
        intent.putExtra("filename", genResultFileName());
        startActivityForResult(intent, REQUEST_FILE_FORMAT);
    }

    private void saveRetTofile(String filename)
    {
        String filepath = genResultFilePath() + "/" + filename;
        LogUtil.i("final file path is " + filepath);
        int size = saveFile(etResult.getText().toString(), filepath);

        FileInfo finfo = new FileInfo();
        finfo.setOriginPath(picturePaht);
        finfo.setResultPath(filepath);
        finfo.setResultSize(size);
        finfo.setResultType("txt");
        finfo.setResultFUID("empty");
        finfo.setUserID("emtpy");
        MainActivity.dbManager.insertRecord(finfo);

        Toast.makeText(this, "文件已保存", Toast.LENGTH_SHORT).show();
    }

    private void showSave()
    {
        new CustomDialog.Builder(RecResultActivity.this)
                .setTitle(R.string.str_dlg_tip)
                .setMessage(R.string.rec_ret_save)
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
                                startChooseFormatActivity();
                                //saveRetTofile();
                                RecResultActivity.this.finish();
                            }
                        }).show();


    }

    private void showDel()
    {
        new CustomDialog.Builder(RecResultActivity.this)
                .setTitle(R.string.str_dlg_tip)
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
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
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
                this.finish();
                break;

            case R.id.tv_save:
                startChooseFormatActivity();
                //showSave();
                break;

            case R.id.iv_share:
                if (!bShareClick)
                {
                    pd = ProgressDialog.show(RecResultActivity.this, "", getString(R.string.link_mess));
                    shareRecoResult();
                }
                break;

            case R.id.iv_copy:
                ClipboardManager myClipboard;
                myClipboard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
                myClipboard.setText(etResult.getText().toString());
                Toast.makeText(RecResultActivity.this, "已经复制到粘贴板", Toast.LENGTH_SHORT).show();
                break;

            case R.id.iv_del:
                showDel();
                break;

            case R.id.iv_exact:
                Intent exactIntent = new Intent(this, ExactActivity.class);
                startActivity(exactIntent);
                this.finish();
                break;
        }
    }


    private void showShare()
    {
        ShareSDK.initSDK(this);
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        Log.i(TAG, "tong------strLinkPath:"+strLinkPath);
        // 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        //oks.setTitle(getString(R.string.share_from_hanvon));
        oks.setTitle("");
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl(strLinkPath);
        // text是分享文本，所有平台都需要这个字段
//		 String title = etNoteTitle.getText().toString();
//		 if(title == "")
//		 {
//			 String strContent = etScanContent.getText().toString();
//			 title = strContent;
//		 }
        oks.setText(etResult.getText().toString());
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
        //bitmapLaunch = getImageThumbnail("/sdcard/DCIM/Camera/IMG_20160428_154425.jpg", 120, 120);
        bitmapLaunch = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        FileOutputStream fos = null;
        try
        {
            fos = openFileOutput("image.png", Context.MODE_PRIVATE);
            bitmapLaunch.compress(Bitmap.CompressFormat.PNG, 100, fos);
        }
        catch (FileNotFoundException e)
        {
        }
        finally
        {
            if (fos != null)
            {
                try
                {
                    fos.flush();
                    fos.close();
                }
                catch (IOException e)
                {
                }
            }
        }
    }


    /**
     * 根据指定的图像路径和大小来获取缩略图
     * 此方法有两点好处：
     *     1. 使用较小的内存空间，第一次获取的bitmap实际上为null，只是为了读取宽度和高度，
     *        第二次读取的bitmap是根据比例压缩过的图像，第三次读取的bitmap是所要的缩略图。
     *     2. 缩略图对于原图像来讲没有拉伸，这里使用了2.2版本的新工具ThumbnailUtils，使
     *        用这个工具生成的图像不会被拉伸。
     * @param imagePath 图像的路径
     * @param width 指定输出图像的宽度
     * @param height 指定输出图像的高度
     * @return 生成的缩略图
     */
    private Bitmap getImageThumbnail(String imagePath, int width, int height)
    {
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        // 获取这个图片的宽和高，注意此处的bitmap为null
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        options.inJustDecodeBounds = false; // 设为 false
        // 计算缩放比
        int h = options.outHeight;
        int w = options.outWidth;
        int beWidth = w / width;
        int beHeight = h / height;
        int be = 1;
        if (beWidth < beHeight)
        {
            be = beWidth;
        }
        else
        {
            be = beHeight;
        }
        if (be <= 0)
        {
            be = 1;
        }
        options.inSampleSize = be;
        // 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        // 利用ThumbnailUtils来创建缩略图，这里要指定要缩放哪个Bitmap对象
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        LogUtil.i("onActivityResult");
        if (requestCode == REQUEST_FILE_FORMAT)
        {
            if (resultCode == RESULT_OK)
            {
                String filename = data.getStringExtra("filename");
                String suffix = data.getStringExtra("suffix");
                LogUtil.i("get file name is " + filename);
                saveRetTofile(filename + "." + suffix);
            }
        }
    }

}
