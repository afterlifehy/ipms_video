package ja.insepector.bxapp.ui.activity.mine

import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.alibaba.android.arouter.facade.annotation.Route
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.SizeUtils
import ja.insepector.base.BaseApplication
import ja.insepector.base.arouter.ARouterMap
import ja.insepector.base.bean.Street
import ja.insepector.base.ext.i18N
import ja.insepector.base.viewbase.VbBaseActivity
import ja.insepector.common.realm.RealmUtil
import ja.insepector.common.util.GlideUtils
import ja.insepector.bxapp.R
import ja.insepector.bxapp.adapter.FeeRatePagerAdapter
import ja.insepector.bxapp.databinding.ActivityFeeRateBinding
import ja.insepector.bxapp.mvvm.viewmodel.FeeRateViewModel
import ja.insepector.bxapp.ui.fragment.FeeRateFragment

@Route(path = ARouterMap.FEE_RATE)
class FeeRateActivity : VbBaseActivity<FeeRateViewModel, ActivityFeeRateBinding>(), OnClickListener {
    var tabList: MutableList<Street> = ArrayList()
    var fragmentList: MutableList<Fragment> = ArrayList()

    override fun initView() {
        GlideUtils.instance?.loadImage(binding.layoutToolbar.ivBack, ja.insepector.common.R.mipmap.ic_back_white)
        binding.layoutToolbar.tvTitle.text = i18N(ja.insepector.base.R.string.费率列表)
        binding.layoutToolbar.tvTitle.setTextColor(ContextCompat.getColor(BaseApplication.instance(), ja.insepector.base.R.color.white))

    }

    override fun initListener() {
        binding.layoutToolbar.flBack.setOnClickListener(this)
        binding.ivArrowLeft.setOnClickListener(this)
        binding.ivArrowRight.setOnClickListener(this)
        binding.stlStreet.setOnTouchListener { p0, p1 -> true }
    }

    override fun initData() {
        tabList = RealmUtil.instance?.findCheckedStreetList() as MutableList<Street>
        for (i in tabList) {
            val bundle = Bundle()
            val feeRateFragment = FeeRateFragment()
            bundle.putString("streetNo", i.streetNo)
            feeRateFragment.arguments = bundle
            fragmentList.add(feeRateFragment)
        }
        binding.vpFeeRate.adapter = FeeRatePagerAdapter(this@FeeRateActivity, fragmentList, tabList)
        binding.stlStreet.setViewPager(binding.vpFeeRate)
        binding.stlStreet.currentTab = 0
        binding.vpFeeRate.offscreenPageLimit = 2
        for (i in tabList.indices) {
            val tab = (binding.stlStreet.getChildAt(0) as LinearLayout).getChildAt(i) as RelativeLayout
            val tabTxt = tab.getChildAt(0) as TextView
            tabTxt.isSingleLine = false
            tabTxt.maxLines = 2
            val lp = tab.layoutParams
            lp.width = ScreenUtils.getScreenWidth() - SizeUtils.dp2px(72f)
            (binding.stlStreet.getChildAt(0) as LinearLayout).getChildAt(i).requestLayout()
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fl_back -> {
                onBackPressedSupport()
            }

            R.id.iv_arrowLeft -> {
                if (binding.vpFeeRate.currentItem > 0) {
                    binding.vpFeeRate.setCurrentItem(binding.vpFeeRate.currentItem - 1, true)
                }
            }

            R.id.iv_arrowRight -> {
                if (binding.vpFeeRate.currentItem < tabList.size - 1) {
                    binding.vpFeeRate.setCurrentItem(binding.vpFeeRate.currentItem + 1, true)
                }
            }
        }
    }

    override fun getVbBindingView(): ViewBinding {
        return ActivityFeeRateBinding.inflate(layoutInflater)
    }

    override fun onReloadData() {
    }

    override val isFullScreen: Boolean
        get() = true

    override fun marginStatusBarView(): View {
        return binding.layoutToolbar.ablToolbar
    }
}