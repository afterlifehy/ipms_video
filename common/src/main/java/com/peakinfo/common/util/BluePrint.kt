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
import com.peakinfo.base.bean.ca.UrgeDetailBean
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
    fun zkblueprint(content: String, type: Int) {
        //打印文本
        try {
            printResult = Print1(content, type)
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
    fun Print1(printText: String, type: Int): Int {
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
            if (type == 1) {
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
            } else if (type == 2) {
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
            } else if (type == 3) {
                val printInfo = JSONObject.parseObject(printText, UrgeDetailBean::class.java)
                zpSDK!!.pageSetup(800, 1600)
                zpSDK!!.DrawSpecialText(147, 10, PrinterInterface.Textfont.siyuanheiti, 24, "上海市道路停车欠费催缴告知书", 0, 0, 0) //3
                zpSDK!!.DrawSpecialText(197, 10 + 36, PrinterInterface.Textfont.siyuanheiti, 20, "编号:${printInfo.urgePayId}", 0, 0, 0) //3
                yLocation = 86
                drawText(10 + 36 + 40, 20, "${printInfo.plateId}驾驶人：")
                yLocation += 32
                drawText(yLocation, 20, "您驾驶的车牌号为:${printInfo.plateId}的车辆在本市xx区道路停车场停车后，")
                yLocation += 32
                drawText(yLocation, 20, "年  月  日——  年  月  日，尚有${printInfo.oweList.size}笔道路停车费未支付，欠付停车费共计XX")
                yLocation += 32
                drawText(yLocation, 20, "元。")
                yLocation += 32
                drawText(yLocation, 20, "根据《上海市社会信用条例》《上海市停车场（库）管理办法》《上海市公")
                yLocation += 32
                drawText(yLocation, 20, "共信用信息归集和使用管理办法》《上海市道路停车场管理规定》等相关规定：")
                yLocation += 32
                drawText(yLocation, 20, "请在收到本告知书后15个工作日内，使用“上海停车”APP（小程序）缴清上述")
                yLocation += 32
                drawText(yLocation, 20, "欠费，也可在本区任一道路停车场通过电子收费系统核实相关机动车辆的道路停")
                yLocation += 32
                drawText(yLocation, 20, "车信息后缴清上述欠费。如您对上述欠费信息有异议的，请在收到本告知书后15")
                yLocation += 32
                drawText(yLocation, 20, "个工作日内，通过电话（咨询电话：   ，服务时间：工作日    ）或下载使用")
                yLocation += 32
                drawText(yLocation, 20, "“上海停车”APP（小程序）在线向本单位提出异议申诉，本单位将在接到您的")
                yLocation += 32
                drawText(yLocation, 20, "异议申诉后5个工作日予以复核答复。逾期未提出异议的，视为无异议。")
                yLocation += 32
                drawText(yLocation, 20, "如您非当事的机动车驾驶人：请在收到本告知书后及时通知当事的机动车驾")
                yLocation += 32
                drawText(yLocation, 20, "驶人按照上述要求限时补缴欠费；您也可以直接代为补缴欠费或者通过上述咨询")
                yLocation += 32
                drawText(yLocation, 20, "电话、“上海停车”APP（小程序）等向本单位提供当事的机动车驾驶人信息并")
                yLocation += 32
                drawText(yLocation, 20, "提交相关佐证材料。")
                yLocation += 32
                drawText(yLocation, 20, "特别告知：")
                yLocation += 32
                drawText(yLocation, 20, "一、逾期未按要求缴清欠费的，相关执法部门可按照《上海市停车场（库）")
                yLocation += 32
                drawText(yLocation, 20, "管理办法》的规定责令补交，并予以行政处罚；")
                yLocation += 32
                drawText(yLocation, 20, "二、逾期未按要求缴清欠费的，相关执法部门可依据《中华人民共和国行政")
                yLocation += 32
                drawText(yLocation, 20, "强制法》的规定申请人民法院强制执行。")
                yLocation += 32
                drawText(yLocation, 20, "三、逾期未按要求缴清欠费的，本单位可依法将您的道路停车欠费信息作为")
                yLocation += 32
                drawText(yLocation, 20, "公共信用失信信息向上海市公共信用信息服务平台归集。")
                yLocation += 32
                drawText(yLocation, 20, "具体欠费信息告知如下：")
                yLocation += 32
                for(i in printInfo.oweList){
                    drawText(yLocation, 20, "年  月  日  时  分——  年  月  日  时  分，在  （           ）路")
                    yLocation += 32
                    drawText(yLocation, 20, "段停放，欠付停车费  元；")
                    yLocation += 32
                }
                var bitmap: Bitmap? = null
                if (printInfo.qrcode!!.isEmpty()) {
                    bitmap =
                        BitmapFactory.decodeResource(BaseApplication.instance().resources, com.peakinfo.common.R.mipmap.ic_print_qr)
                } else {
                    bitmap = AppUtil.base64ToBitmap(printInfo.qrcode!!)
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
                drawText(yLocation, 20, "扫描二维码，获取欠费催缴告知书原件（电子版），支付道路停车费")
                yLocation += 32
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
