package io.ssttkkl.mahjongdetector

import androidx.compose.ui.graphics.ImageBitmap
import io.ssttkkl.mahjongutils.app.base.utils.AppInstance
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.tensorflow.lite.InterpreterApi
import org.tensorflow.lite.InterpreterApi.Options.TfLiteRuntime
import java.nio.ByteBuffer
import java.nio.ByteOrder


actual object MahjongDetector : AbsMahjongDetector() {
    private const val MODEL_FILENAME = "best_float16.tflite"

    private var modelLoaded: Boolean = false
    private val modelLoadMutex = Mutex()

    private lateinit var interpreter: InterpreterApi

    private fun loadModelBytes(modelBytes: ByteArray): ByteBuffer {
        val byteBuffer = ByteBuffer.allocateDirect(modelBytes.size)
        byteBuffer.order(ByteOrder.nativeOrder())
        byteBuffer.put(modelBytes)
        return byteBuffer
    }

    private suspend fun prepareModel() {
        if (!modelLoaded) {
            modelLoadMutex.withLock {
                if (!modelLoaded) {
                    val interpreterOption = InterpreterApi.Options()
                        .setRuntime(TfLiteRuntime.FROM_APPLICATION_ONLY)
                    val bytes = AppInstance.app.assets.open(MODEL_FILENAME)
                        .use { stream ->
                            stream.readBytes()
                        }
                    interpreter = InterpreterApi.create(
                        loadModelBytes(bytes),
                        interpreterOption
                    )
                }
                modelLoaded = true
            }
        }
    }

    fun close() {
        interpreter.close()
    }

    actual override suspend fun run(preprocessedImage: ImageBitmap): Array<FloatArray> {
        prepareModel()

        val input = createInputTensor(preprocessedImage)
        val output = createOutputTensor()

        // 执行推理
        interpreter.run(input, output)
        return output[0]
    }

    // float[1][640][640][3]
    private fun createInputTensor(image: ImageBitmap): Array<Array<Array<FloatArray>>> {
        val pixels = IntArray(image.height * image.width)
        image.readPixels(pixels)

        return Array(1) {
            Array(640) { i ->
                Array(640) { j ->
                    FloatArray(3) { c ->
                        // c=0 -> R, c=1 -> G, c=2 -> B
                        val pixel = pixels[i * 640 + j]
                        val v = (pixel shr (c * 8)) and 0xFF
                        v / 255.0f
                    }
                }
            }
        }
    }

    // float[1][4 + MahjongDetector.CLASS_NAME.size][8400]
    private fun createOutputTensor(): Array<Array<FloatArray>> {
        return Array(1) {
            Array(4 + MahjongDetector.CLASS_NAME.size) {
                FloatArray(8400)
            }
        }
    }
}