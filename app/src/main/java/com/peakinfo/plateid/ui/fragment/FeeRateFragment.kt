package com.peakinfo.plateid.ui.fragment

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import com.alibaba.fastjson.JSONObject
import com.peakinfo.base.BaseApplication
import com.peakinfo.base.bean.FeeRateBean
import com.peakinfo.base.util.ToastUtil
import com.peakinfo.base.viewbase.VbBaseFragment
import com.peakinfo.plateid.adapter.FeeRateAdapter
import com.peakinfo.plateid.databinding.FragmentFeeRateBinding
import com.peakinfo.plateid.mvvm.viewmodel.FeeRateFragmentViewModel

class FeeRateFragment : VbBaseFragment<FeeRateFragmentViewModel, FragmentFeeRateBinding>() {
    var feeRateAdapter: FeeRateAdapter? = null
    var feeRateList: MutableList<FeeRateBean> = ArrayList()
    var streetNo = ""
    override fun initView() {
        streetNo = arguments?.getString("streetNo").toString()

        binding.rvFeeRate.setHasFixedSize(true)
        binding.rvFeeRate.layoutManager = LinearLayoutManager(BaseApplication.instance())
        feeRateAdapter = FeeRateAdapter(feeRateList)
        binding.rvFeeRate.adapter = feeRateAdapter
    }

    override fun initData() {
        showProgressDialog()
        val param = HashMap<String, Any>()
        val jsonobject = JSONObject()
        jsonobject["streetNo"] = streetNo
        param["attr"] = jsonobject
        mViewModel.feeRate(param)
    }

    override fun initListener() {
    }

    override fun startObserve() {
        super.startObserve()
        mViewModel.apply {
            feeRateLiveData.observe(this@FeeRateFragment) {
                dismissProgressDialog()
                feeRateList.addAll(it.result)
                feeRateAdapter?.setList(feeRateList)
            }
            errMsg.observe(this@FeeRateFragment) {
                dismissProgressDialog()
                ToastUtil.showToast(it.msg)
            }
        }
    }

    override fun providerVMClass(): Class<FeeRateFragmentViewModel> {
        return FeeRateFragmentViewModel::class.java
    }

    override fun onReloadData() {
    }

    override fun getVbBindingView(): ViewBinding {
        return FragmentFeeRateBinding.inflate(layoutInflater)
    }
}