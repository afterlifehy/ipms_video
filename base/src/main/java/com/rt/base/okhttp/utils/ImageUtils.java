//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.rt.base.okhttp.utils;

import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import java.io.InputStream;
import java.lang.reflect.Field;

public class ImageUtils {
    public ImageUtils() {
    }

    public static ImageSize getImageSize(InputStream imageStream) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(imageStream, (Rect)null, options);
        return new ImageSize(options.outWidth, options.outHeight);
    }

    public static int calculateInSampleSize(ImageSize srcSize, ImageSize targetSize) {
        int width = srcSize.width;
        int height = srcSize.height;
        int inSampleSize = 1;
        int reqWidth = targetSize.width;
        int reqHeight = targetSize.height;
        if (width > reqWidth && height > reqHeight) {
            int widthRatio = Math.round((float)width / (float)reqWidth);
            int heightRatio = Math.round((float)height / (float)reqHeight);
            inSampleSize = Math.max(widthRatio, heightRatio);
        }

        return inSampleSize;
    }

    public static ImageSize getImageViewSize(View view) {
        ImageSize imageSize = new ImageSize();
        imageSize.width = getExpectWidth(view);
        imageSize.height = getExpectHeight(view);
        return imageSize;
    }

    private static int getExpectHeight(View view) {
        int height = 0;
        if (view == null) {
            return 0;
        } else {
            ViewGroup.LayoutParams params = view.getLayoutParams();
            if (params != null && params.height != -2) {
                height = view.getWidth();
            }

            if (height <= 0 && params != null) {
                height = params.height;
            }

            if (height <= 0) {
                height = getImageViewFieldValue(view, "mMaxHeight");
            }

            if (height <= 0) {
                DisplayMetrics displayMetrics = view.getContext().getResources().getDisplayMetrics();
                height = displayMetrics.heightPixels;
            }

            return height;
        }
    }

    private static int getExpectWidth(View view) {
        int width = 0;
        if (view == null) {
            return 0;
        } else {
            ViewGroup.LayoutParams params = view.getLayoutParams();
            if (params != null && params.width != -2) {
                width = view.getWidth();
            }

            if (width <= 0 && params != null) {
                width = params.width;
            }

            if (width <= 0) {
                width = getImageViewFieldValue(view, "mMaxWidth");
            }

            if (width <= 0) {
                DisplayMetrics displayMetrics = view.getContext().getResources().getDisplayMetrics();
                width = displayMetrics.widthPixels;
            }

            return width;
        }
    }

    private static int getImageViewFieldValue(Object object, String fieldName) {
        int value = 0;

        try {
            Field field = ImageView.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            int fieldValue = field.getInt(object);
            if (fieldValue > 0 && fieldValue < Integer.MAX_VALUE) {
                value = fieldValue;
            }
        } catch (Exception var5) {
        }

        return value;
    }

    public static class ImageSize {
        int width;
        int height;

        public ImageSize() {
        }

        public ImageSize(int width, int height) {
            this.width = width;
            this.height = height;
        }

        public String toString() {
            return "ImageSize{width=" + this.width + ", height=" + this.height + '}';
        }
    }
}
