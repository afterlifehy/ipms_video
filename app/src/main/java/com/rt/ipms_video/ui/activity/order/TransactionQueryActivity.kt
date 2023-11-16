package com.rt.ipms_video.ui.activity.order

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.View.OnClickListener
import android.widget.EditText
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.rt.base.BaseApplication
import com.rt.base.arouter.ARouterMap
import com.rt.base.ext.gone
import com.rt.base.ext.i18N
import com.rt.base.ext.show
import com.rt.base.util.ToastUtil
import com.rt.base.viewbase.VbBaseActivity
import com.rt.common.util.BluePrint
import com.rt.common.util.GlideUtils
import com.rt.common.view.keyboard.KeyboardUtil
import com.rt.common.view.keyboard.MyOnTouchListener
import com.rt.common.view.keyboard.MyTextWatcher
import com.rt.ipms_video.R
import com.rt.ipms_video.adapter.TransactionQueryAdapter
import com.rt.ipms_video.databinding.ActivityTransactionQueryBinding
import com.rt.ipms_video.mvvm.viewmodel.TransactionQueryViewModel
import com.rt.ipms_video.pop.DatePop

@Route(path = ARouterMap.TRANSACTION_QUERY)
class TransactionQueryActivity : VbBaseActivity<TransactionQueryViewModel, ActivityTransactionQueryBinding>(), OnClickListener {
    private lateinit var keyboardUtil: KeyboardUtil

    var transactionQueryAdapter: TransactionQueryAdapter? = null
    var transactionQueryList: MutableList<Int> = ArrayList()
    private val print = BluePrint(this)
    var datePop: DatePop? = null
    var pageIndex = 1
    var pageSize = 10

    override fun initView() {
        GlideUtils.instance?.loadImage(binding.layoutToolbar.ivBack, com.rt.common.R.mipmap.ic_back_white)
        binding.layoutToolbar.tvTitle.text = i18N(com.rt.base.R.string.交易查询)
        binding.layoutToolbar.tvTitle.setTextColor(ContextCompat.getColor(BaseApplication.instance(), com.rt.base.R.color.white))
        GlideUtils.instance?.loadImage(binding.layoutToolbar.ivRight, com.rt.common.R.mipmap.ic_calendar)
        binding.layoutToolbar.ivRight.show()
        initKeyboard()

        binding.rvTransaction.setHasFixedSize(true)
        binding.rvTransaction.layoutManager = LinearLayoutManager(this)
        transactionQueryAdapter = TransactionQueryAdapter(transactionQueryList, this)
        binding.rvTransaction.adapter = transactionQueryAdapter
    }

    private fun initKeyboard() {
        keyboardUtil = KeyboardUtil(binding.kvKeyBoard) {
            binding.etSearch.requestFocus()
            keyboardUtil.changeKeyboard(true)
            keyboardUtil.setEditText(binding.etSearch)
        }

        binding.etSearch.addTextChangedListener(MyTextWatcher(null, null, true, keyboardUtil))

        binding.etSearch.setOnTouchListener(MyOnTouchListener(true, binding.etSearch, keyboardUtil))

        binding.root.setOnClickListener {
            keyboardUtil.hideKeyboard()
        }
    }

    override fun initListener() {
        binding.layoutToolbar.flBack.setOnClickListener(this)
        binding.tvSearch.setOnClickListener(this)
        binding.layoutToolbar.ivRight.setOnClickListener(this)
        binding.root.setOnClickListener(this)
        binding.layoutToolbar.toolbar.setOnClickListener(this)
        binding.srlTransaction.setOnRefreshListener {
            pageIndex = 1
            binding.srlTransaction.finishRefresh(5000)
            transactionQueryList.clear()
            query()
        }
        binding.srlTransaction.setOnLoadMoreListener {
            pageIndex++
            binding.srlTransaction.finishLoadMore(5000)
            query()
        }
    }

    override fun initData() {
        query()
    }

    @SuppressLint("CheckResult")
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fl_back -> {
                onBackPressedSupport()
            }

            R.id.iv_right -> {
                datePop = DatePop(BaseApplication.instance(), object : DatePop.DateCallBack {
                    override fun selectDate() {

                    }

                })
                datePop?.showAsDropDown((v.parent) as Toolbar)
            }

            R.id.tv_search -> {
                query()
            }

            R.id.fl_notification -> {
                print.zkblueprint("")
            }

            R.id.fl_paymentInquiry -> {

            }

            R.id.toolbar,
            binding.root.id -> {
                keyboardUtil.hideKeyboard()
            }
        }
    }

    fun query() {
        binding.srlTransaction.finishLoadMore()
        binding.srlTransaction.finishRefresh()
        keyboardUtil.hideKeyboard()
        val searchContent = binding.etSearch.text.toString()
//        if (searchContent.isNotEmpty() && searchContent.length != 7 && searchContent.length != 8) {
//            ToastUtil.showToast(i18N(com.rt.base.R.string.车牌长度只能是7位或8位))
//            return
//        }
        binding.rvTransaction.show()
        binding.layoutNoData.root.gone()
        transactionQueryList.add(0)
        transactionQueryList.add(1)
        transactionQueryList.add(2)
        transactionQueryList.add(3)
        transactionQueryList.add(4)
        transactionQueryAdapter?.setList(transactionQueryList)
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

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (keyboardUtil.isShow()) {
                keyboardUtil.hideKeyboard()
            } else {
                return super.onKeyDown(keyCode, event)
            }
        }
        return false
    }

}