package vn.ncsc.visafe.base

import android.app.Activity
import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import vn.ncsc.visafe.R
import vn.ncsc.visafe.data.BaseController
import vn.ncsc.visafe.data.BaseResponse
import vn.ncsc.visafe.ui.MainActivity
import vn.ncsc.visafe.ui.authentication.LoginActivity
import vn.ncsc.visafe.utils.SharePreferenceKeyHelper
import vn.ncsc.visafe.widget.ProgressDialogFragment


open class BaseActivity : AppCompatActivity(), BaseController {

    private var progressDialog: ProgressDialogFragment? = null

    open fun showKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
    }

    fun hideKeyboard(activity: Activity?) {
        if (activity != null && activity.window != null) {
            val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(activity.window.decorView.windowToken, 0)
        }
    }

    fun showProgressDialog() {
        val prevFragment = supportFragmentManager.findFragmentByTag(ProgressDialogFragment.TAG)
        if (prevFragment != null) {
            return
        }
        if (progressDialog == null) {
            progressDialog = ProgressDialogFragment()
            progressDialog?.show(supportFragmentManager)
        }
    }

    fun dismissProgress() {
        val prevFragment = supportFragmentManager.findFragmentByTag(ProgressDialogFragment.TAG)
        if (prevFragment != null) {
            (prevFragment as ProgressDialogFragment).dismissAllowingStateLoss()
        }
        if (progressDialog != null) {
            progressDialog?.dismissAllowingStateLoss()
            progressDialog = null
        }
    }

    override fun onTimeOutSession() {
        dismissProgress()
        val builder = AlertDialog.Builder(this)
        with(builder)
        {
            setTitle("Thông báo")
            setMessage(getString(R.string.session_timed_out_content))
            setPositiveButton(
                "OK"
            ) { _, _ -> logOut() }
            show()
        }

    }

    override fun onError(baseResponse: BaseResponse) {
        dismissProgress()
        baseResponse.msg?.let { showAlert(it) }
    }

    fun showAlert(msg: String) {
        val builder = AlertDialog.Builder(this)
        with(builder)
        {
            setTitle("Thông báo")
            setMessage(msg)
            setPositiveButton(
                "OK"
            ) { _, _ -> finish() }
            show()
        }
    }

    fun copyToClipboard(text: String) {
        if (text.isNotEmpty()) {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("", text)
            clipboard.setPrimaryClip(clip)
            showToast("Copy success")
        }
    }

    fun shareLink(text: String) {
        val shareLink = Intent(Intent.ACTION_SEND)
        shareLink.type = "text/plain"
        shareLink.putExtra(Intent.EXTRA_TEXT, text)
        shareLink.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivity(Intent.createChooser(shareLink, text))
    }

    fun logOut() {
        SharePreferenceKeyHelper.getInstance(application).clearAllData()
        startActivity(
            Intent(this, MainActivity::class.java)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        )
        finish()
    }

    fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }

    fun handlerFragment(fragment: Fragment, rootId: Int, tag: String) {
        val manager: FragmentManager = supportFragmentManager
        val fragmentPopped: Boolean = manager.popBackStackImmediate(tag, 0)
        if (!fragmentPopped) { //fragment not in back stack, create it.
            val ft: FragmentTransaction = manager.beginTransaction()
            ft.setCustomAnimations(
                R.anim.slide_in_left_1, R.anim.slide_out_left_1,
                R.anim.slide_out_right_1, R.anim.slide_in_right_1
            )
            ft.replace(rootId, fragment)
            ft.addToBackStack(tag)
            ft.commit()
        }
    }

    fun isLogin(): Boolean {
        return SharePreferenceKeyHelper.getInstance(application).isLogin()
    }

    fun needLogin(): Boolean {
        if (SharePreferenceKeyHelper.getInstance(application).isLogin())
            return false
        startActivity(Intent(this, LoginActivity::class.java))
        return true
    }
}