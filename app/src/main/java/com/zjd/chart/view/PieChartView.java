package com.zjd.chart.view;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnticipateOvershootInterpolator;

import com.zjd.chart.util.DensityUtil;
import com.zjd.chart.util.Util;

import java.math.BigDecimal;

/**
 * Created by 左金栋 on 2017/8/19.
 */

public class PieChartView extends View {
    private Context context;

    private Paint paint,paintDown,paintRipple,paintNum,paintText,paintData,paintCenter,paintCenterText;
    private float value=0;

    private int width;//设置高
    private int height;//设置高

    private float rippleValue=0,downValue=0;
    private OnButtonClickListener mOnButtonClickListener;


    public PieChartView(Context context) {
        super(context);
        this.context=context;
        initPaint();
    }

    public PieChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        initPaint();
    }

    public PieChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context=context;
        initPaint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawSwipe(canvas);
        drawText(canvas);
        drawCenter(canvas);
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

        paint=new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeWidth(DensityUtil.dip2px(context,1.5f));
        setLayerType(LAYER_TYPE_SOFTWARE, paint);


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
        paintText.setColor(Color.BLACK);
        paintText.setStrokeWidth(DensityUtil.dip2px(context,1));

        paintCenterText=new Paint();
        paintCenterText.setAntiAlias(true);
        paintCenterText.setColor(Color.WHITE);
        paintCenterText.setStrokeWidth(DensityUtil.dip2px(context,1));

        paintData=new Paint();
        paintData.setAntiAlias(true);
        paintData.setStyle(Paint.Style.FILL_AND_STROKE);
        paintData.setColor(Color.BLUE);

        paintCenter=new Paint();
        paintCenter.setColor(0xffF9BD2B);
        paintCenter.setAntiAlias(true);
        paintCenter.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    /**
     * 画扇形
     * @param canvas
     */

    private int sum=0;
    private RectF rectF;
    //方向数据
    private float gravityX=4;
    private float gravityY=-4;
    private float gravityZ=4;

    private void drawSwipe(Canvas canvas) {
        if(sum==0||value==0.0f) return;
        rectF=new RectF(width/5,width/5,width*4/5,width*4/5);
        //根据重力数据画阴影
        paint.setShadowLayer(10,gravityX*shadowWidth,-gravityY*shadowWidth,Color.DKGRAY);
        for (int i = 0; i < randomNums.length; i++) {
            paint.setColor(colors[i]);
            float startAngle=value*360*getSum(i-1)/sum;
            float swipeAngle=value*360*randomNums[i]/sum;
            canvas.drawArc(rectF,startAngle,swipeAngle,true,paint);
        }
    }


    /**
     *百分比
     * @param canvas
     */
    private void drawText(Canvas canvas) {
        if(sum==0) return;
        paintText.setTextSize(width/30);
        paintText.setAlpha((int)(255*value));
        for (int i = 0; i < randomNums.length; i++) {
            Rect rect = new Rect();

            double f = (double)randomNums[i]/sum;
            BigDecimal b = new BigDecimal(f*100);
            double f1 = b.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
            String textPercent=(f1)+"%";
            paintText.getTextBounds(textPercent, 0, textPercent.length(), rect);
            int textWidth = rect.width();
            int textHeight = rect.height();
            float textX= (float) (width/2 + Math.sin(2*Math.PI/360*(90-360*(getSum(i-1)+randomNums[i]/2)/sum))*width*6/17-textWidth/2);
            float textY= (float) (width/2 + Math.cos(2*Math.PI/360*(90-360*(getSum(i-1)+randomNums[i]/2)/sum))*width*6/17+textHeight/2);
            canvas.drawText(textPercent,textX,textY,paintText);
        }
    }

    /**
     * 画中心
     * @param canvas
     */
    String textCenter="打开";

    private void drawCenter(Canvas canvas) {
        Rect rect = new Rect();
        paintCenter.setShadowLayer(10,gravityX*2,-gravityY*2,Color.DKGRAY);
        canvas.drawCircle(width/2,width/2,width/15,paintCenter);

        paintCenterText.setTextSize(width/30);
        paintCenterText.getTextBounds(textCenter, 0, textCenter.length(), rect);
        int textWidth = rect.width();
        int textHeight = rect.height();
        canvas.drawText(textCenter,width/2-textWidth/2,width/2+textHeight/2,paintCenterText);
    }

    /**
     * 按下水波纹特效
     * @param canvas
     */
    private void drawDownRipple(Canvas canvas) {
        paintDown.setAlpha((int)(255*(1-downValue/2)));
        Shader mShader = new RadialGradient(width/2,width/2,width/15,0x009F791D,0xFF9F791D,Shader.TileMode.CLAMP);
        paintDown.setShader(mShader);
        canvas.drawCircle(width/2,width/2,downValue*width/15,paintDown);
    }

    /**
     * 抬起水波纹特效
     * @param canvas
     */
    private void drawRipple(Canvas canvas) {
        paintRipple.setAlpha((int)(255*(1-rippleValue)));
        Shader mShader = new RadialGradient(width/2,width/2,width/30,0x009F791D,0xFF9F791D,Shader.TileMode.REPEAT);
        paintRipple.setShader(mShader);
        canvas.drawCircle(width/2,width/2,rippleValue*width/15,paintRipple);
    }

    /**
     * 获取数据总数
     * @return
     */
    private int getSum(int position) {
        int sum=0;
        for (int i = 0; i <= position; i++) {
            sum=sum+randomNums[i];
        }
        return sum;
    }

    int[] randomNums=new int[7];
    int[] colors=new int[7];
    private boolean isOpen=false;
    private ValueAnimator valueAnimatorOpen,valueAnimatorClose;

    public void startAmin(){
        isOpen=true;
        textCenter="关闭";
        //测试随机数
        randomNums=new int[]{
                (int) (Math.random()*101),
                (int) (Math.random()*101),
                (int) (Math.random()*101),
                (int) (Math.random()*101),
                (int) (Math.random()*101),
                (int) (Math.random()*101),
                (int) (Math.random()*101)};

        colors=new int[]{
                Color.rgb((int)(Math.random()*256),(int)(Math.random()*256),(int)(Math.random()*256)),
                Color.rgb((int)(Math.random()*256),(int)(Math.random()*256),(int)(Math.random()*256)),
                Color.rgb((int)(Math.random()*256),(int)(Math.random()*256),(int)(Math.random()*256)),
                Color.rgb((int)(Math.random()*256),(int)(Math.random()*256),(int)(Math.random()*256)),
                Color.rgb((int)(Math.random()*256),(int)(Math.random()*256),(int)(Math.random()*256)),
                Color.rgb((int)(Math.random()*256),(int)(Math.random()*256),(int)(Math.random()*256)),
                Color.rgb((int)(Math.random()*256),(int)(Math.random()*256),(int)(Math.random()*256))};

        sum=getSum(randomNums.length-1);

        valueAnimatorOpen=ValueAnimator.ofFloat(0f,1f);
        valueAnimatorOpen.setDuration(1000);
        valueAnimatorOpen.setInterpolator(new AnticipateOvershootInterpolator());
        valueAnimatorOpen.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                value= (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        valueAnimatorOpen.start();
    }

    /**
     * 关闭动画
     */
    public void closeAmin(){
        isOpen=false;
        textCenter="打开";
        valueAnimatorClose=ValueAnimator.ofFloat(1f,0f);
        valueAnimatorClose.setDuration(1000);
        valueAnimatorClose.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                value= (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        valueAnimatorClose.start();
    }



    /**
     * 手指按下动画
     */
    ValueAnimator valueAnimatorDowm;

    private void downAnim() {
        valueAnimatorDowm=ValueAnimator.ofFloat(0f,1f);
        valueAnimatorDowm.setDuration(300);
        valueAnimatorDowm.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                downValue= (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        valueAnimatorDowm.start();
    }

    /**
     * 手指松开动画
     */
    public void rippleAmin(){
        if(valueAnimatorDowm.isRunning()){
            valueAnimatorDowm.cancel();
        }
        downValue=2;

        if(isOpen){
            closeAmin();
        }else {
            startAmin();
        }

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

    private boolean isClick=true;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                //获取屏幕上点击的坐标
                float downX = event.getX();
                float downY = event.getY();

                for (int i = 0; i < randomNums.length; i++) {
                    if(Util.getDistance(downX,downY,width/2,width/2) < width/15){
                        if(valueAnimatorOpen!=null&&valueAnimatorOpen.isRunning()||valueAnimatorClose!=null&&valueAnimatorClose.isRunning()){
                            return false;
                        }
                        downAnim();
                        isClick=true;
                        return true;
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                float moveX=event.getX();
                float moveY=event.getY();
                if(Util.getDistance(moveX,moveY,width/2,width/2)>width/15){
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
                if(isClick){
//                    mOnButtonClickListener.onButtonClick(clickPosition,randomNums[clickPosition]);
                    rippleAmin();
                }
                return true;
        }
        return super.onTouchEvent(event);
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

    //点击
    public interface OnButtonClickListener{
        void onButtonClick(int position, int number);
    }

    public void setOnButtonClickListener(OnButtonClickListener listener){
        this.mOnButtonClickListener=listener;
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
        if(value <= 100/2){
            fraction = (float)value/(100/2);
            color = (int) evealuator.evaluate(fraction,0xFFFF0000,0xffF9BD2B); //由红到橙
        }else{
            fraction = ( (float)value-100/2 ) / (100/2);
            color = (int) evealuator.evaluate(fraction,0xffF9BD2B,0xFF3DBB2B); //由橙到绿
        }
        return color;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getDefaultSize(getMeasuredWidth(), widthMeasureSpec);// 获得控件的宽度
        height = getDefaultSize(getMeasuredHeight(), heightMeasureSpec);//获得控件的高度
        setMeasuredDimension(width, height);//设置宽和高
    }
}
