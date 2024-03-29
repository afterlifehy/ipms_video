package com.peakinfo.plateid.ui.activity.abnormal

import android.annotation.SuppressLint
import android.content.Intent
import android.view.KeyEvent
import android.view.View
import android.view.View.OnClickListener
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.alibaba.fastjson.JSONObject
import com.peakinfo.base.BaseApplication
import com.peakinfo.base.arouter.ARouterMap
import com.peakinfo.base.bean.Street
import com.peakinfo.base.ds.PreferencesDataStore
import com.peakinfo.base.ds.PreferencesKeys
import com.peakinfo.base.ext.gone
import com.peakinfo.base.ext.hide
import com.peakinfo.base.ext.i18n
import com.peakinfo.base.ext.show
import com.peakinfo.base.util.ToastUtil
import com.peakinfo.base.viewbase.VbBaseActivity
import com.peakinfo.common.event.ParkingSpaceBackEvent
import com.peakinfo.common.realm.RealmUtil
import com.peakinfo.common.util.AppUtil
import com.peakinfo.common.util.Constant
import com.peakinfo.common.util.GlideUtils
import com.peakinfo.common.view.keyboard.KeyboardUtil
import com.peakinfo.common.view.keyboard.MyTextWatcher
import com.peakinfo.plateid.R
import com.peakinfo.plateid.adapter.CollectionPlateColorAdapter
import com.peakinfo.plateid.databinding.ActivityBerthAbnormalBinding
import com.peakinfo.plateid.dialog.AbnormalClassificationDialog
import com.peakinfo.plateid.dialog.AbnormalStreetListDialog
import com.peakinfo.plateid.mvvm.viewmodel.BerthAbnormalViewModel
import kotlinx.coroutines.runBlocking
import org.greenrobot.eventbus.EventBus

@Route(path = ARouterMap.BERTH_ABNORMAL)
class BerthAbnormalActivity : VbBaseActivity<BerthAbnormalViewModel, ActivityBerthAbnormalBinding>(), OnClickListener {
    var collectionPlateColorAdapter: CollectionPlateColorAdapter? = null
    var collectioPlateColorList: MutableList<String> = ArrayList()
    var checkedColor = ""
    private lateinit var keyboardUtil: KeyboardUtil
    val widthType = 1

    var abnormalStreetListDialog: AbnormalStreetListDialog? = null
    var streetList: MutableList<Street> = ArrayList()

    var abnormalClassificationDialog: AbnormalClassificationDialog? = null
    var classificationList: MutableList<String> = ArrayList()
    var currentStreet: Street? = null

    var parkingNo = ""
    var streetNo = ""
    var orderNo = ""
    var carColor = ""
    var carLicense = ""
    var type = ""

    override fun initView() {
        binding.layoutToolbar.tvTitle.text = i18n(com.peakinfo.base.R.string.泊位异常上报)
        GlideUtils.instance?.loadImage(binding.layoutToolbar.ivRight, com.peakinfo.common.R.mipmap.ic_help)
        binding.layoutToolbar.ivRight.show()

        if (intent.getStringExtra(ARouterMap.ABNORMAL_STREET_NO) != null) {
            streetNo = intent.getStringExtra(ARouterMap.ABNORMAL_STREET_NO)!!
            parkingNo = intent.getStringExtra(ARouterMap.ABNORMAL_PARKING_NO)!!
            orderNo = intent.getStringExtra(ARouterMap.ABNORMAL_ORDER_NO)!!
            binding.retParkingNo.setText(parkingNo.replaceFirst(streetNo + "-", ""))
            carLicense = intent.getStringExtra(ARouterMap.ABNORMAL_CARLICENSE)!!
            carColor = intent.getStringExtra(ARouterMap.ABNORMAL_CAR_COLOR)!!
        }

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

    override fun initListener() {
        binding.layoutToolbar.flBack.setOnClickListener(this)
        binding.layoutToolbar.ivRight.setOnClickListener(this)
        binding.cbLotName.setOnClickListener(this)
        binding.cbAbnormalClassification.setOnClickListener(this)
        binding.rflAbnormalClassification.setOnClickListener(this)
        binding.rflRecognize.setOnClickListener(this)
        binding.rflReport.setOnClickListener(this)
        binding.root.setOnClickListener(this)
        binding.llBerthAbnormal2.setOnClickListener(this)
    }

    override fun initData() {
        RealmUtil.instance?.findCheckedStreetList()?.let { streetList.addAll(it) }
        if (streetNo.isNotEmpty()) {
            for (i in streetList) {
                if (i.streetNo == streetNo) {
                    currentStreet = i
                }
            }
        } else {
            currentStreet = RealmUtil.instance?.findCurrentStreet()
        }
        if (streetList.size == 1) {
            binding.cbLotName.hide()
            binding.rflLotName.setOnClickListener(null)
        } else {
            binding.cbLotName.show()
            binding.rflLotName.setOnClickListener(this)
        }
        binding.tvLotName.text = currentStreet?.streetName
        binding.rtvStreetNo.text = currentStreet?.streetNo

        classificationList.add(i18n(com.peakinfo.base.R.string.泊位有车POS无订单))
        classificationList.add(i18n(com.peakinfo.base.R.string.泊位无车POS有订单))
        classificationList.add(i18n(com.peakinfo.base.R.string.在停车牌与POS不一致))


    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initKeyboard() {
        keyboardUtil = KeyboardUtil(binding.kvKeyBoard) {
            binding.etPlate.requestFocus()
            keyboardUtil.changeKeyboard(true)
            keyboardUtil.setEditText(binding.etPlate)
        }

        binding.etPlate.addTextChangedListener(MyTextWatcher(null, null, true, keyboardUtil))

        binding.etPlate.setOnTouchListener { v, p1 ->
            (v as EditText).requestFocus()
            keyboardUtil.changeKeyboard(true)
            val clickPosition = v.getOffsetForPosition(p1!!.x, p1.y)
            keyboardUtil.setEditText(v, clickPosition)
            keyboardUtil.showKeyboard(show = {
                val location = IntArray(2)
                v.getLocationOnScreen(location)
                val editTextPosY = location[1]

                val screenHeight = window!!.windowManager.defaultDisplay.height
                val distanceToBottom: Int = screenHeight - editTextPosY - v.getHeight()

                if (binding.kvKeyBoard.height > distanceToBottom) {
                    // 当键盘高度超过输入框到屏幕底部的距离时，向上移动布局
                    binding.llBerthAbnormal.translationY = (-(binding.kvKeyBoard.height - distanceToBottom)).toFloat()
                }
            }, hide = {
                binding.llBerthAbnormal.translationY = 0f
            })
            true
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

    override fun onClick(v: View?) {
        if (keyboardUtil.isShow()) {
            keyboardUtil.hideKeyboard()
        }
        when (v?.id) {
            R.id.fl_back -> {
                onBackPressedSupport()
            }

            R.id.iv_right -> {
                ARouter.getInstance().build(ARouterMap.ABNORMAL_HELP).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).navigation()
            }

            R.id.cb_lotName -> {
                showAbnormalStreetListDialog()
            }

            R.id.rfl_lotName -> {
                binding.cbLotName.isChecked = true
                showAbnormalStreetListDialog()
            }

            R.id.cb_abnormalClassification -> {
                showAbnormalClassificationDialog()
            }

            R.id.rfl_abnormalClassification -> {
                binding.cbAbnormalClassification.isChecked = true
                showAbnormalClassificationDialog()
            }

            R.id.rfl_recognize -> {
                ARouter.getInstance().build(ARouterMap.SCAN_PLATE).navigation(this@BerthAbnormalActivity, 1)
            }

            R.id.rfl_report -> {
                type = AppUtil.fillZero((classificationList.indexOf(binding.tvAbnormalClassification.text.toString()) + 1).toString())
                if (binding.retParkingNo.text.toString().isEmpty()) {
                    ToastUtil.showMiddleToast(i18n(com.peakinfo.base.R.string.请填写泊位号))
                    return
                }
                if (type == "00") {
                    ToastUtil.showMiddleToast(i18n(com.peakinfo.base.R.string.请选择异常分类))
                    return
                }
                if (type != "02" && binding.etPlate.text.toString().isEmpty()) {
                    ToastUtil.showMiddleToast(i18n(com.peakinfo.base.R.string.请填写车牌))
                    return
                }
                if (type != "02") {
                    if (binding.etPlate.text.toString().length != 7 && binding.etPlate.text.toString().length != 8) {
                        ToastUtil.showMiddleToast(i18n(com.peakinfo.base.R.string.车牌长度只能是7位或8位))
                        return
                    }
                }
                if (type != "02" && checkedColor.isEmpty()) {
                    ToastUtil.showMiddleToast(i18n(com.peakinfo.base.R.string.请选择车牌颜色))
                    return
                }
                runBlocking {
                    val loginName = PreferencesDataStore(BaseApplication.instance()).getString(PreferencesKeys.loginName)
                    val param = HashMap<String, Any>()
                    val jsonobject = JSONObject()
                    jsonobject["loginName"] = loginName
                    jsonobject["streetNo"] = currentStreet?.streetNo
                    jsonobject["parkingNo"] = currentStreet?.streetNo + "-" + fillZero(binding.retParkingNo.text.toString())
                    jsonobject["type"] = type
                    jsonobject["remark"] = binding.retRemarks.text.toString()
                    if (type == "02") {
                        jsonobject["carLicense"] = carLicense
                        jsonobject["carColor"] = carColor
                    } else {
                        jsonobject["carLicense"] = binding.etPlate.text.toString()
                        jsonobject["carColor"] = checkedColor
                    }
                    jsonobject["orderNo"] = orderNo
                    param["attr"] = jsonobject
                    mViewModel.abnormalReport(param)
                    ToastUtil.showMiddleToast(i18n(com.peakinfo.base.R.string.已上报请等待处理))
                    EventBus.getDefault().post(ParkingSpaceBackEvent())
                    onBackPressedSupport()
                }
            }

            R.id.ll_berthAbnormal2, binding.root.id -> {

            }

            R.id.fl_color -> {
                checkedColor = v.tag as String
                collectionPlateColorAdapter?.updateColor(checkedColor, collectioPlateColorList.indexOf(checkedColor))
            }
        }
    }

    fun fillZero(value: String): String {
        if (value.length == 2) {
            return "0" + value
        } else if (value.length == 1) {
            return "00" + value
        } else {
            return value
        }
    }

    fun showAbnormalStreetListDialog() {
        abnormalStreetListDialog =
            AbnormalStreetListDialog(streetList, currentStreet!!, object : AbnormalStreetListDialog.AbnormalStreetCallBack {
                override fun chooseStreet(street: Street) {
                    currentStreet = street
                    binding.tvLotName.text = currentStreet?.streetName
                    binding.rtvStreetNo.text = currentStreet?.streetNo
                }
            })
        abnormalStreetListDialog?.show()
        abnormalStreetListDialog?.setOnDismissListener {
            binding.cbLotName.isChecked = false
        }
    }

    fun showAbnormalClassificationDialog() {
        abnormalClassificationDialog = AbnormalClassificationDialog(classificationList,
            binding.tvAbnormalClassification.text.toString(),
            object : AbnormalClassificationDialog.AbnormalClassificationCallBack {
                override fun chooseClassification(classification: String) {
                    binding.tvAbnormalClassification.text = classification
                    if (classification == i18n(com.peakinfo.base.R.string.在停车牌与POS不一致) || classification == i18n(com.peakinfo.base.R.string.泊位有车POS无订单)) {
                        binding.llPlate.show()
                        binding.rvPlateColor.show()
                    } else {
                        binding.llPlate.gone()
                        binding.rvPlateColor.gone()
                    }
                }
            })
        abnormalClassificationDialog?.show()
        abnormalClassificationDialog?.setOnDismissListener {
            binding.cbAbnormalClassification.isChecked = false
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
                    binding.etPlate.setText(plateId)
                    binding.etPlate.setSelection(plateId.length)
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

    override fun startObserve() {
        super.startObserve()
        mViewModel.apply {
            abnormalReportLiveData.observe(this@BerthAbnormalActivity) {
            }
            errMsg.observe(this@BerthAbnormalActivity) {
                dismissProgressDialog()
                ToastUtil.showMiddleToast(it.msg)
            }
            mException.observe(this@BerthAbnormalActivity) {
                dismissProgressDialog()
            }
        }
    }

    override fun getVbBindingView(): ViewBinding {
        return ActivityBerthAbnormalBinding.inflate(layoutInflater)
    }

    override fun onReloadData() {
    }

    override val isFullScreen: Boolean
        get() = true

    override fun marginStatusBarView(): View {
        return binding.layoutToolbar.ablToolbar
    }

    override fun providerVMClass(): Class<BerthAbnormalViewModel> {
        return BerthAbnormalViewModel::class.java
    }

}