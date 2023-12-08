package com.peakinfo.plateid.ui.fragment

import android.content.Intent
import android.view.View
import android.view.View.OnClickListener
import android.widget.ImageView
import androidx.viewbinding.ViewBinding
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.peakinfo.base.BaseApplication
import com.peakinfo.base.arouter.ARouterMap
import com.peakinfo.base.ext.gone
import com.peakinfo.base.help.ActivityCacheManager
import com.peakinfo.base.viewbase.VbBaseFragment
import com.peakinfo.common.util.GlideUtils
import com.peakinfo.plateid.R
import com.peakinfo.plateid.databinding.FragmentVideoPicBinding
import com.peakinfo.plateid.mvvm.viewmodel.VideoPicFragmentViewModel
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack
import com.shuyu.gsyvideoplayer.listener.LockClickListener
import com.shuyu.gsyvideoplayer.model.VideoOptionModel
import com.shuyu.gsyvideoplayer.utils.OrientationUtils
import tv.danmaku.ijk.media.player.IjkMediaPlayer

class VideoPicFragment : VbBaseFragment<VideoPicFragmentViewModel, FragmentVideoPicBinding>(), OnClickListener {
    var video = ""
    var pic = ""
    override fun initView() {

        val followType = arguments?.getInt("followType")
        when (followType) {
            0 -> {
                video = arguments?.getString("inVideo").toString()
                pic = arguments?.getString("inPicture").toString()
            }

            1 -> {
                video = arguments?.getString("outVideo").toString()
                pic = arguments?.getString("outPicture").toString()
            }
        }
        val gsyVideoOption = GSYVideoOptionBuilder()
        //内置封面可参考SampleCoverVideo
        val imageView = ImageView(ActivityCacheManager.instance().getCurrentActivity())
        loadCover(imageView, video, pic)
        val orientationUtils = OrientationUtils(ActivityCacheManager.instance().getCurrentActivity(), binding.sgvpVideo)
        orientationUtils.setEnable(false)
        gsyVideoOption
            .setThumbImageView(imageView)
            .setIsTouchWiget(true)
            .setRotateViewAuto(false)
            .setLockLand(true)
            .setShowFullAnimation(true)
            .setNeedLockFull(true)
            .setSeekRatio(1f)
            .setUrl(video)
            .setCacheWithPlay(false)
            .setVideoTitle("")
            .setVideoAllCallBack(object : GSYSampleCallBack() {
                override fun onPrepared(url: String?, vararg objects: Any?) {
                    super.onPrepared(url, *objects)
                    //开始播放了才能旋转和全屏
                    orientationUtils.setEnable(binding.sgvpVideo.isRotateWithSystem)
                }

                override fun onQuitFullscreen(url: String?, vararg objects: Any?) {
                    super.onQuitFullscreen(url, *objects)
                }
            }).setLockClickListener(object : LockClickListener {
                override fun onClick(view: View?, lock: Boolean) {
                }
            }).build(binding.sgvpVideo)
        binding.sgvpVideo.backButton.gone()
        binding.sgvpVideo.fullscreenButton.setOnClickListener {
            orientationUtils.resolveByClick()
            binding.sgvpVideo.startWindowFullscreen(ActivityCacheManager.instance().getCurrentActivity(), false, true)
        }
        val videoOptionModel = VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "enable-accurate-seek", 1)
        val list: MutableList<VideoOptionModel> = ArrayList()
        list.add(videoOptionModel)
        GSYVideoManager.instance().optionModelList = list

        GlideUtils.instance?.loadLongImage(binding.rivPic, pic)

//        binding.llVideo.gone()
//        binding.layoutNoData.root.show()
//
//        binding.layoutNoData.tvNoDataTitle.text = i18n(com.peakinfo.base.R.string.无车辆进出图片)
//        GlideUtils.instance?.loadImage(binding.layoutNoData.ivNoData, com.peakinfo.common.R.mipmap.ic_no_data_3)
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
                imgList.add(pic)
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