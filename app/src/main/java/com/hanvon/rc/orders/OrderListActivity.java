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
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
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
public class OrderListActivity extends Activity implements AbsListView.OnScrollListener,View.OnClickListener {
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
    private OrderAdapter mOrderAdapter =  null;
    private List<OrderDetail> mOrderDetailList = new ArrayList<OrderDetail>();

    private static Handler handler;

    private ProgressDialog pd;
    private int status = 0;
    private  boolean isItem = false;

    private int visibleLastIndex = 0;   //最后的可视项索引
    private int visibleItemCount;       // 当前窗口可见项总数
    private View loadMoreView;
    private Button loadMoreButton;

    private int pages = 0;
    private ListView listView;
    private boolean isSameView = false;
    private boolean isLoadComplate = false;
    private boolean isScrollBottom = false;

    private boolean isScrollTop = false;

    public static OrderListActivity instance = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.orderlist);

        loadMoreView = getLayoutInflater().inflate(R.layout.load_more, null);
        loadMoreButton = (Button) loadMoreView.findViewById(R.id.loadMoreButton);

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

        listView = LvOrderList;               //获取id是list的ListView

        listView.addFooterView(loadMoreView);   //设置列表底部视图
        listView.setOnScrollListener(this);     //添加滑动监听
    }

    @Override
    protected void onResume() {
        super.onResume();
        new MyHttpUtils(handler);

        LogUtil.i("--------------onResume---------Page:"+pages);
     //   pages = 0;
        if (mOrderDetailList.size() == 0) {
            initDatas("");
        }else{
         //   mOrderDetailList.clear();
         //   initDatas("");
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
                        if(isScrollTop){
                            isSameView = false;
                            isScrollTop = false;
                        }
                        int j = 0;
                        int recordCount = 0;
                        if(!isSameView) {
                            mOrderDetailList.clear();
                            if(mOrderAdapter != null){
                                mOrderAdapter.clearItem();
                                mOrderAdapter.notifyDataSetChanged(); //数据集变化后,通知adapter
                            }
                            j = 1;
                        }
                        try {
                            JSONArray jsonArray = new JSONArray(json.getString("list"));
                            recordCount = jsonArray.length();
                            for(int i = 0;i < recordCount;i++) {
                                JSONObject orderjson = jsonArray.getJSONObject(i);
                                OrderDetail orderDetail = new OrderDetail();
                                orderDetail.setOrderCreateTime(orderjson.getString("createTime"));
                                orderDetail.setOrderStatus(orderjson.getString("status"));
                                orderDetail.setOrderPrice(orderjson.getString("price"));
                                orderDetail.setOrderNumber(orderjson.getString("oid"));
                                mOrderDetailList.add(orderDetail);
                                if(isSameView) {
                             //       mOrderAdapter.addItem(orderDetail);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        pd.dismiss();
                        if(!isSameView) {
                            initView();
                        }
                        if(j == 1){
                            isSameView = true;
                            if(recordCount < 10){
                                loadMoreButton.setText("没有更多订单了!");    //恢复按钮文字
                                isLoadComplate = true;
                            }
                        }else{
                            mOrderAdapter.notifyDataSetChanged(); //数据集变化后,通知adapter
                            listView.setSelection(visibleLastIndex - visibleItemCount + 1); //设置选中项

                            if(recordCount < 10){
                                loadMoreButton.setText("没有更多订单了!");    //恢复按钮文字
                                isLoadComplate = true;
                            }else {
                                loadMoreButton.setText("点击获取更多数据!");    //恢复按钮文字
                                isLoadComplate = false;
                            }
                        }
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
            RequestJson.OrderList(status,pages);
            pages = pages + 1;
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
        loadMoreButton.setText("点击获取更多数据!");    //恢复按钮文字
        isLoadComplate = false;
        isSameView = false;
        pages = 0;
        switch (v.getId()){
            case R.id.order_all:
                if (status == 0) {
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
                if(isLoadComplate){
                    return super.dispatchTouchEvent(event);
                }
                if(isScrollBottom) {
                    isScrollBottom = false;
                    loadMoreButton.setText("加载中...");   //设置按钮文字loading
                    if (status == 0) {
                        initDatas("");
                    } else if (status == 2) {
                        initDatas("5");
                    } else if (status == 3) {
                        initDatas("1");
                    }
                }
            } else if(y2 - y1 > 50) {
             //   Toast.makeText(OrderListActivity.this, "向下滑", Toast.LENGTH_SHORT).show();
                LogUtil.i("-----------Scoll Up-----------------");
                if(isScrollTop){
                    pages = 0;
                    if (status == 0) {
                        initDatas("");
                    } else if (status == 2) {
                        initDatas("5");
                    } else if (status == 3) {
                        initDatas("1");
                    }
                }
            } else if(x1 - x2 > 50) {
              //  Toast.makeText(OrderListActivity.this, "向左滑", Toast.LENGTH_SHORT).show();
                if(status == 3){
                    ResetStatus(3,2);
                }
                if(status == 0){
                    ResetStatus(0,3);
                }
            } else if(x2 - x1 > 50) {
              //  Toast.makeText(OrderListActivity.this, "向右滑", Toast.LENGTH_SHORT).show();
                if(status == 3){
                    ResetStatus(3,0);
                }
                if(status == 2){
                    ResetStatus(2,3);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        int itemsLastIndex = mOrderAdapter.getCount() - 1;    //数据集最后一项的索引
        int lastIndex = itemsLastIndex + 1;             //加上底部的loadMoreView项
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && visibleLastIndex == lastIndex) {
            //如果是自动加载,可以在这里放置异步加载数据的代码
            LogUtil.i("------------loading more-----------");
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        LogUtil.i("---firstVisibleItem:"+firstVisibleItem+"--visibleItemCount:"+visibleItemCount+"--totalItemCount:"+totalItemCount);
        if(firstVisibleItem + visibleItemCount == totalItemCount){
            isScrollBottom = true;
        }else{
            isScrollBottom = false;
        }
        if(firstVisibleItem == 0){
            isScrollTop = true;
        }else{
            isScrollTop = false;
        }
        this.visibleItemCount = visibleItemCount;
        visibleLastIndex = firstVisibleItem + visibleItemCount - 1;
    }

    /**
     * 点击按钮事件
     * @param view
     */
    public void loadMore(View view) {
        if(isLoadComplate){
            return;
        }
        loadMoreButton.setText("加载中...");   //设置按钮文字loading
        if(status == 0) {
            initDatas("");
        }else if(status == 2){
            initDatas("5");
        }else if(status == 3){
            initDatas("1");
        }
    }

    private void ResetStatus(int srcstatus,int dststatus){
        loadMoreButton.setText("点击获取更多数据!");    //恢复按钮文字
        isLoadComplate = false;
        isSameView = false;
        pages = 0;
        if (srcstatus == 0 || srcstatus == 2){
            TvWaitDownHr.setVisibility(View.INVISIBLE);
            TvAllHr.setVisibility(View.INVISIBLE);
            TvWaitPayHr.setVisibility(View.VISIBLE);
            TvOrderAll.setTextColor(Color.parseColor("#000000"));
            TvOrderWaitDown.setTextColor(Color.parseColor("#000000"));
            TvOrderWaitPay.setTextColor(Color.parseColor("#008000"));
            initDatas("1");
        }else if(srcstatus == 3){
            TvWaitPayHr.setVisibility(View.INVISIBLE);
            TvOrderWaitPay.setTextColor(Color.parseColor("#000000"));
            if(dststatus == 2){
                TvAllHr.setVisibility(View.INVISIBLE);
                TvWaitDownHr.setVisibility(View.VISIBLE);
                TvOrderAll.setTextColor(Color.parseColor("#000000"));
                TvOrderWaitDown.setTextColor(Color.parseColor("#008000"));
                initDatas("5");
            }else if(dststatus == 0){
                TvWaitDownHr.setVisibility(View.INVISIBLE);
                TvAllHr.setVisibility(View.VISIBLE);
                TvOrderAll.setTextColor(Color.parseColor("#008000"));
                TvOrderWaitDown.setTextColor(Color.parseColor("#000000"));
                initDatas("");
            }
        }
        status = dststatus;
    }
}
