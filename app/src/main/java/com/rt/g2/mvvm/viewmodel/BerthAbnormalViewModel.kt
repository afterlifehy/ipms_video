package com.rt.g2.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import com.rt.base.base.mvvm.BaseViewModel
import com.rt.base.base.mvvm.ErrorMessage
import com.rt.base.bean.AbnormalReportResultBean
import com.rt.base.base.mvvm.repository.AbnormalRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BerthAbnormalViewModel: BaseViewModel() {
    val mAbnormalRepository by lazy {
        AbnormalRepository()
    }

    val abnormalReportLiveData = MutableLiveData<AbnormalReportResultBean>()

    fun abnormalReport(param: Map<String, Any?>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mAbnormalRepository.abnormalReport(param)
            }
            executeResponse(response, {
                abnormalReportLiveData.value = response.attr
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.msg, code = response.status))
            })
        }
    }
}