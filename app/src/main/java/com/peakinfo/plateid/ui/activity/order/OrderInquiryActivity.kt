package com.peakinfo.plateid.ui.activity.order

import android.content.Intent
import android.view.KeyEvent
import android.view.View
import android.view.View.OnClickListener
import android.view.View.OnScrollChangeListener
import android.view.WindowManager
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import androidx.viewbinding.ViewBinding
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.alibaba.fastjson.JSONObject
import com.blankj.utilcode.util.TimeUtils
import com.peakinfo.base.BaseApplication
import com.peakinfo.base.arouter.ARouterMap
import com.peakinfo.base.bean.OrderBean
import com.peakinfo.base.bean.Street
import com.peakinfo.base.ext.gone
import com.peakinfo.base.ext.i18N
import com.peakinfo.base.ext.show
import com.peakinfo.base.util.ToastUtil
import com.peakinfo.base.viewbase.VbBaseActivity
import com.peakinfo.common.realm.RealmUtil
import com.peakinfo.common.util.GlideUtils
import com.peakinfo.common.view.keyboard.KeyboardUtil
import com.peakinfo.common.view.keyboard.MyOnTouchListener
import com.peakinfo.common.view.keyboard.MyTextWatcher
import com.peakinfo.plateid.R
import com.peakinfo.plateid.adapter.OrderInquiryAdapter
import com.peakinfo.plateid.databinding.ActivityOrderInquiryBinding
import com.peakinfo.plateid.mvvm.viewmodel.OrderInquiryViewModel
import com.peakinfo.plateid.pop.DatePop

@Route(path = ARouterMap.ORDER_INQUIRY)
class OrderInquiryActivity : VbBaseActivity<OrderInquiryViewModel, ActivityOrderInquiryBinding>(), OnClickListener {
    private lateinit var keyboardUtil: KeyboardUtil
    var orderList: MutableList<OrderBean> = ArrayList()
    var orderInquiryAdapter: OrderInquiryAdapter? = null
    var datePop: DatePop? = null
    var pageIndex = 1
    var pageSize = 10
    var startDate = TimeUtils.millis2String(System.currentTimeMillis(), "yyyy-MM-dd")
    var endDate = TimeUtils.millis2String(System.currentTimeMillis(), "yyyy-MM-dd")
    var street: Street? = null

    override fun initView() {
        window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        GlideUtils.instance?.loadImage(binding.layoutToolbar.ivBack, com.peakinfo.common.R.mipmap.ic_back_white)
        binding.layoutToolbar.tvTitle.text = i18N(com.peakinfo.base.R.string.订单查询)
        binding.layoutToolbar.tvTitle.setTextColor(ContextCompat.getColor(BaseApplication.instance(), com.peakinfo.base.R.color.white))
        GlideUtils.instance?.loadImage(binding.layoutToolbar.ivRight, com.peakinfo.common.R.mipmap.ic_calendar)
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
//        binding.rvOrders.addOnScrollListener(object : OnScrollListener() {
//            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
//                super.onScrollStateChanged(recyclerView, newState)
//                when (newState) {
//                    RecyclerView.SCROLL_STATE_IDLE -> {
//                    }
//
//                    RecyclerView.SCROLL_STATE_DRAGGING -> {
//                        val topRowVerticalPosition =
//                            if (recyclerView.childCount === 0) 0 else recyclerView.getChildAt(0).top
//                        binding.srlOrder.setEnableRefresh(topRowVerticalPosition == 0)
//                    }
//
//                    RecyclerView.SCROLL_STATE_SETTLING -> {
//                    }
//                }
//            }
//        })
    }

    override fun initListener() {
        binding.layoutToolbar.flBack.setOnClickListener(this)
        binding.tvSearch.setOnClickListener(this)
        binding.layoutToolbar.ivRight.setOnClickListener(this)
        binding.root.setOnClickListener(this)
        binding.layoutToolbar.toolbar.setOnClickListener(this)
        binding.srlOrder.setOnRefreshListener {
            pageIndex = 1
            binding.srlOrder.finishRefresh(5000)
            orderList.clear()
            query()
        }
        binding.srlOrder.setOnLoadMoreListener {
            pageIndex++
            query()
            binding.srlOrder.finishLoadMore(5000)
        }
    }

    override fun initData() {
        street = RealmUtil.instance?.findCurrentStreet()
        showProgressDialog(20000)
        query()
    }

    private fun query() {
        keyboardUtil.hideKeyboard()
        val searchContent = binding.etSearch.text.toString()
        if (searchContent.isNotEmpty() && (searchContent.length != 7 && searchContent.length != 8)) {
            dismissProgressDialog()
            ToastUtil.showMiddleToast(i18N(com.peakinfo.base.R.string.车牌长度只能是7位或8位))
            return
        }
        val param = HashMap<String, Any>()
        val jsonobject = JSONObject()
        jsonobject["streetNo"] = street?.streetNo
        jsonobject["carLicense"] = searchContent
        jsonobject["startDate"] = startDate
        jsonobject["endDate"] = endDate
        jsonobject["page"] = pageIndex
        jsonobject["size"] = pageSize
        param["attr"] = jsonobject
        mViewModel.orderInquiry(param)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fl_back -> {
                onBackPressedSupport()
            }

            R.id.tv_search -> {
                pageIndex = 1
                showProgressDialog(20000)
                query()
            }

            R.id.iv_right -> {
                datePop = DatePop(BaseApplication.instance(), startDate, endDate, object : DatePop.DateCallBack {
                    override fun selectDate(startTime: String, endTime: String) {
                        startDate = startTime
                        endDate = endTime
                        pageIndex = 1
                        showProgressDialog(20000)
                        query()
                    }

                })
                datePop?.showAsDropDown((v.parent) as Toolbar)
            }

            R.id.toolbar,
            binding.root.id -> {
                keyboardUtil.hideKeyboard()
            }

            R.id.fl_order -> {
                val orderBean = v.tag as OrderBean
                ARouter.getInstance().build(ARouterMap.ORDER_DETAIL).withParcelable(ARouterMap.ORDER, orderBean)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).navigation()
            }
        }
    }

    override fun startObserve() {
        super.startObserve()
        mViewModel.apply {
            orderInquiryLiveData.observe(this@OrderInquiryActivity) {
                dismissProgressDialog()
                val tempList = it.result
                if (pageIndex == 1) {
                    if (tempList.isEmpty()) {
                        orderInquiryAdapter?.setNewInstance(null)
                        binding.rvOrders.gone()
                        binding.layoutNoData.root.show()
                        binding.srlOrder.finishRefresh()
                    } else {
                        orderList.clear()
                        orderList.addAll(tempList)
                        orderInquiryAdapter?.setList(orderList)
                        binding.srlOrder.finishRefresh()
                        binding.rvOrders.show()
                        binding.layoutNoData.root.gone()
                    }
                } else {
                    if (tempList.isEmpty()) {
                        pageIndex--
                        binding.srlOrder.finishLoadMoreWithNoMoreData()
                    } else {
                        orderList.addAll(tempList)
                        orderInquiryAdapter?.setList(orderList)
                        binding.srlOrder.finishLoadMore(300)
                    }
                }
            }
            errMsg.observe(this@OrderInquiryActivity) {
                dismissProgressDialog()
                ToastUtil.showMiddleToast(it.msg)
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

    override fun providerVMClass(): Class<OrderInquiryViewModel> {
        return OrderInquiryViewModel::class.java
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