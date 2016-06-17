package com.hanvon.rc.md.camera;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.hanvon.rc.activity.FileListActivity;
import com.hanvon.rc.activity.UploadFileActivity;
import com.hanvon.rc.application.HanvonApplication;
import com.hanvon.rc.utils.Base64Utils;
import com.hanvon.rc.utils.InfoMsg;
import com.hanvon.rc.utils.LogUtil;
import com.hanvon.rc.utils.SHA1Util;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by baiheng222 on 16-4-7.
 */
public class UploadImage
{
    private static final String TAG = "UploadImage";

    private final static int BUF_SIZE = 327680;
    private static int offset = 0;  //上传上传的文件长度
    private static boolean isPause;
    private static boolean isDownPause;
    private static long downOffset  = 0;
    private final static String FORMAT_OTHER_YEAR = "yyyyMMdd";

    private static Handler handler = null;

    public UploadImage(Handler h)
    {
        handler = h;
    }

    public static String UploadFiletoHvn(String recgType,String path,String filename, String fileAmount, boolean iszip, String fileFormat)
    {
        String result = null;
        try
        {
            byte[] buffer = null;
            int readBytes = BUF_SIZE;
            Map<String, String> parmas;
            final File file = new File(path);

            FileInputStream fis = new FileInputStream(file);
            int length = fis.available();


            LogUtil.i("!!!!! filename is " + filename);
            LogUtil.i("offset:" + offset);

            if(offset != 0)
            {
                //fis.skip(offset);
                offset = 0;
            }

            boolean flag = true;
            while(flag)
            {
                if ((isPause) || ((!isPause)&&(offset >= length)))
                {
                    break;
                }

                if((length - offset) / BUF_SIZE > 0)
                {
                    readBytes = BUF_SIZE;
                }
                else
                {
                    readBytes = length - offset;
                }

                try
                {
                    buffer =  new byte[readBytes];
                    readBytes = fis.read(buffer);
                }
                catch (IOException e1)
                {
                    e1.printStackTrace();
                }

                LogUtil.i("readBytes:"+readBytes);
                LogUtil.i("offset is " + offset);
                LogUtil.i("file length is " + length);


                if (readBytes < 0)
                {
                    LogUtil.i("!!!!! readBytes is " + readBytes + " , break while!!!");
                    break;
                }




                //parmas = GetMapFromType(buffer, filename, offset, length, type, readBytes);
                //parmas = GetMapFromType(Base64Utils.encode(buffer), filename, offset, length, recgType, readBytes);
                parmas = GetMapFromType(Base64Utils.encode(buffer), filename, offset, length, recgType, readBytes, fileAmount, iszip, fileFormat);
                result = dopost(parmas, buffer);

                if (null != handler)
                {
                    Message msg = Message.obtain();
                    msg.what = UploadFileActivity.MSG_TYPE_UPLOAD;
                    msg.obj = String.valueOf(offset);
                    handler.sendMessage(msg);
                }
            }
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (result == null)
        {
            LogUtil.i("!!! result is null, error return");
            return result;
        }

        String fid = null;
        fid = processUploadRet(result);

        if (null != fid)
        {
            LogUtil.i("!!!! final fid is " + fid);
        }

        return fid;
        /*
        if (fid != null)
        {
            GetRapidRecogRet("test2345", fid, "1", "4");
        }
         */

    }

    public static void GetRapidRecogRet(String uid, String fid, String restype, String platformtype)
    {
        JSONObject JSuserInfoJson = new JSONObject();
        try
        {
            JSuserInfoJson.put("userid", uid);
            JSuserInfoJson.put("fid", fid);
            JSuserInfoJson.put("resType", restype);
            JSuserInfoJson.put("platformtype", platformtype);

        }
        catch(JSONException e)
        {
            e.printStackTrace();
        }
        Log.i(TAG,JSuserInfoJson.toString());
        HttpSend(InfoMsg.UrlRapidRecog, JSuserInfoJson, InfoMsg.FILE_RECOGINE_TYPE);
    }

    public static void GetEvaluate(String fid)
    {
        LogUtil.i("GetEvaluate begin!!!!!");
        JSONObject JSuserInfoJson = new JSONObject();
        try
        {
            JSuserInfoJson.put("userid",HanvonApplication.hvnName);
            JSuserInfoJson.put("fid", fid);
        }
        catch(JSONException e)
        {
            e.printStackTrace();
        }
        LogUtil.i(JSuserInfoJson.toString());
        HttpSend(InfoMsg.UrlOrderEvl, JSuserInfoJson, InfoMsg.ORDER_EVL_TYPE);
    }


    public static void HttpSend(String Url, JSONObject json, final int type)
    {
        String ret = null;
        HttpUtils http = new HttpUtils();
        RequestParams params = new RequestParams();
        try
        {
            params.setBodyEntity(new StringEntity(json.toString()));
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }

        http.send(HttpRequest.HttpMethod.POST,Url,params, new RequestCallBack<String>()
        {
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
                LogUtil.i( "===onFailure====="+s);
                Message msg = new Message();
                msg.what = InfoMsg.FILE_RECO_FAIL;
                msg.obj = s;
                handler.sendMessage(msg);
            }
        });
    }

    public static String processUploadRet(String content)
    {
        String fid = null;
        JSONObject obj = null;
        try
        {
            if (content != null)
            {
                obj = new JSONObject(content);
                if ("0".equals(obj.getString("code")))
                {
                    LogUtil.i("!!!!!!! get success result");

                    fid = obj.getString("fid");
                    String offset = obj.getString("offset");
                    LogUtil.i("fid is " + fid + ", offset is " + offset);

					/* //fjm add
					String result = obj.getString("textResult");
					System.out.println("textResult:" + result);
					System.out.println("content+---------"+content);
					Intent backIntent = new Intent(CropActivity.this,OcrRecognizeResultActivity.class);
					Bundle bundle = new Bundle();
					bundle.putString("textResult", result);
					backIntent.putExtras(bundle);
					if(flag){
						return;
					}
	                CropActivity.this.startActivity(backIntent);
		            CropActivity.this.finish();
		            */
                }
                else if (obj.getString("code").equals("520"))
                {
                    LogUtil.i("!!!!!! server error 520 !!!!!!");
                }
                else if (obj.getString("code").equals("524"))
                {
                    LogUtil.i("!!!!!! checksum error 524 !!!!!!");
                }
                else
                {
                    String result = obj.getString("result");
                    //Toast.makeText(getApplicationContext(), "请重试！", Toast.LENGTH_SHORT).show();
                    LogUtil.i("!!!!!! result is " + result);
                }
            }
            else
            {
                //Toast.makeText(getApplicationContext(), "请重试！", Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return fid;
    }

    public static String getCurDate()
    {
        SimpleDateFormat formatter = new SimpleDateFormat(FORMAT_OTHER_YEAR);
        Date currentTime = new Date();
        String dateString = formatter.format(currentTime);
        Log.d(TAG, " !!!!! date string is " + dateString);
        return dateString;
    }



    public static Map<String, String> GetMapFromType(String data,String filename,
                 int offset, int totalLength, String type,int readBytes, String fileAmount, boolean iszip, String fileFormat)

    /*
    public static Map<String, String> GetMapFromType(byte[] data,String filename,
                                                     int offset, int totalLength, int type,int readBytes)*/
    {
        //封装数据
        Map<String, String> parmas = new HashMap<String, String>();
        if ("2".equals(type))
        {
            parmas.put("userid", HanvonApplication.hvnName);
        }
        else
        {
            if ("".equals(HanvonApplication.hvnName))
            {
                parmas.put("userid", HanvonApplication.AppDeviceId);
            }
            else
            {
                parmas.put("userid", HanvonApplication.hvnName);
            }
        }

        parmas.put("recogType", type);
        parmas.put("fileType", "1");
        parmas.put("fid", "");
        parmas.put("fileName", URLEncoder.encode(filename));
        parmas.put("fileFormat", fileFormat);
        parmas.put("fileAmount", fileAmount);
        parmas.put("size", String.valueOf(totalLength));
        parmas.put("length", String.valueOf(readBytes));
        parmas.put("offset", String.valueOf(offset));
        parmas.put("checksum", SHA1Util.sha(data));
        parmas.put("iszip", String.valueOf(iszip));

        Log.i(TAG, parmas.toString());

        return parmas;
    }

    private static String  dopost(Map<String, String> parmas,byte[] data)
    {
        Log.i(TAG, "!!!!dpost!!!!");
        String result = null;
        MultipartEntityBuilder mEntityBuilder = MultipartEntityBuilder.create();
        mEntityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        mEntityBuilder.setCharset(Charset.defaultCharset());

        ByteArrayBody dataBody = new ByteArrayBody(data,
                ContentType.MULTIPART_FORM_DATA, "uploadTemp");
        mEntityBuilder.addPart("file", dataBody);
        mEntityBuilder.setBoundary("----------ThIs_Is_tHe_bouNdaRY_$");

        DefaultHttpClient client = new DefaultHttpClient();//http客户端
        HttpPost httpPost = null;

        httpPost = new HttpPost(InfoMsg.UrlFileUpload);
        if(parmas != null)
        {
            for (Map.Entry<String, String> e : parmas.entrySet())
            {
                mEntityBuilder.addTextBody(e.getKey(), e.getValue());
            }
        }

        try
        {
            httpPost.setEntity(mEntityBuilder.build());
            HttpResponse response = client.execute(httpPost);
            HttpEntity entity = response.getEntity();
            InputStream content = entity.getContent();
            String returnConnection = convertStreamToString(content);
            Log.i(TAG,"!!!!! return string is " + returnConnection);
            try
            {
                JSONObject json = new JSONObject(returnConnection);
                if (json.getString("code").equals("0"))
                {
                    offset = Integer.valueOf(json.getString("offset"));
                    result = returnConnection;
                    Log.i(TAG,offset+"");
                }
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }

        }
        catch (IllegalStateException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return result;
    }

    private static String convertStreamToString(InputStream is)
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }


    public  static void HttpDownFiles(JSONObject params,String urlStr){
        HttpClient httpClient = new DefaultHttpClient();
        HttpResponse response;
        try { // 模拟调用rest API下载文件接口
            StringEntity entity = new StringEntity(params.toString());
            // 以post方式请求URL
            HttpPost httpPost = new HttpPost(urlStr);
            // 参数为json串形式
            //   httpPost.addHeader("Content-Type", MediaType.APPLICATION_JSON);
            httpPost.setEntity(entity);
            // 执行后获得响应数据
            response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            if(statusCode != 200){
                httpPost.abort();
                throw new RuntimeException("HttpClient, response :" + response);
            }
            String contentDisposition = response.getHeaders("Content-Disposition")[0].toString();
            String size = contentDisposition.substring(
                    contentDisposition.lastIndexOf("#") + 1, contentDisposition.length());

            // 获得附件文件名称
            String attachmentFileName = contentDisposition.substring(
                    contentDisposition.lastIndexOf("=")+2,  contentDisposition.lastIndexOf("#")-1);
            // 解决中文文件名乱码问题
            attachmentFileName = URLDecoder.decode(attachmentFileName, "UTF-8");
            String downloadPath = "/sdcard/"+attachmentFileName;
            Log.i("---------",downloadPath+"   size:"+size);
            if (downOffset == 0){
                File file = new File("/sdcard/"+attachmentFileName);
                if(file.exists()){
                    file.delete();
                }
            }
            InputStream is = response.getEntity().getContent();
            RandomAccessFile randomFile = new RandomAccessFile("/sdcard/"+attachmentFileName, "rw");
            long fileLength = randomFile.length();
            //将写文件指针移到文件尾。
            randomFile.seek(fileLength);

            int read = 0;
            byte[] buffer = new byte[32768];
            int offset = 0;
            while( (read = is.read(buffer)) > 0) {
                randomFile.write(buffer,0,read);
                downOffset = downOffset + read;
            }
            Log.i("*********offset:", downOffset + "");
            randomFile.close();
            is.close();
            //下次取的起始位置
            if (downOffset < Long.valueOf(size)){
                long length = 0;
                if(Long.valueOf(size) - downOffset >= 32768){
                    length = 32768;
                }else{
                    length = Long.valueOf(size) - downOffset;
                }
             //   FileDown(downOffset,length);
            }
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
