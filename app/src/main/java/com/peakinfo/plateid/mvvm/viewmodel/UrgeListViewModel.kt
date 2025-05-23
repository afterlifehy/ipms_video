package com.peakinfo.plateid.mvvm.viewmodel

import com.peakinfo.base.base.mvvm.BaseViewModel
import com.peakinfo.base.base.mvvm.repository.OrderRepository

class UrgeListViewModel: BaseViewModel() {

    val mOrderRepository by lazy {
        OrderRepository()
    }
}