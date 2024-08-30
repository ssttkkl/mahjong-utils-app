package io.ssttkkl.mahjongutils.app.utils.image

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toAwtImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.FileSystem
import okio.Path.Companion.toPath
import okio.use
import java.io.File
import javax.imageio.ImageIO
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

actual object ImageUtils : CommonImageUtils() {
    actual suspend fun save(imageBitmap: ImageBitmap, title: String): Boolean {
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
            return true
        } else {
            return false
        }
    }
}