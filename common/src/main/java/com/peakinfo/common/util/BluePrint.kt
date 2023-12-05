package com.peakinfo.common.util

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.view.Gravity
import android.widget.Toast
import com.alibaba.fastjson.JSONObject
import com.blankj.utilcode.util.ToastUtils
import com.peakinfo.base.BaseApplication
import com.peakinfo.base.bean.IncomeCountingBean
import com.peakinfo.base.bean.PrintInfoBean
import com.peakinfo.base.help.ActivityCacheManager
import org.json.JSONArray
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

    @Throws(JSONException::class)
    fun zkblueprint(content: String) {
        //打印文本
        printResult = Print1(content)
        ActivityCacheManager.instance().getCurrentActivity()!!.runOnUiThread {
            if (printResult == 0) {
                val toast = Toast.makeText(BaseApplication.instance(), "打印完成", Toast.LENGTH_LONG)
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()
            } else if (printResult == -1) {
                val toast = Toast.makeText(BaseApplication.instance(), "蓝牙未连接", Toast.LENGTH_LONG)
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()
            } else if (printResult == -2) {
                val toast = Toast.makeText(BaseApplication.instance(), "路段名称过长...", Toast.LENGTH_LONG)
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()
            } else {
                val toast = Toast.makeText(BaseApplication.instance(), "打印失败", Toast.LENGTH_LONG)
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()
            }
        }
    }

    fun connet(address: String): Int {
        //获取设备蓝牙地址
        mAddress = address
        zpSDK = zp_cpcl_BluetoothPrinter(BaseApplication.instance())
        ToastUtils.showShort("开始连接")
        if (!zpSDK!!.connect(mAddress)) {
            ToastUtils.showShort("连接失败")
            printResult = -1
            return printResult
        }
        ToastUtils.showShort("连接成功")
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
                var height = 300 + 300 * incomeCountingBean.list1.size
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
                return 0
            } else {
                val now = Calendar.getInstance()
                val today = now[Calendar.YEAR].toString() + "年" + (now[Calendar.MONTH] + 1) + "月" + now[Calendar.DAY_OF_MONTH] + "日"
                val printInfo = JSONObject.parseObject(printText, PrintInfoBean::class.java)
                zpSDK!!.pageSetup(800, 1700)
                //zpSDK.drawGraphic(0, 0, 0, 0, bmp);
                zpSDK!!.DrawSpecialText(147, 10, PrinterInterface.Textfont.siyuanheiti, 24, "上海市机动车道路停车费", 0, 0, 0) //3
                zpSDK!!.DrawSpecialText(197, 10 + 36, PrinterInterface.Textfont.siyuanheiti, 24, "电子票据告知书", 0, 0, 0) //3
                drawText(10 + 36 + 40, 20, "-----------------------------------------------")
                drawText(10 + 36 + 40 + 32, 20, "停车单号   " + printInfo.orderId)
                drawText(10 + 36 + 40 + 32 + 32, 20, "车牌号码   " + printInfo.plateId)
                if (printInfo.roadId.length <= 20) {
                    drawText(yLocation, 20, "路段名称   " + printInfo.roadId)
                } else if (printInfo.roadId.length > 20 && printInfo.roadId.length <= 40) {
                    drawText(yLocation, 20, "路段名称   " + printInfo.roadId.substring(0, 20))
                    yLocation += 32
                    drawText(yLocation, 20, "           " + printInfo.roadId.substring(20))
                } else if (printInfo.roadId.length > 40 && printInfo.roadId.length <= 60) {
                    drawText(yLocation, 20, "路段名称   " + printInfo.roadId.substring(0, 20))
                    yLocation += 32
                    drawText(yLocation, 20, "           " + printInfo.roadId.substring(20, 40))
                    yLocation += 32
                    drawText(yLocation, 20, "           " + printInfo.roadId.substring(40))
                } else {
                    return -2
                }
                drawText(yLocation + 48, 20, "停放时段  ")
                drawText(yLocation + 32, 20, "           " + printInfo.startTime)
                drawText(yLocation + 64, 20, "           " + printInfo.leftTime)
                yLocation += 96
                drawText(yLocation, 20, "缴费金额   " + printInfo.payMoney)
                yLocation += 32
                drawText(yLocation, 20, "-----------------------------------------------")
                yLocation += 36
                drawText(yLocation, 20, "----------------电子票据开具方式----------------")
                yLocation += 36
                drawText(yLocation, 20, "1、扫描下载“上海停车”官方APP、小程序(微信、支付宝")
                yLocation += 36
                zpSDK!!.drawQrCode(65 + 60, yLocation, "https://shtc.jtcx.sh.cn/union.html", 0, 10, 0)
                yLocation += (300 + 18)
                drawText(yLocation, 20, "2、注册您的“上海停车”账号,绑定车牌。")
                yLocation += 36
                drawText(yLocation, 20, "3、在停车缴费---“我要开票”---“道路电子票据”---下")
                yLocation += 36
                drawText(yLocation, 20, "载您的道路停车票据")
                yLocation += 36
                drawText(yLocation, 20, "提示:")
                yLocation += 36
                drawText(yLocation, 20, printInfo.plateId + "的车主(单位)")
                yLocation += 36
                drawText(yLocation, 20, "您(单位)在" + today + "之前，累计有 " + printInfo.oweCount + " 笔道路停车欠费记")
                yLocation += 36
                drawText(yLocation, 20, "录，请您尽快在本市任一道路停车场补缴。（其中，属智慧道")
                yLocation += 36
                drawText(yLocation, 20, "路停车场的欠费，可在智慧道路停车场或者登录“上海停车”")
                yLocation += 36
                drawText(yLocation, 20, "官方APP、小程序查询补缴。）")
                yLocation += 36
                drawText(yLocation, 20, "--------------- 注意事项 ----------------")
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
                    drawText(yLocation + 36, 24, printInfo.company)
                } else {
                    drawText(yLocation, 24, printInfo.company.substring(0, 22))
                    drawText(yLocation + 36, 24, printInfo.company.substring(22))
                }
            }
        }
        //-----------------------------------------------------------------自定义打印内容----------------------------------------------------------------------


        //--------------------------------------------------------------------------------------------------------------------------------------------------
        zpSDK!!.print(0, 0)
        zpSDK!!.printerStatus()
        val a = zpSDK!!.GetStatus()

//        zpSDK.disconnect();
        return 0
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
}
