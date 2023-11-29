package ja.insepector.base.viewbase

import android.content.Context
import android.view.View
import ja.insepector.base.widget.PagerStatesView

interface BaseViewAddManagers : BaseCommentLinsener {

    fun getRootView(context: Context, mView: View): View

    fun getRootViewId(context: Context, contextId: Int): View

    fun setIsLoadNotData(isLoadNotData: Boolean)

    fun setIsShowTitle(isShowTitle: Boolean)

    fun setNetWorkStatesView(
        isShow: Boolean,
        mOnNotNetWorkClickLinsener: PagerStatesView.OnPagerClickLinsener? = null
    )
}