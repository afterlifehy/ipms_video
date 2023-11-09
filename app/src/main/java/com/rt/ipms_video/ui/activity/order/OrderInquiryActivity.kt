package com.rt.ipms_video.ui.activity.order

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import android.view.View
import android.view.View.OnClickListener
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
import com.rt.base.viewbase.VbBaseActivity
import com.rt.common.util.GlideUtils
import com.rt.common.view.keyboard.KeyboardUtil
import com.rt.common.view.keyboard.MyOnTouchListener
import com.rt.common.view.keyboard.MyTextWatcher
import com.rt.ipms_video.R
import com.rt.ipms_video.adapter.OrderInquiryAdapter
import com.rt.ipms_video.adapter.TransactionQueryAdapter
import com.rt.ipms_video.databinding.ActivityOrderInquiryBinding
import com.rt.ipms_video.mvvm.viewmodel.OrderInquiryViewModel
import com.rt.ipms_video.pop.DatePop

@Route(path = ARouterMap.ORDER_INQUIRY)
class OrderInquiryActivity : VbBaseActivity<OrderInquiryViewModel, ActivityOrderInquiryBinding>(), OnClickListener {
    private lateinit var keyboardUtil: KeyboardUtil
    var orderList: MutableList<Int> = ArrayList()
    var orderInquiryAdapter: OrderInquiryAdapter? = null
    var datePop: DatePop? = null

    override fun initView() {
        GlideUtils.instance?.loadImage(binding.layoutToolbar.ivBack, com.rt.common.R.mipmap.ic_back_white)
        binding.layoutToolbar.tvTitle.text = i18N(com.rt.base.R.string.订单查询)
        binding.layoutToolbar.tvTitle.setTextColor(ContextCompat.getColor(BaseApplication.instance(), com.rt.base.R.color.white))
        GlideUtils.instance?.loadImage(binding.layoutToolbar.ivRight, com.rt.common.R.mipmap.ic_calendar)
        binding.layoutToolbar.ivRight.show()

        binding.rvOrders.setHasFixedSize(true)
        binding.rvOrders.layoutManager = LinearLayoutManager(this)
        orderInquiryAdapter = OrderInquiryAdapter(orderList, this)
        binding.rvOrders.adapter = orderInquiryAdapter

        initKeyboard()
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
    }

    override fun initData() {
        Handler(Looper.getMainLooper()).postDelayed({
            query()
        }, 1000)
    }

    private fun query() {
        keyboardUtil.hideKeyboard()
        val searchContent = binding.etSearch.text.toString()
//        if (searchContent.isNotEmpty() && searchContent.length != 7 && searchContent.length != 8) {
//            ToastUtil.showToast(i18N(com.rt.base.R.string.车牌长度只能是7位或8位))
//            return
//        }
        binding.rvOrders.show()
        binding.layoutNoData.root.gone()
        orderList.add(0)
        orderList.add(1)
        orderList.add(2)
        orderList.add(3)
        orderList.add(4)
        orderInquiryAdapter?.setList(orderList)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fl_back -> {
                onBackPressedSupport()
            }

            R.id.tv_search -> {

            }

            R.id.iv_right -> {
                datePop = DatePop(BaseApplication.instance(), object : DatePop.DateCallBack {
                    override fun selectDate() {

                    }

                })
                datePop?.showAsDropDown((v.parent) as Toolbar)
            }

            R.id.toolbar,
            binding.root.id -> {
                keyboardUtil.hideKeyboard()
            }

            R.id.rll_order -> {
                ARouter.getInstance().build(ARouterMap.ORDER_DETAIL).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).navigation()
            }
        }
    }

    override fun getVbBindingView(): ViewBinding {
        return ActivityOrderInquiryBinding.inflate(layoutInflater)
    }

    override fun onReloadData() {
    }

    override val isFullScreen: Boolean
        get() = true

    override fun providerVMClass(): Class<OrderInquiryViewModel>? {
        return OrderInquiryViewModel::class.java
    }

    override fun marginStatusBarView(): View? {
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