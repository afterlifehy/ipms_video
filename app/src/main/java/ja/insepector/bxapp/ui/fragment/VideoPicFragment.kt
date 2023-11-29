package ja.insepector.bxapp.ui.fragment

import android.content.Intent
import android.view.View
import android.view.View.OnClickListener
import android.widget.ImageView
import androidx.viewbinding.ViewBinding
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import ja.insepector.base.BaseApplication
import ja.insepector.base.arouter.ARouterMap
import ja.insepector.base.ext.gone
import ja.insepector.base.help.ActivityCacheManager
import ja.insepector.base.viewbase.VbBaseFragment
import ja.insepector.common.util.GlideUtils
import ja.insepector.bxapp.R
import ja.insepector.bxapp.databinding.FragmentVideoPicBinding
import ja.insepector.bxapp.mvvm.viewmodel.VideoPicFragmentViewModel
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack
import com.shuyu.gsyvideoplayer.listener.LockClickListener

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
        gsyVideoOption
            .setThumbImageView(imageView)
            .setIsTouchWiget(true)
            .setRotateViewAuto(false)
            .setLockLand(false)
            .setAutoFullWithSize(true)
            .setShowFullAnimation(false)
            .setNeedLockFull(true)
            .setUrl(video)
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

        GlideUtils.instance?.loadImage(binding.rivPic, pic)

//        binding.llVideo.gone()
//        binding.layoutNoData.root.show()
//
//        binding.layoutNoData.tvNoDataTitle.text = i18n(ja.insepector.base.R.string.无车辆进出图片)
//        GlideUtils.instance?.loadImage(binding.layoutNoData.ivNoData, ja.insepector.common.R.mipmap.ic_no_data_3)
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