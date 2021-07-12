package com.vn.visafe_android.base

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.vn.visafe_android.data.BaseController
import com.vn.visafe_android.data.BaseResponse
import com.vn.visafe_android.data.NetworkClient
import com.vn.visafe_android.ui.authentication.LoginActivity
import com.vn.visafe_android.utils.SharePreferenceKeyHelper
import com.vn.visafe_android.widget.ProgressDialogFragment

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
        Log.e("onTimeOutSession: ", "timeout")
    }

    override fun onError(baseResponse: BaseResponse) {
        dismissProgress()
        if (baseResponse.status_code == NetworkClient.CODE_SEVER_ERROR) {
            baseResponse.msg?.let { showAlert(it) }
        } else {
            Toast.makeText(applicationContext, baseResponse.msg, Toast.LENGTH_LONG).show()
        }
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

    fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }

    fun handlerFragment(fragment: Fragment, rootId: Int, tag: String) {
        val manager: FragmentManager = supportFragmentManager
        val fragmentPopped: Boolean = manager.popBackStackImmediate(tag, 0)
        if (!fragmentPopped) { //fragment not in back stack, create it.
            val ft: FragmentTransaction = manager.beginTransaction()
            ft.setCustomAnimations(
                com.vn.visafe_android.R.anim.slide_in_left_1, com.vn.visafe_android.R.anim.slide_out_left_1,
                com.vn.visafe_android.R.anim.slide_out_right_1, com.vn.visafe_android.R.anim.slide_in_right_1
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