package vn.ncsc.visafe.ui.pin

import android.os.Bundle
import androidx.fragment.app.FragmentManager
import vn.ncsc.visafe.R
import vn.ncsc.visafe.ViSafeApp
import vn.ncsc.visafe.base.BaseActivity
import vn.ncsc.visafe.databinding.ActivityUpdatePinBinding
import vn.ncsc.visafe.utils.PreferenceKey

class UpdatePinActivity : BaseActivity() {

    lateinit var binding: ActivityUpdatePinBinding
    private var mType = ""

    companion object {
        const val TYPE_ACTION = "TYPE_ACTION"
        const val IS_DELETE_PIN = "IS_DELETE_PIN"
        const val IS_CONFIRM_PIN = "IS_CONFIRM_PIN"
        var rootId: Int = R.id.fragment_pin
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdatePinBinding.inflate(layoutInflater)
        setContentView(binding.root)
        intent?.let {
            mType = it.getStringExtra(TYPE_ACTION).toString()
        }
        initView()
    }

    private fun initView() {
        val pin = ViSafeApp().getPreference().getString(PreferenceKey.PIN_CODE) ?: ""
        when (mType) {
            IS_DELETE_PIN -> {
                handlerFragment(ConfirmNewPinFragment.newInstance(1), rootId, tag = "ConfirmNewPinFragment")
            }
            IS_CONFIRM_PIN -> {
                handlerFragment(ConfirmNewPinFragment.newInstance(2), rootId, tag = "ConfirmNewPinFragment")
            }
            else -> {
                if (pin.isNotEmpty()) {
                    handlerFragment(CurrentPinFragment(), rootId, tag = "CurrentPinFragment")
                } else {
                    handlerFragment(CreateNewPinFragment(), rootId, tag = "CreateNewPinFragment")
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val manager: FragmentManager = supportFragmentManager
        if (manager.fragments.isEmpty()) {
            finish()
        }
    }
}