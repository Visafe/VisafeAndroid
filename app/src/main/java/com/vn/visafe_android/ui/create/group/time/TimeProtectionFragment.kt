package com.vn.visafe_android.ui.create.group.time

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.vn.visafe_android.R
import com.vn.visafe_android.base.BaseFragment
import com.vn.visafe_android.databinding.TimeProtectionFragmentBinding
import com.vn.visafe_android.model.TimeProtection
import com.vn.visafe_android.ui.adapter.DaySelectorAdapter
import com.vn.visafe_android.ui.adapter.TimeProtectionAdapter
import com.vn.visafe_android.ui.create.group.CreateGroupActivity
import com.vn.visafe_android.ui.create.group.access_manager.Action

class TimeProtectionFragment : BaseFragment<TimeProtectionFragmentBinding>() {

    companion object {
        fun newInstance(): TimeProtectionFragment {
            val args = Bundle()

            val fragment = TimeProtectionFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private var createGroupActivity: CreateGroupActivity? = null

    private var timeProtectionAdapter: TimeProtectionAdapter? = null


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is CreateGroupActivity) {
            createGroupActivity = context
        }
    }

    override fun initView() {
        initListDay()
        initListTime()
        binding.tvAddTime.setOnClickListener {
            showDialog(null)
        }
        binding.tvFinish.setOnClickListener {
            createGroupActivity?.doCreateGroup()
        }

        binding.itemDayRepeat.setOnSwitchChangeListener {
            if (it) {
                binding.rvDay.visibility = View.GONE
            } else {
                binding.rvDay.visibility = View.VISIBLE
            }
        }
    }

    private fun showDialog(data: TimeProtection?) {
        val bottomSheet = TimePickerBottomSheet.newInstance(data)
        bottomSheet.show(
            childFragmentManager,
            null
        )

        bottomSheet.setOnConfirmListener { timeProtection, action ->
            when (action) {
                Action.ADD -> {
                    timeProtectionAdapter?.addItem(timeProtection)
                }
                Action.EDIT -> {
                    data?.let { timeProtectionAdapter?.editItem(it, timeProtection) }
                }
                Action.DELETE -> {
                    timeProtectionAdapter?.deleteItem(timeProtection)
                }
            }
        }
    }

    private fun initListTime() {
        timeProtectionAdapter = TimeProtectionAdapter {
            showDialog(it)
        }
        with(binding.rvTime) {
            adapter = timeProtectionAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        timeProtectionAdapter?.setData(
            arrayListOf(
                TimeProtection("10:10", "10:10"),
                TimeProtection("11:11", "11:11"),
                TimeProtection("22:22", "22:22"),
            )
        )

    }

    private fun initListDay() {
        val days = resources.getStringArray(R.array.day_selector)
        val daySelectorAdapter = DaySelectorAdapter(datas = days.toList())
        with(binding.rvDay) {
            adapter = daySelectorAdapter
            layoutManager = GridLayoutManager(requireContext(), daySelectorAdapter.itemCount)
        }
    }

    override fun layoutRes(): Int = R.layout.time_protection_fragment
}