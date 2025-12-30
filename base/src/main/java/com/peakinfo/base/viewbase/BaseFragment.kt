package com.peakinfo.base.viewbase

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.peakinfo.base.base.mvvm.BaseViewModel
import com.peakinfo.base.dialog.IOSLoadingDialog
import com.peakinfo.base.network.ViewNetWorkStateManager
import org.greenrobot.eventbus.EventBus

abstract class BaseFragment<VM : BaseViewModel> : Fragment() {
    protected lateinit var mViewModel: VM
    var mRoot: View? = null
    var mInflater: LayoutInflater? = null
    private var mFragment: Fragment? = null
    private var mNewWorkStateManager: ViewNetWorkStateManager? = null

    //用来存储需要监听的网络错误
    private var networkErrorTagList = ArrayList<String>()
    private lateinit var mProgressDialog: IOSLoadingDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val loadBuilder =
            IOSLoadingDialog.Builder(activity)
                .setMessage("")
                .setShowMessage(false)
                .setCancelable(true)
                .setCancelOutside(false)
        mProgressDialog = loadBuilder.create()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initVM()
        initView()
        initListener()
        initData()
        startObserve()
        if (!EventBus.getDefault().isRegistered(this) && isRegEventBus()) {
            EventBus.getDefault().register(this)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(getLayoutResId(), null)
    }

    private fun initVM() {
        providerVMClass()?.let {
            mViewModel = ViewModelProvider(this).get(it)
            mViewModel.let(lifecycle::addObserver)
        }
    }

    open fun isRegEventBus(): Boolean {
        return false
    }

    fun onRestartInstance(bundle: Bundle) {

    }

    protected fun startActivity(z: Class<*>) {
        startActivity(Intent(activity, z))
    }

    fun showProgressDialog(i: Long) {
        mProgressDialog.show()
        Handler(Looper.getMainLooper()).postDelayed({ dismissProgressDialog() }, i)
    }

    fun dismissProgressDialog() {
        mProgressDialog.dismiss()
    }

    /**
     * 添加
     *
     * @param frameLayoutId
     * @param fragment
     */
    protected open fun addFragment(frameLayoutId: Int, fragment: Fragment?) {
        if (fragment != null) {
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            if (fragment.isAdded) {
                if (mFragment != null) {
                    transaction?.hide(mFragment!!)?.show(fragment)
                } else {
                    transaction?.show(fragment)
                }
            } else {
                if (mFragment != null) {
                    transaction?.hide(mFragment!!)?.add(frameLayoutId, fragment)
                } else {
                    transaction?.add(frameLayoutId, fragment)
                }
            }
            mFragment = fragment
            transaction?.commit()
        }
    }

    override fun onDestroy() {
        if (::mViewModel.isInitialized) {
            mViewModel.let {
                lifecycle.removeObserver(it)
            }
        }
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    open fun startObserve() {}
    abstract fun getLayoutResId(): Int
    abstract fun initView()
    abstract fun initListener()
    abstract fun initData()
    open fun providerVMClass(): Class<VM>? = null
}