package com.peakinfo.plateid.ca.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.view.View
import android.view.View.OnClickListener
import android.view.WindowManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.viewbinding.ViewBinding
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.fastjson.JSONObject
import com.blankj.utilcode.util.EncodeUtils
import com.peakinfo.base.BaseApplication
import com.peakinfo.base.arouter.ARouterMap
import com.peakinfo.base.bean.Street
import com.peakinfo.base.bean.ca.UrgeBean
import com.peakinfo.base.ds.PreferencesDataStore
import com.peakinfo.base.ds.PreferencesKeys
import com.peakinfo.base.ext.gone
import com.peakinfo.base.ext.show
import com.peakinfo.base.util.ToastUtil
import com.peakinfo.base.viewbase.VbBaseActivity
import com.peakinfo.common.realm.RealmUtil
import com.peakinfo.common.util.CompressUtil
import com.peakinfo.common.util.Constant
import com.peakinfo.common.util.FileUtil
import com.peakinfo.common.util.GlideUtils
import com.peakinfo.plateid.R
import com.peakinfo.plateid.databinding.ActivityCaCollectionManagementBinding
import com.peakinfo.plateid.mvvm.viewmodel.CollectionManagementViewModel
import com.tbruyelle.rxpermissions3.RxPermissions
import kotlinx.coroutines.runBlocking
import java.io.InputStream

@Route(path = ARouterMap.CA_COLLECTION_MANAGEMENT)
class CACollectionManagementActivity : VbBaseActivity<CollectionManagementViewModel, ActivityCaCollectionManagementBinding>(),
    OnClickListener {
    var streetNo = ""
    var currentStreet: Street? = null
    var currentPic = 1
    var pic1Base64 = ""
    var pic2Base64 = ""
    var pic1Url = ""
    var pic2Url = ""
    var imageType = 0
    var loginName = ""
    var token = ""
    lateinit var urgeBean: UrgeBean
    val plateMap = mutableMapOf(
        Constant.BLUE to com.peakinfo.common.R.mipmap.ic_plate_blue,
        Constant.GREEN to com.peakinfo.common.R.mipmap.ic_plate_green,
        Constant.YELLOW to com.peakinfo.common.R.mipmap.ic_plate_yellow,
        Constant.YELLOW_GREEN to com.peakinfo.common.R.mipmap.ic_plate_yellow_green,
        Constant.WHITE to com.peakinfo.common.R.mipmap.ic_plate_white,
        Constant.BLACK to com.peakinfo.common.R.mipmap.ic_plate_black
    )

    override fun initView() {
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        GlideUtils.instance?.loadImage(binding.layoutToolbar.ivBack, com.peakinfo.common.R.mipmap.ic_back_white)
        binding.layoutToolbar.tvTitle.text = "催缴记录上传"
        binding.layoutToolbar.tvTitle.setTextColor(ContextCompat.getColor(BaseApplication.instance(), com.peakinfo.base.R.color.white))

        urgeBean = intent.getParcelableExtra(ARouterMap.URGE)!!
        binding.tvPlate.setText(urgeBean.plateId)
        if (urgeBean.plateColor == 33) {
            binding.flLin.show()
        } else {
            binding.flLin.gone()
            GlideUtils.instance?.loadImage(binding.ivPlateColor, plateMap[urgeBean.plateColor.toString()]!!)
        }
    }

    override fun initListener() {
        binding.layoutToolbar.flBack.setOnClickListener(this)
        binding.rflSubmit.setOnClickListener(this)
        binding.tvPic1.setOnClickListener(this)
        binding.tvPic2.setOnClickListener(this)
        binding.rivPic1.setOnClickListener(this)
        binding.rivPic2.setOnClickListener(this)
    }

    override fun initData() {
        runBlocking {
            token = PreferencesDataStore(BaseApplication.instance()).getString(PreferencesKeys.token)
            loginName = PreferencesDataStore(BaseApplication.instance()).getString(PreferencesKeys.account)
        }
        currentStreet = RealmUtil.instance?.findCurrentStreet()
        streetNo = currentStreet!!.streetNo
        binding.tvStreetName.text = currentStreet?.streetName
    }

    @SuppressLint("CheckResult")
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fl_back -> {
                onBackPressedSupport()
            }

            R.id.rfl_submit -> {
                submit()
            }

            R.id.toolbar,
            binding.root.id -> {
            }

            R.id.tv_pic1,
            R.id.riv_pic1 -> {
                var rxPermissions = RxPermissions(this@CACollectionManagementActivity)
                rxPermissions.request(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ).subscribe {
                    if (it) {
                        currentPic = 1
                        takePhoto()
                    }
                }
            }

            R.id.tv_pic2,
            R.id.riv_pic2 -> {
                var rxPermissions = RxPermissions(this@CACollectionManagementActivity)
                rxPermissions.request(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ).subscribe {
                    if (it) {
                        currentPic = 2
                        takePhoto()
                    }
                }
            }
        }
    }

    private var photoUri: Uri? = null

    fun takePhoto() {
        // 创建存放原图的文件
        val photoFile = FileUtil.createImageFile(this@CACollectionManagementActivity)
        photoUri = FileProvider.getUriForFile(
            this@CACollectionManagementActivity,
            // 这里替换成你AndroidManifest里配置的FileProvider authority
            "com.peakinfo.plateid.fileprovider",
            photoFile
        )
        if (currentPic == 1) {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            }
            takePictureLauncher1.launch(takePictureIntent)
        } else {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            }
            takePictureLauncher2.launch(takePictureIntent)
        }
    }

    val takePictureLauncher1 = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            photoUri?.let { uri ->
                // 从uri读取原图bitmap（这里才是高清原图）
                val sourceBitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(uri))
                // 压缩到目标尺寸1440*1920
                val compressedBytes = CompressUtil.compressBitmapToTargetSize(sourceBitmap, 1440, 1920)
                val previewBmp = BitmapFactory.decodeByteArray(compressedBytes, 0, compressedBytes!!.size)
                binding.rivPic1.show()
                GlideUtils.instance?.loadImage(binding.rivPic1, previewBmp)
                val file = FileUtil.FileSaveToInside(
                    this@CACollectionManagementActivity,
                    "${urgeBean.urgePayId}_${System.currentTimeMillis()}_1.jpg",
                    compressedBytes
                )
                val bytes = file?.readBytes()
                pic1Base64 = EncodeUtils.base64Encode2String(bytes)
            }
        }
    }

    val takePictureLauncher2 = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            photoUri?.let { uri ->
                // 从uri读取原图bitmap（这里才是高清原图）
                val sourceBitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(uri))
                // 压缩到目标尺寸1440*1920
                val compressedBytes = CompressUtil.compressBitmapToTargetSize(sourceBitmap, 1440, 1920)
                val previewBmp = BitmapFactory.decodeByteArray(compressedBytes, 0, compressedBytes!!.size)
                binding.rivPic2.show()
                GlideUtils.instance?.loadImage(binding.rivPic2, previewBmp)
                val file = FileUtil.FileSaveToInside(
                    this@CACollectionManagementActivity,
                    "${urgeBean.urgePayId}_${System.currentTimeMillis()}_2.jpg",
                    compressedBytes
                )
                val bytes = file?.readBytes()
                pic2Base64 = EncodeUtils.base64Encode2String(bytes)
            }
        }
    }

    fun submit() {
        showProgressDialog(60000)
        val param = HashMap<String, Any>()
        param["token"] = token
        param["urgePayId"] = urgeBean.urgePayId
        param["roadId"] = streetNo
        param["photo1"] = pic1Base64
        param["photo2"] = pic2Base64
        param["photoType"] = "jpg"
        param["dataTime"] = System.currentTimeMillis()
        mViewModel.urgepay(param)
    }

    override fun startObserve() {
        super.startObserve()
        mViewModel.apply {
            urgepayLiveData.observe(this@CACollectionManagementActivity) {
                imageType = 1
                val params = HashMap<String, Any>()
                params["fileName"] = "${urgeBean.urgePayId}_${System.currentTimeMillis()}_1.jpg"
                params["file"] = pic1Base64
                mViewModel.uploadFileForApp(params)
            }
            uploadFileForAppLiveData.observe(this@CACollectionManagementActivity) {
                if (imageType == 1) {
                    imageType = 2
                    pic1Url = it.URL
                    val params = HashMap<String, Any>()
                    params["fileName"] = "${urgeBean.urgePayId}_${System.currentTimeMillis()}_2.jpg"
                    params["file"] = pic2Base64
                    mViewModel.uploadFileForApp(params)
                } else {
                    pic2Url = it.URL
                    urgeNotice()
                }
            }
            urgeNoticeLiveData.observe(this@CACollectionManagementActivity) {
                dismissProgressDialog()
                ToastUtil.showBottomToast("催缴成功")
                onBackPressedSupport()
            }
            errMsg.observe(this@CACollectionManagementActivity) {
                dismissProgressDialog()
                ToastUtil.showBottomToast(it.msg)
            }
            mException.observe(this@CACollectionManagementActivity) {
                dismissProgressDialog()
            }
        }
    }

    fun urgeNotice() {
        val param = HashMap<String, Any>()
        val jsonObject = JSONObject()
        jsonObject["urgePayId"] = urgeBean.urgePayId
        jsonObject["roadId"] = streetNo
        jsonObject["photo1"] = pic1Url
        jsonObject["photo2"] = pic2Url
        jsonObject["photoType"] = "jpg"
        jsonObject["dataTime"] = System.currentTimeMillis()
        jsonObject["inputter"] = loginName
        param["attr"] = jsonObject
        mViewModel.urgeNotice(param)
    }

    override fun getVbBindingView(): ViewBinding {
        return ActivityCaCollectionManagementBinding.inflate(layoutInflater)
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