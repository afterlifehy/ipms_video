package com.rt.ipms_video.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import com.rt.base.base.mvvm.BaseViewModel
import com.rt.base.base.mvvm.ErrorMessage
import com.rt.base.bean.UpdateBean
import com.rt.ipms_video.mvvm.repository.MineRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MineViewModel : BaseViewModel() {

    val mMineRepository by lazy {
        MineRepository()
    }

    val checkUpdateLiveDate = MutableLiveData<UpdateBean>()
    val logoutLiveData = MutableLiveData<Any>()

    fun checkUpdate(param: Map<String, Any?>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mMineRepository.checkUpdate(param)
            }
            executeResponse(response, {
                checkUpdateLiveDate.value = response.attr
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.msg, code = response.status))
            })
        }
    }

    fun logout(param: Map<String, Any?>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mMineRepository.logout(param)
            }
            executeResponse(response, {
                logoutLiveData.value = response.attr
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.msg, code = response.status))
            })
        }
    }
}