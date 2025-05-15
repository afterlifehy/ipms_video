package com.rt.ipms_video.mvvm.viewmodel

import com.rt.base.base.mvvm.BaseViewModel
import com.rt.base.base.mvvm.repository.OrderRepository

class UrgeListViewModel: BaseViewModel() {

    val mOrderRepository by lazy {
        OrderRepository()
    }
}