package com.rt.ipms_video.ui.activity.parking

import android.content.Intent
import android.view.View
import android.view.View.OnClickListener
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.viewbinding.ViewBinding
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.alibaba.fastjson.JSONObject
import com.rt.base.arouter.ARouterMap
import com.rt.base.bean.ParkingLotBean
import com.rt.base.bean.Street
import com.rt.base.util.ToastUtil
import com.rt.base.viewbase.VbBaseActivity
import com.rt.common.realm.RealmUtil
import com.rt.ipms_video.R
import com.rt.ipms_video.adapter.ParkingLotAdapter
import com.rt.ipms_video.databinding.ActivityParkingLotBinding
import com.rt.ipms_video.mvvm.viewmodel.ParkingLotViewModel
import com.rt.ipms_video.pop.StreetPop

@Route(path = ARouterMap.PARKING_LOT)
class ParkingLotActivity : VbBaseActivity<ParkingLotViewModel, ActivityParkingLotBinding>(), OnClickListener {
    var parkingLotAdapter: ParkingLotAdapter? = null
    var parkingLotList: MutableList<ParkingLotBean> = ArrayList()
    var streetPop: StreetPop? = null
    var streetList: MutableList<Street> = ArrayList()
    var currentStreet: Street? = null

    override fun initView() {
        binding.rvParkingLot.setHasFixedSize(true)
        binding.rvParkingLot.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        parkingLotAdapter = ParkingLotAdapter(parkingLotList, this)
        binding.rvParkingLot.adapter = parkingLotAdapter
    }

    override fun initListener() {
        binding.flBack.setOnClickListener(this)
        binding.llTitle.setOnClickListener(this)
    }

    override fun initData() {
        RealmUtil.instance?.findCheckedStreetList()?.let { streetList.addAll(it) }
        binding.tvTitle.text = streetList[0].streetName
        if (streetList.size > 0) {
            getParkingLotList(streetList[0])
        }
    }

    fun getParkingLotList(street: Street) {
        currentStreet = street
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

            R.id.ll_title -> {
                streetPop = StreetPop(this@ParkingLotActivity, currentStreet, streetList, object : StreetPop.StreetSelectCallBack {
                    override fun selectStreet(street: Street) {
                        binding.tvTitle.text = street.streetName
                        getParkingLotList(street)
                    }
                })
                streetPop?.showAsDropDown((v.parent) as Toolbar)
            }

            R.id.rfl_parking -> {
                val parkingLotBean = v.tag as ParkingLotBean
                ARouter.getInstance().build(ARouterMap.PARKING_SPACE).withString(ARouterMap.ORDER_NO, parkingLotBean.orderNo)
                    .withString(ARouterMap.CAR_LICENSE, parkingLotBean.carLicense)
                    .withString(ARouterMap.CAR_COLOR, parkingLotBean.carColor)
                    .withString(ARouterMap.PARKING_NO, parkingLotBean.parkingNo)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).navigation()
            }
        }
    }

    override fun startObserve() {
        super.startObserve()
        mViewModel.apply {
            parkingLotListLiveData.observe(this@ParkingLotActivity) {
                parkingLotList.clear()
                parkingLotList.addAll(it.result)
                parkingLotAdapter?.setList(parkingLotList)
            }
            errMsg.observe(this@ParkingLotActivity) {
                ToastUtil.showToast(it.msg)
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
        return binding.ablToolbar
    }

    override fun providerVMClass(): Class<ParkingLotViewModel> {
        return ParkingLotViewModel::class.java
    }
}