package vn.ncsc.visafe.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter


class ScreenSlidePagerAdapter(
    fm: FragmentManager
) : FragmentStatePagerAdapter(
    fm,
    BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
) {
    private val fragmentList = ArrayList<Fragment>()
    private val titleList = ArrayList<String>()

    fun addFragment(fragment: Fragment, title: String) {
        fragmentList.add(fragment)
        titleList.add(title)
    }

    override fun getCount(): Int = fragmentList.size

    override fun getItem(position: Int): Fragment {
        if (position >= 0 && position < fragmentList.size)
            return fragmentList[position]
        return Fragment()
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return titleList[position]
    }
}