package com.hanvon.mobileocr.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.Log;

public class DisplayUtil
{
	private static final String TAG = "DisplayUtil";
	/**
	 * dipתpx
	 * @param context
	 * @param dipValue
	 * @return
	 */
	public static int dip2px(Context context, float dipValue){
		final float scale = context.getResources().getDisplayMetrics().density;                 
		return (int)(dipValue * scale + 0.5f);         
	}     
	
	/**
	 * pxתdip
	 * @param context
	 * @param pxValue
	 * @return
	 */
	public static int px2dip(Context context, float pxValue){
		final float scale = context.getResources().getDisplayMetrics().density;                 
		return (int)(pxValue / scale + 0.5f);         
	} 
	
	/**
	 * ��ȡ��Ļ��Ⱥ͸߶ȣ���λΪpx
	 * @param context
	 * @return
	 */
	public static Point getScreenMetrics(Context context){
		DisplayMetrics dm =context.getResources().getDisplayMetrics();
		int w_screen = dm.widthPixels;
		int h_screen = dm.heightPixels;
		Log.i(TAG, "Screen---Width = " + w_screen + " Height = " + h_screen + " densityDpi = " + dm.densityDpi);
		return new Point(w_screen, h_screen);
		
	}
	
	/**
	 * ��ȡ��Ļ�����
	 * @param context
	 * @return
	 */
	public static float getScreenRate(Context context){
		Point P = getScreenMetrics(context);
		float H = P.y;
		float W = P.x;
		return (H/W);
	}
	
	 /**
     * 获取状态栏的高度
     * @param activity
     * @return > 0 success; <= 0 fail
     */
    public static int getStatusHeight(Activity activity){
        int statusHeight = 0;
        Rect localRect = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(localRect);
        statusHeight = localRect.top;
      //以防在onCreate、onResume中获取的值是0，根据sdk获取
        if (0 == statusHeight){
            Class<?> localClass;
            try {
                localClass = Class.forName("com.android.internal.R$dimen");
                Object localObject = localClass.newInstance();
                int x = Integer.parseInt(localClass.getField("status_bar_height").get(localObject).toString());
                statusHeight = activity.getResources().getDimensionPixelSize(x);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (NumberFormatException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
        return statusHeight;
    }
}
