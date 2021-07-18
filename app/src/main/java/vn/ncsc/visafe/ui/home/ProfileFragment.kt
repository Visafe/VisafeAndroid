package vn.ncsc.visafe.ui.home

import android.content.Intent
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vn.ncsc.visafe.R
import vn.ncsc.visafe.base.BaseActivity
import vn.ncsc.visafe.base.BaseFragment
import vn.ncsc.visafe.data.BaseCallback
import vn.ncsc.visafe.data.NetworkClient
import vn.ncsc.visafe.databinding.FragmentProfileBinding
import vn.ncsc.visafe.model.WorkspaceGroupData
import vn.ncsc.visafe.model.request.DeleteWorkSpaceRequest
import vn.ncsc.visafe.model.request.UpdateNameWorkspaceRequest
import vn.ncsc.visafe.ui.MainActivity
import vn.ncsc.visafe.ui.create.workspace.CreateWorkspaceActivity
import vn.ncsc.visafe.ui.dialog.AccountTypeDialogBottomSheet
import vn.ncsc.visafe.ui.dialog.OnClickItemAccountType
import vn.ncsc.visafe.ui.setting.SettingActivity
import vn.ncsc.visafe.ui.support.SupportCenterActivity
import vn.ncsc.visafe.utils.setOnSingClickListener

class ProfileFragment : BaseFragment<FragmentProfileBinding>() {

    override fun layoutRes(): Int = R.layout.fragment_profile

    override fun initView() {
        if ((activity as MainActivity).isLogin()) {
            (activity as MainActivity).user.observe(this, {
                if (it != null) {
                    binding.tvName.text = it.fullName
                    binding.tvPhone.text = it.phoneNumber
                }
            })
            binding.ivUser.setImageDrawable(context?.let { ContextCompat.getDrawable(it, R.drawable.ic_anonymous) })
            binding.clLogout.visibility = View.VISIBLE
        } else {
            binding.ivUser.setImageDrawable(context?.let { ContextCompat.getDrawable(it, R.drawable.ic_user_default) })
            binding.clLogout.visibility = View.GONE
        }
        binding.clSetting.setOnSingClickListener {
            if ((activity as BaseActivity).needLogin())
                return@setOnSingClickListener
            startActivity(Intent(context, SettingActivity::class.java))
        }
        binding.clSupport.setOnSingClickListener {
            startActivity(Intent(context, SupportCenterActivity::class.java))
        }
        binding.ctrlInfo.setOnSingClickListener {
            if ((activity as BaseActivity).needLogin())
                return@setOnSingClickListener
        }
        binding.clLogout.setOnSingClickListener {
            (activity as BaseActivity).logOut()
        }
    }
}