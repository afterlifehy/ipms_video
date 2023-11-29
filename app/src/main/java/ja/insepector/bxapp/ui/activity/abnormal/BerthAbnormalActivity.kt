package ja.insepector.bxapp.ui.activity.abnormal

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
import ja.insepector.base.BaseApplication
import ja.insepector.base.arouter.ARouterMap
import ja.insepector.base.bean.Street
import ja.insepector.base.ds.PreferencesDataStore
import ja.insepector.base.ds.PreferencesKeys
import ja.insepector.base.ext.gone
import ja.insepector.base.ext.i18n
import ja.insepector.base.ext.show
import ja.insepector.base.util.ToastUtil
import ja.insepector.base.viewbase.VbBaseActivity
import ja.insepector.common.realm.RealmUtil
import ja.insepector.common.util.AppUtil
import ja.insepector.common.util.GlideUtils
import ja.insepector.common.view.keyboard.KeyboardUtil
import ja.insepector.common.view.keyboard.MyTextWatcher
import ja.insepector.bxapp.R
import ja.insepector.bxapp.adapter.CollectionPlateColorAdapter
import ja.insepector.bxapp.databinding.ActivityBerthAbnormalBinding
import ja.insepector.bxapp.dialog.AbnormalClassificationDialog
import ja.insepector.bxapp.dialog.AbnormalStreetListDialog
import ja.insepector.bxapp.mvvm.viewmodel.BerthAbnormalViewModel
import kotlinx.coroutines.runBlocking

@Route(path = ARouterMap.BERTH_ABNORMAL)
class BerthAbnormalActivity : VbBaseActivity<BerthAbnormalViewModel, ActivityBerthAbnormalBinding>(), OnClickListener {
    var collectionPlateColorAdapter: CollectionPlateColorAdapter? = null
    var collectioPlateColorList: MutableList<Int> = ArrayList()
    var checkedColor = 0
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

    override fun initView() {
        binding.layoutToolbar.tvTitle.text = i18n(ja.insepector.base.R.string.泊位异常上报)
        GlideUtils.instance?.loadImage(binding.layoutToolbar.ivRight, ja.insepector.common.R.mipmap.ic_help)
        binding.layoutToolbar.ivRight.show()

        if (intent.getStringExtra(ARouterMap.ABNORMAL_STREET_NO) != null) {
            streetNo = intent.getStringExtra(ARouterMap.ABNORMAL_STREET_NO)!!
            parkingNo = intent.getStringExtra(ARouterMap.ABNORMAL_PARKING_NO)!!
            orderNo = intent.getStringExtra(ARouterMap.ABNORMAL_ORDER_NO)!!
            binding.retParkingNo.setText(parkingNo.replaceFirst(streetNo + "-", ""))
        }

        collectioPlateColorList.add(0)
        collectioPlateColorList.add(1)
        collectioPlateColorList.add(2)
        collectioPlateColorList.add(3)
        collectioPlateColorList.add(4)
        collectioPlateColorList.add(5)
        collectioPlateColorList.add(6)
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
        binding.rflLotName.setOnClickListener(this)
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
        binding.tvLotName.text = currentStreet?.streetName
        binding.rtvStreetNo.text = currentStreet?.streetNo

        classificationList.add(i18n(ja.insepector.base.R.string.泊位有车POS无订单))
        classificationList.add(i18n(ja.insepector.base.R.string.泊位无车POS有订单))
        classificationList.add(i18n(ja.insepector.base.R.string.在停车牌与POS不一致))


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
            })
            keyboardUtil.changeKeyboard(true)
            keyboardUtil.setEditText(v)
            true
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (keyboardUtil.isShow()) {
                binding.llBerthAbnormal.translationY = 0f
                keyboardUtil.hideKeyboard()
            } else {
                return super.onKeyDown(keyCode, event)
            }
        }
        return false
    }

    override fun onClick(v: View?) {
        if (keyboardUtil.isShow()) {
            binding.llBerthAbnormal.translationY = 0f
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
                if (binding.retParkingNo.text.toString().isEmpty()) {
                    ToastUtil.showToast(i18n(ja.insepector.base.R.string.请填写泊位号))
                    return
                }
                runBlocking {
                    val loginName = PreferencesDataStore(BaseApplication.instance()).getString(PreferencesKeys.loginName)
                    val param = HashMap<String, Any>()
                    val jsonobject = JSONObject()
                    jsonobject["loginName"] = loginName
                    jsonobject["streetNo"] = currentStreet?.streetNo
                    jsonobject["parkingNo"] = currentStreet?.streetNo + "-" + binding.retParkingNo.text.toString()
                    jsonobject["type"] =
                        AppUtil.fillZero((classificationList.indexOf(binding.tvAbnormalClassification.text.toString()) + 1).toString())
                    jsonobject["remark"] = binding.retRemarks.text.toString()
                    jsonobject["carLicense"] = binding.etPlate.text.toString()
                    jsonobject["carColor"] = checkedColor
                    jsonobject["orderNo"] = orderNo
                    param["attr"] = jsonobject
                    showProgressDialog(20000)
                    mViewModel.abnormalReport(param)
                }
            }

            R.id.ll_berthAbnormal2, binding.root.id -> {

            }

            R.id.fl_color -> {
                checkedColor = v.tag as Int
                collectionPlateColorAdapter?.updateColor(checkedColor, collectioPlateColorList.indexOf(checkedColor))
            }
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
                    if (classification == i18n(ja.insepector.base.R.string.在停车牌与POS不一致) || classification == i18n(ja.insepector.base.R.string.泊位有车POS无订单)) {
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
                    if (plate.startsWith("蓝")) {
                        collectionPlateColorAdapter?.updateColor(0, 0)
                    } else if (plate.startsWith("绿")) {
                        collectionPlateColorAdapter?.updateColor(1, 1)
                    } else if (plate.startsWith("黄")) {
                        collectionPlateColorAdapter?.updateColor(2, 2)
                    } else if (plate.startsWith("黄绿")) {
                        collectionPlateColorAdapter?.updateColor(3, 3)
                    } else if (plate.startsWith("白")) {
                        collectionPlateColorAdapter?.updateColor(4, 4)
                    } else if (plate.startsWith("黑")) {
                        collectionPlateColorAdapter?.updateColor(5, 5)
                    } else {
                        collectionPlateColorAdapter?.updateColor(6, 6)
                    }
                }
            }
        }
    }

    override fun startObserve() {
        super.startObserve()
        mViewModel.apply {
            abnormalReportLiveData.observe(this@BerthAbnormalActivity) {
                dismissProgressDialog()
                ToastUtil.showToast(i18n(ja.insepector.base.R.string.上报成功))
                onBackPressedSupport()
            }
            errMsg.observe(this@BerthAbnormalActivity) {
                dismissProgressDialog()
                ToastUtil.showToast(it.msg)
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