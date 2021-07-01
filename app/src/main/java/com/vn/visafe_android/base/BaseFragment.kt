package com.vn.visafe_android.base

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.vn.visafe_android.data.BaseController
import com.vn.visafe_android.data.BaseResponse

abstract class BaseFragment<T : ViewDataBinding> : Fragment(), BaseController {
    protected lateinit var binding: T

    @LayoutRes
    protected abstract fun layoutRes(): Int

    protected abstract fun initView()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, layoutRes(), container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        initView()
    }

    override fun onTimeOutSession() {
        if (activity != null && activity is BaseActivity) {
            (activity as BaseActivity?)?.onTimeOutSession()
        }
    }

    override fun onError(baseResponse: BaseResponse) {
        if (activity != null && activity is BaseActivity) {
            (activity as BaseActivity?)?.onError(baseResponse)
        }
    }

    open fun backFragment() {
        activity?.let {
            (it as BaseActivity).hideKeyboard(it)
            val manager: FragmentManager = it.supportFragmentManager ?: return
            if (manager.backStackEntryCount > 1) {
                manager.popBackStack()
            } else {
                it.finish()
            }
        }
    }

    open fun showProgressDialog() {
        if (activity is BaseActivity) {
            (activity as BaseActivity).showProgressDialog()
        }
    }

    open fun dismissProgress() {
        if (activity is BaseActivity) {
            (activity as BaseActivity).dismissProgress()
        }
    }

    open fun hiddenKeyboard() {
        if (activity is BaseActivity) {
            (activity as BaseActivity).hideKeyboard(activity)
        }
    }

    open fun showKeyboard() {
        if (activity is BaseActivity) {
            (activity as BaseActivity).showKeyboard()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    fun setHideKeyboardFocus(view: View) {
        if (view !is EditText) {
            view.setOnTouchListener { _: View?, _: MotionEvent? ->
                if (activity != null) {
                    hiddenKeyboard()
                }
                false
            }
        }

        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val innerView = view.getChildAt(i)
                setHideKeyboardFocus(innerView)
            }
        }
    }
}