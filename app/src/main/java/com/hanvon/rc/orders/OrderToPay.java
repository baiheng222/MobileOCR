package com.hanvon.rc.orders;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.hanvon.rc.R;
import com.hanvon.rc.application.HanvonApplication;
import com.hanvon.rc.utils.ConnectionDetector;
import com.hanvon.rc.utils.InfoMsg;
import com.hanvon.rc.utils.LogUtil;
import com.hanvon.rc.utils.MyHttpUtils;
import com.hanvon.rc.utils.RequestJson;
import com.tencent.mm.sdk.modelpay.PayReq;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;

/**
 * @Desc:
 * @Auth: chenxzhuang
 * @Time: 2016/3/31 0031.
 */
public class OrderToPay extends Activity implements View.OnClickListener {

    private ImageView IvBack;
    private RadioButton RbWxPay;
    private RadioButton RbZfbPay;
    private TextView TvOrderNumber;
    private TextView TvOrderName;
    private TextView TvOrderPrices;
    private ImageView TvPay;


    private boolean isWxPay = true;
    private boolean isZfbPay = false;

    private static Handler handler;
    private OrderDetail orderDetail;
    private ProgressDialog pd;
    public static OrderToPay instance = null;

    public boolean isOrderChange = false;

    private static final int SDK_PAY_FLAG = 1;

    private int InFrom = 0; //0 from evalprices  1 from orderdetail
    private String resultFileType;

    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            PdDimiss();
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    PayResult payResult = new PayResult((String) msg.obj);
                    /**
                     * 同步返回的结果必须放置到服务端进行验证（验证的规则请看https://doc.open.alipay.com/doc2/
                     * detail.htm?spm=0.0.0.0.xdvAU6&treeId=59&articleId=103665&
                     * docType=1) 建议商户依赖异步通知
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    LogUtil.i("------resultInfo:"+resultInfo);
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        Toast.makeText(OrderToPay.this, "支付成功", Toast.LENGTH_SHORT).show();
                        if(OrderDetailActivity.install != null) {
                            OrderDetailActivity.install.finish();
                        }
                        Intent intent = new Intent();
                        intent.setClass(OrderToPay.this, OrderDetailActivity.class);
                        intent.putExtra("OrderNumber", HanvonApplication.CurrentOid);
                        intent.putExtra("from", "ALIPAYACTIVITY");
                        HanvonApplication.CurrentOid = "";
                        startActivity(intent);
                        finish();
                    } else {
                        // 判断resultStatus 为非"9000"则代表可能支付失败
                        // "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            Toast.makeText(OrderToPay.this, "支付结果确认中", Toast.LENGTH_SHORT).show();
                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            Toast.makeText(OrderToPay.this, "支付失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                }
                default:
                    break;
            }
        };
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.payinfo);

        Intent intent = getIntent();
        if (intent != null) {
            orderDetail = (OrderDetail)intent.getSerializableExtra("ordetail");
            String from = intent.getStringExtra("from");
            if("EVAL_PRICES".equals(from)){
                InFrom = 0;
                resultFileType = intent.getStringExtra("resultfiletype");
            }else{
                InFrom = 1;
                resultFileType = "txt";
            }
        }

        InitView();
        initHandler();
        new MyHttpUtils(handler);
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
                    if (!json.get("code").equals("0")){
                        PdDimiss();
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                switch(msg.what) {
                    case InfoMsg.ORDER_ADD_TYPE:
                        try {
                            if ("0".equals(json.getString("code"))){
                                orderDetail.setOrderNumber(json.getString("oid"));
                                HanvonApplication.CurrentOid = json.getString("oid");
                                LogUtil.i("----CurrentOid:"+HanvonApplication.CurrentOid);
                                if (isZfbPay){
                               //     GetAliPaySign();
                                    RequestJson.GetAliPaySign(orderDetail.getOrderNumber(),orderDetail.getOrderPrice());
                              //     AliPay aliPay = new AliPay(OrderToPay.this,OrderToPay.this);
                               //     aliPay.pay(orderDetail.getOrderPrice(), orderDetail.getOrderNumber());
                                }else{
                                    RequestJson.WxPay(orderDetail.getOrderPrice(), orderDetail.getOrderNumber());
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                    case InfoMsg.ORDER_WXPAY_TYPE:
                        PayReq req = new PayReq();
                        //req.appId = "wxf8b4f85f3a794e77";  // 测试用appId
                        try {
                            req.appId			= json.getString("appId");
                            req.partnerId		= json.getString("partnerId");
                            req.prepayId		= json.getString("prepayId");
                            req.nonceStr		= json.getString("nonceStr");
                            req.timeStamp		= json.getString("timeStamp");;
                            req.packageValue	= "Sign=WXPay";
                            req.sign			= json.getString("reSign");
                            req.extData			= "app data"; // optional
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        HanvonApplication.api.sendReq(req);
                        break;
                    case InfoMsg.ORDER_ALIPAY_SIGN_ORDER_TYPE:
                        String sign = null;
                        String orderInfo = null;
                        try {
                            sign = json.getString("sign");
                            sign = URLEncoder.encode(sign, "UTF-8");
                            orderInfo = json.getString("orderInfo");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        final String payInfo = orderInfo + "&sign=\"" + sign + "\"&" + "sign_type=\"RSA\"";

                        Runnable payRunnable = new Runnable() {

                            @Override
                            public void run() {
                                // 构造PayTask 对象
                                PayTask alipay = new PayTask(OrderToPay.this);
                                // 调用支付接口，获取支付结果
                                String result = alipay.pay(payInfo, true);

                                Message msg = new Message();
                                msg.what = SDK_PAY_FLAG;
                                msg.obj = result;
                                mHandler.sendMessage(msg);
                            }
                        };

                        // 必须异步调用
                        Thread payThread = new Thread(payRunnable);
                        payThread.start();
                        break;

                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        PdDimiss();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        InitView();
    }

    public void InitView(){
        IvBack = (ImageView)findViewById(R.id.payinfo_back);
        RbWxPay = (RadioButton)findViewById(R.id.payinfo_wxpay);
        RbZfbPay = (RadioButton)findViewById(R.id.payinfo_zfbpay);
        TvOrderNumber = (TextView)findViewById(R.id.payinfo_number);
        TvOrderName = (TextView)findViewById(R.id.payinfo_name);
        TvOrderPrices = (TextView)findViewById(R.id.payinfo_evalprice);
        TvPay = (ImageView)findViewById(R.id.payinfo_topay);

        IvBack.setOnClickListener(this);
        RbWxPay.setOnClickListener(this);
        RbZfbPay.setOnClickListener(this);
        TvPay.setOnClickListener(this);
        TvOrderNumber.setText(orderDetail.getOrderNumber());
        TvOrderName.setText(orderDetail.getOrderName() + " " + orderDetail.getOrderPhone());
        TvOrderPrices.setText(orderDetail.getOrderPrice() + " 元");

        RbWxPay.setChecked(true);
        RbZfbPay.setChecked(false);
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
                if(InFrom == 1) {
                    if (isOrderChange) {
                        OrderListActivity.instance.finish();
                        isOrderChange = false;
                    }
                    Intent intent = new Intent();
                    intent.setClass(this, OrderListActivity.class);
                    startActivity(intent);
                }
                finish();
                break;
            case R.id.payinfo_topay:
                LogUtil.i("*************resultFileType:"+resultFileType);
                if(new ConnectionDetector(OrderToPay.this).isConnectingTOInternet()) {
                    isOrderChange = true;
                    //支付宝支付 11  微信支付 12
                    if (isZfbPay) {
                        pd = ProgressDialog.show(this, "", "");
                        RequestJson.OrderAdd(orderDetail, "11",resultFileType);
                    } else {
                        pd = ProgressDialog.show(this, "", "");
                        RequestJson.OrderAdd(orderDetail, "12",resultFileType);
                    }
                }else{
                    Toast.makeText(this,"请检查网络是否连通!",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    private void PdDimiss(){
        if ((null != pd)&&(pd.isShowing())){
            pd.dismiss();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        LogUtil.i("----------------------------------");
        if(keyCode == KeyEvent.KEYCODE_BACK){
            if(InFrom == 1) {
                if (isOrderChange) {
                    OrderListActivity.instance.finish();
                    isOrderChange = false;
                }
                Intent intent = new Intent();
                intent.setClass(this, OrderListActivity.class);
                startActivity(intent);
            }
            finish();
        }
        return true;
    }
}
