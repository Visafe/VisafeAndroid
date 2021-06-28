package com.vn.visafe_android.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

abstract class BaseFragment<T : ViewDataBinding> : Fragment() {
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
}