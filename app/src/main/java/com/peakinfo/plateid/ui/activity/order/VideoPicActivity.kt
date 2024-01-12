package com.peakinfo.plateid.ui.activity.order

import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.fastjson.JSONObject
import com.peakinfo.base.BaseApplication
import com.peakinfo.base.arouter.ARouterMap
import com.peakinfo.base.ext.i18N
import com.peakinfo.base.ext.i18n
import com.peakinfo.base.util.ToastUtil
import com.peakinfo.base.viewbase.VbBaseActivity
import com.peakinfo.common.util.GlideUtils
import com.peakinfo.plateid.R
import com.peakinfo.plateid.adapter.VideoPicPagerAdapter
import com.peakinfo.plateid.databinding.ActivityVideoPicBinding
import com.peakinfo.plateid.mvvm.viewmodel.VideoPicViewModel
import com.peakinfo.plateid.ui.fragment.VideoPicFragment

@Route(path = ARouterMap.VIDEO_PIC)
class VideoPicActivity : VbBaseActivity<VideoPicViewModel, ActivityVideoPicBinding>(), OnClickListener {
    var tabList: MutableList<String> = ArrayList()
    var fragmentList: MutableList<Fragment> = ArrayList()
    var orderNo = ""
    var from = 0

    override fun initView() {
        orderNo = intent.getStringExtra(ARouterMap.VIDEO_PIC_ORDER_NO).toString()
        from = intent.getIntExtra(ARouterMap.VIDEO_PIC_FROM, 0)

        GlideUtils.instance?.loadImage(binding.layoutToolbar.ivBack, com.peakinfo.common.R.mipmap.ic_back_white)
        binding.layoutToolbar.tvTitle.text = i18N(com.peakinfo.base.R.string.视频图片)
        binding.layoutToolbar.tvTitle.setTextColor(ContextCompat.getColor(BaseApplication.instance(), com.peakinfo.base.R.color.white))

        tabList.add(i18n(com.peakinfo.base.R.string.车辆入场))
        if (from == 0) {
            tabList.add(i18n(com.peakinfo.base.R.string.车辆出场))
        }
    }

    override fun initListener() {
        binding.layoutToolbar.flBack.setOnClickListener(this)
    }

    override fun initData() {
        showProgressDialog(20000)
        val param = HashMap<String, Any>()
        val jsonobject = JSONObject()
        jsonobject["orderNo"] = orderNo
        param["attr"] = jsonobject
        mViewModel.videoPic(param)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fl_back -> {
                onBackPressedSupport()
            }
        }
    }

    override fun startObserve() {
        super.startObserve()
        mViewModel.apply {
            videoPicLiveData.observe(this@VideoPicActivity) {
                dismissProgressDialog()
                for (i in tabList.indices) {
                    val bundle = Bundle()
                    val videoPicFragment = VideoPicFragment()
                    if (i == 0) {
                        bundle.putString("inPicture", it.inPicture)
                        bundle.putString("inVideo", it.inVideo)
                    } else {
                        bundle.putString("outPicture", it.outPicture)
                        bundle.putString("outVideo", it.outVideo)
                    }
                    bundle.putInt("followType", i)
                    videoPicFragment.arguments = bundle
                    fragmentList.add(videoPicFragment)
                }
                binding.vpVideoPic.adapter = VideoPicPagerAdapter(this@VideoPicActivity, fragmentList, tabList)
                binding.stlVideoPic.setViewPager(binding.vpVideoPic)
                binding.stlVideoPic.currentTab = 0
                binding.vpVideoPic.offscreenPageLimit = 2
            }
            errMsg.observe(this@VideoPicActivity) {
                dismissProgressDialog()
                ToastUtil.showMiddleToast(it.msg)
            }
            mException.observe(this@VideoPicActivity) {
                dismissProgressDialog()
            }
        }
    }

    override fun providerVMClass(): Class<VideoPicViewModel> {
        return VideoPicViewModel::class.java
    }

    override fun getVbBindingView(): ViewBinding {
        return ActivityVideoPicBinding.inflate(layoutInflater)
    }

    override fun onReloadData() {
    }

    override val isFullScreen: Boolean
        get() = true

    override fun marginStatusBarView(): View? {
        return binding.layoutToolbar.ablToolbar
    }

}