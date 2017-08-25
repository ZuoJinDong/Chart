package com.zjd.chart.view;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.Shader;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.annotation.Nullable;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.CycleInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.PathInterpolator;

import com.zjd.chart.util.DensityUtil;
import com.zjd.chart.util.Util;

import static android.content.Context.SENSOR_SERVICE;

/**
 * Created by 左金栋 on 2017/8/17.
 */

public class BarChartView extends View{
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

    public BarChartView(Context context) {
        super(context);
        this.context=context;
        initPaint();
    }

    public BarChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        initPaint();
    }

    public BarChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context=context;
        initPaint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawAxes(canvas,paint);
        drawText(canvas);
        drawData(canvas);
        drawNum(canvas);
        drawDownRipple(canvas);
        drawRipple(canvas);
    }

    /**
     * 初始化画笔
     */
    int shadowWidth;
    private void initPaint() {
        textSpace=DensityUtil.dip2px(context,5);
        shadowWidth=DensityUtil.dip2px(context,1);
        gravityX=shadowWidth*2;
        gravityY=-shadowWidth*2;
        gravityZ=shadowWidth*2;

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
        paintData.setStyle(Paint.Style.FILL_AND_STROKE);
        paintData.setColor(Color.BLUE);
        setLayerType(LAYER_TYPE_SOFTWARE, paintData);
    }


    /**
     * 按下水波纹特效
     * @param canvas
     */
    private void drawDownRipple(Canvas canvas) {
        paintDown.setAlpha((int)(255*(1-downValue/2)));
        Shader mShader = new RadialGradient(moveX,moveY,width/25,0x00000000,0xff000000,Shader.TileMode.CLAMP);
        paintDown.setShader(mShader);
        canvas.drawCircle(moveX,moveY,downValue*width/25,paintDown);
    }

    /**
     * 抬起水波纹特效
     * @param canvas
     */
    private void drawRipple(Canvas canvas) {
        paintRipple.setAlpha((int)(255*(1-rippleValue)));
        Shader mShader = new RadialGradient(downX,downY,100,0x00000000,0xff000000,Shader.TileMode.REPEAT);
        paintRipple.setShader(mShader);
        canvas.drawCircle(moveX,moveY,rippleValue*width/22,paintRipple);
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
        //根据重力数据画阴影
        paintData.setShadowLayer(10,gravityX*shadowWidth,-gravityY*shadowWidth,Color.DKGRAY);
        for (int i = 0; i < randomNums.length; i++) {
            paintData.setColor(calculateColor((int)(value*randomNums[i])));
            canvas.drawRect(width*(i+2)/10-width/30,width/2-value*randomNums[i]*width/1000,width*(i+2)/10+width/30,width/2,paintData);
        }
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
        valueAnimator.setDuration(1000);
        valueAnimator.setInterpolator(new AnticipateOvershootInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                value= (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        valueAnimator.start();
    }

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
    private float downX=0,downY=0;
    private float moveX=0,moveY=0;
    private boolean isClick=true;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                //获取屏幕上点击的坐标
                downX = event.getX();
                downY = event.getY();
                moveX=downX;
                moveY=downY;

                for (int i = 0; i < randomNums.length; i++) {
                    int num=randomNums[i]<30?30:randomNums[i];
                    if(Util.isInside(downX,downY,width*(i+2)/10-width/30,width/2-value*num*width/1000,width*(i+2)/10+width/30,width/2)){
                        clickPosition=i;
                        isClick=true;
                        downAnim();
                        return true;
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                moveX=event.getX();
                moveY=event.getY();
                int num=randomNums[clickPosition]<30?30:randomNums[clickPosition];
                if(Util.isInside(moveX,moveY,width*(clickPosition+2)/10-width/30,width/2-value*num*width/1000,width*(clickPosition+2)/10+width/30,width/2)){
                    invalidate();
                }else {
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
            Log.e("setGravity","==========X:"+x+",Y:"+y+",Z:"+z);
            invalidate();
        }
    }

    /**
     * 获得渐变色
     * @param value
     * @return
     */
    private int calculateColor(int value){
        ArgbEvaluator evealuator = new ArgbEvaluator();
        float fraction;
        int color;
        if(value <= 400/2){
            fraction = (float)value/(400/2);
            color = (int) evealuator.evaluate(fraction,0xFFFF0000,0xffF9BD2B); //由红到橙
        }else{
            fraction = ( (float)value-400/2 ) / (400/2);
            color = (int) evealuator.evaluate(fraction,0xffF9BD2B,0xFF3DBB2B); //由橙到绿
        }
        return color;
    }

}
