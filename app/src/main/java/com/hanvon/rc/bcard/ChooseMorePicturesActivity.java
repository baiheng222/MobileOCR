package com.hanvon.rc.bcard;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hanvon.rc.R;
import com.hanvon.rc.bcard.adapter.BcardChoosePicAdapter;
import com.hanvon.rc.bcard.adapter.BcardChoosePicAdapter.onGridItemClickListener;
import com.hanvon.rc.bcard.bean.BcardChooseGridItem;
import com.hanvon.rc.db.FileInfo;
import com.hanvon.rc.presentation.CropActivity;
import com.hanvon.rc.utils.FileUtil;
import com.hanvon.rc.wboard.bean.PhotoAlbum;
import com.hanvon.rc.wboard.bean.PhotoItem;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import com.hanvon.md.camera.activity.ThreadBCardListPathProcess;

public class ChooseMorePicturesActivity extends Activity implements OnClickListener
{
	private final static String TAG = "ChooseMorePictures";
	private final static int SCAN_OK = 1;

	private GridView gridView;
	private TextView txt_cancle,txt_confirm,txt_preview,txt_back,txt_selected_count;
	private ImageView img_back;
	private LinearLayout layout_left;
	private RelativeLayout layout_bottom;
	private BcardChoosePicAdapter adapter;
	private List<PhotoAlbum> allAlbumssList = null;
	private PhotoAlbum allAlbums = null;	//存放全部图片的信息
	private ArrayList<String> paths = null;
	private int photoSelectedPos = -1; //gridview中选中的照片位置
	private int count = 0;
	private int chooseNum = 0;//选中图片的个数
	private String selectPicPath;
	private Dialog mHintDialog;
	private final static int REQ_PREVIEW = 1;
	private PhotoAlbum previewPhotoAlum;
	private String comeFrom = "";
	private Context mContext;

	private final static int MAX_PIC_NUM = 9;

	// 设置获取图片的字段信息
	private static final String[] STORE_IMAGES = {
	 		MediaStore.Images.Media.DISPLAY_NAME, // 显示的名称
	 		MediaStore.Images.Media.DATA,
	 		MediaStore.Images.Media.LONGITUDE, // 经度
	 		MediaStore.Images.Media._ID, // id
	 		MediaStore.Images.Media.BUCKET_ID, // dir id 目录
	 		MediaStore.Images.Media.BUCKET_DISPLAY_NAME, // dir name 目录名字
	 		MediaStore.Images.Media.DATE_TAKEN //图片的时间
	 			   
	 	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); //remove title bar

		if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
		{
			Toast.makeText(this, "暂无外部存储", Toast.LENGTH_SHORT).show();
			return;
		}

		comeFrom = this.getIntent().getExtras().getString("parentActivity");
		setContentView(R.layout.bc_choose_more_picture_main);
		
		mContext = this;
	
		allAlbumssList = getPhotoAlbum(this); //加载所有有图片的文件夹,用于初始化文件夹列表的显示
		if(allAlbumssList != null)
		{
			allAlbums = getWholePhotoData(allAlbumssList);
//			previewPhotoAlum = allAlbums;
//			Collections.sort(allAlbums.getBitList(),new SortByDate());//按日期排序
//			Collections.sort(allAlbums.getBitList(),new SortByDate());
		}
		else
		{
			allAlbums = new PhotoAlbum();
			allAlbums.setName("所有");
			allAlbums.setCount("0");
		}
		initUI();

		addUIListener();

		//初始化图片适配器
		adapter = new BcardChoosePicAdapter(this,allAlbums,null);
		gridView.setAdapter(adapter);

		setCheckVisible();
		setBottomStatus();

		initGridItemClick();
	}
	
	private void initUI()
	{
		gridView = (GridView) this.findViewById(R.id.main_grid);
		layout_left = (LinearLayout)this.findViewById(R.id.left);
		img_back = (ImageView)this.findViewById(R.id.bc_left_img_back);
		txt_back = (TextView)this.findViewById(R.id.bc_left_title);
		txt_cancle = (TextView)this.findViewById(R.id.bc_right_cancle);
		txt_preview = (TextView)this.findViewById(R.id.bc_bottom_preview);
		txt_confirm = (TextView)this.findViewById(R.id.bc_bottom_confirm);
		txt_selected_count = (TextView)this.findViewById(R.id.bc_photo_select_count);
		layout_bottom = (RelativeLayout)findViewById(R.id.choose_bottom);
		
		layout_bottom.setBackgroundResource(R.color.darkgray);
		layout_bottom.setClickable(false);
		txt_preview.setClickable(false);
		txt_confirm.setClickable(false);
	}

	private void addUIListener()
	{
		layout_left.setOnClickListener(this);
		img_back.setOnClickListener(this);
		txt_back.setOnClickListener(this);
		txt_cancle.setOnClickListener(this);
		txt_preview.setOnClickListener(this);
		txt_confirm.setOnClickListener(this);
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		initGridItemClick();
	}
	
	@Override
	protected void onPause()
	{
		super.onPause();
	}

	public void initGridItemClick()
	{
		adapter.setOnGridItemClickListener(new onGridItemClickListener()
		{
			@Override
			public void onGridItemClick(View v, int position)
			{
				switch (v.getId())
				{
					case R.id.photo_img_view:
					Intent intent = new Intent();
					intent.setClass(mContext,PreviewPicActivity.class);
					
					intent.putExtra("data", allAlbums);
					intent.putExtra("pos", position);
					intent.putExtra("from", "big");
					intent.putExtra("parentActivity", "ChooseMorePicturesActivity");
					startActivityForResult(intent,REQ_PREVIEW);

					/*
					Log.d(TAG, " !!!!!!! onGridItemClick !!!!!!!!!!");
					Intent intent = new Intent();
					intent.setClass(mContext,CropActivity.class);
					intent.putExtra("data", allAlbums);
					intent.putExtra("pos", position);
					intent.putExtra("from", "big");
					intent.putExtra("parentActivity", comeFrom);
					startActivity(intent);
					*/
					break;

					case R.id.photo_select:
					if( allAlbums.getBitList().get(position).isSelect())
					{
						allAlbums.getBitList().get(position).setSelect(false);
						chooseNum--;
					}
					else
					{
						chooseNum++;
						if(chooseNum<= MAX_PIC_NUM)
						{
							allAlbums.getBitList().get(position).setSelect(true);
						}
						else
						{
							popHintDialog();//如果超过9张照片
						}
					}
					setSelectedCount();
					setBottomStatus();
					adapter.notifyDataSetChanged();
					break;

				default:
					break;
				}
			}
		});
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.left:
		case R.id.bc_left_img_back:
		case R.id.bc_left_title:
			ChooseMorePicturesActivity.this.finish();
			break;

		case R.id.bc_right_cancle:
			ChooseMorePicturesActivity.this.finish();
			break;

		case R.id.bc_bottom_preview:
		{
			
			previewPhotoAlum = new PhotoAlbum();
			for(int i = 0;i<allAlbums.getBitList().size();i++){
				if(allAlbums.getBitList().get(i).isSelect()){
					previewPhotoAlum.getBitList().add(allAlbums.getBitList().get(i));
				}
			}

			Intent intent = new Intent();
			intent.setClass(mContext,PreviewPicActivity.class);
			intent.putExtra("data", previewPhotoAlum);
			intent.putExtra("pos", 0);
			intent.putExtra("from", "preview");
			intent.putExtra("parentActivity",comeFrom);
			startActivityForResult(intent, REQ_PREVIEW);
		}
		break;

		case R.id.bc_bottom_confirm:
		{
			Log.d(TAG, " !!!!!!! onGridItemClick !!!!!!!!!!");
			int position = -1;
			paths = new ArrayList<String>();
			for(int i = 0;i<allAlbums.getBitList().size();i++)
			{
				if(allAlbums.getBitList().get(i).isSelect())
				{
					paths.add(allAlbums.getBitList().get(i).getPath());
					position = i;
					break;
				}
			}

			if ((position < 0) || (position >=allAlbums.getBitList().size()) )
			{
				return;
			}


			Intent intent = new Intent();
			intent.setClass(mContext,CropActivity.class);
			intent.putExtra("data", allAlbums);
			intent.putExtra("pos", position);
			intent.putExtra("from", "big");
			intent.putExtra("parentActivity", "ChooseMorePicturesActivity");
			startActivity(intent);
			/*
			paths = new ArrayList<String>();
			for(int i = 0;i<allAlbums.getBitList().size();i++){
				if(allAlbums.getBitList().get(i).isSelect()){
					paths.add(allAlbums.getBitList().get(i).getPath());
				}
			}
			new ThreadBCardListPathProcess(CameraActivity.getCameraActivity(), paths,comeFrom,ChooseMorePicturesActivity.this).start();
			Intent intent  = new Intent();
			intent.setClass(mContext, BcardFolderActivity.class);
//			intent.putStringArrayListExtra("paths", paths);
			mContext.startActivity(intent);
			this.finish();
			*/
		}
			//调用分类和识别线程
			break;

		/*
		case R.id.dialog_lay_know:
		case R.id.dialog_btn_know:
			mHintDialog.cancel();
			break;
		*/
		default:
			break;
		}
		
	}
	
	private OnItemClickListener gvItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
								long id) {

			final int pos = position;
			final BcardChooseGridItem itemView = (BcardChooseGridItem) view;
			final ImageView img = itemView.getImageView();
			final ImageView mSelect = itemView.getmSelect();
			img.setOnClickListener(new OnClickListener()
			{

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					/*
					Intent intent = new Intent();
					intent.setClass(mContext,PreviewPicActivity.class);
					
					intent.putExtra("data", allAlbums);
					intent.putExtra("pos", pos);
					intent.putExtra("from", "big");
					intent.putExtra("parentActivity", comeFrom);
					startActivityForResult(intent,REQ_PREVIEW);
					*/

					Log.d(TAG, " !!!!!!! onGridItemClick !!!!!!!!!!");
					Intent intent = new Intent();
					intent.setClass(mContext,CropActivity.class);
					intent.putExtra("data", allAlbums);
					intent.putExtra("pos", pos);
					intent.putExtra("from", "big");
					intent.putExtra("parentActivity", "ChooseMorePicturesActivity");
					startActivity(intent);
				}
			});

			mSelect.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if( allAlbums.getBitList().get(pos).isSelect()){				
						allAlbums.getBitList().get(pos).setSelect(false);
						chooseNum--;
					}else{
						chooseNum++;
						if(chooseNum <= MAX_PIC_NUM){
							allAlbums.getBitList().get(pos).setSelect(true);
						}else{
							popHintDialog();//如果超过9张照片
							
						}
					}
					setSelectedCount();
					setBottomStatus();
					adapter.notifyDataSetChanged();
				}

			});

		}
	};

	private void setBottomStatus()
	{
		if(chooseNum == 0)
		{

			layout_bottom.setBackgroundResource(R.color.darkgray);
			layout_bottom.setClickable(false);
			txt_preview.setClickable(false);
			txt_confirm.setClickable(false);
		}
		else
		{
			layout_bottom.setBackgroundResource(R.color.wboard_folder_manage_color);
			layout_bottom.setClickable(true);
			txt_preview.setClickable(true);
			txt_confirm.setClickable(true);
		}
	}
	private void setSelectedCount()
	{
		if(chooseNum != 0)
		{
			txt_selected_count.setVisibility(View.VISIBLE);
			if(chooseNum>MAX_PIC_NUM)
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

	
	private void getBackAlbum(PhotoAlbum backAlbum){
		int backSize = backAlbum.getBitList().size();
		int allSize = allAlbums.getBitList().size();
		for(int i=0; i<backSize; i++){
			String path1 = backAlbum.getBitList().get(i).getPath();
			for(int j = 0;j<allSize; j++){
				String path2 = allAlbums.getBitList().get(j).getPath();
				if(path1.equals(path2)){
					allAlbums.getBitList().get(j).setSelect(backAlbum.getBitList().get(i).isSelect());
				}
			}
		}
		
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if(resultCode ==RESULT_OK){
			switch (requestCode) {
			case REQ_PREVIEW:
				
				if(data != null){
					String back = data.getStringExtra("back_to");
					PhotoAlbum backAlbum = (PhotoAlbum) data.getSerializableExtra("album");
					if(back.equals("big")){
						allAlbums = backAlbum;
						chooseNum = 0;
						adapter = new BcardChoosePicAdapter(mContext, allAlbums, null);
						gridView.setAdapter(adapter);
					}else if(back.equals("preview")){
						chooseNum = 0;
						getBackAlbum(backAlbum);
						}
					}
					for(int i = 0;i<allAlbums.getBitList().size();i++){
						if(allAlbums.getBitList().get(i).isSelect()){
							chooseNum++;
						}
					setSelectedCount();
					setBottomStatus();
				}
				
				break;

			default:
				break;
			}
		}
		
		super.onActivityResult(requestCode, resultCode, data);
	}
	/**
	 * 方法描述：按文件夹获取图片信息(默认所有的图片除了MyGallery下)
	 * 
	 * @author: why
	 * @time: 2013-10-18 下午1:35:24
	 */
	public List<PhotoAlbum> getPhotoAlbum(Context context) //加载该应用的所有文件夹
	{
		List<PhotoAlbum> tempaibumList = new ArrayList<PhotoAlbum>();
		Cursor cursor = MediaStore.Images.Media.query(context.getContentResolver(), MediaStore.Images.Media.EXTERNAL_CONTENT_URI, STORE_IMAGES, MediaStore.Images.Media.MIME_TYPE + "=?",
				new String[] { "image/jpeg"}, MediaStore.Images.Media.DATE_TAKEN + " DESC");	//按日期降序排列
		
		Map<String, PhotoAlbum> countMap = new HashMap<String, PhotoAlbum>();
		PhotoAlbum pa = null; //每个相册
		while (cursor.moveToNext())
		{
			String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
			String id = cursor.getString(3);
			String dir_id = cursor.getString(4);
			String dir = cursor.getString(5);
			String pic_date = cursor.getString(6);
			Log.i(TAG, "id==="+id+", ==dir_id=="+dir_id+", ==dir=="+dir+", ==path="+path);

            if (!FileUtil.exit(path))
            {
                Log.i(TAG, "no file " + path + "exists");
                continue;
            }

			//if(!path.contains("universcan/MyGallery/"))
            //if(path.contains(CameraActivity.FILE_SAVE_PATH + CameraActivity.FILE_SAVE_DIR_NAME))
			{
				if (!countMap.containsKey(dir_id))
				{
                    //Log.i(TAG, "!!!!!! no dir_id");
					pa = new PhotoAlbum();
					pa.setDir_id(dir_id);
					pa.setName(dir);
					pa.setBitmap(Integer.parseInt(id));
					pa.setCount("1");
					pa.getBitList().add(new PhotoItem(Integer.valueOf(id),path,pic_date));
					countMap.put(dir_id, pa);
					pa.setPath(path);
				}
				else
				{
                    //Log.i(TAG, "!!!!!!! exist dir_id");
					pa = countMap.get(dir_id);
					pa.setDir_id(dir_id);
					pa.setCount(String.valueOf(Integer.parseInt(pa.getCount()) + 1));
					pa.getBitList().add(new PhotoItem(Integer.valueOf(id),path,pic_date));
					pa.setPath(path);
				}
			}

		}
		cursor.close();
		Iterable<String> it = countMap.keySet();
		for (String key : it)
		{
			tempaibumList.add(countMap.get(key));
		}
		return tempaibumList;
	}
	
	private PhotoAlbum getWholePhotoData(List<PhotoAlbum> album)
	{
		PhotoAlbum pa = new PhotoAlbum();
		pa.setName("所有");
		pa.setCount("0");
		int count = 0 ;
		for(int i = 0; i<album.size();i++)
		{
			for(int j = 0;j< album.get(i).getBitList().size();j++)
			{
				count++;
				pa.getBitList().add(album.get(i).getBitList().get(j));
				pa.setBitmap(album.get(i).getBitList().get(j).getPhotoID());
				pa.setPath(album.get(i).getBitList().get(j).getPath());
				pa.setCount(String.valueOf(count));
			}
		}
		return pa;		
	}

	class SortByDate implements Comparator<PhotoItem> //按日期排序
	{

		@Override
		public int compare(PhotoItem lhs, PhotoItem rhs) {
			// TODO Auto-generated method stub
			return lhs.getPhotoDate().compareTo(rhs.getPhotoDate());
		}
		
	}
	
	private void setCheckVisible()
	{
		for(int i = 0; i< allAlbums.getBitList().size();i++){
			
			allAlbums.getBitList().get(i).setVisible(true);//设置复选按钮可见
			adapter.notifyDataSetChanged();
		}	
	}
}
