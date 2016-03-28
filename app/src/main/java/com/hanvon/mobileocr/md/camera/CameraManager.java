package com.hanvon.mobileocr.md.camera;

import android.content.Context;
import android.hardware.Camera.PreviewCallback;

import com.zgcf.supercamera.SuperCamera;

public class CameraManager extends SuperCamera {

	private static final String TAG = CameraManager.class.getSimpleName();
	private static CameraManager cameraManager;
	private boolean isTakePicture;

	public CameraManager(Context context, PreviewCallback previewCallback) {
		super(context, previewCallback);
		setCameraManager(this);
		// TODO Auto-generated constructor stub		
	}

	public boolean isTakePicture() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setTakePicture(boolean isTakePicture) {
		this.isTakePicture = isTakePicture;
	}

	public static CameraManager getCameraManager() {
		return cameraManager;
	}

	public static void setCameraManager(CameraManager cameraManager) {
		CameraManager.cameraManager = cameraManager;
	}

	

	
}
