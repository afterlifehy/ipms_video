package com.peakinfo.plateid.ui.activity.mine

import android.view.View
import android.view.View.OnClickListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import com.alibaba.android.arouter.facade.annotation.Route
import com.peakinfo.base.arouter.ARouterMap
import com.peakinfo.base.util.ToastUtil
import com.peakinfo.base.viewbase.VbBaseActivity
import com.peakinfo.plateid.R
import com.peakinfo.plateid.adapter.LogAdapter
import com.peakinfo.plateid.databinding.ActivityLogUpdateBinding
import com.peakinfo.plateid.dialog.ConfirmDialog
import com.peakinfo.plateid.mvvm.viewmodel.LogViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

@Route(path = ARouterMap.LOG_UPLOAD)
class LogUploadActivity : VbBaseActivity<LogViewModel, ActivityLogUpdateBinding>(), OnClickListener {
    lateinit var logAdapter: LogAdapter
    var logFileList: MutableList<File> = ArrayList()
    var logFileCheckedList: MutableList<File> = ArrayList()
    lateinit var confirmDialog: ConfirmDialog
    var position = 0

    override fun initView() {
        binding.layoutToolbar.tvTitle.text = "日志上传"
        logAdapter = LogAdapter(logFileList, logFileCheckedList) { file, isChecked ->
            if (isChecked) {
                logFileCheckedList.clear()
                logFileCheckedList.add(file)
            } else {
                logFileCheckedList.remove(file)
            }
            logAdapter.notifyDataSetChanged()
        }
        binding.rvLog.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@LogUploadActivity)
            adapter = logAdapter
        }
    }

    override fun initListener() {
        binding.layoutToolbar.flBack.setOnClickListener(this)
        binding.rtvUpload.setOnClickListener(this)
    }

    override fun initData() {
        mViewModel.logFileList()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fl_back -> {
                onBackPressedSupport()
            }

            R.id.rtv_upload -> {
                if (logFileCheckedList.size > 0) {
                    if (!::confirmDialog.isInitialized) {
                        confirmDialog = ConfirmDialog("确认上传", {}, {
                            showProgressDialog(300000)
                            binding.rtvUpload.setOnClickListener(null)
                            binding.layoutToolbar.flBack.setOnClickListener(null)
                            mViewModel.logFileUpload(prepareFilePart("file", logFileCheckedList[position]))
                        })
                    }
                    confirmDialog.show()
                } else {
                    ToastUtil.showBottomToast("请选择日志", 2)
                }
            }
        }
    }

    override fun startObserve() {
        super.startObserve()
        mViewModel.apply {
            logFileListLiveData.observe(this@LogUploadActivity) {
                logAdapter.setList(it)
                logAdapter.notifyDataSetChanged()
            }
            upLoadLogLiveData.observe(this@LogUploadActivity) {
                if (position < logFileCheckedList.size - 1) {
                    position++
                    mViewModel.logFileUpload(prepareFilePart("file", logFileCheckedList[position]))
                } else {
                    dismissProgressDialog()
                    binding.rtvUpload.setOnClickListener(this@LogUploadActivity)
                    binding.layoutToolbar.flBack.setOnClickListener(this@LogUploadActivity)
                    ToastUtil.showBottomToast("上传完成", 1)
                    position = 0
                }
            }
            errMsg.observe(this@LogUploadActivity) {
                ToastUtil.showBottomToast(it.msg, 0)
                if (it.api == "upLoadLog") {
                    if (position < logFileCheckedList.size) {
                        position++
                        mViewModel.logFileUpload(prepareFilePart("file", logFileCheckedList[position]))
                    } else {
                        binding.rtvUpload.setOnClickListener(this@LogUploadActivity)
                        binding.layoutToolbar.flBack.setOnClickListener(this@LogUploadActivity)
                        ToastUtil.showBottomToast("上传完成")
                        position = 0
                    }
                }
            }
            mException.observe(this@LogUploadActivity) {
            }
        }
    }

    fun prepareFilePart(partName: String, file: File): MultipartBody.Part {
        // 创建 RequestBody 实例，指定文件类型
        val requestBody = file.asRequestBody("text/plain".toMediaTypeOrNull())
        // 使用 MultipartBody.Part 封装文件
        return MultipartBody.Part.createFormData(partName, file.name, requestBody)
    }

    override fun getVbBindingView(): ViewBinding {
        return ActivityLogUpdateBinding.inflate(layoutInflater)
    }

    override val isFullScreen: Boolean
        get() = true

    override fun marginStatusBarView(): View {
        return binding.layoutToolbar.toolbar
    }

    override fun onReloadData() {
    }

    override fun providerVMClass(): Class<LogViewModel> {
        return LogViewModel::class.java
    }
}

