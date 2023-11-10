package com.rt.ipms_video.ui.fragment

import android.content.Intent
import android.view.View
import android.view.View.OnClickListener
import android.widget.ImageView
import androidx.viewbinding.ViewBinding
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.rt.base.BaseApplication
import com.rt.base.arouter.ARouterMap
import com.rt.base.ext.gone
import com.rt.base.help.ActivityCacheManager
import com.rt.base.viewbase.VbBaseFragment
import com.rt.common.util.GlideUtils
import com.rt.ipms_video.R
import com.rt.ipms_video.databinding.FragmentVideoPicBinding
import com.rt.ipms_video.mvvm.viewmodel.VideoPicFragmentViewModel
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack
import com.shuyu.gsyvideoplayer.listener.LockClickListener

class VideoPicFragment : VbBaseFragment<VideoPicFragmentViewModel, FragmentVideoPicBinding>(), OnClickListener {
    var url = "http://58.246.81.147:801/20230706/JA03109_20230706171341_1.mp4"
    var picUrl = "https://m1.autoimg.cn/cardfs/product/g30/M05/BA/3D/1024x0_q87_autohomecar__ChwFlGIyj0KAcRoJACAuV1_hUzY722.jpg"
    override fun initView() {
        val gsyVideoOption = GSYVideoOptionBuilder()
        //内置封面可参考SampleCoverVideo
        val imageView = ImageView(ActivityCacheManager.instance().getCurrentActivity())
        loadCover(imageView, url, picUrl)
        gsyVideoOption
            .setThumbImageView(imageView)
            .setIsTouchWiget(true)
            .setRotateViewAuto(false)
            .setLockLand(false)
            .setAutoFullWithSize(true)
            .setShowFullAnimation(false)
            .setNeedLockFull(true)
            .setUrl(url)
            .setCacheWithPlay(false)
            .setVideoTitle("")
            .setVideoAllCallBack(object : GSYSampleCallBack() {
                override fun onPrepared(url: String?, vararg objects: Any?) {
                    super.onPrepared(url, *objects)
                }

                override fun onQuitFullscreen(url: String?, vararg objects: Any?) {
                    super.onQuitFullscreen(url, *objects)
                }
            }).setLockClickListener(object : LockClickListener {
                override fun onClick(view: View?, lock: Boolean) {
                }
            }).build(binding.sgvpVideo);
        binding.sgvpVideo.backButton.gone()
        binding.sgvpVideo.fullscreenButton.gone()

        GlideUtils.instance?.loadImage(
            binding.rivPic, picUrl
        )
    }

    fun loadCover(imageView: ImageView, url: String, res: String) {
        Glide.with(BaseApplication.baseApplication)
            .setDefaultRequestOptions(
                RequestOptions()
                    .frame(1000000)
                    .centerCrop()
            )
            .load(url)
            .into(imageView)
    }

    override fun initListener() {
        binding.rivPic.setOnClickListener(this)
    }

    override fun initData() {
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.riv_pic -> {
                val imgList: MutableList<String> = ArrayList()
                imgList.add(picUrl)
                ARouter.getInstance().build(ARouterMap.PREVIEW_IMG).withStringArrayList(ARouterMap.IMG_LIST, imgList as ArrayList)
                    .withInt(ARouterMap.IMG_INDEX, 0)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).navigation()
            }
        }
    }

    override fun getVbBindingView(): ViewBinding {
        return FragmentVideoPicBinding.inflate(layoutInflater)
    }

    override fun onReloadData() {
    }

    override fun providerVMClass(): Class<VideoPicFragmentViewModel>? {
        return VideoPicFragmentViewModel::class.java
    }

    override fun onPause() {
        super.onPause()
        binding.sgvpVideo.currentPlayer.onVideoPause()
    }

    override fun onResume() {
        super.onResume()
        binding.sgvpVideo.getCurrentPlayer().onVideoResume(false);
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.sgvpVideo.currentPlayer.release()
    }

}