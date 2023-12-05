package com.peakinfo.plateid.util

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.PathUtils
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloadListener
import com.liulishuo.filedownloader.FileDownloader
import com.liulishuo.filedownloader.util.FileDownloadUtils
import com.peakinfo.base.BaseApplication
import com.peakinfo.base.bean.UpdateBean
import com.peakinfo.base.ds.PreferencesDataStore
import com.peakinfo.base.ds.PreferencesKeys
import com.peakinfo.base.ext.gone
import com.peakinfo.base.ext.i18N
import com.peakinfo.base.ext.show
import com.peakinfo.base.help.ActivityCacheManager
import com.peakinfo.base.util.ToastUtil
import com.peakinfo.plateid.dialog.UpdateDialog
import com.tbruyelle.rxpermissions3.RxPermissions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import rx.Observable
import rx.Subscriber
import rx.functions.Func1
import rx.schedulers.Schedulers
import java.io.File

/**
 * Created by kxrt_android_03 on 2017/4/20.
 */
class UpdateUtil {
    private var updateDialog: UpdateDialog? = null
    private var updateBean: UpdateBean? = null

    companion object {
        private var updateUtil: UpdateUtil? = null
        val instance: UpdateUtil?
            get() {
                if (updateUtil == null) {
                    updateUtil = UpdateUtil()
                }
                return updateUtil
            }
    }

    fun checkNewVersion(updateBean: UpdateBean, inter: UpdateInterface) {
        this.updateBean = updateBean
        if (updateBean.state == "0") {
            updateDialog = UpdateDialog(updateBean, object : UpdateDialog.updateCallBack {
                override fun confirm() {
                    inter.requestionPermission()
                }

            })
            updateDialog?.show()
            runBlocking {
                PreferencesDataStore(BaseApplication.instance()).putLong(
                    PreferencesKeys.lastCheckUpdateTime,
                    System.currentTimeMillis()
                )
            }
        }
    }

    fun downloadFileAndInstall() {
        updateDialog?.downLoadUI()
        ToastUtil.showMiddleToast("开始下载更新")
        GlobalScope.launch(Dispatchers.IO) {
            FileDownloader.setup(ActivityCacheManager.instance().getCurrentActivity())
            val path = "${PathUtils.getExternalDownloadsPath()}/${FileDownloadUtils.generateFileName(updateBean?.url)}.apk"
            FileDownloader.getImpl().create(updateBean?.url)
                .setPath(path)
                .setListener(object : FileDownloadListener() {
                    override fun pending(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                    }

                    override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            updateDialog?.updateProgress((soFarBytes * 100f / totalBytes.toFloat()).toInt())
                        }
                    }

                    override fun completed(task: BaseDownloadTask?) {
                        AppUtils.installApp(path)
                        if (updateBean?.force == "1") {
                            updateDialog?.updateUI()
                        } else if (updateBean?.force == "0") {
                            updateDialog?.dismiss()
                        }
                    }

                    override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                    }

                    override fun error(task: BaseDownloadTask?, e: Throwable?) {
                    }

                    override fun warn(task: BaseDownloadTask?) {
                    }

                }).start()
        }
    }

    interface UpdateInterface {
        fun requestionPermission()
    }
}
