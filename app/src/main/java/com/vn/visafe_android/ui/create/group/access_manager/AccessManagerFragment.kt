package com.vn.visafe_android.ui.create.group.access_manager

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.vn.visafe_android.R
import com.vn.visafe_android.base.BaseFragment
import com.vn.visafe_android.databinding.FragmentAccessManagerBinding
import com.vn.visafe_android.model.Subject
import com.vn.visafe_android.ui.adapter.WebsiteCreatGroupAdapter
import com.vn.visafe_android.ui.create.group.CreateGroupActivity
import com.vn.visafe_android.ui.create.group.time.TimeProtectionFragment
import com.vn.visafe_android.ui.dialog.VisafeDialogBottomSheet
import com.vn.visafe_android.utils.setOnSingClickListener

class AccessManagerFragment : BaseFragment<FragmentAccessManagerBinding>() {

    companion object {
        fun newInstance(): AccessManagerFragment {
            val args = Bundle()

            val fragment = AccessManagerFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private var createGroupActivity: CreateGroupActivity? = null

    private var blockAdapter: WebsiteCreatGroupAdapter? = null
    private var mDataBlock: ArrayList<Subject> = arrayListOf()

    private var prioritizeAdapter: WebsiteCreatGroupAdapter? = null
    private var mDataPrioritize: ArrayList<Subject> = arrayListOf()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is CreateGroupActivity) {
            createGroupActivity = context
        }
    }

    override fun layoutRes(): Int = R.layout.fragment_access_manager

    override fun initView() {
        binding.ivBack.setOnClickListener {
            createGroupActivity?.onBackPressed()
        }
        val dataItemApp = arrayListOf(
            Subject(
                "Instagram",
                "instagram",
                R.drawable.ic_instagram
            ),
            Subject(
                "Google Map",
                "googlemap",
                R.drawable.ic_google_map
            ),
            Subject(
                "Book",
                "book",
                R.drawable.ic_book
            ),
            Subject(
                "App Store",
                "appstore",
                R.drawable.ic_appstore
            )
        )

        mDataBlock = arrayListOf(
            Subject(
                "https://www.youtube.com/",
                "https://www.youtube.com/",
                R.drawable.ic_instagram
            ),
            Subject(
                "https://www.facebook.com/",
                "https://www.facebook.com/",
                R.drawable.ic_google_map
            ),
            Subject(
                "https://gmail.com/",
                "https://gmail.com/",
                R.drawable.ic_book
            )
        )

        mDataPrioritize = arrayListOf(
            Subject(
                "https://www.youtube.com/",
                "https://www.youtube.com/",
                R.drawable.ic_instagram
            ),
            Subject(
                "https://www.facebook.com/",
                "https://www.facebook.com/",
                R.drawable.ic_google_map
            ),
            Subject(
                "https://gmail.com/",
                "https://gmail.com/",
                R.drawable.ic_book
            )
        )
        binding.itemApp.setData(dataItemApp)
        binding.itemApp.setExpanded(false)
//        binding.itemWeb.setData(dataItemWeb)

        blockAdapter = WebsiteCreatGroupAdapter {
            showDialogEditBlock(it)
        }
        blockAdapter?.setData(mDataBlock)
        binding.rvBlock.layoutManager = LinearLayoutManager(requireContext())
        binding.rvBlock.adapter = blockAdapter

        prioritizeAdapter = WebsiteCreatGroupAdapter {
            showDialogEditPrioritize(it)
        }
        prioritizeAdapter?.setData(mDataPrioritize)
        binding.rvPrioritize.layoutManager = LinearLayoutManager(requireContext())
        binding.rvPrioritize.adapter = prioritizeAdapter

        binding.btnSave.setOnSingClickListener {
            createGroupActivity?.createGroupRequest?.blocked_services =
                binding.itemApp.getDataListSubject()
            createGroupActivity?.createGroupRequest?.block_webs = getDataListBlockWeb()
//            createGroupActivity?.createGroupRequest?.safesearch_enabled = binding.itemLimit.isChecked()
//            createGroupActivity?.createGroupRequest?.porn_enabled = binding.itemSensitive.isChecked()
//            createGroupActivity?.createGroupRequest?.bypass_enabled = binding.itemByPass.isChecked()
            val gson = Gson()
            Log.e(
                "initView: ",
                "" + gson.toJson(createGroupActivity?.createGroupRequest)
            )
            createGroupActivity?.onBackPressed()
        }

        binding.btnBlockWebsite.setOnClickListener {
            binding.btnBlockWebsite.alpha = 1f
            binding.viewBlock.visibility = View.VISIBLE
            binding.rvBlock.visibility = View.VISIBLE

            binding.btnPrioritizeWebsite.alpha = 0.5f
            binding.viewPrioritize.visibility = View.INVISIBLE
            binding.rvPrioritize.visibility = View.GONE
        }

        binding.btnPrioritizeWebsite.setOnClickListener {
            binding.btnBlockWebsite.alpha = 0.5f
            binding.viewBlock.visibility = View.INVISIBLE
            binding.rvBlock.visibility = View.GONE

            binding.btnPrioritizeWebsite.alpha = 1f
            binding.viewPrioritize.visibility = View.VISIBLE
            binding.rvPrioritize.visibility = View.VISIBLE
        }

        binding.btnAddLink.setOnClickListener {
            if (binding.viewBlock.visibility == View.VISIBLE) {
                showDialogBlock()
            } else {
                showDialogPrioritize()
            }
        }
    }

    private fun showDialogEditBlock(data: Subject) {
        val bottomSheet = VisafeDialogBottomSheet.newInstance(
            requireContext().getString(R.string.websites),
            data.title,
            VisafeDialogBottomSheet.TYPE_EDIT,
            requireContext().getString(R.string.edit_websites),
            requireContext().getString(R.string.delete_websites)
        )
        bottomSheet.show(parentFragmentManager, null)
        bottomSheet.setOnClickListener { text, action ->
            when (action) {
                Action.DELETE -> {
                    blockAdapter?.deleteItem(data)
                }
                Action.EDIT -> {
                    showDialogBlock(data)
                }
            }
        }
    }

    private fun showDialogBlock(data: Subject? = null) {
        val bottomSheet = VisafeDialogBottomSheet.newInstanceEdit(
            requireContext().getString(R.string.block_websites_group),
            requireContext().getString(R.string.websites),
            VisafeDialogBottomSheet.TYPE_ADD,
            requireContext().getString(R.string.input_website),
            data?.title ?: ""
        )
        bottomSheet.show(parentFragmentManager, null)
        bottomSheet.setOnClickListener { link, action ->
            hiddenKeyboard()
            when (action) {
                Action.CONFIRM -> {
                    if (data == null) {
                        if (link.isNotBlank()) {
                            blockAdapter?.addItem(Subject(link, link, -1))
                        }
                    } else {
                        data.let { blockAdapter?.editItem(it, Subject(link, link, -1)) }
                    }
                }
                else -> return@setOnClickListener
            }
        }
    }

    private fun getDataListBlockWeb(): MutableList<String>? {
        return blockAdapter?.getData()
    }

    private fun showDialogEditPrioritize(data: Subject) {
        val bottomSheet = VisafeDialogBottomSheet.newInstance(
            requireContext().getString(R.string.websites),
            data.title,
            VisafeDialogBottomSheet.TYPE_EDIT,
            requireContext().getString(R.string.edit_websites),
            requireContext().getString(R.string.delete_websites)
        )
        bottomSheet.show(parentFragmentManager, null)
        bottomSheet.setOnClickListener { text, action ->
            when (action) {
                Action.DELETE -> {
                    prioritizeAdapter?.deleteItem(data)
                }
                Action.EDIT -> {
                    showDialogPrioritize(data)
                }
            }
        }
    }

    private fun showDialogPrioritize(data: Subject? = null) {
        val bottomSheet = VisafeDialogBottomSheet.newInstanceEdit(
            requireContext().getString(R.string.pri_websites_group),
            requireContext().getString(R.string.websites),
            VisafeDialogBottomSheet.TYPE_ADD,
            requireContext().getString(R.string.input_website),
            data?.title ?: ""
        )
        bottomSheet.show(parentFragmentManager, null)
        bottomSheet.setOnClickListener { link, action ->
            hiddenKeyboard()
            when (action) {
                Action.CONFIRM -> {
                    if (data == null) {
                        if (link.isNotBlank()) {
                            prioritizeAdapter?.addItem(Subject(link, link, -1))
                        }
                    } else {
                        data.let { prioritizeAdapter?.editItem(it, Subject(link, link, -1)) }
                    }
                }
                else -> return@setOnClickListener
            }
        }
    }

    private fun getDataListPrioritizeWeb(): MutableList<String>? {
        return prioritizeAdapter?.getData()
    }

}