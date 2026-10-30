package com.peakinfo.plateid.ca.ui.activity

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
import com.peakinfo.base.BaseApplication
import com.peakinfo.base.arouter.ARouterMap
import com.peakinfo.base.bean.ca.FreeParkDetailBean
import com.peakinfo.base.ds.PreferencesDataStore
import com.peakinfo.base.ds.PreferencesKeys
import com.peakinfo.base.ext.gone
import com.peakinfo.base.ext.i18N
import com.peakinfo.base.ext.i18n
import com.peakinfo.base.ext.show
import com.peakinfo.base.util.ToastUtil
import com.peakinfo.base.viewbase.VbBaseActivity
import com.peakinfo.common.util.Constant
import com.peakinfo.common.util.GlideUtils
import com.peakinfo.common.view.keyboard.KeyboardUtil
import com.peakinfo.common.view.keyboard.MyOnTouchListener
import com.peakinfo.common.view.keyboard.MyTextWatcher
import com.peakinfo.plateid.R
import com.peakinfo.plateid.adapter.CollectionPlateColorAdapter
import com.peakinfo.plateid.adapter.FreeParkRecordAdapter
import com.peakinfo.plateid.databinding.ActivityFreeParkRecordBinding
import com.peakinfo.plateid.mvvm.viewmodel.FreeParkRecordViewModel
import kotlinx.coroutines.runBlocking

@Route(path = ARouterMap.CA_FREE_PARK_RECORD)
class CAFreeParkRecordActivity : VbBaseActivity<FreeParkRecordViewModel, ActivityFreeParkRecordBinding>(), OnClickListener {
    private lateinit var keyboardUtil: KeyboardUtil
    var freeParkRecordAdapter :FreeParkRecordAdapter? = null
    var freeParkRecordList:MutableList<FreeParkDetailBean> = ArrayList()

    var collectionPlateColorAdapter: CollectionPlateColorAdapter? = null
    var collectioPlateColorList: MutableList<String> = ArrayList()
    var checkedColor = ""
    var businessId = ""
    var carLicense = ""
    var token = ""

    override fun initView() {
        window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        GlideUtils.instance?.loadImage(binding.layoutToolbar.ivBack, com.peakinfo.common.R.mipmap.ic_back_white)
        binding.layoutToolbar.tvTitle.text = "免费停车记录查询"
        binding.layoutToolbar.tvTitle.setTextColor(ContextCompat.getColor(BaseApplication.instance(), com.peakinfo.base.R.color.white))
        GlideUtils.instance?.loadImage(binding.layoutNoData.ivNoData, com.peakinfo.common.R.mipmap.ic_no_data_2)
        binding.layoutNoData.tvNoDataTitle.text = "未查询到免费停车记录"

        collectioPlateColorList.add(Constant.BLUE)
        collectioPlateColorList.add(Constant.GREEN)
        collectioPlateColorList.add(Constant.YELLOW)
        collectioPlateColorList.add(Constant.YELLOW_GREEN)
        collectioPlateColorList.add(Constant.WHITE)
        collectioPlateColorList.add(Constant.BLACK)
        collectioPlateColorList.add(Constant.OTHERS)

        binding.rvRecord.setHasFixedSize(true)
        binding.rvRecord.layoutManager = LinearLayoutManager(this)
        freeParkRecordAdapter = FreeParkRecordAdapter(freeParkRecordList, this)
        binding.rvRecord.adapter = freeParkRecordAdapter

        binding.rvCarColor.setHasFixedSize(true)
        binding.rvCarColor.layoutManager = LinearLayoutManager(BaseApplication.instance(), LinearLayoutManager.HORIZONTAL, false)
        collectionPlateColorAdapter = CollectionPlateColorAdapter(3, collectioPlateColorList, this)
        binding.rvCarColor.adapter = collectionPlateColorAdapter

        if (intent.getStringExtra(ARouterMap.FREE_PARK_CARLICENSE) != null) {
            carLicense = intent.getStringExtra(ARouterMap.FREE_PARK_CARLICENSE).toString()
            businessId = intent.getStringExtra(ARouterMap.FREE_PARK_BUSINESS_ID).toString()
            checkedColor = intent.getStringExtra(ARouterMap.FREE_PARK_CAR_COLOR).toString()
            binding.etSearch.setText(carLicense)
            binding.etSearch.setSelection(carLicense.length)
            collectionPlateColorAdapter?.updateColor(checkedColor, collectioPlateColorList.indexOf(checkedColor))
        }

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
        runBlocking {
            token = PreferencesDataStore(BaseApplication.instance()).getString(PreferencesKeys.token)
            freeParkDetail()
        }
    }

    fun freeParkDetail(){
        val param = HashMap<String, Any>()
        param["token"] = token
        param["plateId"] = carLicense
        param["businessId"] = businessId
        param["plateColor"] = checkedColor
//        param["plateId"] = "沪A1G296"
//        param["businessId"] = "202610300PD013028587828"
//        param["plateColor"] = "5"
        param["dataTime"] = System.currentTimeMillis()
        mViewModel.freeParkDetail(param)
    }
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fl_back -> {
                onBackPressedSupport()
            }

            R.id.iv_camera -> {
                ARouter.getInstance().build(ARouterMap.SCAN_PLATE).navigation(this@CAFreeParkRecordActivity, 1)
            }

            R.id.tv_search -> {
                carLicense = binding.etSearch.text.toString()
                if (carLicense.isEmpty()) {
                    ToastUtil.showBottomToast(i18n(com.peakinfo.base.R.string.请输入车牌号))
                    return
                }
                if (carLicense.length != 7 && carLicense.length != 8) {
                    ToastUtil.showBottomToast(i18N(com.peakinfo.base.R.string.车牌长度只能是7位或8位))
                    return
                }
                if (checkedColor.isEmpty()) {
                    ToastUtil.showBottomToast("车牌颜色不能为空")
                    return
                }
                freeParkDetail()
            }

            R.id.toolbar,
            binding.root.id -> {
                keyboardUtil.hideKeyboard()
            }

            R.id.fl_color -> {
                checkedColor = v.tag as String
                collectionPlateColorAdapter?.updateColor(checkedColor, collectioPlateColorList.indexOf(checkedColor))
            }
        }
    }

    override fun startObserve() {
        super.startObserve()
        mViewModel.apply {
            freeParkDetailLiveData.observe(this@CAFreeParkRecordActivity) {
                dismissProgressDialog()
                freeParkRecordList.clear()
                it?.let { list ->
                    freeParkRecordList.addAll(list)
                }
                if (freeParkRecordList.size > 0) {
                    binding.rvRecord.show()
                    binding.layoutNoData.root.gone()
                    freeParkRecordAdapter?.setList(freeParkRecordList)
                } else {
                    binding.rvRecord.gone()
                    binding.layoutNoData.root.show()
                }
            }
            errMsg.observe(this@CAFreeParkRecordActivity) {
                dismissProgressDialog()
                ToastUtil.showBottomToast(it.msg)
            }
            mException.observe(this@CAFreeParkRecordActivity) {
                dismissProgressDialog()
            }
        }
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
                    if (plate.startsWith("蓝")) {
                        checkedColor = Constant.BLUE
                        collectionPlateColorAdapter?.updateColor(Constant.BLUE, 0)
                    } else if (plate.startsWith("绿")) {
                        checkedColor = Constant.GREEN
                        collectionPlateColorAdapter?.updateColor(Constant.GREEN, 1)
                    } else if (plate.startsWith("黄")) {
                        checkedColor = Constant.YELLOW
                        collectionPlateColorAdapter?.updateColor(Constant.YELLOW, 2)
                    } else if (plate.startsWith("黄绿")) {
                        checkedColor = Constant.YELLOW_GREEN
                        collectionPlateColorAdapter?.updateColor(Constant.YELLOW_GREEN, 3)
                    } else if (plate.startsWith("白")) {
                        checkedColor = Constant.WHITE
                        collectionPlateColorAdapter?.updateColor(Constant.WHITE, 4)
                    } else if (plate.startsWith("黑")) {
                        checkedColor = Constant.BLACK
                        collectionPlateColorAdapter?.updateColor(Constant.BLACK, 5)
                    } else {
                        checkedColor = Constant.OTHERS
                        collectionPlateColorAdapter?.updateColor(Constant.OTHERS, 6)
                    }
                }
            }
        }
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

    override fun getVbBindingView(): ViewBinding {
        return ActivityFreeParkRecordBinding.inflate(layoutInflater)
    }

    override fun onReloadData() {
    }

    override val isFullScreen: Boolean
        get() = true

    override fun marginStatusBarView(): View? {
        return binding.layoutToolbar.ablToolbar
    }

    override fun providerVMClass(): Class<FreeParkRecordViewModel>? {
        return FreeParkRecordViewModel::class.java
    }
}