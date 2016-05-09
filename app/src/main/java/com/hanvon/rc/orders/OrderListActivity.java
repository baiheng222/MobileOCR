package com.hanvon.rc.orders;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hanvon.rc.R;
import com.hanvon.rc.utils.ConnectionDetector;
import com.hanvon.rc.utils.InfoMsg;
import com.hanvon.rc.utils.LogUtil;
import com.hanvon.rc.utils.MyHttpUtils;
import com.hanvon.rc.utils.RequestJson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @Desc:
 * @Auth: chenxzhuang
 * @Time: 2016/3/28 0028.
 */
public class OrderListActivity extends Activity implements View.OnClickListener{
    private TextView TvOrderAll;
    private TextView TvOrderWaitPay;
    private TextView TvOrderWaitDown;
    private TextView TvAllHr;
    private TextView TvWaitPayHr;
    private  TextView TvWaitDownHr;
    private TextView TvBack;

    private TextView TvOrderEmpty;
    private ListView LvOrderList;

   // private ListView mOrderList;
    private OrderAdapter mOrderAdapter;
    private List<OrderDetail> mOrderDetailList = new ArrayList<OrderDetail>();

    private static Handler handler;

    private ProgressDialog pd;
    private int status = 0;
    private  boolean isItem = false;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.orderlist);

        initHandler();

        LogUtil.i("----------OrderListActivity----------------");

        TvOrderAll = (TextView)findViewById(R.id.order_all);
        TvOrderWaitPay = (TextView)findViewById(R.id.order_waitpay);
        TvOrderWaitDown = (TextView)findViewById(R.id.order_waitdown);
        TvOrderEmpty = (TextView)findViewById(R.id.orderlist_empty);
        LvOrderList = (ListView)findViewById(R.id.orderlist);
        TvAllHr = (TextView)findViewById(R.id.all_hr);
        TvWaitDownHr = (TextView)findViewById(R.id.waitdown_hr);
        TvWaitPayHr = (TextView)findViewById(R.id.waitpay_hr);
        TvBack = (TextView)findViewById(R.id.orderlist_back);

        TvOrderAll.setOnClickListener(this);
        TvOrderWaitPay.setOnClickListener(this);
        TvOrderWaitDown.setOnClickListener(this);
        TvBack.setOnClickListener(this);

        TvWaitDownHr.setVisibility(View.INVISIBLE);
        TvWaitPayHr.setVisibility(View.INVISIBLE);

        TvOrderAll.setTextColor(Color.parseColor("#008000"));
        TvOrderWaitDown.setTextColor(Color.parseColor("#000000"));
        TvOrderWaitPay.setTextColor(Color.parseColor("#000000"));
    }

    @Override
    protected void onResume() {
        super.onResume();
        new MyHttpUtils(handler);

        if (mOrderDetailList.size() == 0) {
            initDatas("");
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
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                switch (msg.what) {
                    case InfoMsg.ORDER_LIST_TYPE:
                        mOrderDetailList.clear();
                        try {
                            JSONArray jsonArray = new JSONArray(json.getString("list"));
                            for(int i = 0;i < jsonArray.length();i++) {
                                JSONObject orderjson = jsonArray.getJSONObject(i);
                                OrderDetail orderDetail = new OrderDetail();
                                orderDetail.setOrderCreateTime(orderjson.getString("createTime"));
                                orderDetail.setOrderStatus(orderjson.getString("status"));
                                orderDetail.setOrderPrice(orderjson.getString("price"));
                                orderDetail.setOrderNumber(orderjson.getString("oid"));
                                mOrderDetailList.add(orderDetail);
                            }
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
    public void initDatas(String status){
        if(new ConnectionDetector(OrderListActivity.this).isConnectingTOInternet()){
            if((null != pd)&&(pd.isShowing())){
                pd.dismiss();
            }
            pd = ProgressDialog.show(OrderListActivity.this,"","正在查询订单....");
            RequestJson.OrderList(status);
        }else{
            Toast.makeText(OrderListActivity.this,"请检查网络是否连通!",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if((null != pd)&&(pd.isShowing())){
            pd.dismiss();
        }
        isItem = false;
    }

    public void initView(){
        mOrderAdapter = new OrderAdapter(this, mOrderDetailList);
        LvOrderList.setAdapter(mOrderAdapter);
        LvOrderList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                isItem = true;
                LogUtil.i("item " + position + " clicked");
                OrderDetail orderDetail = mOrderDetailList.get(position);
                String orderStatus = orderDetail.getOrderStatus();
                Intent newIntent = new Intent(OrderListActivity.this, OrderDetailActivity.class);
                newIntent.putExtra("OrderNumber", orderDetail.getOrderNumber());
                newIntent.putExtra("from", "ORDERLISTACTIVITY");
                startActivity(newIntent);
            }
        });

        if (mOrderDetailList.size() <= 0){
            LvOrderList.setVisibility(View.GONE);
            TvOrderEmpty.setVisibility(View.VISIBLE);
        }else {
            LvOrderList.setVisibility(View.VISIBLE);
            TvOrderEmpty.setVisibility(View.GONE);
        }
        LogUtil.i("======Leave initView============");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.order_all:
                if (status == 0){
                    break;
                }
                TvWaitDownHr.setVisibility(View.INVISIBLE);
                TvWaitPayHr.setVisibility(View.INVISIBLE);
                TvAllHr.setVisibility(View.VISIBLE);
                TvOrderAll.setTextColor(Color.parseColor("#008000"));
                TvOrderWaitDown.setTextColor(Color.parseColor("#000000"));
                TvOrderWaitPay.setTextColor(Color.parseColor("#000000"));
                initDatas("");
                status = 0;
                break;
            case R.id.order_waitdown:
                if (status == 2){
                    break;
                }
                TvAllHr.setVisibility(View.INVISIBLE);
                TvWaitPayHr.setVisibility(View.INVISIBLE);
                TvWaitDownHr.setVisibility(View.VISIBLE);
                TvOrderAll.setTextColor(Color.parseColor("#000000"));
                TvOrderWaitDown.setTextColor(Color.parseColor("#008000"));
                TvOrderWaitPay.setTextColor(Color.parseColor("#000000"));
                initDatas("5");
                status = 2;
                break;
            case R.id.order_waitpay:
                if (status == 3){
                    break;
                }
                TvWaitDownHr.setVisibility(View.INVISIBLE);
                TvAllHr.setVisibility(View.INVISIBLE);
                TvWaitPayHr.setVisibility(View.VISIBLE);
                TvOrderAll.setTextColor(Color.parseColor("#000000"));
                TvOrderWaitDown.setTextColor(Color.parseColor("#000000"));
                TvOrderWaitPay.setTextColor(Color.parseColor("#008000"));
                initDatas("1");
                status = 3;
                break;
            case R.id.orderlist_back:
                finish();
                break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.i("==========onDestroy()============");
        if( OrderDetailActivity.install != null) {
            OrderDetailActivity.install.finish();
        }
        if(OrderEvalPrices.instance != null) {
            OrderEvalPrices.instance.finish();
        }
        if(OrderToPay.instance != null) {
            OrderToPay.instance.finish();
        }
    }


    float x1 = 0;
    float x2 = 0;
    float y1 = 0;
    float y2 = 0;
    public boolean dispatchTouchEvent(MotionEvent event) {
        if(isItem){
            return super.dispatchTouchEvent(event);
        }
        //继承了Activity的onTouchEvent方法，直接监听点击事件
        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            //当手指按下的时候
            x1 = event.getX();
            y1 = event.getY();
        }
        if(event.getAction() == MotionEvent.ACTION_UP) {
            //当手指离开的时候
            x2 = event.getX();
            y2 = event.getY();
            if(y1 - y2 > 50) {
             //   Toast.makeText(OrderListActivity.this, "向上滑", Toast.LENGTH_SHORT).show();
            } else if(y2 - y1 > 50) {
             //   Toast.makeText(OrderListActivity.this, "向下滑", Toast.LENGTH_SHORT).show();
            } else if(x1 - x2 > 50) {
              //  Toast.makeText(OrderListActivity.this, "向左滑", Toast.LENGTH_SHORT).show();
                if(status == 3){
                    TvAllHr.setVisibility(View.INVISIBLE);
                    TvWaitPayHr.setVisibility(View.INVISIBLE);
                    TvWaitDownHr.setVisibility(View.VISIBLE);
                    TvOrderAll.setTextColor(Color.parseColor("#000000"));
                    TvOrderWaitDown.setTextColor(Color.parseColor("#008000"));
                    TvOrderWaitPay.setTextColor(Color.parseColor("#000000"));
                    initDatas("5");
                    status = 2;
                }
                if(status == 0){
                    TvWaitDownHr.setVisibility(View.INVISIBLE);
                    TvAllHr.setVisibility(View.INVISIBLE);
                    TvWaitPayHr.setVisibility(View.VISIBLE);
                    TvOrderAll.setTextColor(Color.parseColor("#000000"));
                    TvOrderWaitDown.setTextColor(Color.parseColor("#000000"));
                    TvOrderWaitPay.setTextColor(Color.parseColor("#008000"));
                    initDatas("1");
                    status = 3;
                }
            } else if(x2 - x1 > 50) {
              //  Toast.makeText(OrderListActivity.this, "向右滑", Toast.LENGTH_SHORT).show();
                if(status == 3){
                    TvWaitDownHr.setVisibility(View.INVISIBLE);
                    TvWaitPayHr.setVisibility(View.INVISIBLE);
                    TvAllHr.setVisibility(View.VISIBLE);
                    TvOrderAll.setTextColor(Color.parseColor("#008000"));
                    TvOrderWaitDown.setTextColor(Color.parseColor("#000000"));
                    TvOrderWaitPay.setTextColor(Color.parseColor("#000000"));
                    initDatas("");
                    status = 0;
                }
                if(status == 2){
                    TvWaitDownHr.setVisibility(View.INVISIBLE);
                    TvAllHr.setVisibility(View.INVISIBLE);
                    TvWaitPayHr.setVisibility(View.VISIBLE);
                    TvOrderAll.setTextColor(Color.parseColor("#000000"));
                    TvOrderWaitDown.setTextColor(Color.parseColor("#000000"));
                    TvOrderWaitPay.setTextColor(Color.parseColor("#008000"));
                    initDatas("1");
                    status = 3;
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }
}
