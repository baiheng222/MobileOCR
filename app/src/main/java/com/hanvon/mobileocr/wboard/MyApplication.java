package com.hanvon.mobileocr.wboard;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.StrictMode;

import com.hanvon.mobileocr.wboard.Constants.Config;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

public class MyApplication extends Application
{
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@SuppressWarnings("unused")
	@Override
	public void onCreate()
	{
		if (Config.DEVELOPER_MODE && Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyDialog().build());
			StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyDeath().build());
		}

		super.onCreate();

		initImageLoader(getApplicationContext());
	}

	public static void initImageLoader(Context context) {
		// This configuration tuning is custom. You can tune every option, you may tune some of them,
		// or you can create default configuration by
		//  ImageLoaderConfiguration.createDefault(this);
		// method.
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
				.threadPriority(Thread.NORM_PRIORITY - 1) //加载图片的线程数
				.denyCacheImageMultipleSizesInMemory() //解码图像的大尺寸将在内存中缓存先前解码图像的小尺寸
				.discCacheFileNameGenerator(new Md5FileNameGenerator()) //设置磁盘缓存文件名称
				.tasksProcessingOrder(QueueProcessingType.LIFO) //设置加载显示图片队列进程
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024)//缓存到内存的最大数据
                .discCacheSize(50 * 1024 * 1024) //缓存到文件的最大数据
                .discCacheFileCount(100) //文件数量
                .writeDebugLogs() // Remove for release app
				.build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
	}

	@Override
	public void onTerminate() {
		// TODO Auto-generated method stub
		super.onTerminate();
		ImageLoader.getInstance().clearMemoryCache();
		ImageLoader.getInstance().clearDiscCache();
	}
	
	
}
