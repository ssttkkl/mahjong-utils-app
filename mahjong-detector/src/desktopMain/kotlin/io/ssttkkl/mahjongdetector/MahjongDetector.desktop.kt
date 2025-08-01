package io.ssttkkl.mahjongdetector

import ai.onnxruntime.OnnxJavaType
import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import androidx.compose.ui.graphics.ImageBitmap
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.nio.ByteBuffer


actual object MahjongDetector : AbsMahjongDetector() {
    private const val MODEL_FILENAME = "best.fp16.onnx"

    private var modelLoaded: Boolean = false
    private val modelLoadMutex = Mutex()

    private val env: OrtEnvironment = OrtEnvironment.getEnvironment()
    private lateinit var session: OrtSession

    private suspend fun prepareModel() {
        if (!modelLoaded) {
            modelLoadMutex.withLock {
                if (!modelLoaded) {
                    val bytes = checkNotNull(
                        this::class.java.classLoader?.getResourceAsStream(MODEL_FILENAME)
                    ).use { it.readBytes() }

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

    actual override suspend fun run(preprocessedImage: ImageBitmap): Array<FloatArray> {
        prepareModel()

        var tensor: OnnxTensor? = null
        var results: OrtSession.Result? = null

        try {
            // 将预处理后的图像数据转换为 ONNX 张量
            tensor = createTensor(preprocessedImage)

            // 执行推理
            results = session.run(mapOf(session.inputNames.first() to tensor))

            // 获取输出张量 (你需要根据你的模型输出名称调整)
            val output = results.get(0).value as Array<Array<FloatArray>>
            return output[0]
        } finally {
            // 释放资源
            tensor?.close()
            results?.close()
        }
    }

    // 创建ONNX输入Tensor
    private fun createTensor(image: ImageBitmap): OnnxTensor {
        val buffer = image.toNchwFp16Buffer()
        return OnnxTensor.createTensor(
            env,
            ByteBuffer.wrap(buffer.readByteArray()).asShortBuffer(),
            longArrayOf(1, 3, image.height.toLong(), image.width.toLong()),  // NCHW格式
            OnnxJavaType.FLOAT16
        )
    }
}