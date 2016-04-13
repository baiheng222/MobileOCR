package com.hanvon.rc.md.camera.draw;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;


public class DrawPromptText extends DrawSuperClass implements DrawInterface{

	private Paint paint;
	private String promptText;
	
	public DrawPromptText(Canvas canvas, String promptText) {
		super(canvas);
		this.promptText=promptText;
		// TODO Auto-generated constructor stub
		this.initDrawInfo();
		this.initPaint();
		this.updataDraw(canvas);
	}

	@Override
	public void updataDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		canvas.drawText(promptText, canvas.getWidth()>>1, canvas.getHeight()>>1, paint);
		//canvas.drawText(interInfo.getPromptStringAutoScanning(), interInfo.getRectDraw().centerX(), interInfo.getRectDraw().top-50, paint);
		//canvas.drawText(interInfo.getPromptStringTakePicture(), interInfo.getRectDraw().centerX(), interInfo.getRectDraw().bottom+100, paint);
	}

	@Override
	public void initDrawInfo() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initPaint() {
		// TODO Auto-generated method stub
		paint=new Paint();
		paint.setAntiAlias(true);
		if(DrawManager.textSize!=0){
			paint.setTextSize(DrawManager.textSize);
		}
		
		paint.setTextAlign(Align.CENTER);
		paint.setColor(Color.WHITE);
		
	}

}
