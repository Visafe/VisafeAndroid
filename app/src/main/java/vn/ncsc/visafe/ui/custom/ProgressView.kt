package vn.ncsc.visafe.ui.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import vn.ncsc.visafe.R

class ProgressView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attrs, defStyle) {

    private var mHeightView = 10

    private var mPercent = 0

    private var color: Int = ContextCompat.getColor(context, R.color.color_FFB31F)


    private var paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.RED
        style = Paint.Style.FILL_AND_STROKE
    }

    init {

        val typeArray = context.obtainStyledAttributes(attrs, R.styleable.ProgressView)

        color = typeArray.getColor(
            R.styleable.ProgressView_color,
            ContextCompat.getColor(context, R.color.color_FFB31F)
        )

        mPercent = typeArray.getInt(R.styleable.ProgressView_percent, 0)

        paint.color = color

        typeArray.recycle()

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)

        if (heightMode == MeasureSpec.EXACTLY) {
            mHeightView = heightSize
        }

        setMeasuredDimension(
            widthMeasureSpec,
            MeasureSpec.makeMeasureSpec(mHeightView, MeasureSpec.EXACTLY)
        )
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        drawProgress(canvas)
    }

    private fun drawProgress(canvas: Canvas?) {
        val rect = RectF(0F, 0F, width * mPercent / 100F, height.toFloat())
        canvas?.drawRoundRect(rect, 0F, 0F, paint)
    }

    fun setProgress(percent: Int) {
        mPercent = percent
        invalidate()
    }
}