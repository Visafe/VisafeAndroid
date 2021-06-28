package com.vn.visafe_android.base

import android.graphics.Color
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.vn.visafe_android.R
import com.vn.visafe_android.model.StatusGroup
import com.vn.visafe_android.utils.setBackgroundTint

object BindingView {

    @JvmStatic
    @BindingAdapter(value = ["circle_image"], requireAll = false)
    fun circleImage(imageView: ImageView, imageUrl: String?) {
        Glide.with(imageView.context)
            .load(imageUrl)
            .apply(
                RequestOptions()
                    .error(R.drawable.ic_group)
            )
            .circleCrop()
            .into(imageView)
    }

    @JvmStatic
    @BindingAdapter(value = ["set_amout_group"], requireAll = false)
    fun setAmoutGroup(textView: TextView, amout: Int?) {
        textView.text = "$amout nhóm"
    }

    @JvmStatic
    @BindingAdapter(value = ["set_status"], requireAll = false)
    fun setStatus(textView: TextView, status: String?) {
        textView.text = status
        textView.setBackgroundTint(
            when (status) {
                StatusGroup.QUAN_TRI.status -> R.color.color_1AFFB31F
                StatusGroup.THANH_VIEN.status -> R.color.color_1A33B6FF
                else -> R.color.color_1AFFB31F
            }
        )
        textView.setTextColor(
            when (status) {
                StatusGroup.QUAN_TRI.status -> Color.parseColor("#FFB31F")
                StatusGroup.THANH_VIEN.status -> Color.parseColor("#33B6FF")
                else -> Color.parseColor("#FFB31F")
            }
        )
    }

    @JvmStatic
    @BindingAdapter(value = ["show_more_action"], requireAll = false)
    fun showMoreAction(image: ImageView, isSelected: Boolean?) {
        if (isSelected!!) {
            image.visibility = View.VISIBLE
            image.isEnabled = true
        } else {
            image.visibility = View.INVISIBLE
            image.isEnabled = false
        }
    }

}