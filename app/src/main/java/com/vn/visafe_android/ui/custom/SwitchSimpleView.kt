package com.vn.visafe_android.ui.custom

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.vn.visafe_android.R
import com.vn.visafe_android.databinding.LayoutSwitchSimpleBinding
import com.vn.visafe_android.model.Subject
import com.vn.visafe_android.ui.adapter.SubjectAdapter

class SwitchSimpleView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
) : FrameLayout(context, attrs, defStyle) {

    private var binding: LayoutSwitchSimpleBinding? = null
    private var subjectAdapter: SubjectAdapter? = null
    private var isExpanded = false

    init {
        binding = LayoutSwitchSimpleBinding.inflate(LayoutInflater.from(context), this, true)

        val a: TypedArray =
            context.theme.obtainStyledAttributes(attrs, R.styleable.ItemLayoutSimple, defStyle, 0)

        val title = a.getString(R.styleable.ItemLayoutSimple_title)
        val subTitle = a.getString(R.styleable.ItemLayoutSimple_sub_title)

        a.recycle()

        if (!title.isNullOrEmpty()) {
            binding?.tvTitle?.text = title
        }

        if (!subTitle.isNullOrEmpty()) {
            binding?.tvSubTitle?.visibility = View.VISIBLE
            binding?.tvSubTitle?.text = subTitle
        } else {
            binding?.tvSubTitle?.visibility = View.GONE
        }


    }

    fun setData(data: ArrayList<Subject>) {
        if (!data.isNullOrEmpty()) {
            binding?.ivArrow?.visibility = View.VISIBLE
        }

        setOnClickListener {
            if (isExpanded) {
                binding?.recyclerView?.let {
                    it.visibility = GONE
                    binding?.ivArrow?.animate()?.rotation(90f)?.start();
                }
            } else {
                binding?.recyclerView?.let {
                    it.visibility = VISIBLE
                    binding?.ivArrow?.animate()?.rotation(-90f)?.start();
                }
            }
            isExpanded = !isExpanded
        }

        subjectAdapter = SubjectAdapter()
        binding?.let {
            with(it.recyclerView) {
                adapter = subjectAdapter
                layoutManager = LinearLayoutManager(context)
            }
        }
        subjectAdapter?.setData(data)
    }

}