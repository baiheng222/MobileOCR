package com.hanvon.rc.orders;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.hanvon.rc.activity.MainActivity;
import com.hanvon.rc.db.OrderInfo;
import com.hanvon.rc.utils.ConnectionDetector;
import com.hanvon.rc.utils.InfoMsg;
import com.hanvon.rc.utils.LogUtil;
import com.hanvon.rc.utils.RequestJson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @Desc:
 * @Auth: chenxzhuang
 * @Time: 2016/5/9 0009.
 */
public class OrderQueryService extends Service {

    private static final int ORDER_QUERY_TIME = 1;
    private static Timer queryTimer;

    private static int INTERVAL_TIME = 0;

    private static OrderQueryService mHanVonService = null;
    private Handler msgHandler = null;

    public void handlerMsg(Message msg) throws UnsupportedEncodingException {
        switch (msg.what) {
            case ORDER_QUERY_TIME:
                if(msg.arg1 <= 0){
                    queryTimer.cancel();
                    QueryOrderPayStatus();
                }
                break;
            case InfoMsg.ORDER_ALIPAY_QUERY_ORDER_TYPE:
                Object aliobj = msg.obj;
                LogUtil.i(aliobj.toString());
                try {
                    JSONObject aliJson = new JSONObject(aliobj.toString());
                    if("0".equals(aliJson.getString("code"))){
                        if("0".equals(aliJson.getString("data"))){
                            String orderNumber = aliJson.getString("oid");
                            MainActivity.dbManager.deleteOrderFromId(orderNumber);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case InfoMsg.ORDER_QUERY_ORDER_TYPE:
                Object wxobj = msg.obj;
                LogUtil.i(wxobj.toString());
                try {
                    JSONObject wxJson = new JSONObject(wxobj.toString());
                    if("0".equals(wxJson.getString("code"))){
                        if("0".equals(wxJson.getString("data"))){
                            String orderNumber = wxJson.getString("oid");
                            MainActivity.dbManager.deleteOrderFromId(orderNumber);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private void startTime(final int minute){
        queryTimer = new Timer();
        TimerTask timerTask = new TimerTask() {
            int i = minute*60;
            @Override
            public void run() {
                Message msg = new Message();
                msg.what = ORDER_QUERY_TIME;
                msg.arg1 = i--;
                msgHandler.sendMessage(msg);
            }
        };
        queryTimer.schedule(timerTask, 3000, 1000);// 3秒后开始倒计时，倒计时间隔为1秒
    }

    private void QueryOrderPayStatus(){
        List<OrderInfo> orderInfoList = MainActivity.dbManager.QueryAllOrder();

        if(orderInfoList.size() == 0){
            if(INTERVAL_TIME == 0){
                INTERVAL_TIME = 1;
            }else{
                INTERVAL_TIME = INTERVAL_TIME*2;
            }
            startTime(INTERVAL_TIME);
            return;
        }else{
            if(new ConnectionDetector(this).isConnectingTOInternet()){
                for(int i = 0;i < orderInfoList.size();i++){
                    // 0 alipay  1 wxpay
                    if("0".equals(orderInfoList.get(i).getPayMode())){
                        RequestJson.OrderAliPayQuery(orderInfoList.get(i).getOrderNumber());
                    }else if("1".equals(orderInfoList.get(i).getPayMode())){
                        RequestJson.OrderWxQuery(orderInfoList.get(i).getOrderNumber());
                    }
                }
            }
        }
        startTime(1);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.i("-----------------1--------------------------");
        mHanVonService = this;
        initHandler();
    }

    private void initHandler(){
        msgHandler = new Handler(){
            public void handleMessage(Message msg) {
                try {
                    handlerMsg(msg);
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        };
    }
    public static boolean startService(Context context) {
        if (mHanVonService != null)
            return false;

        LogUtil.i("-----------------2--------------------------");
        Intent intent = new Intent();
        intent.setClass(context, OrderQueryService.class);
        context.startService(intent);
        return true;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void ResetTimeTask(){
        INTERVAL_TIME = 0;
        queryTimer.cancel();
        startTime(INTERVAL_TIME);
    }

    public static OrderQueryService getServiceInstance() {
        return mHanVonService;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
