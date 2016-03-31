package com.hanvon.mobileocr.md.camera.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.hanvon.mobileocr.R;

/**
 * Created by baiheng222 on 16-3-31.
 */
public class RecFailActivity extends Activity implements View.OnClickListener
{
    private final static String TAG = "RecFailActivity";


    private RelativeLayout rlTitle;
    private ImageView ivBack;
    private ImageView ivError;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.recfail_activity);

            rlTitle = (RelativeLayout) findViewById(R.id.rl_title);
            ivBack = (ImageView) findViewById(R.id.iv_back);
            ivError = (ImageView) findViewById(R.id.iv_error);
            findViewById(R.id.btn_continue).setOnClickListener(this);
            findViewById(R.id.btn_retake_pic).setOnClickListener(this);
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.btn_continue:
                    //TODO implement
                break;
            case R.id.btn_retake_pic:
                    //TODO implement
                break;
        }
    }


}
