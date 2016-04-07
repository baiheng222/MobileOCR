package com.hanvon.rc.utils;

import android.util.Log;

import com.hanvon.rc.application.HanvonApplication;

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
import java.util.HashMap;
import java.util.Map;

import static com.hanvon.rc.utils.RequestJson.FileDown;

/**
 * @Desc:
 * @Auth: chenxzhuang
 * @Time: 2016/3/26 0026.
 */
public class HttpUtilsFiles {

    private final static int BUF_SIZE = 32768;
    private static int offset = 0;  //上传上传的文件长度
    private static boolean isPause;
    private static boolean isDownPause;
    private static long downOffset  = 0;

    public static void UploadFiletoHvn(int type,String path,String filename){
        try {
            byte[] buffer = null;
            int readBytes = BUF_SIZE;
            Map<String, String> parmas;
            final File file = new File(path);

            FileInputStream fis = new FileInputStream(file);
            int length = fis.available();

            Log.i("====Start===","offset:"+offset);
            if(offset != 0){
                fis.skip(offset);
            }
            while(true){
                if ((isPause) || ((!isPause)&&(offset >= length))){
                    break;
                }
                if((length-offset)/BUF_SIZE > 0) {
                    readBytes = BUF_SIZE;
                }else {
                    readBytes = length - offset;
                }
                try {
                    buffer =  new byte[readBytes];
                    readBytes = fis.read(buffer);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                Log.i("=======","readBytes:"+readBytes);
                parmas = GetMapFromType(Base64Utils.encode(buffer), filename, offset, length, type, readBytes);
                dopost(parmas, type, buffer);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static Map<String, String> GetMapFromType(String data,String filename,
                                                     int offset, int totalLength, int type,int readBytes){
        //封装数据
        Map<String, String> parmas = new HashMap<String, String>();
        if (type == InfoMsg.FILE_UPLOAD_TYPE) {
            parmas.put("userid", HanvonApplication.hvnName);
            parmas.put("fileType", "1");
            parmas.put("fid", "");
            parmas.put("fileName", URLEncoder.encode(filename));
            parmas.put("fileFormat", "png");
            parmas.put("fileAmount", "1");
            parmas.put("size", String.valueOf(totalLength));
            parmas.put("length", String.valueOf(readBytes));
            parmas.put("offset", String.valueOf(offset));
            parmas.put("checksum", SHA1Util.sha(data));
            parmas.put("iszip", String.valueOf(false));
        //    parmas.put("devid", "123456789");
            Log.i("==234===", parmas.toString());
        }else if(type == InfoMsg.FILE_RECOGINE_TYPE){
         //   parmas.put("userid", HanvonApplication.hvnName);
            parmas.put("resType", "1");
            parmas.put("platformType", "4");
            parmas.put("fileName", URLEncoder.encode(filename));
            parmas.put("fileFormat", "jpg");
            parmas.put("length", String.valueOf(readBytes));
            parmas.put("size", String.valueOf(totalLength));
            parmas.put("offset", String.valueOf(offset));
            parmas.put("checksum", SHA1Util.sha(data));
            Log.i("-----", parmas.toString());
        }

        return parmas;
    }
    private static void dopost(Map<String, String> parmas,int type,byte[] data){
        MultipartEntityBuilder mEntityBuilder = MultipartEntityBuilder.create();
        mEntityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        mEntityBuilder.setCharset(Charset.defaultCharset());

        ByteArrayBody dataBody = new ByteArrayBody(data,
                ContentType.MULTIPART_FORM_DATA, "uploadTemp");
        mEntityBuilder.addPart("file", dataBody);
        mEntityBuilder.setBoundary("----------ThIs_Is_tHe_bouNdaRY_$");

        DefaultHttpClient client = new DefaultHttpClient();//http客户端
        HttpPost httpPost = null;
        if (type == InfoMsg.FILE_UPLOAD_TYPE) {
            httpPost = new HttpPost(InfoMsg.UrlFileUpload);
        }else if (type == InfoMsg.FILE_RECOGINE_TYPE){
            httpPost = new HttpPost(InfoMsg.UrlFileRecog);
        }

        if(parmas != null){
            for (Map.Entry<String, String> e : parmas.entrySet()) {
                mEntityBuilder.addTextBody(e.getKey(), e.getValue());
            }
        }

        try {
            httpPost.setEntity(mEntityBuilder.build());
            HttpResponse response = client.execute(httpPost);
            HttpEntity entity = response.getEntity();
            InputStream content = entity.getContent();
            String returnConnection = convertStreamToString(content);
            Log.i("=======",returnConnection);
            try {
                JSONObject json = new JSONObject(returnConnection);
                if (json.getString("code").equals("0")){
                    offset = Integer.valueOf(json.getString("offset"));
                    Log.i("=======",offset+"");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String convertStreamToString(InputStream is) {
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
                    FileDown(downOffset,length);
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
