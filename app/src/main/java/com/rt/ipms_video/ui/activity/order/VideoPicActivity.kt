package com.rt.ipms_video.ui.activity.order

import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.fastjson.JSONObject
import com.rt.base.BaseApplication
import com.rt.base.arouter.ARouterMap
import com.rt.base.ext.i18N
import com.rt.base.ext.i18n
import com.rt.base.util.ToastUtil
import com.rt.base.viewbase.VbBaseActivity
import com.rt.common.util.GlideUtils
import com.rt.ipms_video.R
import com.rt.ipms_video.adapter.VideoPicPagerAdapter
import com.rt.ipms_video.databinding.ActivityVideoPicBinding
import com.rt.ipms_video.mvvm.viewmodel.VideoPicViewModel
import com.rt.ipms_video.ui.fragment.VideoPicFragment

@Route(path = ARouterMap.VIDEO_PIC)
class VideoPicActivity : VbBaseActivity<VideoPicViewModel, ActivityVideoPicBinding>(), OnClickListener {
    var tabList: MutableList<String> = ArrayList()
    var fragmentList: MutableList<Fragment> = ArrayList()
    var orderNo = ""

    override fun initView() {
        orderNo = intent.getStringExtra(ARouterMap.VIDEO_PIC_ORDER_NO).toString()

        GlideUtils.instance?.loadImage(binding.layoutToolbar.ivBack, com.rt.common.R.mipmap.ic_back_white)
        binding.layoutToolbar.tvTitle.text = i18N(com.rt.base.R.string.欠费订单详情)
        binding.layoutToolbar.tvTitle.setTextColor(ContextCompat.getColor(BaseApplication.instance(), com.rt.base.R.color.white))

        tabList.add(i18n(com.rt.base.R.string.车辆入场))
        tabList.add(i18n(com.rt.base.R.string.车辆出场))

    }

    override fun initListener() {
        binding.layoutToolbar.flBack.setOnClickListener(this)
    }

    override fun initData() {
        showProgressDialog()
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
                ToastUtil.showToast(it.msg)
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