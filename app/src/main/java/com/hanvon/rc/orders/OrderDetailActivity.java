package com.hanvon.rc.orders;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hanvon.rc.R;
import com.hanvon.rc.activity.MainActivity;
import com.hanvon.rc.db.OrderInfo;
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
public class OrderDetailActivity extends Activity implements View.OnClickListener {

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
    private ImageView IvPay;
    private RelativeLayout Rlpay;
    private ImageView IvMsgInfo;

    private static Handler handler;
    private ProgressDialog pd;
    private int OrderStatus;

    private OrderDetail orderDetail;

    private String orderNumber;
    private boolean isPayComplete = false;
    private TextView TvService;

    public static OrderDetailActivity install = null;
    public boolean isOrderChange = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        install = this;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.orderinfo1);


        TvOrderStatus = (TextView) findViewById(R.id.orderinfo_status);
        TvOrderName = (TextView) findViewById(R.id.orderinfo_filename);
        TvOrderPages = (TextView) findViewById(R.id.orderinfo_page);
        TvOrderBytes = (TextView) findViewById(R.id.orderinfo_bytes);
        TvOrderWaitTime = (TextView) findViewById(R.id.orderinfo_waittime);
        TvOrderNumber = (TextView) findViewById(R.id.orderinfo_number);
        TvFullName = (TextView) findViewById(R.id.orderinfo_fullname);
        TvOrderPrices = (TextView) findViewById(R.id.orderinfo_price);
        BtDelete = (Button) findViewById(R.id.orderinfo_delete);
        TvPayPrices = (TextView) findViewById(R.id.orderinfo_evalprice);
        IvPay = (ImageView) findViewById(R.id.orderinfo_topay);
        IvBack = (ImageView) findViewById(R.id.orderinfo_back);
        Rlpay = (RelativeLayout) findViewById(R.id.orderinfo_pay);
        IvMsgInfo = (ImageView) findViewById(R.id.image_info);
        TvService = (TextView) findViewById(R.id.hvn_service);

        initHandler();
        new MyHttpUtils(handler);
        LogUtil.i("----------OrderDetailActivity----------------");

        Intent intent = getIntent();
        if (intent != null) {
            orderNumber = intent.getStringExtra("OrderNumber");
            String from = intent.getStringExtra("from");
            if ("WXPAYACTIVITY".equals(from)) {
                QueryWxPayResult(orderNumber);
                isOrderChange = true;
            } else if ("ORDERLISTACTIVITY".equals(from)) {
                QueryOrderDetail(orderNumber);
            } else if ("ALIPAYACTIVITY".equals(from)) {
                QueryAliPayResult(orderNumber);
                isOrderChange = true;
            }
            LogUtil.i("------From:" + from + "----ordernumber:" + orderNumber);
        }

        BtDelete.setOnClickListener(this);
        IvBack.setOnClickListener(this);
        IvPay.setOnClickListener(this);
        TvService.setOnClickListener(this);
    }

    public void QueryWxPayResult(String oid) {
        if (new ConnectionDetector(OrderDetailActivity.this).isConnectingTOInternet()) {
            pd = ProgressDialog.show(OrderDetailActivity.this, "", "");
            RequestJson.OrderWxQuery(oid);
        } else {
            Toast.makeText(OrderDetailActivity.this, "请检查网络是否连通!", Toast.LENGTH_SHORT).show();
        }
    }

    public void QueryAliPayResult(String oid) {
        if (new ConnectionDetector(OrderDetailActivity.this).isConnectingTOInternet()) {
            pd = ProgressDialog.show(OrderDetailActivity.this, "", "");
            RequestJson.OrderAliPayQuery(oid);
        } else {
            Toast.makeText(OrderDetailActivity.this, "请检查网络是否连通!", Toast.LENGTH_SHORT).show();
        }
    }

    public void QueryOrderDetail(String oid) {
        if (new ConnectionDetector(OrderDetailActivity.this).isConnectingTOInternet()) {
            pd = ProgressDialog.show(OrderDetailActivity.this, "", "");
            RequestJson.OrderShow(oid);
        } else {
            Toast.makeText(OrderDetailActivity.this, "请检查网络是否连通!", Toast.LENGTH_SHORT).show();
        }
    }

    public void initHandler() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Object obj = msg.obj;
                LogUtil.i(obj.toString());
                JSONObject json = null;
                try {
                    json = new JSONObject(obj.toString());
                    if ((!json.get("code").equals("0")) && (!json.get("code").equals("433"))) {
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
                        try {
                            if (json.get("code").equals("433")) {
                                isPayComplete = true;
                                OrderInfo orderInfo = new OrderInfo();
                                orderInfo.setOrderNumber(orderNumber);
                                orderInfo.setPayMode("1");
                                MainActivity.dbManager.insertOrder(orderInfo);
                                OrderQueryService.getServiceInstance().ResetTimeTask();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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
                            String contactId = json.getString("contactId");
                            if(null == contactId || "null".equals(contactId) || "".equals(contactId)){
                                orderDetail.setContactId("");
                            }else{
                                orderDetail.setContactId(contactId);
                            }
                            String mobile = json.getString("mobile");
                            if(null == mobile || "null".equals(mobile) || "".equals(mobile)){
                                orderDetail.setOrderPhone("");
                            }else{
                                orderDetail.setOrderPhone(mobile);
                            }
                            String fullname = json.getString("fullname");
                            if(null == fullname || "null".equals(fullname) || "".equals(fullname)){
                                orderDetail.setOrderName("");
                            }else{
                                orderDetail.setOrderName(fullname);
                            }
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
                        try {
                            if (json.get("code").equals("433")) {
                                isPayComplete = true;
                                OrderInfo orderInfo = new OrderInfo();
                                orderInfo.setOrderNumber(orderNumber);
                                orderInfo.setPayMode("0");
                                MainActivity.dbManager.insertOrder(orderInfo);
                                OrderQueryService.getServiceInstance().ResetTimeTask();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        QueryOrderDetail(orderNumber);
                        break;
                }
            }
        };
    }

    public void initView() {
        OrderStatus = Integer.valueOf(orderDetail.getOrderStatus());
        if (OrderStatus == 1) {
            IvMsgInfo.setImageResource(R.mipmap.waitpay_head);
            TvOrderStatus.setText(R.string.orderlist_waitpay);
            Rlpay.setVisibility(View.VISIBLE);
        } else if (OrderStatus == 3 || OrderStatus == 2) {
            IvMsgInfo.setImageResource(R.mipmap.working_head);
            TvOrderStatus.setText("处理中");
        } else if (OrderStatus == 4) {
            IvMsgInfo.setImageResource(R.mipmap.order_cancel_head);
            TvOrderStatus.setText("已取消");
        } else if (OrderStatus == 5) {
            IvMsgInfo.setImageResource(R.mipmap.waitdown_head);
            TvOrderStatus.setText("已完成");
        }
        TvOrderName.setText(orderDetail.getOrderFileNanme());
        TvOrderPages.setText(orderDetail.getOrderFilesPages() + " 张");
        TvOrderBytes.setText(orderDetail.getOrderFilesBytes() + " 字节");
        TvOrderWaitTime.setText(orderDetail.getOrderWaitTime());
        TvOrderNumber.setText(orderDetail.getOrderNumber());
        TvFullName.setText(orderDetail.getOrderName() + " " + orderDetail.getOrderPhone());
        TvOrderPrices.setText(orderDetail.getOrderPrice() + " 元");
        TvPayPrices.setText("¥" + orderDetail.getOrderPrice());

        if (isPayComplete) {
            Toast.makeText(this, "如果已经支付完成，状态不一致，请稍后进行支付状态查看!", Toast.LENGTH_SHORT).show();
            isPayComplete = false;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.orderinfo_back:
                if(isOrderChange){
                    OrderListActivity.instance.finish();
                    isOrderChange = false;
                }
                Intent intent1 = new Intent();
                intent1.setClass(this, OrderListActivity.class);
                startActivity(intent1);
                finish();
                break;
            case R.id.orderinfo_delete:
                //   RequestJson.OrderDelete(OrderNumber);
                //   break;
            case R.id.orderinfo_topay:
                Intent intent = new Intent();
                intent.setClass(OrderDetailActivity.this, OrderToPay.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("ordetail", orderDetail);
                intent.putExtras(bundle);
                intent.putExtra("from","DETAIL");
                startActivity(intent);
                break;
            case R.id.hvn_service:
                LayoutInflater inflater = getLayoutInflater();
                View view = inflater.inflate(R.layout.dailcall, null);
                final AlertDialog dialog = new AlertDialog.Builder(this)
                        .setView(view).show();
                TextView TvCallDail = (TextView)view.findViewById(R.id.call_service);
                TvCallDail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent callintent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "010-82786699"));
                        if (ActivityCompat.checkSelfPermission(OrderDetailActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        startActivity(callintent);
                        dialog.dismiss();
                    }
                });
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
            if(isOrderChange){
                OrderListActivity.instance.finish();
                isOrderChange = false;
            }
            Intent intent = new Intent();
            intent.setClass(this,OrderListActivity.class);
            startActivity(intent);
            finish();
        }
        return true;
    }
}
