package com.vn.visafe_android.ui.custom

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.vn.visafe_android.R
import com.vn.visafe_android.databinding.LayoutToolbarBinding
import com.vn.visafe_android.utils.OnSingleClickListener

class VisafeToolbar @JvmOverloads constructor(
    context: Context,
    val attrs: AttributeSet? = null,
    defStyle: Int = 0,
    defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyle, defStyleRes) {
    private var binding: LayoutToolbarBinding? = null
    
    init {
        binding = LayoutToolbarBinding.inflate(LayoutInflater.from(context))
        addView(binding!!.root)
        initView()
    }

    private fun initView() {
        val a : TypedArray = context!!.theme.obtainStyledAttributes(attrs, R.styleable.VisafeToolbar, 0, 0)

        val title = a.getString(R.styleable.VisafeToolbar_toolbar_title)
        if (title != null) {
            binding!!.tvTitle.text = title
            binding!!.tvTitle.visibility = View.VISIBLE
        }

        val rightIcon = a.getResourceId(R.styleable.VisafeToolbar_toolbar_right_icon_src, -1)
        if (rightIcon != -1) {
            binding!!.ivRight.setImageResource(rightIcon)
            binding!!.ivRight.isEnabled = true
            binding!!.ivRight.visibility = View.VISIBLE
        } else {
            binding!!.ivRight.isEnabled = false
            binding!!.ivRight.visibility = View.INVISIBLE
        }

        val leftIcon = a.getResourceId(R.styleable.VisafeToolbar_toolbar_left_icon_src, -1)
        if (leftIcon != -1) {
            binding!!.ivLeft.setImageResource(leftIcon)
            binding!!.ivLeft.isEnabled = true
            binding!!.ivLeft.visibility = View.VISIBLE
        } else {
            binding!!.ivLeft.isEnabled = false
            binding!!.ivLeft.visibility = View.INVISIBLE
        }

        val visibleTitle = a.getBoolean(R.styleable.VisafeToolbar_toolbar_visible_title, false)
        binding!!.tvTitle.visibility = if (visibleTitle) {
            View.VISIBLE
        } else {
            View.GONE
        }

        val background = a.getResourceId(R.styleable.VisafeToolbar_toolbar_background, -1)
        if (background != -1) {
            binding!!.clContainer.setBackgroundResource(background)
        }
        a.recycle()
    }

    fun setOnClickLeftButton(onClickLeftButton : OnSingleClickListener) : VisafeToolbar {
        binding!!.ivLeft.setOnClickListener(onClickLeftButton)
        return this
    }

    fun setOnClickRightButton(onClickRightButton: OnSingleClickListener) : VisafeToolbar {
        binding!!.ivRight.setOnClickListener(onClickRightButton)
        return this
    }

    fun setTitleToolbar(title : String) : VisafeToolbar {
        binding!!.tvTitle.visibility = View.VISIBLE
        binding!!.tvTitle.text = title
        return this
    }
}