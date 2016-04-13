package com.hanvon.rc.md.camera.draw;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;

import com.hanvon.rc.md.camera.CameraManager;
import com.hanvon.rc.md.camera.DensityUtil;
//import com.hanvon.rc.md.camera.activity.BCardProcessing;
import com.hanvon.rc.md.camera.activity.CameraActivity;
import com.hanvon.rc.md.camera.activity.ModeCtrl;
import com.hanvon.rc.md.camera.activity.ModeCtrl.UserMode;
import com.zgcf.supercamera.DrawFocusArea;
import com.zgcf.supercamera.DrawiInflateRect;
import com.zgcf.supercamera.SuperCamera;

public class DrawImage extends DrawSuperClass implements DrawInterface {
	private int canvasW;
	private int canvasH;
	private RectF rect;

	private DrawFocusArea drawFocusArea;

	private DrawiInflateRect drawiInflateRect;
	private boolean rectZoomOk;
	// private long oldTime;
	private RectF intentRect;
	public static int b;

	public void setIntentRect(RectF intentRect) {
		this.intentRect = intentRect;
	}

	public DrawImage(Canvas canvas) {
		super(canvas);
		// this.initDrawInfo();
		this.initRect();

		drawFocusArea = new DrawFocusArea(canvas);

		drawiInflateRect = new DrawiInflateRect(canvas, rect, intentRect);

		initRect();
		if (null != CameraActivity.getCameraActivity()) {
			if (null != CameraActivity.getCameraActivity()
					.getApplicationContext()) {
				b = DensityUtil.dip2px(CameraActivity.getCameraActivity()
						.getApplicationContext(), 95);
			} else {
				b = 190;
			}
		}
		// TODO Auto-generated constructor stub

	}

	@Override
	public void updataDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		if (null == this.rect) {
			this.initRect();
		}

		if (rect != null) {
			if (ModeCtrl.isUserModeChanged()) {
				this.updataRect();
				ModeCtrl.setUserModeChanged(false);
			}

			drawiInflateRect.updataDraw(canvas);
		}
		if (ModeCtrl.getUserMode() == UserMode.BCARD)
		{
			if (null != drawFocusArea) {
				drawFocusArea.updataDraw(canvas);
			}

			/*
			if (BCardProcessing.getBitmap() != null && !BCardProcessing.getBitmap().isRecycled())
			{
				Bitmap bitmap = BCardProcessing.getBitmap();
				float scale = canvas.getWidth() / bitmap.getWidth();

				Matrix matrix = new Matrix();
				matrix.setScale(scale, scale);
				canvas.drawBitmap(bitmap, matrix, null);
			}
			*/
		}

	}

	public void updataRect() {
		drawiInflateRect.setEffectMONO(true);
		if (null != SuperCamera.getSuperCamera()) {
			int rst = SuperCamera.getSuperCamera().setEffectMONO();
			int t = 0;
			t = 9;
			if (t != 9) {
				rst = 0;
			}

		}
		switch (ModeCtrl.getUserMode()) {
		case BCARD:
			if (null != CameraManager.getCameraManager()) {
				this.intentRect = CameraManager.getCameraManager()
						.getPictureCtrlDrawRectF();
				drawiInflateRect.setIntentRect(intentRect);
				drawiInflateRect.setDrawMaskRect(false);
				drawiInflateRect.setDrawSanLine(false);
			}
			/*
			 * if (null != PictureRectCtrl.getPictureRectCtrl()) {
			 * this.intentRect = PictureRectCtrl.getPictureRectCtrl()
			 * .getDrawRect(); drawiInflateRect.setIntentRect(intentRect);
			 * drawiInflateRect.setDrawMaskRect(false);
			 * drawiInflateRect.setDrawSanLine(false);
			 * 
			 * }
			 */
			break;
		case SCANNING:
			if (null != CameraManager.getCameraManager()) {
				this.intentRect = CameraManager.getCameraManager()
						.getScanningRectCtrlDrawRect();
				drawiInflateRect.setIntentRect(intentRect);
				drawiInflateRect.setDrawMaskRect(true);
				if (ModeCtrl.isScanningStop()) {
					drawiInflateRect.setDrawSanLine(false);
				} else {
					drawiInflateRect.setDrawSanLine(true);
				}
			}
			/*
			 * if (null != ScanningRectCtrl.getScanningRectCtrl()) {
			 * this.intentRect = ScanningRectCtrl.getScanningRectCtrl()
			 * .getDrawRect(); drawiInflateRect.setIntentRect(intentRect);
			 * drawiInflateRect.setDrawMaskRect(true); if
			 * (ModeCtrl.isScanningStop()) {
			 * drawiInflateRect.setDrawSanLine(false); } else {
			 * drawiInflateRect.setDrawSanLine(true); }
			 * 
			 * }
			 */
			break;
		case SHOPPING:
			break;
		default:
			break;

		}
	}

	private void initRect() {
		// TODO Auto-generated method stub
		switch (ModeCtrl.getUserMode()) {
		case BCARD:
			if (null != CameraManager.getCameraManager()) {

				this.intentRect = CameraManager.getCameraManager()
						.getPictureCtrlDrawRectF();
				this.rect = new RectF(intentRect);

			}
			/*
			 * if (null != PictureRectCtrl.getPictureRectCtrl()) {
			 * this.intentRect = PictureRectCtrl.getPictureRectCtrl()
			 * .getDrawRect(); this.rect = new RectF(intentRect);
			 *
			 * }
			 */
			break;
		case SCANNING:
			if (null != CameraManager.getCameraManager()) {
				this.intentRect = CameraManager.getCameraManager()
						.getScanningRectCtrlDrawRect();
				this.rect = new RectF(intentRect);
			}
			/*
			 * if (null != ScanningRectCtrl.getScanningRectCtrl()) {
			 * this.intentRect = ScanningRectCtrl.getScanningRectCtrl()
			 * .getDrawRect(); this.rect = new RectF(intentRect);
			 *
			 * } break;
			 */
		case SHOPPING:
			break;
		default:
			break;

		}

	}

	@Override
	public void initDrawInfo() {
		// TODO Auto-generated method stub
		int rectX = canvas.getWidth() / 6;
		int rectW = canvas.getWidth() * 2 / 3;
		int rectH = rectW;// *3/4;
		int rectY = canvas.getHeight() / 2 - rectH / 2;
		rect = new RectF(rectX, rectY, rectX + rectW, rectY + rectH);
		DrawManager.setScanningRect(rect);
	}

	@Override
	public void initPaint() {
		// TODO Auto-generated method stub

	}

	public void DrawIm2age(Canvas canvas) {
		this.canvasW = canvas.getWidth();
		this.canvasH = canvas.getHeight();
	}

	/*
	 * public DrawFocusArea getDrawFocusArea() { return drawFocusArea; }
	 * 
	 * public void setDrawFocusArea(DrawFocusArea drawFocusArea) {
	 * this.drawFocusArea = drawFocusArea; }
	 */

}
