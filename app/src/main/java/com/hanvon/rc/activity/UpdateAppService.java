package com.hanvon.rc.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;

import com.hanvon.rc.application.HanvonApplication;
import com.hanvon.rc.utils.LogUtil;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @Desc:
 * @Auth: chenxzhuang
 * @Time: 2016/4/27 0027.
 */
public class UpdateAppService {
    private Context context;
    private String updateUrl;
    private ProgressDialog pd;

    private boolean isCancel = false;

    public UpdateAppService() {
        super();
    }
    public UpdateAppService(Context context) {
        this.context=context;
    }

    //创建通知
    public void CreateInform(String Url) {
        updateUrl = Url;

        pd = new  ProgressDialog(context);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setMessage("正在下载更新");

        pd.setCancelable(false);
        pd.setButton(ProgressDialog.BUTTON2, "取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                // TODO Auto-generated method stub
                pd.dismiss();
                isCancel = true;
                LogUtil.i("===========================================");
            }
        });
        pd.show();

        new Thread(new updateRunnable()).start();

    }
    class updateRunnable implements Runnable{
        int downnum = 0;//已下载的大小
        int downcount= 0;//下载百分比

        @Override
        public void run() {
            // TODO Auto-generated method stub
            try {
                File file = getFileFromServer(updateUrl);
                //	installApk(file);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        public File getFileFromServer(String path) throws Exception{
            LogUtil.i("PATH:"+path);
            File file = null;
            //    if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            URL url = new URL(path);
            HttpURLConnection conn =  (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            //获取到文件的大小
            pd.setMax(conn.getContentLength());
            int length = conn.getContentLength();
            InputStream is = conn.getInputStream();

            File f = new File("/sdcard/"+"SuluPen.apk");
            if (f.exists()){
                LogUtil.i("---Environment:PATH---"+"/sdcard/"+"RC-release.apk");
                f.delete();
            }
            file = new File("/sdcard/", "SuluPen.apk");
            LogUtil.i("---Environment:PATH---"+"/sdcard/"+"RC-release.apk");

            LogUtil.i("---Environment:PATH---"+"/sdcard/");
            FileOutputStream fos = new FileOutputStream(file);
            BufferedInputStream bis = new BufferedInputStream(is);
            byte[] buffer = new byte[1024];
            int len = 0;
            int total = 0;

            while((len =bis.read(buffer))!=-1){
                fos.write(buffer, 0, len);
                downnum += len;
                if((downcount == 0)||(int) (downnum*100/length)-1>downcount){
                    downcount += 1;
                    // downloadDialog.setProgress(downnum);
                    pd.setProgress(downnum);
                    if (isCancel){
                        break;
                    }
                }
            }

            fos.close();
            bis.close();
            is.close();
            if (isCancel){
                isCancel = false;
                return null;
            }
            if (downnum==length) {
                Intent installIntent = new Intent(Intent.ACTION_VIEW);
                installIntent.setClassName(context,installApk(file));
            }
            return file;

        }

    }
    /**
     * 5.安装下载的Apk软件
     * @return
     */
    protected String installApk(File file) {
        LogUtil.i("*****************");
        HanvonApplication.path = Uri.fromFile(file).toString();
        LogUtil.i(HanvonApplication.path);
        //	nManager.cancel(100);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        //执行动作
        // intent.setAction(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //执行的数据类型
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        context.startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());
        //    HanvonApplication.isUpdate = false;
        return null;
    }
    // }

    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    public void onDestory(){
        //	super.onDestroy();
    }
}
