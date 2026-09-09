package com.peakinfo.common.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream

object CompressUtil {

    /**
     * 将 Bitmap 压缩至指定尺寸，并确保转换为字节后小于指定 KB
     * @param bitmap 原始 Bitmap
     * @param targetWidth 目标宽度 (如 1440)
     * @param targetHeight 目标高度 (如 1920)
     * @param maxFileSizeKb 最大文件大小限制 (1024 即 1M)
     * @return 压缩后的 Bitmap，失败返回 null
     */
    fun compressBitmapToTargetSize(
        bitmap: Bitmap,
        targetWidth: Int,
        targetHeight: Int,
        maxFileSizeKb: Int = 1024
    ): Bitmap? {
        // 1. 第一步：精确缩放到目标尺寸
        val exactBitmap = if (bitmap.width == targetWidth && bitmap.height == targetHeight) {
            bitmap
        } else {
            Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true)
        }

        // 2. 第二步：质量压缩，直到小于目标大小
        var quality = 90
        val outputStream = ByteArrayOutputStream()
        while (quality > 10) {
            outputStream.reset()
            exactBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            val sizeKb = outputStream.size() / 1024
            if (sizeKb <= maxFileSizeKb) {
                break
            }
            quality -= 10 // 每次降低 10 档画质
        }

        // 3. 将压缩后的字节流重新解码为 Bitmap 返回
        val compressedBytes = outputStream.toByteArray()

        // 如果生成了新的缩放图，回收它以释放内存
        if (exactBitmap !== bitmap) {
            exactBitmap.recycle()
        }

        return BitmapFactory.decodeByteArray(compressedBytes, 0, compressedBytes.size)
    }
}