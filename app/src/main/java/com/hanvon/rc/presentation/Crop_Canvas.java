package com.hanvon.rc.presentation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.hanvon.rc.R;
import com.hanvon.rc.utils.LogUtil;

import org.apache.commons.logging.Log;

public class Crop_Canvas extends ImageView
{
	
	private final static int PRESS_LT = 0; //表示左上
	private final static int PRESS_RT = 1; //表示右上
	private final static int PRESS_RB = 2; //表示右下
	private final static int PRESS_LB = 3; //表示左下
	
	private final static int PRESS_CT = 4; //表示顶部的中点
	private final static int PRESS_CB = 5; //表示底部的中点
	private final static int PRESS_CL = 6; //表示左边的中点
	private final static int PRESS_CR = 7; //表示右边的中点
	
	private Bitmap bitmap = null;//原始图片
	private Paint mPaint = null;
	
	private float[] leftTop;	//左上角的顶点坐标
	private float[] rightTop;	//右上角的顶点坐标
	private float[] rightBottom;//右下角的顶点坐标
	private float[] leftBottom;//左下角的顶点坐标
	
	private float[] centerTop;	//顶部中点的坐标
	private float[] centerBottom;	//底部中点的坐标
	private float[] centerLeft;	//左边中点的坐标
	private float[] centerRight;	//右边中点的坐标
	
	private final static int RADIUS = 20; //圆的半径
	private int width; //宽度
	private int height;//高度
	private float scale,density;
	private boolean isSmall = false;//图片长度大于屏幕长度
	private int frameWidth,frameHeight,canvasHeight,canvasWidth;
	private boolean touchFlag = false; //触笔是否在屏幕之 
	private boolean cutFlag = false; //是否点击了menu上的裁剪按钮 
	private int recFlag = -1; //用来存储触笔点击了哪个小圆框
	private boolean firstFlag = false;
	
	private float[] points; 
	private float startX = 0,startY = 0,moveX = 0,moveY = 0;
	private boolean rotateFlag = false; //旋转标记
	private float translateHeight,translateWidth;//平移的高度
	private int rectWidth,rectHeight;//框的高宽
	private RectF boundRect = new RectF(); //画框的边界
    private int padding = 10;
	public Crop_Canvas(Context context) {
		super(context);
		init();
	}
	
	public Crop_Canvas(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	public void init()
	{
		cutFlag = true;
		mPaint = new Paint();
		mPaint.setColor(getResources().getColor(R.color.dodgerblue));
		//mPaint.setColor(Color.GREEN);
		mPaint.setStrokeWidth(5); //设置线宽,单位是像素
		mPaint.setStyle(Paint.Style.STROKE); // 将画笔的风格改为空心
		firstFlag = true;
		
		leftTop = new float[2];
		rightTop = new float[2];
		rightBottom = new float[2];
		leftBottom = new float[2];
		
		centerTop = new float[2];
		centerBottom = new float[2];
		centerLeft = new float[2];
		centerRight = new float[2];
		//printPointPos("print at init");
	}

	private void printPointPos(String tag)
	{
		LogUtil.i(tag);
		LogUtil.i(leftTop[0] + ","+ leftTop[1]+ ": " + centerTop[0] + ","+centerTop[1] + ": " + rightTop[0] + "," + rightTop[1]);
		LogUtil.i(centerLeft[0]+ "," +centerLeft[1] + ",     " + centerRight[0]+","+centerRight[1]);
		LogUtil.i(leftBottom[0]+ "," +leftBottom[1] + ", " + centerBottom[0]+","+centerBottom[1] + ", " + rightBottom[0]+","+rightBottom[1]);
		LogUtil.i("translateWidth: " + translateWidth + " ,translateHeight: " + translateHeight);
		LogUtil.i("frameWidht:" + frameWidth + " , frameHeight:" + frameHeight);
		LogUtil.i("rectWidth:" + rectWidth + " , rectHeight:" + rectHeight);
		LogUtil.i("boundRect is " + boundRect.left + "," + boundRect.top + " :" + boundRect.right+"," + boundRect.bottom);
	}

	public void setBitmap(Bitmap bitmap)
	{
		this.bitmap = bitmap;
		if(scale>1)
		{
			rectHeight = bitmap.getHeight();
			rectWidth = bitmap.getWidth();
			scale = 1;
		}
		else
		{
			rectHeight = (int) (bitmap.getHeight() * scale);
			rectWidth = (int) (bitmap.getWidth() * scale);
		}
		setImageBitmap(bitmap);
//		Matrix matrix = new Matrix();

		LogUtil.i("bitmap w：" + bitmap.getWidth() + " bitmap h:" + bitmap.getHeight());
		LogUtil.i("rectWidth:" + rectWidth + " , rectHeight:" + rectHeight);


		translateHeight = (float) (frameHeight-rectHeight-96*density-padding*2*density)/2 - 20;//减去20是为了画框更准
		//translateHeight = 0;
		translateWidth = (float)(frameWidth-rectWidth-padding*2*density)/2;

		LogUtil.i("translateWidth: " + translateWidth + " ,translateHeight: " + translateHeight);

		LogUtil.i("setBitmap end !!!!");

//		matrix.setScale(scale, scale);
//		matrix.postTranslate(translateWidth, translateHeight);
//		setImageMatrix(matrix);
	}
     
	public void setCanvasWidthAndHeight(int width,int height)
	{
		canvasWidth = width;
		canvasHeight = height;
	}
//	public void setHeightAndWidth(int w, int h,float point[],float scale){
//		points = point;
//		frameHeight = h;
//		frameWidth = w;
//		this.scale = scale;
//	}
	public void setHeightAndWidth(int w, int h,float scale,float density)
	{
//		points = point;
		frameWidth = w;
		frameHeight = h;
		this.scale = scale;
		this.density = density;
	}
	
	public void imageScale()
	{
		LogUtil.i("imagescale"+canvasWidth+"--"+canvasHeight);
		width = rectWidth;
		height = rectHeight;
		boundRect.left = density*padding + translateWidth;
		boundRect.top = (float) (density*padding + translateHeight);
		boundRect.right = (float) (width+density*padding)+translateWidth;
		boundRect.bottom = (float) (height+density*padding + translateHeight);
		
		leftTop[0] = density*padding + translateWidth;
		leftTop[1] = (float) (density*padding + translateHeight);
		rightTop[0] = (float) (width+density*padding)+translateWidth;
		rightTop[1] = (float) (density*padding + translateHeight);
		rightBottom[0] = (float) (width+density*padding)+translateWidth;;
		rightBottom[1] = (float) (height+density*padding + translateHeight);
		leftBottom[0] = density*padding+translateWidth;
		leftBottom[1] = (float) (height-0+density*padding + translateHeight);
		
		centerTop[0] = (float)((leftTop[0] + rightTop[0])/2);
		centerTop[1] = (float) ((leftTop[1] + rightTop[1])/2);
		
		centerBottom[0] = (float) ((leftBottom[0] + rightBottom[0])/2);
		centerBottom[1] =  (float) ((leftBottom[1] + rightBottom[1])/2);
		
		centerLeft[0] =  (float) ((leftTop[0] + leftBottom[0])/2);
		centerLeft[1] = (float) ((leftTop[1] + leftBottom[1])/2);
		
		centerRight[0] = (float)( (rightTop[0] + rightBottom[0])/2);
		centerRight[1] = (float)( (rightTop[1] + rightBottom[1])/2);

		//printPointPos("printpoints in func imageScale");
	}
      
	public void setRotateFlag(boolean rotateFlag)
	{
		this.rotateFlag = rotateFlag;
	}

	public Bitmap getSubsetBitmap(int frameWidth, int frameHeight)
	{
        firstFlag = true;  
        /*float ratio = (float) ((bitmap.getWidth()*100.0)/(frameWidth*100.0)) ;
        int x =  (int) ((leftTop[0]*ratio*100)/100);
//        int y =  (int) ((((leftTop[1])*ratio*100)/100) );//平移前
        int y =  (int) ((((leftTop[1]-translateHeight)*ratio*100)/100));//平移后
    	int w = (int) (((rightTop[0]-leftTop[0])* ratio *100)/100);
    	int h = (int) (((leftBottom[1]-leftTop[1])* ratio *100 )/100);
        System.out.println(x+"width" + w );
        return Bitmap.createBitmap(bitmap, x, y,w,h);*/        
        int x = (int) ((leftTop[0] - padding*density-translateWidth)/scale);
        int y =  (int) ((leftTop[1] - padding*density-translateHeight)/scale);
    	int w = (int) (((rightTop[0]-leftTop[0])/scale *100)/100);
    	int h = (int) (((leftBottom[1]-leftTop[1])/scale *100 )/100);
    	//防止越界处理
    	if(x<0){
    		x= 0;
    	}
    	if(y<0){
    		y = 0;
    	}
    	if(x+ w>bitmap.getWidth())
		{
    		x =bitmap.getWidth()- w;
    	}
    	if(y+h>bitmap.getHeight())
		{
    		y = bitmap.getHeight() - h;
    	}
        LogUtil.i(x+"width" + w );
        return Bitmap.createBitmap(bitmap, x, y,w,h);
	}  
    public boolean onTouchEvent(MotionEvent event)
	{
        if(event.getAction() == MotionEvent.ACTION_DOWN && cutFlag)
		{
        	startX = event.getX();
        	startY = event.getY();
            if(this.findPresseddst(event.getX(), event.getY()))
			{
            	touchFlag = true;  
            	return true;  
            }
			else if(this.findPresseddst2(event.getX(), event.getY()))
			{
            	touchFlag = true;  
            	return true;
            }
        }

		if (event.getAction() == MotionEvent.ACTION_MOVE && touchFlag)
		{
			moveX = event.getX();
			moveY = event.getY();
			// 判断是否点击了哪个个小圆框
			if(this.pressOnArea(moveX, moveY)){
				return true;
			}
			// TODO 如果选择区域大小跟图像大小一样时，就不能移动
		}
        if(event.getAction() == MotionEvent.ACTION_UP)
		{
            recFlag = -1;  
            this.invalidate();  
            touchFlag = false;  
        }  
          
        return super.onTouchEvent(event);  
    }  
      
    private boolean pressOnArea(float x,float y)
	{
        switch(recFlag)
		{
        case Crop_Canvas.PRESS_LB:  
            this.pressLB(x, y);  
            break;  
        case Crop_Canvas.PRESS_LT:  
            this.pressLT(x, y);  
            break;  
        case Crop_Canvas.PRESS_RB:  
            this.pressRB(x, y);  
            break;  
        case Crop_Canvas.PRESS_RT:  
            this.pressRT(x, y);  
            break; 
        case Crop_Canvas.PRESS_CT:
        	this.pressCT(x, y);
        	break;
        case Crop_Canvas.PRESS_CB:
        	this.pressCB(x, y);
        	break;
        case Crop_Canvas.PRESS_CL:
        	this.pressCL(x, y);
        	break;
        case Crop_Canvas.PRESS_CR:
        	this.pressCR(x, y);
        	break;
        default:return false;  
        }  
        this.invalidate();  
        startX = moveX;
		startY = moveY;
        return true;  
    }  
      
    public boolean findPresseddst(float x,float y)
	{
        boolean returnFlag = false;  
        if(isInCircle(x, y, leftTop)){  
            recFlag = Crop_Canvas.PRESS_LT;  
            returnFlag = true;  
        }else if(isInCircle(x, y, rightTop)){  
            recFlag = Crop_Canvas.PRESS_RT;  
            returnFlag = true;  
        }else if(isInCircle(x, y, rightBottom)){  
            recFlag = Crop_Canvas.PRESS_RB;  
            returnFlag = true;  
        }else if(isInCircle(x, y, leftBottom)){  
            recFlag = Crop_Canvas.PRESS_LB;  
            returnFlag = true;  
        }else if(isInCircle(x, y, centerTop)){
        	recFlag = Crop_Canvas.PRESS_CT;  
            returnFlag = true;  
        }else if(isInCircle(x, y, centerBottom)){
        	recFlag = Crop_Canvas.PRESS_CB;  
            returnFlag = true;  
        }else if(isInCircle(x, y, centerLeft)){
        	recFlag = Crop_Canvas.PRESS_CL;  
            returnFlag = true;  
        }else if(isInCircle(x, y, centerRight)){
        	recFlag = Crop_Canvas.PRESS_CR;  
            returnFlag = true;  
        }
        
        return returnFlag;  
    }  
    public boolean findPresseddst2(float x,float y)
	{
        boolean returnFlag = false;  
        int lt = distance(x, y, leftTop);
    	int rt = distance(x, y, rightTop);
    	int lb = distance(x, y, leftBottom);
    	int rb = distance(x, y, rightBottom);
    	int ct = distance(x, y, centerTop);
    	int cb = distance(x, y, centerBottom);
    	int cl = distance(x, y, centerLeft);
    	int cr = distance(x, y, centerRight);
    	int [] dis= {lt,ct,rt,cr,rb,cb,lb,cl};
    	int minPos = 0,minDis =dis[0];
    	for(int i=0;i<dis.length;i++){//查找离哪个点最近，作为按下的点
    		if(minDis > dis[i]){
    			minDis= dis[i];
    			minPos = i;
    		}
    	}
    	if(minPos ==0 ){
    		recFlag = Crop_Canvas.PRESS_LT;  
            returnFlag = true; 
    	}else if(minPos == 1){
    		recFlag = Crop_Canvas.PRESS_CT;  
            returnFlag = true; 
    	}else if(minPos == 2){
    		recFlag = Crop_Canvas.PRESS_RT;  
            returnFlag = true; 
    	}else if(minPos == 3){
    		recFlag = Crop_Canvas.PRESS_CR;  
            returnFlag = true; 
    	}else if(minPos == 4){
    		recFlag = Crop_Canvas.PRESS_RB;  
            returnFlag = true; 
    	}else if(minPos == 5){
    		recFlag = Crop_Canvas.PRESS_CB;  
            returnFlag = true; 
    	}else if(minPos == 6){
    		recFlag = Crop_Canvas.PRESS_LB;  
            returnFlag = true; 
    	}else{
    		recFlag = Crop_Canvas.PRESS_CL;  
            returnFlag = true; 
    	}
        return returnFlag;  
        
    }  
    public int distance(float x,float y,float[] area)
	{
    	float dx = x - area[0];
    	float dy = y - area[1];
    	return (int) Math.sqrt(dx * dx + dy *dy) ;
    }

    public boolean isInCircle(float x, float y, float[] circle)
	{
    	System.out.println(circle[0] + "===" + circle[1]);
        if(x >= circle[0]-20 && x <= circle[0]+20 && y >= circle[1]-20 && y <= circle[1]+20)
		{
            return true;  
        }  
        return false;  
    }  
    
	private void pressLT(float x, float y)
	{
		float dst[] = {leftTop[0]+x-startX,leftTop[1]+y-startY};
		if (dst[0] <boundRect.left) {
			leftTop[0] =boundRect.left;
		} else if( dst[0] > boundRect.right) {
			leftTop[0] = boundRect.right;
		}else{
			leftTop[0] = dst[0];
			centerLeft[0] = (float )(leftTop[0] + leftBottom[0])/2;
			centerTop[0] = (float )(leftTop[0] + rightTop[0]) /2;
		}
		if (dst[1] <  boundRect.top) {
			leftTop[1] = boundRect.top ;
		}  
		else if(dst[1] > boundRect.bottom){
			leftTop[1] = boundRect.bottom;
		}else{
			leftTop[1] = dst[1]; 
			centerLeft[1] = (float )(leftTop[1] + leftBottom[1])/2;
			centerTop [1] = (float )(leftTop[1] + rightTop[1]) /2;
		}
	}
      
	private void pressRT(float x, float y)
	{
		float dst[] = {rightTop[0]+x-startX,rightTop[1]+y-startY};
		if (dst[0]> boundRect.right) {
			rightTop[0] = boundRect.right;
		} else if( dst[0] < boundRect.left) {
			rightTop[0] = boundRect.left;
		}else {
			rightTop[0] = dst[0];
			centerTop [0] = (float )(leftTop[0] + rightTop[0]) /2;
			centerRight[0] = (float )(rightTop[0] + rightBottom[0]) /2;
		}
		if (dst[1] <  boundRect.top) {
			rightTop[1] =  boundRect.top ;
		} else  if(dst[1] > boundRect.bottom){
			rightTop[1] = boundRect.bottom;
		}else{
			rightTop[1] = dst[1];
			centerTop [1] = (float )(leftTop[1] + rightTop[1]) /2;
			centerRight[1] = (float )(rightTop[1] + rightBottom[1]) /2;
		}
	}

	private void pressRB(float x, float y)
	{
		float dst[] = {rightBottom[0]+x-startX,rightBottom[1]+y-startY};
		if (dst[0] > boundRect.right) {
			rightBottom[0] = boundRect.right;
		} else if(dst[0] < boundRect.left){
			rightBottom[0] = boundRect.left;
		}else{
			rightBottom[0] = dst[0];
			centerBottom [0] = (float )(leftBottom[0] + rightBottom[0]) /2;
			centerRight[0] = (float )(rightTop[0] + rightBottom[0]) /2;
		}
		if (dst[1] > boundRect.bottom) {
			rightBottom[1] = boundRect.bottom;
		}else if(dst[1] < boundRect.top){
			rightBottom[1] = boundRect.top;
		}else{
			rightBottom[1] = dst[1];
			centerBottom [1] =(float ) (leftBottom[1] + rightBottom[1]) /2;
			centerRight[1] = (float )(rightTop[1] + rightBottom[1]) /2;
		}
	} 
  
	private void pressLB(float x,float y){
		float dst[] = {leftBottom[0]+x-startX,leftBottom[1]+y-startY};
		System.out.println(x);
		if (dst[0] <boundRect.left) {
			leftBottom[0] = boundRect.left;
		} else if( dst[0] > boundRect.right) {
			leftBottom[0] = boundRect.right;
		}else{
			leftBottom[0] = dst[0];
			centerLeft[0] = (float )(leftTop[0] +leftBottom[0])/2;
			centerBottom[0] = (float )(leftBottom[0] +rightBottom[0])/2;
		}
		if (dst[1] > boundRect.bottom) {
			leftBottom[1] = boundRect.bottom;
		} else if(dst[1] < boundRect.top){
			leftBottom[1] = boundRect.top;
		}else{
			leftBottom[1] = dst[1];
			centerLeft[1] = (float )(leftTop[1] +leftBottom[1])/2;
			centerBottom[1] = (float )(leftBottom[1] +rightBottom[1])/2;
		}
	}

	private void pressCT(float x,float y){
		float dst[] = {centerTop[0]+x-startX,centerTop[1]+y-startY};
		System.out.println(x);
		if (dst[0] != width/2) {
			centerTop[0] = (float ) (leftTop[0] + rightTop[0])/2;
		} else {
			centerTop[0] = dst[0];
		}
		if (dst[1] < boundRect.top || dst[1] > boundRect.bottom) {
			centerTop[1] = (float )(leftTop[1]+rightTop[1])/2;
		}else{
			centerTop[1] = dst[1];
			//顶部中点移动时 左右的中点都会跟着移动
			leftTop[1] = dst[1];
			rightTop[1] = dst[1];
			
			centerLeft[1] = leftTop[1] + (float )(leftBottom[1] - leftTop[1])/2 ;
			centerRight[1] = rightTop[1] + (float) (rightBottom[1] - rightTop[1])/2;
		}
	}  
	private void pressCB(float x,float y){
		float dst[] = {centerBottom[0]+x-startX,centerBottom[1]+y-startY};
		System.out.println(x);
		if (dst[0] != width/2) {
			centerBottom[0] = (float ) (leftBottom[0]+rightBottom[0])/2;
		} else {
			centerBottom[0] = dst[0];
		}
		if (dst[1] > boundRect.bottom || dst[1] <boundRect.top) {
			centerBottom[1] = (float )(leftBottom[1]+rightBottom[1])/2;
		} else {
			centerBottom[1] = dst[1];
			//底部中点移动时 左右的中点都会跟着移动
			leftBottom[1] = dst[1];
			rightBottom[1] = dst[1];
			
			centerLeft[1] = leftBottom[1] - (float )(leftBottom[1] - leftTop[1])/2 ;
			centerRight[1] = rightBottom[1] - (float) (rightBottom[1] - rightTop[1])/2;
		}
	} 
	
	private void pressCL(float x,float y){
		float dst[] = {centerLeft[0]+x-startX,centerLeft[1]+y-startY};
		System.out.println(x);
		if (dst[0] < boundRect.left|| dst[0] > boundRect.right) {
			centerLeft[0] =  (float )(leftTop[0]+ leftBottom[0])/2;
		} else {
			centerLeft[0] = dst[0];
			//左边中点移动时 上下的中点都会跟着移动
			leftBottom[0] = dst[0];
			leftTop[0] = dst[0];
			centerBottom[0] = leftBottom[0] + (float) (rightBottom[0] - leftBottom[0])/2;
			centerTop[0] = leftTop[0] + (float)(rightTop[0]-leftTop[0])/2;
		}
		if (dst[1] != height/2  + translateHeight ) {
			centerLeft[1] = (float )(leftTop[1]+leftBottom[1])/2;
		} else {
			centerLeft[1] = dst[1];
		}
	}
	private void pressCR(float x,float y){
		float dst[] = {centerRight[0]+x-startX,centerRight[1]+y-startY};
		System.out.println(x);
		if (dst[0] < boundRect.left|| dst[0] > boundRect.right) {
			centerRight[0] = (float ) (rightTop[0] + rightBottom[0])/2;
		} else {
			centerRight[0] = dst[0];
			
			//右边边中点移动时 上下的中点都会跟着移动
			rightBottom[0] = dst[0];
			rightTop[0] = dst[0];
			centerBottom[0] = rightBottom[0] - (float) (rightBottom[0] - leftBottom[0])/2;
			centerTop[0] = rightTop[0] - (float)( rightTop[0] - leftTop[0])/2;
		}
		if (dst[1] != height/2  + translateHeight) {
			centerRight[1] =(float ) (rightTop[1]+rightBottom[1])/2;
		} else {
			centerRight[1] = dst[1];
		}
	}
	
	
	/** 
     * 比onDraw先执行 
     *  
     * 一个MeasureSpec封装了父布局传递给子布局的布局要求，每个MeasureSpec代表了一组宽度和高度的要求。 
     * 一个MeasureSpec由大小和模式组成 
     * 它有三种模式：UNSPECIFIED(未指定),父元素不对子元素施加任何束缚，子元素可以得到任意想要的大小; 
     *              EXACTLY(完全)，父元素决定子元素的确切大小，子元素将被限定在给定的边界里而忽略它本身大小； 
     *              AT_MOST(至多)，子元素至多达到指定大小的值。 
     *  
     * 　　它常用的三个函数： 　　 
     * 1.static int getMode(int measureSpec):根据提供的测量值(格式)提取模式(上述三个模式之一) 
     * 2.static int getSize(int measureSpec):根据提供的测量值(格式)提取大小值(这个大小也就是我们通常所说的大小)  
     * 3.static int makeMeasureSpec(int size,int mode):根据提供的大小值和模式创建一个测量值(格式) 
     */  
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int measureWidth = measureWidth(widthMeasureSpec);  
        int measureHeight = measureHeight(heightMeasureSpec); 
        canvasWidth = measureWidth;
        canvasHeight = measureHeight;
        LogUtil.i(canvasHeight+"-canvas------------------------"+canvasWidth);
        // 设置自定义的控件MyViewGroup的大小  
        setMeasuredDimension(measureWidth, measureHeight); 
	}

	private int measureWidth(int pWidthMeasureSpec)
	{
        int result = 0;  
        int widthMode = MeasureSpec.getMode(pWidthMeasureSpec);// 得到模式
        int widthSize = MeasureSpec.getSize(pWidthMeasureSpec);// 得到尺寸
  
        switch (widthMode) {  
        /** 
         * mode共有三种情况，取值分别为MeasureSpec.UNSPECIFIED, MeasureSpec.EXACTLY, 
         * MeasureSpec.AT_MOST。 
         *  
         *  
         * MeasureSpec.EXACTLY是精确尺寸， 
         * 当我们将控件的layout_width或layout_height指定为具体数值时如andorid 
         * :layout_width="50dip"，或者为FILL_PARENT是，都是控件大小已经确定的情况，都是精确尺寸。 
         *  
         *  
         * MeasureSpec.AT_MOST是最大尺寸， 
         * 当控件的layout_width或layout_height指定为WRAP_CONTENT时 
         * ，控件大小一般随着控件的子空间或内容进行变化，此时控件尺寸只要不超过父控件允许的最大尺寸即可 
         * 。因此，此时的mode是AT_MOST，size给出了父控件允许的最大尺寸。 
         *  
         *  
         * MeasureSpec.UNSPECIFIED是未指定尺寸，这种情况不多，一般都是父控件是AdapterView， 
         * 通过measure方法传入的模式。 
         */  
        case MeasureSpec.AT_MOST:
        case MeasureSpec.EXACTLY:
            result = widthSize;  
            break;  
        }  
        return result;  
    }  
  
    private int measureHeight(int pHeightMeasureSpec)
	{
        int result = 0;  
  
        int heightMode = MeasureSpec.getMode(pHeightMeasureSpec);
        int heightSize = MeasureSpec.getSize(pHeightMeasureSpec);
  
        switch (heightMode) {  
        case MeasureSpec.AT_MOST:
        case MeasureSpec.EXACTLY:
            result = heightSize;  
            break;  
        }  
        return result;  
    }  
    
	public void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		if (firstFlag) {
			firstFlag = false;
			imageScale();
		}else{
			if(rotateFlag){
				imageScale();
				rotateFlag = false;
			}
		}

		//printPointPos("print at onDraw");
		Path path = new Path();
		path.moveTo(leftTop[0], leftTop[1]);//起始点左上角
		path.lineTo(centerTop[0], centerTop[1]); //顶部中点
		path.lineTo(rightTop[0], rightTop[1]);//右上角顶点
		path.lineTo(centerRight[0], centerRight[1]);//右边中点
		path.lineTo(rightBottom[0], rightBottom[1]);//右下角顶点
		path.lineTo(centerBottom[0], centerBottom[1]);//底部中点
		path.lineTo(leftBottom[0], leftBottom[1]);	// 左下角顶点
		path.lineTo(centerLeft[0], centerLeft[1]);//左边中点
		path.close();//封闭
		canvas.drawPath(path, mPaint);
		
		
		canvas.drawCircle(leftTop[0], leftTop[1], RADIUS, mPaint);
		canvas.drawCircle(rightTop[0], rightTop[1], RADIUS, mPaint);
		canvas.drawCircle(rightBottom[0], rightBottom[1], RADIUS, mPaint);
		canvas.drawCircle(leftBottom[0], leftBottom[1], RADIUS, mPaint);
		
		canvas.drawCircle(centerTop[0], centerTop[1], RADIUS, mPaint);
		canvas.drawCircle(centerBottom[0], centerBottom[1], RADIUS, mPaint);
		canvas.drawCircle(centerLeft[0], centerLeft[1], RADIUS, mPaint);
		canvas.drawCircle(centerRight[0], centerRight[1], RADIUS, mPaint);
//		canvas.drawCircle(cx, cy, radius, paint);
	}  
      
}