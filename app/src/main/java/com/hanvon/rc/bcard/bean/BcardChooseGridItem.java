package com.hanvon.rc.bcard.bean;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.hanvon.rc.R;

public class BcardChooseGridItem extends RelativeLayout
{
	private Context mContext;
	private boolean mCheck;
	private ImageView mImageView;
	private ImageView mSelect;

	public BcardChooseGridItem(Context context) {
		this(context, null, 0);
	}
	
	public BcardChooseGridItem(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

	public BcardChooseGridItem(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		LayoutInflater.from(mContext).inflate(R.layout.bcard_choose_gridview_item, this);
		mImageView = (ImageView)findViewById(R.id.photo_img_view);
		mSelect = (ImageView)findViewById(R.id.photo_select);
		/*int imgW = (int)((DPIUtil.screen_width  - DPIUtil.dip2px(context, 32) * 3f - 20) / 3f);
		mImageView.getLayoutParams().width = imgW ;
		mImageView.getLayoutParams().height =  imgW; *///将dip转换成px
//		mName.getLayoutParams().width = imgW;
	}
	
	
	public void setChecked(boolean checked) {
		mCheck = checked;
//		System.out.println(checked);
//		mSelect.setImageDrawable(getResources().getDrawable(R.drawable.cb_on));
		mSelect.setImageResource(mCheck ? R.mipmap.pic_selected : R.mipmap.pic_unselected);
		
	}

	public boolean getChecked() {
		return mCheck;
	}

	public void setImgResID(int id){
		if(mImageView != null){
			mImageView.setBackgroundResource(id);
		}
	}
	
	public void SetBitmap(Bitmap bit){
		if(mImageView != null){
			mImageView.setImageBitmap(bit);
		}
	}
	
	public ImageView getImageView(){
		return mImageView;
	}

	
	public void toggle() {
		// TODO Auto-generated method stub
		mCheck = !mCheck;
	}


	public ImageView getmSelect() {
		return mSelect;
	}

	public void setmSelect(ImageView mSelect) {
		this.mSelect = mSelect;
	}

}
