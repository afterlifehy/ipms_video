package com.rt.ipms_video.ca.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.provider.MediaStore
import android.view.View
import android.view.View.OnClickListener
import android.view.WindowManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import com.alibaba.android.arouter.facade.annotation.Route
import com.blankj.utilcode.util.EncodeUtils
import com.blankj.utilcode.util.ImageUtils
import com.blankj.utilcode.util.UriUtils
import com.rt.base.BaseApplication
import com.rt.base.arouter.ARouterMap
import com.rt.base.bean.Street
import com.rt.base.bean.ca.UrgeBean
import com.rt.base.ds.PreferencesDataStore
import com.rt.base.ds.PreferencesKeys
import com.rt.base.ext.gone
import com.rt.base.ext.show
import com.rt.base.util.ToastUtil
import com.rt.base.viewbase.VbBaseActivity
import com.rt.common.realm.RealmUtil
import com.rt.common.util.Constant
import com.rt.common.util.GlideUtils
import com.rt.ipms_video.R
import com.rt.ipms_video.databinding.ActivityCaCollectionManagementBinding
import com.rt.ipms_video.dialog.SelectPicDialog
import com.rt.ipms_video.mvvm.viewmodel.CollectionManagementViewModel
import com.tbruyelle.rxpermissions3.RxPermissions
import kotlinx.coroutines.runBlocking

@Route(path = ARouterMap.CA_COLLECTION_MANAGEMENT)
class CACollectionManagementActivity : VbBaseActivity<CollectionManagementViewModel, ActivityCaCollectionManagementBinding>(),
    OnClickListener {
    var streetList: MutableList<Street> = ArrayList()
    var streetNo = ""
    var currentStreet: Street? = null
    var selectPicDialog: SelectPicDialog? = null
    var currentPic = 1
    var pic1Base64 = ""
    var pic2Base64 = ""
    lateinit var urgeBean: UrgeBean
    val plateMap = mutableMapOf(
        Constant.BLUE to com.rt.common.R.mipmap.ic_plate_blue,
        Constant.GREEN to com.rt.common.R.mipmap.ic_plate_green,
        Constant.YELLOW to com.rt.common.R.mipmap.ic_plate_yellow,
        Constant.YELLOW_GREEN to com.rt.common.R.mipmap.ic_plate_yellow_green,
        Constant.WHITE to com.rt.common.R.mipmap.ic_plate_white,
        Constant.BLACK to com.rt.common.R.mipmap.ic_plate_black
    )

    override fun initView() {
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        GlideUtils.instance?.loadImage(binding.layoutToolbar.ivBack, com.rt.common.R.mipmap.ic_back_white)
        binding.layoutToolbar.tvTitle.text = "催缴单催缴"
        binding.layoutToolbar.tvTitle.setTextColor(ContextCompat.getColor(BaseApplication.instance(), com.rt.base.R.color.white))

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
                var rxPermissions = RxPermissions(this@CACollectionManagementActivity)
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
        showProgressDialog(60000)
        runBlocking {
            val token = PreferencesDataStore(BaseApplication.instance()).getString(PreferencesKeys.token)
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
    }

    override fun startObserve() {
        super.startObserve()
        mViewModel.apply {
            urgepayLiveData.observe(this@CACollectionManagementActivity) {
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