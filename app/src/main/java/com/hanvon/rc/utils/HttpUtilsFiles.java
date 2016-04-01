package com.hanvon.rc.utils;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * @Desc:
 * @Auth: chenxzhuang
 * @Time: 2016/3/26 0026.
 */
public class HttpUtilsFiles {

    private final static int BUF_SIZE = 32768;

    public static void UploadFiletoHvn(int type,String path,String filename){
        try {
            final int blocknum;
            final byte[] buffer;
            int readBytes = BUF_SIZE;
            Map<String, String> parmas;
            final File file = new File(path);
            String checksum =  MD5Util.getFileMD5String(file);

            FileInputStream fis = new FileInputStream(file);
            int length = fis.available();

            if (length%BUF_SIZE != 0){
                blocknum = length/BUF_SIZE + 1;
            }else{
                blocknum = length/BUF_SIZE;
            }
            if (blocknum <= 1){
                buffer =  new byte[length];
            }else{
                buffer =  new byte[BUF_SIZE];
            }

            for(int i = 0;i < blocknum;i++){
                if (i == blocknum -1){
                    readBytes = length - i*BUF_SIZE;
                    byte[] buffer1 = new byte[readBytes];
                    readBytes = fis.read(buffer1);
                    parmas = GetMapFromType(Base64Utils.encode(buffer1), filename, i * BUF_SIZE, length, type, readBytes, checksum);
                    dopost(parmas, type, buffer1);
                }else{
                    readBytes = BUF_SIZE;
                    readBytes = fis.read(buffer);
                    parmas = GetMapFromType(Base64Utils.encode(buffer), filename, i * BUF_SIZE, length, type, readBytes, checksum);
                    dopost(parmas, type, buffer);
                }

            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static Map<String, String> GetMapFromType(String data,String filename,
                                                     int offset, int totalLength, int type,int readBytes,String checkSum){
        //封装数据
        Map<String, String> parmas = new HashMap<String, String>();
        if (type == InfoMsg.FILE_UPLOAD_TYPE) {
            parmas.put("userid", "test2345");
            parmas.put("fileType", "1");
            parmas.put("fid", "");
            parmas.put("fileName", URLEncoder.encode(filename));
            parmas.put("fileFormat", "jpg");
            parmas.put("fileAmount", "1");
            parmas.put("size", String.valueOf(totalLength));
            parmas.put("length", String.valueOf(readBytes));
            parmas.put("offset", String.valueOf(offset));
            parmas.put("checksum", SHA1Util.encodeBySHA(data));
            parmas.put("iszip", String.valueOf(false));
        //    parmas.put("devid", "123456789");
            Log.i("==234===", parmas.toString());
        }else if(type == InfoMsg.FILE_RECOGINE_TYPE){
            parmas.put("userid", "test2345");
            parmas.put("resType", "1");
            parmas.put("platformType", "4");
            parmas.put("fileName", URLEncoder.encode(filename));
            parmas.put("fileFormat", "jpg");
            parmas.put("length", String.valueOf(readBytes));
            parmas.put("size", String.valueOf(totalLength));
            parmas.put("offset", String.valueOf(offset));
            parmas.put("checksum", SHA1Util.encodeBySHA(data));
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
}
