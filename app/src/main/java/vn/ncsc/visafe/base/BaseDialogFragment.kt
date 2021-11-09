package vn.ncsc.visafe.base

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import vn.ncsc.visafe.widget.ProgressDialogFragment
import vn.ncsc.visafe.R

open class BaseDialogFragment : DialogFragment() {
    private lateinit var progressDialog: ProgressDialogFragment
    override fun getTheme(): Int {
        return R.style.DialogTheme
    }

    fun showProgressDialog() {
        val prevFragment = childFragmentManager.findFragmentByTag(ProgressDialogFragment.TAG)
        if (prevFragment != null) {
            return
        }
        progressDialog = ProgressDialogFragment()
        progressDialog.show(childFragmentManager)
    }

    fun dismissProgress() {
        val prevFragment = childFragmentManager?.findFragmentByTag(ProgressDialogFragment.TAG)
        if (prevFragment != null) {
            (prevFragment as ProgressDialogFragment).dismiss()
        }
    }

    fun hideKeyboard(activity: Activity?) {
        if (activity != null && activity.window != null) {
            val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(activity.window.decorView.windowToken, 0)
        }
    }

    fun showToastMessage(msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
    }

}