package vn.ncsc.visafe.base

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import vn.ncsc.visafe.model.OWNER
import vn.ncsc.visafe.model.TYPE_WORKSPACES
import vn.ncsc.visafe.utils.screenWidth
import vn.ncsc.visafe.utils.setBackgroundTint
import vn.ncsc.visafe.R

object BindingView {

    @JvmStatic
    @BindingAdapter(value = ["circle_image"], requireAll = false)
    fun circleImage(imageView: ImageView, type: String?) {
        val type = TYPE_WORKSPACES.fromIsTypeWorkSpaces(type)
        Glide.with(imageView.context)
            .load(type?.resDrawableIcon)
            .apply(
                RequestOptions()
                    .error(R.drawable.ic_group)
            )
            .circleCrop()
            .into(imageView)
    }

    @JvmStatic
    @BindingAdapter(value = ["set_amount_group"], requireAll = false)
    fun setAmoutGroup(textView: TextView, amout: Int?) {
        textView.text = "$amout nhóm"
    }


    @JvmStatic
    @BindingAdapter(value = ["set_type"], requireAll = false)
    fun setType(textView: TextView, type: Boolean?) {
        val owner = OWNER.fromIsOwner(type)
        textView.text = textView.context.getString(owner.title)
        textView.setBackgroundTint(
            owner.backgroundColor
        )
        textView.setTextColor(
            owner.textColor
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

    @JvmStatic
    @BindingAdapter(value = ["set_progress"], requireAll = false)
    fun setProgress(view: View, progress: Int) {
        val param = view.layoutParams
        val screenWidth = screenWidth() / 2
        param.width = progress * screenWidth / 100
        view.layoutParams = param
    }
}