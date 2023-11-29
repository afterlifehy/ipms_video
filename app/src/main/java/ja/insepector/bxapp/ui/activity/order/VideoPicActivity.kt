package ja.insepector.bxapp.ui.activity.order

import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.fastjson.JSONObject
import ja.insepector.base.BaseApplication
import ja.insepector.base.arouter.ARouterMap
import ja.insepector.base.ext.i18N
import ja.insepector.base.ext.i18n
import ja.insepector.base.util.ToastUtil
import ja.insepector.base.viewbase.VbBaseActivity
import ja.insepector.common.util.GlideUtils
import ja.insepector.bxapp.R
import ja.insepector.bxapp.adapter.VideoPicPagerAdapter
import ja.insepector.bxapp.databinding.ActivityVideoPicBinding
import ja.insepector.bxapp.mvvm.viewmodel.VideoPicViewModel
import ja.insepector.bxapp.ui.fragment.VideoPicFragment

@Route(path = ARouterMap.VIDEO_PIC)
class VideoPicActivity : VbBaseActivity<VideoPicViewModel, ActivityVideoPicBinding>(), OnClickListener {
    var tabList: MutableList<String> = ArrayList()
    var fragmentList: MutableList<Fragment> = ArrayList()
    var orderNo = ""

    override fun initView() {
        orderNo = intent.getStringExtra(ARouterMap.VIDEO_PIC_ORDER_NO).toString()

        GlideUtils.instance?.loadImage(binding.layoutToolbar.ivBack, ja.insepector.common.R.mipmap.ic_back_white)
        binding.layoutToolbar.tvTitle.text = i18N(ja.insepector.base.R.string.欠费订单详情)
        binding.layoutToolbar.tvTitle.setTextColor(ContextCompat.getColor(BaseApplication.instance(), ja.insepector.base.R.color.white))

        tabList.add(i18n(ja.insepector.base.R.string.车辆入场))
        tabList.add(i18n(ja.insepector.base.R.string.车辆出场))

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