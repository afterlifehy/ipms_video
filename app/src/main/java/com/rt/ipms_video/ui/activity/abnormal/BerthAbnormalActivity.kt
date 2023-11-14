package com.rt.ipms_video.ui.activity.abnormal

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.DialogInterface.OnDismissListener
import android.content.Intent
import android.view.KeyEvent
import android.view.View
import android.view.View.OnClickListener
import android.widget.CompoundButton
import android.widget.CompoundButton.OnCheckedChangeListener
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.rt.base.BaseApplication
import com.rt.base.arouter.ARouterMap
import com.rt.base.ext.gone
import com.rt.base.ext.i18n
import com.rt.base.ext.show
import com.rt.base.viewbase.VbBaseActivity
import com.rt.common.util.GlideUtils
import com.rt.common.view.keyboard.KeyboardUtil
import com.rt.common.view.keyboard.MyTextWatcher
import com.rt.ipms_video.R
import com.rt.ipms_video.adapter.CollectionPlateColorAdapter
import com.rt.ipms_video.databinding.ActivityBerthAbnormalBinding
import com.rt.ipms_video.dialog.AbnormalClassificationDialog
import com.rt.ipms_video.dialog.AbnormalStreetListDialog
import com.rt.ipms_video.mvvm.viewmodel.BerthAbnormalViewModel

@Route(path = ARouterMap.BERTH_ABNORMAL)
class BerthAbnormalActivity : VbBaseActivity<BerthAbnormalViewModel, ActivityBerthAbnormalBinding>(), OnClickListener {
    var collectionPlateColorAdapter: CollectionPlateColorAdapter? = null
    var collectioPlateColorList: MutableList<Int> = ArrayList()
    var checkedColor = 0
    private lateinit var keyboardUtil: KeyboardUtil
    val widthType = 1

    var abnormalStreetListDialog: AbnormalStreetListDialog? = null
    var streetList: MutableList<Int> = ArrayList()

    var abnormalClassificationDialog: AbnormalClassificationDialog? = null
    var classificationList: MutableList<String> = ArrayList()

    override fun initView() {
        binding.layoutToolbar.tvTitle.text = i18n(com.rt.base.R.string.泊位异常上报)
        GlideUtils.instance?.loadImage(binding.layoutToolbar.ivRight, com.rt.common.R.mipmap.ic_help)
        binding.layoutToolbar.ivRight.show()

        collectioPlateColorList.add(com.rt.common.R.mipmap.ic_plate_blue)
        collectioPlateColorList.add(com.rt.common.R.mipmap.ic_plate_green)
        collectioPlateColorList.add(com.rt.common.R.mipmap.ic_plate_yellow)
        collectioPlateColorList.add(com.rt.common.R.mipmap.ic_plate_yellow_green)
        collectioPlateColorList.add(com.rt.common.R.mipmap.ic_plate_white)
        collectioPlateColorList.add(com.rt.common.R.mipmap.ic_plate_black)
        collectioPlateColorList.add(com.rt.common.R.mipmap.ic_plate_other)
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
        streetList.add(1)
        streetList.add(2)
        streetList.add(3)
        streetList.add(4)
        streetList.add(5)

        classificationList.add(i18n(com.rt.base.R.string.泊位有车POS无订单))
        classificationList.add(i18n(com.rt.base.R.string.泊位无车POS有订单))
        classificationList.add(i18n(com.rt.base.R.string.在停车牌与POS不一致))
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
                onBackPressedSupport()
            }

            R.id.ll_berthAbnormal2,
            binding.root.id -> {

            }

            R.id.fl_color -> {
                checkedColor = v.tag as Int
                collectionPlateColorAdapter?.updateColor(checkedColor, collectioPlateColorList.indexOf(checkedColor))
            }
        }
    }

    fun showAbnormalStreetListDialog() {
        abnormalStreetListDialog = AbnormalStreetListDialog(streetList, object : AbnormalStreetListDialog.AbnormalStreetCallBack {
            override fun chooseStreet(currentStreet: Int) {
                binding.tvLotName.text = currentStreet.toString()
            }
        })
        abnormalStreetListDialog?.show()
        abnormalStreetListDialog?.setOnDismissListener {
            binding.cbLotName.isChecked = false
        }
    }

    fun showAbnormalClassificationDialog() {
        abnormalClassificationDialog =
            AbnormalClassificationDialog(
                classificationList,
                binding.tvAbnormalClassification.text.toString(),
                object : AbnormalClassificationDialog.AbnormalClassificationCallBack {
                    override fun chooseClassification(classification: String) {
                        binding.tvAbnormalClassification.text = classification
                        if (classification == i18n(com.rt.base.R.string.在停车牌与POS不一致)) {
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
                }
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