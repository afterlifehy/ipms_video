package ja.insepector.base.viewbase.inter

interface OnNetWorkViewShowLinsener {
    /**
     * 当前是否有网络
     */
    fun onCurrentNewWorkState(isNetWork: Boolean)
}