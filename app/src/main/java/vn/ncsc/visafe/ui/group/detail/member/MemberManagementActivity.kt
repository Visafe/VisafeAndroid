package vn.ncsc.visafe.ui.group.detail.member

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import vn.ncsc.visafe.R
import vn.ncsc.visafe.base.BaseActivity
import vn.ncsc.visafe.databinding.ActivityMemberManagementBinding
import vn.ncsc.visafe.model.GroupData
import vn.ncsc.visafe.model.UsersGroupInfo
import vn.ncsc.visafe.ui.create.group.access_manager.Action
import vn.ncsc.visafe.ui.dialog.VisafeDialogBottomSheet
import vn.ncsc.visafe.utils.setOnSingClickListener

class MemberManagementActivity : BaseActivity(), MemberManagerAdapter.OnSelectItemListener {

    lateinit var binding: ActivityMemberManagementBinding
    private var groupData: GroupData? = null
    private var listUsersGroupInfo: MutableList<UsersGroupInfo> = mutableListOf()
    private var groupNumber: String? = ""
    private var memberManagerAdapter: MemberManagerAdapter? = null

    companion object {
        const val KEY_GROUP_NUMBER = "KEY_GROUP_NUMBER"
        const val KEY_DATA = "KEY_DATA"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMemberManagementBinding.inflate(layoutInflater)
        setContentView(binding.root)
        intent?.let {
            groupData = it.getParcelableExtra(KEY_DATA)
            groupNumber = it.getStringExtra(KEY_GROUP_NUMBER)
        }
        initView()
        initControl()
    }

    @SuppressLint("SetTextI18n")
    private fun initView() {
        groupData?.let {
            listUsersGroupInfo.clear()
            it.listUsersGroupInfo?.toMutableList()?.let { it1 -> listUsersGroupInfo.addAll(it1) }
            binding.tvNumberMember.text = "${it.listUsersGroupInfo?.size} thành viên"
            binding.tvContent.text = "${groupNumber}: ${it.name}"
        }
        memberManagerAdapter = MemberManagerAdapter(this)
        memberManagerAdapter?.setData(listUsersGroupInfo)
        binding.rcvMember.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
        binding.rcvMember.adapter = memberManagerAdapter
    }

    private fun initControl() {
        binding.ivBack.setOnSingClickListener { finish() }
    }

    override fun onSelectItem(item: UsersGroupInfo, position: Int) {
    }

    override fun onMore(item: UsersGroupInfo, position: Int) {
        val fullName = item.fullName
        val bottomSheet = VisafeDialogBottomSheet.newInstance(
            "Thành viên",
            fullName,
            VisafeDialogBottomSheet.TYPE_EDIT,
            "Cấp quyền làm Quản trị viên",
            "Xóa thành viên khỏi nhóm"
        )
        bottomSheet.show(supportFragmentManager, null)
        bottomSheet.setOnClickListener { _, action ->
            when (action) {
                Action.DELETE -> {
                    showDialogDeleteGroup(fullName)
                }
                Action.EDIT -> {
                }
                else -> {
                    return@setOnClickListener
                }
            }
        }
    }

    private fun showDialogDeleteGroup(fullName: String?) {
        val bottomSheet = VisafeDialogBottomSheet.newInstance(
            "",
            getString(R.string.delete_group_content, "thành viên ${fullName} khỏi nhóm?"),
            VisafeDialogBottomSheet.TYPE_CONFIRM
        )
        bottomSheet.show(supportFragmentManager, null)
        bottomSheet.setOnClickListener { _, action ->
            when (action) {
                Action.CONFIRM -> {

                }
                else -> {
                    return@setOnClickListener
                }
            }
        }
    }
}