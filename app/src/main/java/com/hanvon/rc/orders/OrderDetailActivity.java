package com.hanvon.rc.orders;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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

    private TextView TvOrderStatus;
    private TextView TvOrderName;
    private TextView TvOrderPages;
    private TextView TvOrderBytes;
    private TextView TvOrderWaitTime;
    private TextView TvOrderNumber;
    private TextView TvFullName;
    private Button BtDelete;
    private TextView TvOrderPrices;
    private TextView TvPayPrices;
    private ImageView IvBack;
    private  ImageView IvPay;
    private RelativeLayout Rlpay;
    private ImageView IvMsgInfo;

    private static Handler handler;
    private ProgressDialog pd;
    private int OrderStatus;

    private OrderDetail orderDetail;

    private String orderNumber;

    public static OrderDetailActivity install = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        install = this;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.orderinfo1);

        TvOrderStatus = (TextView)findViewById(R.id.orderinfo_status);
        TvOrderName = (TextView)findViewById(R.id.orderinfo_filename);
        TvOrderPages = (TextView)findViewById(R.id.orderinfo_page);
        TvOrderBytes = (TextView)findViewById(R.id.orderinfo_bytes);
        TvOrderWaitTime = (TextView)findViewById(R.id.orderinfo_waittime);
        TvOrderNumber = (TextView)findViewById(R.id.orderinfo_number);
        TvFullName = (TextView)findViewById(R.id.orderinfo_fullname);
        TvOrderPrices = (TextView)findViewById(R.id.orderinfo_price);
        BtDelete = (Button)findViewById(R.id.orderinfo_delete);
        TvPayPrices = (TextView)findViewById(R.id.orderinfo_evalprice);
        IvPay = (ImageView)findViewById(R.id.orderinfo_topay);
        IvBack = (ImageView)findViewById(R.id.orderinfo_back);
        Rlpay = (RelativeLayout)findViewById(R.id.orderinfo_pay);
        IvMsgInfo = (ImageView)findViewById(R.id.image_info);

        initHandler();
        new MyHttpUtils(handler);
        LogUtil.i("----------OrderDetailActivity----------------");

        Intent intent = getIntent();
        if (intent != null) {
            orderNumber = intent.getStringExtra("OrderNumber");
            String from = intent.getStringExtra("from");
            if("WXPAYACTIVITY".equals(from)){
                QueryWxPayResult(orderNumber);
            }else if("ORDERLISTACTIVITY".equals(from)){
                QueryOrderDetail(orderNumber);
            }else if("ALIPAYACTIVITY".equals(from)){
                QueryAliPayResult(orderNumber);
            }
            LogUtil.i("------From:" + from+"----ordernumber:"+orderNumber);
        }

        BtDelete.setOnClickListener(this);
        IvBack.setOnClickListener(this);
        IvPay.setOnClickListener(this);
    }

    public void QueryWxPayResult(String oid){
        if(new ConnectionDetector(OrderDetailActivity.this).isConnectingTOInternet()){
            pd = ProgressDialog.show(OrderDetailActivity.this,"","");
            RequestJson.OrderWxQuery(oid);
        }else{
            Toast.makeText(OrderDetailActivity.this, "请检查网络是否连通!", Toast.LENGTH_SHORT).show();
        }
    }

    public void QueryAliPayResult(String oid){
        if(new ConnectionDetector(OrderDetailActivity.this).isConnectingTOInternet()){
            pd = ProgressDialog.show(OrderDetailActivity.this,"","");
            RequestJson.OrderAliPayQuery(oid);
        }else{
            Toast.makeText(OrderDetailActivity.this,"请检查网络是否连通!",Toast.LENGTH_SHORT).show();
        }
    }

    public void QueryOrderDetail(String oid){
        if(new ConnectionDetector(OrderDetailActivity.this).isConnectingTOInternet()){
            pd = ProgressDialog.show(OrderDetailActivity.this,"","");
            RequestJson.OrderShow(oid);
        }else{
            Toast.makeText(OrderDetailActivity.this,"请检查网络是否连通!",Toast.LENGTH_SHORT).show();
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
                    if ((!json.get("code").equals("0"))&&(!json.get("code").equals("433"))){
                        pd.dismiss();
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                switch (msg.what) {
                    case InfoMsg.ORDER_DELETE_TYPE:
                        finish();
                        break;
                    case InfoMsg.ORDER_QUERY_ORDER_TYPE:
                        LogUtil.i("---QUERY--" + json.toString());
                        pd.dismiss();
                        QueryOrderDetail(orderNumber);
                        break;
                    case InfoMsg.ORDER_SHOW_TYPE:
                        orderDetail = new OrderDetail();
                        try {
                            orderDetail.setOrderFileNanme(json.getString("fileName"));
                            orderDetail.setOrderFilesPages(json.getString("fileAmount"));
                            orderDetail.setOrderFilesBytes(json.getString("wordsRange"));
                            orderDetail.setOrderWaitTime(json.getString("waitTime"));
                            orderDetail.setOrderStatus(json.getString("status"));
                            orderDetail.setOrderNumber(json.getString("oid"));
                            orderDetail.setOrderPhone(json.getString("mobile"));
                            orderDetail.setOrderName(json.getString("fullname"));
                            orderDetail.setOrderPrice(json.getString("price"));
                            pd.dismiss();
                            initView();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                    case InfoMsg.ORDER_ALIPAY_QUERY_ORDER_TYPE:
                        LogUtil.i("---QUERY--" + json.toString());
                        pd.dismiss();
                        QueryOrderDetail(orderNumber);
                        break;
                }
            }
        };
    }

    public void initView(){
        OrderStatus = Integer.valueOf(orderDetail.getOrderStatus());
        if(OrderStatus == 1){
            IvMsgInfo.setImageResource(R.mipmap.waitpay_head);
            TvOrderStatus.setText(R.string.orderlist_waitpay);
            Rlpay.setVisibility(View.VISIBLE);
        }else if (OrderStatus == 2){
            IvMsgInfo.setImageResource(R.mipmap.wait_working_head);
            TvOrderStatus.setText("待加工");
        }else if(OrderStatus == 3){
            IvMsgInfo.setImageResource(R.mipmap.working_head);
            TvOrderStatus.setText("处理中");
        }else if(OrderStatus == 4){
            IvMsgInfo.setImageResource(R.mipmap.waitdown_head);
            TvOrderStatus.setText("已取消");
        }else if(OrderStatus == 5){
            IvMsgInfo.setImageResource(R.mipmap.waitdown_head);
            TvOrderStatus.setText("已完成");
        }
        TvOrderName.setText(orderDetail.getOrderFileNanme());
        TvOrderPages.setText(orderDetail.getOrderFilesPages() + " 张");
        TvOrderBytes.setText(orderDetail.getOrderFilesBytes() + " 字节");
        TvOrderWaitTime.setText(orderDetail.getOrderWaitTime());
        TvOrderNumber.setText(orderDetail.getOrderNumber());
        TvFullName.setText(orderDetail.getOrderName()+" "+orderDetail.getOrderPhone());
        TvOrderPrices.setText(orderDetail.getOrderPrice() + " 元");
        TvPayPrices.setText("¥"+orderDetail.getOrderPrice());
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.orderinfo_back:
                Intent intent1 = new Intent();
                intent1.setClass(this,OrderListActivity.class);
                startActivity(intent1);
                finish();
                break;
            case R.id.orderinfo_delete:
             //   RequestJson.OrderDelete(OrderNumber);
             //   break;
            case R.id.orderinfo_topay:
                Intent intent = new Intent();
                intent.setClass(OrderDetailActivity.this,OrderToPay.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("ordetail", orderDetail);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.i("==========onDestroy()============");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            Intent intent = new Intent();
            intent.setClass(this,OrderListActivity.class);
            startActivity(intent);
            finish();
        }
        return true;
    }
}
