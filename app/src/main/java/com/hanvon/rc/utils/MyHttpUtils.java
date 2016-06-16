package com.hanvon.rc.utils;

import android.os.Handler;
import android.os.Message;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;


/**
 * @Desc:
 * @Auth: chenxzhuang
 * @Time: 2016/3/25 0025.
 */
public class MyHttpUtils {

    private static Handler handler;
    private static long offset = 0;

    public MyHttpUtils(Handler handler){
        this.handler = handler;
    }

    public static void HttpSend(String Url, JSONObject json, final int type){
        HttpUtils http = new HttpUtils();
        RequestParams params = new RequestParams();
        try {
            params.setBodyEntity(new StringEntity(json.toString()));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        http.send(HttpRequest.HttpMethod.POST,Url,params, new RequestCallBack<String>() {
            @Override
            public void onStart() {
            }
            @Override
            public void onLoading(long total, long current, boolean isUploading) {
            }
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                LogUtil.i("===onSuccess====="+responseInfo.result.toString());

                Message msg = new Message();
                msg.what = type;
                msg.obj = responseInfo.result.toString();
                handler.sendMessage(msg);
            }
            @Override
            public void onFailure(com.lidroid.xutils.exception.HttpException e, String s)
            {
                LogUtil.i("===onFailure====="+s);
                Message msg = Message.obtain();
                msg.what = InfoMsg.NET_ERR_SOCKET_TIMEOUT;
                msg.obj = s;
                handler.sendMessage(msg);
            }
        });
    }
}
