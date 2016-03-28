package com.hanvon.mobileocr.wboard;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: hetuo
 * Date: 13-10-28
 * Time: 上午11:14
 * To change this template use File | Settings | File Templates.
 */
public class Util
{
    //获取图片所在文件夹名称
	public static boolean firstLoad = true;
    public static String getDir(String path)
    {
        String subString = path.substring(0, path.lastIndexOf('/'));
        return subString.substring(subString.lastIndexOf('/') + 1, subString.length());
    }

    /** 
     * 图片加载第一次显示监听器 
     * @author Administrator 
     * 
     */  
    public static class AnimateFirstDisplayListener extends SimpleImageLoadingListener
    {
        static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());
        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay) {
                    FadeInBitmapDisplayer.animate(imageView, 500); //设置image隐藏动画500ms
                    displayedImages.add(0,imageUri);//将图片uri添加到集合中
                }
                
            }
            firstLoad = false;  
        }
        
    }


}
