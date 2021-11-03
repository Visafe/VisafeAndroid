package vn.ncsc.visafe.ui.dialog

import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import vn.ncsc.visafe.R
import vn.ncsc.visafe.base.BaseDialogBottomSheet
import vn.ncsc.visafe.databinding.LayoutAccountTypeDialogBottomSheetBinding
import vn.ncsc.visafe.model.WorkspaceGroupData
import vn.ncsc.visafe.ui.adapter.AccountTypeAdapter
import vn.ncsc.visafe.ui.adapter.OnClickMenu
import vn.ncsc.visafe.ui.create.group.access_manager.Action

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
        val positionSelected = arguments?.getInt(POSITON_SELECTED, 0)
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
                    VisafeDialogBottomSheet.TYPE_EDIT_DELETE,
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
        positionSelected?.let { adapter?.setSelected(it) }
        binding.rvGroup.adapter = adapter
        binding.btnAdd.setOnClickListener {
            dismiss()
            onClickItemAccountType?.add()
        }
    }

    fun deleteWorkSpace(data: WorkspaceGroupData, position: Int) {
        adapter?.deleteItem(data, position)
    }

    fun updateNameWorkSpace(newName: String, position: Int) {
        adapter?.updateName(newName, position)
    }

    private fun showDialogDeleteWorkSpace(data: WorkspaceGroupData, position: Int) {
        val bottomSheet = VisafeDialogBottomSheet.newInstance(
            "",
            getString(R.string.delete_workspace_content, data.name),
            VisafeDialogBottomSheet.TYPE_CONFIRM_CANCEL
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
                VisafeDialogBottomSheet.TYPE_INPUT_SAVE,
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