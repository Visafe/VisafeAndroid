package com.vn.visafe_android.ui.group.detail

import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.vn.visafe_android.R
import com.vn.visafe_android.base.BaseFragment
import com.vn.visafe_android.databinding.FragmentGroupDetailProtectTimeBinding
import com.vn.visafe_android.model.TimeProtection
import com.vn.visafe_android.ui.adapter.DaySelectorAdapter
import com.vn.visafe_android.ui.adapter.TimeProtectionAdapter
import com.vn.visafe_android.ui.create.group.access_manager.Action
import com.vn.visafe_android.ui.create.group.time.TimePickerBottomSheet

class GroupDetailProtectTimeFragment : BaseFragment<FragmentGroupDetailProtectTimeBinding>() {

    private var timeProtectionAdapter: TimeProtectionAdapter? = null

    override fun layoutRes(): Int = R.layout.fragment_group_detail_protect_time

    override fun initView() {
        initListDay()
        initListTime()
        binding.tvAddTime.setOnClickListener {
            showDialog(null)
        }
        binding.tvSave.setOnClickListener {
            activity?.finish()
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

}