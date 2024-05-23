package com.peakinfo.plateid.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import com.peakinfo.base.base.mvvm.BaseViewModel
import com.peakinfo.base.base.mvvm.ErrorMessage
import com.peakinfo.base.bean.UpdateBean
import com.peakinfo.plateid.mvvm.repository.MineRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainViewModel : BaseViewModel() {
    val mMineRepository by lazy {
        MineRepository()
    }

    val login2LiveData = MutableLiveData<Any>()

    fun login2(param: Map<String, Any?>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mMineRepository.login2(param)
            }
            executeResponse(response, {
                login2LiveData.value = response.attr
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.msg, code = response.status))
            })
        }
    }
}