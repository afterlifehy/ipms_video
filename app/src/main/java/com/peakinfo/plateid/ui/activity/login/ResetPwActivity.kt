package com.peakinfo.plateid.ui.activity.login

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import com.alibaba.android.arouter.facade.annotation.Route
import com.blankj.utilcode.util.ClickUtils
import com.peakinfo.base.BaseApplication
import com.peakinfo.base.arouter.ARouterMap
import com.peakinfo.base.util.ToastUtil
import com.peakinfo.base.viewbase.VbBaseActivity
import com.peakinfo.plateid.R
import com.peakinfo.plateid.databinding.ActivityResetPwBinding
import com.peakinfo.plateid.mvvm.viewmodel.ResetPwViewModel

@Route(path = ARouterMap.RESET_PW)
class ResetPwActivity : VbBaseActivity<ResetPwViewModel, ActivityResetPwBinding>(), View.OnClickListener {

    override fun initView() {
    }

    override fun initListener() {
        binding.layoutToolbar.flBack.setOnClickListener(this)
        binding.rtvReset.setOnClickListener(this)
        binding.etAccount.addTextChangedListener(textWatcher)
        binding.etOldPw.addTextChangedListener(textWatcher)
        binding.etNewPw.addTextChangedListener(textWatcher)
        binding.etRepeatPw.addTextChangedListener(textWatcher)
    }

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable?) {
            updateResetButton()  // 每次文本改变后更新按钮状态
        }
    }

    private fun updateResetButton() {
        val isAllFieldsFilled = binding.etAccount.text.isNotEmpty() &&
                binding.etOldPw.text.isNotEmpty() &&
                binding.etNewPw.text.isNotEmpty() &&
                binding.etRepeatPw.text.isNotEmpty()

        val color = if (isAllFieldsFilled) {
            com.peakinfo.base.R.color.color_ff0371f4
        } else {
            com.peakinfo.base.R.color.color_990371f4
        }

        binding.rtvReset.delegate.setBackgroundColor(
            ContextCompat.getColor(BaseApplication.instance(), color)
        )

        if (isAllFieldsFilled) {
            ClickUtils.applySingleDebouncing(binding.rtvReset, 3000, this@ResetPwActivity)
        } else {
            binding.rtvReset.setOnClickListener(null)
        }

        binding.rtvReset.delegate.init()
    }

    override fun initData() {
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fl_back -> {
                onBackPressedSupport()
            }

            R.id.rtv_reset -> {
                if (binding.etNewPw.text.toString() != binding.etRepeatPw.text.toString()) {
                    ToastUtil.showBottomToast("两次输入密码不相同")
                    return
                }
                onBackPressedSupport()
            }
        }
    }

    override fun startObserve() {
        super.startObserve()
        mViewModel.apply {

        }
    }

    override fun getVbBindingView(): ViewBinding {
        return ActivityResetPwBinding.inflate(layoutInflater)
    }

    override val isFullScreen: Boolean
        get() = true

    override fun providerVMClass(): Class<ResetPwViewModel> {
        return ResetPwViewModel::class.java
    }

    override fun marginStatusBarView(): View {
        return binding.layoutToolbar.ablToolbar
    }

    override fun onReloadData() {
    }
}