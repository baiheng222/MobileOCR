package com.hanvon.rc.orders;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hanvon.rc.R;
import com.hanvon.rc.utils.ConnectionDetector;
import com.hanvon.rc.utils.InfoMsg;
import com.hanvon.rc.utils.LogUtil;
import com.hanvon.rc.utils.MyHttpUtils;
import com.hanvon.rc.utils.RequestJson;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @Desc:
 * @Auth: chenxzhuang
 * @Time: 2016/3/29 0029.
 */
public class OrderDetailActivity extends Activity implements View.OnClickListener{

    private TextView TvOrderDes;
    private TextView TvOrderWaitTime;
    private TextView TvOrderPages;
    private TextView TvOrderBytes;
    private TextView TvOrderChanges;
    private TextView TvOrderPrices;
    private TextView TvOrderNumber;
    private TextView TvOrderCreateTime;
    private TextView TvOrderName;
    private TextView TvOrderPhone;
    private Button BtOrderDelete;
    private TextView TvOrderPayPrice;
    private LinearLayout LLToPay;
    private RelativeLayout RlPay;

    private ImageView IvBack;
    private Button BtDelete;

    private String OrderWaitTime;
    private String OrderPages;
    private String OrderBytes;
    private String OrderChanges;
    private String OrderPrices;
    private String OrderCreateTime;
    private String OrderName;
    private String OrderPhone;
    private String OrderPayPrices;

    private static Handler handler;

    private ProgressDialog pd;
    private String OrderNumber;
    private int OrderStatus;
    private int index;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.orderinfo1);


        TvOrderDes = (TextView)findViewById(R.id.orderinfo_des);
        TvOrderWaitTime = (TextView)findViewById(R.id.orderinfo_waittime);
        TvOrderPages = (TextView)findViewById(R.id.orderinfo_pages);
        TvOrderBytes = (TextView)findViewById(R.id.orderinfo_bytes);
        TvOrderChanges = (TextView)findViewById(R.id.orderinfo_change);
        TvOrderPrices = (TextView)findViewById(R.id.orderinfo_price);
        TvOrderNumber = (TextView)findViewById(R.id.orderinfo_number);
        TvOrderCreateTime = (TextView)findViewById(R.id.orderinfo_createtime);
        TvOrderName = (TextView)findViewById(R.id.orderinfo_name);
        TvOrderPhone = (TextView)findViewById(R.id.orderinfo_phone);
        BtOrderDelete = (Button)findViewById(R.id.orderinfo_delete);
        TvOrderPayPrice = (TextView)findViewById(R.id.orderinfo_payprice);
        LLToPay = (LinearLayout)findViewById(R.id.orderinfo_topay);
        RlPay = (RelativeLayout)findViewById(R.id.orderinfo_pay);
        IvBack = (ImageView)findViewById(R.id.orderinfo_back);
        BtDelete = (Button)findViewById(R.id.orderinfo_delete);

        initHandler();
        new MyHttpUtils(handler);
        LogUtil.i("----------OrderDetailActivity----------------");

        Intent intent = getIntent();
        if (intent != null) {
            OrderNumber = intent.getStringExtra("OrderNumber");
            index = intent.getIntExtra("index",0);
        }

        initDatas(OrderNumber);

        TvOrderDes.setOnClickListener(this);
        TvOrderWaitTime.setOnClickListener(this);
        TvOrderPages.setOnClickListener(this);
        TvOrderBytes.setOnClickListener(this);
        TvOrderChanges.setOnClickListener(this);
        TvOrderPrices.setOnClickListener(this);
        TvOrderNumber.setOnClickListener(this);
        TvOrderCreateTime.setOnClickListener(this);
        TvOrderName.setOnClickListener(this);
        TvOrderPhone.setOnClickListener(this);
        BtOrderDelete.setOnClickListener(this);
        TvOrderPayPrice.setOnClickListener(this);
        LLToPay.setOnClickListener(this);
        IvBack.setOnClickListener(this);
        BtDelete.setOnClickListener(this);
    }

    public void initDatas(String oid){
        if(new ConnectionDetector(OrderDetailActivity.this).isConnectingTOInternet()){
            pd = ProgressDialog.show(OrderDetailActivity.this,"","正在查询订单....");
            RequestJson.OrderShow(oid);
        }
    }

    public void initHandler(){
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                Object obj = msg.obj;
                LogUtil.i(obj.toString());
                JSONObject json = null;
                try {
                    json = new JSONObject(obj.toString());
                    if (!json.get("code").equals("0")){
                        pd.dismiss();
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                switch (msg.what) {
                    case InfoMsg.ORDER_SHOW_TYPE:
                        try {
                            OrderPages = "16"+"张";
                            OrderBytes = json.getString("wordsRange");
                            OrderChanges = "转换方式";
                            OrderPrices = json.getString("price");
                            OrderCreateTime = json.getString("createTime");
                            OrderName =  json.getString("fullname");
                            OrderPhone = json.getString("mobile");
                            OrderStatus = Integer.valueOf(json.getString("status"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        pd.dismiss();
                        initView();
                        break;
                }
            }
        };
    }

    public void initView(){
        if(OrderStatus == 1){
            TvOrderDes.setText(R.string.orderinfo_failedtime);
            TvOrderWaitTime.setText(OrderWaitTime);
            BtOrderDelete.setVisibility(View.GONE);
        }else if (OrderStatus == 3){
            TvOrderDes.setText(R.string.orderinfo_Ocring);
            TvOrderWaitTime.setText(OrderWaitTime);
            BtOrderDelete.setVisibility(View.GONE);
        }else if(OrderStatus == 5){
            TvOrderDes.setText("交易完成 √");
            TvOrderWaitTime.setVisibility(View.GONE);
            RlPay.setVisibility(View.GONE);
        }
        TvOrderPages.setText(OrderPages);
        TvOrderBytes.setText(OrderBytes);
        TvOrderChanges.setText(OrderChanges);
        TvOrderPrices.setText("¥"+OrderPrices);
        TvOrderNumber.setText(OrderNumber);
        TvOrderCreateTime.setText(OrderCreateTime);
        TvOrderName.setText(OrderName);
        TvOrderPhone.setText(OrderPhone);
    }
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.orderinfo_back:
                finish();
                break;
            case R.id.orderinfo_delete:
                RequestJson.OrderDelete(OrderNumber);
                break;
            case R.id.orderinfo_topay:
                Intent intent = new Intent();
                intent.setClass(OrderDetailActivity.this,OrderToPay.class);
                intent.putExtra("OrderNumber", OrderNumber);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.i("==========onDestroy()============");
    }
}
