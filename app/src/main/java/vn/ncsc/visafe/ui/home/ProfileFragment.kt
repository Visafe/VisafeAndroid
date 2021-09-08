package vn.ncsc.visafe.ui.home

import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import vn.ncsc.visafe.R
import vn.ncsc.visafe.base.BaseActivity
import vn.ncsc.visafe.base.BaseFragment
import vn.ncsc.visafe.databinding.FragmentProfileBinding
import vn.ncsc.visafe.model.UserInfo
import vn.ncsc.visafe.ui.MainActivity
import vn.ncsc.visafe.ui.authentication.RegisterActivity
import vn.ncsc.visafe.ui.setting.SettingActivity
import vn.ncsc.visafe.ui.support.SupportCenterActivity
import vn.ncsc.visafe.ui.upgrade.UpgradeActivity
import vn.ncsc.visafe.utils.setOnSingClickListener

class ProfileFragment : BaseFragment<FragmentProfileBinding>() {

    override fun layoutRes(): Int = R.layout.fragment_profile
    private var userInfo: UserInfo? = null

    override fun initView() {
        if ((activity as MainActivity).isLogin()) {
            (activity as MainActivity).userInfoLiveData.observe(this, {
                if (it != null) {
                    binding.tvName.text = it.fullName
                    binding.tvPhone.text = it.phoneNumber
                }
            })
            (activity as MainActivity).userInfoLiveData.observe(this, {
                if (it != null) {
                    userInfo = it
                }
            })
            binding.ivUser.setImageDrawable(context?.let { ContextCompat.getDrawable(it, R.drawable.ic_anonymous) })
            binding.clLogout.visibility = View.VISIBLE
            binding.clUpgrade.visibility = View.VISIBLE
            binding.layoutUpgrade.llRegisterNow.visibility = View.GONE
        } else {
            binding.ivUser.setImageDrawable(context?.let { ContextCompat.getDrawable(it, R.drawable.ic_user_default) })
            binding.clLogout.visibility = View.GONE
            binding.clUpgrade.visibility = View.GONE
            binding.layoutUpgrade.llRegisterNow.visibility = View.VISIBLE
        }
        binding.clSetting.setOnSingClickListener {
            if ((activity as MainActivity).needLogin(MainActivity.POSITION_PROFILE))
                return@setOnSingClickListener
            startActivity(Intent(context, SettingActivity::class.java))
        }
        binding.clSupport.setOnSingClickListener {
            startActivity(Intent(context, SupportCenterActivity::class.java))
        }
        binding.ctrlInfo.setOnSingClickListener {
            if ((activity as MainActivity).needLogin(MainActivity.POSITION_PROFILE))
                return@setOnSingClickListener
        }
        binding.clLogout.setOnSingClickListener {
            (activity as BaseActivity).logOut()
        }
        binding.clUpgrade.setOnSingClickListener {
            if ((activity as MainActivity).needLogin(MainActivity.POSITION_PROFILE))
                return@setOnSingClickListener
            val intent = Intent(requireContext(), UpgradeActivity::class.java)
            intent.putExtra(UpgradeActivity.CURRENT_PACKAGE, userInfo?.AccountType)
            startActivity(intent)
        }
        binding.layoutUpgrade.btnUpgradeNow.setOnSingClickListener {
            if ((activity as MainActivity).needLogin(MainActivity.POSITION_PROFILE))
                return@setOnSingClickListener
            val intent = Intent(requireContext(), UpgradeActivity::class.java)
            intent.putExtra(UpgradeActivity.CURRENT_PACKAGE, userInfo?.AccountType)
            startActivity(intent)
        }
        binding.layoutUpgrade.btnRegister.setOnSingClickListener {
            resultLauncherRegisterActivity.launch(Intent(requireContext(), RegisterActivity::class.java))
        }
        binding.clShare.setOnSingClickListener {
            val sendIntent = Intent(Intent.ACTION_SEND)
            sendIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=vn.ncsc.visafe")
            sendIntent.type = "text/plain"
            startActivity(Intent.createChooser(sendIntent, null))
        }
    }

    private var resultLauncherRegisterActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                if ((activity as MainActivity).needLogin(MainActivity.POSITION_PROFILE))
                    return@registerForActivityResult
            }
        }
}