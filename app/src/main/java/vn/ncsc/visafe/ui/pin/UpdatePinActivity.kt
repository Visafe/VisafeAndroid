package vn.ncsc.visafe.ui.pin

import android.os.Bundle
import androidx.fragment.app.FragmentManager
import vn.ncsc.visafe.base.BaseActivity
import vn.ncsc.visafe.R
import vn.ncsc.visafe.databinding.ActivityUpdatePinBinding

class UpdatePinActivity : BaseActivity() {

    lateinit var binding: ActivityUpdatePinBinding

    companion object {
        var rootId: Int = R.id.fragment_pin
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdatePinBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        handlerFragment(CurrentPinFragment(), rootId, tag = "CurrentPinFragment")
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val manager: FragmentManager = supportFragmentManager
        if (manager.fragments.isEmpty()) {
            finish()
        }
    }
}