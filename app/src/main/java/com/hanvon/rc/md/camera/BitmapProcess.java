package com.hanvon.rc.md.camera;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class BitmapProcess
{

	public BitmapProcess() {

	}

	/**
	 * 
	 * @param path
	 * @param bitmapConfig
	 * @return
	 */
	public static Bitmap pathToBitmap(String path, Config bitmapConfig) {
		// 获取资源图片
		FileInputStream fis = null;
		BufferedInputStream bs = null;
		try {
			fis = new FileInputStream(path);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		bs = new BufferedInputStream(fis);
		// 解析图片
		BitmapFactory.Options opt = new BitmapFactory.Options();

		opt.inPreferredConfig = bitmapConfig;
		opt.inPurgeable = true;
		opt.inJustDecodeBounds = false;

		Bitmap bitmap = BitmapFactory.decodeStream(bs, null, opt);
		return bitmap;
	}

	/**
	 *
	 * @param path
	 * @param bitmapConfig
	 * @param maxSideLenght
	 * @return
	 * @throws FileNotFoundException
	 */
	public static Bitmap pathToBitmap(String path, Config bitmapConfig,
									  int maxSideLenght) throws FileNotFoundException
	{

		// 获取资源图片
		FileInputStream fis = null;
		BufferedInputStream bs = null;
		fis = new FileInputStream(path);
		bs = new BufferedInputStream(fis);
		// 解析图片
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inJustDecodeBounds = true;

		BitmapFactory.decodeStream(bs, null, opt);
		int width = opt.outWidth;
		int height = opt.outHeight;
		int SrcLongSize;
		if (width > height) {
			SrcLongSize = width;
		} else {
			SrcLongSize = height;
		}
		opt.inPreferredConfig = bitmapConfig;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		opt.inSampleSize = SrcLongSize / maxSideLenght + 1;
		opt.inJustDecodeBounds = false;

		fis = new FileInputStream(path);
		bs = new BufferedInputStream(fis);
		Bitmap bitmap = BitmapFactory.decodeStream(bs, null, opt);
		return bitmap;
	}

	public static Bitmap JEPGByteToBitmap(byte[] data, Config bitmapConfig) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inPreferredConfig = bitmapConfig;
		opts.inPurgeable = true;
		opts.inInputShareable = true;
		opts.inJustDecodeBounds = false;

		return BitmapFactory.decodeByteArray(data, 0, data.length, opts);
	}

	public static Bitmap JEPGByteToBitmap(byte[] data, Config bitmapConfig,
										  int maxSideLenght) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inPreferredConfig = bitmapConfig;
		opts.inPurgeable = true;
		opts.inInputShareable = true;
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeByteArray(data, 0, data.length, opts);
		int width = opts.outWidth;
		int height = opts.outHeight;
		int SrcLongSize;
		if (width > height) {
			SrcLongSize = width;
		} else {
			SrcLongSize = height;
		}
		opts.inSampleSize = SrcLongSize / maxSideLenght + 1;

		opts.inJustDecodeBounds = false;

		return BitmapFactory.decodeByteArray(data, 0, data.length, opts);
	}

	/**
	 * 以最省内存的方式读取本地的图片
	 *
	 * @param context
	 * @param resId
	 * @return
	 * @throws FileNotFoundException
	 */
	public static Bitmap readIsBitmap(String path) throws FileNotFoundException
	{
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		// 获取资源图片
		FileInputStream fis = null;
		BufferedInputStream bs = null;
		fis = new FileInputStream(path);
		bs = new BufferedInputStream(fis);
		return BitmapFactory.decodeStream(bs, null, opt);
	}

	public static byte[] BitmapToByteArray(Bitmap bitmap, boolean isSamping,
										   int longSize) {
		// TODO Auto-generated method stub

		if (null == bitmap) {
			return null;
		}

		ByteArrayOutputStream baos2 = new ByteArrayOutputStream();

		if (isSamping) {
			Bitmap samplingBitmap = BitmapProcess.getSamplingBitmapToAssign(
					bitmap, longSize);
			if (null != samplingBitmap) {
				samplingBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos2);
				if (!samplingBitmap.isRecycled()) {
					samplingBitmap.recycle();
				}
				samplingBitmap = null;
			}

		} else {
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos2);
			if (!bitmap.isRecycled()) {
				bitmap.recycle();
			}
			bitmap = null;
		}

		byte[] dataArray = baos2.toByteArray();
		try {
			baos2.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dataArray;
	}

	public static Bitmap getSamplingBitmapToAssign(Bitmap bitmap, int longSize) {

		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		boolean isBig = false;
		if (bitmap.getWidth() > bitmap.getHeight()) {
			if (bitmap.getWidth() > longSize) {
				isBig = true;
				width = longSize;
				height = longSize * bitmap.getHeight() / bitmap.getWidth();
			}
		} else {
			if (bitmap.getHeight() > longSize) {
				isBig = true;
				height = longSize;
				width = longSize * bitmap.getWidth() / bitmap.getHeight();
			}

		}
		if (isBig) {

			Bitmap samplingBitmap = Bitmap.createScaledBitmap(bitmap, width,
					height, false);
			return samplingBitmap;

		}
		return null;
	}

	public static byte[] byteArraySampling(byte[] data, int longSize,
			int compressRatio) {
		if (null == data) {
			return null;
		}
		if (compressRatio > 100 || compressRatio < 0) {
			compressRatio = 100;
		}

		Options opts = new Options();
		opts.inPreferredConfig = Config.ARGB_8888;
		opts.inPurgeable = true;
		opts.inInputShareable = true;
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeByteArray(data, 0, data.length, opts);
		int width = opts.outWidth;
		int height = opts.outHeight;
		int SrcLongSize;
		if (width > height) {
			SrcLongSize = width;
		} else {
			SrcLongSize = height;
		}
		opts.inSampleSize = SrcLongSize / longSize;

		opts.inJustDecodeBounds = false;

		Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length,
				opts);
		data = null;
		if (null == bitmap) {
			return null;
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, compressRatio, baos);
		bitmap.recycle();
		bitmap = null;
		byte[] dstData = null;
		dstData = baos.toByteArray();
		try {
			baos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dstData;
	}

}
