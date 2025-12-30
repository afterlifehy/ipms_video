package com.peakinfo.base.ext

import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2

fun ViewPager2.bindFragment(
    fragmentActivity: FragmentActivity,
    fragments: () -> List<Fragment>
) {
    adapter = object : FragmentStateAdapter(fragmentActivity) {
        private val fragmentList = fragments.invoke()

        override fun getItemCount() = fragmentList.size
        override fun createFragment(position: Int) = fragmentList[position]
    }
}

fun View.gone(): View{
    this.visibility = View.GONE
    return this
}

fun View.show(): View{
    this.visibility = View.VISIBLE
    return this
}

fun View.hide(): View{
    this.visibility = View.INVISIBLE
    return this
}
