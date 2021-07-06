package com.vn.visafe_android.ui.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.TypedValue;

import me.dm7.barcodescanner.core.ViewFinderView;

public class CustomViewScanQR extends ViewFinderView {
    public static final String TRADE_MARK_TEXT = "        Di chuyển camera đến vùng";
    public static final String TRADE_MARK_TEXT_02 = "             chứa mã QR để quét";
    public static final int TRADE_MARK_TEXT_SIZE_SP = 14;
    public final Paint PAINT = new Paint();

    public CustomViewScanQR(Context context) {
        super(context);
        init();
    }

    public CustomViewScanQR(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        PAINT.setColor(Color.WHITE);
        PAINT.setAntiAlias(true);
        float textPixelSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                TRADE_MARK_TEXT_SIZE_SP, getResources().getDisplayMetrics());
        PAINT.setTextSize(textPixelSize);
        setSquareViewFinder(true);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawTradeMark(canvas);
    }

    private void drawTradeMark(Canvas canvas) {
        Rect framingRect = getFramingRect();
        float tradeMarkTop;
        float tradeMarkLeft;
        if (framingRect != null) {
            tradeMarkTop = framingRect.bottom + PAINT.getTextSize() + 10;
            tradeMarkLeft = framingRect.left;
        } else {
            tradeMarkTop = 10;
            tradeMarkLeft = canvas.getHeight() - PAINT.getTextSize() - 10;
        }
        canvas.drawText(TRADE_MARK_TEXT, tradeMarkLeft, tradeMarkTop, PAINT);

        Rect framingRect2 = getFramingRect();
        float tradeMarkTop2;
        float tradeMarkLeft2;
        if (framingRect2 != null) {
            tradeMarkTop2 = framingRect2.bottom + PAINT.getTextSize() + 10 + 34;
            tradeMarkLeft2 = framingRect2.left;
        } else {
            tradeMarkTop2 = 44;
            tradeMarkLeft2 = canvas.getHeight() - PAINT.getTextSize() - 10;
        }
        canvas.drawText(TRADE_MARK_TEXT_02, tradeMarkLeft2, tradeMarkTop2, PAINT);
    }
}
