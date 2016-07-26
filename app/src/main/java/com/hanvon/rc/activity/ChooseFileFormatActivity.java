package com.hanvon.rc.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hanvon.rc.R;
import com.hanvon.rc.adapter.FileFormatAdapter;
import com.hanvon.rc.bcard.ChooseMorePicturesActivity;
import com.hanvon.rc.md.camera.activity.RecResultActivity;
import com.hanvon.rc.presentation.CropActivity;
import com.hanvon.rc.utils.InfoMsg;
import com.hanvon.rc.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by baiheng222 on 16-5-5.
 */
public class ChooseFileFormatActivity extends Activity implements View.OnClickListener
{
    private ImageView ivBack;
    private TextView tvTitle;
    private EditText etFileName;
    private TextView tvFileSize;
    private RelativeLayout rlFileSize;

    private ListView lvFormat;

    private FileFormatAdapter adapter;

    private List<String> mDatas;

    private int mResultType = 0;
    private String mFileSize;
    private String defFileName = null;
    private String suffix = "txt";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_format);

        initData();
        initView();
        lvFormat.setAdapter(adapter);
        lvFormat.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {

                if (mResultType == InfoMsg.RESULT_TYPE_QUICK_RECO)
                {
                    suffix = "txt";
                }
                else
                {
                    suffix = mDatas.get(position).toLowerCase();
                }
                LogUtil.i("select file format is " + mDatas.get(position));
                setRet();
                finish();
            }
        });
    }

    private void initView()
    {
        ivBack = (ImageView) findViewById(R.id.iv_back);
        ivBack.setOnClickListener(this);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        etFileName =  (EditText) findViewById(R.id.et_input);
        lvFormat = (ListView) findViewById(R.id.lv_format);
        tvFileSize = (TextView) findViewById(R.id.tv_file_size);
        tvFileSize.setText(mFileSize);
        rlFileSize = (RelativeLayout) findViewById(R.id.rl_filesize);

        if (null != defFileName)
        {
            if (defFileName.equals("test"))
            {
                etFileName.setText(defFileName);
            }
            else
            {
                etFileName.setText(defFileName.substring(0, defFileName.lastIndexOf(".zip")));
            }
        }

        if (mResultType == InfoMsg.RESULT_TYPE_QUICK_RECO)
        {
            rlFileSize.setVisibility(View.GONE);
        }

    }

    private void initData()
    {
        Intent intent = getIntent();
        mResultType = intent.getIntExtra("resultType", 0);
        defFileName = intent.getStringExtra("filename");
        LogUtil.i("defFilename is " + defFileName);
        mFileSize = intent.getStringExtra("filesize");
        LogUtil.i("resultType is " + mResultType);
        LogUtil.i("file size is " + mFileSize);
        initAdapter();
    }

    private void initAdapter()
    {
        mDatas = new ArrayList<String>();
        mDatas.add("TXT");
        //mDatas.add("PDF");
        //mDatas.add("DOC");

        adapter = new FileFormatAdapter(this, mDatas);
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.iv_back:
                //setRet();
                Intent intent = new Intent(ChooseFileFormatActivity.this, CropActivity.class);
                setResult(RESULT_CANCELED, intent);
                finish();
            break;
        }
    }

    private void setRet()
    {
        LogUtil.i("setRet!!!!");
        if (mResultType == InfoMsg.RESULT_TYPE_QUICK_RECO)
        {
            LogUtil.i("call setResult !!!");
            Intent intent = new Intent(ChooseFileFormatActivity.this, RecResultActivity.class);
            intent.putExtra("filename", etFileName.getText().toString());
            intent.putExtra("suffix", suffix);
            setResult(RESULT_OK, intent);
        }
        else
        {
            LogUtil.i("call setResult !!!");
            Intent intent = new Intent(ChooseFileFormatActivity.this, ChooseMorePicturesActivity.class);
            intent.putExtra("filename", etFileName.getText().toString());
            intent.putExtra("suffix", suffix);
            setResult(RESULT_OK, intent);
        }
    }

}

