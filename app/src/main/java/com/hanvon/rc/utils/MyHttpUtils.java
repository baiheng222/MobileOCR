package com.hanvon.rc.utils;

import android.os.Handler;
import android.os.Message;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import java.io.File;
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
            public void onFailure(com.lidroid.xutils.exception.HttpException e, String s) {
                LogUtil.i("===onFailure====="+s);
            }
        });
    }

    public static void HttpDownFile(String Url, JSONObject json, final int type){
        HttpUtils http = new HttpUtils();
        RequestParams params = new RequestParams();
      //  HttpHandler handler = http.download();
        try {
            params.setBodyEntity(new StringEntity(json.toString()));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        HttpHandler handler = http.download(HttpRequest.HttpMethod.POST,Url,"/sdcard/down.txt",params,
                true, // 如果目标文件存在，接着未完成的部分继续下载。
                false, // 如果从请求返回信息中获取到文件名，下载完成后自动重命名。
                new RequestCallBack<File>() {

                    @Override
                    public void onStart() {
                    //    resultText.setText("conn...");
                        LogUtil.i("====onStart====conn...");
                    }

                    @Override
                    public void onSuccess(ResponseInfo<File> responseInfo) {
                        LogUtil.i("====onSuccess===="+responseInfo.result.toString());
                        LogUtil.i("====onSuccess===="+responseInfo.result.length()+"");
                        offset = offset+responseInfo.result.length();
                        long w = offset;
                        if (responseInfo.result.length() <= 32768){
                            LogUtil.i("====onSuccess===="+" Go On......"+w);
                            RequestJson.FileDown(w);
                        }
                    }

                    @Override
                    public void onFailure(HttpException error, String msg) {
                      //  resultText.setText(error.getExceptionCode() + ":" + msg);
                        LogUtil.i("====onFailure===="+msg);
                    }
                });
    }
}
