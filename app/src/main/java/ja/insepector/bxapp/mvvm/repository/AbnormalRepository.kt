package ja.insepector.bxapp.mvvm.repository

import ja.insepector.base.base.mvvm.BaseRepository
import ja.insepector.base.bean.AbnormalReportResultBean
import ja.insepector.base.bean.HttpWrapper

class AbnormalRepository : BaseRepository() {
    /**
     * 异常上报
     */
    suspend fun abnormalReport(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<AbnormalReportResultBean> {
        return mServer.abnormalReport(param)
    }
}