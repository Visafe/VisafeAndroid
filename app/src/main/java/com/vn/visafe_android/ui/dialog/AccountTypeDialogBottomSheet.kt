package com.vn.visafe_android.ui.dialog

import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import com.vn.visafe_android.R
import com.vn.visafe_android.ViSafeApp
import com.vn.visafe_android.base.BaseDialogBottomSheet
import com.vn.visafe_android.databinding.LayoutAccountTypeDialogBottomSheetBinding
import com.vn.visafe_android.model.WorkspaceGroupData
import com.vn.visafe_android.ui.adapter.AccountTypeAdapter
import com.vn.visafe_android.ui.create.group.access_manager.Action
import com.vn.visafe_android.ui.home.OnClickMenu

class AccountTypeDialogBottomSheet :
    BaseDialogBottomSheet<LayoutAccountTypeDialogBottomSheetBinding>() {

    companion object {
        const val DATA_WORKSPACE = "DATA_WORKSPACE"
        const val POSITON_SELECTED = "POSITON_SELECTED"
        fun newInstance(
            listMenu: List<WorkspaceGroupData>,
            postionSelected: Int
        ): AccountTypeDialogBottomSheet {
            val dialog = AccountTypeDialogBottomSheet()
            dialog.arguments = bundleOf(
                Pair(DATA_WORKSPACE, listMenu),
                Pair(POSITON_SELECTED, postionSelected)
            )
            return dialog
        }
    }

    private var adapter: AccountTypeAdapter? = null
    var onClickItemAccountType: OnClickItemAccountType? = null

    override fun layoutRes(): Int = R.layout.layout_account_type_dialog_bottom_sheet

    override fun initView() {
        val listMenu =
            arguments?.getParcelableArrayList<WorkspaceGroupData>(DATA_WORKSPACE) as ArrayList<WorkspaceGroupData>
        val postionSelected = arguments?.getInt(POSITON_SELECTED, 0)
        binding.rvGroup.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        adapter = AccountTypeAdapter(listMenu, object : OnClickMenu {
            override fun onClickMenu(data: WorkspaceGroupData, position: Int) {
                adapter?.setSelected(position)
                data.let {
                    onClickItemAccountType?.onChoosse(it, position)
                    dismiss()
                }
            }

            override fun onMoreGroup(data: WorkspaceGroupData, position: Int) {
                dialog?.hide()
                val bottomSheet = VisafeDialogBottomSheet.newInstance(
                    getString(R.string.workspaces),
                    data.name!!,
                    VisafeDialogBottomSheet.TYPE_EDIT,
                    getString(R.string.edit_workspace),
                    getString(R.string.delete_workspace)
                )
                bottomSheet.show(parentFragmentManager, null)
                bottomSheet.setOnClickListener { _, action ->
                    when (action) {
                        Action.DELETE -> {
                            showDialogDeleteWorkSpace(data, position)
                            bottomSheet.dismiss()
                        }
                        Action.EDIT -> {
                            showDialogUpdateNameWorkSpace(data, position)
                            bottomSheet.dismiss()
                        }
                        else -> {
                            return@setOnClickListener
                        }
                    }
                }
            }
        })
        adapter?.setSelected(postionSelected!!)
        binding.rvGroup.adapter = adapter
        binding.btnAdd.setOnClickListener {
            dismiss()
            onClickItemAccountType?.add()
        }
    }

    private fun showDialogDeleteWorkSpace(data: WorkspaceGroupData, position: Int) {
        val bottomSheet = VisafeDialogBottomSheet.newInstance(
            "",
            getString(R.string.delete_workspace_content, data.name),
            VisafeDialogBottomSheet.TYPE_CONFIRM
        )
        bottomSheet.show(parentFragmentManager, null)
        bottomSheet.setOnClickListener { inputText, action ->
            when (action) {
                Action.CONFIRM -> {
                    onClickItemAccountType?.doDeleteWorkSpace(data, position)
                    bottomSheet.dismiss()
                }
                else -> {
                    return@setOnClickListener
                }
            }
        }
        dismiss()
    }

    private fun showDialogUpdateNameWorkSpace(data: WorkspaceGroupData, position: Int) {
        val bottomSheet = data.name?.let {
            VisafeDialogBottomSheet.newInstanceEdit(
                "",
                getString(R.string.update_name_workspace),
                VisafeDialogBottomSheet.TYPE_SAVE,
                "", it
            )
        }
        bottomSheet?.show(parentFragmentManager, null)
        bottomSheet?.setOnClickListener { inputText, action ->
            when (action) {
                Action.SAVE -> {
                    onClickItemAccountType?.doUpdateNameWorkSpace(data, inputText, position)
                    bottomSheet?.dismiss()
                }
                else -> {
                    return@setOnClickListener
                }
            }
        }
    }
}

interface OnClickItemAccountType {
    fun onChoosse(data: WorkspaceGroupData, position: Int)

    fun doDeleteWorkSpace(data: WorkspaceGroupData, position: Int)

    fun doUpdateNameWorkSpace(data: WorkspaceGroupData, name: String, position: Int)

    fun add()
}