package com.rt.ipms_video.dialog

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.View.OnClickListener
import android.view.WindowManager
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import com.alibaba.android.arouter.launcher.ARouter
import com.rt.base.BaseApplication
import com.rt.base.arouter.ARouterMap
import com.rt.base.dialog.VBBaseLibDialog
import com.rt.base.help.ActivityCacheManager
import com.rt.common.view.keyboard.KeyboardUtil
import com.rt.common.view.keyboard.MyTextWatcher
import com.rt.ipms_video.R
import com.rt.ipms_video.adapter.CollectionPlateColorAdapter
import com.rt.ipms_video.databinding.DialogCollectionBinding


class CollectionDialog(val callback: CollecteCallBack) :
    VBBaseLibDialog<DialogCollectionBinding>(ActivityCacheManager.instance().getCurrentActivity()!!),
    OnClickListener {
    var collectionPlateColorAdapter: CollectionPlateColorAdapter? = null
    var collectioPlateColorList: MutableList<Int> = ArrayList()
    var checkedColor = 0
    private lateinit var keyboardUtil: KeyboardUtil
    val widthType = 2

    init {
        initView()
    }

    private fun initView() {
        collectioPlateColorList.add(0)
        collectioPlateColorList.add(1)
        collectioPlateColorList.add(2)
        collectioPlateColorList.add(3)
        collectioPlateColorList.add(4)
        collectioPlateColorList.add(5)
        collectioPlateColorList.add(6)
        binding.rvPlateColor.setHasFixedSize(true)
        binding.rvPlateColor.layoutManager = LinearLayoutManager(BaseApplication.instance(), LinearLayoutManager.HORIZONTAL, false)
        collectionPlateColorAdapter = CollectionPlateColorAdapter(widthType, collectioPlateColorList, this)
        binding.rvPlateColor.adapter = collectionPlateColorAdapter

        binding.rflRecognize.setOnClickListener(this)
        binding.rtvCancel.setOnClickListener(this)
        binding.rtvOk.setOnClickListener(this)

        window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
// 其他dialog设置

        initKeyboard()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initKeyboard() {
        keyboardUtil = KeyboardUtil(binding.kvKeyBoard) {
            binding.retPlate.requestFocus()
            keyboardUtil.changeKeyboard(true)
            keyboardUtil.setEditText(binding.retPlate)
        }

        binding.retPlate.addTextChangedListener(MyTextWatcher(null, null, true, keyboardUtil))

        binding.retPlate.setOnTouchListener { v, p1 ->
            (v as EditText).requestFocus()
            keyboardUtil.showKeyboard(show = {
                val location = IntArray(2)
                v.getLocationOnScreen(location)
                val editTextPosY = location[1]

                val screenHeight = window!!.windowManager.defaultDisplay.height
                val distanceToBottom: Int = screenHeight - editTextPosY - v.getHeight()

                if (binding.kvKeyBoard.height > distanceToBottom) {
                    // 当键盘高度超过输入框到屏幕底部的距离时，向上移动布局
                    binding.rllCollect.translationY = (-(binding.kvKeyBoard.height - distanceToBottom)).toFloat()
                }
            })
            keyboardUtil.changeKeyboard(true)
            keyboardUtil.setEditText(v)
            true
        }
        binding.root.setOnClickListener {
            if (keyboardUtil.isShow()) {
                binding.rllCollect.translationY = 0f
                keyboardUtil.hideKeyboard()
            } else {
                dismiss()
            }
        }
        setOnKeyListener(object : DialogInterface.OnKeyListener {
            override fun onKey(p0: DialogInterface?, p1: Int, p2: KeyEvent?): Boolean {
                if (p1 == KeyEvent.KEYCODE_BACK && p2?.action == KeyEvent.ACTION_UP) {
                    if (keyboardUtil.isShow()) {
                        binding.rllCollect.translationY = 0f
                        keyboardUtil.hideKeyboard()
                        return true // 表明已处理返回键事件
                    }
                }
                return false
            }

        })
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.rfl_recognize -> {
                ARouter.getInstance().build(ARouterMap.SCAN_PLATE).navigation(ActivityCacheManager.instance().getCurrentActivity(), 2)
            }

            R.id.rtv_cancel -> {
                dismiss()
            }

            R.id.rtv_ok -> {
                callback.collect(binding.retPlate.text.toString(), checkedColor)
                dismiss()
            }

            R.id.fl_color -> {
                checkedColor = v.tag as Int
                collectionPlateColorAdapter?.updateColor(checkedColor, collectioPlateColorList.indexOf(checkedColor))
            }
        }
    }

    override fun getVbBindingView(): ViewBinding? {
        return DialogCollectionBinding.inflate(layoutInflater)
    }

    override fun getHideInput(): Boolean {
        return true
    }

    override fun getWidth(): Float {
        return WindowManager.LayoutParams.MATCH_PARENT.toFloat()
    }

    override fun getHeight(): Float {
        return WindowManager.LayoutParams.MATCH_PARENT.toFloat()
    }

    override fun getCanceledOnTouchOutside(): Boolean {
        return true
    }

    override fun getGravity(): Int {
        return Gravity.CENTER
    }

    fun setPlate(plateId: String) {
        binding.retPlate.setText(plateId)
    }

    interface CollecteCallBack {
        fun collect(plate: String, color: Int)
    }

}