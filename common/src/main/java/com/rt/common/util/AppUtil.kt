package com.rt.common.util

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.CalendarContract
import android.text.TextUtils
import android.util.Log
import android.view.View
import com.rt.base.help.ActivityCacheManager
import com.rt.common.realm.RealmUtil
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by huy  on 2022/8/5.
 */
object AppUtil {

    /**
     * 跳转到手机浏览器
     */
    fun goBrowser(url: String?) {
        val uri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        ActivityCacheManager.instance().getCurrentActivity()?.startActivity(intent)
    }

    /**
     * 截图
     *
     * @param v
     * @return
     */
    fun getViewBp(v: View?): Bitmap? {
        if (null == v) {
            return null
        }
        v.isDrawingCacheEnabled = true
        v.buildDrawingCache()
        v.measure(
            View.MeasureSpec.makeMeasureSpec(v.width, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(v.height, View.MeasureSpec.EXACTLY)
        )
        v.layout(v.x.toInt(), v.y.toInt(), v.x.toInt() + v.measuredWidth, v.y.toInt() + v.measuredHeight)
        val b = Bitmap.createBitmap(v.drawingCache, 0, 0, v.measuredWidth, v.measuredHeight)
        v.isDrawingCacheEnabled = false
        v.destroyDrawingCache()
        return b
    }

    fun getGroupSeparator(): Char {
        return DecimalFormatSymbols.getInstance().groupingSeparator
    }

    fun getDecimalSeparator(): Char {
        return DecimalFormatSymbols.getInstance().decimalSeparator
    }

    fun replaceComma(value: String): String {
        return value.replace(getGroupSeparator().toString(), "").replace(getDecimalSeparator().toString(), ".")
    }

    /**
     * 保留N位小数
     */
    fun keepNDecimal(value: Double, N: Int): String {
        var format = "##0."
        for (i in 1..N) {
            format += "0"
        }
        val df = DecimalFormat(format)
        return replaceComma(df.format(value))
    }

    /**
     * 保留N位小数
     */
    fun keepNDecimal(value: String, N: Int): String {
        if (N == 0) {
            val format = "##"
            val df = DecimalFormat(format)
            return replaceComma(df.format(value.toDouble()))
        } else {
            if (value.length - 1 - value.indexOf(".") > N) {
                var format = "##0."
                if (!TextUtils.isEmpty(value)) {
                    for (i in 1..N) {
                        format += "0"
                    }
                } else {
                    return ""
                }
                val df = DecimalFormat(format)
                return replaceComma(df.format(value.toDouble()))
            } else {
                return replaceComma(value)
            }
        }
    }

    /**
     * 保留N位小数，最多maxLength位数
     */
    val maxLength = 7
    fun subStringNDecimal(value: String, N: Int): String {
        if (value.contains(".")) {
            val pointIndex = value.indexOf(".")
            if (N == 0) {
                return value.substring(0, pointIndex)
            } else if (N < 0) {
                return replaceComma(value)
            } else {
                if (pointIndex + 1 + N > value.length) {
                    return replaceComma(value)
                } else {
                    val tempValue = value.substring(0, pointIndex + 1 + N)
                    if (tempValue.length < maxLength) {
                        if (value.length <= maxLength) {
                            return replaceComma(value)
                        } else {
                            return replaceComma(value.substring(0, maxLength))
                        }
                    }
                    return replaceComma(value.substring(0, pointIndex + 1 + N))
                }
            }
        } else {
            return value
        }
    }

    /**
     * 将固定格式转化成时间戳（默认 yyyy-MM-dd HH:mm:ss）
     */
    @SuppressLint("SimpleDateFormat")
    fun getStringToLong(format: String?, dateString: String): Long {
        var format = format
        if (TextUtils.isEmpty(format)) {
            format = "yyyy-MM-dd HH:mm:ss"
        }
        val sdf = SimpleDateFormat(format)
        sdf.timeZone = TimeZone.getTimeZone("GMT")
        return try {
            val date = sdf.parse(dateString)
            date.time
        } catch (e: ParseException) {
            e.printStackTrace()
            0
        }
    }

    //long转String
    fun getLongToString(date: Long, type: String?): String? {
        return SimpleDateFormat(type, Locale.ENGLISH)
            .format(Date(date))
    }

    //防止快速点击
    private var lastClickTime1: Long = 0

    fun isFastClick(interval: Long): Boolean {
        val currentClickTime = System.currentTimeMillis()
        Log.v("lastClickTime3", (currentClickTime - AppUtil.lastClickTime1).toString())
        return if (currentClickTime - lastClickTime1 >= interval) {
            lastClickTime1 = currentClickTime
            false
        } else {
            lastClickTime1 = currentClickTime
            true
        }
    }

    /**
     * 补0
     */
    fun fillZero(value: String): String {
        if (value.length == 1) {
            return "0" + value
        } else {
            return value
        }
    }

//    fun insertCalender(year: Int, month: Int, day: Int, hour: Int, minute: Int, contentResolver: ContentResolver) {
//        // 获取日历实例
//        val beginTime: Calendar = Calendar.getInstance()
//        beginTime.set(year, month, day, hour, minute)
//
//        val endTime: Calendar = Calendar.getInstance()
//        endTime.set(year, month, day, hour, minute)
//
//        val title = "You have a workout to do,Go to the app and begin your challenge." // 设置事件标题
//
//        val cr = contentResolver
//        val values = ContentValues()
//        values.put(CalendarContract.Events.DTSTART, beginTime.timeInMillis)
//        values.put(CalendarContract.Events.DTEND, endTime.timeInMillis)
//        values.put(CalendarContract.Events.TITLE, title)
//        values.put(CalendarContract.Events.CALENDAR_ID, 1)
//        values.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
//        val uri: Uri? = cr.insert(CalendarContract.Events.CONTENT_URI, values)
//
//        // 获取事件 ID
//        val eventID: Long = uri?.lastPathSegment!!.toLong()
//        RealmUtil.instance?.addRealm(CalenderEventIdBean(eventID))
//
//        // 添加提醒
//        val reminderValues = ContentValues()
//        reminderValues.put(CalendarContract.Reminders.EVENT_ID, eventID)
//        reminderValues.put(
//            CalendarContract.Reminders.METHOD,
//            CalendarContract.Reminders.METHOD_ALERT
//        )
//        reminderValues.put(CalendarContract.Reminders.MINUTES, 10)
//        val reminderUri: Uri? = cr.insert(CalendarContract.Reminders.CONTENT_URI, reminderValues)
//    }

    fun deleteCalender(eventId:Long,contentResolver: ContentResolver) {
        // 构建要删除的事件的 URI
        val deleteUri: Uri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventId)
        // 删除事件
        val cr = contentResolver
        val rows = cr.delete(deleteUri, null, null)
    }
}