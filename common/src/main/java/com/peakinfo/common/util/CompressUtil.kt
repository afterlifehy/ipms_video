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
    ): ByteArray? {
        // 1. 第一步：精确缩放到目标尺寸
        val exactBitmap = if (bitmap.width == targetWidth && bitmap.height == targetHeight) {
            bitmap
        } else {
            Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true)
        }

        val outputStream = ByteArrayOutputStream()
        var bestQuality = 0
        var low = 10
        var high = 100

        // 二分查找最优quality
        while (low <= high) {
            val mid = (low + high) / 2
            outputStream.reset()
            exactBitmap.compress(Bitmap.CompressFormat.JPEG, mid, outputStream)
            val sizeKb = outputStream.size() / 1024

            if (sizeKb <= maxFileSizeKb * 0.5) {
                // 当前质量满足要求，尝试更高画质
                bestQuality = mid
                low = mid + 1
            } else {
                // 文件太大，降低画质
                high = mid - 1
            }
        }

        // 兜底：如果没有找到可用quality（最低10还超标）
        if (bestQuality < 10) {
            outputStream.reset()
            exactBitmap.compress(Bitmap.CompressFormat.JPEG, 10, outputStream)
        } else {
            outputStream.reset()
            exactBitmap.compress(Bitmap.CompressFormat.JPEG, bestQuality, outputStream)
        }

        val compressedBytes = outputStream.toByteArray()

        // 回收临时缩放bitmap
        if (exactBitmap !== bitmap) {
            exactBitmap.recycle()
        }

        return compressedBytes
    }
}