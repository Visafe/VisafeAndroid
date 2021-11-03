package vn.ncsc.visafe.ui.custom

import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import vn.ncsc.visafe.R
import kotlin.math.min

open class CircularSeekBar : View {
    /**
     * Used to scale the dp units to pixels
     */
    protected val DPTOPX_SCALE = resources.displayMetrics.density

    /**
     * `Paint` instance used to draw the inactive circle.
     */
    protected var mCirclePaint = Paint()

    /**
     * `Paint` instance used to draw the active circle (represents progress).
     */
    protected var mCircleProgressPaint = Paint()

    /**
     * The width of the circle (in pixels).
     */
    protected var mCircleStrokeWidth = 0f

    /**
     * The X radius of the circle (in pixels).
     */
    protected var mCircleXRadius = 0f

    /**
     * The Y radius of the circle (in pixels).
     */
    protected var mCircleYRadius = 0f

    /**
     * Start angle of the CircularSeekBar.
     * Note: If mStartAngle and mEndAngle are set to the same angle, 0.1 is subtracted
     * from the mEndAngle to make the circle function properly.
     */
    protected var mStartAngle = 0f

    /**
     * End angle of the CircularSeekBar.
     * Note: If mStartAngle and mEndAngle are set to the same angle, 0.1 is subtracted
     * from the mEndAngle to make the circle function properly.
     */
    protected var mEndAngle = 0f

    /**
     * `RectF` that represents the circle (or ellipse) of the seekbar.
     */
    protected var mCircleRectF = RectF()

    /**
     * Holds the color value for `mPointerPaint` before the `Paint` instance is created.
     */
    protected var mPointerColor = DEFAULT_POINTER_COLOR

    /**
     * Holds the color value for `mCirclePaint` before the `Paint` instance is created.
     */
    protected var mCircleColor = DEFAULT_CIRCLE_COLOR

    /**
     * Holds the color value for `mCircleFillPaint` before the `Paint` instance is created.
     */
    protected var mCircleFillColor = DEFAULT_CIRCLE_FILL_COLOR

    /**
     * Holds the color value for `mCircleProgressPaint` before the `Paint` instance is created.
     */
    protected var mCircleProgressColor = DEFAULT_CIRCLE_PROGRESS_COLOR

    /**
     * Holds the OnTouch alpha value for `mPointerHaloPaint`.
     */
    protected var mPointerAlphaOnTouch = DEFAULT_POINTER_ALPHA_ONTOUCH

    /**
     * Distance (in degrees) that the the circle/semi-circle makes up.
     * This amount represents the max of the circle in degrees.
     */
    protected var mTotalCircleDegrees = 0f

    /**
     * Distance (in degrees) that the current progress makes up in the circle.
     */
    protected var mProgressDegrees = 0f

    /**
     * `Path` used to draw the circle/semi-circle.
     */
    protected var mCirclePath: Path? = null

    /**
     * `Path` used to draw the progress on the circle.
     */
    protected var mCircleProgressPath: Path? = null

    /**
     * Max value that this CircularSeekBar is representing.
     */
    protected var mMax = 0

    /**
     * Progress value that this CircularSeekBar is representing.
     */
    protected var mProgress = 0f

    /**
     * If true, then the user can specify the X and Y radii.
     * If false, then the View itself determines the size of the CircularSeekBar.
     */
    protected var mCustomRadii = false

    /**
     * Maintain a perfect circle (equal x and y radius), regardless of view or custom attributes.
     * The smaller of the two radii will always be used in this case.
     * The default is to be a circle and not an ellipse, due to the behavior of the ellipse.
     */
    protected var mMaintainEqualCircle = false

    /**
     * Once a user has touched the circle, this determines if moving outside the circle is able
     * to change the position of the pointer (and in turn, the progress).
     */
    protected var mMoveOutsideCircle = false

    protected var mCircleWidth = 0f

    /**
     * The height of the circle used in the `RectF` that is used to draw it.
     * Based on either the View width or the custom Y radius.
     */
    protected var mCircleHeight = 0f

    /**
     * Represents the progress mark on the circle, in geometric degrees.
     * This is not provided by the user; it is calculated;
     */
    protected var mPointerPosition = 0f

    /**
     * Pointer position in terms of X and Y coordinates.
     */
    protected var mPointerPositionXY = FloatArray(2)

    /**
     * If true, progress will be draw clockwise
     */
    private var mIsClockwise = false

    /**
     * Initialize the CircularSeekBar with the attributes from the XML style.
     * Uses the defaults defined at the top of this file when an attribute is not specified by the user.
     * @param attrArray TypedArray containing the attributes.
     */
    protected fun initAttributes(attrArray: TypedArray) {
        mCircleXRadius =
            attrArray.getDimension(R.styleable.CircularSeekBar_circle_x_radius, DEFAULT_CIRCLE_X_RADIUS * DPTOPX_SCALE)
        mCircleYRadius =
            attrArray.getDimension(R.styleable.CircularSeekBar_circle_y_radius, DEFAULT_CIRCLE_Y_RADIUS * DPTOPX_SCALE)
        mCircleStrokeWidth =
            attrArray.getDimension(R.styleable.CircularSeekBar_circle_stroke_width, DEFAULT_CIRCLE_STROKE_WIDTH * DPTOPX_SCALE)
        mPointerColor = attrArray.getColor(R.styleable.CircularSeekBar_pointer_color, DEFAULT_POINTER_COLOR)
        mCircleColor = attrArray.getColor(R.styleable.CircularSeekBar_circle_color, DEFAULT_CIRCLE_COLOR)
        mCircleProgressColor =
            attrArray.getColor(R.styleable.CircularSeekBar_circle_progress_color, DEFAULT_CIRCLE_PROGRESS_COLOR)
        mCircleFillColor = attrArray.getColor(R.styleable.CircularSeekBar_circle_fill, DEFAULT_CIRCLE_FILL_COLOR)
        mMax = attrArray.getInt(R.styleable.CircularSeekBar_max, DEFAULT_MAX)
        mProgress = attrArray.getFloat(R.styleable.CircularSeekBar_c_progress, DEFAULT_PROGRESS)
        mCustomRadii = attrArray.getBoolean(R.styleable.CircularSeekBar_use_custom_radii, DEFAULT_USE_CUSTOM_RADII)
        mMaintainEqualCircle =
            attrArray.getBoolean(R.styleable.CircularSeekBar_maintain_equal_circle, DEFAULT_MAINTAIN_EQUAL_CIRCLE)
        mMoveOutsideCircle = attrArray.getBoolean(R.styleable.CircularSeekBar_move_outside_circle, DEFAULT_MOVE_OUTSIDE_CIRCLE)
        mIsClockwise = attrArray.getBoolean(R.styleable.CircularSeekBar_c_clockwise, DEFAULT_CLOCKWISE)
        // Modulo 360 right now to avoid constant conversion
        mStartAngle = (360f + attrArray.getFloat(R.styleable.CircularSeekBar_start_angle, DEFAULT_START_ANGLE) % 360f) % 360f
        mEndAngle = (360f + attrArray.getFloat(R.styleable.CircularSeekBar_end_angle, DEFAULT_END_ANGLE) % 360f) % 360f
        if (mStartAngle == mEndAngle) { //mStartAngle = mStartAngle + 1f;
            mEndAngle -= .1f
        }
    }

    /**
     * Initializes the `Paint` objects with the appropriate styles.
     */
    protected fun initPaints() {
        with(mCirclePaint) {
            isAntiAlias = true
            isDither = true
            color = mCircleColor
            strokeWidth = mCircleStrokeWidth
            style = Paint.Style.STROKE
            strokeJoin = Paint.Join.MITER
            strokeCap = Paint.Cap.SQUARE
        }

        with(mCircleProgressPaint) {
            isAntiAlias = true
            isDither = true
            color = mCircleProgressColor
            strokeWidth = mCircleStrokeWidth
            style = Paint.Style.STROKE
            strokeJoin = Paint.Join.MITER
            strokeCap = Paint.Cap.SQUARE
        }
    }

    /**
     * Calculates the total degrees between mStartAngle and mEndAngle, and sets mTotalCircleDegrees
     * to this value.
     */
    protected fun calculateTotalDegrees() {
        mTotalCircleDegrees = (360f - (mStartAngle - mEndAngle)) % 360f // Length of the entire circle/arc
        if (mTotalCircleDegrees <= 0f) {
            mTotalCircleDegrees = 360f
        }
    }

    /**
     * Calculate the degrees that the progress represents. Also called the sweep angle.
     * Sets mProgressDegrees to that value.
     */
    protected fun calculateProgressDegrees() {
        mProgressDegrees = mPointerPosition - mStartAngle // Verified
        mProgressDegrees = if (mProgressDegrees < 0) 360f + mProgressDegrees else mProgressDegrees // Verified
    }

    /**
     * Calculate the pointer position (and the end of the progress arc) in degrees.
     * Sets mPointerPosition to that value.
     */
    protected fun calculatePointerAngle() {
        val progressPercent = mProgress.toFloat() / mMax.toFloat()
        mPointerPosition = progressPercent * mTotalCircleDegrees + mStartAngle
        mPointerPosition %= 360f
    }

    protected fun calculatePointerXYPosition() {
        var pm = PathMeasure(mCircleProgressPath, false)
        var returnValue = pm.getPosTan(pm.length, mPointerPositionXY, null)
        if (!returnValue) {
            pm = PathMeasure(mCirclePath, false)
            returnValue = pm.getPosTan(0f, mPointerPositionXY, null)
        }
    }

    /**
     * Initialize the `Path` objects with the appropriate values.
     */
    protected fun initPaths() {
        mCirclePath = Path()
        mCirclePath!!.addArc(mCircleRectF, mStartAngle, mTotalCircleDegrees)
        mCircleProgressPath = Path()
        if (mIsClockwise) {
            mCircleProgressPath!!.addArc(mCircleRectF, mStartAngle, -mProgressDegrees)
        } else {
            mCircleProgressPath!!.addArc(mCircleRectF, mStartAngle, mProgressDegrees)
        }
    }

    /**
     * Initialize the `RectF` objects with the appropriate values.
     */
    protected fun initRects() {
        mCircleRectF[-mCircleWidth, -mCircleHeight, mCircleWidth] = mCircleHeight
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.translate(this.width / 2.toFloat(), this.height / 2.toFloat())
        canvas.drawPath(mCirclePath!!, mCirclePaint)
        canvas.drawPath(mCircleProgressPath!!, mCircleProgressPaint)
    }

    /**
     * Get the progress of the CircularSeekBar.
     * @return The progress of the CircularSeekBar.
     */
    /**
     * Set the progress of the CircularSeekBar.
     * If the progress is the same, then any listener will not receive a onProgressChanged event.
     * @param progress The progress to set the CircularSeekBar to.
     */
    var progress: Float
        get() = (mMax.toFloat() * mProgressDegrees / mTotalCircleDegrees)
        set(progress) {
            if (mProgress != progress) {
                mProgress = progress
                recalculateAll()
                invalidate()
            }
        }

    protected fun setProgressBasedOnAngle(angle: Float) {
        mPointerPosition = angle
        calculateProgressDegrees()
        mProgress = mMax.toFloat() * mProgressDegrees / mTotalCircleDegrees
    }

    private fun recalculateAll() {
        calculateTotalDegrees()
        calculatePointerAngle()
        calculateProgressDegrees()
        initRects()
        initPaths()
        calculatePointerXYPosition()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val height = getDefaultSize(suggestedMinimumHeight, heightMeasureSpec)
        val width = getDefaultSize(suggestedMinimumWidth, widthMeasureSpec)
        if (mMaintainEqualCircle) {
            val min = min(width, height)
            setMeasuredDimension(min, min)
        } else {
            setMeasuredDimension(width, height)
        }
        // Set the circle width and height based on the view for the moment
        mCircleHeight = height.toFloat() / 2f
        mCircleWidth = width.toFloat() / 2f
        // If it is not set to use custom
        if (mCustomRadii) { // Check to make sure the custom radii are not out of the view. If they are, just use the view values
            if (mCircleYRadius < mCircleHeight) {
                mCircleHeight = mCircleYRadius
            }
            if (mCircleXRadius < mCircleWidth) {
                mCircleWidth = mCircleXRadius
            }
        }
        if (mMaintainEqualCircle) { // Applies regardless of how the values were determined
            val min = min(mCircleHeight, mCircleWidth)
            mCircleHeight = min - mCircleStrokeWidth / 2
            mCircleWidth = min - mCircleStrokeWidth / 2
        }
        recalculateAll()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return false
    }

    protected fun init(attrs: AttributeSet?, defStyle: Int) {
        val attrArray = context.obtainStyledAttributes(attrs, R.styleable.CircularSeekBar, defStyle, 0)
        initAttributes(attrArray)
        attrArray.recycle()
        initPaints()
    }

    constructor(context: Context?) : super(context) {
        init(null, 0)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs, defStyle)
    }

    override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()
        val state = Bundle()
        state.putParcelable("PARENT", superState)
        state.putInt("MAX", mMax)
        state.putFloat("PROGRESS", mProgress)
        state.putInt("mCircleColor", mCircleColor)
        state.putInt("mCircleProgressColor", mCircleProgressColor)
        state.putInt("mPointerColor", mPointerColor)
        state.putInt("mPointerAlphaOnTouch", mPointerAlphaOnTouch)
        return state
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        val savedState = state as Bundle
        val superState = savedState.getParcelable<Parcelable>("PARENT")
        super.onRestoreInstanceState(superState)
        mMax = savedState.getInt("MAX")
        mProgress = savedState.getFloat("PROGRESS")
        mCircleColor = savedState.getInt("mCircleColor")
        mCircleProgressColor = savedState.getInt("mCircleProgressColor")
        mPointerColor = savedState.getInt("mPointerColor")
        mPointerAlphaOnTouch = savedState.getInt("mPointerAlphaOnTouch")
        initPaints()
        recalculateAll()
    }

    /**
     * Gets the circle color.
     * @return An integer color value for the circle
     */
    /**
     * Sets the circle color.
     * @param color the color of the circle
     */
    var circleColor: Int
        get() = mCircleColor
        set(color) {
            mCircleColor = color
            mCirclePaint.color = mCircleColor
            invalidate()
        }

    /**
     * Gets the circle progress color.
     * @return An integer color value for the circle progress
     */
    /**
     * Sets the circle progress color.
     * @param color the color of the circle progress
     */
    var circleProgressColor: Int
        get() = mCircleProgressColor
        set(color) {
            mCircleProgressColor = color
            mCircleProgressPaint.color = mCircleProgressColor
            invalidate()
        }

    /**
     * Get the current max of the CircularSeekBar.
     * @return Synchronized integer value of the max.
     */// If the new max is less than current progress, set progress to zero// Check to make sure it's greater than zero
    /**
     * Set the max of the CircularSeekBar.
     * If the new max is less than the current progress, then the progress will be set to zero.
     * If the progress is changed as a result, then any listener will receive a onProgressChanged event.
     * @param max The new max for the CircularSeekBar.
     */
    @get:Synchronized
    var max: Int
        get() = mMax
        set(max) {
            if (max > 0) { // Check to make sure it's greater than zero
                if (max <= mProgress) {
                    mProgress = 0f // If the new max is less than current progress, set progress to zero
                }
                mMax = max
                recalculateAll()
                invalidate()
            }
        }

    companion object {
        // Default values
        private const val DEFAULT_CIRCLE_X_RADIUS = 30f
        private const val DEFAULT_CIRCLE_Y_RADIUS = 30f
        private const val DEFAULT_POINTER_RADIUS = 7f
        private const val DEFAULT_CIRCLE_STROKE_WIDTH = 5f
        private const val DEFAULT_START_ANGLE = 270f // Geometric (clockwise, relative to 3 o'clock)
        private const val DEFAULT_END_ANGLE = 270f // Geometric (clockwise, relative to 3 o'clock)
        private const val DEFAULT_MAX = 100
        private const val DEFAULT_PROGRESS = 0f
        private const val DEFAULT_CIRCLE_COLOR = Color.DKGRAY
        private val DEFAULT_CIRCLE_PROGRESS_COLOR = Color.argb(235, 74, 138, 255)
        private val DEFAULT_POINTER_COLOR = Color.argb(235, 74, 138, 255)
        private const val DEFAULT_CIRCLE_FILL_COLOR = Color.TRANSPARENT
        private const val DEFAULT_POINTER_ALPHA_ONTOUCH = 100
        private const val DEFAULT_USE_CUSTOM_RADII = false
        private const val DEFAULT_MAINTAIN_EQUAL_CIRCLE = true
        private const val DEFAULT_MOVE_OUTSIDE_CIRCLE = false
        private const val DEFAULT_CLOCKWISE = false
    }
}