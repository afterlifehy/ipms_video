package com.peakinfo.plateid.ui.activity.login

import android.content.Intent
import android.os.Build
import android.view.View
import android.view.View.OnClickListener
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.peakinfo.base.BaseApplication
import com.peakinfo.base.arouter.ARouterMap
import com.peakinfo.base.bean.LoginBean
import com.peakinfo.base.bean.Street
import com.peakinfo.base.ds.PreferencesDataStore
import com.peakinfo.base.ds.PreferencesKeys
import com.peakinfo.base.ext.i18N
import com.peakinfo.base.viewbase.VbBaseActivity
import com.peakinfo.common.realm.RealmUtil
import com.peakinfo.plateid.R
import com.peakinfo.plateid.adapter.StreetChoosedAdapter
import com.peakinfo.plateid.databinding.ActivityStreetChooseBinding
import com.peakinfo.plateid.dialog.StreetChooseListDialog
import com.peakinfo.plateid.mvvm.viewmodel.StreetChooseViewModel
import kotlinx.coroutines.runBlocking

@Route(path = ARouterMap.STREET_CHOOSE)
class StreetChooseActivity : VbBaseActivity<StreetChooseViewModel, ActivityStreetChooseBinding>(),
    OnClickListener {
    var streetList: MutableList<Street> = ArrayList()
    var streetChooseListDialog: StreetChooseListDialog? = null
    var streetChoosedAdapter: StreetChoosedAdapter? = null
    var streetChoosedList: MutableList<Street> = ArrayList()
    var loginInfo: LoginBean? = null

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun initView() {
        binding.layoutToolbar.tvTitle.text = i18N(com.peakinfo.base.R.string.路段选择)

        loginInfo = intent.getParcelableExtra(ARouterMap.LOGIN_INFO) as? LoginBean

        binding.rvStreet.setHasFixedSize(true)
        binding.rvStreet.layoutManager = LinearLayoutManager(this)
        streetChoosedAdapter = StreetChoosedAdapter(streetChoosedList, this)
        binding.rvStreet.adapter = streetChoosedAdapter
    }

    override fun initListener() {
        binding.layoutToolbar.flBack.setOnClickListener(this)
        binding.rflAddStreet.setOnClickListener(this)
        binding.rtvEnterWorkBench.setOnClickListener(this)
    }

    override fun initData() {
        streetList.clear()
        streetList = loginInfo?.result as MutableList<Street>
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fl_back -> {
                onBackPressedSupport()
            }

            R.id.rfl_addStreet -> {
                streetChooseListDialog =
                    StreetChooseListDialog(streetList, streetChoosedList, object : StreetChooseListDialog.StreetChooseCallBack {
                        override fun chooseStreets() {
                            streetChoosedAdapter?.setList(streetChoosedList)
                        }

                    })
                streetChooseListDialog?.show()
            }

            R.id.rtv_enterWorkBench -> {
                if (streetChoosedList.isNotEmpty()) {
                    for (i in streetChoosedList) {
                        RealmUtil.instance?.updateStreetChoosed(i)
                    }
                    val streetList = loginInfo?.result as ArrayList<Street>
                    runBlocking {
                        PreferencesDataStore(BaseApplication.instance()).putString(PreferencesKeys.token, loginInfo!!.token)
                        PreferencesDataStore(BaseApplication.instance()).putString(PreferencesKeys.phone, loginInfo!!.phone)
                        PreferencesDataStore(BaseApplication.instance()).putString(PreferencesKeys.name, loginInfo!!.name)
                        PreferencesDataStore(BaseApplication.instance()).putString(PreferencesKeys.loginName, loginInfo!!.loginName)
                    }
                    RealmUtil.instance?.deleteAllStreet()
                    RealmUtil.instance?.addRealmAsyncList(streetList)
                    RealmUtil.instance?.updateCurrentStreet(streetChoosedList[0], null)
                    ARouter.getInstance().build(ARouterMap.MAIN).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).navigation()
                }
            }

            R.id.rfl_delete -> {
                val item = v.tag as Street
                val position = streetChoosedList.indexOf(item)
                streetChoosedList.remove(item)
                streetChoosedAdapter?.removeAt(position)
            }
        }
    }

    override fun getVbBindingView(): ViewBinding {
        return ActivityStreetChooseBinding.inflate(layoutInflater)
    }

    override fun onReloadData() {
    }

    override val isFullScreen: Boolean
        get() = true

    override fun providerVMClass(): Class<StreetChooseViewModel> {
        return StreetChooseViewModel::class.java
    }

    override fun marginStatusBarView(): View {
        return binding.layoutToolbar.ablToolbar
    }
}