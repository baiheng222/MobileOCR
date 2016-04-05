package com.hanvon.rc.md.camera.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hanvon.rc.R;

/**
 * Created by baiheng222 on 16-4-5.
 */


public class RecResultActivity extends Activity implements View.OnClickListener
{

    private static final String TAG = "RecResultActivity";
    private ImageView ivBack;
    private TextView tvSave;

    private TextView tvShare;
    private TextView tvCopy;
    private TextView tvExact;
    private TextView tvDel;
    private TextView tvTitel;

    private EditText etResult;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rec_result_activity);

        ivBack = (ImageView) findViewById(R.id.iv_back);
        tvTitel = (TextView) findViewById(R.id.tv_title);
        tvSave = (TextView) findViewById(R.id.tv_save);

        tvShare = (TextView) findViewById(R.id.tv_share);
        tvCopy = (TextView) findViewById(R.id.tv_copy);
        tvExact = (TextView) findViewById(R.id.tv_exact);
        tvDel = (TextView) findViewById(R.id.tv_del);

        etResult = (EditText) findViewById(R.id.et_result);
    }


    @Override
    public void onClick(View view)
    {
        switch(view.getId())
        {
            case R.id.iv_back:
                break;

            case R.id.tv_save:
                break;

            case R.id.tv_share:
                break;

            case R.id.tv_copy:
                break;

            case R.id.tv_del:
                break;

            case R.id.tv_exact:
                break;
        }
    }

}
