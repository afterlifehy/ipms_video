package com.peakinfo.plateid.ui.activity.parking

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.View.OnClickListener
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.viewbinding.ViewBinding
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.alibaba.fastjson.JSONObject
import com.peakinfo.base.BaseApplication
import com.peakinfo.base.arouter.ARouterMap
import com.peakinfo.base.bean.ParkingLotBean
import com.peakinfo.base.bean.Street
import com.peakinfo.base.ext.i18N
import com.peakinfo.base.util.ToastUtil
import com.peakinfo.base.viewbase.VbBaseActivity
import com.peakinfo.common.realm.RealmUtil
import com.peakinfo.common.util.GlideUtils
import com.peakinfo.plateid.R
import com.peakinfo.plateid.adapter.ParkingLotAdapter
import com.peakinfo.plateid.databinding.ActivityParkingLotBinding
import com.peakinfo.plateid.mvvm.viewmodel.ParkingLotViewModel

@Route(path = ARouterMap.PARKING_LOT)
class ParkingLotActivity : VbBaseActivity<ParkingLotViewModel, ActivityParkingLotBinding>(), OnClickListener {
    var parkingLotAdapter: ParkingLotAdapter? = null
    var parkingLotList: MutableList<ParkingLotBean> = ArrayList()
    var currentStreet: Street? = null
    var count = 0
    var handler = Handler(Looper.getMainLooper())

    override fun initView() {
        GlideUtils.instance?.loadImage(binding.layoutToolbar.ivBack, com.peakinfo.common.R.mipmap.ic_back_white)
        binding.layoutToolbar.tvTitle.text = i18N(com.peakinfo.base.R.string.停车场)
        binding.layoutToolbar.tvTitle.setTextColor(ContextCompat.getColor(BaseApplication.instance(), com.peakinfo.base.R.color.white))

        binding.rvParkingLot.setHasFixedSize(true)
        binding.rvParkingLot.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        parkingLotAdapter = ParkingLotAdapter(parkingLotList, this)
        binding.rvParkingLot.adapter = parkingLotAdapter
    }

    override fun initListener() {
        binding.layoutToolbar.flBack.setOnClickListener(this)
    }

    override fun initData() {
        currentStreet = RealmUtil.instance?.findCurrentStreet()
    }

    val runnable = object : Runnable {
        override fun run() {
            if (count < 600) {
                getParkingLotList()
                count++
                handler.postDelayed(this, 10000)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(runnable)
    }

    override fun onResume() {
        super.onResume()
        handler.post(runnable)
    }

    fun getParkingLotList() {
        showProgressDialog(20000)
        val param = HashMap<String, Any>()
        val jsonobject = JSONObject()
        jsonobject["streetNo"] = currentStreet!!.streetNo
        param["attr"] = jsonobject
        mViewModel.getParkingLotList(param)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fl_back -> {
                onBackPressedSupport()
            }

            R.id.rfl_parking -> {
                val parkingLotBean = v.tag as ParkingLotBean
                if (parkingLotBean.state == "01") {
                    ARouter.getInstance().build(ARouterMap.BERTH_ABNORMAL)
                        .withString(ARouterMap.ABNORMAL_STREET_NO, currentStreet!!.streetNo)
                        .withString(ARouterMap.ABNORMAL_PARKING_NO, parkingLotBean.parkingNo)
                        .withString(ARouterMap.ABNORMAL_ORDER_NO, "")
                        .withString(ARouterMap.ABNORMAL_CARLICENSE, "")
                        .withString(ARouterMap.ABNORMAL_CAR_COLOR, "")
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).navigation()
                } else {
                    ARouter.getInstance().build(ARouterMap.PARKING_SPACE).withString(ARouterMap.ORDER_NO, parkingLotBean.orderNo)
                        .withString(ARouterMap.CAR_LICENSE, parkingLotBean.carLicense)
                        .withString(ARouterMap.CAR_COLOR, parkingLotBean.carColor)
                        .withString(ARouterMap.PARKING_NO, parkingLotBean.parkingNo)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).navigation()
                }
            }
        }
    }

    override fun startObserve() {
        super.startObserve()
        mViewModel.apply {
            parkingLotListLiveData.observe(this@ParkingLotActivity) {
                dismissProgressDialog()
                parkingLotList.clear()
                parkingLotList.addAll(it.result)
                parkingLotAdapter?.setList(parkingLotList)
            }
            errMsg.observe(this@ParkingLotActivity) {
                dismissProgressDialog()
                ToastUtil.showMiddleToast(it.msg)
            }
        }
    }

    override fun getVbBindingView(): ViewBinding {
        return ActivityParkingLotBinding.inflate(layoutInflater)
    }

    override fun onReloadData() {
    }

    override val isFullScreen: Boolean
        get() = true

    override fun marginStatusBarView(): View {
        return binding.layoutToolbar.ablToolbar
    }

    override fun providerVMClass(): Class<ParkingLotViewModel> {
        return ParkingLotViewModel::class.java
    }

    override fun onDestroy() {
        super.onDestroy()
        if (handler != null) {
            handler.removeCallbacks(runnable)
        }
    }
}