package com.hanvon.rc.utils;

import android.util.Log;

import com.hanvon.rc.application.HanvonApplication;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @Desc:
 * @Auth: chenxzhuang
 * @Time: 2016/3/26 0026.
 */
public class RequestJson {

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
            JSuserInfoJson.put("recogType", "1");
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
        MyHttpUtils.HttpSend(InfoMsg.UrlFileDelete,JSuserInfoJson,InfoMsg.FILE_DELETE_TYPE);
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
        MyHttpUtils.HttpSend(InfoMsg.UrlFileCheckSum,JSuserInfoJson,InfoMsg.FILE_CHECKSUM_TYPE);
    }

    public static void FileDown(long offset,long length){
        JSONObject JSuserInfoJson = new JSONObject();
        try
        {
            JSuserInfoJson.put("userid", HanvonApplication.hvnName);
            JSuserInfoJson.put("fileType", "2");
            JSuserInfoJson.put("fid", "05n7xzvtvntk39");
            JSuserInfoJson.put("filePath", "");
            JSuserInfoJson.put("offset", offset);
            JSuserInfoJson.put("length", String.valueOf(length));
        } catch(JSONException e) {
            e.printStackTrace();
        }
        Log.i("-----",JSuserInfoJson.toString());
        HttpUtilsFiles.HttpDownFiles(JSuserInfoJson,InfoMsg.UrlFileDown);
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

    public static void OrderAdd() {
        JSONObject JSuserInfoJson = new JSONObject();
        try
        {
            JSuserInfoJson.put("userid", HanvonApplication.hvnName);
            JSuserInfoJson.put("inFid", "123456");
            JSuserInfoJson.put("recogType", "1");
            JSuserInfoJson.put("wordsRange", "5100-5200");
            JSuserInfoJson.put("price", "10");
            JSuserInfoJson.put("waitTime", "1800");
            JSuserInfoJson.put("outputType", "0");
            JSuserInfoJson.put("level", "3");
            JSuserInfoJson.put("platformType", "4");
        } catch(JSONException e) {
            e.printStackTrace();
        }
        Log.i("-----",JSuserInfoJson.toString());
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
        Log.i("-----",JSuserInfoJson.toString());
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
        Log.i("-----",JSuserInfoJson.toString());
        MyHttpUtils.HttpSend(InfoMsg.UrlOrderPay,JSuserInfoJson,InfoMsg.ORDER_PAY_TYPE);
    }

    public static void OrderList(String status) {
        JSONObject JSuserInfoJson = new JSONObject();
        try
        {
            JSONObject conditionJson = new JSONObject();
            //     conditionJson.put("beginTime", SyncInfo.HvnOldSynchroTime);
            //    conditionJson.put("endTime", SyncInfo.HvnSystemCurTime);
            JSuserInfoJson.put("userid", HanvonApplication.hvnName);
            JSuserInfoJson.put("start", "0");
            JSuserInfoJson.put("pageSize", "10");
            JSuserInfoJson.put("sort", conditionJson);
            JSuserInfoJson.put("status", status);
        } catch(JSONException e) {
            e.printStackTrace();
        }
        Log.i("-----",JSuserInfoJson.toString());
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
        Log.i("-----",JSuserInfoJson.toString());
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
        Log.i("-----",JSuserInfoJson.toString());
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
        Log.i("-----",JSuserInfoJson.toString());
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
        Log.i("-----",JSuserInfoJson.toString());
        MyHttpUtils.HttpSend(InfoMsg.UrlOrderDelete,JSuserInfoJson,InfoMsg.ORDER_DELETE_TYPE);
    }
}
