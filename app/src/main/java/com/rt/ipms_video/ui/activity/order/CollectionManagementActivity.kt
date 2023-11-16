package com.rt.ipms_video.ui.activity.order

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.view.KeyEvent
import android.view.View
import android.view.View.OnClickListener
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.rt.base.BaseApplication
import com.rt.base.arouter.ARouterMap
import com.rt.base.ext.i18N
import com.rt.base.viewbase.VbBaseActivity
import com.rt.common.util.GlideUtils
import com.rt.common.view.keyboard.KeyboardUtil
import com.rt.common.view.keyboard.MyTextWatcher
import com.rt.ipms_video.R
import com.rt.ipms_video.adapter.CollectionPlateColorAdapter
import com.rt.ipms_video.databinding.ActivityCollectionManagementBinding
import com.rt.ipms_video.dialog.AbnormalStreetListDialog
import com.rt.ipms_video.mvvm.viewmodel.CollectionManagementViewModel
import com.tbruyelle.rxpermissions3.RxPermissions

@Route(path = ARouterMap.COLLECTION_MANAGEMENT)
class CollectionManagementActivity : VbBaseActivity<CollectionManagementViewModel, ActivityCollectionManagementBinding>(), OnClickListener {
    var collectionPlateColorAdapter: CollectionPlateColorAdapter? = null
    var collectioPlateColorList: MutableList<Int> = ArrayList()
    var checkedColor = 0
    private lateinit var keyboardUtil: KeyboardUtil
    val widthType = 3
    var streetList: MutableList<Int> = ArrayList()
    var abnormalStreetListDialog: AbnormalStreetListDialog? = null

    override fun initView() {
        GlideUtils.instance?.loadImage(binding.layoutToolbar.ivBack, com.rt.common.R.mipmap.ic_back_white)
        binding.layoutToolbar.tvTitle.text = i18N(com.rt.base.R.string.催缴管理)
        binding.layoutToolbar.tvTitle.setTextColor(ContextCompat.getColor(BaseApplication.instance(), com.rt.base.R.color.white))

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
        binding.rflRecognize.setOnClickListener(this)
        binding.rflStreetName.setOnClickListener(this)
        binding.rflSubmit.setOnClickListener(this)
    }

    override fun initData() {
        streetList.add(1)
        streetList.add(2)
        streetList.add(3)
        streetList.add(4)
        streetList.add(5)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initKeyboard() {
        keyboardUtil = KeyboardUtil(binding.kvKeyBoard) {
            binding.retPlate.requestFocus()
            keyboardUtil.changeKeyboard(true)
            keyboardUtil.setEditText(binding.retPlate)
        }

        binding.retPlate.addTextChangedListener(MyTextWatcher(null, null, true, keyboardUtil))

        binding.retPlate.setOnTouchListener { v, p1 ->
            (v as EditText).requestFocus()
            keyboardUtil.showKeyboard(show = {
                val location = IntArray(2)
                v.getLocationOnScreen(location)
                val editTextPosY = location[1]

                val screenHeight = window!!.windowManager.defaultDisplay.height
                val distanceToBottom: Int = screenHeight - editTextPosY - v.getHeight()

                if (binding.kvKeyBoard.height > distanceToBottom) {
                    // 当键盘高度超过输入框到屏幕底部的距离时，向上移动布局
                    binding.rllManagement.translationY = (-(binding.kvKeyBoard.height - distanceToBottom)).toFloat()
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
                binding.rllManagement.translationY = 0f
                keyboardUtil.hideKeyboard()
            } else {
                return super.onKeyDown(keyCode, event)
            }
        }
        return false
    }

    override fun onClick(v: View?) {
        if (keyboardUtil.isShow()) {
            binding.rllManagement.translationY = 0f
            keyboardUtil.hideKeyboard()
        }
        when (v?.id) {
            R.id.fl_back -> {
                onBackPressedSupport()
            }

            R.id.rfl_recognize -> {
                ARouter.getInstance().build(ARouterMap.SCAN_PLATE).navigation(this@CollectionManagementActivity, 1)
            }

            R.id.rfl_streetName -> {
                showAbnormalStreetListDialog()
            }

            R.id.rfl_submit -> {

            }

            R.id.fl_color -> {
                checkedColor = v.tag as Int
                collectionPlateColorAdapter?.updateColor(checkedColor, collectioPlateColorList.indexOf(checkedColor))
            }
        }
    }

    fun showAbnormalStreetListDialog() {
        val currentStreet = if (binding.tvStreetName.text.toString().isEmpty()) {
            0
        } else {
            binding.tvStreetName.text.toString().toInt()
        }
        abnormalStreetListDialog = AbnormalStreetListDialog(
            streetList,
            currentStreet,
            object : AbnormalStreetListDialog.AbnormalStreetCallBack {
                override fun chooseStreet(currentStreet: Int) {
                    binding.tvStreetName.text = currentStreet.toString()
                }
            })
        abnormalStreetListDialog?.show()
        abnormalStreetListDialog?.setOnDismissListener {
            binding.cbStreetName.isChecked = false
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
                    binding.retPlate.setText(plateId)
                }
            }
        }
    }

    override fun getVbBindingView(): ViewBinding {
        return ActivityCollectionManagementBinding.inflate(layoutInflater)
    }

    override fun onReloadData() {
    }

    override val isFullScreen: Boolean
        get() = true

    override fun marginStatusBarView(): View {
        return binding.layoutToolbar.ablToolbar
    }

    override fun providerVMClass(): Class<CollectionManagementViewModel> {
        return CollectionManagementViewModel::class.java
    }
}