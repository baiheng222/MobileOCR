package com.hanvon.rc.md.camera;

import android.graphics.Rect;
import android.hardware.Camera.Size;
import android.util.Log;

import com.hanvon.rc.md.camera.activity.ModeCtrl;
import com.hanvon.rc.md.camera.activity.ModeCtrl.UserMode;
import com.imageprocessing.ImageProcessing;

public class ThreadPreviewDataToImageData extends Thread
{

	private static ThreadPreviewDataToImageData threadPreviewDataToImageData;
	private static final String TAG = ThreadPreviewDataToImageData.class
			.getSimpleName();
	// private GrayByteData grayByteDataPreview;

	private boolean isQuit;
	private Size previewSize;
	private boolean isInit = false;
	private int width;
	private int height;
	private int offsetX;
	private int offsetY;

	public ThreadPreviewDataToImageData()
	{
		setThreadPreviewDataToImageData(this);
	}

	@Override
	public void run()
	{
		// TODO Auto-generated method stub
		super.run();
		while (!isQuit())
		{
			// Log.i(TAG, this.getName());
			long threadStartTime = System.currentTimeMillis();
			try
			{
				Thread.sleep(30);
			} catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			while (null == BlockingQueuePreviewData.getBlockingQueuePreviewData())
			{
				Log.i(TAG, "BlockingQueuePreviewData is null");
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			if (!isInit)
			{
				this.initPreviewImageData();

			}

			if (previewSize == null)
			{
				continue;
			}
			this.isInit = true;
			GrayByteData grayByteDataPreviewData = BlockingQueuePreviewData
					.getBlockingQueuePreviewData().takeData();

			long startTime = System.currentTimeMillis();

			// Log.i(TAG,
			// "ImageProcessing.YUV420spToGray-before");//------------------------------
			// QRcode
			// 扫码
			if (ModeCtrl.getUserMode() == UserMode.SCANNING)
			{
				if (null != BlockingQueueGrayByteData
						.getBlockingQueueGrayByteData()) {
					if (BlockingQueueGrayByteData
							.getBlockingQueueGrayByteData().isEmpty()) {
						GrayByteData grayByteDataQR = new GrayByteData(
								new byte[width * height], width, height);

						if (!this.isQuit) {
							Log.i(TAG,
									"start-1-ImageProcessing" + this.getName());
							ImageProcessing.YUV420SPToGrayByteRotation(
									grayByteDataPreviewData.getData(),
									previewSize.width, previewSize.height,
									offsetX, offsetY, height, width,
									grayByteDataQR.getData(), 90);

							Log.i(TAG, "end-1-ImageProcessing" + this.getName());
						}

						GrayByteData[] grayByteDataArray = new GrayByteData[2];

						grayByteDataArray[0] = new GrayByteData(new byte[width
								* height], width, height);

						grayByteDataArray[1] = new GrayByteData(new byte[height
								* width], height, width);
						System.arraycopy(grayByteDataQR.getData(), 0,
								grayByteDataArray[0].getData(), 0,
								grayByteDataQR.getData().length);

						if (null != BlockingQueueGrayByteData
								.getBlockingQueueGrayByteData()) {
							BlockingQueueGrayByteData
									.getBlockingQueueGrayByteData().putData(
											grayByteDataQR);
						}
						grayByteDataQR = null;

						// ------------------------------Barcode

						// Log.i(TAG, "ImageProcessing.YUV420spToGray-before");
						if (!this.isQuit) {
							Log.i(TAG,
									"start-2-ImageProcessing" + this.getName());

							ImageProcessing.YUV420SPToGrayByteRotation(
									grayByteDataPreviewData.getData(),
									previewSize.width, previewSize.height,
									offsetX, offsetY, height, width,
									grayByteDataArray[1].getData(), 0);
							Log.i(TAG, "end-2-ImageProcessing" + this.getName());
						}
						// Log.i(TAG, "ImageProcessing.YUV420spToGray-end");
						if (null != BlockingQueueGrayByteDataArray
								.getBlockingQueueGrayByteDataArray()) {
							BlockingQueueGrayByteDataArray
									.getBlockingQueueGrayByteDataArray()
									.putData(grayByteDataArray);
						}
						grayByteDataArray = null;
					}
				}

			}

			// 名片识别
			if (ModeCtrl.getUserMode() == UserMode.BCARD)
			{

				if (null != BlockingQueueGrayByteDataPreviewData
						.getBlockingQueueGrayByteDataPreviewData()) {

					BlockingQueueGrayByteDataPreviewData
							.getBlockingQueueGrayByteDataPreviewData().putData(
									grayByteDataPreviewData);

					Log.i(TAG, "put BCard previewData" + this.getName());

				}

			}

			grayByteDataPreviewData = null;
			long endTime = System.currentTimeMillis();

			long threadEndTime = System.currentTimeMillis();
			// DrawManager.setPromptText(String.valueOf(threadEndTime-threadStartTime));
		}

		previewSize = null;

	}

	private void initPreviewImageData()
	{
		// TODO Auto-generated method stub
		if (null != CameraManager.getCameraManager()) {
			if (null != CameraManager.getCameraManager()) {
				previewSize = CameraManager.getCameraManager().getPreviewSize();
			}
		}
		/*if (SizeCtrl.getSizeCtrl() != null) {
			previewSize = SizeCtrl.getSizeCtrl().getPreviewSize();
		}*/

		if (previewSize == null) {
			return;
		}
		width = previewSize.height;
		height = previewSize.width;
		offsetX = 0;
		offsetY = 0;
		Rect rect = null;
		// ------------
		if (null != CameraManager.getCameraManager()) {
			rect = CameraManager.getCameraManager()
					.getScanningRectCtrlPreviewRect();
		}
		/*
		 * if (null != ScanningRectCtrl.getScanningRectCtrl()) { rect =
		 * ScanningRectCtrl.getScanningRectCtrl().getPreviewRect(); }
		 */

		if (rect == null) {
			return;
		}
		width = rect.right - rect.left;
		height = rect.bottom - rect.top;
		offsetX = rect.top;
		offsetY = previewSize.height - rect.right;

		isInit = true;
	}

	public synchronized boolean isQuit() {
		return isQuit;
	}

	public synchronized void setQuit(boolean isQuit) {
		this.isQuit = isQuit;
	}

	public static ThreadPreviewDataToImageData getThreadPreviewDataToImageData() {
		return threadPreviewDataToImageData;
	}

	public static void setThreadPreviewDataToImageData(
			ThreadPreviewDataToImageData threadPreviewDataToImageData) {
		ThreadPreviewDataToImageData.threadPreviewDataToImageData = threadPreviewDataToImageData;
	}
}
