package com.hanvon.rc.md.camera.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.hanvon.rc.R;

/**
 * Created by baiheng222 on 16-4-25.
 */
public class ExactActivity extends Activity implements View.OnClickListener
{
    private ImageView ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exact);

        ivBack = (ImageView) findViewById(R.id.iv_back);
        ivBack.setOnClickListener(this);

    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.iv_back:
                ExactActivity.this.finish();
            break;
        }
    }

}
