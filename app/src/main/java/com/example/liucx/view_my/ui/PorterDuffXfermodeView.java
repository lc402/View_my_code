
package com.example.liucx.view_my.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.View;

import com.example.liucx.view_my.R;
import com.example.liucx.view_my.utils.UiUtils;

/**
 * @author tianjian
 * @created 2015/2/2
 */
public class PorterDuffXfermodeView extends View {

    private static final int WAVE_TRANS_SPEED = 4;

    private Paint mBitmapPaint, mPicPaint;
    private int mTotalWidth, mTotalHeight;
    private int mCenterX, mCenterY;
    private int mSpeed;

    private Bitmap mSrcBitmap;
    private Rect mSrcRect, mDestRect;

    private PorterDuffXfermode mPorterDuffXfermode;
    private Bitmap mMaskBitmap;
    private Rect mMaskSrcRect, mMaskDestRect;
    private PaintFlagsDrawFilter mDrawFilter;

    private int mCurrentPosition;

    public PorterDuffXfermodeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaint();
        initBitmap();
        mPorterDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
        mSpeed = UiUtils.dipToPx(context, WAVE_TRANS_SPEED);
        mDrawFilter = new PaintFlagsDrawFilter(Paint.ANTI_ALIAS_FLAG, Paint.DITHER_FLAG);
        new Thread() {
            public void run() {
                while (true) {
                    // ���ϸı���ƵĲ��˵�λ��
                    mCurrentPosition += mSpeed;
                    if (mCurrentPosition >= mSrcBitmap.getWidth()) {
                        mCurrentPosition = 0;
                    }
                    try {
                        // Ϊ�˱�֤Ч����ͬʱ�������ܽ�cpu�ճ���������������ʹ��
                        Thread.sleep(30);
                    } catch (InterruptedException e) {
                    }

                    postInvalidate();
                }

            };
        }.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // ��canvas����ȥ�����
        canvas.setDrawFilter(mDrawFilter);
        canvas.drawColor(Color.TRANSPARENT);

        /*
         * �����Ʋ������浽�µ�ͼ��
         */
        int sc = canvas.saveLayer(0, 0, mTotalWidth, mTotalHeight, null, Canvas.ALL_SAVE_FLAG);

        // �趨Ҫ���ƵĲ��Ʋ���
        mSrcRect.set(mCurrentPosition, 0, mCurrentPosition + mCenterX, mTotalHeight);
        // ���Ʋ��Ʋ���
        canvas.drawBitmap(mSrcBitmap, mSrcRect, mDestRect, mBitmapPaint);

        // ����ͼ��Ļ��ģʽ
        mBitmapPaint.setXfermode(mPorterDuffXfermode);
        // ��������Բ
        canvas.drawBitmap(mMaskBitmap, mMaskSrcRect, mMaskDestRect,
                mBitmapPaint);
        mBitmapPaint.setXfermode(null);
        canvas.restoreToCount(sc);
    }

    // ��ʼ��bitmap
    private void initBitmap() {
        mSrcBitmap = ((BitmapDrawable) getResources().getDrawable(R.mipmap.wave_2000))
                .getBitmap();
        mMaskBitmap = ((BitmapDrawable) getResources().getDrawable(
                R.mipmap.circle_500))
                .getBitmap();
    }

    // ��ʼ������paint
    private void initPaint() {

        mBitmapPaint = new Paint();
        // ������
        mBitmapPaint.setDither(true);
        // ����ͼ�����
        mBitmapPaint.setFilterBitmap(true);

        mPicPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPicPaint.setDither(true);
        mPicPaint.setColor(Color.RED);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mTotalWidth = w;
        mTotalHeight = h;
        mCenterX = mTotalWidth / 2;
        mCenterY = mTotalHeight / 2;

        mSrcRect = new Rect();
        mDestRect = new Rect(0, 0, mTotalWidth, mTotalHeight);

        int maskWidth = mMaskBitmap.getWidth();
        int maskHeight = mMaskBitmap.getHeight();
        mMaskSrcRect = new Rect(0, 0, maskWidth, maskHeight);
        mMaskDestRect = new Rect(0, 0, mTotalWidth, mTotalHeight);
    }

}
