package com.peakinfo.common.util

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import com.alibaba.fastjson.JSONObject
import com.blankj.utilcode.util.ThreadUtils
import com.blankj.utilcode.util.TimeUtils
import com.peakinfo.base.BaseApplication
import com.peakinfo.base.bean.IncomeCountingBean
import com.peakinfo.base.bean.PrintInfoBean
import com.peakinfo.base.ext.i18n
import com.peakinfo.base.help.ActivityCacheManager
import com.peakinfo.base.util.ToastUtil
import com.peakinfo.common.realm.RealmUtil
import org.json.JSONException
import zpCPCLSDK.zpCPCLSDK.PrinterInterface
import zpCPCLSDK.zpCPCLSDK.zp_cpcl_BluetoothPrinter
import java.util.Calendar

class BluePrint() {
    var zpSDK: zp_cpcl_BluetoothPrinter? = null
    private var printResult = 0
    var mAddress: String? = null

    companion object {
        private var bluePrint: BluePrint? = null

        @get:Synchronized
        val instance: BluePrint?
            get() {
                if (bluePrint == null) bluePrint = BluePrint()
                return bluePrint
            }
        val instanceBlock: BluePrint?
            get() {
                if (bluePrint == null) synchronized(BluePrint::class.java) {
                    if (bluePrint == null) bluePrint = BluePrint()
                }
                return bluePrint
            }
    }

    val orderTypeMap = mutableMapOf(
        1 to "预付",
        2 to "场内支付",
        3 to "欠费补缴"
    )

    @Throws(JSONException::class)
    fun zkblueprint(content: String) {
        //打印文本
        try {
            printResult = Print1(content)
        } catch (e: Exception) {
            Handler(Looper.getMainLooper()).post {
                ToastUtil.showBottomToast("打印机状态异常")
            }
        }
        ActivityCacheManager.instance().getCurrentActivity()!!.runOnUiThread {
            if (printResult == 0) {
//                val toast = Toast.makeText(BaseApplication.instance(), "打印完成", Toast.LENGTH_LONG)
//                toast.setGravity(Gravity.CENTER, 0, 0)
//                toast.show()
            } else if (printResult == -1) {

            } else if (printResult == -2) {
                Handler(Looper.getMainLooper()).post {
                    ToastUtil.showBottomToast("路段名称过长...")
                }
            } else {
                Handler(Looper.getMainLooper()).post {
                    ToastUtil.showBottomToast("打印失败")
                }
            }
        }
    }

    fun connet(address: String): Int {
        //获取设备蓝牙地址
        mAddress = address
        zpSDK = zp_cpcl_BluetoothPrinter(BaseApplication.instance())
        ThreadUtils.runOnUiThread {
            ToastUtil.showBottomToast("打印机开始连接")
        }

        if (!zpSDK!!.connect(mAddress)) {
            ThreadUtils.runOnUiThread {
                ToastUtil.showBottomToast("打印机连接失败")
            }
            zpSDK = null
            printResult = -1
            return printResult
        }
        ThreadUtils.runOnUiThread {
            ToastUtil.showBottomToast("打印机连接成功")
        }
        return 0
    }

    fun disConnect() {
        if (zpSDK != null) {
            zpSDK!!.disconnect()
        }
    }

    val blueToothDevice: MutableList<BluetoothDevice>
        @SuppressLint("MissingPermission")
        get() {
            val mAdapter = BluetoothAdapter.getDefaultAdapter()
            val blueToothDeviceList = mAdapter.bondedDevices.toMutableList()
            var deviceList: MutableList<BluetoothDevice> = ArrayList()
            for (i in blueToothDeviceList) {
                if (i.name.startsWith("CC")) {
                    deviceList.add(i)
                }
            }
            return deviceList
        }

    /**
     * 打印
     *
     * @param printText 打印内容
     * @return
     */
    fun Print1(printText: String?): Int {
        var printText = printText
        var yLocation = 182
        if (printText !== "" && printText != null) {
            val receipt = printText.contains("receipt")
            if (receipt) {
                printText = printText.substring(8)
            }
            var ystart = 10 + 60 + 40
            if (zpSDK == null) {
                return -1
            }
            if (receipt) {
                val incomeCountingBean = JSONObject.parseObject(printText, IncomeCountingBean::class.java)
                var height = 300
                if (incomeCountingBean.list2 != null && incomeCountingBean.list2.size > 0) {
                    height += 300 * incomeCountingBean.list1.size
                } else {
                    height += 150 * incomeCountingBean.list1.size
                }
                zpSDK!!.pageSetup(800, height)
                zpSDK!!.DrawSpecialText(230, 10, PrinterInterface.Textfont.siyuanheiti, 30, "数据打印", 0, 1, 0) //3
                zpSDK!!.DrawSpecialText(
                    20,
                    10 + 60,
                    PrinterInterface.Textfont.siyuanheiti,
                    20,
                    "--------------------------------------------------",
                    0,
                    1,
                    0
                )
                printDrawText("登录账号:", incomeCountingBean.loginName, ystart, 9)
                ystart += 40
                for (i in incomeCountingBean.list1) {
                    zpSDK!!.DrawSpecialText(
                        20,
                        ystart,
                        PrinterInterface.Textfont.siyuanheiti,
                        27,
                        i.streetName,
                        0,
                        0,
                        0
                    )
                    ystart += 40
                    printDrawText("① 交易笔数:", i.number.toString() + " 笔", ystart, 12)
                    ystart += 40
                    printDrawText("② 交易金额:", i.amount + " 元", ystart, 12)
                    ystart += 40
                }
                ystart += 40
                printDrawText("统计时间:", incomeCountingBean.range, ystart, 8)
                ystart += 40
                for (i in incomeCountingBean.list2) {
                    zpSDK!!.DrawSpecialText(
                        20,
                        ystart,
                        PrinterInterface.Textfont.siyuanheiti,
                        27,
                        i.streetName,
                        0,
                        0,
                        0
                    )
                    ystart += 40
                    printDrawText("① 交易笔数:", i.number.toString() + " 笔", ystart, 12)
                    ystart += 40
                    printDrawText("② 交易金额:", i.amount + " 元", ystart, 12)
                    ystart += 40
                }
                zpSDK!!.DrawSpecialText(
                    20,
                    ystart + 40 + 40,
                    PrinterInterface.Textfont.siyuanheiti,
                    20,
                    "--------------------------------------------------",
                    0,
                    1,
                    0
                )
                zpSDK!!.print(0, 0)
                zpSDK!!.printerStatus()
                printGetStatus()
                return 0
            } else {
                val currentStreet = RealmUtil.instance?.findCurrentStreet()
                if (currentStreet!!.streetNo.startsWith("JSW") || currentStreet.streetNo.startsWith("CN")
                    || currentStreet.streetNo.startsWith("CNW")
                ) {
                    val now = Calendar.getInstance()
                    val today = now[Calendar.YEAR].toString() + "年" + (now[Calendar.MONTH] + 1) + "月" + now[Calendar.DAY_OF_MONTH] + "日"
                    val printInfo = JSONObject.parseObject(printText, PrintInfoBean::class.java)
                    zpSDK!!.pageSetup(800, 1600)
                    //zpSDK.drawGraphic(0, 0, 0, 0, bmp);
                    zpSDK!!.DrawSpecialText(147, 10, PrinterInterface.Textfont.siyuanheiti, 24, "上海市机动车道路停车收费", 0, 0, 0) //3
                    zpSDK!!.DrawSpecialText(197, 10 + 36, PrinterInterface.Textfont.siyuanheiti, 24, "电子缴款书下载告知", 0, 0, 0) //3
                    drawText(10 + 36 + 40, 20, "-----------------------------------------------")
                    drawText(10 + 36 + 40 + 32, 20, "停车单号:   " + printInfo.orderId)
                    drawText(10 + 36 + 40 + 32 + 32, 20, "缴费类型:   " + orderTypeMap[printInfo.orderType])
                    drawText(10 + 36 + 40 + 32 + 32 + 32, 20, "车牌号码:   " + printInfo.plateId)
                    yLocation += 32
                    if (printInfo.roadId.length <= 21) {
                        drawText(yLocation, 20, "停车路段:   " + printInfo.roadId)
                    } else if (printInfo.roadId.length > 21 && printInfo.roadId.length <= 42) {
                        drawText(yLocation, 20, "停车路段:   " + printInfo.roadId.substring(0, 21))
                        yLocation += 32
                        drawText(yLocation, 20, "           " + printInfo.roadId.substring(21))
                    } else if (printInfo.roadId.length > 42 && printInfo.roadId.length <= 63) {
                        drawText(yLocation, 20, "停车路段:   " + printInfo.roadId.substring(0, 21))
                        yLocation += 32
                        drawText(yLocation, 20, "           " + printInfo.roadId.substring(21, 42))
                        yLocation += 32
                        drawText(yLocation, 20, "           " + printInfo.roadId.substring(42))
                    } else {
                        return -2
                    }
                    drawText(yLocation + 48, 20, "停放时间:   ")
                    drawText(yLocation + 32, 20, "            " + printInfo.startTime)
                    drawText(yLocation + 64, 20, "            " + printInfo.leftTime)
                    yLocation += 96
//                drawText(yLocation, 20, "缴费金额:   " + printInfo.payMoney)
//                yLocation += 32
                    drawText(yLocation, 20, "-----------------------------------------------")
                    yLocation += 36
                    drawText(yLocation, 20, "----------------电子缴款书开具方式----------------")
                    yLocation += 36
                    drawText(yLocation, 20, "扫描如下二维码，确认订单，填写邮箱地址，开具道路停车")
                    yLocation += 36
                    drawText(yLocation, 20, "收费电子书")
                    yLocation += 36

                    var bitmap: Bitmap? = null
                    if (printInfo.ticketQrCode.isEmpty()) {
                        bitmap =
                            BitmapFactory.decodeResource(BaseApplication.instance().resources, com.peakinfo.common.R.mipmap.ic_print_qr)
                    } else {
                        bitmap = AppUtil.base64ToBitmap(printInfo.ticketQrCode)
                    }
                    val scaledBitmap = Bitmap.createScaledBitmap(bitmap!!, 300, 300, true)
                    zpSDK!!.drawGraphic(
                        65 + 60,
                        yLocation,
                        300,
                        300,
                        scaledBitmap
                    )
//                zpSDK!!.drawQrCode(65 + 60, yLocation, "https://shtc.jtcx.sh.cn/union.html", 0, 10, 0)
                    yLocation += (300 + 18)
                    drawText(yLocation, 20, "--------------------温馨提示：-------------------")
                    yLocation += 36
                    drawText(yLocation, 20, "${printInfo.plateId}的车主(单位)")
                    yLocation += 36
                    drawText(
                        yLocation,
                        20,
                        "您(单位)在${
                            TimeUtils.millis2String(
                                System.currentTimeMillis(),
                                "yyyy年MM月dd日"
                            )
                        }之前，累计有${printInfo.oweCount}笔道路停车欠费"
                    )
                    yLocation += 36
                    drawText(yLocation, 20, "记录，请您(单位)登录“上海停车“官方 APP、小程序(微")
                    yLocation += 36
                    drawText(yLocation, 20, "信、支付宝)尽快补缴。")
                    yLocation += 36
                    yLocation += 36
                    drawText(yLocation, 20, "--------------------注意事项-------------------")
                    yLocation += 36
                    drawText(yLocation, 20, "1、如需要核实有关停车收费情况请致电 " + printInfo.phone + " 。")
                    yLocation += 36
                    drawText(yLocation, 20, "2、本告知书仅为您(单位)本次停车付费的凭证，如需开具")
                    yLocation += 36
                    drawText(yLocation, 20, "   电子缴款书请扫描二维码。")
                    yLocation += 36
                    drawText(yLocation, 20, "3、预付费遵循多退少补原则，实际停车时长超出预付时长")
                    yLocation += 36
                    drawText(yLocation, 20, "   的，请及时补缴停车费;实际停车时长少于预付时长的，")
                    yLocation += 36
                    drawText(yLocation, 20, "   在车辆离场后 24小时内将原路退还超出的停车费。")
                    yLocation += 36
                    drawText(yLocation, 20, "4、如您(单位)在下载电子票据过程中遇到问题，请将问题")
                    yLocation += 36
                    drawText(yLocation, 20, "   描述和您的姓名、电话等有效的联系方式反馈至邮箱")
                    yLocation += 36
                    drawText(yLocation, 20, "   service@jtcx.sh.com")
                    yLocation += 36
                    drawText(yLocation, 20, "-----------------------------------------------")
                    yLocation += 36
                    if (printInfo.remark.length <= 22) {
                        drawText(yLocation, 24, printInfo.remark)
                        yLocation += 36
                    } else {
                        drawText(yLocation, 24, printInfo.remark.substring(0, 22))
                        drawText(yLocation + 36, 24, printInfo.remark.substring(22))
                        yLocation += 72
                    }
                    if (printInfo.company.length <= 22) {
                        drawText(yLocation, 24, printInfo.company)
                    } else {
                        drawText(yLocation, 24, printInfo.company.substring(0, 22))
                        drawText(yLocation + 36, 24, printInfo.company.substring(22))
                    }
                } else {
                    val now = Calendar.getInstance()
                    val today = now[Calendar.YEAR].toString() + "年" + (now[Calendar.MONTH] + 1) + "月" + now[Calendar.DAY_OF_MONTH] + "日"
                    val printInfo = JSONObject.parseObject(printText, PrintInfoBean::class.java)
                    zpSDK!!.pageSetup(800, 1400)
                    //zpSDK.drawGraphic(0, 0, 0, 0, bmp);
                    zpSDK!!.DrawSpecialText(147, 10, PrinterInterface.Textfont.siyuanheiti, 24, "上海市机动车道路停车费", 0, 0, 0) //3
                    zpSDK!!.DrawSpecialText(197, 10 + 36, PrinterInterface.Textfont.siyuanheiti, 24, "电子票据告知书", 0, 0, 0) //3
                    drawText(10 + 36 + 40, 20, "-----------------------------------------------")
                    drawText(10 + 36 + 40 + 32, 20, "停车单号:   " + printInfo.orderId)
                    drawText(10 + 36 + 40 + 32 + 32, 20, "车牌号码:   " + printInfo.plateId)
                    if (printInfo.roadId.length <= 21) {
                        drawText(yLocation, 20, "停车路段:   " + printInfo.roadId)
                    } else if (printInfo.roadId.length > 21 && printInfo.roadId.length <= 42) {
                        drawText(yLocation, 20, "停车路段:   " + printInfo.roadId.substring(0, 21))
                        yLocation += 32
                        drawText(yLocation, 20, "           " + printInfo.roadId.substring(21))
                    } else if (printInfo.roadId.length > 42 && printInfo.roadId.length <= 63) {
                        drawText(yLocation, 20, "停车路段:   " + printInfo.roadId.substring(0, 21))
                        yLocation += 32
                        drawText(yLocation, 20, "           " + printInfo.roadId.substring(21, 42))
                        yLocation += 32
                        drawText(yLocation, 20, "           " + printInfo.roadId.substring(42))
                    } else {
                        return -2
                    }
                    drawText(yLocation + 48, 20, "停放时间:   ")
                    drawText(yLocation + 32, 20, "            " + printInfo.startTime)
                    drawText(yLocation + 64, 20, "            " + printInfo.leftTime)
                    yLocation += 96
                    drawText(yLocation, 20, "缴费金额:   " + printInfo.payMoney)
                    yLocation += 32
                    drawText(yLocation, 20, "-----------------------------------------------")
                    yLocation += 36
                    drawText(yLocation, 20, "----------------电子票据开具方式----------------")
                    yLocation += 36
                    drawText(yLocation, 20, "1、扫描下载“上海停车”官方APP、小程序(微信、支付宝)")
                    yLocation += 36
                    var bitmap: Bitmap? = null
                    if (printInfo.ticketQrCode.isEmpty()) {
                        bitmap = BitmapFactory.decodeResource(BaseApplication.instance().resources, com.peakinfo.common.R.mipmap.ic_print_qr)
                    } else {
                        bitmap = AppUtil.base64ToBitmap(printInfo.ticketQrCode)
                    }
                    val scaledBitmap = Bitmap.createScaledBitmap(bitmap!!, 300, 300, true)
                    zpSDK!!.drawGraphic(
                        65 + 60,
                        yLocation,
                        300,
                        300,
                        scaledBitmap
                    )
                    yLocation += (300 + 18)
                    drawText(yLocation, 20, "2、注册您的“上海停车”账号,绑定车牌。")
                    yLocation += 36
                    drawText(yLocation, 20, "3、在“停车缴费”---“我要开票”---“道路停车电子缴”")
                    yLocation += 36
                    drawText(yLocation, 20, "款书(票据)---下载您的道路停车票据")
                    yLocation += 36
                    drawText(yLocation, 20, "--------------------注意事项-------------------")
                    yLocation += 36
                    drawText(yLocation, 20, "1、本告知书仅为您(单位)本次停车付费的凭证，不作为电子")
                    yLocation += 36
                    drawText(yLocation, 20, "   票据。")
                    yLocation += 36
                    drawText(yLocation, 20, "2、如需要核实有关停车收费情况请致电 " + printInfo.phone + " 。")
                    yLocation += 36
                    drawText(yLocation, 20, "3、如您需要电子票据的,请在即日起30天内，通过“上海停")
                    yLocation += 36
                    drawText(yLocation, 20, "   车”官方APP、小程序(微信、支付宝)下载。如您(单位)")
                    yLocation += 36
                    drawText(yLocation, 20, "   在下载电子票据过程中遇到问题，请将问题描述和您的")
                    yLocation += 36
                    drawText(yLocation, 20, "   姓名、电话等有效的联系方式反馈至邮箱service@shtc")
                    yLocation += 36
                    drawText(yLocation, 20, "   xx.com")
                    yLocation += 36
                    drawText(yLocation, 20, "-----------------------------------------------")
                    yLocation += 36
                    if (printInfo.remark.length <= 22) {
                        drawText(yLocation, 24, printInfo.remark)
                        yLocation += 36
                    } else {
                        drawText(yLocation, 24, printInfo.remark.substring(0, 22))
                        drawText(yLocation + 36, 24, printInfo.remark.substring(22))
                        yLocation += 72
                    }
                    if (printInfo.company.length <= 22) {
                        drawText(yLocation, 24, printInfo.company)
                    } else {
                        drawText(yLocation, 24, printInfo.company.substring(0, 22))
                        drawText(yLocation + 36, 24, printInfo.company.substring(22))
                    }
                }
            }
        }
        zpSDK!!.print(0, 0)
        zpSDK!!.printerStatus()
        printGetStatus()
        return 0
    }

    fun printGetStatus() {
        Thread {
            try {
                when (zpSDK?.GetStatus()) {
                    -1 -> {
                        Handler(Looper.getMainLooper()).post {
                            ToastUtil.showBottomToast(i18n(com.peakinfo.base.R.string.打印机状态异常))
                        }
                        disConnect()
                        return@Thread
                    }

                    0 -> {
                        disConnect()
                    }

                    1 -> {
                        Handler(Looper.getMainLooper()).post {
                            ToastUtil.showBottomToast(i18n(com.peakinfo.base.R.string.打印机缺纸))
                        }
                        disConnect()
                        return@Thread
                    }

                    2 -> {
                        Handler(Looper.getMainLooper()).post {
                            ToastUtil.showBottomToast(i18n(com.peakinfo.base.R.string.打印机开盖))
                        }
                        disConnect()
                        return@Thread
                    }
                }
            } catch (e: Exception) {
                Handler(Looper.getMainLooper()).post {
                    ToastUtil.showBottomToast(i18n(com.peakinfo.base.R.string.打印机状态异常))
                }
                disConnect()
                return@Thread
            }
        }.start()
    }

    fun printDrawText(text1: String, text2: String, ystart: Int, spaceOffset: Int) {
        var space = ""
        var count = 35 - text2.length - spaceOffset
        for (i in 0..count) {
            space += " "
        }
        zpSDK!!.DrawSpecialText(
            20,
            ystart,
            PrinterInterface.Textfont.siyuanheiti,
            27,
            text1 + space + text2,
            0,
            0,
            0
        )
    }

    fun drawText(y: Int, fontSize: Int, txt: String) {
        zpSDK!!.DrawSpecialText(
            25,
            y,
            PrinterInterface.Textfont.siyuanheiti,
            fontSize,
            txt,
            0,
            0,
            0
        )
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val width = options.outWidth
        val height = options.outHeight
        var inSampleSize = 1
        if (reqWidth > 0 && reqHeight > 0) {
            if (width > reqWidth || height > reqHeight) {
                val halfWidth = width / 2
                val halfHeight = height / 2

                // 计算最大的 inSampleSize 值，使得宽度和高度都大于请求的宽度和高度
                while (halfWidth / inSampleSize >= reqWidth && halfHeight / inSampleSize >= reqHeight) {
                    inSampleSize *= 2
                }
            }
        }
        return inSampleSize
    }
}
