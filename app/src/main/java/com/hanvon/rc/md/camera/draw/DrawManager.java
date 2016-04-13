package com.hanvon.rc.md.camera.draw;

import com.hanvon.rc.md.camera.DensityUtil;
import com.hanvon.rc.md.camera.activity.CameraActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class DrawManager extends SurfaceView implements SurfaceHolder.Callback {
	private static final String TAG = DrawManager.class.getSimpleName();
	public static RectF scanningRect;
	// public static int canvasW;
	// public static int canvasH;
	private DrawImage drawImage;

	private static String promptText;
	private static String DisplayWidth;
	private static int orientation;
	public static long time1;
	public static float textSize;
	// public static boolean flag;
	public static Bitmap bitmap;

	public DrawManager(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		this.setZOrderOnTop(true);
		this.surfaceHolder = this.getHolder();
		this.surfaceHolder.setFormat(PixelFormat.TRANSPARENT);
		this.surfaceHolder.addCallback(this);
		// this.surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		textSize = DensityUtil.dip2px(context, 15);

	}

	private SurfaceHolder surfaceHolder;
	private ThreadSurfaceDraw threadSurfaceDraw;

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
							   int height) {
		// TODO Auto-generated method stub
		Log.i(TAG, "*********************************surfaceChanged*********************************");
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		Log.i(TAG, "*********************************surfaceCreated*********************************");
		threadSurfaceDraw = new ThreadSurfaceDraw();
		threadSurfaceDraw.mSurfaceHolder = this.surfaceHolder;
		threadSurfaceDraw.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		 Log.i(TAG, "*********************************surfaceDestroyed*********************************");
		threadSurfaceDraw.mQuit = true;
		// Log.i("DrawSurfaceView",
		// "threadSurfaceDraw.mQuit = true;-------------");

	}

	public class ThreadSurfaceDraw extends Thread
	{
		SurfaceHolder mSurfaceHolder;
		boolean mRunning;
		boolean mQuit;
		boolean mInitialized = false;
		private String TAG = "ThreadSurfaceDraw";

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Paint paint = new Paint();
			int timeinterval = 25;

			while (true) {
				if (mQuit) {
					return;
				}
				// Log.i(TAG,
				// "<---------------------->Canvas canvas = mSurfaceHolder.lockCanvas()--start;");
				Canvas canvas = mSurfaceHolder.lockCanvas();
				// Log.i(TAG,
				// "<---------------------->Canvas canvas = mSurfaceHolder.lockCanvas()--end;");

				if ((canvas != null)) {
					canvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
					int w = canvas.getWidth();
					int h = canvas.getHeight();
					long time011 = System.currentTimeMillis();
					// if(CameraManager.isAutoScanning()){
					if (drawImage == null) {
						drawImage = new DrawImage(canvas);
					}
					drawImage.updataDraw(canvas);
					/*
					 * if(DrawManager.flag){ RectF rect=new
					 * RectF(0,0,canvas.getWidth(),canvas.getHeight()); Paint
					 * mPaint=new Paint(); mPaint.setStyle(Style.FILL);
					 * mPaint.setColor(0x7f7f7f7f); canvas.drawRect(rect,
					 * mPaint); }
					 */

					// }else{
					// drawImage=null;
					// }

					// new DrawPromptText( canvas,
					// String.valueOf(DrawManager.DisplayWidth));
					// new DrawPromptText( canvas,
					// String.valueOf(canvas.getWidth()));

					/* canvas.drawText("display:Width"+DisplayWidth, 100, 800,
					 paint);*/

					// canvas.drawText("canvas:Width"+canvas.getWidth(), 100,
					// 200, paint);
					// canvas.drawText("Orientation"+String.valueOf(getOrientation()),
					// 300, 300, paint);

					if (bitmap != null) {
						// canvas.drawBitmap(CameraManager.getPreviewBitmap(),
						// 0, 0, null);
						Matrix matrix = new Matrix();
						matrix.setScale(1, 1);
						canvas.drawBitmap(bitmap, matrix, null);
					}
					paint.setTextSize(textSize);
					paint.setAntiAlias(true);
					paint.setColor(Color.WHITE);
					paint.setTextAlign(Align.CENTER);
					
					if (getPromptText() != null) {
						new DrawPromptText(canvas, getPromptText());
					}
					long time012 = System.currentTimeMillis();

				}
				 Log.i(TAG,
				 "<----------------------> mSurfaceHolder.unlockCanvasAndPost(canvas)--start;;");
				if (mQuit) {

					// Log.i(TAG,
					// "<----------------------> mQuitmQuitmQuitmQuitmQuitmQuitmQuitmQuit");
					return;
				}else{
					mSurfaceHolder.unlockCanvasAndPost(canvas);
				}
				
				// Log.i(TAG,
				// "<---------------------->mSurfaceHolder.unlockCanvasAndPost(canvas)--end;;");
				try {
					Thread.sleep(timeinterval);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	public synchronized static String getPromptText() {
		return promptText;
	}

	public synchronized static void setPromptText(String promptText) {
		DrawManager.promptText = promptText;
	}

	public static String getDisplayWidth() {
		return DisplayWidth;
	}

	public static void setDisplayWidth(String displayWidth) {
		DisplayWidth = displayWidth;
	}

	public synchronized static int getOrientation() {
		return orientation;
	}

	public synchronized static void setOrientation(int orientation) {
		DrawManager.orientation = orientation;
	}

	public synchronized static RectF getScanningRect() {
		return scanningRect;
	}

	public synchronized static void setScanningRect(RectF scanningRect) {
		DrawManager.scanningRect = scanningRect;
	}

	public DrawImage getDrawImage() {
		return drawImage;
	}

}