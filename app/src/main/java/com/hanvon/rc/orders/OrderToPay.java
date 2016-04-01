package com.hanvon.rc.orders;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.hanvon.rc.R;

/**
 * @Desc:
 * @Auth: chenxzhuang
 * @Time: 2016/3/31 0031.
 */
public class OrderToPay extends Activity implements View.OnClickListener {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.payinfo);
    }
    @Override
    public void onClick(View v) {

    }
}
