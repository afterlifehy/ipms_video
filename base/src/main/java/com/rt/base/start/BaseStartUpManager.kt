package com.rt.base.start

import android.app.Application
import com.hyperai.hyperlpr3.HyperLPR3
import com.hyperai.hyperlpr3.bean.HyperLPRParameter
import com.rt.base.BaseApplication

class BaseStartUpManager private constructor() : AppInitManager() {

    companion object {
        var mStartUpManager: BaseStartUpManager? = null

        fun instance(): BaseStartUpManager {
            if (mStartUpManager == null) {
                mStartUpManager = BaseStartUpManager()
            }
            return mStartUpManager!!
        }

    }


    override fun applicationInit(application: Application) {
    }

    override fun delayInit(application: Application) {
        //只有测试才开启
        BaseApplication.instance().getOnAppBaseProxyLinsener()?.let {
            if (it.onIsDebug()) {

            }
        }
        //车牌识别初始化
        initHyperLPR()
//        val appDir = File(Environment.getExternalStorageDirectory(), Constant.rt_FILE_PATH)
//        FileUtils.createOrExistsDir(appDir)
    }

    private fun initHyperLPR() {
        // 车牌识别算法配置参数
        val parameter = HyperLPRParameter()
            .setDetLevel(HyperLPR3.DETECT_LEVEL_LOW)
            .setMaxNum(1)
            .setRecConfidenceThreshold(0.85f)
        // 初始化(仅执行一次生效)
        HyperLPR3.getInstance().init(BaseApplication.instance(), parameter)

//        ISNav.getInstance().init { context, path, imageView ->
//            Glide.with(context).load(path).into(imageView)
//        }
    }

}