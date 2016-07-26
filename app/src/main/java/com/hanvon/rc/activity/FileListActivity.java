package com.hanvon.rc.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hanvon.rc.R;
import com.hanvon.rc.adapter.ResultFileListAdapter;
import com.hanvon.rc.application.HanvonApplication;
import com.hanvon.rc.db.FileInfo;
import com.hanvon.rc.md.camera.activity.RecResultActivity;
import com.hanvon.rc.presentation.CropActivity;
import com.hanvon.rc.utils.ConnectionDetector;
import com.hanvon.rc.utils.HttpUtilsFiles;
import com.hanvon.rc.utils.InfoMsg;
import com.hanvon.rc.utils.LogUtil;
import com.hanvon.rc.utils.MyHttpUtils;
import com.hanvon.rc.utils.RequestJson;
import com.hanvon.rc.utils.StatisticsUtils;
import com.hanvon.rc.utils.ZipCompressor;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RunnableFuture;


public class FileListActivity extends Activity implements View.OnClickListener
{

    private RelativeLayout rlBottom;
    private RelativeLayout rlTitle;
    private ImageView ivBack;
    private TextView tvTitle;
    private ListView lvFile;
    private TextView mTvEdit;

    private ImageView mIvShare;
    private ImageView mIvCopyFiles;
    private ImageView mIvDelFiles;
    private TextView mTvShare;

    private ProgressDialog pd;

    private ResultFileListAdapter fileListAdapter;
    private List<ResultFileInfo> mFileList = new ArrayList<ResultFileInfo>();

    private int mShowMode;

    private static Handler handler;
    private static Handler delHandler;

    private static int EDIT_MODE = 2;
    private static int VIEW_MODE = 1;

    private static long downOffset  = 0;

    private boolean isDownLoading = false;
    private boolean isDeleteing = false;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ret_list);

        mShowMode = VIEW_MODE;

        initHandler();

        initDatas();

        initView();

        isDownLoading = false;
        isDeleteing = false;

        //mFileList = MainActivity.dbManager.queryForAll();
        /*
        fileListAdapter = new ResultFileListAdapter(this, mFileList, VIEW_MODE);

        lvFile.setAdapter(fileListAdapter);

        lvFile.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position, long id)
            {
                LogUtil.i("item " +  position + " clicked");
                startRecsultActivity(position);
                FileListActivity.this.finish();
            }
        });
        */
        StatisticsUtils.IncreaseFileListPage();
    }

    private void setAdapter()
    {
        fileListAdapter = new ResultFileListAdapter(this, mFileList, VIEW_MODE);

        lvFile.setAdapter(fileListAdapter);

        if (mFileList != null && mFileList.size() > 0)
        {
            mTvEdit.setVisibility(View.VISIBLE);
        }
        else
        {
            mTvEdit.setVisibility(View.GONE);
        }

        /*
        lvFile.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position, long id)
            {
                LogUtil.i("item " +  position + " clicked");
                startRecsultActivity(position);
                FileListActivity.this.finish();
            }
        });
        */
    }

    public void initView()
    {
        rlTitle = (RelativeLayout) findViewById(R.id.rl_title);
        rlBottom = (RelativeLayout) findViewById(R.id.ll_bottom_bar);
        ivBack = (ImageView) findViewById(R.id.iv_back);
        ivBack.setOnClickListener(this);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        lvFile = (ListView) findViewById(R.id.lv_file);
        mTvEdit = (TextView) findViewById(R.id.tv_edit);
        mTvEdit.setOnClickListener(this);

        mIvShare = (ImageView) findViewById(R.id.iv_share);
        mIvShare.setOnClickListener(this);
        mTvShare = (TextView)findViewById(R.id.tv_share);
        mTvShare.setOnClickListener(this);
        mIvCopyFiles = (ImageView) findViewById(R.id.iv_copy_files);
        mIvCopyFiles.setOnClickListener(this);
        mIvDelFiles = (ImageView) findViewById(R.id.iv_del);
        mIvDelFiles.setOnClickListener(this);

        if (mShowMode == VIEW_MODE)
        {
            rlBottom.setVisibility(View.GONE);
        }

    }

    public void initDatas()
    {
        mFileList.clear();
        if(new ConnectionDetector(FileListActivity.this).isConnectingTOInternet())
        {
            if((null != pd)&&(pd.isShowing()))
            {
                pd.dismiss();
            }
            pd = ProgressDialog.show(FileListActivity.this,"","正在查询文件....");
            new MyHttpUtils(handler);
            RequestJson.GetFilesList();
        }
        else
        {
            Toast.makeText(FileListActivity.this,"请检查网络是否连通!",Toast.LENGTH_SHORT).show();
        }
    }


    public  void startRecsultActivity(int pos)
    {
        /*
        FileInfo finfo = mFileList.get(pos);
        Intent intent = new Intent(this, RecResultActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("info", finfo);
        intent.putExtras(bundle);
        startActivity(intent);
        */
    }

    public void startRecResultActivityWithFileName(String filepath)
    {
        LogUtil.i("startRecResultActivityWithFileName called");
        Intent intent = new Intent(this, RecResultActivity.class);
        intent.putExtra("filename", filepath);
        startActivity(intent);
    }


    private void switchMode()
    {
        LogUtil.i("in switchMode");
        if (mShowMode == VIEW_MODE)
        {
            mShowMode = EDIT_MODE;
            rlBottom.setVisibility(View.VISIBLE);
            mTvEdit.setText(R.string.bc_str_cancle);

        }
        else
        {
            mShowMode = VIEW_MODE;
            rlBottom.setVisibility(View.GONE);
            mTvEdit.setText(R.string.edit);
        }

        fileListAdapter.setmShowMode(mShowMode);
        fileListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.iv_back:
                if (mShowMode == VIEW_MODE)
                {
                    this.finish();
                }
                else
                {
                    switchMode();
                }
            break;

            case R.id.tv_share:
            case R.id.iv_share:
                StatisticsUtils.IncreaseShareFileBtn();
                    sendByEmail();
                break;

            case R.id.iv_del:
                StatisticsUtils.IncreaseDeleteFileBtn();
                    showDelDlg();
                break;

            case R.id.iv_copy_files:
                break;

            case R.id.tv_edit:
                switchMode();
                break;

        }
    }

    private void doAfterDel()
    {
        initDatas();
    }

    private void doDelete()
    {
        if (isDeleteing)
        {
            LogUtil.i("deleteding file ,return");
            return;
        }
        else
        {
            isDeleteing = true;
        }

        if (!connInNet())
        {
            Toast.makeText(FileListActivity.this, "网络连接失败，请检查网络后重试！", Toast.LENGTH_LONG).show();
            isDeleteing = false;
            return;
        }


        pd = ProgressDialog.show(FileListActivity.this, "", "正在删除......");

        DeleteThread thread = new DeleteThread();
        new Thread(thread).start();
    }

    private  void FileDelete(int tyep,String fuid)
    {
        LogUtil.i("fileDelete called !!!");
        JSONObject JSuserInfoJson = new JSONObject();
        try
        {
            JSuserInfoJson.put("userid", HanvonApplication.hvnName);
            JSuserInfoJson.put("fid", fuid);
            JSuserInfoJson.put("fileType", String.valueOf(tyep));
        } catch(JSONException e) {
            e.printStackTrace();
        }
        LogUtil.i(JSuserInfoJson.toString());
        new MyHttpUtils(delHandler);
        MyHttpUtils.HttpSend(InfoMsg.UrlFileDelete, JSuserInfoJson, InfoMsg.FILE_DELETE_TYPE);
    }

    private void delSelectedFiles()
    {
        LogUtil.i("delSelectFiles called here !!!");
        ArrayList<ResultFileInfo> filelist = fileListAdapter.getSelectedFilesInfo();
        String ids = new String();
        for (int i =0; i < filelist.size(); i++)
        {
            ids = ids + filelist.get(i).getFid();
            if (i < (filelist.size() - 2))
            {
                ids = ids + ",";
            }
        }
        LogUtil.i("fid sets is {" + ids + "}");
        FileDelete(2, ids);
        return;
    }

    private void sendByEmail()
    {
        ArrayList<String> filelist = fileListAdapter.getSelectFiles();

        if (filelist.size() > 0)
        {
            Intent it = new Intent(Intent.ACTION_SEND_MULTIPLE);
            String theme = "分享";
            it.putExtra(Intent.EXTRA_SUBJECT, theme);

            ArrayList<Uri> uri = new ArrayList<Uri>();

            for (int i = 0; i < filelist.size(); i++)
            {
                LogUtil.i("filepath is " + filelist.get(i));
                File file = new File(filelist.get(i));
                uri.add(Uri.fromFile(file));
            }
            it.setType("application/octet-stream");
            it.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uri);
            startActivity(it);

            /*
            String fullPath = "/sdcard/MobileOCR/aic_201606171145552389_result.zip";
            it.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(fullPath)));
            String fullPath2 = "/sdcard/MobileOCR/aic_201606021717002994_txt.zip";
            it.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(fullPath2)));
            it.setType("plain/text");
            //it.setType("plain/text");邮件
            //it.setType("application/octet-stream");邮件+蓝牙+其它
            //intent.setType("text/html"):邮件+蓝牙
            //setType("text/plain")：邮件+蓝牙+其它
            startActivity(it);
            */
        }
    }

    private void showDelDlg()
    {
        ArrayList<ResultFileInfo> filelist = fileListAdapter.getSelectedFilesInfo();
        if (filelist.size() <= 0)
        {
            LogUtil.i("no file selected");
            return;
        }

        AlertDialog.Builder dialog = new AlertDialog.Builder(FileListActivity.this);
        dialog.setMessage(FileListActivity.this.getString(R.string.dle_msg));
        dialog.setNegativeButton(R.string. bc_str_cancle, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {

            }
        });

        dialog.setPositiveButton(R.string.ensure, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                doDelete();
            }
        });

        dialog.show();


    }

    public void initHandler()
    {
        delHandler = new Handler()
        {
            @Override
            public void handleMessage(Message msg)
            {
                Object obj = msg.obj;
                LogUtil.i("handleMessage msg.obj is " + obj);
                JSONObject jsonObject = null;
                switch (msg.what)
                {
                    case InfoMsg.NET_ERR_SOCKET_TIMEOUT:
                        LogUtil.i("network error !!!!!");
                        pd.dismiss();
                        Toast.makeText(FileListActivity.this, "网路出错，请重试", Toast.LENGTH_SHORT).show();
                        isDeleteing = false;
                        break;

                    case InfoMsg.FILE_DELETE_TYPE:
                        pd.dismiss();
                        isDeleteing = false;
                        try
                        {
                            jsonObject = new JSONObject(obj.toString());
                            if("0".equals(jsonObject.getString("code")))
                            {
                                LogUtil.i("files delete success !!!!!");
                                Toast.makeText(FileListActivity.this, "文件已经删除", Toast.LENGTH_SHORT).show();
                                doAfterDel();
                            }
                            else
                            {
                                Toast.makeText(FileListActivity.this, "文件未能删除!!", Toast.LENGTH_LONG).show();
                            }

                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                        break;
                }
            }
        };

        handler = new Handler()
        {
            @Override
            public void handleMessage(Message msg)
            {
                Object obj = msg.obj;
                LogUtil.i("handleMessage msg.obj is " + obj);
                JSONObject jsonObject = null;
                switch (msg.what)
                {
                    case InfoMsg.FILE_LIST_TYPE:

                        try
                        {
                            jsonObject = new JSONObject(obj.toString());
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }

                        try
                        {
                            JSONArray jsonArray = new JSONArray(jsonObject.getString("list"));
                            int recordCount = jsonArray.length();
                            for(int i = 0;i < recordCount;i++)
                            {
                                JSONObject json = jsonArray.getJSONObject(i);
                                ResultFileInfo fileInfo = new ResultFileInfo();
                                fileInfo.setFid(json.getString("fid"));
                                fileInfo.setFileNanme(json.getString("fileName"));
                                fileInfo.setFileType(json.getString("fileType"));
                                fileInfo.setFileAmount(json.getString("fileAmount"));
                                fileInfo.setFileSize(json.getString("fileSize"));
                                fileInfo.setDownloadFlag(json.getString("downloadFlag"));
                                fileInfo.setCreateTime(json.getString("createTime"));
                                mFileList.add(fileInfo);
                            }
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                        pd.dismiss();

                        setAdapter();

                    break;

                    case InfoMsg.NET_ERR_SOCKET_TIMEOUT:
                        LogUtil.i("network error !!!!!");
                        pd.dismiss();
                        Toast.makeText(FileListActivity.this, "网路出错，请重试", Toast.LENGTH_SHORT).show();
                        break;

                    case InfoMsg.MSG_DOWNLOAD_DONE:
                        LogUtil.i("handle downLoad done");
                        pd.dismiss();
                        ArrayList<String> filelist = msg.getData().getStringArrayList("filelist");
                        if (filelist != null)
                        {
                            for (int i = 0; i < filelist.size(); i++)
                            {
                                LogUtil.i("filelist[" + i + "] is " + filelist.get(i));
                            }
                        }
                        isDownLoading = false;
                        startRecResultActivityWithFileName(filelist.get(0));
                        break;
                }
            }
        };
    }


    public class DeleteThread implements Runnable
    {
        public DeleteThread()
        {

        }

        @Override
        public void run()
        {
            delSelectedFiles();
        }
    }

    public class DownLoadThread implements Runnable
    {
        private long mOffset;
        private long mLength;
        private String mFid;
        private String mPath;

        public DownLoadThread(long offset, long length, String fid, String path)
        {
            mOffset = offset;
            mLength = length;
            mFid = fid;
            mPath = path;
        }

        @Override
        public void run()
        {
            FileDown(mOffset, mLength, mFid, mPath);
        }
    }



    public void downLoadFile(int position)
    {
        if (isDownLoading)
        {
            LogUtil.i("downloading file ,return");
            return;
        }
        else
        {
            isDownLoading = true;
        }

        if (!connInNet())
        {
            Toast.makeText(FileListActivity.this, "网络连接失败，请检查网络后重试！", Toast.LENGTH_LONG).show();
            isDownLoading = false;
            return;
        }

        if (mFileList.get(position).getDownloadFlag().equals("0"))
        {
            pd = ProgressDialog.show(FileListActivity.this, "", "正在下载......");
        }
        else
        {
            pd = ProgressDialog.show(FileListActivity.this, "", "正在打开......");
        }

        downOffset = 0;
        long offset = 0;
        long length = Long.parseLong(mFileList.get(position).getFileSize());
        String fid = mFileList.get(position).getFid();
        String localFileName = "/sdcard/MobileOCR/" + mFileList.get(position).getFileNanme()+".zip";
        //FileDown(offset, length, fid, localFileName);
        DownLoadThread thread = new DownLoadThread(offset, length, fid, localFileName);
        new Thread(thread).start();
    }

    public static void FileDown(long offset,long length,String fid,String path)
    {
        JSONObject JSuserInfoJson = new JSONObject();
        try
        {
            JSuserInfoJson.put("userid", HanvonApplication.hvnName);
            JSuserInfoJson.put("recogType", 2);
            JSuserInfoJson.put("fileType", "2");
            JSuserInfoJson.put("fid", fid);
            JSuserInfoJson.put("filePath", "");
            JSuserInfoJson.put("offset", offset);
            JSuserInfoJson.put("length", String.valueOf(length));
        }
        catch(JSONException e)
        {
            e.printStackTrace();
        }

        LogUtil.i(JSuserInfoJson.toString());
        HttpDownFiles(JSuserInfoJson, InfoMsg.UrlFileDown, path, fid);
    }

    public  static void HttpDownFiles(JSONObject params,String urlStr,String path,String fid)
    {
        HttpClient httpClient = new DefaultHttpClient();
        HttpResponse response;
        try // 模拟调用rest API下载文件接口
        {
            StringEntity entity = new StringEntity(params.toString());
            // 以post方式请求URL
            HttpPost httpPost = new HttpPost(urlStr);
            // 参数为json串形式
            //   httpPost.addHeader("Content-Type", MediaType.APPLICATION_JSON);
            httpPost.setEntity(entity);
            // 执行后获得响应数据
            response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            if(statusCode != 200)
            {
                httpPost.abort();
                throw new RuntimeException("HttpClient, response :" + response);
            }
            String contentDisposition = response.getHeaders("Content-Disposition")[0].toString();
            String size = contentDisposition.substring(
                    contentDisposition.lastIndexOf("#") + 1, contentDisposition.length());

            // 获得附件文件名称
            String attachmentFileName = contentDisposition.substring(
                    contentDisposition.lastIndexOf("=")+2,  contentDisposition.lastIndexOf("#")-1);
            // 解决中文文件名乱码问题
            attachmentFileName = URLDecoder.decode(attachmentFileName, "UTF-8");
            String downloadPath = path;
            LogUtil.i(downloadPath+"   size:"+size);
            if (downOffset == 0)
            {
                File file = new File(path);
                if(file.exists())
                {
                    file.delete();
                }
            }
            InputStream is = response.getEntity().getContent();
            RandomAccessFile randomFile = new RandomAccessFile(path, "rw");
            long fileLength = randomFile.length();
            //将写文件指针移到文件尾。
            randomFile.seek(fileLength);

            int read = 0;
            byte[] buffer = new byte[32768];
            int offset = 0;
            while( (read = is.read(buffer)) > 0)
            {
                randomFile.write(buffer,0,read);
                downOffset = downOffset + read;
            }
            LogUtil.i("downOffset is " +  downOffset);
            randomFile.close();
            is.close();
            //下次取的起始位置
            if (downOffset < Long.valueOf(size))
            {
                long length = 0;
                if(Long.valueOf(size) - downOffset >= 32768)
                {
                    length = 32768;
                }
                else
                {
                    length = Long.valueOf(size) - downOffset;
                }
                FileDown(downOffset,length,path,fid);
            }
            else
            {
                LogUtil.i("path is " + path);
                String dirname = path.substring(0, path.lastIndexOf(".zip"));
                LogUtil.i("dirname is " + dirname);
                ArrayList<String> filelist = ZipCompressor.unZip(path, dirname);

                LogUtil.i("send downdone msg");
                Message message = new Message();
                message.what = InfoMsg.MSG_DOWNLOAD_DONE;
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("filelist", filelist);
                message.setData(bundle);
                FileListActivity.handler.sendMessage(message);

            }
        }
        catch (UnsupportedEncodingException e1)
        {
            e1.printStackTrace();
        }
        catch (ClientProtocolException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }


    public  boolean connInNet() //检查是否连网
    {
        ConnectionDetector connectionDetector = new ConnectionDetector(getApplication());
        if(connectionDetector.isConnectingTOInternet())
        {
            return true;
        }
        else
        {
            //Toast.makeText(getApplication(), "网络连接失败，请检查网络后重试！", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    /*
    public Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            mProgress.dismiss();
            isDownLoading = false;
        }
    };
    */

}

