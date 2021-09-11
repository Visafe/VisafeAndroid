package vn.ncsc.visafe.ui.create.group.access_manager

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import vn.ncsc.visafe.ui.create.group.CreateGroupActivity
import vn.ncsc.visafe.ui.dialog.VisafeDialogBottomSheet
import vn.ncsc.visafe.R
import vn.ncsc.visafe.base.BaseFragment
import vn.ncsc.visafe.databinding.FragmentAccessManagerBinding
import vn.ncsc.visafe.model.Subject
import vn.ncsc.visafe.ui.adapter.WebsiteCreatGroupAdapter
import vn.ncsc.visafe.utils.OnSingleClickListener
import vn.ncsc.visafe.utils.setOnSingClickListener

class AccessManagerFragment : BaseFragment<FragmentAccessManagerBinding>() {

    companion object {
        const val KEY_SELECTED = "KEY_SELECTED"
        fun newInstance(selected: Boolean, onSaveAccessManager: OnSaveAccessManager): AccessManagerFragment {
            val args = Bundle()
            args.putBoolean(KEY_SELECTED, selected)
            val fragment = AccessManagerFragment()
            fragment.arguments = args
            fragment.onSaveAccessManager = onSaveAccessManager
            return fragment
        }
    }

    private lateinit var onSaveAccessManager: OnSaveAccessManager
    private var isSelected: Boolean = false
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            isSelected = it.getBoolean(KEY_SELECTED)
        }
    }

    override fun layoutRes(): Int = R.layout.fragment_access_manager

    override fun initView() {
        binding.toolbar.setOnClickLeftButton(object : OnSingleClickListener() {
            override fun onSingleClick(view: View) {
                createGroupActivity?.onBackPressed()
            }
        })
        val dataItemApp = arrayListOf(
            Subject(
                "Facebook",
                "facebook",
                R.drawable.ic_facebook
            ),
            Subject(
                "Zalo",
                "zalo",
                R.drawable.ic_zalo
            ),
            Subject(
                "Tiktok",
                "tiktok",
                R.drawable.ic_tiktok
            ),
            Subject(
                "Instagram",
                "instagram",
                R.drawable.ic_instagram
            ),
            Subject(
                "Tinder",
                "tinder",
                R.drawable.ic_tinder
            ),
            Subject(
                "Twitter",
                "twitter",
                R.drawable.ic_twitter
            ),
            Subject(
                "Netflix",
                "netflix",
                R.drawable.ic_netflix
            ),
            Subject(
                "Reddit",
                "reddit",
                R.drawable.ic_reddit
            ),
            Subject(
                "9gag",
                "9gag",
                R.drawable.ic_9gag
            ),
            Subject(
                "Discord",
                "discord",
                R.drawable.ic_discord
            )
        )

        mDataBlock = arrayListOf(
//            Subject(
//                "https://www.youtube.com/",
//                "https://www.youtube.com/",
//                R.drawable.ic_instagram
//            ),
//            Subject(
//                "https://www.facebook.com/",
//                "https://www.facebook.com/",
//                R.drawable.ic_facebook
//            ),
//            Subject(
//                "https://gmail.com/",
//                "https://gmail.com/",
//                R.drawable.ic_book
//            )
        )

        mDataPrioritize = arrayListOf(
//            Subject(
//                "https://www.youtube.com/",
//                "https://www.youtube.com/",
//                R.drawable.ic_instagram
//            ),
//            Subject(
//                "https://www.facebook.com/",
//                "https://www.facebook.com/",
//                R.drawable.ic_facebook
//            ),
//            Subject(
//                "https://gmail.com/",
//                "https://gmail.com/",
//                R.drawable.ic_book
//            )
        )
        binding.itemApp.setData(dataItemApp)
        binding.itemApp.setExpanded(false)

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
            createGroupActivity?.createGroupRequest?.game_ads_enabled = binding.itemGame.isChecked()
            val gson = Gson()
            Log.e(
                "AccessManagerFragment: ",
                "" + gson.toJson(createGroupActivity?.createGroupRequest)
            )
            createGroupActivity?.onBackPressed()
            onSaveAccessManager.onSaveAccessManager(
                binding.itemApp.isChecked() || binding.itemGame.isChecked()
                        || mDataPrioritize.isNotEmpty() || mDataBlock.isNotEmpty()
            )
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
        setCheckedForAll(isSelected)
        binding.btnReset.setOnSingClickListener {
            setCheckedForAll(false)
        }
    }

    private fun setCheckedForAll(isSelected: Boolean) {
        binding.itemApp.setChecked(isSelected)
        binding.itemGame.setChecked(isSelected)
    }

    private fun showDialogEditBlock(data: Subject) {
        val bottomSheet = VisafeDialogBottomSheet.newInstance(
            requireContext().getString(R.string.websites),
            data.title,
            VisafeDialogBottomSheet.TYPE_EDIT_DELETE,
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
            VisafeDialogBottomSheet.TYPE_INPUT_CONFIRM,
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
            VisafeDialogBottomSheet.TYPE_EDIT_DELETE,
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
            VisafeDialogBottomSheet.TYPE_INPUT_CONFIRM,
            requireContext().getString(R.string.input_website_prioritized),
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

    interface OnSaveAccessManager {
        fun onSaveAccessManager(isSave: Boolean)
    }
}