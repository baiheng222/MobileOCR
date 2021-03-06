package com.hanvon.rc.bcard;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Environment;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
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
import com.hanvon.rc.activity.ChooseFileFormatActivity;
import com.hanvon.rc.activity.UploadFileActivity;
import com.hanvon.rc.bcard.adapter.BcardChoosePicAdapter;
import com.hanvon.rc.bcard.adapter.BcardChoosePicAdapter.onGridItemClickListener;
import com.hanvon.rc.bcard.bean.BcardChooseGridItem;
import com.hanvon.rc.db.FileInfo;
import com.hanvon.rc.md.camera.activity.CameraActivity;
import com.hanvon.rc.presentation.CropActivity;
import com.hanvon.rc.utils.FileUtil;
import com.hanvon.rc.utils.InfoMsg;
import com.hanvon.rc.utils.LogUtil;
import com.hanvon.rc.utils.ZipCompressorByAnt;
import com.hanvon.rc.wboard.bean.PhotoAlbum;
import com.hanvon.rc.wboard.bean.PhotoItem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ChooseMorePicturesActivity extends Activity implements OnClickListener
{
	private final static String TAG = "ChooseMorePictures";
	private final static int SCAN_OK = 1;

	private GridView gridView;
	private TextView txt_cancle,txt_confirm,txt_preview,txt_back,txt_selected_count;
	private TextView tv_recap;
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
	private String zipFileFullPaht = null;
	private String zipFileName = null;

	private PhotoAlbum previewPhotoAlum;
	private String comeFrom = "";
	private int recoMode;
	private int capMode;
	private String entry = null;
	private Context mContext;

	private ArrayList<String> picturesPath;

	private int lastImageSelectItem;

	private final static int MAX_PIC_NUM = 9;

	private final static int REQ_PREVIEW = 1;
	private static final int REQUEST_FILE_FORMAT = 4;

	private static final int CAPTURE_SINGLE = 1;
	private static final int CAPTURE_MULTI = 2;

	private ProgressDialog mProgress = null;
	boolean isCompressing = false;

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

		entry = this.getIntent().getStringExtra("entry");

		if (entry !=null)
		{
			Bundle bundle = this.getIntent().getBundleExtra("bundle");
			picturesPath = bundle.getStringArrayList("pictures");
			for (int i = 0; i < picturesPath.size(); i++)
			{
				LogUtil.i("picture " + i + " path is " + picturesPath.get(i));
			}
		}

		isCompressing = false;

		comeFrom = this.getIntent().getExtras().getString("parentActivity");
		recoMode = this.getIntent().getIntExtra("recomode", InfoMsg.RECO_MODE_QUICK_RECO);
		capMode = this.getIntent().getIntExtra("capmode", CAPTURE_SINGLE);
		LogUtil.i("capmode si " + capMode);
		LogUtil.i("recomode is " + recoMode);
		setContentView(R.layout.bc_choose_more_picture_main);
		
		mContext = this;

		if (picturesPath != null && picturesPath.size() > 0)
		{
			allAlbumssList = getLastPhotoAlbum(this);
		}
		else
		{
			allAlbumssList = getPhotoAlbum(this); //加载所有有图片的文件夹,用于初始化文件夹列表的显示
		}

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

		lastImageSelectItem = -1;

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
		tv_recap = (TextView) findViewById(R.id.tv_recap);

		layout_bottom.setBackgroundResource(R.color.darkgray);
		layout_bottom.setClickable(false);
		txt_preview.setClickable(false);
		txt_confirm.setClickable(false);

		if (capMode == CAPTURE_MULTI && (entry != null))
		{
			tv_recap.setVisibility(View.VISIBLE);
			txt_preview.setText("继续拍摄");
			txt_confirm.setText("确定");
			txt_preview.setClickable(true);
		}
		else
		{
			tv_recap.setVisibility(View.GONE);
		}


	}

	private void addUIListener()
	{
		layout_left.setOnClickListener(this);
		img_back.setOnClickListener(this);
		txt_back.setOnClickListener(this);
		txt_cancle.setOnClickListener(this);
		txt_preview.setOnClickListener(this);
		txt_confirm.setOnClickListener(this);
		tv_recap.setOnClickListener(this);
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
					LogUtil.i("!!!!! start preview , recomode is " + recoMode);
					intent.putExtra("recomod", recoMode);
					intent.putExtra("data", allAlbums);
					intent.putExtra("pos", position);
					intent.putExtra("from", "big");
					intent.putExtra("parentActivity", "ChooseMorePicturesActivity");
					startActivityForResult(intent,REQ_PREVIEW);

					break;

					case R.id.photo_select:
						if (recoMode == InfoMsg.RECO_MODE_QUICK_RECO)
						{
							quickImageSelect(v, position);
						}
						else
						{
							exactImageSelect(v, position);
						}
					break;

				default:
					break;
				}
			}
		});
	}

	private void quickImageSelect(View v, int position)
	{
		LogUtil.i("quickImageSelect");
		if (-1 != lastImageSelectItem)
		{
			if (position != lastImageSelectItem)
			{
				allAlbums.getBitList().get(lastImageSelectItem).setSelect(false);
				allAlbums.getBitList().get(position).setSelect(true);
				lastImageSelectItem = position;
				chooseNum = 1;
			}
			else
			{
				allAlbums.getBitList().get(lastImageSelectItem).setSelect(false);
				chooseNum = 0;
				lastImageSelectItem = -1;
			}
		}
		else
		{
			allAlbums.getBitList().get(position).setSelect(true);
			chooseNum = 1;
			lastImageSelectItem = position;
		}

		setSelectedCount();
		setBottomStatus();
		adapter.notifyDataSetChanged();

	}

	private void exactImageSelect(View v, int position)
	{
		LogUtil.i("exactImageSelect func");
		if( allAlbums.getBitList().get(position).isSelect())
		{
			allAlbums.getBitList().get(position).setSelect(false);
			chooseNum--;
		}
		else
		{

			if(chooseNum < MAX_PIC_NUM)
			{
				allAlbums.getBitList().get(position).setSelect(true);
				chooseNum++;
			}
			else
			{
				popHintDialog();//如果超过9张照片
			}
		}
		setSelectedCount();
		setBottomStatus();
		adapter.notifyDataSetChanged();
	}


	private void processBackBtn()
	{
		Intent intent = new Intent(this, CameraActivity.class);
		/*
		ArrayList<String> filelist = new ArrayList<String>();
		for (int i = 0; i < allAlbums.getBitList().size(); i++)
		{
			String path = allAlbums.getBitList().get(i).getPath();
			LogUtil.i("file " + i + " is " + path);
			filelist.add(path);
		}

		intent.putStringArrayListExtra("filelist", filelist);
		intent.putExtra("message", "backmsg");
		*/
		intent.putExtra("message", "recapture");
		startActivity(intent);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			processBackBtn();
			ChooseMorePicturesActivity.this.finish();
		}
		return false;
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.left:
		case R.id.bc_left_img_back:
		case R.id.bc_left_title:
			processBackBtn();
			ChooseMorePicturesActivity.this.finish();
			break;

		case R.id.bc_right_cancle:
			ChooseMorePicturesActivity.this.finish();
			break;

		case R.id.bc_bottom_preview:
		{
			if (entry == null)
			//if (capMode == CAPTURE_SINGLE)
			{
				previewPhotoAlum = new PhotoAlbum();
				for (int i = 0; i < allAlbums.getBitList().size(); i++)
				{
					if (allAlbums.getBitList().get(i).isSelect())
					{
						previewPhotoAlum.getBitList().add(allAlbums.getBitList().get(i));
					}
				}

				Intent intent = new Intent();
				intent.setClass(mContext, PreviewPicActivity.class);
				LogUtil.i("start prieview, recomode is " + recoMode);
				intent.putExtra("recomod", recoMode);
				intent.putExtra("data", previewPhotoAlum);
				intent.putExtra("pos", 0);
				intent.putExtra("from", "preview");
				intent.putExtra("parentActivity", comeFrom);
				startActivityForResult(intent, REQ_PREVIEW);
			}
			else
			{
				this.finish();
			}
		}
		break;

		case R.id.bc_bottom_confirm:
		{
			LogUtil.i("confirm button clicked");
			if (recoMode == InfoMsg.RECO_MODE_QUICK_RECO)
			{
				quickRecoConfirm();
			}
			else
			{
				//getResultFileFormat();
				exactRecoConfirm();
			}
		}
		break;

		case R.id.tv_recap:
			Intent myintent = new Intent(ChooseMorePicturesActivity.this, CameraActivity.class);
			myintent.putExtra("message", "recapture");
			startActivity(myintent);
			this.finish();
			break;

		default:
			break;
		}
		
	}

	private void quickRecoConfirm()
	{
		LogUtil.i("quickRecoConfirm");

		if (lastImageSelectItem == -1)
		{
			LogUtil.i("no item selected !!!");
			return;
		}

		paths = new ArrayList<String>();
		paths.add(allAlbums.getBitList().get(lastImageSelectItem).getPath());

		/*
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
		*/

		Intent intent = new Intent();
		intent.setClass(mContext,CropActivity.class);
		intent.putExtra("data", allAlbums);
		//intent.putExtra("pos", position);
		intent.putExtra("pos", lastImageSelectItem);
		intent.putExtra("from", "big");
		intent.putExtra("parentActivity", "ChooseMorePicturesActivity");
		startActivity(intent);
	}


	private void getResultFileFormat(long filesize)
	{
		Intent intent = new Intent(this, ChooseFileFormatActivity.class);
		intent.putExtra("resultType", InfoMsg.RECO_MODE_EXACT_RECO);
		intent.putExtra("filename", zipFileName);
		String size;
		long ksize = (filesize + 1023) / 1024;
		if (ksize > 1024)
		{
			size = String.valueOf((ksize + 1023)/1024) + "M";
		}
		else
		{
			size = String.valueOf(ksize) + "K";
		}
		intent.putExtra("filesize", size);
		startActivityForResult(intent, REQUEST_FILE_FORMAT);
	}

	public static void RecursionDeleteFile(File file)
	{
		LogUtil.i("in RecursionDeleteFile");
        if(file.isFile())
		{
            file.delete();
			LogUtil.i("delete file");
            return;
        }

        if(file.isDirectory())
		{
			LogUtil.i("delete directory");
            File[] childFile = file.listFiles();
            if(childFile == null || childFile.length == 0)
			{
                file.delete();

                return;
            }
            for(File f : childFile)
			{
                RecursionDeleteFile(f);
            }
            file.delete();
			LogUtil.i("delete files");
        }
    }


	private String getCurTimeString()
	{
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-ddHHmmss");
		Date currentTime = new Date();
		String dateString = formatter.format(currentTime);
		return dateString;
	}

	public static final int MSG_ZIP_COMPLETE = 0x11;

	public Handler zipHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			super.handleMessage(msg);
			mProgress.dismiss();
			isCompressing = false;
			Object obj = msg.obj;
			switch (msg.what)
			{
				case MSG_ZIP_COMPLETE:
					long length =  Long.parseLong(obj.toString());
					LogUtil.i("receive msg, file leng is " + length);
					getResultFileFormat(length);
				break;
			}
		}
	};

	public class CompressThread implements Runnable
	{
		public CompressThread()
		{

		}

		@Override
		public void run()
		{
			compressFils();
		}

	}

	private void compressFils()
	{
		LogUtil.i("call compressFiles func here !!!!!");
		ArrayList<String> paths = new ArrayList<String>();
		for(int i = 0;i<allAlbums.getBitList().size();i++)
		{
			if(allAlbums.getBitList().get(i).isSelect())
			{
				paths.add(allAlbums.getBitList().get(i).getPath());
			}
		}

		String sdCardPath = FileUtil.getSDCadrPath();

		String datestring = getCurTimeString();
		String zipDir = sdCardPath + "/MobileOCR/" + datestring;

		//File f1 = new File(sdCardPath + "/rctmp");
		File f1 = new File(zipDir);
		if (f1.exists())
		{
			//f1.delete();
			LogUtil.i("dir exists!!!!");
			RecursionDeleteFile(f1);
		}

		boolean status = f1.mkdirs();
		if (status)
		{
			LogUtil.i("mkdir " + zipDir + " success!!");
		}
		else
		{
			LogUtil.i("mkdirs fail!!!!");
		}

		for (int i = 0; i < paths.size(); i++)
		{
			LogUtil.i("path is " + paths.get(i));
			String filename = paths.get(i).substring(paths.get(i).lastIndexOf("/") + 1, paths.get(i).length());
			LogUtil.i("file name is " + filename);
			String prefix = "RCA000" + String.valueOf(i) + "_";
			LogUtil.i("prefix is " + prefix);
			//CopySdcardFile(paths.get(i), sdCardPath + "/rctmp/" + prefix + filename);
			CopySdcardFile(paths.get(i), zipDir + "/" + prefix + filename);
		}

		zipFileFullPaht = zipDir + ".zip";
		zipFileName = datestring + ".zip";
		LogUtil.i("zipFileFullPath is " + zipFileFullPaht);
		LogUtil.i("zipFileName is " + zipFileName);
		//ZipCompressorByAnt zip = new ZipCompressorByAnt("/sdcard/rctmp.zip");
		//zip.compressExe(sdCardPath + "/rctmp/");
		ZipCompressorByAnt zip = new ZipCompressorByAnt(zipFileFullPaht);
		zip.compressExe(zipDir + "/");
		File zipfile = zip.getCompressedFile();
		LogUtil.i("file size is " + zipfile.length());
		Message msg = Message.obtain();
		msg.what = MSG_ZIP_COMPLETE;
		msg.obj = zipfile.length();
		ChooseMorePicturesActivity.this.zipHandler.handleMessage(msg);
	}

	private void exactRecoConfirm()
	{
		LogUtil.i("exactRecoConfirm func");

		if (isCompressing)
		{
			LogUtil.i("compressing files, return !!!!");
			return;
		}

		isCompressing = true;

		mProgress = ProgressDialog.show(ChooseMorePicturesActivity.this, "", "正在处理...");
		CompressThread thread = new CompressThread();
		new Thread(thread).start();
		/*
		ArrayList<String> paths = new ArrayList<String>();
		for(int i = 0;i<allAlbums.getBitList().size();i++)
		{
			if(allAlbums.getBitList().get(i).isSelect())
			{
				paths.add(allAlbums.getBitList().get(i).getPath());
			}
		}



		String sdCardPath = FileUtil.getSDCadrPath();

		String datestring = getCurTimeString();
		String zipDir = sdCardPath + "/MobileOCR/" + datestring;

		//File f1 = new File(sdCardPath + "/rctmp");
		File f1 = new File(zipDir);
		if (f1.exists())
		{
			//f1.delete();
			LogUtil.i("dir exists!!!!");
			RecursionDeleteFile(f1);
		}

		boolean status = f1.mkdirs();
		if (status)
		{
			LogUtil.i("mkdir " + zipDir + " success!!");
		}
		else
		{
			LogUtil.i("mkdirs fail!!!!");
		}



		for (int i = 0; i < paths.size(); i++)
		{
			LogUtil.i("path is " + paths.get(i));
			String filename = paths.get(i).substring(paths.get(i).lastIndexOf("/") + 1, paths.get(i).length());
			LogUtil.i("file name is " + filename);
			String prefix = "RCA000" + String.valueOf(i) + "_";
			LogUtil.i("prefix is " + prefix);
			//CopySdcardFile(paths.get(i), sdCardPath + "/rctmp/" + prefix + filename);
			CopySdcardFile(paths.get(i), zipDir + "/" + prefix + filename);
		}

		zipFileFullPaht = zipDir + ".zip";
		zipFileName = datestring + ".zip";
		LogUtil.i("zipFileFullPath is " + zipFileFullPaht);
		LogUtil.i("zipFileName is " + zipFileName);
		//ZipCompressorByAnt zip = new ZipCompressorByAnt("/sdcard/rctmp.zip");
		//zip.compressExe(sdCardPath + "/rctmp/");
		ZipCompressorByAnt zip = new ZipCompressorByAnt(zipFileFullPaht);
		zip.compressExe(zipDir + "/");
		File zipfile = zip.getCompressedFile();
		LogUtil.i("file size is " + zipfile.length());
		*/

		//getResultFileFormat(zipfile.length());
	}

	private void startUpload(String resultFileFormat)
	{
		LogUtil.i("startUpload called!!!!!");
		Intent intent = new Intent(ChooseMorePicturesActivity.this, UploadFileActivity.class);
		intent.putExtra("fileamount", chooseNum);
		intent.putExtra("fileformat", "jpg");
		//intent.putExtra("fullpath", "/sdcard/rctmp.zip");
		//intent.putExtra("filename", "rctmp.zip");
		intent.putExtra("fullpath", zipFileFullPaht);
		intent.putExtra("filename", zipFileName);
		intent.putExtra("resultfiletype", resultFileFormat);
		startActivity(intent);
		this.finish();
	}


	private int copy(String fromFile, String toFile)
    {
        //要复制的文件目录
        File[] currentFiles;
        File root = new File(fromFile);
        //如同判断SD卡是否存在或者文件是否存在
        //如果不存在则 return出去
        if(!root.exists())
        {
            return -1;
        }
        //如果存在则获取当前目录下的全部文件 填充数组
        currentFiles = root.listFiles();

        //目标目录
        File targetDir = new File(toFile);
        //创建目录
        if(!targetDir.exists())
        {
            targetDir.mkdirs();
        }
        //遍历要复制该目录下的全部文件
        for(int i= 0;i<currentFiles.length;i++)
        {
            if(currentFiles[i].isDirectory())//如果当前项为子目录 进行递归
            {
                copy(currentFiles[i].getPath() + "/", toFile + currentFiles[i].getName() + "/");

            }else//如果当前项为文件则进行文件拷贝
            {
                CopySdcardFile(currentFiles[i].getPath(), toFile + currentFiles[i].getName());
            }
        }
        return 0;
    }


    //文件拷贝
    //要复制的目录下的所有非子目录(文件夹)文件拷贝
    private int CopySdcardFile(String fromFile, String toFile)
    {
		LogUtil.i("fromfile is " + fromFile);
		LogUtil.i("toFile is " + toFile);
        try
        {
            InputStream fosfrom = new FileInputStream(fromFile);
            OutputStream fosto = new FileOutputStream(toFile);
            byte bt[] = new byte[1024];
            int c;
            while ((c = fosfrom.read(bt)) > 0)
            {
                fosto.write(bt, 0, c);
            }
            fosfrom.close();
            fosto.close();
            return 0;

        } catch (Exception ex)
        {
            return -1;
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
			if (capMode == CAPTURE_MULTI)
			{
				txt_preview.setClickable(true);
			}
			else
			{
				txt_preview.setClickable(false);
			}
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

	
	private void getBackAlbum(PhotoAlbum backAlbum)
	{
		int backSize = backAlbum.getBitList().size();
		int allSize = allAlbums.getBitList().size();
		for(int i=0; i<backSize; i++)
		{
			String path1 = backAlbum.getBitList().get(i).getPath();
			for(int j = 0;j<allSize; j++)
			{
				String path2 = allAlbums.getBitList().get(j).getPath();
				if(path1.equals(path2))
				{
					allAlbums.getBitList().get(j).setSelect(backAlbum.getBitList().get(i).isSelect());
				}
			}
		}
		
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if(resultCode ==RESULT_OK)
		{
			switch (requestCode)
			{
				case REQ_PREVIEW:
					if(data != null)
					{
						String back = data.getStringExtra("back_to");
						PhotoAlbum backAlbum = (PhotoAlbum) data.getSerializableExtra("album");

						if ((backAlbum != null) && (backAlbum.getBitList().size() == 0))
						{
							LogUtil.i("all pic has been del, finish activity");
							Intent myintent = new Intent(ChooseMorePicturesActivity.this, CameraActivity.class);
							myintent.putExtra("message", "userdelall");
							startActivity(myintent);
							this.finish();
							return;
						}

						if(back.equals("big"))
						{
							LogUtil.i("on activity result return big");
							allAlbums = backAlbum;
							chooseNum = 0;
							adapter = new BcardChoosePicAdapter(mContext, allAlbums, null);
							gridView.setAdapter(adapter);
						}
						else if(back.equals("preview"))
						{
							LogUtil.i("on activity result return preview");
							chooseNum = 0;
							getBackAlbum(backAlbum);
						}
						else if (back.equals("finish"))
						{
							LogUtil.i("on activity result return finish");
							allAlbums = backAlbum;
							chooseNum = 0;
							adapter = new BcardChoosePicAdapter(mContext, allAlbums, null);
							gridView.setAdapter(adapter);
							if (recoMode == InfoMsg.RECO_MODE_QUICK_RECO)
							{
								lastImageSelectItem = -1;
								for (int i = 0; i < allAlbums.getBitList().size(); i++)
								{
									if (allAlbums.getBitList().get(i).isSelect())
									{
										lastImageSelectItem = i;
										break;
									}
								}
								quickRecoConfirm();
							}
							else
							{
								//getResultFileFormat();
								exactRecoConfirm();
							}
						}
					}
					for(int i = 0;i<allAlbums.getBitList().size();i++)
					{
						if(allAlbums.getBitList().get(i).isSelect())
						{
							chooseNum++;
						}
						setSelectedCount();
						setBottomStatus();
					}
					adapter.notifyDataSetChanged();
				break;

				case REQUEST_FILE_FORMAT:
					String filename = data.getStringExtra("filename");
					String suffix = data.getStringExtra("suffix");
					LogUtil.i("get file name is " + filename);
					LogUtil.i("suffix is " + suffix);
					//exactRecoConfirm(suffix);
					startUpload(suffix);
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
			//LogUtil.i( "id==="+id+", ==dir_id=="+dir_id+", ==dir=="+dir+", ==path="+path);

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


	public List<PhotoAlbum> getLastPhotoAlbum(Context context)
	{
		List<PhotoAlbum> tempaibumList = new ArrayList<PhotoAlbum>();
		Cursor cursor = MediaStore.Images.Media.query(context.getContentResolver(), MediaStore.Images.Media.EXTERNAL_CONTENT_URI, STORE_IMAGES, MediaStore.Images.Media.MIME_TYPE + "=?",
				new String[] { "image/jpeg"}, MediaStore.Images.Media.DATE_TAKEN + " DESC");	//按日期降序排列

		Map<String, PhotoAlbum> countMap = new HashMap<String, PhotoAlbum>();
		PhotoAlbum pa = null; //每个相册
		int size = 0;
		while (cursor.moveToNext())
		{

			if (size >= picturesPath.size())
			{
				break;
			}

			String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
			String id = cursor.getString(3);
			String dir_id = cursor.getString(4);
			String dir = cursor.getString(5);
			String pic_date = cursor.getString(6);
			LogUtil.i( "id==="+id+", ==dir_id=="+dir_id+", ==dir=="+dir+", ==path="+path);

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

			size++;
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
		for(int i = 0; i< allAlbums.getBitList().size();i++)
		{
			allAlbums.getBitList().get(i).setVisible(true);//设置复选按钮可见
			adapter.notifyDataSetChanged();
		}	
	}


}
