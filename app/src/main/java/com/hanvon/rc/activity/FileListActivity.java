package com.hanvon.rc.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hanvon.rc.R;
import com.hanvon.rc.adapter.ResultFileListAdapter;
import com.hanvon.rc.db.FileInfo;
import com.hanvon.rc.md.camera.activity.RecResultActivity;
import com.hanvon.rc.utils.ConnectionDetector;
import com.hanvon.rc.utils.InfoMsg;
import com.hanvon.rc.utils.LogUtil;
import com.hanvon.rc.utils.MyHttpUtils;
import com.hanvon.rc.utils.RequestJson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ret_list);

        mShowMode = VIEW_MODE;

        initHandler();

        initDatas();

        initView();

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
                }
            }
        };
    }

    public void downLoadFile(int position)
    {
        
    }

}

