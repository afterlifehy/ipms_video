package com.peakinfo.common.util

import com.baidu.location.BDAbstractLocationListener
import com.baidu.location.BDLocation
import com.baidu.location.LocationClient
import com.baidu.location.LocationClientOption
import com.blankj.utilcode.util.NetworkUtils
import com.peakinfo.base.BaseApplication

class BaiduLocationUtil {
    private var mLocationClient: LocationClient? = null
    private var callback: BaiduLocationCallBack? = null

    companion object {
        var longitude = 0.0
        var latitude = 0.0
    }

    fun initBaiduLocation() {
        // 初始化client
        if (null == mLocationClient) {
            //setAgreePrivacy接口需要在LocationClient实例化之前调用
            //如果setAgreePrivacy接口参数设置为了false，则定位功能不会实现
            //true，表示用户同意隐私合规政策
            //false，表示用户不同意隐私合规政策
            LocationClient.setAgreePrivacy(true);
            mLocationClient = LocationClient(BaseApplication.instance())
        }

        mLocationClient?.locOption = locationOption
        mLocationClient?.registerLocationListener(object : BDAbstractLocationListener() {
            override fun onReceiveLocation(p0: BDLocation?) {
                longitude = p0!!.longitude
                latitude = p0.latitude
                val errorCode = p0.locType
                // 获取定位类型、定位错误返回码，具体信息可参照类参考中BDLocation类中的说明
                if (errorCode == 61 || errorCode == 66 || errorCode == 161) {
                    // 定位成功
                    locationSuccess(longitude, latitude, locationOption, p0.address.address)
                } else {
                    // 定位失败
                    locationFailure(locationOption)
                }
            }
        })
    }

    private val locationOption: LocationClientOption
        get() {
            val mOption = LocationClientOption()
            if (NetworkUtils.isConnected()) {
                //可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
                mOption.locationMode = LocationClientOption.LocationMode.Hight_Accuracy
            } else {
                mOption.locationMode = LocationClientOption.LocationMode.Device_Sensors
            }
            //可选，设置是否gps优先，只在高精度模式下有效。默认关闭
            mOption.openGps = true
            //海外地区定位，无需设置坐标类型，统一返回wgs84类型坐标
            mOption.setCoorType("bd09ll")
            //首次定位时可以选择定位的返回是准确性优先还是速度优先，默认为速度优先
            mOption.firstLocType = LocationClientOption.FirstLocType.SPEED_IN_FIRST_LOC
            //设置发起定位请求的间隔，int类型，单位ms
            mOption.setScanSpan(1000)
            //设置是否当卫星定位有效时按照1S/1次频率输出卫星定位结果，默认false
            mOption.isLocationNotify = true
            mOption.isIgnoreKillProcess = true
            mOption.disableLocCache = false
            mOption.isNeedNewVersionRgc = true
            mOption.addrType = "all"

            return mOption
        }

    /**
     * 开始定位
     */
    fun startLocation() {
        // 开启地图定位图层
        mLocationClient!!.start()
        mLocationClient!!.requestLocation()
    }


    /**
     * 停止定位
     */
    fun stopLocation() {
        mLocationClient!!.stop()
    }

    private fun locationFailure(location: LocationClientOption?) {
        if (callback != null) {
            callback!!.locationChange(0.0, 0.0, location, false, "")
        }
    }

    private fun locationSuccess(
        longitude: Double,
        latitude: Double,
        location: LocationClientOption?,
        address: String?
    ) {
        if (callback != null) {
            callback!!.locationChange(longitude, latitude, location, true, address)
        }
    }

    fun setBaiduLocationCallBack(callback: BaiduLocationCallBack?) {
        this.callback = callback
    }

    interface BaiduLocationCallBack {
        fun locationChange(lon: Double, lat: Double, location: LocationClientOption?, isSuccess: Boolean, address: String?)
    }
}