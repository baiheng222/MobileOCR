package com.hanvon.rc.md.camera.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.hanvon.rc.R;


public class SaveRecResultActivity extends Activity implements View.OnClickListener
{
    private static final String TAG = "SaveRecResultActivity";

    private ImageView ivBack;
    private TextView tvTitle;
    private TextView tvSave;

    private EditText etFileName;
    private ListView lvFileFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.share_save_activity);

        ivBack = (ImageView) findViewById(R.id.iv_back);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvSave = (TextView) findViewById(R.id.tv_save);

        lvFileFormat = (ListView) findViewById(R.id.lv_file_format);
        etFileName = (EditText) findViewById(R.id.et_filename);
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.iv_back:

                break;

            case R.id.tv_save:

                break;
        }
    }

}

