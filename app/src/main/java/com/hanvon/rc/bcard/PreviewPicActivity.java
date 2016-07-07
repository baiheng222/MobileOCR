package com.hanvon.rc.bcard;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

//import com.baidu.mobstat.StatService;
//import com.hanvon.mobileocr.md.camera.activity.ThreadBCardListPathProcess;

import com.hanvon.rc.R;
import com.hanvon.rc.utils.BitmapUtil;
import com.hanvon.rc.utils.DisplayUtil;
import com.hanvon.rc.utils.InfoMsg;
import com.hanvon.rc.utils.LogUtil;
import com.hanvon.rc.wboard.bean.PhotoAlbum;

import java.io.File;


public class PreviewPicActivity extends Activity implements OnClickListener,OnTouchListener
{
	private RelativeLayout lay_done,lay_top;
	private TextView txt_done,txt_back,txt_selected_count, txt_del;
	private ImageView preview_img_back,preview_img,selected;
	private Point p = new Point();
	private int width,height;
	private Matrix matrix= new Matrix(),saveMatrix = new Matrix();//图形变换矩阵
//	private ArrayList<String> pathList = 
	private int picturePos = 0;
	private boolean isLastPicture = false;
	//图片的三种状态
	private static final int NONE = 0;
	private static final int DRAG = 1;//拖动
	private static final int ZOOM = 2;//
	int mode = NONE;
	
	PointF start = new PointF();
	PointF mid = new PointF();
	float oldDist = 1f;
	private boolean keyUpDown = false;
	private int timer = 0;
	
	private int clickCount = 0;
	private int img_width,img_height,w,h;
	private PhotoAlbum album,previewAlbum;
	private String comeForm,parentActivity;
	private final String PREVIEW = "preview";
	private final String BIG = "big";
	private int chooseNum = 0;

	private int recoMode;
	private int lastImageSelectItem;

	private Context mContext;
	private final String mPageName = "PreviewPicActivity";

	private final static int MAX_PIC_NUM = 9;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		getSendData();

		setContentView(R.layout.bc_choose_preview);
		
		mContext = this;
	
		initUI();
		addUIListener();
		
		if(comeForm.equals(PREVIEW)) //预览按钮进来
		{
			
		}
		else if(comeForm.equals(BIG)) //点击图片进来
		{
			
		}
		
		for(int i = 0;i<album.getBitList().size();i++) //获取选中的图片个数
		{
			if(album.getBitList().get(i).isSelect())
			{
				chooseNum++;
			}
		}

		LogUtil.i("choosnum is " + chooseNum);
		
		selected.setImageResource(album.getBitList().get(picturePos).isSelect()? R.mipmap.pic_selected:R.mipmap.pic_unselected);//设置顶部的选中按钮是否选中
		
		setSelectedCount();


		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inSampleSize = BitmapUtil.getImageScale(album.getBitList().get(picturePos).getPath());
		Bitmap b = BitmapFactory.decodeFile(album.getBitList().get(picturePos).getPath(),opts);
		preview_img.setImageBitmap(b);

		setPreviewImage();

		ViewTreeObserver vtos = preview_img.getViewTreeObserver();
        //为其添加监听器
        vtos.addOnGlobalLayoutListener(new OnGlobalLayoutListener()
		{
            @Override
            public void onGlobalLayout() 
            {
              preview_img.getViewTreeObserver().removeGlobalOnLayoutListener(this);
              img_height = preview_img.getDrawable().getBounds().height();
              img_width = preview_img.getDrawable().getBounds().width();
              w= img_width;
              h = img_height;
              System.out.println(img_width+"  "+img_height );
             }
        });

		/**
         * 设置图片居中
         */
		p = DisplayUtil.getScreenMetrics(this);
        int width=p.x/2-b.getWidth()/2;
		//int height=(p.y-192)/2-b.getHeight()/2;
        int height=(p.y-96)/2-b.getHeight()/2;
        matrix.postTranslate(width,height);
        preview_img.setImageMatrix(matrix);
	}


	private void setPreviewImage()
	{
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inSampleSize = BitmapUtil.getImageScale(album.getBitList().get(picturePos).getPath());
		Bitmap b = BitmapFactory.decodeFile(album.getBitList().get(picturePos).getPath(),opts);
		preview_img.setImageBitmap(b);
	}

	private void setSelectedCount()
	{
		LogUtil.i("setSelectCount !!!");
		if(chooseNum != 0)
		{
			LogUtil.i("chooseNum is " + chooseNum);
			txt_selected_count.setVisibility(View.VISIBLE);
			if(chooseNum > MAX_PIC_NUM)
			{
				txt_selected_count.setText(String.valueOf(MAX_PIC_NUM));
			}
			else
			{
				txt_selected_count.setText(String.valueOf(chooseNum));
			}
		}
		else
		{
			txt_selected_count.setVisibility(View.GONE);
		}
	}

	private void getSendData()
	{
		// TODO Auto-generated method stub
		album = (PhotoAlbum) this.getIntent().getSerializableExtra("data");
		LogUtil.i("pic list size is " + album.getBitList().size());
		picturePos = this.getIntent().getExtras().getInt("pos");
		LogUtil.i("picturePos is " + picturePos);
		comeForm = this.getIntent().getExtras().getString("from");
		parentActivity = this.getIntent().getExtras().getString("parentActivity");
		recoMode = this.getIntent().getIntExtra("recomod", InfoMsg.RECO_MODE_QUICK_RECO);
		LogUtil.i("recomode is " + recoMode);

		if (recoMode == InfoMsg.RECO_MODE_QUICK_RECO)
		{
			lastImageSelectItem = -1;
			for (int i = 0; i < album.getBitList().size(); i++)
			{
				if (album.getBitList().get(i).isSelect())
				{
					lastImageSelectItem = i;
					break;
				}
			}
		}

	}


	@Override
	protected void onResume()
	{
		super.onResume();
	}
	
	@Override
	protected void onPause()
	{
		super.onPause();
	}

	private void initUI()
	{
		// TODO Auto-generated method stub
		lay_top = (RelativeLayout)this.findViewById(R.id.bc_preview_top);
		selected = (ImageView)this.findViewById(R.id.img_selected);
		lay_done = (RelativeLayout)this.findViewById(R.id.bc_preview_bottom);
		txt_done = (TextView)this.findViewById(R.id.bc_preview_confirm);
		txt_back = (TextView) this.findViewById(R.id.bc_preview_img_back);
		txt_selected_count = (TextView) this.findViewById(R.id.bc_photo_select_count);
		preview_img = (ImageView)this.findViewById(R.id.bc_preview_img);
		txt_del = (TextView) this.findViewById(R.id.tv_del);
		
	}

	private void addUIListener()
	{
		// TODO Auto-generated method stub
		selected.setOnClickListener(this);
		txt_back.setOnClickListener(this);
		txt_done.setOnClickListener(this);
		preview_img.setOnTouchListener(this);
		preview_img.setOnClickListener(this);
		txt_del.setOnClickListener(this);
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.bc_preview_img_back:
			Intent intent = new Intent();
			intent.putExtra("album", album);
			if(comeForm.equals(PREVIEW)) //预览按钮进来
			{
				intent.putExtra("back_to", PREVIEW);
			}
			else if(comeForm.equals(BIG)) //点击图片进来
			{
				intent.putExtra("back_to", BIG);
			}
			setResult(RESULT_OK,intent);
			PreviewPicActivity.this.finish();
			break;
		case R.id.bc_preview_confirm:
		{
			Intent retintent = new Intent();
			retintent.putExtra("album", album);
			if (chooseNum > 0)
			{
				retintent.putExtra("back_to", "finish");
			}
			else
			{
				if(comeForm.equals(PREVIEW)) //预览按钮进来
				{
					retintent.putExtra("back_to", PREVIEW);
				}
				else if(comeForm.equals(BIG)) //点击图片进来
				{
					retintent.putExtra("back_to", BIG);
				}
			}
			setResult(RESULT_OK, retintent);
			PreviewPicActivity.this.finish();
			/*
			//完成按钮
			ArrayList<String> pathList = new ArrayList<String>();
			for(int i=0;i<album.getBitList().size();i++){
				if(album.getBitList().get(i).isSelect()){
					pathList.add(album.getBitList().get(i).getPath());
				}
			}
			new ThreadBCardListPathProcess(CameraActivity.getCameraActivity(), pathList,parentActivity,PreviewPicActivity.this).start();//调用预分类和识别线程
			Intent intent1  = new Intent();
			intent1.setClass(mContext, BcardFolderActivity.class);
			mContext.startActivity(intent1);
			this.finish();
			*/
		}
			break;
		case R.id.bc_preview_img:
			
			break;
		case R.id.img_selected:
			/*
			if(album.getBitList().get(picturePos).isSelect())
			{
				album.getBitList().get(picturePos).setSelect(false);
				selected.setImageResource(R.mipmap.pic_unselected);
				chooseNum --;
			}
			else
			{
				chooseNum++;
				if(chooseNum<= MAX_PIC_NUM)
				{
					selected.setImageResource(R.mipmap.pic_selected);
					album.getBitList().get(picturePos).setSelect(true);
				}
				else
				{
					popHintDialog();//如果超过9张照片
				}
			}
			setSelectedCount();
			*/
			processImageSelect();
			break;
		/*
		case R.id.dialog_lay_know:
		case R.id.dialog_btn_know:
			mHintDialog.cancel();
			break;
			*/
			case R.id.tv_del:
				deletePicture();
				break;
		default:
			break;
		}
		
	}

	private void deletePicture()
	{
		int size = album.getBitList().size();
		if (size > 0)
		{
			String filepathToDel = album.getBitList().get(picturePos).getPath();
			Uri imageuri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
			ContentResolver mContentResolver = PreviewPicActivity.this.getContentResolver();
			String where = MediaStore.Images.Media.DATA + "='" + filepathToDel + "'";
			LogUtil.i("wherw is " + where);
			mContentResolver.delete(imageuri, where, null);

			Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
			File file = new File(filepathToDel);
			Uri uri = Uri.fromFile(file);
			intent.setData(uri);
			PreviewPicActivity.this.sendBroadcast(intent);

			album.getBitList().remove(picturePos);
			size--;

			if (picturePos > (size - 1))
			{
				picturePos = size - 1;
			}

			LogUtil.i("picturePos is " + picturePos);

			int picsize = album.getBitList().size();
			if (picsize > 0)
			{
				setPreviewImage();
			}
			else
			{
				Toast.makeText(PreviewPicActivity.this, "图片已经完全删除!", Toast.LENGTH_LONG).show();

				LogUtil.i("last pic has been del");
				Intent myintent = new Intent();
				myintent.putExtra("album", album);
				if(comeForm.equals(PREVIEW)) //预览按钮进来
				{
					myintent.putExtra("back_to", PREVIEW);
				}
				else if(comeForm.equals(BIG)) //点击图片进来
				{
					myintent.putExtra("back_to", BIG);
				}
				setResult(RESULT_OK,myintent);
				PreviewPicActivity.this.finish();
			}
		}
		else
		{
			LogUtil.i("all pic has been deleted!!!!!");
		}

	}

	private void processImageSelect()
	{
		if (recoMode == InfoMsg.RECO_MODE_QUICK_RECO)
		{
			LogUtil.i("quickImageSelect");
			if (-1 != lastImageSelectItem)
			{
				if (picturePos != lastImageSelectItem)
				{
					album.getBitList().get(lastImageSelectItem).setSelect(false);
					album.getBitList().get(picturePos).setSelect(true);
					lastImageSelectItem = picturePos;
					chooseNum = 1;
					selected.setImageResource(R.mipmap.pic_selected);
				}
				else
				{
					album.getBitList().get(lastImageSelectItem).setSelect(false);
					chooseNum = 0;
					lastImageSelectItem = -1;
					selected.setImageResource(R.mipmap.pic_unselected);
				}
			}
			else
			{
				album.getBitList().get(picturePos).setSelect(true);
				chooseNum = 1;
				lastImageSelectItem = picturePos;
				selected.setImageResource(R.mipmap.pic_selected);
			}

		}
		else
		{
			if(album.getBitList().get(picturePos).isSelect())
			{
				album.getBitList().get(picturePos).setSelect(false);
				selected.setImageResource(R.mipmap.pic_unselected);
				chooseNum --;
			}
			else
			{
				chooseNum++;
				if(chooseNum<= MAX_PIC_NUM)
				{
					selected.setImageResource(R.mipmap.pic_selected);
					album.getBitList().get(picturePos).setSelect(true);
				}
				else
				{
					popHintDialog();//如果超过9张照片
				}
			}

		}

		setSelectedCount();
	}

	private Dialog mHintDialog;
	public void popHintDialog()
	{
		/*
		LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.bc_choose_hint_dialog, null);

		mHintDialog = new Dialog(this,R.style.bc_dialog);
		
		Window win = mHintDialog.getWindow();
		android.view.WindowManager.LayoutParams lp = win.getAttributes();
	    lp.dimAmount = 0.8f;
	    lp.width = DisplayUtil.getScreenMetrics(this).x/2;
	    win.setAttributes(lp);
	    win.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
	    
	    mHintDialog.setContentView(view);
		TextView title = (TextView)view.findViewById(R.id.txt_view);
		title.setText("您最多只能选择9张照片");
		LinearLayout lay_know = (LinearLayout)view.findViewById(R.id.dialog_lay_know);
		Button btn_know = (Button)view.findViewById(R.id.dialog_btn_know);
		lay_know.setOnClickListener(this);
		btn_know.setOnClickListener(this);
		mHintDialog.setCanceledOnTouchOutside(false);//设置点击Dialog外部任意区域不能关闭Dialog
		mHintDialog.show();
		*/
	}

	@Override
	public boolean onTouch(View v, MotionEvent event)
	{
		ImageView view = (ImageView) v;
		switch (event.getAction() & MotionEvent.ACTION_MASK)
		{
		case MotionEvent.ACTION_DOWN:
			saveMatrix.set(matrix);
			//设置起点位置
			start.set(event.getX(), event.getY());
			Log.d("Touch","mode = DRAG");
			clickHandler.sendEmptyMessage(0);
			mode = DRAG;
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			oldDist = spacing(event);
			Log.d("Touch","oldDist =" +oldDist);
			if(oldDist> 10f)
			{
				saveMatrix.set(matrix);
				midPoint(mid,event);
				mode = ZOOM;
				Log.d("Touch","mode = ZOOM");
			}
			break;
		case MotionEvent.ACTION_UP:
			if(mode == DRAG)
			{
				float moveX = event.getX();
				float moveY = event.getY();
				if(moveX - start.x > 0 )//向右滑动
				{
					if(picturePos == 0)//第一张
					{
						Toast.makeText(mContext, "当前已经是第一张", Toast.LENGTH_SHORT).show();
					}
					else
					{
						String path = album.getBitList().get(--picturePos).getPath();
						BitmapFactory.Options opts = new BitmapFactory.Options();
						opts.inSampleSize = BitmapUtil.getImageScale(path);
						Bitmap b = BitmapFactory.decodeFile(path,opts);
						preview_img.setImageBitmap(b);
						preview_img.setScaleType(ScaleType.CENTER_INSIDE);
						matrix = new Matrix();
				        int width=p.x/2-b.getWidth()/2;
//				        int height=(p.y-192)/2-b.getHeight()/2;
				        int height=(p.y-96)/2-b.getHeight()/2;
				        matrix.postTranslate(width,height);
				        preview_img.setImageMatrix(matrix);
				        selected.setImageResource(album.getBitList().get(picturePos).isSelect()? R.mipmap.pic_selected:R.mipmap.pic_unselected);
					}
					
				}
				else if (moveX - start.x  < 0 )
				{
					if(picturePos == album.getBitList().size()-1)
					{
						isLastPicture = true;
					}
					else
					{
						isLastPicture = false;
					}
					if(isLastPicture)
					{
						Toast.makeText(mContext, "当前已经是最后一张", Toast.LENGTH_SHORT).show();
					}
					else
					{
						String path = album.getBitList().get(++picturePos).getPath();
						BitmapFactory.Options opts = new BitmapFactory.Options();
						opts.inSampleSize = BitmapUtil.getImageScale(path);
						Bitmap b = BitmapFactory.decodeFile(path,opts);
						preview_img.setImageBitmap(b);
						preview_img.setScaleType(ScaleType.CENTER_INSIDE);
						matrix = new Matrix();
				        int width=p.x/2-b.getWidth()/2;
//				        int height=(p.y-192)/2-b.getHeight()/2;
				        int height=(p.y-96)/2-b.getHeight()/2;
				        matrix.postTranslate(width,height);
				        preview_img.setImageMatrix(matrix);
				        selected.setImageResource(album.getBitList().get(picturePos).isSelect()? R.mipmap.pic_selected:R.mipmap.pic_unselected);
					}
	            }
			}
			
			clickHandler.sendEmptyMessage(1);
//			break;
		case MotionEvent.ACTION_POINTER_UP:
			mode = NONE;
			Log.d("Touch","mode = NONE");
			break;
		case MotionEvent.ACTION_MOVE:
			if(mode == DRAG){
//				matrix.set(saveMatrix);
//				matrix.postTranslate(event.getX()-start.x, event.getY()-start.y);
	
			}else if(mode == ZOOM){
				preview_img.setScaleType(ScaleType.MATRIX);
				float newDist = spacing(event);
				Log.d("Touch","newDist = " + newDist);
				if(newDist > 10f){
					matrix.set(saveMatrix);
					float scale = newDist / oldDist;
					float small = h > w? w*scale : h*scale;
					float max = h > w? h :w;
					if( small > 50){
						matrix.postScale(scale, scale, mid.x, mid.y);
						h = (int)( h*scale);
						w = (int)  (w *scale);
					}
				}
			}
			break;
		default:
			break;
		}
		view.setImageMatrix(matrix);
		return true;//indicate event was handled
	}

	private float spacing(MotionEvent event)
	{
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return (float)Math.sqrt(x * x + y *y );
	}

	private void midPoint(PointF point, MotionEvent event)
	{
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x/2, y/2);
	}

	private Handler clickHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			super.handleMessage(msg);
			if(msg.what == 0)
			{
				keyUpDown = true;
				keyUpDownListener();
			}
			else if(msg.what == 1)
			{
				keyUpDown = false;
				if(timer < 1)
				{
					//ShowBigPicActivity.this.finish();
				}
				else
				{
					if (timer == 1)//解决事件冲突问题，若是点击事件
					{
						if (clickCount % 2 == 0)
						{
							lay_top.setVisibility(View.GONE);
							lay_done.setVisibility(View.GONE);
						}
						else
						{
							lay_top.setVisibility(View.VISIBLE);
							lay_done.setVisibility(View.VISIBLE);
						}
						clickCount++;
						timer = 0;
					}
					else
					{
						timer = 0;
					}
				}
			}
		}
		
	};

	private  int  keyUpDownListener()
	{
		new Thread()
		{
			@Override
			public void run()
			{
				super.run();
				while(keyUpDown)
				{
					try
					{
						sleep(100);
						timer ++ ;
						LogUtil.i("timing:timer =" + timer);
					}
					catch (InterruptedException e)
					{
						// TODO: handle exception
						e.printStackTrace();
					}
				}
			}
		}.start();
		return timer;
	}
	
	
}
