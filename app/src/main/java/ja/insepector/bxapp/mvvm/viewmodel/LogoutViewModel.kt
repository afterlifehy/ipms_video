package ja.insepector.bxapp.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import ja.insepector.base.base.mvvm.BaseViewModel
import ja.insepector.base.base.mvvm.ErrorMessage
import ja.insepector.bxapp.mvvm.repository.LogoutRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LogoutViewModel : BaseViewModel() {
    val mLogoutRepository by lazy {
        LogoutRepository()
    }

    val logoutLiveData = MutableLiveData<Any>()

    fun logout(param: Map<String, Any?>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mLogoutRepository.logout(param)
            }
            executeResponse(response, {
                logoutLiveData.value = response.attr
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.msg, code = response.status))
            })
        }
    }
}