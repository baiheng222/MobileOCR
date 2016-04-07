package com.hanvon.rc.orders;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

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
                                orderDetail.setOrderPrice("¥"+orderjson.getString("price"));
                                orderDetail.setOrderNumber(orderjson.getString("oid"));
                                orderDetail.setOrderTitle(orderjson.getString("fileName") + i);
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
            pd = ProgressDialog.show(OrderListActivity.this,"","正在查询订单....");
            RequestJson.OrderList(status);
        }
    }

    public void initView(){


        mOrderAdapter = new OrderAdapter(this, mOrderDetailList);
        LvOrderList.setAdapter(mOrderAdapter);
        LvOrderList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LogUtil.i("item " + position + " clicked");
                OrderDetail orderDetail = mOrderDetailList.get(position);
                String orderStatus = orderDetail.getOrderStatus();
                Intent newIntent = new Intent(OrderListActivity.this, OrderDetailActivity.class);
                newIntent.putExtra("OrderNumber", orderDetail.getOrderNumber());
                newIntent.putExtra("index",position);
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
    }
}
