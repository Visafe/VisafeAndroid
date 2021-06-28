package com.vn.visafe_android.ui.custom.otp

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.Menu
import android.view.MenuItem
import android.view.animation.AnimationUtils
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.vn.visafe_android.R


class OtpEditText : AppCompatEditText, TextWatcher {
    private var mClickListener: OnClickListener? = null
    private var mLinesPaint: Paint? = null
    private var mStrokePaint: Paint? = null
    private var mTextPaint: Paint? = null
    private var mHintPaint: Paint? = null
    private var mMaskInput = false
    private var defStyleAttr = 0
    private var mMaxLength = 6
    private var mPrimaryColor = 0
    private var mSecondaryColor = 0
    private var mTextColor = 0
    private var mHintTextColor = 0
    private var mLineStrokeSelected = 2f //2dp by default
    private var mLineStroke = 1f //1dp by default
    private var mSpace = 10f //24 dp by default, space between the lines
    private var mCharSize = 0f
    private var mNumChars = 6f
    private var mLineSpacing = 10f //8dp by default, height of the text from our lines
    private var mBoxStyle: String? = null
    private var mMaskCharacter = "*"
    private var mHintText: String = ""
    private val ROUNDED_BOX = "rounded_box"
    private val UNDERLINE = "underline"
    private val SQUARE_BOX = "square_box"
    private val ROUNDED_UNDERLINE = "rounded_underline"
    lateinit var textWidths: FloatArray
    var hintWidth = FloatArray(1)
    private var completeListener: OnCompleteListener? = null
    private var changeListener: OnChangeListener? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        this.defStyleAttr = defStyleAttr
        init(context, attrs)
    }

    private fun init(context: Context, @Nullable attrs: AttributeSet?) {
        if (attrs != null) {
            getAttrsFromTypedArray(attrs)
        }
        mTextPaint = paint
        mTextPaint?.color = mTextColor
        mHintPaint = Paint(paint)
        mHintPaint?.color = mHintTextColor

        // Set the TextWatcher
        addTextChangedListener(this)
        val multi: Float = context.resources.displayMetrics.density
        mLineStroke *= multi
        mLineStrokeSelected *= multi
        mLinesPaint = Paint(paint)
        mLinesPaint?.strokeWidth = mLineStroke
        setBackgroundResource(0)
        mSpace *= multi //convert to pixels for our density
        mNumChars = mMaxLength.toFloat()

        //Disable copy paste
        super.setCustomSelectionActionModeCallback(object : android.view.ActionMode.Callback {
            override fun onCreateActionMode(mode: android.view.ActionMode?, menu: Menu?): Boolean {
                return false
            }

            override fun onPrepareActionMode(mode: android.view.ActionMode?, menu: Menu?): Boolean {
                return false
            }

            override fun onActionItemClicked(mode: android.view.ActionMode?, item: MenuItem?): Boolean {
                return false
            }

            override fun onDestroyActionMode(mode: android.view.ActionMode?) {
            }
        })
        // When tapped, move cursor to end of text.
        super.setOnClickListener { v ->
            setSelection(text!!.length)
            if (mClickListener != null) {
                mClickListener?.onClick(v)
            }
        }
    }

    private fun getAttrsFromTypedArray(attributeSet: AttributeSet) {
        val a = context.obtainStyledAttributes(attributeSet, R.styleable.OtpEditText, defStyleAttr, 0)
        mPrimaryColor =
            a.getColor(R.styleable.OtpEditText_oev_primary_color, ContextCompat.getColor(context, R.color.accent_color))
        mSecondaryColor =
            a.getColor(R.styleable.OtpEditText_oev_secondary_color, ContextCompat.getColor(context, R.color.secondary_color))
        mTextColor = a.getColor(R.styleable.OtpEditText_oev_text_color, ContextCompat.getColor(context, R.color.black))
        mBoxStyle = a.getString(R.styleable.OtpEditText_oev_box_style)
        mMaskInput = a.getBoolean(R.styleable.OtpEditText_oev_mask_input, false)
        mMaskCharacter = if (a.getString(R.styleable.OtpEditText_oev_mask_character) != null) {
            a.getString(R.styleable.OtpEditText_oev_mask_character).toString().substring(0, 1)
        } else {
            context.getString(R.string.mask_character)
        }
        if (mBoxStyle != null && mBoxStyle?.isNotEmpty() == true) {
            when (mBoxStyle) {
                UNDERLINE, ROUNDED_UNDERLINE -> {
                    mStrokePaint = Paint(paint)
                    mStrokePaint?.strokeWidth = 6f
                    mStrokePaint?.style = Paint.Style.FILL
                }
                SQUARE_BOX, ROUNDED_BOX -> {
                    mStrokePaint = Paint(paint)
                    mStrokePaint?.strokeWidth = 6f
                    mStrokePaint?.style = Paint.Style.STROKE
                }
                else -> {
                    mStrokePaint = Paint(paint)
                    mStrokePaint?.strokeWidth = 6f
                    mStrokePaint?.style = Paint.Style.FILL
                    mBoxStyle = UNDERLINE
                }
            }
        } else {
            mStrokePaint = Paint(paint)
            mStrokePaint?.strokeWidth = 6f
            mStrokePaint?.style = Paint.Style.FILL
            mBoxStyle = UNDERLINE
        }
        a.recycle()
    }

    @get:Nullable
    val otpValue: String?
        get() = if (text.toString().length != mMaxLength) {
            triggerErrorAnimation()
            null
        } else {
            text.toString()
        }

    @get:NonNull
    val maxCharLength: Int
        get() = mNumChars.toInt()

    private fun triggerErrorAnimation() {
        startAnimation(AnimationUtils.loadAnimation(context, R.anim.shake))
    }

    override fun setCustomSelectionActionModeCallback(actionModeCallback: android.view.ActionMode.Callback?) {
        throw RuntimeException("setCustomSelectionActionModeCallback() not supported.")
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        val availableWidth = width - paddingRight - paddingLeft
        mCharSize = if (mSpace < 0) {
            availableWidth / (mNumChars * 2 - 1)
        } else {
            (availableWidth - mSpace * (mNumChars - 1)) / mNumChars
        }
        mLineSpacing = (height * .6).toFloat()
        var startX = paddingLeft.toFloat()
        var hintStartX = paddingLeft.toFloat()
        val bottom = height - paddingBottom.toFloat()
        val top = paddingTop.toFloat()

        //Text Width
        val text = text
        val textLength = text!!.length
        textWidths = FloatArray(textLength)
        if (text.isEmpty() && mHintText.isNotEmpty()) {
            paint.getTextWidths("1", 0, 1, hintWidth)
            var i = 0
            while (i < mNumChars && i < mHintText.length) {
                val middle = hintStartX + mCharSize / 2
                mHintPaint?.let { canvas.drawText(mHintText, i, i + 1, middle - hintWidth[0] / 2, mLineSpacing, it) }
                hintStartX += if (mSpace < 0) {
                    (mCharSize * 2).toInt()
                } else {
                    (mCharSize + mSpace).toInt()
                }
                i++
            }
        }
        paint.getTextWidths(getText(), 0, textLength, textWidths)
        var i = 0
        while (i < mNumChars) {
            updateColorForLines(i <= textLength, i == textLength)
            when (mBoxStyle) {
                ROUNDED_UNDERLINE -> try {
                    mStrokePaint?.let { canvas.drawRoundRect(startX, bottom * .95f, startX + mCharSize, bottom, 16f, 16f, it) }
                } catch (err: NoSuchMethodError) {
                    mStrokePaint?.let { canvas.drawRect(startX, bottom * .95f, startX + mCharSize, bottom, it) }
                }
                ROUNDED_BOX -> try {
                    mLinesPaint?.let { canvas.drawRoundRect(startX, top, startX + mCharSize, bottom, 8f, 8f, it) }
                    mStrokePaint?.let { canvas.drawRoundRect(startX, top, startX + mCharSize, bottom, 8f, 8f, it) }
                } catch (err: NoSuchMethodError) {
                    mLinesPaint?.let { canvas.drawRect(startX, top, startX + mCharSize, bottom, it) }
                    mStrokePaint?.let { canvas.drawRect(startX, top, startX + mCharSize, bottom, it) }
                }
                UNDERLINE -> mStrokePaint?.let {
                    canvas.drawRect(
                        startX, bottom * .95f, startX + mCharSize, bottom,
                        it
                    )
                }
                SQUARE_BOX -> {
                    mLinesPaint?.let { canvas.drawRect(startX, top, startX + mCharSize, bottom, it) }
                    mStrokePaint?.let { canvas.drawRect(startX, top, startX + mCharSize, bottom, it) }
                }
            }
            if (getText()!!.length > i) {
                val middle = startX + mCharSize / 2
                if (mMaskInput) {
                    mTextPaint?.let { canvas.drawText(maskText, i, i + 1, middle - textWidths[0] / 2, mLineSpacing, it) }
                } else {
                    mTextPaint?.let { canvas.drawText(text, i, i + 1, middle - textWidths[0] / 2, mLineSpacing, it) }
                }
            }
            startX += if (mSpace < 0) {
                (mCharSize * 2).toInt()
            } else {
                (mCharSize + mSpace).toInt()
            }
            i++
        }
    }

    private val maskText: String
        private get() {
            val length = text.toString().length
            val out = StringBuilder()
            for (i in 0 until length) {
                out.append(mMaskCharacter)
            }
            return out.toString()
        }

    /**
     * @param next Is the current char the next character to be input?
     */
    private fun updateColorForLines(next: Boolean, current: Boolean) {
        if (next) {
            mStrokePaint?.color = mSecondaryColor
            mLinesPaint?.color = mSecondaryColor
        } else {
            mStrokePaint?.color = mSecondaryColor
            mLinesPaint?.color = ContextCompat.getColor(context, R.color.white)
        }
        if (current) {
            mLinesPaint?.color = ContextCompat.getColor(context, R.color.white)
            mStrokePaint?.color = mPrimaryColor
        }
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    override fun afterTextChanged(s: Editable) {
        if (changeListener != null) {
            changeListener?.onChange(s.toString())
        }
        if (s.length.toFloat() == mNumChars && completeListener != null) {
            completeListener?.onComplete(s.toString())
        }
    }

    override fun setOnClickListener(l: OnClickListener?) {
        mClickListener = l
    }

    fun setOnCompleteListener(listener: OnCompleteListener?) {
        completeListener = listener
    }

    fun setOnChangeListener(listener: OnChangeListener?) {
        changeListener = listener
    }
}