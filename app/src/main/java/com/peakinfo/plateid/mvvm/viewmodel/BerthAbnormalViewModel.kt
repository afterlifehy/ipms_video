package com.peakinfo.plateid.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import com.peakinfo.base.base.mvvm.BaseViewModel
import com.peakinfo.base.base.mvvm.ErrorMessage
import com.peakinfo.base.bean.AbnormalReportResultBean
import com.peakinfo.plateid.mvvm.repository.AbnormalRepository
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