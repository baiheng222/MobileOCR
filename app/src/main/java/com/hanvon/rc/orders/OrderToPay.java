package com.hanvon.rc.orders;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.hanvon.rc.R;
import com.hanvon.rc.orders.alipay.AliPay;

/**
 * @Desc:
 * @Auth: chenxzhuang
 * @Time: 2016/3/31 0031.
 */
public class OrderToPay extends Activity implements View.OnClickListener {

    private ImageView IvBack;
    private RadioButton RbWxPay;
    private RadioButton RbZfbPay;
    private TextView TvPages;
    private TextView TvBytes;
    private TextView TvTrans;
    private TextView TvPrices;
    private TextView TvOrderNumber;
    private TextView TvOrderCreateTime;
    private TextView TvOrderName;
    private TextView TvOrderPhone;
    private TextView TvOrderPrices;
    private TextView TvPay;


    private boolean isWxPay;
    private boolean isZfbPay;
    private String OrderPages;
    private String OrderBytes;
    private String OrderTrans;
    private String OrderPrices;
    private String OrderNumber;
    private String OrderCreateTime;
    private String OrderName;
    private String OrderPhone;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.payinfo);

        InitView();
    }

    public void InitView(){
        IvBack = (ImageView)findViewById(R.id.payinfo_back);
        RbWxPay = (RadioButton)findViewById(R.id.payinfo_wxpay);
        RbZfbPay = (RadioButton)findViewById(R.id.payinfo_zfbpay);
        TvPages = (TextView)findViewById(R.id.payinfo_pages);
        TvBytes = (TextView)findViewById(R.id.payinfo_bytes);
        TvTrans = (TextView)findViewById(R.id.payinfo_chnagesstatus);
        TvPrices = (TextView)findViewById(R.id.payinfo_prices);
        TvOrderNumber = (TextView)findViewById(R.id.payinfo_number);
        TvOrderCreateTime = (TextView)findViewById(R.id.payinfo_cratetime);
        TvOrderName = (TextView)findViewById(R.id.payinfo_name);
        TvOrderPhone = (TextView)findViewById(R.id.payinfo_phone);
        TvOrderPrices = (TextView)findViewById(R.id.payinfo_toprices);
        TvPay = (TextView)findViewById(R.id.payinfo_topay);

        IvBack.setOnClickListener(this);
        RbWxPay.setOnClickListener(this);
        RbZfbPay.setOnClickListener(this);
        TvPay.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.payinfo_wxpay:
                RbWxPay.setChecked(true);
                RbZfbPay.setChecked(false);
                isWxPay = true;
                isZfbPay = false;
                break;
            case R.id.payinfo_zfbpay:
                RbWxPay.setChecked(false);
                RbZfbPay.setChecked(true);
                isWxPay = false;
                isZfbPay = true;
                break;
            case R.id.payinfo_back:
                finish();
                break;
            case R.id.payinfo_topay:
                if (isZfbPay){
                    Intent intent = new Intent();
                    intent.setClass(this, AliPay.class);
                    startActivity(intent);
                }else{

                }
                break;
            default:
                break;
        }
    }
}
