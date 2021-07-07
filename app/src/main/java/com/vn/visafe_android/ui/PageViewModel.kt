package com.vn.visafe_android.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.vn.visafe_android.R

class PageViewModel : ViewModel() {

    private val _index = MutableLiveData<Int>()
    val title: LiveData<Int> = Transformations.map(_index) {
        when (it) {
            1 -> R.string.splash_title1
            2 -> R.string.splash_title2
            3 -> R.string.splash_title3

            else -> { // Note the block
                0
            }
        }
    }

    val content: LiveData<Int> = Transformations.map(_index) {
        when (it) {
            1 -> R.string.splash_content1
            2 -> R.string.splash_content2
            3 -> R.string.splash_content3

            else -> { // Note the block
                0
            }
        }
    }

    val resourceId: LiveData<Int> = Transformations.map(_index) {
        when (it) {
            1 -> R.drawable.onboarding_icon1
            2 -> R.drawable.onboarding_icon2
            3 -> R.drawable.onboarding_icon3
            else -> { // Note the block
                0
            }
        }
    }

    fun setIndex(index: Int) {
        _index.value = index
    }
}