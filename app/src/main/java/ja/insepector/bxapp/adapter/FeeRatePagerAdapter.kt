package ja.insepector.bxapp.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import ja.insepector.base.bean.Street

/**
 * Created by huy  on 2023/3/1.
 */
class FeeRatePagerAdapter(
    activity: FragmentActivity,
    val fragmentList: List<Fragment>,
    val tabList: MutableList<Street>
) :
    ja.insepector.common.view.flycotablayout.adapter.SlidingAdapter(activity) {

    override fun getItemCount(): Int {
        return fragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }

    override fun getPageTitle(position: Int): CharSequence {
        return tabList[position].streetName
    }
}