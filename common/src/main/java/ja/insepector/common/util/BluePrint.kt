package ja.insepector.common.util

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.view.Gravity
import android.widget.Toast
import com.blankj.utilcode.util.ToastUtils
import ja.insepector.base.BaseApplication
import ja.insepector.base.help.ActivityCacheManager
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
        val json: JSONArray? = null
        //连接结果
        val conn_result: String? = null
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
        var yLocation1 = 0
        val now = Calendar.getInstance()
        val today = now[Calendar.YEAR].toString() + "年" + (now[Calendar.MONTH] + 1) + "月" + now[Calendar.DAY_OF_MONTH] + "日"
        if (printText !== "" && printText != null) {
            val receipt = printText.contains("receipt")
            if (receipt) {
                printText = printText.substring(8)
            }
            val arr = printText.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            var ystart = 10 + 60 + 40 + 40
            if (receipt) {
                if (arr.size >= 10 && "totalAmountundefined" == arr[9]) {
                    arr[9] = "0"
                }
                if (arr.size >= 7 && "beRecoveredMoneyundefined" == arr[6]) {
                    arr[6] = "0"
                }
                if (arr.size >= 8 && "totalRecoveredMoneyundefined" == arr[7]) {
                    arr[7] = "0"
                }
                if (arr.size >= 9 && "autonomousPayCountundefined" == arr[8]) {
                    arr[8] = "0"
                }
                if (zpSDK == null) {
                    return -1
                }
                zpSDK!!.pageSetup(800, 700)
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
                zpSDK!!.DrawSpecialText(
                    20,
                    10 + 60 + 40,
                    PrinterInterface.Textfont.siyuanheiti,
                    27,
                    "登录账号:                  " + arr[0],
                    0,
                    0,
                    0
                ) //3
                //zpSDK.DrawSpecialText(20, 10+60+40+40, PrinterInterface.Textfont.siyuanheiti,27, "签到时间:                   " + arr[1], 0, 0, 0);//3
                if (printText.contains("payMoneyToday")) {
                    zpSDK!!.DrawSpecialText(
                        20,
                        ystart,
                        PrinterInterface.Textfont.siyuanheiti,
                        27,
                        "① 今日总收费:             " + arr[3].replace("payMoneyToday", "") + " 元",
                        0,
                        0,
                        0
                    )
                    //ystart = ystart;
                }
                if (printText.contains("orderTotalToday")) {
                    zpSDK!!.DrawSpecialText(
                        20,
                        ystart + 40,
                        PrinterInterface.Textfont.siyuanheiti,
                        27,
                        "② 今日订单数:             " + arr[4].replace("orderTotalToday", "") + " 笔",
                        0,
                        0,
                        0
                    )
                    ystart = ystart + 40
                }
                if (printText.contains("unclearedTotal")) {
                    zpSDK!!.DrawSpecialText(
                        20,
                        ystart + 40,
                        PrinterInterface.Textfont.siyuanheiti,
                        27,
                        "③ 欠费订单数:             " + arr[5].replace("unclearedTotal", "") + " 笔",
                        0,
                        0,
                        0
                    )
                    ystart = ystart + 40
                }
                if (printText.contains("payMoneyTotal")) {
                    zpSDK!!.DrawSpecialText(
                        20,
                        ystart + 40,
                        PrinterInterface.Textfont.siyuanheiti,
                        27,
                        "④ 总收入:                 " + arr[6].replace("payMoneyTotal", "") + " 元",
                        0,
                        0,
                        0
                    )
                    ystart = ystart + 40
                }
                if (printText.contains("orderTotal")) {
                    zpSDK!!.DrawSpecialText(
                        20,
                        ystart + 40,
                        PrinterInterface.Textfont.siyuanheiti,
                        27,
                        "⑤ 已下单:                 " + arr[7].replace("orderTotal", "") + " 笔",
                        0,
                        0,
                        0
                    )
                    ystart = ystart + 40
                }
                zpSDK!!.DrawSpecialText(
                    20,
                    ystart + 40,
                    PrinterInterface.Textfont.siyuanheiti,
                    27,
                    "打印时间:                  " + arr[2],
                    0,
                    0,
                    0
                )
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
                val a = zpSDK!!.GetStatus()
                //                zpSDK.disconnect();
                return 0
            }
            //-----------------------------------------------------------------自定义打印内容----------------------------------------------------------------------
            if ("undefined" == arr[6]) {
                arr[6] = "**"
            } else if ("undefined" == arr[7]) {
                arr[7] = "**"
            } else if ("undefined" == arr[8]) {
                arr[8] = "**"
            } else if ("undefined" == arr[9]) {
                arr[9] = "**"
            }
            zpSDK!!.pageSetup(800, 1600)
            //zpSDK.drawGraphic(0, 0, 0, 0, bmp);
            zpSDK!!.DrawSpecialText(147, 10, PrinterInterface.Textfont.siyuanheiti, 24, "上海市机动车道路停车费", 0, 0, 0) //3
            zpSDK!!.DrawSpecialText(197, 10 + 36, PrinterInterface.Textfont.siyuanheiti, 24, "电子票据告知书", 0, 0, 0) //3
            zpSDK!!.DrawSpecialText(
                25,
                10 + 36 + 40,
                PrinterInterface.Textfont.siyuanheiti,
                20,
                "-----------------------------------------------",
                0,
                1,
                0
            )
            zpSDK!!.DrawSpecialText(25, 10 + 36 + 40 + 32, PrinterInterface.Textfont.siyuanheiti, 20, "停车单号   " + arr[0], 0, 0, 0)
            zpSDK!!.DrawSpecialText(25, 10 + 36 + 40 + 32 + 32, PrinterInterface.Textfont.siyuanheiti, 20, "车牌号码   " + arr[1], 0, 0, 0)
            if (arr[2].length <= 20) {
                zpSDK!!.DrawSpecialText(25, yLocation, PrinterInterface.Textfont.siyuanheiti, 20, "路段名称   " + arr[2], 0, 0, 0)
            } else if (arr[2].length > 20 && arr[2].length <= 40) {
                zpSDK!!.DrawSpecialText(
                    25,
                    yLocation,
                    PrinterInterface.Textfont.siyuanheiti,
                    20,
                    "路段名称   " + arr[2].substring(0, 20),
                    0,
                    0,
                    0
                )
                zpSDK!!.DrawSpecialText(
                    25,
                    yLocation + 32,
                    PrinterInterface.Textfont.siyuanheiti,
                    20,
                    "           " + arr[2].substring(20),
                    0,
                    0,
                    0
                )
                yLocation = yLocation + 32
            } else if (arr[2].length > 40 && arr[2].length <= 60) {
                zpSDK!!.DrawSpecialText(
                    25,
                    yLocation,
                    PrinterInterface.Textfont.siyuanheiti,
                    20,
                    "路段名称   " + arr[2].substring(0, 20),
                    0,
                    0,
                    0
                )
                zpSDK!!.DrawSpecialText(
                    25,
                    yLocation + 32,
                    PrinterInterface.Textfont.siyuanheiti,
                    20,
                    "           " + arr[2].substring(20, 40),
                    0,
                    0,
                    0
                )
                zpSDK!!.DrawSpecialText(
                    25,
                    yLocation + 64,
                    PrinterInterface.Textfont.siyuanheiti,
                    20,
                    "           " + arr[2].substring(40),
                    0,
                    0,
                    0
                )
                yLocation = yLocation + 64
            } else {
                return -2
            }
            zpSDK!!.DrawSpecialText(25, yLocation + 48, PrinterInterface.Textfont.siyuanheiti, 20, "停放时段  ", 0, 0, 0)
            zpSDK!!.DrawSpecialText(25, yLocation + 32, PrinterInterface.Textfont.siyuanheiti, 20, "           " + arr[3], 0, 0, 0)
            zpSDK!!.DrawSpecialText(25, yLocation + 64, PrinterInterface.Textfont.siyuanheiti, 20, "           " + arr[4], 0, 0, 0)
            zpSDK!!.DrawSpecialText(25, yLocation + 64 + 32, PrinterInterface.Textfont.siyuanheiti, 20, "缴费金额   " + arr[5], 0, 0, 0)
            zpSDK!!.DrawSpecialText(
                25,
                yLocation + 64 + 32 + 32,
                PrinterInterface.Textfont.siyuanheiti,
                20,
                "-----------------------------------------------",
                0,
                1,
                0
            )
            zpSDK!!.DrawSpecialText(
                25,
                yLocation + 64 + 32 + 32 + 36,
                PrinterInterface.Textfont.siyuanheiti,
                20,
                "----------------电子票据开具方式----------------",
                0,
                0,
                0
            )
            zpSDK!!.DrawSpecialText(
                25,
                yLocation + 64 + 32 + 32 + 36 + 36,
                PrinterInterface.Textfont.siyuanheiti,
                20,
                "1、扫描下载“上海停车”官方APP、小程序(微信、支付宝)",
                0,
                0,
                0
            )
            zpSDK!!.drawQrCode(65 + 60, yLocation + 64 + 32 + 32 + 36 + 36 + 36, "https://shtc.jtcx.sh.cn/union.html", 0, 10, 0)
            zpSDK!!.DrawSpecialText(
                25,
                yLocation + 64 + 32 + 32 + 36 + 36 + 36 + 300 + 18,
                PrinterInterface.Textfont.siyuanheiti,
                20,
                "2、注册您的“上海停车”账号,绑定车牌。",
                0,
                0,
                0
            )
            zpSDK!!.DrawSpecialText(
                25,
                yLocation + 64 + 32 + 32 + 36 + 36 + 36 + 300 + 18 + 36,
                PrinterInterface.Textfont.siyuanheiti,
                20,
                "3、在停车缴费---“我要开票”---“道路电子票据”---下",
                0,
                0,
                0
            )
            zpSDK!!.DrawSpecialText(
                25,
                yLocation + 64 + 32 + 32 + 36 + 36 + 36 + 300 + 18 + 36 + 36,
                PrinterInterface.Textfont.siyuanheiti,
                20,
                "载您的道路停车票据",
                0,
                0,
                0
            )
            zpSDK!!.DrawSpecialText(
                25,
                yLocation + 64 + 32 + 32 + 36 + 36 + 36 + 300 + 18 + 36 + 36 + 36,
                PrinterInterface.Textfont.siyuanheiti,
                20,
                "提示:",
                0,
                0,
                0
            )
            zpSDK!!.DrawSpecialText(
                25,
                yLocation + 64 + 32 + 32 + 36 + 36 + 36 + 300 + 18 + 36 + 36 + 36 + 36,
                PrinterInterface.Textfont.siyuanheiti,
                20,
                arr[1] + "的车主(单位)",
                0,
                0,
                0
            )
            zpSDK!!.DrawSpecialText(
                25,
                yLocation + 64 + 32 + 32 + 36 + 36 + 36 + 300 + 18 + 36 + 36 + 36 + 36 + 36,
                PrinterInterface.Textfont.siyuanheiti,
                20,
                "您(单位)在" + today + "之前，累计有 " + arr[6] + " 笔道路停车欠费记",
                0,
                0,
                0
            )
            zpSDK!!.DrawSpecialText(
                25,
                yLocation + 64 + 32 + 32 + 36 + 36 + 36 + 300 + 18 + 36 + 36 + 36 + 36 + 36 + 36,
                PrinterInterface.Textfont.siyuanheiti,
                20,
                "录，请您(单位)登录“上海停车”官方APP、小程序(微信、",
                0,
                0,
                0
            )
            zpSDK!!.DrawSpecialText(
                25,
                yLocation + 64 + 32 + 32 + 36 + 36 + 36 + 300 + 18 + 36 + 36 + 36 + 36 + 36 + 36 + 36,
                PrinterInterface.Textfont.siyuanheiti,
                20,
                "支付宝)尽快补缴。",
                0,
                0,
                0
            )
            zpSDK!!.DrawSpecialText(
                25,
                yLocation + 64 + 32 + 32 + 36 + 36 + 36 + 300 + 18 + 36 + 36 + 36 + 36 + 36 + 36 + 36 + 36,
                PrinterInterface.Textfont.siyuanheiti,
                24,
                "--------------- 注意事项 ----------------",
                0,
                0,
                0
            )
            zpSDK!!.DrawSpecialText(
                25,
                yLocation + 64 + 32 + 32 + 36 + 36 + 36 + 300 + 18 + 36 + 36 + 36 + 36 + 36 + 36 + 36 + 36 + 36,
                PrinterInterface.Textfont.siyuanheiti,
                20,
                "1、本告知书仅为您(单位)本次停车付费的凭证，不作为电子",
                0,
                0,
                0
            )
            zpSDK!!.DrawSpecialText(
                25,
                yLocation + 64 + 32 + 32 + 36 + 36 + 36 + 300 + 18 + 36 + 36 + 36 + 36 + 36 + 36 + 36 + 36 + 36 + 36,
                PrinterInterface.Textfont.siyuanheiti,
                20,
                "   票据。",
                0,
                0,
                0
            )
            zpSDK!!.DrawSpecialText(
                25,
                yLocation + 64 + 32 + 32 + 36 + 36 + 36 + 300 + 18 + 36 + 36 + 36 + 36 + 36 + 36 + 36 + 36 + 36 + 36 + 36,
                PrinterInterface.Textfont.siyuanheiti,
                20,
                "2、如需要核实有关停车收费情况请致电 " + arr[7] + " 。",
                0,
                0,
                0
            )
            zpSDK!!.DrawSpecialText(
                25,
                yLocation + 64 + 32 + 32 + 36 + 36 + 36 + 300 + 18 + 36 + 36 + 36 + 36 + 36 + 36 + 36 + 36 + 36 + 36 + 36 + 36,
                PrinterInterface.Textfont.siyuanheiti,
                20,
                "3、如您需要电子票据的,请在即日起30天内，通过“上海停",
                0,
                0,
                0
            )
            zpSDK!!.DrawSpecialText(
                25,
                yLocation + 64 + 32 + 32 + 36 + 36 + 36 + 300 + 18 + 36 + 36 + 36 + 36 + 36 + 36 + 36 + 36 + 36 + 36 + 36 + 36 + 36,
                PrinterInterface.Textfont.siyuanheiti,
                20,
                "   车”官方APP、小程序(微信、支付宝)下载。如您(单位)",
                0,
                0,
                0
            )
            zpSDK!!.DrawSpecialText(
                25,
                yLocation + 64 + 32 + 32 + 36 + 36 + 36 + 300 + 18 + 36 + 36 + 36 + 36 + 36 + 36 + 36 + 36 + 36 + 36 + 36 + 36 + 36 + 36,
                PrinterInterface.Textfont.siyuanheiti,
                20,
                "   在下载电子票据过程中遇到问题，请将问题描述和您的",
                0,
                0,
                0
            )
            zpSDK!!.DrawSpecialText(
                25,
                yLocation + 64 + 32 + 32 + 36 + 36 + 36 + 300 + 18 + 36 + 36 + 36 + 36 + 36 + 36 + 36 + 36 + 36 + 36 + 36 + 36 + 36 + 36 + 36,
                PrinterInterface.Textfont.siyuanheiti,
                20,
                "   姓名、电话等有效的联系方式反馈至邮箱service@shtc",
                0,
                0,
                0
            )
            zpSDK!!.DrawSpecialText(
                25,
                yLocation + 64 + 32 + 32 + 36 + 36 + 36 + 300 + 18 + 36 + 36 + 36 + 36 + 36 + 36 + 36 + 36 + 36 + 36 + 36 + 36 + 36 + 36 + 36 + 36,
                PrinterInterface.Textfont.siyuanheiti,
                20,
                "   xx.com",
                0,
                0,
                0
            )
            zpSDK!!.DrawSpecialText(
                25,
                yLocation + 64 + 32 + 32 + 36 + 36 + 36 + 300 + 18 + 36 + 36 + 36 + 36 + 36 + 36 + 36 + 36 + 36 + 36 + 36 + 36 + 36 + 36 + 36 + 36 + 36,
                PrinterInterface.Textfont.siyuanheiti,
                20,
                "-----------------------------------------------",
                0,
                1,
                0
            )
            yLocation1 = if (arr[8].length <= 22) {
                zpSDK!!.DrawSpecialText(
                    25,
                    yLocation + 64 + 32 + 32 + 36 + 36 + 36 + 300 + 18 + 36 + 36 + 36 + 36 + 36 + 36 + 36 + 36 + 36 + 36 + 36 + 36 + 36 + 36 + 36 + 36 + 36 + 36,
                    PrinterInterface.Textfont.siyuanheiti,
                    24,
                    arr[8],
                    0,
                    0,
                    0
                )
                64 + 32 + 32 + 36 + 36 + 36 + 300 + 36 + 36 + 36 + 36 + 36 + 36 + 36 + 36 + 36 + 36 + 18 + 36 + 36 + 36 + 36 + 36 + 36 + 36 + 36
            } else {
                zpSDK!!.DrawSpecialText(
                    25,
                    yLocation + 64 + 32 + 32 + 36 + 36 + 36 + 300 + 18 + 36 + 36 + 36 + 36 + 36 + 36 + 36 + 36 + 36 + 36 + 36 + 36 + 36 + 36 + 36 + 36 + 36 + 36,
                    PrinterInterface.Textfont.siyuanheiti,
                    24,
                    arr[8].substring(0, 22),
                    0,
                    0,
                    0
                )
                zpSDK!!.DrawSpecialText(
                    25,
                    yLocation + 64 + 32 + 32 + 36 + 36 + 36 + 300 + 18 + 36 + 36 + 36 + 36 + 36 + 36 + 36 + 36 + 36 + 36 + 36 + 36 + 36 + 36 + 36 + 36 + 36 + 36 + 36,
                    PrinterInterface.Textfont.siyuanheiti,
                    24,
                    arr[8].substring(22),
                    0,
                    0,
                    0
                )
                64 + 32 + 32 + 36 + 36 + 36 + 300 + 18 + 36 + 36 + 36 + 36 + 36 + 36 + 36 + 36 + 36 + 36 + 36 + 36 + 36 + 36 + 36 + 36 + 36 + 36 + 36
            }
            if (arr[9].length <= 22) {
                zpSDK!!.DrawSpecialText(25, yLocation + yLocation1 + 36, PrinterInterface.Textfont.siyuanheiti, 24, arr[9], 0, 0, 0)
            } else {
                zpSDK!!.DrawSpecialText(
                    25,
                    yLocation + yLocation1 + 36,
                    PrinterInterface.Textfont.siyuanheiti,
                    24,
                    arr[9].substring(0, 22),
                    0,
                    0,
                    0
                )
                zpSDK!!.DrawSpecialText(
                    25,
                    yLocation + yLocation1 + 36 + 36,
                    PrinterInterface.Textfont.siyuanheiti,
                    24,
                    arr[9].substring(22),
                    0,
                    0,
                    0
                )
            }
        }


        //--------------------------------------------------------------------------------------------------------------------------------------------------
        zpSDK!!.print(0, 0)
        zpSDK!!.printerStatus()
        val a = zpSDK!!.GetStatus()

//        zpSDK.disconnect();
        return 0
    }
}
