package com.rt.ipms_video.ui.activity.login

import android.content.Intent
import android.view.View
import android.view.View.OnClickListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.rt.base.arouter.ARouterMap
import com.rt.base.ext.i18N
import com.rt.base.viewbase.VbBaseActivity
import com.rt.ipms_video.R
import com.rt.ipms_video.adapter.StreetChoosedAdapter
import com.rt.ipms_video.databinding.ActivityStreetChooseBinding
import com.rt.ipms_video.dialog.StreetChooseListDialog
import com.rt.ipms_video.mvvm.viewmodel.StreetChooseViewModel

@Route(path = ARouterMap.STREET_CHOOSE)
class StreetChooseActivity : VbBaseActivity<StreetChooseViewModel, ActivityStreetChooseBinding>(),
    OnClickListener {

    var streetChooseListDialog: StreetChooseListDialog? = null
    var streetChoosedAdapter: StreetChoosedAdapter? = null
    var streetChoosedList: MutableList<String> = ArrayList()

    override fun initView() {
        binding.layoutToolbar.tvTitle.text = i18N(com.rt.base.R.string.路段选择)

        binding.rvStreet.setHasFixedSize(true)
        binding.rvStreet.layoutManager = LinearLayoutManager(this)
        streetChoosedAdapter = StreetChoosedAdapter(streetChoosedList, this)
        binding.rvStreet.adapter = streetChoosedAdapter
    }

    override fun initListener() {
        binding.layoutToolbar.flBack.setOnClickListener(this)
        binding.rflAddStreet.setOnClickListener(this)
        binding.rtvEnterWorkBench.setOnClickListener(this)
    }

    override fun initData() {
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fl_back -> {
                onBackPressedSupport()
            }

            R.id.rfl_addStreet -> {
                streetChooseListDialog = StreetChooseListDialog(object : StreetChooseListDialog.StreetChooseCallBack {
                    override fun chooseStreets(checkedList: MutableList<Int>?) {
                        streetChoosedList.add("定西路(愚园路～安化路)1侧")
                        streetChoosedList.add("定西路(愚园路～安化路)2侧")
                        streetChoosedList.add("定西路(愚园路～安化路)3侧")
                        streetChoosedList.add("定西路(愚园路～安化路)4侧")
                        streetChoosedList.add("定西路(愚园路～安化路)5侧")
                        streetChoosedAdapter?.setList(streetChoosedList)
                    }

                })
                streetChooseListDialog?.show()
            }

            R.id.rtv_enterWorkBench -> {
                if (streetChoosedList.isNotEmpty()) {
                    ARouter.getInstance().build(ARouterMap.MAIN).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).navigation()
                }
            }

            R.id.rfl_delete -> {
                val item = v.tag as String
                val position = streetChoosedList.indexOf(item)
                streetChoosedList.remove(item)
                streetChoosedAdapter?.removeAt(position)
            }
        }
    }

    override fun getVbBindingView(): ViewBinding {
        return ActivityStreetChooseBinding.inflate(layoutInflater)
    }

    override fun onReloadData() {
    }

    override val isFullScreen: Boolean
        get() = true

    override fun providerVMClass(): Class<StreetChooseViewModel> {
        return StreetChooseViewModel::class.java
    }

    override fun marginStatusBarView(): View? {
        return binding.layoutToolbar.ablToolbar
    }
}