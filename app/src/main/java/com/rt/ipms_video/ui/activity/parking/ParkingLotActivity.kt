package com.rt.ipms_video.ui.activity.parking

import android.content.Intent
import android.view.View
import android.view.View.OnClickListener
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.viewbinding.ViewBinding
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.rt.base.arouter.ARouterMap
import com.rt.base.viewbase.VbBaseActivity
import com.rt.ipms_video.R
import com.rt.ipms_video.adapter.ParkingLotAdapter
import com.rt.ipms_video.databinding.ActivityParkingLotBinding
import com.rt.ipms_video.mvvm.viewmodel.ParkingLotViewModel
import com.rt.ipms_video.pop.StreetPop

@Route(path = ARouterMap.PARKING_LOT)
class ParkingLotActivity : VbBaseActivity<ParkingLotViewModel, ActivityParkingLotBinding>(), OnClickListener {
    var parkingLotAdapter: ParkingLotAdapter? = null
    var parkingLotList: MutableList<Int> = ArrayList()
    var streetPop: StreetPop? = null
    var streetList: MutableList<Int> = ArrayList()

    override fun initView() {
        binding.tvTitle.text = "CN007定西路(愚园)"

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
        parkingLotList.add(1)
        parkingLotList.add(0)
        parkingLotList.add(0)
        parkingLotList.add(4)
        parkingLotList.add(5)
        parkingLotList.add(6)
        parkingLotList.add(9)
        parkingLotList.add(20)
        parkingLotList.add(0)
        parkingLotAdapter?.setList(parkingLotList)

        streetList.add(1)
        streetList.add(2)
        streetList.add(3)
        streetList.add(4)
        streetList.add(5)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fl_back -> {
                onBackPressedSupport()
            }

            R.id.ll_title -> {
                streetPop = StreetPop(this@ParkingLotActivity, streetList, object : StreetPop.StreetSelectCallBack {
                    override fun selectStreet(street: Int) {

                    }

                })
                streetPop?.showAsDropDown((v.parent) as Toolbar)
            }

            R.id.rfl_parking -> {
                ARouter.getInstance().build(ARouterMap.PARKING_SPACE).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).navigation()
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