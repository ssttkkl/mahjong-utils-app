package io.ssttkkl.mahjongutils.app.base.utils.image

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import io.ssttkkl.mahjongutils.app.components.appscaffold.AppState
import io.ssttkkl.mahjongutils.app.components.appscaffold.activity
import io.ssttkkl.mahjongutils.app.base.utils.ActivityHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


actual class SaveResult(val uri: Uri, val title: String) {
    actual val isSupportOpen: Boolean
        get() = true
    actual val isSupportShare: Boolean
        get() = true

    actual suspend fun open() {
        // 创建Intent以打开图片
        val intent = Intent().apply {
            action = Intent.ACTION_VIEW
            setDataAndType(uri, "image/png") // 设置MIME类型
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION // 给予读权限
        }

        // 启动Intent
        ActivityHelper.currentActivity?.startActivity(intent)
    }

    actual suspend fun share() {
        // 创建分享意图
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, uri)  // 附加 URI
            type = "image/png"  // 或 "image/jpeg" 根据实际情况
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION // 给予读权限
        }

        // 启动分享意图
        ActivityHelper.currentActivity?.startActivity(Intent.createChooser(shareIntent, title))
    }
}

actual object ImageSaver {
    actual suspend fun save(
        appState: AppState,
        imageBitmap: ImageBitmap,
        title: String
    ): SaveResult? {
        val activity = appState.activity

        return withContext(Dispatchers.IO) {
            val values = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, title)
                put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }

            val uri: Uri = activity.contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                values
            ) ?: return@withContext null

            try {
                activity.contentResolver.openOutputStream(uri)?.use {
                    imageBitmap.asAndroidBitmap().compress(Bitmap.CompressFormat.PNG, 100, it)
                } ?: return@withContext null
                values.clear()
                values.put(MediaStore.Images.Media.IS_PENDING, 0)
                activity.contentResolver.update(uri, values, null, null)
            } catch (e: Exception) {
                e.printStackTrace()
                return@withContext null
            }
            return@withContext SaveResult(uri, title)
        }
    }
}