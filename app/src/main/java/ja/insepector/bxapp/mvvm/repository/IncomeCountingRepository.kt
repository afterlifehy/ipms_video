package ja.insepector.bxapp.mvvm.repository

import ja.insepector.base.base.mvvm.BaseRepository
import ja.insepector.base.bean.HttpWrapper
import ja.insepector.base.bean.IncomeCountingBean

class IncomeCountingRepository : BaseRepository() {

    /**
     * 营收盘点
     */
    suspend fun incomeCounting(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<IncomeCountingBean> {
        return mServer.incomeCounting(param)
    }
}