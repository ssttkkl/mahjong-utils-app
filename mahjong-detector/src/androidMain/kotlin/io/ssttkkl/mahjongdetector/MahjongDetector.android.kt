package io.ssttkkl.mahjongdetector

import ai.onnxruntime.OnnxJavaType
import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.core.graphics.get
import io.ssttkkl.mahjongdetector.ImagePreprocessor.preprocessImage
import io.ssttkkl.mahjongutils.app.base.utils.AppInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import mahjongutils.models.Tile
import java.nio.FloatBuffer
import java.nio.ShortBuffer


actual object MahjongDetector {
    private const val MODEL_FILENAME = "best.fp16.onnx"

    private var modelLoaded: Boolean = false
    private val modelLoadMutex = Mutex()

    private val env: OrtEnvironment = OrtEnvironment.getEnvironment()
    private lateinit var session: OrtSession

    private suspend fun prepareModel() {
        if (!modelLoaded) {
            modelLoadMutex.withLock {
                if (!modelLoaded) {
                    val bytes = AppInstance.app.assets.open(MODEL_FILENAME)
                        .use { stream ->
                            stream.readBytes()
                        }
                    session = env.createSession(bytes, OrtSession.SessionOptions())
                }
                modelLoaded = true
            }
        }
    }

    fun close() {
        session.close()
        env.close()
    }

    actual suspend fun predict(image: ImageBitmap): List<Tile> =
        withContext(Dispatchers.Default) {
            prepareModel()

            // 预处理图像
            val (preprocessedImage, paddingInfo) = preprocessImage(image)

            // 将预处理后的图像数据转换为 ONNX 张量
            val tensor = createTensor(preprocessedImage.asAndroidBitmap())

            // 执行推理
            val results = session.run(mapOf(session.inputNames.first() to tensor))

            // 获取输出张量 (你需要根据你的模型输出名称调整)
            val output = results.get(0).value as Array<Array<FloatArray>>
            val detections =
                YoloV8PostProcessor.postprocess(output[0], paddingInfo, CLASS_NAME.size)

            // 释放资源
            tensor.close()
            results.close()

            detections.sortedBy { it.x1 }.map { Tile[CLASS_NAME[it.classId]] }
        }

    // 创建ONNX输入Tensor
    private fun createTensor(image: Bitmap): OnnxTensor {
        val width = image.width
        val height = image.height
        val siz = height * width

        // 2. 转换为BGR float数组（OpenCV兼容格式）
        // 使用 ShortBuffer 代替 FloatBuffer 来存储 float16 数据
        val bgrData = ShortArray(3 * siz)
        var p = 0
        for (i in 0 until height) {
            for (j in 0 until width) {
                val pixel = image[i, j]

                val r = (pixel shr 16) and 0xFF
                val g = (pixel shr 8) and 0xFF
                val b = pixel and 0xFF

                bgrData[p] = floatToHalf(r / 255.0f)
                bgrData[siz + p] = floatToHalf(g / 255.0f)
                bgrData[siz * 2 + p] = floatToHalf(b / 255.0f)
                p++
            }
        }

        return OnnxTensor.createTensor(
            env,
            ShortBuffer.wrap(bgrData),
            longArrayOf(1, 3, height.toLong(), width.toLong()), // NCHW格式
            OnnxJavaType.FLOAT16
        )
    }

    // float32 转 float16 的辅助函数
    private fun floatToHalf(f: Float): Short {
        val bits = java.lang.Float.floatToIntBits(f)
        val s = ((bits shr 16) and 0x8000)
        var e = ((bits shr 23) and 0xff) - 127 + 15
        var m = (bits and 0x007fffff)

        if (e < 0) return s.toShort()
        if (e > 30) return (s or 0x7c00).toShort()

        if (e < 15) {
            m = (m or 0x00800000) shr (14 - e)
            if ((m and 0x1000) != 0) m += 0x2000
            return (s or (m shr 13)).toShort()
        }

        if ((m and 0x1000) != 0) {
            m += 0x2000
            if ((m and 0x800000) != 0) {
                m = 0
                e += 1
            }
        }

        if (e > 30) return (s or 0x7c00).toShort()
        return (s or (e shl 10) or (m shr 13)).toShort()
    }
}