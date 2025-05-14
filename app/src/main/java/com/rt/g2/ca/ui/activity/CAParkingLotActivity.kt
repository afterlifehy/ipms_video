package com.rt.g2.ca.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.View.OnClickListener
import android.widget.PopupWindow
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.viewbinding.ViewBinding
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.alibaba.fastjson.JSONObject
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.EncryptUtils
import com.rt.base.BaseApplication
import com.rt.base.arouter.ARouterMap
import com.rt.base.bean.ParkingLotBean
import com.rt.base.bean.Street
import com.rt.base.ds.PreferencesDataStore
import com.rt.base.ds.PreferencesKeys
import com.rt.base.ext.i18N
import com.rt.base.help.ActivityCacheManager
import com.rt.base.util.Constant
import com.rt.base.util.ToastUtil
import com.rt.base.viewbase.VbBaseActivity
import com.rt.common.event.CurrentStreetUpdateEvent
import com.rt.common.event.RefreshParkingLotEvent
import com.rt.common.realm.RealmUtil
import com.rt.common.util.GlideUtils
import com.rt.g2.R
import com.rt.g2.adapter.ParkingLotAdapter
import com.rt.g2.databinding.ActivityParkingLotBinding
import com.rt.g2.mvvm.viewmodel.ParkingLotViewModel
import com.rt.g2.pop.StreetPop
import com.rt.g2.ui.activity.login.LoginActivity
import kotlinx.coroutines.runBlocking
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

@Route(path = ARouterMap.CA_PARKING_LOT)
class CAParkingLotActivity : VbBaseActivity<ParkingLotViewModel, ActivityParkingLotBinding>(), OnClickListener {
    var parkingLotAdapter: ParkingLotAdapter? = null
    var parkingLotList: MutableList<ParkingLotBean> = ArrayList()
    var count = 0
    var handler = Handler(Looper.getMainLooper())

    var streetPop: StreetPop? = null
    var streetList: MutableList<Street> = ArrayList()
    var currentStreet: Street? = null
    var tempStreet: Street? = null
    var account = ""
    var passwordMD5 = ""

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(refreshParkingLotEvent: RefreshParkingLotEvent) {
        getParkingLotList()
    }

    override fun initView() {
        GlideUtils.instance?.loadImage(binding.ivBack, com.rt.common.R.mipmap.ic_back_white)
        binding.tvTitle.text = i18N(com.rt.base.R.string.停车场)
        binding.tvTitle.setTextColor(ContextCompat.getColor(BaseApplication.instance(), com.rt.base.R.color.white))

        binding.rvParkingLot.setHasFixedSize(true)
        binding.rvParkingLot.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        parkingLotAdapter = ParkingLotAdapter(parkingLotList, this)
        binding.rvParkingLot.adapter = parkingLotAdapter
    }

    override fun initListener() {
        binding.flBack.setOnClickListener(this)
        binding.tvTitle.setOnClickListener(this)
    }

    override fun initData() {
        runBlocking {
            account = PreferencesDataStore(BaseApplication.instance()).getString(PreferencesKeys.account)
            passwordMD5 = EncryptUtils.encryptMD5ToString(account).lowercase()
        }
        currentStreet = RealmUtil.instance?.findCurrentStreet()
        streetList = RealmUtil.instance?.findCheckedStreetList() as MutableList<Street>
        if (currentStreet!!.streetName.indexOf("(") < 0) {
            binding.tvTitle.text = currentStreet!!.streetNo + currentStreet!!.streetName
        } else {
            binding.tvTitle.text =
                currentStreet!!.streetNo + currentStreet!!.streetName.substring(0, currentStreet!!.streetName.indexOf("("))
        }
        if (streetList.size == 1) {
            binding.tvTitle.setCompoundDrawables(
                null,
                null,
                null,
                null
            )
            binding.tvTitle.setOnClickListener(null)
        } else {
            binding.tvTitle.setOnClickListener(this)
        }
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
                    ARouter.getInstance().build(ARouterMap.CA_PARKING_SPACE).withString(ARouterMap.ORDER_NO, parkingLotBean.orderNo)
                        .withString(ARouterMap.CAR_LICENSE, parkingLotBean.carLicense)
                        .withString(ARouterMap.CAR_COLOR, parkingLotBean.carColor)
                        .withString(ARouterMap.PARKING_NO, parkingLotBean.parkingNo)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).navigation()
                }
            }

            R.id.tv_title -> {
                streetPop = StreetPop(this@CAParkingLotActivity, currentStreet, streetList, object : StreetPop.StreetSelectCallBack {
                    @SuppressLint("MissingPermission")
                    override fun selectStreet(street: Street) {
                        if (street.streetNo == currentStreet!!.streetNo) {
                            return
                        }
                        showProgressDialog(20000)
                        tempStreet = street
                        token()
                    }
                })
                streetPop?.showAsDropDown((v.parent) as Toolbar)
                val upDrawable = ContextCompat.getDrawable(BaseApplication.instance(), com.rt.common.R.mipmap.ic_arrow_up)
                upDrawable?.setBounds(0, 0, upDrawable.intrinsicWidth, upDrawable.intrinsicHeight)
                binding.tvTitle.setCompoundDrawables(
                    null,
                    null,
                    upDrawable,
                    null
                )
                streetPop?.setOnDismissListener(object : PopupWindow.OnDismissListener {
                    override fun onDismiss() {
                        val downDrawable = ContextCompat.getDrawable(BaseApplication.instance(), com.rt.common.R.mipmap.ic_arrow_down)
                        downDrawable?.setBounds(0, 0, downDrawable.intrinsicWidth, downDrawable.intrinsicHeight)
                        binding.tvTitle.setCompoundDrawables(
                            null,
                            null,
                            downDrawable,
                            null
                        )
                    }
                })
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun startObserve() {
        super.startObserve()
        mViewModel.apply {
            parkingLotListLiveData.observe(this@CAParkingLotActivity) {
                dismissProgressDialog()
                parkingLotList.clear()
                parkingLotList.addAll(it.result)
                parkingLotAdapter?.setList(parkingLotList)
            }
            tokenLiveData.observe(this@CAParkingLotActivity) {
                logout(it.token.toString())
            }
            logoutLiveData.observe(this@CAParkingLotActivity) {
                dismissProgressDialog()
                ToastUtil.showBottomToast("签退成功", 0)
                Constant.APP_ID = tempStreet!!.appId
                Constant.PASSWORD = tempStreet!!.password
                logInOutNotice("2")
                caLogin()
            }
            caLoginLiveData.observe(this@CAParkingLotActivity) {
                dismissProgressDialog()
                currentStreet = tempStreet
                val old = RealmUtil.instance?.findCurrentStreet()
                RealmUtil.instance?.updateCurrentStreet(currentStreet!!, old)
                runBlocking {
                    PreferencesDataStore(BaseApplication.instance()).putString(PreferencesKeys.token, it.token)
                    getParkingLotList()
                    EventBus.getDefault().post(CurrentStreetUpdateEvent(currentStreet!!))
                }
                ToastUtil.showBottomToast("${currentStreet?.streetName}签到成功")
                if (currentStreet!!.streetName.indexOf("(") < 0) {
                    binding.tvTitle.text = currentStreet!!.streetNo + currentStreet!!.streetName
                } else {
                    binding.tvTitle.text =
                        currentStreet!!.streetNo + currentStreet!!.streetName.substring(0, currentStreet!!.streetName.indexOf("("))
                }
                logInOutNotice("1")
            }
            errMsg.observe(this@CAParkingLotActivity) {
                dismissProgressDialog()
                ToastUtil.showBottomToast(it.msg)
                if (it.api == "caLogin") {
                    ToastUtil.showBottomToast("${currentStreet?.streetName}签到失败")
                    runBlocking {
                        PreferencesDataStore(BaseApplication.instance()).putString(PreferencesKeys.token, "")
                        PreferencesDataStore(BaseApplication.instance()).putString(PreferencesKeys.phone, "")
                        PreferencesDataStore(BaseApplication.instance()).putString(PreferencesKeys.name, "")
                        PreferencesDataStore(BaseApplication.instance()).putString(PreferencesKeys.account, "")
                    }
                    RealmUtil.instance?.deleteAllStreet()
                    ARouter.getInstance().build(ARouterMap.LOGIN).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).navigation()
                    for (i in ActivityCacheManager.instance().getAllActivity()) {
                        if (i !is LoginActivity) {
                            i.finish()
                        }
                    }
                }
            }
            mException.observe(this@CAParkingLotActivity) {
                dismissProgressDialog()
            }
        }
    }

    fun token() {
        showProgressDialog(20000)
        val param = HashMap<String, Any>()
        param["userId"] = account
        param["simId"] = Constant.simId
        param["password"] = passwordMD5
        param["dataTime"] = System.currentTimeMillis()
        mViewModel.token(param)
    }

    fun logout(token: String) {
        runBlocking {
            val longitude = PreferencesDataStore(BaseApplication.baseApplication).getDouble(PreferencesKeys.lon)
            val latitude = PreferencesDataStore(BaseApplication.baseApplication).getDouble(PreferencesKeys.lat)
            val param = HashMap<String, Any>()
            param["token"] = token
            param["userId"] = account
            param["deviceId"] = Constant.deviceId
            param["simId"] = Constant.simId
            param["password"] = passwordMD5
            param["longitude"] = longitude
            param["latitude"] = latitude
            param["dataTime"] = System.currentTimeMillis()
            mViewModel.caLogout(param)
        }
    }

    fun logInOutNotice(state: String) {
        val param = HashMap<String, Any>()
        val jsonobject = JSONObject()
        jsonobject["imei"] = Constant.imei
        jsonobject["loginName"] = account
        jsonobject["simId"] = Constant.simId
        jsonobject["state"] = state
        jsonobject["version"] = AppUtils.getAppVersionName()
        param["attr"] = jsonobject
        mViewModel.logInOutNotice(param)
    }

    fun caLogin() {
        runBlocking {
            val longitude = PreferencesDataStore(BaseApplication.baseApplication).getDouble(PreferencesKeys.lon)
            val latitude = PreferencesDataStore(BaseApplication.baseApplication).getDouble(PreferencesKeys.lat)
            val param = HashMap<String, Any>()
            param["userId"] = account
            param["deviceId"] = Constant.deviceId
            param["simId"] = Constant.simId
            param["password"] = passwordMD5
            param["longitude"] = longitude
            param["latitude"] = latitude
            param["dataTime"] = System.currentTimeMillis()
            mViewModel.caLogin(param)
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

    override fun isRegEventBus(): Boolean {
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        if (handler != null) {
            handler.removeCallbacks(runnable)
        }
    }
}