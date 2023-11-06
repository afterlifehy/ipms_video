package com.rt.ipms_video.ui.activity.order

import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.view.View.OnClickListener
import android.widget.EditText
import androidx.viewbinding.ViewBinding
import com.rt.base.viewbase.VbBaseActivity
import com.rt.common.util.GlideUtils
import com.rt.common.view.keyboard.KeyboardUtil
import com.rt.ipms_video.databinding.ActivityTransactionQueryBinding
import com.rt.ipms_video.mvvm.viewmodel.TransactionQueryViewModel

class TransactionQueryActivity : VbBaseActivity<TransactionQueryViewModel, ActivityTransactionQueryBinding>(), OnClickListener {
    private lateinit var keyboardUtil: KeyboardUtil

    override fun initView() {
        GlideUtils.instance?.loadImage(binding.layoutToolbar.ivBack, com.rt.common.R.mipmap.ic_back_white)
        initKeyboard()
    }

    private fun initKeyboard() {
        keyboardUtil = KeyboardUtil(binding.viewKeyboard) {
            binding.etSearch.requestFocus()
            keyboardUtil.changeKeyboard(true)
            keyboardUtil.setEditText(binding.etSearch)
        }

        binding.etSearch.addTextChangedListener(MyTextWatcher(null, null, true, keyboardUtil))

        binding.etSearch.setOnTouchListener(MyOnTouchListener(0, true, etList, keyboardUtil))

        mBinding.root.setOnClickListener {
            keyboardUtil.hideKeyboard()
        }

        beforeEt = etList[0]
    }

    override fun initListener() {

    }

    override fun initData() {
    }

    override fun onClick(v: View?) {
    }

    override fun getVbBindingView(): ViewBinding {
        return ActivityTransactionQueryBinding.inflate(layoutInflater)
    }

    override fun onReloadData() {
    }

    override val isFullScreen: Boolean
        get() = true

    override fun providerVMClass(): Class<TransactionQueryViewModel>? {
        return TransactionQueryViewModel::class.java
    }

    override fun marginStatusBarView(): View {
        return binding.layoutToolbar.ablToolbar
    }

    inner class MyOnTouchListener(
        private val index: Int,
        private val isNumber: Boolean,
        private val etList: List<EditText>,
        private val keyboardUtil: KeyboardUtil
    ) : View.OnTouchListener {
        override fun onTouch(v: View?, event: MotionEvent?): Boolean {
            if(index > 0){
                beforeEt =  etList[index-1]
            }

            etList[index].requestFocus()

            if(et == null || et == etList[index] || !keyboardUtil.isShow()) {
                showKeyboard()
                et = etList[index]
            }

            // 切换键盘
            keyboardUtil.hideSoftInputMethod(v as EditText)
            keyboardUtil.changeKeyboard(isNumber)
            keyboardUtil.setEditText(v)

            return false
        }
    }

    class MyTextWatcher(
        private val beforeEditText: EditText?, private val nextEditText: EditText?,
        private val isNumber: Boolean, private val keyboardUtil: KeyboardUtil
    ) : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

        }

        override fun afterTextChanged(s: Editable?) {
            s?.let {
                if (it.isNotEmpty()) {
                    nextEditText?.let {
                        it.requestFocus()
                        it.setSelection(it.text.length)
                        keyboardUtil.changeKeyboard(isNumber)
                        keyboardUtil.setEditText(nextEditText)
                    }
                } else {
                    beforeEditText?.let {
                        it?.requestFocus()
                        keyboardUtil.changeKeyboard(isNumber)
                        keyboardUtil.setEditText(it)
                    }
                }
            }
        }

    }
}