package com.vn.visafe_android.ui.custom

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.vn.visafe_android.R
import com.vn.visafe_android.databinding.LayoutSwitchWebsitesBinding
import com.vn.visafe_android.model.Subject
import com.vn.visafe_android.ui.adapter.WebsiteAdapter
import com.vn.visafe_android.ui.create.group.access_manager.Action
import com.vn.visafe_android.ui.create.group.access_manager.AddWebsiteBottomSheet
import com.vn.visafe_android.ui.create.group.access_manager.EditWebsiteBottomSheet

class SwitchWebsiteView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
) : FrameLayout(context, attrs, defStyle) {

    private var binding: LayoutSwitchWebsitesBinding? = null
    private var websiteAdapter: WebsiteAdapter? = null
    private var isExpanded = false

    init {
        binding = LayoutSwitchWebsitesBinding.inflate(LayoutInflater.from(context), this, true)

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

        setOnClickListener {
            if (isExpanded) {
                binding?.layoutList?.let {
                    it.visibility = GONE
                    binding?.ivArrow?.animate()?.rotation(90f)?.start();
                }
            } else {
                binding?.layoutList?.let {
                    it.visibility = VISIBLE
                    binding?.ivArrow?.animate()?.rotation(-90f)?.start();
                }
            }
            isExpanded = !isExpanded
        }

        binding?.tvAddLink?.setOnClickListener {
            showDialogAdd()
        }

    }

    private fun showDialogEdit(data: Subject) {
        val bottomSheet = EditWebsiteBottomSheet.newInstance(data)
        bottomSheet.show(
            (binding?.root?.context as FragmentActivity).supportFragmentManager,
            EditWebsiteBottomSheet.TAG
        )
        bottomSheet.setOnClickListener {
            when (it) {
                Action.DELETE -> {
                    websiteAdapter?.deleteItem(data)
                }

                Action.EDIT -> {
                    showDialogAdd(data)
                }
            }
        }
    }

    private fun showDialogAdd(data: Subject? = null) {
        val bottomSheet = AddWebsiteBottomSheet.newInstance(data)
        bottomSheet.show(
            (binding?.root?.context as FragmentActivity).supportFragmentManager,
            EditWebsiteBottomSheet.TAG
        )
        bottomSheet.setOnConfirmListener { subject, action ->
            when (action) {
                Action.ADD -> {
                    websiteAdapter?.addItem(subject)
                }
                Action.EDIT -> {
                    data?.let { websiteAdapter?.editItem(it, subject) }
                }
            }
        }
    }

    fun setData(data: ArrayList<Subject>) {
        if (!data.isNullOrEmpty()) {
            binding?.ivArrow?.visibility = View.VISIBLE
        }
        websiteAdapter = WebsiteAdapter {
            showDialogEdit(it)
        }
        binding?.let {
            with(it.recyclerView) {
                adapter = websiteAdapter
                layoutManager = LinearLayoutManager(context)
            }
        }
        websiteAdapter?.setData(data)
    }

}