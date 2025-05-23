package com.peakinfo.plateid.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.peakinfo.common.view.flycotablayout.adapter.SlidingAdapter

/**
 * Created by huy  on 2023/3/1.
 */
class VideoPicPagerAdapter(
    activity: FragmentActivity,
    val fragmentList: List<Fragment>,
    val tabList: MutableList<String>
) :
    SlidingAdapter(activity) {

    override fun getItemCount(): Int {
        return fragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }

    override fun getPageTitle(position: Int): CharSequence {
        return tabList[position]
    }
}