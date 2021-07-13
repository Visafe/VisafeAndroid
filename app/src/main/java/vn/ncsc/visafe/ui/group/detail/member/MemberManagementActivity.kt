package vn.ncsc.visafe.ui.group.detail.member

import android.os.Bundle
import vn.ncsc.visafe.base.BaseActivity
import vn.ncsc.visafe.databinding.ActivityMemberManagementBinding
import vn.ncsc.visafe.model.GroupData
import vn.ncsc.visafe.model.UsersGroupInfo

class MemberManagementActivity : BaseActivity() {

    lateinit var binding: ActivityMemberManagementBinding
    private var groupData: GroupData? = null
    private var listUsersGroupInfo: MutableList<UsersGroupInfo> = mutableListOf()

    companion object {
        const val KEY_DATA = "KEY_DATA"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMemberManagementBinding.inflate(layoutInflater)
        setContentView(binding.root)
        intent?.let {
            groupData = it.getParcelableExtra(KEY_DATA)
        }
        initView()
        initControl()
    }

    private fun initView() {
        groupData?.let {
            listUsersGroupInfo.clear()
            it.listUsersGroupInfo?.toMutableList()?.let { it1 -> listUsersGroupInfo.addAll(it1) }
        }
    }

    private fun initControl() {
    }
}