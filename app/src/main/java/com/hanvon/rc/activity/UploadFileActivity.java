package com.hanvon.rc.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hanvon.rc.R;
import com.hanvon.rc.application.HanvonApplication;
import com.hanvon.rc.md.camera.UploadImage;
import com.hanvon.rc.orders.OrderDetail;
import com.hanvon.rc.orders.OrderEvalPrices;
import com.hanvon.rc.presentation.CropActivity;
import com.hanvon.rc.utils.ConnectionDetector;
import com.hanvon.rc.utils.InfoMsg;
import com.hanvon.rc.utils.LogUtil;
import com.hanvon.rc.utils.MyHttpUtils;
import com.hanvon.rc.utils.StatisticsUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class UploadFileActivity extends Activity
{
    private ImageView ivBack;
    private ImageView ivUpload;
    private TextView tvUpload;
    private ProgressBar processBar;

    private String mFullPath;
    private String mRetFileType;

    private int fileAmount;
    private String fileFormat;
    private boolean isZip;
    private String fileName;
    private String fullPath;
    private String recType;
    private String resultFileType;
    private long fileSize;

    private String fid;

    public final static int MSG_TYPE_UPLOAD = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_activity);

        initData();

        ivBack = (ImageView) findViewById(R.id.iv_back);
        ivUpload = (ImageView) findViewById(R.id.iv_upload);
        tvUpload = (TextView) findViewById(R.id.tv_upload);
        processBar = (ProgressBar) findViewById(R.id.process_bar);

        if (connInNet())
        {
            UploadThread uploadThread = new UploadThread(fileName, fullPath, recType, String.valueOf(fileAmount), isZip, fileFormat);
            new Thread(uploadThread).start();
        }

        StatisticsUtils.IncreaseUploadPage();
    }

    private void initData()
    {
        Intent intent = getIntent();
        fileAmount = intent.getIntExtra("fileamount", 0);
        LogUtil.i("!!!! fileAmount is " + fileAmount);
        fileFormat = intent.getStringExtra("fileformat");
        fileName = intent.getStringExtra("filename");
        fullPath = intent.getStringExtra("fullpath");
        resultFileType = intent.getStringExtra("resultfiletype");
        isZip = true;
        recType = String.valueOf("2");

        File file = new File(fullPath);
        //fileSize = file.length();
        try
        {
            FileInputStream fis = new FileInputStream(file);
            fileSize = fis.available();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }


    }


    public boolean connInNet() //检查是否连网
    {
        ConnectionDetector connectionDetector = new ConnectionDetector(getApplication());
        if (connectionDetector.isConnectingTOInternet())
        {
            return true;
        }
        else
        {
            //Toast.makeText(getApplication(), "网络连接失败，请检查网络后重试！", Toast.LENGTH_LONG).show();
            return false;
        }
    }


    private void OrderEvaluateResult(String fuid)
    {
        JSONObject JSuserInfoJson = new JSONObject();
        try
        {
            JSuserInfoJson.put("userid", HanvonApplication.hvnName);
            JSuserInfoJson.put("fid", fuid);
        }
        catch(JSONException e)
        {
            e.printStackTrace();
        }
        LogUtil.i("-----"+JSuserInfoJson.toString());
        MyHttpUtils.HttpSend(InfoMsg.UrlOrderEvlResult, JSuserInfoJson,InfoMsg.ORDER_EVL_TYPE);
    }

    public class GetEvlResultThread implements Runnable
    {
        private String mFid;
        public GetEvlResultThread(String fid)
        {
            mFid = fid;
        }

        @Override
        public void run()
        {
            new MyHttpUtils(evaluateHandler);
            OrderEvaluateResult(mFid);
        }
    }

    private void OrderEvaluateProcess(String fuid)
    {
        JSONObject JSuserInfoJson = new JSONObject();
        try
        {
            JSuserInfoJson.put("userid", HanvonApplication.hvnName);
            JSuserInfoJson.put("fid", fuid);
        }
        catch(JSONException e)
        {
            e.printStackTrace();
        }
        LogUtil.i("-----"+JSuserInfoJson.toString());
        MyHttpUtils.HttpSend(InfoMsg.UrlOrderEvlProcess, JSuserInfoJson,InfoMsg.ORDER_EVL_RESULT_TYPE);
    }

    public class GetEvalProcessThread implements Runnable
    {
        private String mFid;
        public GetEvalProcessThread(String fid)
        {
            mFid = fid;
        }

        @Override
        public void run()
        {
            try
            {
                Thread.sleep(10000);
                LogUtil.i("sleep 10 secednds");
            }
            catch(InterruptedException e)
            {

            }

            new MyHttpUtils(evaluateHandler);
            OrderEvaluateProcess(mFid);
        }
    }


    private void startEvlProcessThread()
    {
        if (connInNet())
        {
            GetEvalProcessThread evlProcessThread = new GetEvalProcessThread(fid);
            new Thread(evlProcessThread).start();
        }
    }

    private void startEvlResultThread()
    {
        if (connInNet())
        {
            GetEvlResultThread evlResultThread = new GetEvlResultThread(fid);
            new Thread(evlResultThread).start();
        }
    }

    public class UploadThread implements Runnable
    {
        private String mFileName;
        private String mPath;
        private String mRecgType;
        private String mFileAmount;
        private boolean mIsZip;
        private String mFileFormat;

        public UploadThread(String filename, String path, String recgtype, String fileAmount, boolean iszip, String fileFormat)
        {
            mFileName = filename;
            mPath = path;
            mRecgType = recgtype;
            mFileAmount = fileAmount;
            mIsZip = iszip;
            mFileFormat = fileFormat;
        }

        @Override
        public void run()
        {
            LogUtil.i("!!!!!!!! UploadThread running !!!!!!!");

            if (!connInNet())
            {
                LogUtil.i("network err ,send msg !!!!!!");
                Message msg = new Message();
                msg.what = InfoMsg.NETWORK_ERR;
                UploadFileActivity.this.textHandler.sendMessage(msg);
                return;
            }


            new UploadImage(textHandler);

            fid = null;
            fid = UploadImage.UploadFiletoHvn(mRecgType, mPath, mFileName, mFileAmount, mIsZip, mFileFormat);
            LogUtil.i("get fid is " + fid);
            if (null == fid)
            {
                LogUtil.i("upload file failed !!!");
                Message msg = new Message();
                msg.what = InfoMsg.FILE_UPLOAD_FAIL;
                UploadFileActivity.this.textHandler.sendMessage(msg);
                return;
            }
            else if (fid.equals("8002"))
            {
                LogUtil.i("receive result is 8002, no result");
                Message msg = new Message();
                msg.what = InfoMsg.ERR_COOD_8002;
                UploadFileActivity.this.textHandler.sendMessage(msg);

                return;
            }
            else
            {
                Message msg = Message.obtain();
                msg.what = InfoMsg.MSG_FILEUPLOAD_DONE;
                UploadFileActivity.this.textHandler.sendMessage(msg);
            }
            new UploadImage(evaluateHandler).GetEvaluate(fid);
            //UploadImage.GetEvaluate(fid);
        }
    }


    private void updateInfoMsg()
    {
        tvUpload.setText(R.string.str_upload_done);
    }

    private void updateProcessBar(long curbytes)
    {
        LogUtil.i("get current readbytes " + curbytes);
        LogUtil.i("filesize is " + fileSize);
        int percent = (int)(200 * curbytes / fileSize);
        LogUtil.i("current is " + percent);
        processBar.setProgress(percent);
    }

    private void doEvlProcess(String percent)
    {
        if (percent.equals("520"))
        {
            tvUpload.setText("评估接口出现错误， 520");
        }
        else if (percent.equals("100%"))
        {
            tvUpload.setText("已评估" + percent);
            startEvlResultThread();
        }
        else
        {
            tvUpload.setText("已评估" + percent + "，请稍候");
            startEvlProcessThread();
        }
    }

    public Handler evaluateHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            LogUtil.i("!!!!!!! processBarHandler handle msg");
            switch (msg.what)
            {
                case InfoMsg.ORDER_EVL_RESULT_TYPE:
                    Object evlobj1 = msg.obj;
                    String evlprocess = evlobj1.toString();
                    try
                    {
                        JSONObject json = new JSONObject(evlprocess);
                        if ("0".equals(json.getString("code")))
                        {
                            String percent = json.getString("rateOfProgress");
                            doEvlProcess(percent);
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                break;

                case UploadFileActivity.MSG_TYPE_UPLOAD:
                {
                    LogUtil.i("MSG_TYPE_UPLOAD");
                    long arg = Long.parseLong(msg.obj.toString());
                    updateProcessBar(arg);
                }
                break;

                case InfoMsg.NET_ERR_SOCKET_TIMEOUT:
                    //Toast.makeText(UploadFileActivity.this, "网络连接超时，请重试", Toast.LENGTH_LONG).show();
                    LogUtil.i("socket timeout error, from evaluatehandler !!!");
                    startEvlProcessThread();
                    break;

                case InfoMsg.ERR_COOD_8002:
                    Toast.makeText(UploadFileActivity.this, "图片样本不清晰，请重新拍摄图片", Toast.LENGTH_LONG).show();
                    LogUtil.i("msg 8002 !!!!!!!!!!");
                    //CropActivity.this.finish();
                    updateErrMsg("图片样本不清晰，请重新拍摄图片");
                    break;

                case InfoMsg.NETWORK_ERR:
                    Toast.makeText(UploadFileActivity.this, "网络连接失败，请检查网络后重试！", Toast.LENGTH_LONG).show();
                    break;

                case InfoMsg.ORDER_EVL_TYPE:
                    LogUtil.i("MSG InfoMsg.ORDER_EVL_TYPE");
                    Object evlobj = msg.obj;
                    String evlcontent = evlobj.toString();
                    try
                    {
                        JSONObject json = new JSONObject(evlcontent);
                        if("0".equals(json.getString("code")))
                        {
                            OrderDetail orderDetail = new OrderDetail();
                            orderDetail.setOrderFileNanme(fileName);
                            orderDetail.setOrderFilesPages(json.getString("fileAmount"));
                            orderDetail.setOrderFilesBytes(json.getString("wordsRange"));
                            orderDetail.setOrderFinshTime(json.getString("finishTime"));
                            orderDetail.setOrderPrice(json.getString("price"));
                            orderDetail.setOrderWaitTime(json.getString("waitTime"));
                            orderDetail.setAccurateWords(json.getString("accurateWords"));
                            orderDetail.setRecogRate(json.getString("recogRate"));
                            orderDetail.setRecogAngle(json.getString("recogAngle"));
                            orderDetail.setOrderNumber(json.getString("oid"));
                            orderDetail.setZoom(json.getString("zoom"));
                            orderDetail.setOrderLevel(json.getString("level"));
                            orderDetail.setOrderFid(fid);
                            orderDetail.setOrderStatus("1");
                            String contactId = json.getString("contactId");
                            if(null == contactId || "null".equals(contactId) || "".equals(contactId)){
                                orderDetail.setContactId("");
                            }else{
                                orderDetail.setContactId(contactId);
                            }
                            String mobile = json.getString("mobile");
                            if(null == mobile || "null".equals(mobile) || "".equals(mobile)){
                                orderDetail.setOrderPhone("");
                            }else{
                                orderDetail.setOrderPhone(mobile);
                            }
                            String fullname = json.getString("fullname");
                            if(null == fullname || "null".equals(fullname) || "".equals(fullname)){
                                orderDetail.setOrderName("");
                            }else{
                                orderDetail.setOrderName(fullname);
                            }
                            Intent intent = new Intent();
                            intent.setClass(UploadFileActivity.this, OrderEvalPrices.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("ordetail", orderDetail);
                            intent.putExtras(bundle);
                            intent.putExtra("resultfiletype", resultFileType);
                            LogUtil.i("*************resultFileType:"+resultFileType);
                            startActivity(intent);
                            finish();
                        }
                        else if ("8002".equals(json.getString("code")))
                        {
                            Toast.makeText(UploadFileActivity.this,json.getString("result"),Toast.LENGTH_SHORT).show();
                            tvUpload.setText("图片样本不够清晰");
                        }
                        else
                        {
                            Toast.makeText(UploadFileActivity.this,"评估过程出现错误!",Toast.LENGTH_SHORT).show();
                            updateErrMsg(json.getString("code"));
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };


    private void updateErrMsg(String msg)
    {
        tvUpload.setText(msg);
    }

    public Handler textHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            LogUtil.i("!!!!!!! textHandler handle msg");
            //mProgress.dismiss();
            //isRecognizing = false;
            switch (msg.what)
            {

                case UploadFileActivity.MSG_TYPE_UPLOAD:
                {
                    LogUtil.i("MSG_TYPE_UPLOAD");
                    long arg = Long.parseLong(msg.obj.toString());
                    updateProcessBar(arg);
                }
                break;

                case InfoMsg.MSG_FILEUPLOAD_DONE:
                {
                    updateInfoMsg();
                }

                case InfoMsg.ERR_COOD_8002:
                    //Toast.makeText(UploadFileActivity.this, "图片样本不清晰，请重新拍摄图片", Toast.LENGTH_LONG).show();
                    LogUtil.i("handle msg 8002, !!!!!!!!!!!!!!!!!!!!!!!!!, who send this msg!!!!");
                    //CropActivity.this.finish();
                    //updateErrMsg("图片样本不清晰，请重新拍摄图片");
                    break;

                case InfoMsg.NET_ERR_SOCKET_TIMEOUT:
                    Toast.makeText(UploadFileActivity.this, "网络连接超时，请重试", Toast.LENGTH_LONG).show();
                    updateErrMsg("网络连接超时，请重试");
                    break;

                case InfoMsg.NETWORK_ERR:
                    Toast.makeText(UploadFileActivity.this, "网络连接失败，请检查网络后重试！", Toast.LENGTH_LONG).show();
                    updateErrMsg("网络连接失败，请检查网络后重试！");
                    break;

                case InfoMsg.FILE_UPLOAD_FAIL:
                    Toast.makeText(UploadFileActivity.this, "上传失败，请检查网络并重试", Toast.LENGTH_SHORT).show();
                    updateErrMsg("上传失败，请检查网络并重试");
                    break;
                case InfoMsg.FILE_RECO_FAIL:
                    Object msgobj = msg.obj;
                    String msgcontent = msgobj.toString();
                    String errcode = null;
                    JSONObject jsonObject = null;
                    try
                    {
                        jsonObject = new JSONObject(msgcontent);
                        errcode = jsonObject.getString("code");
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                    //startRecFailActivity(errcode);
                    break;

                case InfoMsg.ORDER_EVL_TYPE:
                    LogUtil.i("MSG InfoMsg.ORDER_EVL_TYPE");
                    Object evlobj = msg.obj;
                    String evlcontent = evlobj.toString();
                    try
                    {
                        JSONObject json = new JSONObject(evlcontent);
                        if("0".equals(json.getString("code")))
                        {
                            OrderDetail orderDetail = new OrderDetail();
                            orderDetail.setOrderFileNanme(fileName);
                            orderDetail.setOrderFilesPages(json.getString("fileAmount"));
                            orderDetail.setOrderFilesBytes(json.getString("wordsRange"));
                            orderDetail.setOrderFinshTime(json.getString("finishTime"));
                            orderDetail.setOrderPrice(json.getString("price"));
                            orderDetail.setOrderWaitTime(json.getString("waitTime"));
                            orderDetail.setAccurateWords(json.getString("accurateWords"));
                            orderDetail.setRecogRate(json.getString("recogRate"));
                            orderDetail.setRecogAngle(json.getString("recogAngle"));
                            orderDetail.setOrderNumber(json.getString("oid"));
                            orderDetail.setZoom(json.getString("zoom"));
                            orderDetail.setOrderLevel(json.getString("level"));
                            orderDetail.setOrderFid(fid);
                            orderDetail.setOrderStatus("1");
                             String contactId = json.getString("contactId");
                            if(null == contactId || "null".equals(contactId) || "".equals(contactId)){
                                orderDetail.setContactId("");
                            }else{
                                orderDetail.setContactId(contactId);
                            }
                            String mobile = json.getString("mobile");
                            if(null == mobile || "null".equals(mobile) || "".equals(mobile)){
                                orderDetail.setOrderPhone("");
                            }else{
                                orderDetail.setOrderPhone(mobile);
                            }
                            String fullname = json.getString("fullname");
                            if(null == fullname || "null".equals(fullname) || "".equals(fullname)){
                                orderDetail.setOrderName("");
                            }else{
                                orderDetail.setOrderName(fullname);
                            }
                            Intent intent = new Intent();
                            intent.setClass(UploadFileActivity.this, OrderEvalPrices.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("ordetail", orderDetail);
                            intent.putExtras(bundle);
                            intent.putExtra("resultfiletype", resultFileType);
                            LogUtil.i("*************resultFileType:"+resultFileType);
                            startActivity(intent);
                            finish();
                        }
                        else if ("8002".equals(json.getString("code")))
                        {
                            Toast.makeText(UploadFileActivity.this,json.getString("result"),Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(UploadFileActivity.this,"评估过程出现错误!",Toast.LENGTH_SHORT).show();
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    break;

            }
        }
    };
}


