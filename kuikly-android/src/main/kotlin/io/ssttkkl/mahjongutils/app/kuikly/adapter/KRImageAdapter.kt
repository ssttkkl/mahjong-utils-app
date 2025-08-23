package io.ssttkkl.mahjongutils.app.kuikly.adapter

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.DrawableWrapper
import android.os.Build
import android.util.Base64
import android.util.Log
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.tencent.kuikly.core.render.android.adapter.HRImageLoadOption
import com.tencent.kuikly.core.render.android.adapter.IKRImageAdapter
import io.ssttkkl.mahjongutils.app.base.utils.AppInstance
import java.util.concurrent.Executors
import kotlin.math.roundToInt

class KRImageAdapter(val context: Context) : IKRImageAdapter {

    override fun fetchDrawable(
        imageLoadOption: HRImageLoadOption,
        callback: (drawable: Drawable?) -> Unit,
    ) {
        if (imageLoadOption.isBase64()) {
            loadFromBase64(imageLoadOption, callback)
        } else if (imageLoadOption.isWebUrl() || imageLoadOption.isAssets() || imageLoadOption.isFile()) {
            // http/assets/file 图片使用 glide 加载
            requestImage(imageLoadOption, callback)
        } else if(imageLoadOption.src.startsWith("drawable-res://")) {
            loadFromDrawableResId(imageLoadOption, callback)
        }
    }

    private fun requestImage(
        imageLoadOption: HRImageLoadOption,
        callback: (drawable: Drawable?) -> Unit,
    ) {
        val src = if (imageLoadOption.isAssets()) {
            val assetPath = imageLoadOption.src.substring(HRImageLoadOption.SCHEME_ASSETS.length)
            "file:///android_asset/$assetPath"
        } else {
            imageLoadOption.src
        }

        val requestBuilder = if (src.endsWith(".gif")) {
            Glide.with(AppInstance.app)
                .asGif()
                .load(src) as RequestBuilder<Drawable>
        } else {
            Glide.with(AppInstance.app)
                .asDrawable()
                .load(src)
        }

        if (imageLoadOption.needResize) {
            requestBuilder.override(imageLoadOption.requestWidth, imageLoadOption.requestHeight)
            when (imageLoadOption.scaleType) {
                ImageView.ScaleType.CENTER_CROP -> requestBuilder.centerCrop()
                ImageView.ScaleType.FIT_CENTER -> requestBuilder.fitCenter()
                else -> {}
            }
        }
        requestBuilder
            .into(object : CustomTarget<Drawable>() {

                override fun onLoadCleared(placeholder: Drawable?) {
                    callback.invoke(null)
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    super.onLoadFailed(errorDrawable)
                    callback.invoke(null)
                }

                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable>?,
                ) {
                    val newDrawable = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        DensityAwareDrawable(context, resource)
                    } else {
                        // 这里接入方可以自己实现响应Density的Drawable
                        resource
                    };
                    callback.invoke(newDrawable)
                }
            })
    }

    private fun loadFromDrawableResId(
        imageLoadOption: HRImageLoadOption,
        callback: (drawable: Drawable?) -> Unit,
    ) {
        execOnSubThread {
            try {
                val drawableResId = imageLoadOption.src.replace("drawable-res://", "").toInt()
                val drawable = ContextCompat.getDrawable(context, drawableResId)

                if (drawable == null) {
                    callback.invoke(null)
                    return@execOnSubThread
                }

                if (imageLoadOption.needResize && imageLoadOption.requestWidth > 0 && imageLoadOption.requestHeight > 0) {
                    val width = imageLoadOption.requestWidth
                    val height = imageLoadOption.requestHeight

                    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                    val canvas = android.graphics.Canvas(bitmap)
                    drawable.setBounds(0, 0, canvas.width, canvas.height)
                    drawable.draw(canvas)
                    callback.invoke(BitmapDrawable(context.resources, bitmap))
                } else {
                    callback.invoke(drawable)
                }
            } catch (e: Exception) {
                Log.d("KRImageAdapter", "loadFromDrawableResId failed: $e")
                callback.invoke(null)
            }
        }
    }

    private fun loadFromBase64(
        imageLoadOption: HRImageLoadOption,
        callback: (drawable: Drawable?) -> Unit,
    ) {
        execOnSubThread {
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            val bytes = Base64.decode(imageLoadOption.src.split(",")[1], Base64.DEFAULT)
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)
            try {
                options.inPreferredConfig = Bitmap.Config.ARGB_8888
                options.inJustDecodeBounds = false
                try {
                    options.inSampleSize = calculateInSampleSize(
                        options,
                        imageLoadOption.requestWidth,
                        imageLoadOption.requestHeight
                    )
                } catch (e: ArithmeticException) { // 偶现报除以0，可能是inSampleSize超过int的范围溢出了。这里catch兜底使用原始inSampleSize
                    Log.d("ECHRImageAdapter", "loadFromBase64: $e")
                }
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)
                callback.invoke(BitmapDrawable(Resources.getSystem(), bitmap))
            } catch (e: OutOfMemoryError) {
                Log.d("ECHRImageAdapter", "oom happen: $e")
            }
        }
    }


    private fun calculateInSampleSize(
        options: BitmapFactory.Options,
        reqWidth: Int,
        reqHeight: Int,
    ): Int {
        return if (reqWidth != 0 && reqHeight != 0 && reqWidth != -1 && reqHeight != -1) {
            var height = options.outHeight
            var width = options.outWidth
            var inSampleSize: Int
            inSampleSize = 1
            while (height > reqHeight && width > reqWidth) {
                val heightRatio = (height.toFloat() / reqHeight.toFloat()).roundToInt()
                val widthRatio = (width.toFloat() / reqWidth.toFloat()).roundToInt()
                val ratio = if (heightRatio > widthRatio) heightRatio else widthRatio
                if (ratio < 2) {
                    break
                }
                width = width shr 1
                height = height shr 1
                inSampleSize = inSampleSize shl 1
            }
            inSampleSize
        } else {
            1
        }
    }

}

/**
 * Glide默认的表现是不处理density缩放的，这一点会导致和iOS，鸿蒙表现不一致
 * 这里统一加上对density处理
 */
@RequiresApi(Build.VERSION_CODES.M)
class DensityAwareDrawable(
    context: Context,
    drawable: Drawable
) : DrawableWrapper(drawable) {

    private val density: Float = context.resources.displayMetrics.density

    override fun getIntrinsicWidth(): Int {
        return (super.getIntrinsicWidth() / density).toInt()
    }

    override fun getIntrinsicHeight(): Int {
        return (super.getIntrinsicHeight() / density).toInt()
    }
}

private val subThreadPoolExecutor by lazy {
    Executors.newFixedThreadPool(2)
}

fun execOnSubThread(runnable: () -> Unit) {
    subThreadPoolExecutor.execute(runnable)
}