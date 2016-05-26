package com.hanvon.rc.utils;

import android.util.Log;

import com.hanvon.rc.application.HanvonApplication;
import com.hanvon.rc.orders.OrderDetail;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @Desc:
 * @Auth: chenxzhuang
 * @Time: 2016/3/26 0026.
 */
public class RequestJson {

    public static void GetRapidRecogRet(String uid, String fid, String restype, String platformtype)
    {
        JSONObject JSuserInfoJson = new JSONObject();
        try
        {
            JSONObject conditionJson = new JSONObject();
            JSuserInfoJson.put("userid", uid);
            JSuserInfoJson.put("fid", fid);
            JSuserInfoJson.put("resType", restype);
            JSuserInfoJson.put("platformtype", platformtype);

        }
        catch(JSONException e)
        {
            e.printStackTrace();
        }
        Log.i("-----",JSuserInfoJson.toString());
        MyHttpUtils.HttpSend(InfoMsg.UrlRapidRecog, JSuserInfoJson, InfoMsg.FILE_RECOGINE_TYPE);
    }

    public static void GetFilesList() {
        JSONObject JSuserInfoJson = new JSONObject();
        try
        {
            JSONObject conditionJson = new JSONObject();
            //     conditionJson.put("beginTime", SyncInfo.HvnOldSynchroTime);
            //    conditionJson.put("endTime", SyncInfo.HvnSystemCurTime);
            JSuserInfoJson.put("userid", HanvonApplication.hvnName);
            JSuserInfoJson.put("start", "0");
            JSuserInfoJson.put("pageSize", "100");
            JSuserInfoJson.put("sort", conditionJson);
            JSuserInfoJson.put("fileType", "2");
            JSuserInfoJson.put("recogType", "");
        } catch(JSONException e) {
            e.printStackTrace();
        }
        Log.i("-----",JSuserInfoJson.toString());
        MyHttpUtils.HttpSend(InfoMsg.UrlFileList,JSuserInfoJson,InfoMsg.FILE_LIST_TYPE);
    }

    public static void FileRename(String fuid,String newName){
        JSONObject JSuserInfoJson = new JSONObject();
        try
        {
            JSuserInfoJson.put("userid", HanvonApplication.hvnName);
            JSuserInfoJson.put("fid", fuid);
            JSuserInfoJson.put("fileName", newName);
            JSuserInfoJson.put("fileType", "1");
        } catch(JSONException e) {
            e.printStackTrace();
        }
        Log.i("-----",JSuserInfoJson.toString());
        MyHttpUtils.HttpSend(InfoMsg.UrlFileRename,JSuserInfoJson,InfoMsg.FILE_RENAME_TYPE);
    }

    public static void FileShow(int tyep,String fuid){
        JSONObject JSuserInfoJson = new JSONObject();
        try
        {
            JSuserInfoJson.put("userid", HanvonApplication.hvnName);
            JSuserInfoJson.put("fid", fuid);
            JSuserInfoJson.put("fileType", String.valueOf(tyep));
        } catch(JSONException e) {
            e.printStackTrace();
        }
        Log.i("-----",JSuserInfoJson.toString());
        MyHttpUtils.HttpSend(InfoMsg.UrlFileShow,JSuserInfoJson,InfoMsg.FILE_SHOW_TYPE);
    }
    public static void FileDelete(int tyep,String fuid){
        JSONObject JSuserInfoJson = new JSONObject();
        try
        {
            JSuserInfoJson.put("userid", HanvonApplication.hvnName);
            JSuserInfoJson.put("fid", fuid);
            JSuserInfoJson.put("fileType", String.valueOf(tyep));
        } catch(JSONException e) {
            e.printStackTrace();
        }
        Log.i("-----",JSuserInfoJson.toString());
        MyHttpUtils.HttpSend(InfoMsg.UrlFileDelete, JSuserInfoJson, InfoMsg.FILE_DELETE_TYPE);
    }
    public static void FileCheckSum(String path){
        JSONObject JSuserInfoJson = new JSONObject();
        try
        {
            JSuserInfoJson.put("userid", HanvonApplication.hvnName);
            JSuserInfoJson.put("filePath", path);
        } catch(JSONException e) {
            e.printStackTrace();
        }
        Log.i("-----",JSuserInfoJson.toString());
        MyHttpUtils.HttpSend(InfoMsg.UrlFileCheckSum, JSuserInfoJson, InfoMsg.FILE_CHECKSUM_TYPE);
    }

    public static void FileDown(long offset,long length,String fid,String path){
        JSONObject JSuserInfoJson = new JSONObject();
        try
        {
            JSuserInfoJson.put("userid", HanvonApplication.hvnName);
            JSuserInfoJson.put("fileType", "2");
            JSuserInfoJson.put("fid", fid);
            JSuserInfoJson.put("filePath", "");
            JSuserInfoJson.put("offset", offset);
            JSuserInfoJson.put("length", String.valueOf(length));
        } catch(JSONException e) {
            e.printStackTrace();
        }
        Log.i("-----",JSuserInfoJson.toString());
        HttpUtilsFiles.HttpDownFiles(JSuserInfoJson, InfoMsg.UrlFileDown, path, fid);
    }

    public static void FilesSearch() {
        JSONObject JSuserInfoJson = new JSONObject();
        try
        {
            JSONObject conditionJson = new JSONObject();
            JSONObject queryJson = new JSONObject();
            //     conditionJson.put("beginTime", SyncInfo.HvnOldSynchroTime);
            //    conditionJson.put("endTime", SyncInfo.HvnSystemCurTime);
            JSuserInfoJson.put("userid", HanvonApplication.hvnName);
            JSuserInfoJson.put("start", "0");
            JSuserInfoJson.put("pageSize", "10");
            JSuserInfoJson.put("sort", conditionJson);
            JSuserInfoJson.put("fileType", "2");
            JSuserInfoJson.put("recogType", "1");
            JSuserInfoJson.put("queryMap", queryJson);
            JSuserInfoJson.put("fid", "05n7xzvtvntk39");
            JSuserInfoJson.put("beginDate", "");
            JSuserInfoJson.put("endDate", "");
            JSuserInfoJson.put("month", "");
            JSuserInfoJson.put("fileName", "");
        } catch(JSONException e) {
            e.printStackTrace();
        }
        Log.i("-----",JSuserInfoJson.toString());
        MyHttpUtils.HttpSend(InfoMsg.UrlSearch, JSuserInfoJson, InfoMsg.FILE_SEARCH_TYPE);
    }

    public static void OrderAdd(OrderDetail ordertail,String paytyep,String outType) {
        JSONObject JSuserInfoJson = new JSONObject();
        try
        {
            JSuserInfoJson.put("userid", HanvonApplication.hvnName);
            JSuserInfoJson.put("oid", ordertail.getOrderNumber());
            JSuserInfoJson.put("inFid", ordertail.getOrderFid());
            JSuserInfoJson.put("contactId", ordertail.getContactId());
            JSuserInfoJson.put("recogType", "2");
            JSuserInfoJson.put("wordsRange", ordertail.getOrderFilesBytes());
            JSuserInfoJson.put("accurateWords", ordertail.getAccurateWords());
            JSuserInfoJson.put("recogRate",ordertail.getRecogRate() );
            JSuserInfoJson.put("recogAngle", ordertail.getRecogAngle());
            JSuserInfoJson.put("zoom", ordertail.getZoom());
            JSuserInfoJson.put("payType","1" );
            JSuserInfoJson.put("payWay", paytyep);
            JSuserInfoJson.put("price", "0.01");
            String waitTime = null;
            try {
                waitTime = URLEncoder.encode(ordertail.getOrderWaitTime(), "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            JSuserInfoJson.put("waitTime", waitTime);
            if("txt".equals(outType)) {
                JSuserInfoJson.put("outputType", "0");
            }else if("pdf".equals(outType)){
                JSuserInfoJson.put("outputType", "1");
            }else if("doc".equals(outType)){
                JSuserInfoJson.put("outputType", "2");
            }
            JSuserInfoJson.put("level", "2");
            JSuserInfoJson.put("platformType", "4");
        } catch(JSONException e) {
            e.printStackTrace();
        }
        LogUtil.i("-----"+JSuserInfoJson.toString());
        MyHttpUtils.HttpSend(InfoMsg.UrlOrderAdd,JSuserInfoJson,InfoMsg.ORDER_ADD_TYPE);
    }

    public static void OrderEvaluate(String fuid){
        JSONObject JSuserInfoJson = new JSONObject();
        try
        {
            JSuserInfoJson.put("userid", HanvonApplication.hvnName);
            JSuserInfoJson.put("fid", fuid);
        } catch(JSONException e) {
            e.printStackTrace();
        }
        LogUtil.i("-----"+JSuserInfoJson.toString());
        MyHttpUtils.HttpSend(InfoMsg.UrlOrderEvl,JSuserInfoJson,InfoMsg.ORDER_EVL_TYPE);
    }

    public static void OrderPay(String oid) {
        JSONObject JSuserInfoJson = new JSONObject();
        try
        {
            JSuserInfoJson.put("userid", HanvonApplication.hvnName);
            JSuserInfoJson.put("oid", oid);
            JSuserInfoJson.put("payType", "1");
            JSuserInfoJson.put("payWay", "11");
            JSuserInfoJson.put("price", "10");
        } catch(JSONException e) {
            e.printStackTrace();
        }
        LogUtil.i("-----"+JSuserInfoJson.toString());
        MyHttpUtils.HttpSend(InfoMsg.UrlOrderPay,JSuserInfoJson,InfoMsg.ORDER_PAY_TYPE);
    }

    public static void OrderList(String status,int start) {
        JSONObject JSuserInfoJson = new JSONObject();
        try
        {
            JSONObject conditionJson = new JSONObject();
            //     conditionJson.put("beginTime", SyncInfo.HvnOldSynchroTime);
            //    conditionJson.put("endTime", SyncInfo.HvnSystemCurTime);
            JSuserInfoJson.put("userid", HanvonApplication.hvnName);
            JSuserInfoJson.put("start", String.valueOf(start*10));
            JSuserInfoJson.put("pageSize", "10");
            JSuserInfoJson.put("sort", conditionJson);
            JSuserInfoJson.put("status", status);
        } catch(JSONException e) {
            e.printStackTrace();
        }
        LogUtil.i("-----" + JSuserInfoJson.toString());
        MyHttpUtils.HttpSend(InfoMsg.UrlOrderList,JSuserInfoJson,InfoMsg.ORDER_LIST_TYPE);
    }

    public static void OrderShow(String oid){
        JSONObject JSuserInfoJson = new JSONObject();
        try
        {
            JSuserInfoJson.put("userid", HanvonApplication.hvnName);
            JSuserInfoJson.put("oid", oid);
        } catch(JSONException e) {
            e.printStackTrace();
        }
        LogUtil.i("-----"+JSuserInfoJson.toString());
        MyHttpUtils.HttpSend(InfoMsg.UrlOrderShow,JSuserInfoJson,InfoMsg.ORDER_SHOW_TYPE);
    }

    public static void OrderSearch(String oid) {
        JSONObject JSuserInfoJson = new JSONObject();
        try
        {
            JSONObject conditionJson = new JSONObject();
            JSONObject queryJson = new JSONObject();
            //     conditionJson.put("beginTime", SyncInfo.HvnOldSynchroTime);
            //    conditionJson.put("endTime", SyncInfo.HvnSystemCurTime);
            JSuserInfoJson.put("userid", HanvonApplication.hvnName);
            JSuserInfoJson.put("start", "0");
            JSuserInfoJson.put("pageSize", "10");
            JSuserInfoJson.put("sort", conditionJson);
            JSuserInfoJson.put("status", "");
            JSuserInfoJson.put("queryMap", queryJson);
            JSuserInfoJson.put("oid", oid);
            JSuserInfoJson.put("beginDate", "");
            JSuserInfoJson.put("endDate", "");
            JSuserInfoJson.put("month", "");
        } catch(JSONException e) {
            e.printStackTrace();
        }
        LogUtil.i("-----"+JSuserInfoJson.toString());
        MyHttpUtils.HttpSend(InfoMsg.UrlOrderSearch,JSuserInfoJson,InfoMsg.ORDER_SEARCH_TYPE);
    }

    public static void OrderCancel(String fuid){
        JSONObject JSuserInfoJson = new JSONObject();
        try
        {
            JSuserInfoJson.put("userid", HanvonApplication.hvnName);
            JSuserInfoJson.put("fid", fuid);
            JSuserInfoJson.put("status", "4");
        } catch(JSONException e) {
            e.printStackTrace();
        }
        LogUtil.i("-----"+JSuserInfoJson.toString());
        MyHttpUtils.HttpSend(InfoMsg.UrlOrderCancel,JSuserInfoJson,InfoMsg.ORDER_CANCEL_TYPE);
    }

    public static void OrderDelete(String oid){
        JSONObject JSuserInfoJson = new JSONObject();
        try
        {
            JSuserInfoJson.put("userid", HanvonApplication.hvnName);
            JSuserInfoJson.put("oid", oid);
        } catch(JSONException e) {
            e.printStackTrace();
        }
        LogUtil.i("-----"+JSuserInfoJson.toString());
        MyHttpUtils.HttpSend(InfoMsg.UrlOrderDelete,JSuserInfoJson,InfoMsg.ORDER_DELETE_TYPE);
    }

    public static void WxPay(String prices,String orderid){
        JSONObject JSuserInfoJson = new JSONObject();
        try
        {
            JSuserInfoJson.put("userid", HanvonApplication.hvnName);
            JSuserInfoJson.put("oid", orderid);
            JSuserInfoJson.put("productId", "2");
            JSuserInfoJson.put("body", URLEncoder.encode("汉王识文-精准人工识别", "UTF-8"));
            JSuserInfoJson.put("price", "0.01");
        } catch(Exception e) {
            e.printStackTrace();
        }
        LogUtil.i("-----"+JSuserInfoJson.toString());
        MyHttpUtils.HttpSend(InfoMsg.UrlOrderWxPay,JSuserInfoJson,InfoMsg.ORDER_WXPAY_TYPE);
    }

    public static void OrderWxQuery(String orderid){
        JSONObject JSuserInfoJson = new JSONObject();
        try
        {
            JSuserInfoJson.put("userid", HanvonApplication.hvnName);
            JSuserInfoJson.put("oid", orderid);
        } catch(Exception e) {
            e.printStackTrace();
        }
        LogUtil.i("-----"+JSuserInfoJson.toString());
        MyHttpUtils.HttpSend(InfoMsg.UrlOrderWxQuery,JSuserInfoJson,InfoMsg.ORDER_QUERY_ORDER_TYPE);
    }

    public static void OrderAliPayQuery(String orderid){
        JSONObject JSuserInfoJson = new JSONObject();
        try
        {
            JSuserInfoJson.put("userid", HanvonApplication.hvnName);
            JSuserInfoJson.put("oid", orderid);
        } catch(Exception e) {
            e.printStackTrace();
        }
        LogUtil.i("-----"+JSuserInfoJson.toString());
        MyHttpUtils.HttpSend(InfoMsg.UrlOrderAliPayQuery,JSuserInfoJson,InfoMsg.ORDER_ALIPAY_QUERY_ORDER_TYPE);
    }

    public static void GetAliPaySign(String oidinfo){
        JSONObject JSuserInfoJson = new JSONObject();
        try
        {
            JSuserInfoJson.put("userid", HanvonApplication.hvnName);
            JSuserInfoJson.put("orderInfo", URLEncoder.encode(oidinfo, "UTF-8"));
        } catch(Exception e) {
            e.printStackTrace();
        }
        LogUtil.i("-----"+JSuserInfoJson.toString());
        MyHttpUtils.HttpSend(InfoMsg.UrlOrderAliPaySign,JSuserInfoJson,InfoMsg.ORDER_ALIPAY_SIGN_ORDER_TYPE);
    }


    public static void ModifyContactsMsg(String name,String phone,String email){
        JSONObject JSuserInfoJson = new JSONObject();
        try
        {
            JSuserInfoJson.put("userid", HanvonApplication.hvnName);
            JSuserInfoJson.put("fullname", URLEncoder.encode(name, "UTF-8"));
            JSuserInfoJson.put("mobile",phone);
            JSuserInfoJson.put("email",phone);
        } catch(Exception e) {
            e.printStackTrace();
        }
        LogUtil.i("-----"+JSuserInfoJson.toString());
        MyHttpUtils.HttpSend(InfoMsg.UrlContactsModify, JSuserInfoJson, InfoMsg.ORDER_CONTACTS_MODIFY_TYPE);
    }
}
