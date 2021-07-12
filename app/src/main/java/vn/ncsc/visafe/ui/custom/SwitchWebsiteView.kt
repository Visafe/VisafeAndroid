package vn.ncsc.visafe.ui.custom

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import vn.ncsc.visafe.ui.dialog.VisafeDialogBottomSheet
import vn.ncsc.visafe.R
import vn.ncsc.visafe.databinding.LayoutSwitchWebsitesBinding
import vn.ncsc.visafe.model.Subject
import vn.ncsc.visafe.ui.adapter.WebsiteAdapter
import vn.ncsc.visafe.ui.create.group.access_manager.Action

class SwitchWebsiteView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
) : FrameLayout(context, attrs, defStyle) {

    private var binding: LayoutSwitchWebsitesBinding? = null
    private var websiteAdapter: WebsiteAdapter? = null
    private var isExpanded = false
    private var mData: ArrayList<Subject> = arrayListOf()
    private var isChoose: Boolean = false

    init {
        binding = LayoutSwitchWebsitesBinding.inflate(LayoutInflater.from(context), this, true)

        val a: TypedArray =
            context.theme.obtainStyledAttributes(attrs, R.styleable.ItemLayoutSimple, defStyle, 0)

        val title = a.getString(R.styleable.ItemLayoutSimple_title)
        val subTitle = a.getString(R.styleable.ItemLayoutSimple_sub_title)

        a.recycle()

        websiteAdapter = WebsiteAdapter {
            showDialogEdit(it)
        }
        binding?.let {
            with(it.recyclerView) {
                adapter = websiteAdapter
                layoutManager = LinearLayoutManager(context)
            }
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
            showDialog(null)
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
                isChoose = isChecked
            } else {
                for (i in mData) {
                    i.isChecked = isChecked
                }
                websiteAdapter?.notifyDataSetChanged()
            }
        }

    }

    private fun showDialogEdit(data: Subject) {
        val bottomSheet = VisafeDialogBottomSheet.newInstance(
            context.getString(R.string.websites),
            data.title,
            VisafeDialogBottomSheet.TYPE_EDIT,
            context.getString(R.string.edit_websites),
            context.getString(R.string.delete_websites)
        )
        bottomSheet.show(
            (binding?.root?.context as FragmentActivity).supportFragmentManager,
            null
        )
        bottomSheet.setOnClickListener { text, action ->
            when (action) {
                Action.DELETE -> {
                    websiteAdapter?.deleteItem(data)
                }
                Action.EDIT -> {
                    showDialog(data)
                }
            }
        }
    }

    private fun showDialog(data: Subject? = null) {
        val bottomSheet = VisafeDialogBottomSheet.newInstanceEdit(
            context.getString(R.string.block_websites_group),
            context.getString(R.string.websites),
            VisafeDialogBottomSheet.TYPE_ADD,
            context.getString(R.string.input_website),
            data?.title ?: ""
        )
        bottomSheet.show(
            (binding?.root?.context as FragmentActivity).supportFragmentManager,
            null
        )
        bottomSheet.setOnClickListener { link, action ->
            when (action) {
                Action.CONFIRM -> {
                    if (data == null) {
                        if (link.isNotBlank()) {
                            websiteAdapter?.addItem(Subject(link, link, -1))
                        }
                    } else {
                        data.let { websiteAdapter?.editItem(it, Subject(link, link, -1)) }
                    }
                }
                else -> return@setOnClickListener
            }
        }
    }

    fun setData(data: ArrayList<Subject>) {
        mData.addAll(data)
        if (!data.isNullOrEmpty()) {
            binding?.ivArrow?.visibility = View.VISIBLE
        }
        websiteAdapter?.setData(data)
    }

    fun getDataListBlockWeb(): MutableList<String>? {
        return websiteAdapter?.getData()
    }

}