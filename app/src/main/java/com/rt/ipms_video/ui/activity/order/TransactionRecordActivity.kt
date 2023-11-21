package com.rt.ipms_video.ui.activity.order

import android.view.View
import android.view.View.OnClickListener
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.fastjson.JSONObject
import com.rt.base.BaseApplication
import com.rt.base.arouter.ARouterMap
import com.rt.base.bean.TransactionBean
import com.rt.base.ext.gone
import com.rt.base.ext.i18N
import com.rt.base.ext.show
import com.rt.base.util.ToastUtil
import com.rt.base.viewbase.VbBaseActivity
import com.rt.common.util.BluePrint
import com.rt.common.util.GlideUtils
import com.rt.ipms_video.R
import com.rt.ipms_video.adapter.TransactionRecordAdapter
import com.rt.ipms_video.databinding.ActivityTransactionRecordBinding
import com.rt.ipms_video.mvvm.viewmodel.TransactionRecordViewModel

@Route(path = ARouterMap.TRANSACTION_RECORD)
class TransactionRecordActivity : VbBaseActivity<TransactionRecordViewModel, ActivityTransactionRecordBinding>(), OnClickListener {
    var transactionRecordAdapter: TransactionRecordAdapter? = null
    var transactionRecordList: MutableList<TransactionBean> = ArrayList()
    var orderNo = ""
    val print = BluePrint(this)

    override fun initView() {
        GlideUtils.instance?.loadImage(binding.layoutToolbar.ivBack, com.rt.common.R.mipmap.ic_back_white)
        binding.layoutToolbar.tvTitle.text = i18N(com.rt.base.R.string.交易记录信息)
        binding.layoutToolbar.tvTitle.setTextColor(ContextCompat.getColor(BaseApplication.instance(), com.rt.base.R.color.white))

        orderNo = intent.getStringExtra(ARouterMap.TRANSACTION_RECORD_ORDER_NO).toString()

        binding.rvTransactionRecord.setHasFixedSize(true)
        binding.rvTransactionRecord.layoutManager = LinearLayoutManager(this@TransactionRecordActivity)
        transactionRecordAdapter = TransactionRecordAdapter(transactionRecordList, this)
        binding.rvTransactionRecord.adapter = transactionRecordAdapter
    }

    override fun initListener() {
        binding.layoutToolbar.flBack.setOnClickListener(this)
    }

    override fun initData() {
        query()
    }

    private fun query() {
        showProgressDialog()
        val param = HashMap<String, Any>()
        val jsonobject = JSONObject()
        jsonobject["orderNo"] = orderNo
        param["attr"] = jsonobject
        mViewModel.transactionInquiryByOrder(param)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fl_back -> {
                onBackPressedSupport()
            }

            R.id.fl_notification -> {
                showProgressDialog()
                val transactionBean = v.tag as TransactionBean
                val param = HashMap<String, Any>()
                val jsonobject = JSONObject()
                jsonobject["tradeNo"] = transactionBean.tradeNo
                param["attr"] = jsonobject
                mViewModel.transactionInquiryByOrder(param)
            }
        }
    }

    override fun startObserve() {
        super.startObserve()
        mViewModel.apply {
            transactionInquiryByOrderLiveData.observe(this@TransactionRecordActivity) {
                transactionRecordList.clear()
                transactionRecordList.addAll(it.result)
                if (transactionRecordList.size > 0) {
                    binding.rvTransactionRecord.show()
                    binding.layoutNoData.root.gone()
                    transactionRecordAdapter?.setList(transactionRecordList)
                } else {
                    binding.rvTransactionRecord.gone()
                    binding.layoutNoData.root.show()
                }
                dismissProgressDialog()
            }
            notificationInquiryLiveData.observe(this@TransactionRecordActivity) {
                print.zkblueprint("")
            }
            errMsg.observe(this@TransactionRecordActivity) {
                dismissProgressDialog()
                ToastUtil.showToast(it.msg)
            }
        }
    }

    override fun getVbBindingView(): ViewBinding {
        return ActivityTransactionRecordBinding.inflate(layoutInflater)
    }

    override fun onReloadData() {
    }

    override val isFullScreen: Boolean
        get() = true

    override fun marginStatusBarView(): View {
        return binding.layoutToolbar.ablToolbar
    }

    override fun providerVMClass(): Class<TransactionRecordViewModel> {
        return TransactionRecordViewModel::class.java
    }
}