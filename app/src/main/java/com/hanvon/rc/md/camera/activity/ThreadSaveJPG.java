package com.hanvon.rc.md.camera.activity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.hanvon.rc.md.camera.BitmapProcess;
import com.hanvon.rc.md.camera.CurrentTime;
import com.hanvon.rc.utils.FileUtil;
import com.hanvon.rc.utils.LogUtil;

import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ThreadSaveJPG extends Thread
{

	private static final int MAX_SIDE_LENGHT = 3200;
	private String TAG = ThreadSaveJPG.class.getSimpleName();
	private CameraActivity cameraActivity;
	private byte[] data;
	private String path;
	private Context context;

	public ThreadSaveJPG(CameraActivity cameraActivity, byte[] data)
	{
		this.cameraActivity = cameraActivity;
		this.data = data;
		this.context = this.cameraActivity.getApplicationContext();
		//this.path = FileUtil.getSDCadrPath() + "/universcan/MyGallery/未分类/";
		this.path = FileUtil.getSDCadrPath() + CameraActivity.FILE_SAVE_PATH + CameraActivity.FILE_SAVE_DIR_NAME + "/";
	}

	@Override
	public void run()
	{
		super.run();
		Log.i(TAG, this.getName());

		if (null == cameraActivity)
		{
			return;
		}

		Bitmap bitmap = BitmapProcess.JEPGByteToBitmap(data, Config.RGB_565,MAX_SIDE_LENGHT);

		if (null == bitmap)
		{
			LogUtil.i("bitmap null call restart preview");
			this.cameraActivity.messageToRestartPreview();
			return;
		}


		if (null == context)
		{
			LogUtil.i("context is null , call restart preview");
			this.cameraActivity.messageToRestartPreview();
			return;
		}

		/*
		while (null != ThreadBCardScanning.getThreadBCardScanning())
		{
			ThreadBCardScanning.getThreadBCardScanning().setQuit(true);
			try {
				ThreadBCardRecognize.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		*/

		byte[] byteData = new byte[bitmap.getWidth() * bitmap.getHeight()];
		LogUtil.i("width * height: " + String.valueOf(bitmap.getWidth()) + "_" + String.valueOf(bitmap.getHeight()));

		//fjm add
		ByteArrayOutputStream baos2 = new ByteArrayOutputStream();

		//Bitmap mMyBitmap = Bitmap.createScaledBitmap(bitmap, 720, 1280,true);

		//bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos2);
		bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos2);
		//mMyBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos2);

		if (!bitmap.isRecycled())
		{
				bitmap.recycle();
		}
		bitmap = null;

		byte[] dataArray = baos2.toByteArray();
		try
		{
			baos2.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		path += CurrentTime.getCurrentTime() + ".jpg";
		LogUtil.i( "file path to be write is " + path);

		try
		{
			FileUtil.writeFileBytes(dataArray, path);
		}
		catch (IOException e1)
		{
			e1.printStackTrace();
			return;
		}




		this.cameraActivity.messageToCrop(path);


		//fjm add end


		/*
		int rst= ImageProcessing.BitmapRGB565ToGrayByte(bitmap, byteData,
				bitmap.getWidth(), bitmap.getHeight(), 1);

		Log.i(TAG, String.valueOf(byteData));

		String response = null;
		try
		{
			response = BCard.getBCardText(byteData, bitmap.getWidth(),
					bitmap.getHeight());
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		if (null != response)
		{
			// path=ThreadWriteBitmap.writeImage(path, bitmap);
			try {
				this.displayName(response);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			long startTime = System.currentTimeMillis();

			Bitmap bitmapafterR = BCardProcessing.rotation(bitmap,
					BCardProcessing.getRotation(response));

			long endTime = System.currentTimeMillis();
			String time = "rotation:" + String.valueOf(endTime - startTime)
					+ "\r\n";

			//BCardProcessing.setBitmap(bitmapafterR);
			//BCardProcessing.detect(bitmapafterR);
			

			path = cameraActivity.preProcessBCard(bitmapafterR, false);// 对名片存图进行预处理
			cameraActivity.handleToBCard(path, response, false);
			ModeCtrl.setBCardScanningStop(true);
			if (bitmapafterR != null) {
				if (!bitmapafterR.isRecycled()) {
					bitmapafterR.recycle();
				}
				bitmapafterR = null;
			}
			if(bitmap!=null){
				if(!bitmap.isRecycled()){
					bitmap.recycle();
					System.gc();
				}
			}
		}
		else
		{
			Log.i(TAG, "show tips 请将名片充满整个屏幕");
			DrawManager.setPromptText("请将名片充满整个屏幕");
			this.cameraActivity.messageToRestartPreview();
			new ThreadWriteBitmap(path, bitmap,true).start();
		}
		BCardProcessing.setBitmap(null);
		*/
		

		// ---------------------------------------------------------------分类结果处理
		// long end = System.currentTimeMillis();
	}

	private void displayName(String response) throws JSONException
	{
		/*
		boolean flag = (Boolean) SharedPreferencesUtil.getData(context,
				SetRecogActivity.SR_BCARD, true);
		if (flag) {

			JSONObject content = new JSONObject(response);
			JSONArray names = null;
			names = content.getJSONArray("name");
			String text = "I do !";
			text = names.getString(0);
			DrawManager.setPromptText(text);
			Log.i(TAG, "<<<<<<<<<<<---mid--->>>>>>>>>>>");
		} else {
			DrawManager.setPromptText("请修改设置选项支持名片识别功能");
		}
		*/
	}

}
