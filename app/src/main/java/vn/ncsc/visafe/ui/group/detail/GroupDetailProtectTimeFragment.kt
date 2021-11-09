package vn.ncsc.visafe.ui.group.detail

import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import vn.ncsc.visafe.R
import vn.ncsc.visafe.base.BaseFragment
import vn.ncsc.visafe.databinding.FragmentGroupDetailProtectTimeBinding
import vn.ncsc.visafe.model.TimeProtection
import vn.ncsc.visafe.ui.adapter.DaySelectorAdapter
import vn.ncsc.visafe.ui.adapter.TimeProtectionAdapter
import vn.ncsc.visafe.ui.create.group.access_manager.Action
import vn.ncsc.visafe.ui.create.group.time.TimePickerBottomSheet

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