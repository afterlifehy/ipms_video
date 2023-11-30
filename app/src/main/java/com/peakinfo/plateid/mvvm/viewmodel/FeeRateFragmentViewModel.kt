package com.peakinfo.plateid.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import com.peakinfo.base.base.mvvm.BaseViewModel
import com.peakinfo.base.base.mvvm.ErrorMessage
import com.peakinfo.base.bean.FeeRateResultBean
import com.peakinfo.plateid.mvvm.repository.MineRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FeeRateFragmentViewModel : BaseViewModel() {

    val mMineRepository by lazy {
        MineRepository()
    }
    val feeRateLiveData = MutableLiveData<FeeRateResultBean>()

    fun feeRate(param: Map<String, Any?>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mMineRepository.feeRate(param)
            }
            executeResponse(response, {
                feeRateLiveData.value = response.attr
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.msg, code = response.status))
            })
        }
    }
}