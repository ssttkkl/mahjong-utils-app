package io.ssttkkl.mahjongutils.app.utils.image

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toAwtImage
import io.ssttkkl.mahjongutils.app.components.appscaffold.AppState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.use
import java.awt.Desktop
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter


actual class SaveResult(val file: File) {
    actual val isSupportOpen: Boolean
        get() = true
    actual val isSupportShare: Boolean
        get() = false

    actual suspend fun open() {
        withContext(Dispatchers.IO) {
            try {
                Desktop.getDesktop().open(file)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    actual suspend fun share() {
    }

}

actual object ImageUtils : CommonImageUtils() {
    actual suspend fun save(appState: AppState, imageBitmap: ImageBitmap, title: String): SaveResult? {
        // 弹出文件选择框
        val fileChooser = JFileChooser().apply {
            dialogTitle = "Save File"
            // 设置文件选择模式为保存文件模式
            fileSelectionMode = JFileChooser.FILES_ONLY
            fileFilter = FileNameExtensionFilter("PNG Image", "png")
            selectedFile = File("${title}.png")
        }

        // 显示保存文件对话框
        val userSelection = fileChooser.showSaveDialog(null)

        // 如果用户选择了文件
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            val fileToSave = fileChooser.selectedFile
            withContext(Dispatchers.Default) {
                fileToSave.outputStream().use { sink ->
                    ImageIO.write(imageBitmap.toAwtImage(), "png", sink)
                }
            }
            return SaveResult(fileToSave)
        } else {
            return null
        }
    }
}