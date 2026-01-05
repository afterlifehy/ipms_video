package com.peakinfo.plateid.ui.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.TELEPHONY_SERVICE
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.telephony.TelephonyManager
import android.view.View
import android.view.View.OnClickListener
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.getSystemService
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import com.alibaba.android.arouter.launcher.ARouter
import com.alibaba.fastjson.JSONObject
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.PhoneUtils
import com.blankj.utilcode.util.TimeUtils
import com.peakinfo.base.BaseApplication
import com.peakinfo.base.arouter.ARouterMap
import com.peakinfo.base.bean.LoginBean
import com.peakinfo.base.bean.Street
import com.peakinfo.base.bean.WorkingHoursBean
import com.peakinfo.base.ds.PreferencesDataStore
import com.peakinfo.base.ds.PreferencesKeys
import com.peakinfo.base.util.ToastUtil
import com.peakinfo.base.viewbase.VbBaseFragment
import com.peakinfo.common.realm.RealmUtil
import com.peakinfo.common.util.AppUtil
import com.peakinfo.plateid.R
import com.peakinfo.plateid.adapter.StreetChoosedAdapter
import com.peakinfo.plateid.databinding.FragmentChooseStreetNonCaBinding
import com.peakinfo.plateid.dialog.StreetChooseListDialog
import com.peakinfo.plateid.mvvm.viewmodel.ChooseStreetNonCAViewModel
import com.peakinfo.plateid.service.HeartbeatService
import com.tbruyelle.rxpermissions3.RxPermissions
import kotlinx.coroutines.runBlocking

class ChooseStreetNonCAFragment : VbBaseFragment<ChooseStreetNonCAViewModel, FragmentChooseStreetNonCaBinding>(), OnClickListener {
    var streetList: MutableList<Street> = ArrayList()
    var streetChooseListDialog: StreetChooseListDialog? = null
    var streetChoosedAdapter: StreetChoosedAdapter? = null
    var streetChoosedList: MutableList<Street> = ArrayList()
    var loginInfo: LoginBean? = null

    companion object {
        fun newInstance(loginInfo: LoginBean): ChooseStreetNonCAFragment {
            return ChooseStreetNonCAFragment().apply {
                arguments = Bundle().apply {
                    putParcelableArrayList("streetList", ArrayList(streetList))
                    putParcelable("loginInfo", loginInfo)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setBackgroundColor(Color.TRANSPARENT)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun initView() {
        loginInfo = requireArguments().getParcelable("loginInfo")
        streetList.clear()
        loginInfo?.result?.let { streetList.addAll(it) }

        binding.rvStreet.setHasFixedSize(true)
        binding.rvStreet.layoutManager = LinearLayoutManager(requireContext())
        streetChoosedAdapter = StreetChoosedAdapter(streetChoosedList, this)
        binding.rvStreet.adapter = streetChoosedAdapter
    }

    override fun initListener() {
        binding.rflAddStreet.setOnClickListener(this)
        binding.rtvEnterWorkBench.setOnClickListener(this)
    }

    override fun initData() {
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.rfl_addStreet -> {
                streetChooseListDialog =
                    StreetChooseListDialog(streetList, streetChoosedList, object : StreetChooseListDialog.StreetChooseCallBack {
                        override fun chooseStreets() {
                            streetChoosedAdapter?.setList(streetChoosedList.distinct())
                        }

                    })
                streetChooseListDialog?.show()
            }

            R.id.rtv_enterWorkBench -> {
                login()
            }

            R.id.rfl_delete -> {
                if (!AppUtil.isFastClick(500)) {
                    val item = v.tag as Street
                    val position = streetChoosedList.indexOf(item)
                    streetChoosedList.remove(item)
                    streetChoosedAdapter?.removeAt(position)
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun login() {
        if (streetChoosedList.isNotEmpty()) {
            showProgressDialog(20000)
            runBlocking {
                val longitude = PreferencesDataStore(BaseApplication.baseApplication).getDouble(PreferencesKeys.lon)
                val latitude = PreferencesDataStore(BaseApplication.baseApplication).getDouble(PreferencesKeys.lat)
                val param = HashMap<String, Any>()
                val jsonobject = JSONObject()
                jsonobject["loginName"] = loginInfo?.loginName
                jsonobject["streetNo"] = streetChoosedList[0].streetNo
                jsonobject["longitude"] = longitude.takeIf { it != 0.0 }?.toString() ?: longitude.toString()
                jsonobject["latitude"] = latitude.takeIf { it != 0.0 }?.toString() ?: latitude.toString()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    try {
                        jsonobject["imei"] = (requireActivity().getSystemService(TELEPHONY_SERVICE) as TelephonyManager).imei
                        jsonobject["simId"] = (requireActivity().getSystemService(TELEPHONY_SERVICE) as TelephonyManager).simSerialNumber
                    } catch (e: Exception) {
                        val manufacturer = Build.MANUFACTURER
                        val model = Build.MODEL
                        val id = manufacturer + model + " " + Settings.Secure.getString(
                            requireActivity().contentResolver,
                            Settings.Secure.ANDROID_ID
                        )
                        jsonobject["imei"] = id
                        jsonobject["simId"] = id
                    }
                } else {
                    jsonobject["imei"] = PhoneUtils.getIMEI()
                    jsonobject["simId"] = (requireActivity().getSystemService(TELEPHONY_SERVICE) as TelephonyManager).simSerialNumber
                }
                jsonobject["version"] = AppUtils.getAppVersionName()
                param["attr"] = jsonobject
                mViewModel.login2(param)
            }
        } else {
            ToastUtil.showBottomToast("请添加路段")
        }
    }

    override fun startObserve() {
        super.startObserve()
        mViewModel.apply {
            login2LiveData.observe(this@ChooseStreetNonCAFragment) {
                dismissProgressDialog()
                for (i in streetChoosedList) {
                    RealmUtil.instance?.updateStreetChoosed(i)
                }
                val streetList = loginInfo?.result as ArrayList<Street>
                runBlocking {
                    PreferencesDataStore(BaseApplication.instance()).putString(PreferencesKeys.token, it.token)
                    PreferencesDataStore(BaseApplication.instance()).putString(PreferencesKeys.phone, loginInfo!!.phone.toString())
                    PreferencesDataStore(BaseApplication.instance()).putString(PreferencesKeys.name, loginInfo!!.name.toString())
                    PreferencesDataStore(BaseApplication.instance()).putString(
                        PreferencesKeys.account,
                        loginInfo!!.loginName.toString()
                    )
                    PreferencesDataStore(BaseApplication.instance()).putString(
                        PreferencesKeys.streetNOs,
                        streetChoosedList.joinToString(separator = ",") { it.streetNo })
                    startHeartbeatService(requireContext())
                }
                RealmUtil.instance?.deleteAllStreet()
                RealmUtil.instance?.addRealmAsyncList(streetChoosedList)
                RealmUtil.instance?.updateCurrentStreet(streetChoosedList[0], null)

                val workingHoursBean = RealmUtil.instance?.findCurrentWorkingHour(loginInfo!!.loginName.toString())
                if (workingHoursBean != null) {
                    val lastDay = TimeUtils.millis2String(workingHoursBean.time, "yyyy-MM-dd")
                    val currentDay = TimeUtils.millis2String(System.currentTimeMillis(), "yyyy-MM-dd")
                    if (lastDay != currentDay) {
                        RealmUtil.instance?.addRealm(WorkingHoursBean(loginInfo!!.loginName.toString(), System.currentTimeMillis()))
                    }
                } else {
                    RealmUtil.instance?.addRealm(WorkingHoursBean(loginInfo!!.loginName.toString(), System.currentTimeMillis()))
                }
                ARouter.getInstance().build(ARouterMap.MAIN).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).navigation()
            }
            errMsg.observe(this@ChooseStreetNonCAFragment) {
                dismissProgressDialog()
                ToastUtil.showBottomToast(it.msg)
            }
            mException.observe(this@ChooseStreetNonCAFragment) {
                dismissProgressDialog()
            }
        }
    }

    fun startHeartbeatService(context: Context) {
        val intent = Intent(context, HeartbeatService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }

    override fun getVbBindingView(): ViewBinding {
        return FragmentChooseStreetNonCaBinding.inflate(layoutInflater)
    }

    override fun onReloadData() {
    }

    override fun providerVMClass(): Class<ChooseStreetNonCAViewModel> {
        return ChooseStreetNonCAViewModel::class.java
    }
}