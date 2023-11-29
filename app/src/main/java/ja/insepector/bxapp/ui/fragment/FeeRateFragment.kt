package ja.insepector.bxapp.ui.fragment

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import com.alibaba.fastjson.JSONObject
import ja.insepector.base.BaseApplication
import ja.insepector.base.bean.FeeRateBean
import ja.insepector.base.util.ToastUtil
import ja.insepector.base.viewbase.VbBaseFragment
import ja.insepector.bxapp.adapter.FeeRateAdapter
import ja.insepector.bxapp.databinding.FragmentFeeRateBinding
import ja.insepector.bxapp.mvvm.viewmodel.FeeRateFragmentViewModel

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