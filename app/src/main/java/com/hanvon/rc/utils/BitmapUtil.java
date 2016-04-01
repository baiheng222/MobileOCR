package com.hanvon.rc.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff;
import android.util.Base64;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class BitmapUtil
{
	
private static int  MINIBOUNDLEN  = 1080;
	
	/**
	 * 将位图旋转角度，放回旋转后的位图
	 * @param b
	 * @param rotateDegree
	 * @return
	 */
	public static Bitmap getRotateBitmap(Bitmap b, float rotateDegree){
		Matrix matrix = new Matrix();
		matrix.postRotate((float)rotateDegree);
		Bitmap rotaBitmap = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), matrix, false);
		//add
		if(b != null && !b.isRecycled()){
			b.recycle();
			b = null;
		}
		return rotaBitmap;
	}
	/**
	 * 将位图按照指定的比例转换成字节数组
	 * @param bm
	 * @param ratio
	 * @return
	 */
	public static byte[] Bitmap2Bytes(Bitmap bm, int ratio) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.JPEG, ratio, baos);
		//add
		if(bm != null && !bm.isRecycled()){
			bm.recycle();
			bm = null;
		}
		return baos.toByteArray();
	}
	
	
	
	/**
	 * 根据原图计算InSampleSize
	 * @param options
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static int calculateInSampleSize(Options options, int reqWidth, int reqHeight) {
		// 源图片的高度和宽度
		final int width = options.outWidth;
		final int height = options.outHeight;
		int inSampleSize = 1;

		if (width >= height) { // 横型图片
			if (width > reqWidth || height > reqHeight) {
				final int heightRatio = Math.round((float) height
						/ (float) reqHeight);
				final int widthRatio = Math.round((float) width
						/ (float) reqWidth);
				// 选择宽和高中最小的比率作为inSampleSize的值，这样可以保证最终图片的宽和高
				// 一定都会大于等于目标的宽和高
				inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
			}
		} else {
			if (width > reqHeight || height > reqWidth) {
				final int heightRatio = Math.round((float) height
						/ (float) reqWidth);
				final int widthRatio = Math.round((float) width
						/ (float) reqHeight);
				inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
			}
		}

		return inSampleSize;
	}
	
	/**
	 * 将位图按照默认的比例转换成字符串
	 * return after Base64 string 
	 * @param picBitmap
	 * @return
	 */
	public static String encodeBitmapData(Bitmap picBitmap) {
		try {
			byte[] resBuffer = null;
			resBuffer = Bitmap2Bytes(picBitmap, 100);
//			FileUtil.writeFileBytes(resBuffer, "/storage/sdcard0/DCIM/IMG_20140804_1.jpg");
			if (null != resBuffer) {
				return Base64.encodeToString(resBuffer, Base64.DEFAULT);
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if(picBitmap != null && !picBitmap.isRecycled()){
				picBitmap.recycle();
				picBitmap = null;
			}
		}
	}
	
	/**
	 * 将位图按照指定的比例转换成字符串
	 * @param picBitmap
	 * @param ratio
	 * @return
	 */
	public static String encodeBitmapData(Bitmap picBitmap, int ratio) {
		if (ratio > 100) {
			ratio = 100;
		}
		if (ratio < 0 ) {
			ratio = 0;
		}
		try {
			byte[] resBuffer = null;
			resBuffer = Bitmap2Bytes(picBitmap, ratio);
//			FileUtil.writeFileBytes(resBuffer, Environment.getExternalStorageDirectory() + "/0000000.jpg");
//			Log.e("encodeBitmapData", "分类图片的大小" + resBuffer.length);
			if (null != resBuffer) {
				return Base64.encodeToString(resBuffer, Base64.DEFAULT);
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if(picBitmap != null && !picBitmap.isRecycled()){
				picBitmap.recycle();
				picBitmap = null;
			}
		}
	}
	
	/**
	 * 将字符串转换成位图
	 * @param string
	 * @return
	 */
	public Bitmap stringtoBitmap(String string){
	    
	    Bitmap bitmap=null;
	    try {
	    	byte[] bitmapArray;
	    	bitmapArray = Base64.decode(string, Base64.DEFAULT);
	    	bitmap= BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
	    return bitmap;
	}
	
	/**
	 * 对位图进行灰度处理
	 * @param bmSrc
	 * @return bmpGray
	 */
	public static Bitmap bitmap2Gray(Bitmap bmSrc) {
		// 得到图片的长和宽
		int width = bmSrc.getWidth();
		int height = bmSrc.getHeight();
		// 创建目标灰度图像
		Bitmap bmpGray = null;
		bmpGray = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
		// 创建画布
		Canvas c = new Canvas(bmpGray);
		Paint paint = new Paint();
		ColorMatrix cm = new ColorMatrix();
		cm.setSaturation(0);
		ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
		paint.setColorFilter(f);
		c.drawBitmap(bmSrc, 0, 0, paint);
		//add
		if(bmSrc != null && !bmSrc.isRecycled()){
			bmSrc.recycle();
			bmSrc = null;
		}
		return bmpGray;
	}

	/**
	 * 对图像进行线性灰度化
	 * @param image
	 * @return
	 */
	public static Bitmap lineGrey(Bitmap image)
	{
		//得到图像的宽度和长度
		int width = image.getWidth();
		int height = image.getHeight();
		//创建线性拉升灰度图像
		Bitmap linegray = null;
		linegray = image.copy(Config.ARGB_8888, true);
		//依次循环对图像的像素进行处理
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				//得到每点的像素值
				int col = image.getPixel(i, j);
				int alpha = col & 0xFF000000;
				int red = (col & 0x00FF0000) >> 16;
				int green = (col & 0x0000FF00) >> 8;
				int blue = (col & 0x000000FF);
				// 增加了图像的亮度
				red = (int) (1.1 * red + 30);
				green = (int) (1.1 * green + 30);
				blue = (int) (1.1 * blue + 30);
				//对图像像素越界进行处理
				if (red >= 255)
				{
					red = 255;
				}

				if (green >= 255) {
					green = 255;
				}

				if (blue >= 255) {
					blue = 255;
				}
				// 新的ARGB
				int newColor = alpha | (red << 16) | (green << 8) | blue;
				//设置新图像的RGB值
				linegray.setPixel(i, j, newColor);
			}
		}
		//add
		if(image != null && !image.isRecycled()){
			image.recycle();
			image = null;
		}
		return linegray;
	}


	/**
	 * 对图像进行二值化处理
	 * @param graymap
	 * @return
	 */
	public static Bitmap gray2Binary(Bitmap graymap) {// 该函数实现对图像进行二值化处理
		//得到图形的宽度和长度
		int width = graymap.getWidth();
		int height = graymap.getHeight();
		//创建二值化图像
		Bitmap binarymap = null;
		binarymap = graymap.copy(Config.ARGB_8888, true);
		//依次循环，对图像的像素进行处理
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				//得到当前像素的值
				int col = binarymap.getPixel(i, j);
				//得到alpha通道的值
				int alpha = col & 0xFF000000;
				//得到图像的像素RGB的值
				int red = (col & 0x00FF0000) >> 16;
				int green = (col & 0x0000FF00) >> 8;
				int blue = (col & 0x000000FF);
				// 用公式X = 0.3×R+0.59×G+0.11×B计算出X代替原来的RGB
				int gray = (int) ((float) red * 0.3 + (float) green * 0.59 + (float) blue * 0.11);
				//对图像进行二值化处理
				if (gray <= 95) {
					gray = 0;
				} else {
					gray = 255;
				}
				// 新的ARGB
				int newColor = alpha | (gray << 16) | (gray << 8) | gray;
				//设置新图像的当前像素值
				binarymap.setPixel(i, j, newColor);
			}
		}
		return binarymap;
	}
	public static int getImageScale(String imagePath) {
		BitmapFactory.Options option = new BitmapFactory.Options();
		// set inJustDecodeBounds to true, allowing the caller to query the bitmap info without having to allocate the
		// memory for its pixels.
		option.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(imagePath, option);

		int tmp = option.outWidth > option.outHeight ? option.outHeight : option.outWidth;

		int scale = 1;
		if (tmp > MINIBOUNDLEN) {
			scale = 2;
		}
		if (tmp > MINIBOUNDLEN * 2) {
			scale = 3;
		}
		if (tmp > MINIBOUNDLEN * 3) {
			scale = 4;
		}

		return scale;
	}

	public static Bitmap addColor(Bitmap bitmap, int left, int top, int right, int bottom, int color){
		//创建涂上颜色的图像
		Bitmap colorBitmap = null;
		colorBitmap = bitmap.copy(Config.ARGB_8888, true);
		Canvas canvas = new Canvas(colorBitmap);
		Paint paint = new Paint();
		paint.setStyle(Style.FILL);
		paint.setAntiAlias(true);
		paint.setColor(color);
		canvas.drawRect(left,top,right,bottom, paint);
		Matrix m = new Matrix();
		canvas.drawBitmap(colorBitmap,m,paint);
		//add
		if(bitmap != null && !bitmap.isRecycled()){
			bitmap.recycle();
			bitmap = null;
		}
		return colorBitmap;

	}

	public static Bitmap clearColor(Bitmap bitmap, int left, int top, int right, int bottom){
		//创建清除颜色的图像
		Bitmap clearBitmap = null;
		clearBitmap = bitmap.copy(Config.ARGB_8888, true);
		Canvas canvas = new Canvas(clearBitmap);
		Paint paint = new Paint();
		canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
//		paint.setStyle(Style.FILL);
//		paint.setAntiAlias(true);
//		paint.setColor(color);
//		canvas.drawRect(left,top,right,bottom, paint);
		Matrix m = new Matrix();
		canvas.drawBitmap(clearBitmap,m,paint);
		return clearBitmap;

	}

	/**
     * 以最省内存的方式读取本地资源的图片
     * @param context
     * @param resId
     * @return
     */
    public static Bitmap readIsBitmap(Context context, int resId){
         BitmapFactory.Options opt = new BitmapFactory.Options();
         opt.inPreferredConfig = Bitmap.Config.RGB_565;
         opt.inPurgeable = true;
         opt.inInputShareable = true;
           //获取资源图片
         InputStream is = context.getResources().openRawResource(resId);
         return BitmapFactory.decodeStream(is,null,opt);
   }

    /** 以最省内存的方式读取资源图片
     * @param context
     * @param resId
     * @return
     */
    public static Bitmap getIsBitmap(Context context, int resId){
    	InputStream is2 = context.getResources().openRawResource(resId);
        BitmapFactory.Options options2=new BitmapFactory.Options();
        options2.inJustDecodeBounds = false;
        options2.inSampleSize = 2;   //width，hight设为原来的2分一
        Bitmap bmp = BitmapFactory.decodeStream(is2,null,options2);
		return bmp;
    }

    /**
     * 以最省内存的方式读取本地的图片
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
           //获取资源图片  
         FileInputStream fis = null;
         BufferedInputStream bs = null;
         fis = new FileInputStream(path);
         bs = new BufferedInputStream(fis);
         return BitmapFactory.decodeStream(bs,null,opt);
   }
}
