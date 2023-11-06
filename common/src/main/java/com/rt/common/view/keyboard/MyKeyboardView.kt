package com.rt.common.view.keyboard

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.inputmethodservice.KeyboardView
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.blankj.utilcode.util.SizeUtils
import com.rt.base.BaseApplication
import com.rt.base.ext.i18n
import com.rt.common.R


class MyKeyboardView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : KeyboardView(context, attrs) {

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val keyboard = keyboard ?: return
        val keys = keyboard.keys
        if (keys != null && keys.size > 0) {
            val paint = Paint()
            paint.color = ContextCompat.getColor(BaseApplication.baseApplication, com.rt.base.R.color.color_ff0371f4)
            paint.textAlign = Paint.Align.CENTER
            val font: Typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
            paint.setTypeface(font)
            paint.isAntiAlias = true


            //此处进行特殊按键的背景色处理
            for (key in keys) {
                if (key.codes[0] == -2) {
                    val rect = RectF(
                        key.x.toFloat(), key.y.toFloat() + SizeUtils.dp2px(8f), (
                                key.x + key.width).toFloat(), key.y + key.height + SizeUtils.dp2px(8f).toFloat()
                    )
                    canvas.drawRoundRect(
                        rect,
                        SizeUtils.dp2px(4f).toFloat(),
                        SizeUtils.dp2px(4f).toFloat(),
                        paint
                    )

                    paint.color = getContext().getResources().getColor(R.color.white);
                    paint.textSize = SizeUtils.dp2px(15f).toFloat()
                    canvas.drawText(
                        i18n(com.rt.base.R.string.其他),
                        key.x.toFloat() + key.width / 2,
                        key.y.toFloat() + key.height / 2 + key.height / 4,
                        paint
                    )

                }

                if (key.codes[0] == -1) {
                    val rect = RectF(
                        key.x.toFloat(), key.y.toFloat() + SizeUtils.dp2px(8f), (
                                key.x + key.width).toFloat(), key.y + key.height + SizeUtils.dp2px(8f).toFloat()
                    )
                    canvas.drawRoundRect(
                        rect,
                        SizeUtils.dp2px(4f).toFloat(),
                        SizeUtils.dp2px(4f).toFloat(),
                        paint
                    )

                    paint.color = ContextCompat.getColor(BaseApplication.baseApplication, com.rt.base.R.color.white)
                    paint.textSize = SizeUtils.dp2px(15f).toFloat()
                    canvas.drawText(
                        i18n(com.rt.base.R.string.返回),
                        key.x.toFloat() + key.width / 2,
                        key.y.toFloat() + key.height / 2 + key.height / 4,
                        paint
                    )
                }


                if (key.codes[0] == -3) {
                    val paint = Paint()
                    paint.color = ContextCompat.getColor(BaseApplication.baseApplication, com.rt.base.R.color.color_ff0371f4)

                    val rect = RectF(
                        key.x.toFloat(), key.y.toFloat() + SizeUtils.dp2px(8f), (key.x + key.width).toFloat(),
                        key.y + key.height + SizeUtils.dp2px(8f).toFloat()
                    )
                    canvas.drawRoundRect(
                        rect,
                        SizeUtils.dp2px(4f).toFloat(),
                        SizeUtils.dp2px(4f).toFloat(),
                        paint
                    )

                    val dr = ContextCompat.getDrawable(BaseApplication.instance(), com.rt.common.R.mipmap.ic_keyboard_delete) as Drawable
                    dr.setBounds(
                        key.x + key.width / 4,
                        key.y + key.height / 3 + SizeUtils.dp2px(5f),
                        key.x + key.width / 4 + key.width / 4 + key.width / 4,
                        key.y + key.height - SizeUtils.dp2px(8f)
                    )
                    dr.draw(canvas)
                }

            }
        }
    }

}