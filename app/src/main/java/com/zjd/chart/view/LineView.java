package com.zjd.chart.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.BounceInterpolator;

import com.zjd.chart.util.DensityUtil;
import com.zjd.chart.util.Util;

/**
 * Created by 左金栋 on 2017/8/17.
 */

public class LineView extends View {
    private Context context;
    private int defaultColor= Color.BLACK;
    private Paint paint,paintDown,paintRipple,paintNum,paintText,paintData;
    private float value=0;
    private float rippleValue=0,downValue=0;
    private int textSpace=0;
    private OnPointClickListener mOnPointClickListener;

    private int width;//设置高
    private int height;//设置高

    //方向数据
    private float gravityX=4;
    private float gravityY=-4;
    private float gravityZ=4;

    private String[] textVertical ={"100","200","300","400"};
    private String[] textHorizontal ={"0","1","2","3","4","5","6","7"};

    public LineView(Context context) {
        super(context);
        this.context=context;
        initPaint();
    }

    public LineView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        initPaint();
    }

    public LineView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context=context;
        initPaint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawData(canvas);
        drawAxes(canvas,paint);
        drawText(canvas);
        drawNum(canvas);
        drawDownRipple(canvas);
        drawRipple(canvas);
    }

    /**
     * 初始化画笔
     */
    int shadowWidth;
    private void initPaint() {
        shadowWidth=DensityUtil.dip2px(context,1);
        gravityX=shadowWidth*2;
        gravityY=-shadowWidth*2;
        gravityZ=shadowWidth*2;
        textSpace=DensityUtil.dip2px(context,5);
        
        paint=new Paint();
        paint.setColor(defaultColor);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(DensityUtil.dip2px(context,1.5f));

        paintDown=new Paint();
        paintDown.setAntiAlias(true);
        paintDown.setStrokeWidth(DensityUtil.dip2px(context,15));
        paintDown.setStyle(Paint.Style.FILL);

        paintRipple=new Paint();
        paintRipple.setStrokeWidth(DensityUtil.dip2px(context,15));
        paintRipple.setStyle(Paint.Style.FILL);
        paintRipple.setAntiAlias(true);

        paintNum=new Paint();
        paintNum.setAntiAlias(true);
        paintNum.setColor(Color.RED);
        paintNum.setTextSize(20);
        paintNum.setStrokeWidth(DensityUtil.dip2px(context,1));

        paintText=new Paint();
        paintText.setAntiAlias(true);
        paintText.setColor(Color.DKGRAY);
        paintText.setStrokeWidth(DensityUtil.dip2px(context,1));

        paintData=new Paint();
        paintData.setAntiAlias(true);
        paintData.setStyle(Paint.Style.STROKE);
        paintData.setStrokeWidth(DensityUtil.dip2px(context,3));
        paintData.setColor(Color.BLUE);
        setLayerType(LAYER_TYPE_SOFTWARE, paintData);
    }


    /**
     * 按下水波纹特效
     * @param canvas
     */
    private void drawDownRipple(Canvas canvas) {
        paintDown.setAlpha((int)(255*(1-downValue/2)));
        Shader mShader = new RadialGradient(width*(clickPosition+2)/10,width/2-randomNums[clickPosition]*width/1000,width/22,0x00878787,Color.GRAY,Shader.TileMode.CLAMP);
        paintDown.setShader(mShader);
        canvas.drawCircle(width*(clickPosition+2)/10,width/2-randomNums[clickPosition]*width/1000,downValue*width/22,paintDown);
    }

    /**
     * 抬起水波纹特效
     * @param canvas
     */
    private void drawRipple(Canvas canvas) {
        paintRipple.setAlpha((int)(255*(1-rippleValue)));
        Shader mShader = new RadialGradient(width*(clickPosition+2)/10,width/2-randomNums[clickPosition]*width/1000,width/22,0x00878787,Color.DKGRAY,Shader.TileMode.REPEAT);
        paintRipple.setShader(mShader);
        canvas.drawCircle(width*(clickPosition+2)/10,width/2-randomNums[clickPosition]*width/1000,rippleValue*width/22,paintRipple);
    }

    /**
     * 点击事件区域
     * @param canvas
     * @param paint
     */
    private void drawTouch(Canvas canvas, Paint paint) {
        for (int i = 0; i < randomNums.length; i++) {
            canvas.drawCircle(width*(i+2)/10,width/2-randomNums[i]*width/1000,width/22,paint);
        }
    }

    /**
     * 动态数值
     * @param canvas
     */
    private void drawNum(Canvas canvas) {
        paintNum.setTextSize(width/30);
        if(value==0) return;
        for (int i = 0; i < randomNums.length; i++) {
            Rect rect = new Rect();
            paintNum.getTextBounds((int)(value*randomNums[i])+"", 0, String.valueOf((int)(value*randomNums[i])).length(), rect);
            int textWidth = rect.width();
            int textHeight = rect.height();
            canvas.drawText((int)(value*randomNums[i])+"",width*(i+2)/10-textWidth/2, width/2-value*randomNums[i]*width/1000-textHeight/2,paintNum);
        }
    }

    /**
     * 坐标轴汉字
     * @param canvas
     */
    private void drawText(Canvas canvas) {
        paintText.setTextSize(width/30);
        //横轴
        for (int i = 0; i < textHorizontal.length; i++) {
            Rect rect = new Rect();
            paintText.getTextBounds(textHorizontal[i], 0, textHorizontal[i].length(), rect);
            int textWidth = rect.width();
            int textHeight = rect.height();
            canvas.drawText(textHorizontal[i],width/10+i*width/10-textWidth/2,width/2+textHeight+textSpace,paintText);
        }

        //纵轴
        for (int i = 0; i < textVertical.length; i++) {
            Rect rect = new Rect();
            paintText.getTextBounds(textVertical[i], 0, textVertical[i].length(), rect);
            int textWidth = rect.width();
            int textHeight = rect.height();
            canvas.drawText(textVertical[i],width/10-textWidth-textSpace,width/2-(i+1)*width/10+textHeight/2,paintText);
        }
    }

    /**
     * 画数据
     * @param canvas
     */
    private void drawData(Canvas canvas) {
        if(value==0) return;
        //根据重力数据画阴影
        paintData.setShadowLayer(5,gravityX*shadowWidth,-gravityY*shadowWidth,Color.LTGRAY);

        Path pathData=new Path();
        pathData.moveTo(width/10, width/2);
        for (int i = 0; i < randomNums.length; i++) {
            pathData.lineTo(width*(i+2)/10, width/2-value*randomNums[i]*width/1000);
        }
        canvas.drawPath(pathData, paintData);
    }

    /**
     * 画坐标轴
     * @param canvas
     * @param paint
     */
    private void drawAxes(Canvas canvas, Paint paint) {
        //坐标轴
        Path path = new Path();//三角形
        path.moveTo(width/10, width/20);
        path.lineTo(width/10, width/2);
        path.lineTo(width*9/10, width/2);
        canvas.drawPath(path, paint);

        //向上箭头
        Path pathArrowTop=new Path();
        pathArrowTop.moveTo(width/10-width/60,width/20+width/60);
        pathArrowTop.lineTo(width/10, width/20);
        pathArrowTop.lineTo(width/10+width/60, width/20+width/60);
        canvas.drawPath(pathArrowTop, paint);

        //向下箭头
        Path pathArrowRight=new Path();
        pathArrowRight.moveTo(width*9/10-width/60, width/2-width/60);
        pathArrowRight.lineTo(width*9/10, width/2);
        pathArrowRight.lineTo(width*9/10-width/60, width/2+width/60);
        canvas.drawPath(pathArrowRight, paint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getDefaultSize(getMeasuredWidth(), widthMeasureSpec);// 获得控件的宽度
        height = getDefaultSize(getMeasuredHeight(), heightMeasureSpec);//获得控件的高度
        setMeasuredDimension(width, height);//设置宽和高
    }

    int[] randomNums=new int[7];

    public void startAmin(){
        //测试随机数
        randomNums=new int[]{
                (int) (Math.random()*401),
                (int) (Math.random()*401),
                (int) (Math.random()*401),
                (int) (Math.random()*401),
                (int) (Math.random()*401),
                (int) (Math.random()*401),
                (int) (Math.random()*401)};
        ValueAnimator valueAnimator=ValueAnimator.ofFloat(0f,1f);
        valueAnimator.setDuration(800);
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                value= (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        valueAnimator.start();
    }

    /**
     * 手指松开动画
     */
    public void rippleAmin(){
        if(valueAnimatorDowm.isRunning()){
            valueAnimatorDowm.cancel();
        }
        downValue=2;

        ValueAnimator valueAnimator=ValueAnimator.ofFloat(0f,1f);
        valueAnimator.setDuration(300);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                rippleValue= (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        valueAnimator.start();
    }

    /**
     * 手指按下动画
     */
    ValueAnimator valueAnimatorDowm;

    private void downAnim() {
        valueAnimatorDowm=ValueAnimator.ofFloat(0f,1f);
        valueAnimatorDowm.setDuration(150);
        valueAnimatorDowm.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                downValue= (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        valueAnimatorDowm.start();
    }

    private int clickPosition=0;
    private float downX=0;
    private float downY=0;
    private boolean isClick=true;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                //获取屏幕上点击的坐标
                downX = event.getX();
                downY = event.getY();
                for (int i = 0; i < randomNums.length; i++) {
                    if(Util.getDistance(downX,downY,width*(i+2)/10,width/2-randomNums[i]*width/1000)<width/22){
                        clickPosition=i;
                        isClick=true;
                        downAnim();
                        return true;
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                float moveX=event.getX();
                float moveY=event.getY();
                if(Util.getDistance(moveX,moveY,downX,downY)>width/15){
                    if(valueAnimatorDowm.isRunning()){
                        valueAnimatorDowm.cancel();
                    }
                    downValue=2;
                    isClick=false;
                    invalidate();
                    return false;
                }
                break;
            case MotionEvent.ACTION_UP:
                if(mOnPointClickListener!=null&&isClick){
                    mOnPointClickListener.onPointClick(clickPosition,randomNums[clickPosition]);
                    rippleAmin();
                }
                return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void setOnTouchListener(OnTouchListener l) {
        super.setOnTouchListener(l);
    }

    //点击
    public interface OnPointClickListener{
        void onPointClick(int position, int number);
    }

    public void setOnPointClickListener(OnPointClickListener listener){
        this.mOnPointClickListener=listener;
    }

    /**
     * 获取手机方向
     * @param x
     * @param y
     * @param z
     */
    public void setGravity(float x,float y,float z){
        if(Math.abs(x-gravityX)>0.1||Math.abs(y-gravityY)>0.1||Math.abs(z-gravityZ)>0.1){
            gravityX=x;
            gravityY=y;
            gravityZ=z;
            invalidate();
        }
    }
}
