package io.ssttkkl.mahjongdetector

import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.core.graphics.get
import io.ssttkkl.mahjongdetector.ImagePreprocessor.preprocessImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.nio.FloatBuffer


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
                    val bytes = MyApp.current.assets.open(MODEL_FILENAME)
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

    actual suspend fun predict(image: ImageBitmap): List<String> =
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

            detections.sortedBy { it.x1 }.map { CLASS_NAME[it.classId] }
        }

    // 创建ONNX输入Tensor
    private fun createTensor(image: Bitmap): OnnxTensor {
        val width = image.width
        val height = image.height
        val siz = height * width

        // 2. 转换为BGR float数组（OpenCV兼容格式）
        val bgrData = FloatArray(3 * siz)
        var p = 0
        for (i in 0 until height) {
            for (j in 0 until width) {
                val pixel = image[i, j]

                val r = (pixel shr 16) and 0xFF
                val g = (pixel shr 8) and 0xFF
                val b = pixel and 0xFF

                bgrData[p] = r / 255.0f
                bgrData[siz + p] = g / 255.0f
                bgrData[siz * 2 + p] = b / 255.0f
                p++
            }
        }

        return OnnxTensor.createTensor(
            env,
            FloatBuffer.wrap(bgrData),
            longArrayOf(1, 3, height.toLong(), width.toLong()) // NCHW格式
        )
    }
}