/*******************************************************************************
 * Copyright 2011-2013 Sergey Tarasevich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.hanvon.mobileocr.wboard;


import com.hanvon.mobileocr.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

/**
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public final class Constants
{
    public static ImageLoader imageLoader = ImageLoader.getInstance();
    public static DisplayImageOptions image_display_options = new DisplayImageOptions.Builder()
			    .showImageOnLoading(R.mipmap.ic_stub)
			    .showImageForEmptyUri(R.mipmap.ic_empty)
			    .showImageOnFail(R.mipmap.ic_error)
			    .bitmapConfig(android.graphics.Bitmap.Config.RGB_565)
			    .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
			//   .displayer(new RoundedBitmapDisplayer(20))//设置显示圆角图片 
			    .cacheInMemory(true)
			    .cacheOnDisc(true)
			    .build();

/*    public static DisplayImageOptions image_display_options_first = new DisplayImageOptions.Builder()
             .showImageOnLoading(R.drawable.ic_stub)
            .build();
   public static DisplayImageOptions image_display_options_not_first = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.ic_stub)
			.showImageForEmptyUri(R.drawable.ic_empty) 
			.showImageOnFail(R.drawable.ic_error)
			//    .displayer(new RoundedBitmapDisplayer(20))//设置显示圆角图片 
			.cacheInMemory(true)
			.cacheOnDisc(true)
			.build();*/
	public static class Config {
		public static final boolean DEVELOPER_MODE = false;
	}
}
