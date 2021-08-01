package vn.ncsc.visafe.ui.custom

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import vn.ncsc.visafe.R
import vn.ncsc.visafe.databinding.LayoutSwitchSimpleBinding
import vn.ncsc.visafe.model.Subject
import vn.ncsc.visafe.ui.adapter.SubjectAdapter

class SwitchSimpleView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
) : FrameLayout(context, attrs, defStyle) {

    private var binding: LayoutSwitchSimpleBinding? = null
    private var subjectAdapter: SubjectAdapter? = null
    private var isExpanded = false
    private var disableExpanded = false
    private var mData: ArrayList<Subject> = arrayListOf()
    private var mOnSwitchChangeListener: ((Boolean) -> Unit)? = null
    private var mOnSwitchItemChangeListener: ((Boolean, Int) -> Unit)? = null

    init {
        binding = LayoutSwitchSimpleBinding.inflate(LayoutInflater.from(context), this, true)

        val a: TypedArray =
            context.theme.obtainStyledAttributes(attrs, R.styleable.ItemLayoutSimple, defStyle, 0)

        val title = a.getString(R.styleable.ItemLayoutSimple_title)
        val subTitle = a.getString(R.styleable.ItemLayoutSimple_sub_title)

        a.recycle()

        subjectAdapter = SubjectAdapter()
        binding?.let {
            with(it.recyclerView) {
                adapter = subjectAdapter
                layoutManager = LinearLayoutManager(context)
            }
        }
        setOnClickListener {
            if (disableExpanded) {
                return@setOnClickListener
            }
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
        if (!title.isNullOrEmpty()) {
            binding?.tvTitle?.text = title
        }

        if (!subTitle.isNullOrEmpty()) {
            binding?.tvSubTitle?.visibility = View.VISIBLE
            binding?.tvSubTitle?.text = subTitle
        } else {
            binding?.tvSubTitle?.visibility = View.GONE
        }
        binding?.switchWidget?.setOnCheckedChangeListener { _, isChecked ->
            if (mData.isNullOrEmpty()) {
                mOnSwitchChangeListener?.invoke(isChecked)
            } else {
                for (i in mData) {
                    i.isChecked = isChecked
                }
                subjectAdapter?.notifyDataSetChanged()
                mOnSwitchChangeListener?.invoke(isChecked)
            }
        }
        subjectAdapter?.setOnSwitchItem { isChecked, position ->
            mOnSwitchItemChangeListener?.invoke(isChecked, position)
        }
    }

    fun setOnSwitchChangeListener(onSwitchChange: (Boolean) -> Unit) {
        mOnSwitchChangeListener = onSwitchChange
    }

    fun setOnSwitchItemChangeListener(onSwitchItemChange: (Boolean, Int) -> Unit) {
        mOnSwitchItemChangeListener = onSwitchItemChange
    }

    fun isChecked(): Boolean {
        return binding?.switchWidget?.isChecked == true
    }

    fun setChecked(value: Boolean) {
        binding?.switchWidget?.isChecked = value
        for (i in mData) {
            i.isChecked = value
        }
        subjectAdapter?.notifyDataSetChanged()
    }

    fun setData(data: ArrayList<Subject>) {
        mData.clear()
        mData.addAll(data)
        if (!data.isNullOrEmpty()) {
            binding?.ivArrow?.visibility = View.VISIBLE
        }
        subjectAdapter?.setData(data)
    }

    fun reloadData() {
        subjectAdapter?.notifyDataSetChanged()
    }

    fun getDataListSubject(): MutableList<String>? {
        return subjectAdapter?.getData()
    }

    fun setExpanded(isExpanded: Boolean) {
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
        this.isExpanded = !isExpanded
    }

    fun disableExpanded() {
        this.disableExpanded = true
        binding?.ivArrow?.visibility = View.INVISIBLE
    }
}