package vn.ncsc.visafe.ui.home

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_switch_dns.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vn.ncsc.visafe.BuildConfig
import vn.ncsc.visafe.R
import vn.ncsc.visafe.ViSafeApp
import vn.ncsc.visafe.base.BaseFragment
import vn.ncsc.visafe.data.BaseCallback
import vn.ncsc.visafe.data.NetworkClient
import vn.ncsc.visafe.databinding.FragmentProfileBinding
import vn.ncsc.visafe.model.UserInfo
import vn.ncsc.visafe.model.request.RegisterRequest
import vn.ncsc.visafe.ui.MainActivity
import vn.ncsc.visafe.ui.SwitchDnsActivity
import vn.ncsc.visafe.ui.VipMemberActivity
import vn.ncsc.visafe.ui.authentication.RegisterActivity
import vn.ncsc.visafe.ui.create.group.access_manager.Action
import vn.ncsc.visafe.ui.dialog.VisafeDialogBottomSheet
import vn.ncsc.visafe.ui.setting.SettingActivity
import vn.ncsc.visafe.ui.support.SupportCenterActivity
import vn.ncsc.visafe.ui.upgrade.UpgradeActivity
import vn.ncsc.visafe.utils.PreferenceKey
import vn.ncsc.visafe.utils.SharePreferenceKeyHelper
import vn.ncsc.visafe.utils.setOnSingClickListener


class ProfileFragment : BaseFragment<FragmentProfileBinding>() {

    override fun layoutRes(): Int = R.layout.fragment_profile
    private var userInfo: UserInfo? = null

    @SuppressLint("SetTextI18n")
    override fun initView() {
        (activity as MainActivity).isLoadView.observe(this) {
            if ((activity as MainActivity).isLogin()) {
                (activity as MainActivity).userInfoLiveData.observe(this) {
                    if (it != null) {
                        userInfo = it
                        binding.tvName.text = it.fullName
                        binding.tvPhone.text = it.phoneNumber
                    }
                }
                binding.ivUser.setImageDrawable(context?.let {
                    ContextCompat.getDrawable(
                        it,
                        R.drawable.ic_anonymous
                    )
                })
                binding.clLogout.visibility = View.VISIBLE
                binding.clUpgrade.visibility = View.GONE
                binding.layoutUpgrade.llRegisterNow.visibility = View.GONE
            } else {
                binding.ivUser.setImageDrawable(context?.let {
                    ContextCompat.getDrawable(
                        it,
                        R.drawable.ic_user_default
                    )
                })
                binding.clLogout.visibility = View.GONE
                binding.clUpgrade.visibility = View.GONE
                binding.layoutUpgrade.llRegisterNow.visibility = View.VISIBLE
                binding.tvName.text = getString(R.string.login)
                binding.tvPhone.text = getString(R.string.login_content)
            }
        }
        val result:String =  SharePreferenceKeyHelper.getInstance(ViSafeApp()).getString(
            PreferenceKey.RADIO_BUTTON_DNS
        )
        when (result) {
            "0" -> {
                binding.statusDns.text = "Mặc định"
            }
            "1" -> {
                binding.statusDns.text = "Gia đình"
            }
            "2" -> {
                binding.statusDns.text = "Nâng cao"
            }
            else -> {
                binding.statusDns.text = "Cá nhân"
            }
        }
        binding.TextViewTestVersion.text = getString(R.string.phien_ban_thu_nghiem) + " " + getString(R.string.version_name)

        if ((activity as MainActivity).isLogin()) {
            (activity as MainActivity).userInfoLiveData.observe(this) {
                if (it != null) {
                    userInfo = it
                    binding.tvName.text = it.fullName
                    binding.tvPhone.text = it.phoneNumber
                }
            }
            binding.ivUser.setImageDrawable(context?.let { ContextCompat.getDrawable(it, R.drawable.ic_anonymous) })
            binding.clLogout.visibility = View.VISIBLE
            binding.clUpgrade.visibility = View.GONE
            binding.layoutUpgrade.llRegisterNow.visibility = View.GONE
        } else {
            binding.ivUser.setImageDrawable(context?.let { ContextCompat.getDrawable(it, R.drawable.ic_user_default) })
            binding.clLogout.visibility = View.GONE
            binding.clUpgrade.visibility = View.GONE
            binding.layoutUpgrade.llRegisterNow.visibility = View.VISIBLE
        }



        initControl()
    }

    override fun onResume() {
        super.onResume()
        val result:String =  SharePreferenceKeyHelper.getInstance(ViSafeApp()).getString(
            PreferenceKey.RADIO_BUTTON_DNS
        )
        when (result) {
            "0" -> {
                binding.statusDns.text = "Mặc định"
            }
            "1" -> {
                binding.statusDns.text = "Gia đình"
            }
            "2" -> {
                binding.statusDns.text = "Nâng cao"
            }
            else -> {
                binding.statusDns.text = "Cá nhân"
            }
        }
    }
    private fun initControl() {
        binding.clSetting.setOnSingClickListener {
            startActivity(Intent(context, SettingActivity::class.java))
        }
        binding.setDNSServer.setOnSingClickListener {
//            Toast.makeText(context, "Thiết lập DNS server", Toast.LENGTH_SHORT).show()
            startActivity(Intent(context, SwitchDnsActivity::class.java))
        }
        binding.clSupport.setOnSingClickListener {
            startActivity(Intent(context, SupportCenterActivity::class.java))
        }
        binding.ctrlInfo.setOnSingClickListener {
            if ((activity as MainActivity).needLogin(MainActivity.POSITION_PROFILE))
                return@setOnSingClickListener
            showDialogEditName()
        }
        binding.clLogout.setOnSingClickListener {
            val bottomSheet = VisafeDialogBottomSheet.newInstance(
                "",
                "Bạn có chắc chắn muốn đăng xuất không?",
                VisafeDialogBottomSheet.TYPE_CONFIRM_CANCEL
            )
            bottomSheet.show(parentFragmentManager, null)
            bottomSheet.setOnClickListener { _, action ->
                when (action) {
                    Action.CONFIRM -> {
                        SharePreferenceKeyHelper.getInstance(ViSafeApp()).clearAllData()
                        (activity as MainActivity).isLoadView.value = true
                    }
                    else -> {
                        return@setOnClickListener
                    }
                }
            }
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
        binding.clRate.setOnSingClickListener {
            val uri: Uri = Uri.parse("market://details?id=${context?.packageName}")
            val goToMarket = Intent(Intent.ACTION_VIEW, uri)
            // To count with Play market backstack, After pressing back button,
            // to taken back to our application, we need to add following flags to intent.
            goToMarket.addFlags(
                Intent.FLAG_ACTIVITY_NO_HISTORY or
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK
            )
            try {
                startActivity(goToMarket)
            } catch (e: ActivityNotFoundException) {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=$context?.packageName")
                    )
                )
            }
        }
        binding.clVip.setOnSingClickListener {
            val intent = Intent(requireContext(), VipMemberActivity::class.java)
            resultLauncherVipActivity.launch(intent)
        }
    }

    private fun showDialogEditName() {
        val name = binding.tvName.text.toString()
        val bottomSheet = VisafeDialogBottomSheet.newInstanceEdit(
            "",
            getString(R.string.edit_info_user),
            VisafeDialogBottomSheet.TYPE_INPUT_SAVE,
            getString(R.string.input_full_name),
            name
        )
        bottomSheet.show(childFragmentManager, null)
        bottomSheet.setOnClickListener { inputText, action ->
            when (action) {
                Action.SAVE -> {
                    changeNameUser(inputText)
                }
                else -> {
                    return@setOnClickListener
                }
            }
        }
    }

    private fun changeNameUser(newName: String) {
        showProgressDialog()
        val changeName = RegisterRequest(fullName = newName, email = userInfo?.email, phoneNumber = userInfo?.phoneNumber)
        val client = NetworkClient()
        val call = client.client(context = requireContext()).doChangeProfile(changeName)
        call.enqueue(BaseCallback(this, object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if (response.code() == NetworkClient.CODE_SUCCESS) {
                    binding.tvName.text = newName
                    userInfo?.fullName = newName
                    SharePreferenceKeyHelper.getInstance(ViSafeApp()).putString(
                        PreferenceKey.USER_INFO,
                        Gson().toJson(userInfo)
                    )
                }
                dismissProgress()
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.message?.let { Log.e("onFailure: ", it) }
                dismissProgress()
            }
        }))

    }

    private var resultLauncherVipActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
//                (activity as MainActivity).doGetUserInfo()
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