package com.hanvon.rc.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.Handler;
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
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;


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

    private ProgressDialog pd;

    private ResultFileListAdapter fileListAdapter;
    private List<ResultFileInfo> mFileList = new ArrayList<ResultFileInfo>();

    private int mShowMode;

    private static Handler handler;

    private static int EDIT_MODE = 2;
    private static int VIEW_MODE = 1;

    private static long downOffset  = 0;

    private boolean isDownLoading = false;

    public final static int MSG_DOWNLOAD_DONE = 0x31;
    public final static int MSG_DOWNLOAD_FAIL = 0x32;


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

    }

    private void setAdapter()
    {
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
        if(new ConnectionDetector(FileListActivity.this).isConnectingTOInternet())
        {
            if((null != pd)&&(pd.isShowing()))
            {
                pd.dismiss();
            }
            pd = ProgressDialog.show(FileListActivity.this,"","正在查询订单....");
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


    private void switchMode()
    {
        LogUtil.i("in switchMode");
        if (mShowMode == VIEW_MODE)
        {
            mShowMode = EDIT_MODE;
            rlBottom.setVisibility(View.VISIBLE);

        }
        else
        {
            mShowMode = VIEW_MODE;
            rlBottom.setVisibility(View.GONE);
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

            case R.id.iv_share:

                break;

            case R.id.iv_del:
                break;

            case R.id.iv_copy_files:
                break;

            case R.id.tv_edit:
                switchMode();
                break;

        }
    }

    public void initHandler()
    {
        handler = new Handler()
        {
            @Override
            public void handleMessage(Message msg)
            {
                Object obj = msg.obj;
                LogUtil.i("handleMessage msg.obj is " + obj);
                JSONObject jsonObject = null;
                try
                {
                    jsonObject = new JSONObject(obj.toString());
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }

                switch (msg.what)
                {
                    case InfoMsg.FILE_LIST_TYPE:
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

                    case MSG_DOWNLOAD_DONE:
                        LogUtil.i("downLoad done");
                        pd.dismiss();
                        isDownLoading = false;
                        break;

                    case MSG_DOWNLOAD_FAIL:
                        pd.dismiss();
                        isDownLoading = false;
                        break;
                }
            }
        };
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

        pd = ProgressDialog.show(FileListActivity.this, "", "正在下载......");

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

                ZipCompressor.unZip(path, "/sdcard/MobileOCR/zip/");

                LogUtil.i("send downdone msg");
                Message message = Message.obtain();
                message.what = MSG_DOWNLOAD_DONE;
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

