package ja.insepector.bxapp.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import ja.insepector.base.base.mvvm.BaseViewModel
import ja.insepector.base.base.mvvm.ErrorMessage
import ja.insepector.base.bean.FeeRateResultBean
import ja.insepector.bxapp.mvvm.repository.MineRepository
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