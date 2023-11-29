package ja.insepector.bxapp.adapter

import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.viewpager.widget.PagerAdapter
import com.blankj.utilcode.util.ImageUtils
import com.github.chrisbanes.photoview.PhotoView
import ja.insepector.base.BaseApplication
import ja.insepector.base.ext.i18n
import ja.insepector.base.help.ActivityCacheManager
import ja.insepector.base.util.ToastUtil
import ja.insepector.common.util.GlideUtils
import ja.insepector.bxapp.R
import java.util.ArrayList

class SamplePagerAdapter(
    var onClickListener: View.OnClickListener?,
    var list: ArrayList<String>?
) : PagerAdapter() {
    var saveBuilder: AlertDialog.Builder? = null
    var bitmap: Bitmap? = null
    var drawable: Drawable? = null
    private val cacheView: View? = null

    private val onDismissListener = DialogInterface.OnDismissListener {
        if (null != cacheView) {
            cacheView.isEnabled = true
        }
    }
    var onLongClickListener: View.OnLongClickListener? = View.OnLongClickListener { v ->
        drawable = (v as PhotoView).drawable
        if (drawable is BitmapDrawable) {
            bitmap = (drawable as BitmapDrawable).bitmap
            saveBuilder!!.show()
        }
        true
    }

    init {
        saveBuilder =
            ActivityCacheManager.instance().getCurrentActivity()?.let { AlertDialog.Builder(it) }
        saveBuilder!!.setMessage(i18n(ja.insepector.base.R.string.保存图片))
        saveBuilder!!.setPositiveButton(i18n(ja.insepector.base.R.string.确定)) { dialog, _ ->
            dialog.dismiss()
            ImageUtils.save2Album(bitmap, Bitmap.CompressFormat.JPEG)
            i18n(ja.insepector.base.R.string.保存成功).let { ToastUtil.showToast(it) }
        }
        saveBuilder!!.setOnDismissListener(onDismissListener)
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val convertView =
            View.inflate(BaseApplication.instance(), R.layout.item_preview_img, null)
        if (list != null) {
            var url = ""
            url = list!![position]
            GlideUtils.instance?.loadImagePreview(convertView.findViewById(R.id.pv_img), url)
        }
        container.addView(
            convertView,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        convertView.findViewById<PhotoView>(R.id.pv_img)
            .setOnLongClickListener(onLongClickListener)
        convertView.findViewById<PhotoView>(R.id.pv_img).setOnClickListener(onClickListener)
        return convertView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        val contentView = `object` as View
        container.removeView(contentView)
        if (`object` is PhotoView) {
            val s = `object`
            val bitmapDrawable = s.drawable as BitmapDrawable
            val bm = bitmapDrawable.bitmap
            if (bm != null && !bm.isRecycled) {
                s.setImageResource(0)
                bm.recycle()
            }
        }
    }

    override fun getCount(): Int {
        return if (list == null) 1 else list!!.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

}