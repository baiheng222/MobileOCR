package com.hanvon.rc.presentation;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.hanvon.rc.R;
import com.hanvon.rc.activity.ChooseFileFormatActivity;
import com.hanvon.rc.activity.UploadFileActivity;
import com.hanvon.rc.application.HanvonApplication;
import com.hanvon.rc.bcard.ChooseMorePicturesActivity;
import com.hanvon.rc.md.camera.UploadImage;
import com.hanvon.rc.md.camera.activity.RecFailActivity;
import com.hanvon.rc.md.camera.activity.RecResultActivity;
import com.hanvon.rc.orders.OrderDetail;
import com.hanvon.rc.orders.OrderEvalPrices;
import com.hanvon.rc.utils.Base64Utils;
import com.hanvon.rc.utils.BitmapUtil;
import com.hanvon.rc.utils.ConnectionDetector;
import com.hanvon.rc.utils.DisplayUtil;
import com.hanvon.rc.utils.FileUtil;
import com.hanvon.rc.utils.InfoMsg;
import com.hanvon.rc.utils.LogUtil;
import com.hanvon.rc.utils.ZipCompressorByAnt;
import com.hanvon.rc.wboard.bean.PhotoAlbum;
import com.hanwang.preprocessjava.preprocessdll;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

//import com.hanvon.HWCloudManager;
//import com.hanvon.common.HWLangDict;

public class CropActivity extends Activity
{
	private static final String TAG = "CropActivity";
	private Crop_Canvas canvas = null;  
	private ImageView backImage;
    private Bitmap backBitmap;
    private Bitmap cropBitmap;
    private Button mBtnReCapture,ensure,rotate;
    private int frameWidth,frameHeight;
    private int screen_width,screen_height,canvas_width,canvas_height,btn_width,btn_height;

    private static int MSG_PROCESS_IMAGE= 1;
    //private HWCloudManager hwCloudManagerText; //文本识别
    private ProgressDialog mProgress = null;
    private volatile boolean flag = false; //识别线程中断标识
    int w,h;
    private float[]  points;
    private static String EXTENDPATH = Environment.getExternalStorageDirectory() +"/";
    private Matrix matrix = new Matrix();
    private String pathBefore,pathMid,pathAfter,path;
    private float scale,density;
    private int padding = 10;
	private boolean isRecognizing = false;

	private String oriName;
	private String fid;

	private int recoMode;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pt_crop);

		Point p = DisplayUtil.getScreenMetrics(this);
		screen_height = p.y;
		screen_width = p.x;
		density = this.getResources().getDisplayMetrics().density;
		LogUtil.i("density==========="+density);

		recoMode = this.getIntent().getIntExtra("recomode", InfoMsg.RECO_MODE_QUICK_RECO);
		LogUtil.i("recomode is " + recoMode);

		String parent = this.getIntent().getStringExtra("parentActivity");
		Log.i(TAG, "parent is " + parent);
		if (parent.equals("CameraActivity"))
		{
			path = this.getIntent().getStringExtra("path");
			Log.i(TAG, "!!!! from CameraActivity, path is " + path);
		}
		else if (parent.equals("ChooseMorePicturesActivity"))
		{
			PhotoAlbum album = (PhotoAlbum) this.getIntent().getSerializableExtra("data");
			int picturePos = this.getIntent().getExtras().getInt("pos");
			path = album.getBitList().get(picturePos).getPath();
			Log.i(TAG, "!!!! from ChooseMorePicturesActivity, path is " + path);
		}
		else
		{
			Log.i(TAG, "error !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		}


		Log.d(TAG, "!!!!! path is " + path);

		BitmapFactory.Options opt =  new  BitmapFactory.Options();
		opt.inSampleSize = BitmapUtil.getImageScale(path);
		backBitmap = BitmapFactory.decodeFile(path,opt);
		canvas = (Crop_Canvas) findViewById(R.id.myCanvas);
		canvas.setImageBitmap(backBitmap);
		mBtnReCapture = (Button) findViewById(R.id.pt_crop_recapture);
		//cancel = (Button) findViewById(R.id.pt_crop_cancel);
		//rotate = (Button) findViewById(R.id.pt_crop_rotate);
		ensure = (Button) findViewById(R.id.pt_crop_ok);
		backImage = (ImageView) findViewById(R.id.iv_back);
        init();
		isRecognizing = false;
		/*
		new Thread(new Runnable()
		{
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Message msg =new Message();
				msg.what = MSG_PROCESS_IMAGE;
				msg.obj = "process";
				handler.sendMessage(msg);
			}
		}).start();
		*/

		/*points = preprocessdll.Preprocess_FindSide(path,  EXTENDPATH+"MyTemp/pfind1.jpg");
		if (Build.VERSION.SDK_INT < 19) {//API 19 以前的{
			sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File( EXTENDPATH+"MyTemp/pfind1.jpg"))));
		}else{
			MediaScannerConnection.scanFile(CropActivity.this,
					new String[]{Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath() +"MyTemp/pfind1.jpg"}, null, null);
		}
		System.out.println(points.length);*/

		/* //fjm add commet
		hwCloudManagerText = new HWCloudManager(this, "b8ad3ae4-1393-4494-81ea-2851b481ac9a",
	   			"74e51a88-41ec-413e-b162-bd031fe0407e");
	   	*/
        mBtnReCapture.setOnClickListener(new MyListener());
        ensure.setOnClickListener(new MyListener());
        //rotate.setOnClickListener(new MyListener());
		backImage.setOnClickListener(new MyListener());

	}

	public class MyListener implements OnClickListener
	{

		@Override
		public void onClick(View v)
		{
			switch (v.getId())
			{
			case R.id.pt_crop_recapture:
				CropActivity.this.finish();
				break;
			case R.id.pt_crop_rotate:
				/*
				preprocessdll.Preprocess_Rotate(pathAfter, pathAfter, 90, false);//旋转后保存到原路径
				backBitmap = BitmapFactory.decodeFile(pathAfter);
				canvas.setRotateFlag(true);
				init();
				*/
				break;
			case R.id.pt_crop_ok:

				if (isRecognizing)
				{
					Log.i(TAG, "recogize thread already running");
					return;
				}
				else
				{
					isRecognizing = true;
				}

				File tmp = new File(Environment.getExternalStorageDirectory() + "/MyTemp");
                tmp.mkdirs();
				String tmpFileName = Environment.getExternalStorageDirectory() + "/MyTemp/" + "cropAfter" + ".png";
                if (FileUtil.exit(tmpFileName))
				{
					Log.i(TAG, "file " + tmpFileName + " exists, delete it");
					FileUtil.deleteSDFile(tmpFileName);
				}
				//File f = new File(Environment.getExternalStorageDirectory() + "/MyTemp/" + "cropAfter" + ".png");
                File f = new File(tmpFileName);
				try
				{
                    f.createNewFile();
                }
				catch (IOException e1)
				{
                    e1.printStackTrace();
                }

                FileOutputStream fOut = null;
                try
				{
                        fOut = new FileOutputStream(f);
                }
				catch (FileNotFoundException e)
				{
                        e.printStackTrace();
                }

                canvas.getSubsetBitmap(screen_width,screen_height).compress(Bitmap.CompressFormat.PNG, 60, fOut);
                try
				{
                        fOut.flush();
                }
				catch (IOException e)
				{
                        e.printStackTrace();
                }

                try
				{
                        fOut.close();
                }
				catch (IOException e)
				{
                        e.printStackTrace();
                }

                BitmapFactory.Options opts1 = new BitmapFactory.Options();
                opts1.inSampleSize = BitmapUtil.getImageScale(f.getAbsolutePath());
                System.out.println(f.getAbsolutePath() +" file path");
                cropBitmap = BitmapFactory.decodeFile(f.getAbsolutePath(), opts1);
				Log.d(TAG, "!!!!! saved file path is "  + f.getAbsolutePath());
				FileUtil.saveBitmap(cropBitmap);

				if (recoMode == InfoMsg.RECO_MODE_QUICK_RECO)
				{
					if (connInNet()) //如果连网
					{
						mProgress = ProgressDialog.show(CropActivity.this, "", "正在识别......");
						oriName = path.substring(path.lastIndexOf("/") + 1, path.length());
						RecoThread recoThread = new RecoThread(oriName, f.getAbsolutePath(), "1");
						new Thread(recoThread).start();
					}
				}
				else
				{
					exactRecoSingle(f.getAbsolutePath());
				}
				break;

				case R.id.iv_back:
					CropActivity.this.finish();
					break;

			default:
				break;
			}

		}

	}

	private Handler handler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if(msg.what == MSG_PROCESS_IMAGE){
				pathMid = FileUtil.saveBitmap(backBitmap);	//将图片保存到临时文件夹
				pathAfter = imageProcessSeq(pathMid);	//对图片处理
//				pathAfter = imageProcessSeq(path);
				System.out.println(pathAfter + "----------thread");
				BitmapFactory.Options opts = new BitmapFactory.Options();
				opts.inSampleSize = BitmapUtil.getImageScale(pathAfter);
				//backBitmap = BitmapFactory.decodeFile(pathAfter,opts);
//				canvas.setBitmap(backBitmap);
				canvas.setImageBitmap(backBitmap);
//				FileUtil.deleteSDFile("/storage/sdcard0/MyTemp");//处理完后删除临时文件中的图片
			}
		}
		
	};

	private void init()
	{
		float scaleX = (float) (((screen_width- padding*2*density) * 1.0)/(backBitmap.getWidth() * 1.0) );
//	 	float scaleY = (float) (((screen_height-90*density-10*density)* 1.0)/(backBitmap.getHeight() * 1.0) ); 
//	 	scale = scaleX < scaleY ? scaleX:scaleY;
		scale = scaleX;
	 	LogUtil.i("scale is " + scale+"----------->");
		LogUtil.i("screenwidth is " + screen_width + " screenheight " + screen_height);
        canvas.setHeightAndWidth(screen_width, screen_height,scale,density);
        canvas.setBitmap(backBitmap);
	 } 
	
	protected String imageProcessSeq(String path) //找点，裁边
	{
		// TODO Auto-generated method stub
		String str;
		preprocessdll.Preprocess_Binary2(path,  EXTENDPATH+"MyTemp/pbinary1.jpg");
		str = EXTENDPATH+"MyTemp/pbinary1.jpg";
		if (Build.VERSION.SDK_INT < 19) {//API 19 以前的
			sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(EXTENDPATH+"MyTemp/pbinary1.jpg"))));
		}else{
			MediaScannerConnection.scanFile(CropActivity.this,
					new String[]{Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath() +"MyTemp/pbinary1.jpg"}, null, null);
		}
		
		return str; 
	
	}

	public class RecoThread implements Runnable
	{
		private String mFileName;
		private String mPath;
		private String mRecgType;

		public RecoThread(String filename, String path,String recgtype)
		{
			mFileName = filename;
			mPath = path;
			mRecgType = recgtype;
		}

		@Override
		public void run()
		{
			Log.i(TAG, "!!!!!!!! RecoThread running !!!!!!!");

			if(!connInNet())
			{
				Log.i(TAG, "network err ,send msg !!!!!!");
				Message msg = new Message();
				msg.what = InfoMsg.NETWORK_ERR;
				CropActivity.this.textHandler.sendMessage(msg);
				return;
			}

			//String fid = null;
			fid = UploadImage.UploadFiletoHvn(mRecgType, mPath, mFileName, String.valueOf(1), false, "png");

			if (null == fid)
			{
				Log.d(TAG, "upload file failed !!!");
				Message msg = new Message();
				msg.what = InfoMsg.FILE_UPLOAD_FAIL;
				CropActivity.this.textHandler.sendMessage(msg);
				return;
			}
			/**********test by chenxinzhuang************/
			/*
			if(HanvonApplication.isAccurateRecg)
			{
				new UploadImage(textHandler).GetEvaluate(fid);
			}
			else
			{*/
				Log.i(TAG, "!!!!!!DEVID is " + HanvonApplication.AppDeviceId);
				if("".equals(HanvonApplication.hvnName))
				{
					new UploadImage(textHandler).GetRapidRecogRet(HanvonApplication.AppDeviceId, fid, "1", "4");
				}
				else
				{
					new UploadImage(textHandler).GetRapidRecogRet(HanvonApplication.hvnName, fid, "1", "4");
				}
			//}
			/***************END*************/
		}
	}

	public Handler textHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			super.handleMessage(msg);
			Log.d(TAG, "!!!!!!! textHandler handle msg");
			mProgress.dismiss();
			isRecognizing = false;
			switch (msg.what)
			{
				case InfoMsg.NETWORK_ERR:
					Toast.makeText(CropActivity.this, "网络连接失败，请检查网络后重试！", Toast.LENGTH_LONG).show();
					break;

				case InfoMsg.FILE_UPLOAD_FAIL:
					Toast.makeText(CropActivity.this, "上传失败，请检查网络并重试", Toast.LENGTH_SHORT);
					break;
				case InfoMsg.FILE_RECO_FAIL:
					Object msgobj = msg.obj;
					String msgcontent = msgobj.toString();
					String errcode = null;
					JSONObject jsonObject = null;
					try
					{
						jsonObject = new JSONObject(msgcontent);
						errcode = jsonObject.getString("code");
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}

					startRecFailActivity(errcode);
				break;

				case InfoMsg.FILE_RECOGINE_TYPE:
				{
					Object obj = msg.obj;
					Log.i(TAG, obj.toString());
					//processResult(obj.toString());
					String content = obj.toString();
					LogUtil.i("content is " + content);
					JSONObject jobj = null;
					try
					{
						if (content != null)
						{
							jobj = new JSONObject(content);
							if ("0".equals(jobj.getString("code")))
							{
								byte [] ret = Base64Utils.decode(jobj.getString("result"));
								String result = new String(ret, "GB2312");
								//String result = jobj.getString("result");
								LogUtil.i(" !!!! result is " + result);
								String offset = jobj.getString("offset");
								//String fuid = jobj.getString("fuid");
								//LogUtil.i("fuid is " + fuid + "; offset is " + offset);

								startResultActivity(result);
							}
							else
							{
								startRecFailActivity(jobj.getString("code"));
							}
						}
						else
						{
							Toast.makeText(getApplicationContext(), "请重试！", Toast.LENGTH_SHORT).show();
						}
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
					break;
				}
				case InfoMsg.ORDER_EVL_TYPE:
					Object evlobj = msg.obj;
					String evlcontent = evlobj.toString();
					try {
						JSONObject json = new JSONObject(evlcontent);
						if("0".equals(json.getString("code"))){
							OrderDetail orderDetail = new OrderDetail();
							orderDetail.setOrderFileNanme(oriName);
							orderDetail.setOrderFilesPages(json.getString("fileAmount"));
							orderDetail.setOrderFilesBytes(json.getString("wordsRange"));
							orderDetail.setOrderFinshTime(json.getString("finishTime"));
							orderDetail.setOrderPrice(json.getString("price"));
							orderDetail.setOrderWaitTime(json.getString("waitTime"));
							orderDetail.setAccurateWords(json.getString("accurateWords"));
							orderDetail.setRecogRate(json.getString("recogRate"));
							orderDetail.setRecogAngle(json.getString("recogAngle"));
							orderDetail.setOrderNumber(json.getString("oid"));
							orderDetail.setZoom(json.getString("zoom"));
							orderDetail.setOrderFid(fid);
							orderDetail.setOrderStatus("1");
							orderDetail.setContactId(json.getString("contactId"));
							orderDetail.setOrderPhone(json.getString("mobile"));
							orderDetail.setOrderName(json.getString("fullname"));
							Intent intent = new Intent();
							intent.setClass(CropActivity.this, OrderEvalPrices.class);
							Bundle bundle = new Bundle();
							bundle.putSerializable("ordetail", orderDetail);
							intent.putExtras(bundle);
							startActivity(intent);
						//	finish();
						}else if ("8002".equals(json.getString("code"))){
							Toast.makeText(CropActivity.this,json.getString("result"),Toast.LENGTH_SHORT).show();
						}else{
							Toast.makeText(CropActivity.this,"评估过程出现错误!",Toast.LENGTH_SHORT).show();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
			}
		};
	};

	protected void startRecFailActivity(String retCode)
	{
		String errMsg = null;
		if (null == retCode)
		{
			errMsg = InfoMsg.RECO_ERR_UNKNOWN;
		}
		else
		{
			Log.i(TAG, "result code is " + retCode);
			if (retCode.equals("520"))
			{
				Log.d(TAG, "!!!!!! server error 520 !!!!!!");
				errMsg = InfoMsg.RECO_ERR_SERVER;
			}
			else if (retCode.equals("524"))
			{
				Log.d(TAG, "!!!!!! checksum error 524 !!!!!!");
				errMsg = InfoMsg.RECO_ERR_CHECKSUM;
			}
			else
			{
				errMsg = InfoMsg.RECO_ERR_UNKNOWN;
			}
		}

		Intent retIntent = new Intent(CropActivity.this,RecFailActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("errMsg", errMsg);
		retIntent.putExtras(bundle);
		CropActivity.this.startActivity(retIntent);
		CropActivity.this.finish();

	}

	protected void startResultActivity(String result)
	{
		Intent retIntent = new Intent(CropActivity.this,RecResultActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("textResult", result);
		bundle.putString("path", path);
		retIntent.putExtras(bundle);
		CropActivity.this.startActivity(retIntent);
		CropActivity.this.finish();
	}

	protected void processResult(String content)
	{
		Log.d(TAG, "!!!!!!! processResult !!!!!!");
		JSONObject obj = null;
		try
		{
			if (content != null)
			{
				obj = new JSONObject(content);
				if ("0".equals(obj.getString("code")))
				{
					Log.d(TAG, "!!!!!!! get success result");
					byte [] ret = Base64Utils.decode(obj.getString("result"));
					String result = new String(ret);
					//String result = obj.getString("result");
					Log.d(TAG, " !!!! result is " + result);
					String offset = obj.getString("offset");
					Log.d(TAG, "!!!! offset is " + offset);

					Intent backIntent = new Intent(CropActivity.this,RecResultActivity.class);
					Bundle bundle = new Bundle();
					bundle.putString("textResult", result);
					backIntent.putExtras(bundle);
					CropActivity.this.startActivity(backIntent);
					CropActivity.this.finish();

				}
				else if (obj.getString("code").equals("520"))
				{
					Log.d(TAG, "!!!!!! server error 520 !!!!!!");
				}
				else if (obj.getString("code").equals("524"))
				{
					Log.d(TAG, "!!!!!! checksum error 524 !!!!!!");
				}
				else
				{
					String result = obj.getString("result");
					Toast.makeText(getApplicationContext(), "请重试！", Toast.LENGTH_SHORT).show();
					System.out.println(result);
				}
			}
			else
			{
				Toast.makeText(getApplicationContext(), "请重试！", Toast.LENGTH_SHORT).show();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public  boolean connInNet() //检查是否连网
	{
		ConnectionDetector connectionDetector = new ConnectionDetector(getApplication());
		if(connectionDetector.isConnectingTOInternet())
		{
			return true;
		}
		else
		{
			//Toast.makeText(getApplication(), "网络连接失败，请检查网络后重试！", Toast.LENGTH_LONG).show();
			return false;
		}		
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		if (cropBitmap!= null &&!cropBitmap.isRecycled())
		{
			cropBitmap.recycle();
		}

		if(backBitmap != null && !backBitmap.isRecycled())
		{
			backBitmap.recycle();
			backBitmap = null;
		}

		if (null != mProgress) {
			mProgress.dismiss();
		}
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK)
		{
			switch (requestCode)
			{
				case REQUEST_FILE_FORMAT:
					String filename = data.getStringExtra("filename");
					String suffix = data.getStringExtra("suffix");
					LogUtil.i("get file name is " + filename);
					LogUtil.i("suffix is " + suffix);
					startUpload(suffix);
					break;
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK )
        {
			flag = true;
			CropActivity.this.finish();	
        }
		return true;
	}


	private void exactRecoSingle(String path)
	{
		LogUtil.i("exactRecoSingle func");
		ArrayList<String> paths = new ArrayList<String>();
		paths.add(path);
		/*
		for(int i = 0;i<allAlbums.getBitList().size();i++)
		{
			if(allAlbums.getBitList().get(i).isSelect())
			{
				paths.add(allAlbums.getBitList().get(i).getPath());
			}
		}
		*/

		String sdCardPath = FileUtil.getSDCadrPath();
		File f1 = new File(sdCardPath + "/rctmp");
		if (f1.exists())
		{
			//f1.delete();
			RecursionDeleteFile(f1);
		}

		f1.mkdirs();

		for (int i = 0; i < paths.size(); i++)
		{
			LogUtil.i("path is " + paths.get(i));
			String filename = paths.get(i).substring(paths.get(i).lastIndexOf("/") + 1, paths.get(i).length());
			LogUtil.i("file name is " + filename);
			CopySdcardFile(paths.get(i), sdCardPath + "/rctmp/" + filename);
		}

		ZipCompressorByAnt zip = new ZipCompressorByAnt("/sdcard/rctmp.zip");
		zip.compressExe(sdCardPath + "/rctmp/");
		File zipfile = zip.getCompressedFile();
		LogUtil.i("file size is " + zipfile.length());
		getResultFileFormat(zipfile.length());
	}

	private static final int REQUEST_FILE_FORMAT = 4;

	private void getResultFileFormat(long filesize)
	{
		Intent intent = new Intent(CropActivity.this, ChooseFileFormatActivity.class);
		intent.putExtra("resultType", InfoMsg.RECO_MODE_EXACT_RECO);
		intent.putExtra("filename", "test");
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

	private void startUpload(String resultFileFormat)
	{
		Intent intent = new Intent(CropActivity.this, UploadFileActivity.class);
		intent.putExtra("fileamount", 1);
		intent.putExtra("fileformat", "png");
		intent.putExtra("fullpath", "/sdcard/rctmp.zip");
		intent.putExtra("filename", "rctmp.zip");
		intent.putExtra("resultfiletype", resultFileFormat);
		startActivity(intent);
		this.finish();
	}

}
