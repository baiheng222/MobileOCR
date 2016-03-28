package com.hanvon.mobileocr.md.camera;

import android.hardware.Camera;
import android.hardware.Camera.Size;

public class PreviewDataManager implements Camera.PreviewCallback {
	private static final String TAG = PreviewDataManager.class.getSimpleName();
	private static PreviewDataManager previewDataManager;

	private long oldTime = System.currentTimeMillis();

	private ThreadPreviewDataToImageData threadPreviewDataToImageData;

	// private ThreadPreviewDataToFakePictureImageData
	// threadPreviewDataToFakePictureImageData;
	// private ThreadPreviewYUVDecode threadPreviewYUVDecode;

	// private BlockingQueueBitmap blockingQueueBitmap;
	private void initThread() {
		threadPreviewDataToImageData = new ThreadPreviewDataToImageData();
		// threadPreviewDataToFakePictureImageData = new
		// ThreadPreviewDataToFakePictureImageData();
		// threadPreviewYUVDecode = new ThreadPreviewYUVDecode();

		threadPreviewDataToImageData.start();
		/*
		 * if (CameraConfigure.isUseFakeImageData) { //
		 * threadPreviewDataToFakePictureImageData.start(); } else {
		 * //threadPreviewYUVDecode.start(); }
		 */

		/** preview callback to ThreadPreviewDataToImageData */
		if (null == BlockingQueuePreviewData.getBlockingQueuePreviewData()) {
			new BlockingQueuePreviewData();
		}
		/** ThreadPreviewDataToImageData to ThreadQRcodeDecode */
		if (null == BlockingQueueGrayByteData.getBlockingQueueGrayByteData()) {
			new BlockingQueueGrayByteData();
		}
		/** ThreadPreviewDataToImageData to ThreadBarcodeDecode */
		if (null == BlockingQueueGrayByteDataArray
				.getBlockingQueueGrayByteDataArray()) {
			new BlockingQueueGrayByteDataArray();
		}
		/** ThreadPreviewDataToImageData to ThreadBCardScanning */
		if (null == BlockingQueueGrayByteDataPreviewData
				.getBlockingQueueGrayByteDataPreviewData()) {
			new BlockingQueueGrayByteDataPreviewData();
		}
		/*
		 * if (null == BlockingQueueByteArray.getBlockingQueueByteArray()) { new
		 * BlockingQueueByteArray(); }
		 */

		/*
		 * if (null == BlockingQueueFakePictureImageData
		 * .getBlockingQueueFakePictureImageData()) { new
		 * BlockingQueueFakePictureImageData(); }
		 */
	}

	public void release() {

		if (null != threadPreviewDataToImageData) {
			ThreadPreviewDataToImageData.setThreadPreviewDataToImageData(null);
			threadPreviewDataToImageData.setQuit(true);
			threadPreviewDataToImageData = null;
		}
		/*
		 * if (null != threadPreviewDataToFakePictureImageData) {
		 * threadPreviewDataToFakePictureImageData.setQuit(true);
		 * threadPreviewDataToFakePictureImageData = null; } if (null !=
		 * threadPreviewYUVDecode) { threadPreviewYUVDecode.setQuit(true);
		 * threadPreviewYUVDecode = null; }
		 */
		// ---------------------------------------------------
		/** preview callback to ThreadPreviewDataToImageData */
		if (null != BlockingQueuePreviewData.getBlockingQueuePreviewData()) {
			BlockingQueuePreviewData.setBlockingQueuePreviewData(null);
		}
		/** ThreadPreviewDataToImageData to ThreadQRcodeDecode */
		if (null != BlockingQueueGrayByteData.getBlockingQueueGrayByteData()) {
			BlockingQueueGrayByteData.setBlockingQueueGrayByteData(null);
		}
		/** ThreadPreviewDataToImageData to ThreadBarcodeDecode */
		if (null != BlockingQueueGrayByteDataArray
				.getBlockingQueueGrayByteDataArray()) {
			BlockingQueueGrayByteDataArray
					.setBlockingQueueGrayByteDataArray(null);
		}

		/** ThreadPreviewDataToImageData to ThreadBCardScanning */
		if (null != BlockingQueueGrayByteDataPreviewData
				.getBlockingQueueGrayByteDataPreviewData()) {
			BlockingQueueGrayByteDataPreviewData
					.setBlockingQueueGrayByteDataPreviewData(null);
		}

		/*
		 * if (null != BlockingQueueByteArray.getBlockingQueueByteArray()) {
		 * BlockingQueueByteArray.setBlockingQueueByteArray(null); }
		 */

		/*
		 * if (null != BlockingQueueFakePictureImageData
		 * .getBlockingQueueFakePictureImageData()) {
		 * BlockingQueueFakePictureImageData
		 * .getBlockingQueueFakePictureImageData().release();
		 * BlockingQueueFakePictureImageData
		 * .setBlockingQueueFakePictureImageData(null);
		 * 
		 * }
		 */

	}

	public PreviewDataManager() {

		this.initThread();
		setPreviewDataManager(this);
	}

	@Override
	public void onPreviewFrame(byte[] data, Camera camera) {
		// TODO Auto-generated method stub
		/*
		 * long tempTime=System.currentTimeMillis();
		 * DrawManager.setPromptText(String.valueOf(tempTime-oldTime));
		 * oldTime=tempTime;
		 */
		if (camera == null) {
			return;
		}
		if (data == null) {
			return;
		}
		Size previewSize=null;
		if (null != CameraManager.getCameraManager()) {
			 previewSize = CameraManager.getCameraManager()
					.getPreviewSize();
			
		}
		if(null!=previewSize){
			if ((previewSize.width * previewSize.height * 3 / 2) != data.length) {
				return;
			} else {
				long startTime = System.currentTimeMillis();

				if (null != BlockingQueuePreviewData
						.getBlockingQueuePreviewData()) {
					BlockingQueuePreviewData.getBlockingQueuePreviewData()
							.putData(
									new GrayByteData(data, previewSize.width,
											previewSize.height));
				}

				long endTime = System.currentTimeMillis();

			}
		}
	}

	public static PreviewDataManager getPreviewDataManager() {
		return previewDataManager;
	}

	public static void setPreviewDataManager(
			PreviewDataManager previewDataManager) {
		PreviewDataManager.previewDataManager = previewDataManager;
	}

}
