package com.rt.ipms_video.ui.activity.order

import android.annotation.SuppressLint
import android.content.Intent
import android.view.KeyEvent
import android.view.View
import android.view.View.OnClickListener
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.alibaba.fastjson.JSONObject
import com.rt.base.BaseApplication
import com.rt.base.arouter.ARouterMap
import com.rt.base.bean.DebtCollectionBean
import com.rt.base.ds.PreferencesDataStore
import com.rt.base.ds.PreferencesKeys
import com.rt.base.ext.gone
import com.rt.base.ext.i18N
import com.rt.base.ext.i18n
import com.rt.base.ext.show
import com.rt.base.util.ToastUtil
import com.rt.base.viewbase.VbBaseActivity
import com.rt.common.util.GlideUtils
import com.rt.common.view.keyboard.KeyboardUtil
import com.rt.common.view.keyboard.MyOnTouchListener
import com.rt.common.view.keyboard.MyTextWatcher
import com.rt.ipms_video.R
import com.rt.ipms_video.adapter.DebtCollectionAdapter
import com.rt.ipms_video.databinding.ActivityDebtCollectionBinding
import com.rt.ipms_video.dialog.CollectionDialog
import com.rt.ipms_video.mvvm.viewmodel.DebtCollectionViewModel
import kotlinx.coroutines.runBlocking

@Route(path = ARouterMap.DEBT_COLLECTION)
class DebtCollectionActivity : VbBaseActivity<DebtCollectionViewModel, ActivityDebtCollectionBinding>(), OnClickListener {
    private lateinit var keyboardUtil: KeyboardUtil
    var debtCollectionAdapter: DebtCollectionAdapter? = null
    var debtCollectionList: MutableList<DebtCollectionBean> = ArrayList()
    var collectionDialog: CollectionDialog? = null
    var carLicense = ""
    var token = ""

    override fun initView() {
        GlideUtils.instance?.loadImage(binding.layoutToolbar.ivBack, com.rt.common.R.mipmap.ic_back_white)
        binding.layoutToolbar.tvTitle.text = i18N(com.rt.base.R.string.欠费追缴)
        binding.layoutToolbar.tvTitle.setTextColor(ContextCompat.getColor(BaseApplication.instance(), com.rt.base.R.color.white))
        GlideUtils.instance?.loadImage(binding.layoutNoData.ivNoData, com.rt.common.R.mipmap.ic_no_data_2)
        binding.layoutNoData.tvNoDataTitle.text = i18N(com.rt.base.R.string.通过车牌号未查询到欠费订单)

        if (intent.getStringExtra(ARouterMap.DEBT_CAR_LICENSE) != null) {
            carLicense = intent.getStringExtra(ARouterMap.DEBT_CAR_LICENSE).toString()
            binding.etSearch.setText(carLicense)
        }
        binding.rvDebt.setHasFixedSize(true)
        binding.rvDebt.layoutManager = LinearLayoutManager(this)
        debtCollectionAdapter = DebtCollectionAdapter(debtCollectionList, this)
        binding.rvDebt.adapter = debtCollectionAdapter

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
        binding.tvCollect.setOnClickListener(this)
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
                ARouter.getInstance().build(ARouterMap.SCAN_PLATE).navigation(this@DebtCollectionActivity, 1)
            }

            R.id.tv_collect -> {
                collectionDialog = CollectionDialog(object : CollectionDialog.CollecteCallBack {
                    override fun collect(plate: String, color: Int) {

                    }

                })
                collectionDialog?.show()
            }

            R.id.tv_search -> {
                carLicense = binding.etSearch.text.toString()
                if (carLicense.isEmpty()) {
                    ToastUtil.showToast(i18n(com.rt.base.R.string.请输入车牌号))
                    return
                }
                query()
            }

            R.id.toolbar,
            binding.root.id -> {
                keyboardUtil.hideKeyboard()
            }

            R.id.rrl_debtCollection -> {
                val debtCollectionBean = v.tag as DebtCollectionBean
                ARouter.getInstance().build(ARouterMap.DEBT_ORDER_DETAIL).withParcelable(ARouterMap.DEBT_ORDER, debtCollectionBean)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).navigation()
            }
        }
    }

    fun query() {
        keyboardUtil.hideKeyboard()
        showProgressDialog(20000)
        runBlocking {
            token = PreferencesDataStore(BaseApplication.instance()).getString(PreferencesKeys.token)
            val param = HashMap<String, Any>()
            val jsonobject = JSONObject()
            jsonobject["token"] = token
            jsonobject["carLicense"] = carLicense
            param["attr"] = jsonobject
            mViewModel.debtInquiry(param)
        }
    }

    override fun startObserve() {
        super.startObserve()
        mViewModel.apply {
            debtInquiryLiveData.observe(this@DebtCollectionActivity) {
                dismissProgressDialog()
                debtCollectionList.clear()
                debtCollectionList.addAll(it.result)
                if (debtCollectionList.size > 0) {
                    binding.rvDebt.show()
                    binding.layoutNoData.root.gone()
                    debtCollectionAdapter?.setList(debtCollectionList)
                } else {
                    binding.rvDebt.gone()
                    binding.layoutNoData.root.show()
                }
            }
            errMsg.observe(this@DebtCollectionActivity) {
                dismissProgressDialog()
                ToastUtil.showToast(it.msg)
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
                }
            } else if (requestCode == 2) {
                val plate = data?.getStringExtra("plate")
                if (!plate.isNullOrEmpty()) {
                    val plateId = if (plate.contains("新能源")) {
                        plate.substring(plate.length - 8, plate.length)
                    } else {
                        plate.substring(plate.length.minus(7) ?: 0, plate.length)
                    }
                    collectionDialog?.setPlate(plateId)
                    if (plate.startsWith("蓝")) {
                        collectionDialog?.collectionPlateColorAdapter?.updateColor(0, 0)
                    } else if (plate.startsWith("绿")) {
                        collectionDialog?.collectionPlateColorAdapter?.updateColor(1, 1)
                    } else if (plate.startsWith("黄")) {
                        collectionDialog?.collectionPlateColorAdapter?.updateColor(2, 2)
                    } else if (plate.startsWith("黄绿")) {
                        collectionDialog?.collectionPlateColorAdapter?.updateColor(3, 3)
                    } else if (plate.startsWith("白")) {
                        collectionDialog?.collectionPlateColorAdapter?.updateColor(4, 4)
                    } else if (plate.startsWith("黑")) {
                        collectionDialog?.collectionPlateColorAdapter?.updateColor(5, 5)
                    } else {
                        collectionDialog?.collectionPlateColorAdapter?.updateColor(6, 6)
                    }
                }
            }

        }
    }

    override fun getVbBindingView(): ViewBinding {
        return ActivityDebtCollectionBinding.inflate(layoutInflater)
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