package com.hanvon.rc.md.camera.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.hanvon.rc.R;

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
            ivBack.setOnClickListener(this);
            ivError = (ImageView) findViewById(R.id.iv_error);
            findViewById(R.id.btn_continue).setOnClickListener(this);
            findViewById(R.id.btn_retake_pic).setOnClickListener(this);
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.iv_back:
            case R.id.btn_continue:
                RecFailActivity.this.finish();
                break;
            case R.id.btn_retake_pic:
                Intent intent = new Intent(RecFailActivity.this, CameraActivity.class);
                startActivity(intent);
                RecFailActivity.this.finish();
                break;
        }
    }


}
