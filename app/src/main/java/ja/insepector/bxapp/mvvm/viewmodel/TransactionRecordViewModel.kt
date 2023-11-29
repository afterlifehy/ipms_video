package ja.insepector.bxapp.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import ja.insepector.base.base.mvvm.BaseViewModel
import ja.insepector.base.base.mvvm.ErrorMessage
import ja.insepector.base.bean.NotificationBean
import ja.insepector.base.bean.TransactionResultBean
import ja.insepector.bxapp.mvvm.repository.OrderRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TransactionRecordViewModel: BaseViewModel() {

    val mOrderRepository by lazy {
        OrderRepository()
    }

    val transactionInquiryByOrderLiveData = MutableLiveData<TransactionResultBean>()
    val notificationInquiryLiveData = MutableLiveData<NotificationBean>()

    fun transactionInquiryByOrder(param: Map<String, Any?>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mOrderRepository.transactionInquiryByOrder(param)
            }
            executeResponse(response, {
                transactionInquiryByOrderLiveData.value = response.attr
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.msg, code = response.status))
            })
        }
    }

    fun notificationInquiry(param: Map<String, Any?>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mOrderRepository.notificationInquiry(param)
            }
            executeResponse(response, {
                notificationInquiryLiveData.value = response.attr
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.msg, code = response.status))
            })
        }
    }
}