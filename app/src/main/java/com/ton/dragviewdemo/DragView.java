package com.ton.dragviewdemo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.InputStream;


/**
 * Created by ton on 17/5/18.
 * 悬浮可拖拽View
 */

public class DragView extends View {

    private static final String TAG = "DragView";


    private int WIDTH = 160;
    private int HEIGHT = 160;
    private int heigh, width;
    private Rect rect = new Rect(0, 0, WIDTH, WIDTH);//绘制矩形的区域
    private int deltaX, deltaY;//点击位置和图形边界的偏移量
    private static Paint paint = new Paint();//画笔
    private Bitmap mBitmap = null;
    private Bitmap mBitmap2 = null;
    private String imgUrl;
    private String imgUrl2;
    private OnClickListener mClickListener;
    private int mStartX, mStartY;

    private Context mContext;
    private Handler mHandler;

    private boolean isAside = true;

    private int DELAY_TIME = 8000;

    public DragView(Context context, AttributeSet attrs) {
        super(context, attrs);
        WIDTH = 137;
        HEIGHT = 183;
        paint = new Paint();
        mContext = context;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(widthSize, heightSize);
        width = widthSize;
        heigh = heightSize;
        rect = new Rect(width - WIDTH, heigh / 2 - HEIGHT / 2, width, heigh / 2 + HEIGHT / 2);//绘制矩形的区域
    }

    /**
     * Implement this to do your drawing.
     *
     * @param canvas the canvas on which the background will be drawn
     */
    @Override
    protected void onDraw(final Canvas canvas) {

        if (isAside) {
            //绘制aside图像
            if (mBitmap2 != null) {
                Rect rectF = new Rect();
                rectF.set(0, 0, mBitmap2.getWidth(), mBitmap2.getHeight());
                canvas.drawBitmap(mBitmap2, rectF, rect, paint);
            } else {

                //绘制默认
                @SuppressLint("ResourceType") InputStream is =
                        getContext().getResources().openRawResource(R.mipmap.dragon_label);
                Bitmap bmp = BitmapFactory.decodeStream(is);
                //图片重新裁剪，原图从中心点按显示大小裁剪
                int bw = bmp.getWidth(), bh = bmp.getHeight();
                int w = WIDTH, h = HEIGHT;

                // 计算缩放比例
                float scaleWidth = ((float) w) / bw;
                float scaleHeight = ((float) h) / bh;
                // 取得想要缩放的matrix参数
                Matrix matrix = new Matrix();
                matrix.postScale(scaleWidth, scaleHeight);
                // 得到新的图片
                Bitmap defaultAsideBmp = Bitmap.createBitmap(bmp, 0, 0, bw, bh, matrix,
                        true);

                Rect rectF = new Rect();
                rectF.set(0, 0, defaultAsideBmp.getWidth(), defaultAsideBmp.getHeight());
                canvas.drawBitmap(defaultAsideBmp, rectF, rect, paint);

            }

        } else {
            //绘制offside图像
            if (mBitmap != null) {
                Rect rectF = new Rect();
                rectF.set(0, 0, mBitmap.getWidth(), mBitmap.getHeight());
                canvas.drawBitmap(mBitmap, rectF, rect, paint);
            } else {

                //绘制默认
                @SuppressLint("ResourceType") InputStream is =
                        getContext().getResources().openRawResource(R.mipmap.dragon);
                Bitmap bmp = BitmapFactory.decodeStream(is);
                //图片重新裁剪，原图从中心点按显示大小裁剪
                int bw = bmp.getWidth(), bh = bmp.getHeight();
                int w = WIDTH, h = HEIGHT;

                // 计算缩放比例
                float scaleWidth = ((float) w) / bw;
                float scaleHeight = ((float) h) / bh;
                // 取得想要缩放的matrix参数
                Matrix matrix = new Matrix();
                matrix.postScale(scaleWidth, scaleHeight);
                // 得到新的图片
                Bitmap defaultOffsideBmp = Bitmap.createBitmap(bmp, 0, 0, bw, bh, matrix,
                        true);

                Rect rectF = new Rect();
                rectF.set(0, 0, defaultOffsideBmp.getWidth(), defaultOffsideBmp.getHeight());
                canvas.drawBitmap(defaultOffsideBmp, rectF, rect, paint);


            }

        }

        //判断是否靠边
        if (rect.left == width - WIDTH) {

            if (null == mHandler) {
                mHandler = getHandler();
            }
            //先取消之前的asiderunnable，postdelay一个靠边的runnable
            mHandler.removeCallbacks(runnableAside);
            mHandler.postDelayed(runnableAside, DELAY_TIME);

        } else {
            if (null != mHandler) {
                //取消靠边runable，设置aside为false
                mHandler.removeCallbacks(runnableAside);
                isAside = false;
            }
        }
    }


    //Aside的runnable
    private Runnable runnableAside = new Runnable() {
        @Override
        public void run() {
            isAside = true;
            Rect oldl = new Rect(rect);
            oldl.union(rect);
            invalidate(oldl);
        }
    };


    //offside的runnable
    private Runnable runnableOffside = new Runnable() {
        @Override
        public void run() {
            isAside = false;
            Rect oldl = new Rect(rect);
            oldl.union(rect);
            invalidate(oldl);
        }
    };


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!rect.contains(x, y)) {
                    return false;//没有在矩形上点击，不处理触摸消息
                }
                mStartX = x;
                mStartY = y;
                deltaX = x - rect.left;
                deltaY = y - rect.top;
                break;
            case MotionEvent.ACTION_MOVE:
                Rect old = new Rect(rect);
                //更新矩形的位置
                rect.left = x - deltaX;
                if (rect.left < 0) {
                    rect.left = 0;
                }
                rect.top = y - deltaY;
                if (rect.top < 0) {
                    rect.top = 0;
                }
                rect.right = rect.left + WIDTH;
                if (rect.right > width) {
                    rect.right = width;
                    rect.left = width - WIDTH;
                }
                rect.bottom = rect.top + HEIGHT;
                if (rect.bottom > heigh) {
                    rect.bottom = heigh;
                    rect.top = heigh - HEIGHT;
                }
                old.union(rect);//要刷新的区域，求新矩形区域与旧矩形区域的并集
                invalidate(old);//出于效率考虑，设定脏区域，只进行局部刷新，不是刷新整个view
                break;
            case MotionEvent.ACTION_UP:
                Rect oldl = new Rect(rect);
                //更新矩形的位置
//                if (rect.left + WIDTH / 2 < width / 2){
//                    rect.left = 0;
//                }else {
//                    rect.left = width - WIDTH;
//                }
                //TODO 始终右侧停靠
                rect.left = width - WIDTH;

                rect.top = y - deltaY;
                if (rect.top < 0) {
                    rect.top = 0;
                }
                rect.right = rect.left + WIDTH;
                rect.bottom = rect.top + HEIGHT;
                if (rect.bottom > heigh) {
                    rect.bottom = heigh;
                    rect.top = heigh - HEIGHT;
                }
                oldl.union(rect);
                invalidate(oldl);

                if (Math.abs(mStartX - x) < 10
                        && Math.abs(y - mStartY) < 10) {//捕捉点击事件

                    //过滤靠边后第一次点击
                    if(isAside){

                        if (null == mHandler) {
                            mHandler = getHandler();
                        }
                        //发起一次offside的runnable
                        mHandler.post(runnableOffside);

                    }else{
                        if (mClickListener != null) {
                            mClickListener.onClick(this);
                        }
                    }

                }
                break;
        }
        return true;//处理了触摸消息，消息不再传递
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        this.mClickListener = l;
    }

    /***
     * 加载资源图片
     * @param resId
     */
    public void setImageResource(int resId, int resId2) {

        try {
            //TODO mBitmap
            InputStream is = getContext().getResources().openRawResource(resId);
            Bitmap bmp = BitmapFactory.decodeStream(is);
            //图片重新裁剪，原图从中心点按显示大小裁剪
            int bw = bmp.getWidth(), bh = bmp.getHeight();
            int w = WIDTH, h = HEIGHT;

            // 计算缩放比例
            float scaleWidth = ((float) w) / bw;
            float scaleHeight = ((float) h) / bh;
            // 取得想要缩放的matrix参数
            Matrix matrix = new Matrix();
            matrix.postScale(scaleWidth, scaleHeight);
            // 得到新的图片
            mBitmap = Bitmap.createBitmap(bmp, 0, 0, bw, bh, matrix,
                    true);


            //TODO mBitmap2
            InputStream is2 = getContext().getResources().openRawResource(resId2);
            Bitmap bmp2 = BitmapFactory.decodeStream(is2);
            //图片重新裁剪，原图从中心点按显示大小裁剪
            int bw2 = bmp2.getWidth(), bh2 = bmp2.getHeight();
            int w2 = WIDTH, h2 = HEIGHT;

            // 计算缩放比例
            float scaleWidth2 = ((float) w2) / bw2;
            float scaleHeight2 = ((float) h2) / bh2;
            // 取得想要缩放的matrix参数
            Matrix matrix2 = new Matrix();
            matrix2.postScale(scaleWidth2, scaleHeight2);
            // 得到新的图片
            mBitmap2 = Bitmap.createBitmap(bmp2, 0, 0, bw2, bh2, matrix2,
                    true);


            invalidate();
        } catch (Exception e) {

            Log.e(TAG, e.getLocalizedMessage());
            Toast.makeText(mContext, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();

        }

    }

    /****
     * 使用网络图片
     * @param imageUrl
     */
    public void setImageUrl(String imageUrl, String imageUrl2) {

        try {

            if (imageUrl.startsWith("http://") || imageUrl.startsWith("https://")) {
                imgUrl = imageUrl;
            } else {
                Log.d(TAG, "image url error !");
                return;
            }

            if (imageUrl2.startsWith("http://") || imageUrl2.startsWith("https://")) {
                imgUrl2 = imageUrl2;
            } else {
                Log.d(TAG, "image url error !");
                return;
            }


            //TODO mBitmap
            if (mBitmap == null && imgUrl.length() > 0) {
                ImageLoader.getInstance().loadImage(imgUrl, new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String s, View view) {

                    }

                    @Override
                    public void onLoadingFailed(String s, View view, FailReason failReason) {

                    }

                    @Override
                    public void onLoadingComplete(String s, View view, Bitmap bitmap) {


                        int bw = bitmap.getWidth(), bh = bitmap.getHeight();
                        int w = WIDTH, h = HEIGHT;

                        // 计算缩放比例
                        float scaleWidth = ((float) w) / bw;
                        float scaleHeight = ((float) h) / bh;
                        // 取得想要缩放的matrix参数
                        Matrix matrix = new Matrix();
                        matrix.postScale(scaleWidth, scaleHeight);
                        // 得到新的图片
                        mBitmap = Bitmap.createBitmap(bitmap, 0, 0, bw, bh, matrix,
                                true);

                        invalidate();
                    }

                    @Override
                    public void onLoadingCancelled(String s, View view) {

                    }
                });
            }

            //TODO
            if (mBitmap2 == null && imgUrl2.length() > 0) {
                ImageLoader.getInstance().loadImage(imgUrl2, new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String s, View view) {

                    }

                    @Override
                    public void onLoadingFailed(String s, View view, FailReason failReason) {

                    }

                    @Override
                    public void onLoadingComplete(String s, View view, Bitmap bitmap) {


                        int bw = bitmap.getWidth(), bh = bitmap.getHeight();
                        int w = WIDTH, h = HEIGHT;

                        // 计算缩放比例
                        float scaleWidth = ((float) w) / bw;
                        float scaleHeight = ((float) h) / bh;
                        // 取得想要缩放的matrix参数
                        Matrix matrix = new Matrix();
                        matrix.postScale(scaleWidth, scaleHeight);
                        // 得到新的图片
                        mBitmap2 = Bitmap.createBitmap(bitmap, 0, 0, bw, bh, matrix,
                                true);

                        invalidate();
                    }

                    @Override
                    public void onLoadingCancelled(String s, View view) {

                    }
                });
            }


        } catch (Exception e) {
            Log.e(TAG, e.getLocalizedMessage());
            Toast.makeText(mContext, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        }

    }
}
