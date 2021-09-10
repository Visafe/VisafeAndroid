package vn.ncsc.visafe.ui.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import vn.ncsc.visafe.R;

public class CornerProgressBar extends View {

    private final Paint progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final Path mPath = new Path();

    private float mPhase;
    private final float[] mIntervals = {0, 0};

    private int mStrokeWidth = 5;
    private boolean shouldShowBackground = true;
    private int mCornerRadius = 10;

    public CornerProgressBar(Context context) {
        super(context);
        init(context, null);
    }

    public CornerProgressBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CornerProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        int progressColor;
        int backgroundColor;

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SquareProgressBar);
            progressColor = a.getColor(R.styleable.SquareProgressBar_spb_progressColor, Color.parseColor("#FB2971"));
            backgroundColor = a.getColor(R.styleable.SquareProgressBar_spb_backgroundColor, Color.parseColor("#DFDFDF"));
            mStrokeWidth = a.getDimensionPixelSize(R.styleable.SquareProgressBar_spb_progressWidth, 5);
            mCornerRadius = a.getDimensionPixelSize(R.styleable.SquareProgressBar_spb_cornerRadius, 10);
            shouldShowBackground = a.getBoolean(R.styleable.SquareProgressBar_spb_showBackground, true);

            a.recycle();
        } else {
            progressColor = Color.parseColor("#FB2971");
            backgroundColor = Color.parseColor("#DFDFDF");
        }

        progressPaint.setColor(progressColor);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(mStrokeWidth + 1);

        backgroundPaint.setColor(backgroundColor);
        backgroundPaint.setStyle(Paint.Style.STROKE);
        backgroundPaint.setStrokeWidth(mStrokeWidth);

        PathEffect effect = new DashPathEffect(mIntervals, mPhase - mPhase * 1);
        backgroundPaint.setPathEffect(effect);
        PathEffect effect1 = new DashPathEffect(mIntervals, mPhase - mPhase * 1);
        progressPaint.setPathEffect(effect1);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mPath.reset();
        RectF rect = new RectF(0, 0, w, h);
        float inset = progressPaint.getStrokeWidth();
        rect.inset(inset, inset);

        mPath.addRoundRect(rect, mCornerRadius, mCornerRadius, Path.Direction.CCW);
        mPhase = new PathMeasure(mPath, false).getLength();
        mIntervals[0] = mIntervals[1] = mPhase;
        PathEffect effect = new DashPathEffect(mIntervals, mPhase);
        progressPaint.setPathEffect(effect);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (shouldShowBackground) {
            canvas.drawPath(mPath, backgroundPaint);
        }
        canvas.drawPath(mPath, progressPaint);
    }

    public void setProgressColor(int color) {
        progressPaint.setColor(color);
    }

    public void setBackgroundColor(int color) {
        backgroundPaint.setColor(color);
    }

    public void setProgressWidth(int width) {
        mStrokeWidth = width;
        progressPaint.setStrokeWidth(mStrokeWidth);
        backgroundPaint.setStrokeWidth(mStrokeWidth);
    }

    public void setShouldShowBackground(boolean shouldShowBackground) {
        this.shouldShowBackground = shouldShowBackground;
    }

    public void setCornerRadius(int radius) {
        this.mCornerRadius = radius;
    }

    public void setProgress(int progress) {
        PathEffect effect = new DashPathEffect(mIntervals, mPhase - mPhase * progress / 100);
        progressPaint.setPathEffect(effect);

        invalidate();
    }

}