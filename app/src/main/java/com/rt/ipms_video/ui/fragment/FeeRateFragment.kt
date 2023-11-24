package com.rt.ipms_video.ui.fragment

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import com.rt.base.BaseApplication
import com.rt.base.viewbase.VbBaseFragment
import com.rt.ipms_video.adapter.FeeRateAdapter
import com.rt.ipms_video.databinding.FragmentFeeRateBinding
import com.rt.ipms_video.mvvm.viewmodel.FeeRateFragmentViewModel

class FeeRateFragment : VbBaseFragment<FeeRateFragmentViewModel, FragmentFeeRateBinding>() {
    var feeRateAdapter: FeeRateAdapter? = null
    var feeRateList: MutableList<Int> = ArrayList()
    override fun initView() {
        binding.rvFeeRate.setHasFixedSize(true)
        binding.rvFeeRate.layoutManager = LinearLayoutManager(BaseApplication.instance())
        feeRateAdapter = FeeRateAdapter(feeRateList)
        binding.rvFeeRate.adapter = feeRateAdapter
    }

    override fun initData() {
        feeRateList.add(1)
        feeRateList.add(2)
        feeRateList.add(3)
        feeRateAdapter?.setList(feeRateList)
    }

    override fun initListener() {
    }

    override fun onReloadData() {
    }

    override fun getVbBindingView(): ViewBinding {
        return FragmentFeeRateBinding.inflate(layoutInflater)
    }
}