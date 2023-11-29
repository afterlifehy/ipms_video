package ja.insepector.base.ext

import ja.insepector.base.BaseApplication


fun Any.i18n(res: Int): String {
    return BaseApplication.instance().resources.getString(res)
}
