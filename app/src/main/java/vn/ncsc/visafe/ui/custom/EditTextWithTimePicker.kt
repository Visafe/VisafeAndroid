package vn.ncsc.visafe.ui.custom

import android.app.TimePickerDialog
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import vn.ncsc.visafe.databinding.LayoutEditTextWithTimePickerBinding
import java.util.*

class EditTextWithTimePicker @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
) : FrameLayout(context, attrs, defStyle) {

    private var binding: LayoutEditTextWithTimePickerBinding? = null
    val mCurrentTime = Calendar.getInstance()
    var mHour = mCurrentTime.get(Calendar.HOUR_OF_DAY)
    var mMinute = mCurrentTime.get(Calendar.MINUTE)

    init {
        binding =
            LayoutEditTextWithTimePickerBinding.inflate(LayoutInflater.from(context), this, true)

        binding?.tvData?.setOnClickListener {
            showTimeDialog()
        }
    }

    private fun showTimeDialog() {

        val timePicker = TimePickerDialog(context, { _, hourOfDay, minute ->
            binding?.tvData?.text = String.format("%02d:%02d", hourOfDay, minute)
            mHour = hourOfDay
            mMinute = minute
        }, mHour, mMinute, true)
        timePicker.show()

    }

    fun getText(): String {
        return binding?.tvData?.text.toString()
    }

    fun setText(value: String?) {
        binding?.tvData?.text = value
    }
}