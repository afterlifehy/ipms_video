package com.rt.ipms_geo.ca.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.view.KeyEvent
import android.view.View
import android.view.View.OnClickListener
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.rt.base.BaseApplication
import com.rt.base.arouter.ARouterMap
import com.rt.base.bean.ca.UrgeBean
import com.rt.base.ds.PreferencesDataStore
import com.rt.base.ds.PreferencesKeys
import com.rt.base.ext.gone
import com.rt.base.ext.i18N
import com.rt.base.ext.i18n
import com.rt.base.ext.show
import com.rt.base.util.ToastUtil
import com.rt.base.viewbase.VbBaseActivity
import com.rt.common.event.RefreshDebtOrderListEvent
import com.rt.common.util.Constant
import com.rt.common.util.GlideUtils
import com.rt.common.view.keyboard.KeyboardUtil
import com.rt.common.view.keyboard.MyOnTouchListener
import com.rt.common.view.keyboard.MyTextWatcher
import com.rt.ipms_geo.R
import com.rt.ipms_geo.adapter.CAUrgeAdapter
import com.rt.ipms_geo.adapter.CollectionPlateColorAdapter
import com.rt.ipms_geo.databinding.ActivityUrgeListBinding
import com.rt.ipms_geo.mvvm.viewmodel.DebtCollectionViewModel
import kotlinx.coroutines.runBlocking
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

@Route(path = ARouterMap.CA_URGE)
class CAUrgeListActivity : VbBaseActivity<DebtCollectionViewModel, ActivityUrgeListBinding>(), OnClickListener {
    private lateinit var keyboardUtil: KeyboardUtil
    var caUrgeAdapter: CAUrgeAdapter? = null
    var urgeList: MutableList<UrgeBean> = ArrayList()
    var carLicense = ""
    var token = ""
    var collectionPlateColorAdapter: CollectionPlateColorAdapter? = null
    var collectioPlateColorList: MutableList<String> = ArrayList()
    var checkedColor = ""
    val widthType = 3

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(refreshDebtOrderListEvent: RefreshDebtOrderListEvent) {
        if (carLicense.isEmpty()) {
            return
        }
        query()
    }

    override fun initView() {
        window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        GlideUtils.instance?.loadImage(binding.layoutToolbar.ivBack, com.rt.common.R.mipmap.ic_back_white)
        binding.layoutToolbar.tvTitle.text = "催缴管理"
        binding.layoutToolbar.tvTitle.setTextColor(ContextCompat.getColor(BaseApplication.instance(), com.rt.base.R.color.white))
        GlideUtils.instance?.loadImage(binding.layoutNoData.ivNoData, com.rt.common.R.mipmap.ic_no_data_2)
        binding.layoutNoData.tvNoDataTitle.text = "通过车牌号未查询到催缴单"

        if (intent.getStringExtra(ARouterMap.DEBT_CAR_LICENSE) != null) {
            carLicense = intent.getStringExtra(ARouterMap.DEBT_CAR_LICENSE).toString()
            binding.etSearch.setText(carLicense)
            binding.etSearch.setSelection(carLicense.length)
        }
        binding.rvUrge.setHasFixedSize(true)
        binding.rvUrge.layoutManager = LinearLayoutManager(this)
        caUrgeAdapter = CAUrgeAdapter(urgeList, this)
        binding.rvUrge.adapter = caUrgeAdapter

        collectioPlateColorList.add(Constant.BLUE)
        collectioPlateColorList.add(Constant.GREEN)
        collectioPlateColorList.add(Constant.YELLOW)
        collectioPlateColorList.add(Constant.YELLOW_GREEN)
        collectioPlateColorList.add(Constant.WHITE)
        collectioPlateColorList.add(Constant.BLACK)
        collectioPlateColorList.add(Constant.OTHERS)
        binding.rvPlateColor.setHasFixedSize(true)
        binding.rvPlateColor.layoutManager = LinearLayoutManager(BaseApplication.instance(), LinearLayoutManager.HORIZONTAL, false)
        collectionPlateColorAdapter = CollectionPlateColorAdapter(widthType, collectioPlateColorList, this)
        binding.rvPlateColor.adapter = collectionPlateColorAdapter

        initKeyboard()
    }

    private fun initKeyboard() {
        keyboardUtil = KeyboardUtil(binding.kvKeyBoard) {
            binding.etSearch.requestFocus()
            keyboardUtil.changeKeyboard(true)
            keyboardUtil.setEditText(binding.etSearch)
        }

        binding.etSearch.addTextChangedListener(MyTextWatcher(null, null, true, keyboardUtil))
        binding.etSearch.setOnTouchListener(MyOnTouchListener(true, binding.etSearch, keyboardUtil))
        binding.root.setOnClickListener {
            keyboardUtil.hideKeyboard()
        }
    }

    override fun initListener() {
        binding.layoutToolbar.flBack.setOnClickListener(this)
        binding.ivCamera.setOnClickListener(this)
        binding.tvSearch.setOnClickListener(this)
        binding.root.setOnClickListener(this)
        binding.layoutToolbar.toolbar.setOnClickListener(this)
    }

    override fun initData() {
        if (carLicense.isEmpty()) {
            return
        }
        query()
    }

    @SuppressLint("CheckResult")
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fl_back -> {
                onBackPressedSupport()
            }

            R.id.iv_camera -> {
                ARouter.getInstance().build(ARouterMap.SCAN_PLATE).navigation(this@CAUrgeListActivity, 1)
            }

            R.id.tv_search -> {
                carLicense = binding.etSearch.text.toString()
                if (carLicense.isEmpty()) {
                    ToastUtil.showBottomToast(i18n(com.rt.base.R.string.请输入车牌号))
                    return
                }
                if (carLicense.length != 7 && carLicense.length != 8) {
                    ToastUtil.showBottomToast(i18N(com.rt.base.R.string.车牌长度只能是7位或8位))
                    return
                }
                query()
            }

            R.id.toolbar,
            binding.root.id -> {
                keyboardUtil.hideKeyboard()
            }

            R.id.fl_color -> {
                checkedColor = v.tag as String
                collectionPlateColorAdapter?.updateColor(checkedColor, collectioPlateColorList.indexOf(checkedColor))
                query()
            }

            R.id.rrl_urge -> {
                val urgeBean = v.tag as UrgeBean
                ARouter.getInstance().build(ARouterMap.CA_COLLECTION_MANAGEMENT).withParcelable(ARouterMap.URGE, urgeBean)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .navigation()
            }
        }
    }

    fun query() {
        keyboardUtil.hideKeyboard()
        showProgressDialog(20000)
        carLicense = binding.etSearch.text.toString()
        runBlocking {
            token = PreferencesDataStore(BaseApplication.instance()).getString(PreferencesKeys.token)
            val param = HashMap<String, Any>()
            param["token"] = token
            param["plateId"] = carLicense
            param["plateColor"] = checkedColor
            mViewModel.urgepaylist(param)
        }
    }

    override fun startObserve() {
        super.startObserve()
        mViewModel.apply {
            urgepaylistLiveData.observe(this@CAUrgeListActivity) {
                dismissProgressDialog()
                urgeList.clear()
                urgeList.addAll(it)
                if (urgeList.size > 0) {
                    caUrgeAdapter?.updateCarLicense(carLicense)
                    binding.rvUrge.show()
                    binding.layoutNoData.root.gone()
                    caUrgeAdapter?.setList(urgeList)
                } else {
                    binding.rvUrge.gone()
                    binding.layoutNoData.root.show()
                }
            }
            errMsg.observe(this@CAUrgeListActivity) {
                dismissProgressDialog()
                ToastUtil.showBottomToast(it.msg)
            }
            mException.observe(this@CAUrgeListActivity) {
                dismissProgressDialog()
            }
        }
    }

    override fun providerVMClass(): Class<DebtCollectionViewModel> {
        return DebtCollectionViewModel::class.java
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                val plate = data?.getStringExtra("plate")
                if (!plate.isNullOrEmpty()) {
                    val plateId = if (plate.contains("新能源")) {
                        plate.substring(plate.length - 8, plate.length)
                    } else {
                        plate.substring(plate.length.minus(7) ?: 0, plate.length)
                    }
                    binding.etSearch.setText(plateId)
                    binding.etSearch.setSelection(plateId.length)
                }
            } else if (requestCode == 2) {
                val plate = data?.getStringExtra("plate")
                if (!plate.isNullOrEmpty()) {
                    val plateId = if (plate.contains("新能源")) {
                        plate.substring(plate.length - 8, plate.length)
                    } else {
                        plate.substring(plate.length.minus(7) ?: 0, plate.length)
                    }
                }
            }
        }
    }

    override fun isRegEventBus(): Boolean {
        return true
    }

    override fun getVbBindingView(): ViewBinding {
        return ActivityUrgeListBinding.inflate(layoutInflater)
    }

    override fun onReloadData() {
    }

    override val isFullScreen: Boolean
        get() = true

    override fun marginStatusBarView(): View {
        return binding.layoutToolbar.ablToolbar
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (keyboardUtil.isShow()) {
                keyboardUtil.hideKeyboard()
            } else {
                return super.onKeyDown(keyCode, event)
            }
        }
        return false
    }
}