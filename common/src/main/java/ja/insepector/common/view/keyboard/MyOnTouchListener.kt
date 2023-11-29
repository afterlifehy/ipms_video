package ja.insepector.common.view.keyboard

import android.view.MotionEvent
import android.view.View
import android.widget.EditText

class MyOnTouchListener(
    private val isNumber: Boolean,
    private val editText: EditText,
    private val keyboardUtil: KeyboardUtil
) : View.OnTouchListener {
    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        editText.requestFocus()

        showKeyboard()

        // 切换键盘
//        keyboardUtil.hideSoftInputMethod(v as EditText)
        keyboardUtil.changeKeyboard(isNumber)
        keyboardUtil.setEditText(v as EditText)

        return true
    }

    fun showKeyboard() {
        keyboardUtil.showKeyboard(show = {

        }, hide = {

        })
    }
}