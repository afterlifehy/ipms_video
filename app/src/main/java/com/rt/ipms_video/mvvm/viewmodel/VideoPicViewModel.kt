package com.rt.ipms_video.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import com.rt.base.base.mvvm.BaseViewModel
import com.rt.base.base.mvvm.ErrorMessage
import com.rt.base.bean.VideoPicBean
import com.rt.ipms_video.mvvm.repository.OrderRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class VideoPicViewModel: BaseViewModel() {
    val mOrderRepository by lazy {
        OrderRepository()
    }

    val videoPicLiveData = MutableLiveData<VideoPicBean>()

    fun videoPic(param: Map<String, Any?>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mOrderRepository.videoPic(param)
            }
            executeResponse(response, {
                videoPicLiveData.value = response.attr
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.msg, code = response.status))
            })
        }
    }
}