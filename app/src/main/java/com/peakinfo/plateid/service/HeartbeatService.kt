package com.peakinfo.plateid.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.peakinfo.base.BaseApplication
import com.peakinfo.base.ds.PreferencesDataStore
import com.peakinfo.base.ds.PreferencesKeys
import com.peakinfo.plateid.mvvm.viewmodel.HeartBeatViewModel
import kotlinx.coroutines.runBlocking
import java.util.Timer
import java.util.TimerTask

class HeartbeatService : Service() {

    private val vm = HeartBeatViewModel()
    private var token = ""
    private var longitude = 0.0
    private var latitude = 0.0
    private var timer: Timer? = null

    override fun onCreate() {
        super.onCreate()
        // 启动前台服务（Android 8.0+要求）
        startForegroundServiceWithNotification()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        timer?.cancel()
        timer = Timer()
        timer?.schedule(object : TimerTask() {
            override fun run() {
                runBlocking {
                    token = PreferencesDataStore(BaseApplication.instance()).getString(PreferencesKeys.token)
                    longitude = PreferencesDataStore(BaseApplication.baseApplication).getDouble(PreferencesKeys.lon)
                    latitude = PreferencesDataStore(BaseApplication.baseApplication).getDouble(PreferencesKeys.lat)
                    if (token.isEmpty()) {
                        cancel()
                        stopSelf()
                    } else {
                        sendHeartbeat()
                    }
                }
            }
        }, 0, 15 * 60 * 1000) // 每15分钟一次

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun sendHeartbeat() {
        val param = hashMapOf<String, Any>(
            "token" to token,
            "longitude" to longitude,
            "latitude" to latitude,
            "dataTime" to System.currentTimeMillis()
        )
        vm.heartbeat(param)
    }

    private fun startForegroundServiceWithNotification() {
        val channelId = "heartbeat_channel"
        val channelName = "Heartbeat Service"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val chan = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(chan)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("心跳服务运行中")
            .setContentText("正在发送心跳数据")
            .setSmallIcon(com.peakinfo.base.R.mipmap.ic_app_logo) // 替换为你项目中的图标
            .build()

        startForeground(1, notification)
    }
}
