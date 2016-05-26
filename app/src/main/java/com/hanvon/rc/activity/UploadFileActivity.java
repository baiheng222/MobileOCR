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
import com.hanvon.rc.md.camera.UploadImage;
import com.hanvon.rc.orders.OrderDetail;
import com.hanvon.rc.orders.OrderEvalPrices;
import com.hanvon.rc.utils.ConnectionDetector;
import com.hanvon.rc.utils.InfoMsg;
import com.hanvon.rc.utils.LogUtil;

import org.json.JSONObject;

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

    private String fid;

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
    }

    private void initData()
    {
        Intent intent = getIntent();
        fileAmount = intent.getIntExtra("fileamount", 0);
        fileFormat = intent.getStringExtra("fileformat");
        fileName = intent.getStringExtra("filename");
        fullPath = intent.getStringExtra("fullpath");
        resultFileType = intent.getStringExtra("resultfiletype");
        isZip = true;
        recType = String.valueOf("2");

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

            fid = null;
            fid = UploadImage.UploadFiletoHvn(mRecgType, mPath, mFileName, mFileAmount, mIsZip, mFileFormat);

            if (null == fid)
            {
                LogUtil.i("upload file failed !!!");
                Message msg = new Message();
                msg.what = InfoMsg.FILE_UPLOAD_FAIL;
                UploadFileActivity.this.textHandler.sendMessage(msg);
                return;
            }

            new UploadImage(textHandler).GetEvaluate(fid);
        }
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
                case InfoMsg.NETWORK_ERR:
                    //Toast.makeText(CropActivity.this, "网络连接失败，请检查网络后重试！", Toast.LENGTH_LONG).show();
                    break;

                case InfoMsg.FILE_UPLOAD_FAIL:
                    //Toast.makeText(CropActivity.this, "上传失败，请检查网络并重试", Toast.LENGTH_SHORT);
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


