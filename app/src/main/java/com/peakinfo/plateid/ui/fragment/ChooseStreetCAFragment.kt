package com.peakinfo.plateid.ui.fragment

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import com.alibaba.android.arouter.launcher.ARouter
import com.alibaba.fastjson.JSONObject
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.EncryptUtils
import com.peakinfo.base.BaseApplication
import com.peakinfo.base.arouter.ARouterMap
import com.peakinfo.base.base.mvvm.ErrorMessage
import com.peakinfo.base.bean.LoginBean
import com.peakinfo.base.bean.Street
import com.peakinfo.base.bean.WorkingHoursBean
import com.peakinfo.base.ds.PreferencesDataStore
import com.peakinfo.base.ds.PreferencesKeys
import com.peakinfo.base.ext.startAct
import com.peakinfo.base.util.Constant
import com.peakinfo.base.util.ToastUtil
import com.peakinfo.base.viewbase.VbBaseFragment
import com.peakinfo.common.realm.RealmUtil
import com.peakinfo.common.util.AppUtil
import com.peakinfo.plateid.R
import com.peakinfo.plateid.adapter.StreetChoosedAdapter
import com.peakinfo.plateid.databinding.FragmentChooseStreetCaBinding
import com.peakinfo.plateid.dialog.StreetChooseListDialog
import com.peakinfo.plateid.mvvm.viewmodel.ChooseStreetCAViewModel
import com.peakinfo.plateid.service.HeartbeatService
import com.peakinfo.plateid.ui.activity.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class ChooseStreetCAFragment : VbBaseFragment<ChooseStreetCAViewModel, FragmentChooseStreetCaBinding>(), OnClickListener {
    var streetList: MutableList<Street> = ArrayList()
    var streetChooseListDialog: StreetChooseListDialog? = null
    var streetChoosedAdapter: StreetChoosedAdapter? = null
    var streetChoosedList: MutableList<Street> = ArrayList()
    var loginInfo: LoginBean? = null
    var needLogin = false

    companion object {
        fun newInstance(loginInfo: LoginBean, streetList: MutableList<Street>): ChooseStreetCAFragment {
            return ChooseStreetCAFragment().apply {
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
        streetList = requireArguments().getParcelableArrayList("streetList", Street::class.java)!!
        loginInfo = requireArguments().getParcelable("loginInfo", LoginBean::class.java)

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
                showProgressDialog(60000)
                checkOnWork()
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

    override fun startObserve() {
        super.startObserve()
        mViewModel.apply {
            checkOnWorkLiveData.observe(this@ChooseStreetCAFragment) {
                caLogin()
            }
            caLoginLiveData.observe(this@ChooseStreetCAFragment) {
                runBlocking {
                    PreferencesDataStore(BaseApplication.instance()).putString(PreferencesKeys.account, loginInfo?.loginName.toString())
                    PreferencesDataStore(BaseApplication.instance()).putString(PreferencesKeys.token, it.token)
                    PreferencesDataStore(BaseApplication.instance()).putString(
                        PreferencesKeys.streetNOs,
                        streetChoosedList.joinToString(separator = ",") { it.streetNo })
                    startHeartbeatService(requireContext())
                }
                val firstStreet = streetChoosedList[0]
                Constant.APP_ID = firstStreet.appId
                Constant.PASSWORD = firstStreet.password
                RealmUtil.instance?.deleteAllStreet()
                RealmUtil.instance?.addRealmAsyncList(streetChoosedList)
                RealmUtil.instance?.updateCurrentStreet(streetChoosedList[0], null)
                RealmUtil.instance?.addRealm(WorkingHoursBean(loginInfo?.loginName.toString(), System.currentTimeMillis()))
                if (it != null && it.token != "") {
                    ARouter.getInstance().build(ARouterMap.MAIN).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).navigation()
                    logInOutNotice("1")
                } else {
                    ToastUtil.showBottomToast("登录失败, 响应结果为空或token为空!", 1)
                }
            }
            tokenLiveData.observe(this@ChooseStreetCAFragment) {
                logout(it.token.toString())
            }
            logoutLiveData.observe(this@ChooseStreetCAFragment) {
                logInOutNotice("2")
                if (needLogin) {
                    needLogin = false
                    caLogin()
                } else {
                    dismissProgressDialog()
                    ToastUtil.showBottomToast("签退成功", 0)
                }
            }
            errMsg.observe(this@ChooseStreetCAFragment) {
                dismissProgressDialog()
                ToastUtil.showBottomToast(it.msg)
                if (it.api == "caLogin") {
                    if (it.code == 1012) {
                        needLogin = true
                        token()
                    } else {
                        dismissProgressDialog()
                        val param = HashMap<String, Any>()
                        param["deviceId"] = Constant.deviceId
                        param["deviceCode"] = Constant.deviceId
                        mViewModel.refreshCert(param)
                    }
                } else {
                    dismissProgressDialog()
                }
            }
            mException.observe(this@ChooseStreetCAFragment) {
                dismissProgressDialog()
            }
        }
    }

    fun checkOnWork() {
        runBlocking {
            val longitude = PreferencesDataStore(BaseApplication.baseApplication).getDouble(PreferencesKeys.lon)
            val latitude = PreferencesDataStore(BaseApplication.baseApplication).getDouble(PreferencesKeys.lat)
            val param = HashMap<String, Any>()
            val jsonobject = JSONObject()
            jsonobject["loginName"] = loginInfo?.loginName
            jsonobject["streetNos"] = streetChoosedList.joinToString(separator = ",") { it.streetNo }
            jsonobject["longitude"] = longitude.takeIf { it != 0.0 }?.toString() ?: longitude.toString()
            jsonobject["latitude"] = latitude.takeIf { it != 0.0 }?.toString() ?: latitude.toString()
            param["attr"] = jsonobject
            mViewModel.checkOnWork(param)
        }
    }

    fun caLogin() {
        runBlocking {
            val longitude = PreferencesDataStore(BaseApplication.baseApplication).getDouble(PreferencesKeys.lon)
            val latitude = PreferencesDataStore(BaseApplication.baseApplication).getDouble(PreferencesKeys.lat)
            val passwordMD5 = EncryptUtils.encryptMD5ToString(loginInfo?.loginName).lowercase()
            val param = HashMap<String, Any>()
            param["userId"] = loginInfo?.loginName.toString()
            param["deviceId"] = Constant.deviceId
            param["simId"] = Constant.simId
            param["password"] = passwordMD5
            param["longitude"] = longitude.takeIf { it != 0.0 }?.toString() ?: longitude.toString()
            param["latitude"] = latitude.takeIf { it != 0.0 }?.toString() ?: latitude.toString()
            param["dataTime"] = System.currentTimeMillis()
            mViewModel.caLogin(param)
        }
    }

    fun logInOutNotice(state: String) {
        val param = HashMap<String, Any>()
        val jsonobject = JSONObject()
        jsonobject["imei"] = Constant.imei
        jsonobject["loginName"] = loginInfo?.loginName
        jsonobject["simId"] = Constant.simId
        jsonobject["state"] = state
        jsonobject["version"] = AppUtils.getAppVersionName()
        param["attr"] = jsonobject
        mViewModel.logInOutNotice(param)
    }

    fun token() {
        val userId = loginInfo?.loginName
        ToastUtil.showBottomToast("正在签退, 请稍后...", 0)
        val passwordMD5 = EncryptUtils.encryptMD5ToString(userId).lowercase()
        val param = HashMap<String, Any>()
        param["userId"] = userId.toString()
        param["simId"] = Constant.simId
        param["password"] = passwordMD5
        param["dataTime"] = System.currentTimeMillis()
        mViewModel.token(param)
    }

    fun logout(token: String) {
        runBlocking {
            val longitude = PreferencesDataStore(BaseApplication.baseApplication).getDouble(PreferencesKeys.lon)
            val latitude = PreferencesDataStore(BaseApplication.baseApplication).getDouble(PreferencesKeys.lat)
            val userId = loginInfo?.loginName
            val passwordMD5 = EncryptUtils.encryptMD5ToString(userId).lowercase()
            val param = HashMap<String, Any>()
            param["token"] = token
            param["userId"] = userId.toString()
            param["deviceId"] = Constant.deviceId
            param["simId"] = Constant.simId
            param["password"] = passwordMD5
            param["longitude"] = longitude.takeIf { it != 0.0 }?.toString() ?: longitude.toString()
            param["latitude"] = latitude.takeIf { it != 0.0 }?.toString() ?: latitude.toString()
            param["dataTime"] = System.currentTimeMillis()
            mViewModel.logout(param)
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
        return FragmentChooseStreetCaBinding.inflate(layoutInflater)
    }

    override fun onReloadData() {
    }

}