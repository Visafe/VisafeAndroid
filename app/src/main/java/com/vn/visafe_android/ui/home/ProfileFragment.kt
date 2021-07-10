package com.vn.visafe_android.ui.home

import android.content.Intent
import com.vn.visafe_android.R
import com.vn.visafe_android.base.BaseFragment
import com.vn.visafe_android.databinding.FragmentProfileBinding
import com.vn.visafe_android.model.WorkspaceGroupData
import com.vn.visafe_android.ui.MainActivity
import com.vn.visafe_android.ui.create.workspace.CreateWorkspaceActivity
import com.vn.visafe_android.ui.dialog.AccountTypeDialogBottomSheet
import com.vn.visafe_android.ui.dialog.OnClickItemAccountType

class ProfileFragment : BaseFragment<FragmentProfileBinding>() {

    override fun layoutRes(): Int = R.layout.fragment_profile
    private var positionTypeChoose = 0

    override fun initView() {
        (activity as MainActivity).user.observe(this, {
            if (it != null) {
                binding.tvName.text = it.fullName
                binding.tvPhone.text = it.phoneNumber
            }
        })

        binding.clType.setOnClickListener {
            val bottomSheet = AccountTypeDialogBottomSheet.newInstance(createList(), positionTypeChoose)
            bottomSheet.show(parentFragmentManager, null)
            bottomSheet.onClickItemAccountType = object : OnClickItemAccountType {
                override fun onChoosse(data: WorkspaceGroupData, position: Int) {
                    positionTypeChoose = position
                }

                override fun doDeleteWorkSpace(data: WorkspaceGroupData, position: Int) {

                }

                override fun doUpdateNameWorkSpace(
                    data: WorkspaceGroupData,
                    name: String,
                    position: Int
                ) {

                }

                override fun add() {
                    startActivity(Intent(context, CreateWorkspaceActivity::class.java))
                }

            }
        }
    }

    private fun createList() : ArrayList<WorkspaceGroupData> {
        val list: ArrayList<WorkspaceGroupData> = ArrayList()
        list.add(WorkspaceGroupData("1", "Gia đình và nhóm", false, "", 1, true, false, false, false, null, null, null, null, true))
        list.add(WorkspaceGroupData("2", "Pit Studio", false, "", 1, false, false, false, false, null, null, null, null, false))
        list.add(WorkspaceGroupData("3", "Cihub Archeitect", false, "", 1, false, false, false, false, null, null, null, null, false))
        return list
    }
}