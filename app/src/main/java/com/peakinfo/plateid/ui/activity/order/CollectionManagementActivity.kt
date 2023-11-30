package com.peakinfo.plateid.ui.activity.order

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.provider.MediaStore
import android.view.KeyEvent
import android.view.View
import android.view.View.OnClickListener
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.alibaba.fastjson.JSONObject
import com.blankj.utilcode.util.EncodeUtils
import com.blankj.utilcode.util.ImageUtils
import com.blankj.utilcode.util.UriUtils
import com.peakinfo.base.BaseApplication
import com.peakinfo.base.arouter.ARouterMap
import com.peakinfo.base.bean.Street
import com.peakinfo.base.ds.PreferencesDataStore
import com.peakinfo.base.ds.PreferencesKeys
import com.peakinfo.base.ext.i18N
import com.peakinfo.base.ext.show
import com.peakinfo.base.util.ToastUtil
import com.peakinfo.base.viewbase.VbBaseActivity
import com.peakinfo.common.realm.RealmUtil
import com.peakinfo.common.util.GlideUtils
import com.peakinfo.common.view.keyboard.KeyboardUtil
import com.peakinfo.common.view.keyboard.MyTextWatcher
import com.peakinfo.plateid.R
import com.peakinfo.plateid.adapter.CollectionPlateColorAdapter
import com.peakinfo.plateid.databinding.ActivityCollectionManagementBinding
import com.peakinfo.plateid.dialog.AbnormalStreetListDialog
import com.peakinfo.plateid.dialog.SelectPicDialog
import com.peakinfo.plateid.mvvm.viewmodel.CollectionManagementViewModel
import com.tbruyelle.rxpermissions3.RxPermissions
import kotlinx.coroutines.runBlocking

@Route(path = ARouterMap.COLLECTION_MANAGEMENT)
class CollectionManagementActivity : VbBaseActivity<CollectionManagementViewModel, ActivityCollectionManagementBinding>(), OnClickListener {
    var collectionPlateColorAdapter: CollectionPlateColorAdapter? = null
    var collectioPlateColorList: MutableList<Int> = ArrayList()
    var checkedColor = 0
    private lateinit var keyboardUtil: KeyboardUtil
    val widthType = 3
    var streetList: MutableList<Street> = ArrayList()
    var abnormalStreetListDialog: AbnormalStreetListDialog? = null
    var streetNo = ""
    var currentStreet: Street? = null
    var selectPicDialog: SelectPicDialog? = null
    var currentPic = 1
    var pic1Base64 = ""
    var pic2Base64 = ""

    override fun initView() {
        GlideUtils.instance?.loadImage(binding.layoutToolbar.ivBack, com.peakinfo.common.R.mipmap.ic_back_white)
        binding.layoutToolbar.tvTitle.text = i18N(com.peakinfo.base.R.string.催缴管理)
        binding.layoutToolbar.tvTitle.setTextColor(ContextCompat.getColor(BaseApplication.instance(), com.peakinfo.base.R.color.white))

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
        binding.cbStreetName.setOnClickListener(this)
        binding.rflStreetName.setOnClickListener(this)
        binding.rflSubmit.setOnClickListener(this)
        binding.tvPic1.setOnClickListener(this)
        binding.tvPic2.setOnClickListener(this)
        binding.rivPic1.setOnClickListener(this)
        binding.rivPic2.setOnClickListener(this)
        binding.root.setOnClickListener(this)
        binding.layoutToolbar.toolbar.setOnClickListener(this)
    }

    override fun initData() {
        streetList = RealmUtil.instance?.findCheckedStreetList() as MutableList<Street>
        if (streetNo.isNotEmpty()) {
            for (i in streetList) {
                if (i.streetNo == streetNo) {
                    currentStreet = i
                }
            }
        } else {
            currentStreet = RealmUtil.instance?.findCurrentStreet()
            streetNo = currentStreet!!.streetNo
        }
        binding.tvStreetName.text = currentStreet?.streetName
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

    @SuppressLint("CheckResult")
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

            R.id.cb_streetName -> {
                showAbnormalStreetListDialog()
            }

            R.id.rfl_streetName -> {
                binding.cbStreetName.isChecked = true
                showAbnormalStreetListDialog()
            }

            R.id.rfl_submit -> {
                submit()
            }

            R.id.fl_color -> {
                checkedColor = v.tag as Int
                collectionPlateColorAdapter?.updateColor(checkedColor, collectioPlateColorList.indexOf(checkedColor))
            }

            R.id.toolbar,
            binding.root.id -> {
            }

            R.id.tv_pic1,
            R.id.riv_pic1 -> {
                var rxPermissions = RxPermissions(this@CollectionManagementActivity)
                rxPermissions.request(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ).subscribe {
                    if (it) {
                        selectPicDialog = null
                        if (selectPicDialog == null) {
                            selectPicDialog = SelectPicDialog(object : SelectPicDialog.Callback {
                                override fun onTakePhoto() {
                                    currentPic = 1
                                    takePhoto()
                                }

                                override fun onPickPhoto() {
                                    currentPic = 1
                                    selectPhoto()
                                }
                            })
                        }
                        selectPicDialog?.show()
                    }
                }
            }

            R.id.tv_pic2,
            R.id.riv_pic2 -> {
                var rxPermissions = RxPermissions(this@CollectionManagementActivity)
                rxPermissions.request(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ).subscribe {
                    if (it) {
                        selectPicDialog = null
                        if (selectPicDialog == null) {
                            selectPicDialog = SelectPicDialog(object : SelectPicDialog.Callback {
                                override fun onTakePhoto() {
                                    currentPic = 2
                                    takePhoto()
                                }

                                override fun onPickPhoto() {
                                    currentPic = 2
                                    selectPhoto()
                                }
                            })
                        }
                        selectPicDialog?.show()
                    }
                }
            }
        }
    }

    fun showAbnormalStreetListDialog() {
        abnormalStreetListDialog =
            AbnormalStreetListDialog(streetList, currentStreet!!, object : AbnormalStreetListDialog.AbnormalStreetCallBack {
                override fun chooseStreet(street: Street) {
                    currentStreet = street
                    streetNo = currentStreet!!.streetNo
                    binding.tvStreetName.text = street.streetName.toString()
                }
            })
        abnormalStreetListDialog?.show()
        abnormalStreetListDialog?.setOnDismissListener {
            binding.cbStreetName.isChecked = false
        }
    }

    fun takePhoto() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        takePictureLauncher.launch(takePictureIntent)
    }

    fun selectPhoto() {
        selectImageLauncher.launch("image/*")
    }

    val takePictureLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageBitmap = result.data?.extras?.get("data") as Bitmap
            when (currentPic) {
                1 -> {
                    binding.rivPic1.show()
                    GlideUtils.instance?.loadImage(binding.rivPic1, imageBitmap)
                    val file = ImageUtils.save2Album(imageBitmap, Bitmap.CompressFormat.JPEG)
                    val bytes = file?.readBytes()
                    pic1Base64 = EncodeUtils.base64Encode2String(bytes)
                }

                2 -> {
                    binding.rivPic2.show()
                    GlideUtils.instance?.loadImage(binding.rivPic2, imageBitmap)
                    val file = ImageUtils.save2Album(imageBitmap, Bitmap.CompressFormat.JPEG)
                    val bytes = file?.readBytes()
                    pic2Base64 = EncodeUtils.base64Encode2String(bytes)
                }
            }
        }
    }

    val selectImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            when (currentPic) {
                1 -> {
                    binding.rivPic1.show()
                    val file = UriUtils.uri2File(it)
                    GlideUtils.instance?.loadImageFile(binding.rivPic1, file)
                    val bytes = file?.readBytes()
                    pic1Base64 = EncodeUtils.base64Encode2String(bytes)
                }

                2 -> {
                    binding.rivPic2.show()
                    val file = UriUtils.uri2File(it)
                    GlideUtils.instance?.loadImageFile(binding.rivPic2, file)
                    val bytes = file?.readBytes()
                    pic2Base64 = EncodeUtils.base64Encode2String(bytes)
                }
            }
        }
    }

    fun submit() {
        showProgressDialog(20000)
        runBlocking {
            val token = PreferencesDataStore(BaseApplication.instance()).getString(PreferencesKeys.token)
            val param = HashMap<String, Any>()
            val jsonobject = JSONObject()
            jsonobject["token"] = token
            jsonobject["streetNo"] = streetNo
            jsonobject["photo1"] = pic1Base64
            jsonobject["photo2"] = pic2Base64
            jsonobject["photoType"] = "jpg"
            param["attr"] = jsonobject
            mViewModel.callSubmit(param)
        }
    }

    override fun startObserve() {
        super.startObserve()
        mViewModel.apply {
            callSubmitLiveData.observe(this@CollectionManagementActivity) {
                dismissProgressDialog()
            }
            errMsg.observe(this@CollectionManagementActivity) {
                dismissProgressDialog()
                ToastUtil.showToast(it.msg)
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