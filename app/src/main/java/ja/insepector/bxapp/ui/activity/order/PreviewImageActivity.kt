package ja.insepector.bxapp.ui.activity.order

import android.Manifest
import android.annotation.SuppressLint
import android.view.View
import android.view.View.OnClickListener
import androidx.viewbinding.ViewBinding
import androidx.viewpager.widget.ViewPager
import com.alibaba.android.arouter.facade.annotation.Route
import ja.insepector.base.arouter.ARouterMap
import ja.insepector.base.viewbase.VbBaseActivity
import ja.insepector.bxapp.adapter.SamplePagerAdapter
import ja.insepector.bxapp.databinding.ActivityPreviewImageBinding
import ja.insepector.bxapp.mvvm.viewmodel.PreviewImageViewModel
import com.tbruyelle.rxpermissions3.RxPermissions
import java.util.ArrayList

@Route(path = ARouterMap.PREVIEW_IMG)
class PreviewImageActivity : VbBaseActivity<PreviewImageViewModel, ActivityPreviewImageBinding>(), OnClickListener {
    var index = 0
    var imageList: ArrayList<String> = ArrayList()
    var samplePagerAdapter: SamplePagerAdapter? = null

    @SuppressLint("SetTextI18n")
    override fun initView() {
        index = intent.getIntExtra(ARouterMap.IMG_INDEX, 0)
        imageList = intent.getStringArrayListExtra(ARouterMap.IMG_LIST) as ArrayList<String>

        samplePagerAdapter = SamplePagerAdapter(this, imageList)
        binding.hvpViewpager.adapter = samplePagerAdapter
        binding.hvpViewpager.currentItem = index
        binding.hvpViewpager.offscreenPageLimit = imageList.size
        binding.rtvIndicator.text = "${(index + 1)}/${imageList.size}"

        binding.hvpViewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                binding.rtvIndicator.text = "${(position + 1)}/${imageList.size}"
            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })

        binding.hvpViewpager.setOnClickListener(this)

        var rxPermissions = RxPermissions(this@PreviewImageActivity)
        rxPermissions.request(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
            .subscribe {
                if (it) {
                } else {

                }
            }
    }

    override fun initListener() {
    }

    override fun initData() {
    }

    override fun onClick(v: View?) {
    }

    override fun getVbBindingView(): ViewBinding {
        return ActivityPreviewImageBinding.inflate(layoutInflater)
    }

    override fun onReloadData() {
    }

    override val isFullScreen: Boolean
        get() = true

    override fun providerVMClass(): Class<PreviewImageViewModel>? {
        return PreviewImageViewModel::class.java
    }
}