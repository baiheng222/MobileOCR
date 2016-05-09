package com.hanvon.rc.bcard.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseAdapter;

import com.hanvon.rc.bcard.bean.BcardChooseGridItem;
import com.hanvon.rc.wboard.Constants;
import com.hanvon.rc.wboard.Util;
import com.hanvon.rc.wboard.bean.PhotoAlbum;
import com.hanvon.rc.wboard.bean.PhotoItem;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;

public class BcardChoosePicAdapter extends BaseAdapter
{
	private static final String TAG = "BcardChoosePicAdapter";
	private Context context;
	private PhotoAlbum aibum;
	private ArrayList<PhotoItem> gl_arr;
	private ImageLoadingListener animateFirstListener = new Util.AnimateFirstDisplayListener();
	private boolean firstLoading;
	public BcardChoosePicAdapter(Context context, PhotoAlbum aibum, ArrayList<PhotoItem> gl_arr) {
		this.context = context;
		this.aibum = aibum;
		this.gl_arr=gl_arr;
	}

	@Override
	public int getCount() {
		if (gl_arr==null) {
			return aibum.getBitList().size();
		}else{
			return gl_arr.size();
		}
	}

	@Override
	public PhotoItem getItem(int position) {
		if(gl_arr==null){
			return aibum.getBitList().get(position);
		}else{
			return gl_arr.get(position);
		}
		
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final BcardChooseGridItem item;
		final View v = convertView;
		final int pos = position;
		if(convertView == null){
			item = new BcardChooseGridItem(context);
			 item.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                     LayoutParams.MATCH_PARENT));
		}else{
			item = (BcardChooseGridItem)convertView;
		}
		// 通过ID 加载缩略图
		if (gl_arr==null)
		{
//			Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(context.getContentResolver(),  aibum.getBitList().get(position).getPhotoID(), Thumbnails.MICRO_KIND, null);
//			item.SetBitmap(bitmap);
			String temp = aibum.getBitList().get(position).getPath();
			Log.i(TAG, "pic path is " + temp);
//			Constants.imageLoader.displayImage("file://" + aibum.getBitList().get(position).getPath(), item.getImageView(), Constants.image_display_options, animateFirstListener);
			/*Bitmap bitmap = null;
			try {
				bitmap = BitmapUtil.readIsBitmap(temp);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			item.getImageView().setImageBitmap(bitmap);*/
			if(Util.firstLoad)
			{
				Constants.imageLoader.displayImage("file://" + aibum.getBitList().get(position).getPath(), item.getImageView(),Constants.image_display_options,animateFirstListener);
			}
			else
			{
				Constants.imageLoader.displayImage("file://" + aibum.getBitList().get(position).getPath(), item.getImageView(),null,animateFirstListener);
				Util.firstLoad = true ;
			}
	        boolean flag = aibum.getBitList().get(position).isSelect();
			item.setChecked(flag);
//			item.setName(temp.substring(temp.lastIndexOf("/")+1));
		}
		else
		{
//			Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(context.getContentResolver(),  gl_arr.get(position).getPhotoID(), Thumbnails.MICRO_KIND, null);
//			item.SetBitmap(bitmap);
			Constants.imageLoader.displayImage("file://" + gl_arr.get(position).getPath(), item.getImageView(), Constants.image_display_options, animateFirstListener);
		}
		
		item.getImageView().setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mListener != null) {
                    mListener.onGridItemClick(v, pos);
				}
			}
		});
		item.getmSelect().setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mListener != null) {
                    mListener.onGridItemClick(v, pos);
				}
			}
		});
		return item;
	}

	public boolean isFirstLoading() {
		return firstLoading;
	}

	public void setFirstLoading(boolean firstLoading) {
		this.firstLoading = firstLoading;
	}

	
	 /**
    * 单击事件监听器
    */
   private onGridItemClickListener mListener = null;

   public void setOnGridItemClickListener(onGridItemClickListener listener){
   		mListener = listener;
   }
   
   public interface onGridItemClickListener {
       void onGridItemClick(View v, int position);
   }
}