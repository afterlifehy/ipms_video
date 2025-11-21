package com.peakinfo.plateid.mvvm.viewmodel

import android.os.Environment
import androidx.lifecycle.MutableLiveData
import com.peakinfo.base.BaseApplication
import com.peakinfo.base.base.mvvm.BaseViewModel
import com.peakinfo.base.base.mvvm.ErrorMessage
import com.peakinfo.base.base.mvvm.repository.MineRepository
import com.peakinfo.base.util.Constant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import retrofit2.http.Part
import java.io.File

class LogViewModel : BaseViewModel() {
    val mMineRepository by lazy {
        MineRepository()
    }

    val logFileListLiveData = MutableLiveData<MutableList<File>>()
    val upLoadLogLiveData = MutableLiveData<Any>()

    fun logFileList() {
        val logFileList: MutableList<File> = ArrayList()
        val logDir = File(BaseApplication.instance().getExternalFilesDir(null), Constant.LOG_DIR_NAME)
        if (logDir.exists() && logDir.isDirectory) {
            val files = logDir.listFiles()
            if (files != null && files.isNotEmpty()) {
                logFileList.addAll(files)
                logFileListLiveData.value = logFileList
            } else {
            }
        } else {
        }
    }

    fun logFileUpload(@Part file: MultipartBody.Part) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mMineRepository.logFileUpload(file)
            }
            executeResponse(response, {
                upLoadLogLiveData.value = response.attr
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.msg, code = response.status, api = "logFileUpload"))
            })
        }
    }
}