package com.rt.ipms_geo.ui.activity.parking

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.telephony.TelephonyManager
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
import com.blankj.utilcode.util.PhoneUtils
import com.rt.base.BaseApplication
import com.rt.base.arouter.ARouterMap
import com.rt.base.bean.ParkingLotBean
import com.rt.base.bean.Street
import com.rt.base.ds.PreferencesDataStore
import com.rt.base.ds.PreferencesKeys
import com.rt.base.ext.i18N
import com.rt.base.help.ActivityCacheManager
import com.rt.base.util.ToastUtil
import com.rt.base.viewbase.VbBaseActivity
import com.rt.common.event.CurrentStreetUpdateEvent
import com.rt.common.event.RefreshParkingLotEvent
import com.rt.common.realm.RealmUtil
import com.rt.common.util.GlideUtils
import com.rt.ipms_geo.R
import com.rt.ipms_geo.adapter.ParkingLotAdapter
import com.rt.ipms_geo.databinding.ActivityParkingLotBinding
import com.rt.ipms_geo.mvvm.viewmodel.ParkingLotViewModel
import com.rt.ipms_geo.pop.StreetPop
import com.rt.ipms_geo.ui.activity.login.LoginActivity
import kotlinx.coroutines.runBlocking
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

@Route(path = ARouterMap.PARKING_LOT)
class ParkingLotActivity : VbBaseActivity<ParkingLotViewModel, ActivityParkingLotBinding>(), OnClickListener {
    var parkingLotAdapter: ParkingLotAdapter? = null
    var parkingLotList: MutableList<ParkingLotBean> = ArrayList()
    var count = 0
    var handler = Handler(Looper.getMainLooper())

    var streetPop: StreetPop? = null
    var streetList: MutableList<Street> = ArrayList()
    var currentStreet: Street? = null
    var tempStreet: Street? = null

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
                    ARouter.getInstance().build(ARouterMap.PARKING_SPACE).withString(ARouterMap.ORDER_NO, parkingLotBean.orderNo)
                        .withString(ARouterMap.CAR_LICENSE, parkingLotBean.carLicense)
                        .withString(ARouterMap.CAR_COLOR, parkingLotBean.carColor)
                        .withString(ARouterMap.PARKING_NO, parkingLotBean.parkingNo)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).navigation()
                }
            }

            R.id.tv_title -> {
                streetPop = StreetPop(this@ParkingLotActivity, currentStreet, streetList, object : StreetPop.StreetSelectCallBack {
                    @SuppressLint("MissingPermission")
                    override fun selectStreet(street: Street) {
                        if (street.streetNo == currentStreet!!.streetNo) {
                            return
                        }
                        showProgressDialog(20000)
                        tempStreet = street
                        runBlocking {
                            val token =
                                PreferencesDataStore(BaseApplication.baseApplication).getString(PreferencesKeys.token)
                            val longitude = PreferencesDataStore(BaseApplication.baseApplication).getDouble(PreferencesKeys.lon)
                            val latitude = PreferencesDataStore(BaseApplication.baseApplication).getDouble(PreferencesKeys.lat)
                            val param = HashMap<String, Any>()
                            val jsonobject = JSONObject()
                            jsonobject["token"] = token
                            jsonobject["longitude"] = longitude
                            jsonobject["latitude"] = latitude
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                try {
                                    jsonobject["imei"] = (getSystemService(TELEPHONY_SERVICE) as TelephonyManager).imei
                                    jsonobject["simId"] = (getSystemService(TELEPHONY_SERVICE) as TelephonyManager).simSerialNumber
                                } catch (e: Exception) {
                                    val manufacturer = Build.MANUFACTURER
                                    val model = Build.MODEL
                                    val id =
                                        manufacturer + model + " " + Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
                                    jsonobject["imei"] = id
                                    jsonobject["simId"] = id
                                }
                            } else {
                                jsonobject["imei"] = PhoneUtils.getIMEI()
                                jsonobject["simId"] = (getSystemService(TELEPHONY_SERVICE) as TelephonyManager).simSerialNumber
                            }
                            jsonobject["version"] = AppUtils.getAppVersionName()
                            param["attr"] = jsonobject
                            mViewModel.logout(param)
                        }
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
            parkingLotListLiveData.observe(this@ParkingLotActivity) {
                dismissProgressDialog()
                parkingLotList.clear()
                parkingLotList.addAll(it.result)
                parkingLotAdapter?.setList(parkingLotList)
            }
            logoutLiveData.observe(this@ParkingLotActivity) {
                runBlocking {
                    PreferencesDataStore(BaseApplication.instance()).putString(PreferencesKeys.token, "")
                    val loginName = PreferencesDataStore(BaseApplication.instance()).getString(PreferencesKeys.account)
                    val longitude = PreferencesDataStore(BaseApplication.baseApplication).getDouble(PreferencesKeys.lon)
                    val latitude = PreferencesDataStore(BaseApplication.baseApplication).getDouble(PreferencesKeys.lat)
                    val param = HashMap<String, Any>()
                    val jsonobject = JSONObject()
                    jsonobject["loginName"] = loginName
                    jsonobject["streetNo"] = currentStreet?.streetNo
                    jsonobject["longitude"] = longitude
                    jsonobject["latitude"] = latitude
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        try {
                            jsonobject["imei"] = (getSystemService(TELEPHONY_SERVICE) as TelephonyManager).imei
                            jsonobject["simId"] = (getSystemService(TELEPHONY_SERVICE) as TelephonyManager).simSerialNumber
                        } catch (e: Exception) {
                            val manufacturer = Build.MANUFACTURER
                            val model = Build.MODEL
                            val id = manufacturer + model + " " + Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
                            jsonobject["imei"] = id
                            jsonobject["simId"] = id
                        }
                    } else {
                        jsonobject["imei"] = PhoneUtils.getIMEI()
                        jsonobject["simId"] = (getSystemService(TELEPHONY_SERVICE) as TelephonyManager).simSerialNumber
                    }
                    jsonobject["version"] = AppUtils.getAppVersionName()
                    param["attr"] = jsonobject
                    mViewModel.login2(param)
                }
            }
            login2LiveData.observe(this@ParkingLotActivity) {
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
            }
            errMsg.observe(this@ParkingLotActivity) {
                dismissProgressDialog()
                ToastUtil.showBottomToast(it.msg)
                if (it.api == "login2") {
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
            mException.observe(this@ParkingLotActivity) {
                dismissProgressDialog()
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