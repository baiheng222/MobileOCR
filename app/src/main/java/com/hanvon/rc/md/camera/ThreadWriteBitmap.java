package com.hanvon.rc.md.camera;

import android.graphics.Bitmap;
import android.util.Log;

import com.hanvon.rc.utils.FileUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ThreadWriteBitmap extends Thread
{
	private String fileName;
	private Bitmap bitmap;
	private boolean isRecycle;

	public ThreadWriteBitmap(String fileName, Bitmap bitmap, boolean isRecycle) {
		this.fileName = fileName;
		this.bitmap = bitmap;
		this.isRecycle = isRecycle;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		writeImage(fileName, bitmap, isRecycle);
	}

	public static String writeImage(String path, Bitmap bitmap,
									boolean isRecycle) {

		ByteArrayOutputStream baos2 = new ByteArrayOutputStream();

		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos2);
		if (isRecycle) {
			if (!bitmap.isRecycled()) {
				bitmap.recycle();
			}
		}

		bitmap = null;

		byte[] dataArray = baos2.toByteArray();
		try {
			baos2.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		path += CurrentTime.getCurrentTime() + ".jpg";
		Log.i("ThreadWriteBitmap", "file path to be write is " + path);
		try {
			FileUtil.writeFileBytes(dataArray, path);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return path;
	}

}
