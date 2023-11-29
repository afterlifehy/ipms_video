package ja.insepector.base.base.mvvm

object UrlManager {
    const val DEV_HOST = "http://180.169.37.244/ipms/service/"
    const val FORMAL_HOST = "https://api.runba.xyz/"

    fun getServerUrl(): String {
        if (ja.insepector.base.BuildConfig.is_dev) {
            return DEV_HOST
        } else {
            return FORMAL_HOST
        }
    }
}