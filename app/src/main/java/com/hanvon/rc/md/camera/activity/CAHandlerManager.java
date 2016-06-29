package com.hanvon.rc.md.camera.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class CAHandlerManager extends Handler
{

	private CameraActivity cameraActivity;
	public static final int RESTART_PREVIEW = 1000;
	//public static final int SET_PREVIEW_CALLBACK = 1001;

	public static final int TOAST_STRING = 1002;
	public static final int INVISBLE_BUTTON = 1003;
	public static final int INTENT_SHOPPING = 1004;
	public static final int UPDATA_TEXTVIEW = 1005;
	public static final int INTENT_TO_IMAGE_SHOPPING_URL = 1006;
	public static final int TEXTVIEW_PROCESS = 1007;
	public static final int AUTO_FOCUS_CYCLE=1008;
	public static final int SET_VISIBLE_BUTTON=1009;
	public static final int RESTART_USERMODE=1010;
	public static final int JPG_SAVE_COMPLETE=1011;

	public CAHandlerManager(CameraActivity cameraActivity) {
		this.cameraActivity = cameraActivity;
	}

	@Override
	public void handleMessage(Message msg) {
		// TODO Auto-generated method stub
		super.handleMessage(msg);
		if (cameraActivity == null) {
			return;
		}
		Bundle b;
		switch (msg.what) {
		case RESTART_USERMODE:
			
			//cameraActivity.restartUserMode();
			break;
		case SET_VISIBLE_BUTTON:
			
			//cameraActivity.setVisibleButton();
			break;
		case AUTO_FOCUS_CYCLE:
			 b = msg.getData();
			int time=1000;;
			if (b != null) {
				 time=b.getInt("delayTime");
			}
			
			//cameraActivity.autoFocusCycle(time);
			break;
		case RESTART_PREVIEW:
			cameraActivity.restartPreviewAfterTackpicture();
			break;
		/*case SET_PREVIEW_CALLBACK:
			if(PreviewDataManager.getPreviewDataManager()!=null){
				PreviewDataManager.getPreviewDataManager().start();
			}*/
		case TOAST_STRING:

			 b = msg.getData();
			if(b!=null){
				String string = b.getString("ToastString");
			//cameraActivity.ToastString(string);
			}
			
			break;
		case TEXTVIEW_PROCESS:

			 b = msg.getData();
			if (b != null) {
				String string = b.getString("textViewProcess");
				//cameraActivity.updataTextViewProcess(string);
			}

			break;
		case INVISBLE_BUTTON:
			//cameraActivity.setButtonInvisible();

			break;
		/*case INTENT_SHOPPING:
			b = msg.getData();
			if (b != null) {
				//byte[] dataArray = b.getByteArray("dataArray");
				Bitmap bitmap=b.getParcelable("bitmap");
				cameraActivity.intentToStartImageClassifyActivity(bitmap);
			}
			break;*/
		case INTENT_TO_IMAGE_SHOPPING_URL:
			b = msg.getData();
			
			if (b != null) {
				String url = b.getString("url");
				//cameraActivity.intentToImageShoppingByUrl(url);
			}
			break;
		case UPDATA_TEXTVIEW:
			b = msg.getData();
			if (b != null) {
				String string = b.getString("textView");
				//cameraActivity.setTextView(string);
			}
			break;

			case JPG_SAVE_COMPLETE:
				b = msg.getData();
				if (b != null)
				{
					String path = b.getString("filepath");
					cameraActivity.jpgSaveComplete(path);
				}

			break;
		}
		
	}
}
