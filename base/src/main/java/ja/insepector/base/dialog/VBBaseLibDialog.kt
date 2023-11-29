package ja.insepector.base.dialog

import android.content.Context
import android.view.View
import androidx.viewbinding.ViewBinding
import ja.insepector.base.R

/**
 * Created by huy  on 2022/8/13.
 */
abstract class VBBaseLibDialog<vb : ViewBinding>(context: Context, themeResId: Int) :
    ja.insepector.base.dialog.BaseLibDialog(context, themeResId) {

    constructor(context: Context) : this(context, R.style.alertdialog)

    lateinit var binding: vb

    abstract fun getVbBindingView(): ViewBinding?

    override fun getBindingView(): View {
        val mBindind = getVbBindingView()
        binding = mBindind as vb
        return mBindind!!.root
    }

    override fun getLayoutResID(): Int {
        return 0
    }
}