package com.hanvon.rc.utils;

/**
 * @Desc:
 * @Auth: chenxzhuang
 * @Time: 2016/3/25 0025.
 */
public class InfoMsg {

    public static final String HanvonUrl = "http://dpi.hanvon.com/rt/ws/v1";
    //public static final String HanvonUrl = "http://rc.hwyun.com:9090/rws-cloud/rt/ws/v1";

    public static final String ShareUrl = "http://dpi.hanvon.com/rt/ap/v1";
    //public static final String ShareUrl = "http://cloud.hwyun.com/dws-cloud/rt/ap/v1";


    public static final String UrlOriginQucikRecog = HanvonUrl + "/file/cutimg/quick/recog";
    public static final String UrlRapidRecog = HanvonUrl + "/file/web/rapid/recog";
    public static final String UrlFileRecog = HanvonUrl+"/file/quick/recog";
    public static final String UrlFileList = HanvonUrl+"/file/list";
    public static final String UrlFileUpload = HanvonUrl+"/file/upload";
    public static final String UrlFileDown = HanvonUrl+"/file/download";
    public static final String UrlFileShow = HanvonUrl+"/file/show";
    public static final String UrlFileRename = HanvonUrl+"/file/rename";
    public static final String UrlFileCheckSum = HanvonUrl+"/file/checksum";
    public static final String UrlFileDelete = HanvonUrl+"/file/delete";
    public static final String UrlSearch = HanvonUrl+"/file/search";

    public static final String UrlOrderAdd = HanvonUrl+"/order/add";
    public static final String UrlOrderEvl = HanvonUrl+"/order/evaluate";
    public static final String UrlOrderPay = HanvonUrl+"/order/pay";
    public static final String UrlOrderList = HanvonUrl+"/order/list";
    public static final String UrlOrderShow = HanvonUrl+"/order/show";
    public static final String UrlOrderSearch = HanvonUrl+"/order/search";
    public static final String UrlOrderCancel = HanvonUrl+"/order/cancel";
    public static final String UrlOrderDelete = HanvonUrl+"/order/delete";

    public static final String UrlOrderWxPay = HanvonUrl+"/wxpay/unifiedorder";
    public static final String UrlOrderWxQuery = HanvonUrl+"/wxpay/query";

    public static final String UrlOrderAliPaySign = HanvonUrl+"/alipay/sign";
    public static final String UrlOrderAliPayQuery = HanvonUrl+"/alipay/query";

    public static final String UrlContactsModify = HanvonUrl+"/user/edit/order/contacts/info";

    public static final String UrlOrderEvlProcess = HanvonUrl+"/order/evaluate/progress";
    public static final String UrlOrderEvlResult = HanvonUrl+"/order/evaluate/result";

    public static final int FILE_RECOGINE_TYPE = 0x01;
    public static final int FILE_LIST_TYPE = 0x02;
    public static final int FILE_UPLOAD_TYPE = 0x03;
    public static final int FILE_DOWN_TYPE = 0x04;
    public static final int FILE_SHOW_TYPE = 0x05;
    public static final int FILE_RENAME_TYPE = 0x06;
    public static final int FILE_CHECKSUM_TYPE = 0x07;
    public static final int FILE_DELETE_TYPE = 0x08;
    public static final int FILE_SEARCH_TYPE = 0x09;


    public static final int ORDER_ADD_TYPE = 0x10;
    public static final int ORDER_EVL_TYPE = 0x11;
    public static final int ORDER_PAY_TYPE = 0x12;
    public static final int ORDER_SHOW_TYPE = 0x13;
    public static final int ORDER_LIST_TYPE = 0x14;
    public static final int ORDER_SEARCH_TYPE = 0x15;
    public static final int ORDER_CANCEL_TYPE = 0x16;
    public static final int ORDER_DELETE_TYPE = 0x17;
    public static final int ORDER_EVL_RESULT_TYPE = 0x18;


    public static final int ORDER_WXPAY_TYPE = 0x18;
    public static final int ORDER_SEARCH_ORDER_TYPE = 0x19;
    public static final int ORDER_QUERY_ORDER_TYPE = 0x24;
    public static final int ORDER_ALIPAY_SIGN_ORDER_TYPE = 0x25;
    public static final int ORDER_ALIPAY_QUERY_ORDER_TYPE = 0x26;
    public static final int ORDER_CONTACTS_MODIFY_TYPE = 0x28;

    //add by fjm
    public static final int FILE_UPLOAD_FAIL = 0x21;
    public static final int FILE_RECO_FAIL = 0x22;
    public static final int NETWORK_ERR     = 0x23;
    public static final String RECO_ERR_UNKNOWN = "unknow";
    public static final String RECO_ERR_SERVER = "520";
    public static final String RECO_ERR_CHECKSUM = "524";

    public static final int RESULT_TYPE_QUICK_RECO = 0x24;
    public static final int RESULT_TYPE_EXACT_RECO = 0x25;
    public static final int RECO_MODE_QUICK_RECO = 0x26;
    public static final int RECO_MODE_EXACT_RECO = 0x27;

    public static final int NET_ERR_SOCKET_TIMEOUT = 0x47;
    public static final int ERR_COOD_8002 = 0x48;

    public static final int MSG_DOWNLOAD_DONE = 0x60;
    public static final int MSG_FILEUPLOAD_DONE = 0x61;
}
