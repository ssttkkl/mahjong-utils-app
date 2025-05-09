package io.ssttkkl.mahjongdetector


import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asSkiaBitmap
import kotlinx.coroutines.await
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.jetbrains.skia.ColorAlphaType
import org.jetbrains.skia.ColorType
import org.jetbrains.skia.ImageInfo
import org.khronos.webgl.Float32Array
import org.khronos.webgl.set
import kotlin.js.Promise

@JsModule("onnxruntime-web")
private external val ort: JsAny

private fun createSession(path: String): Promise<JsAny> = js(
    "ort.InferenceSession.create(path)"
)

private fun createFloat32Tensor(array: Float32Array, shape: JsArray<JsNumber>): JsAny = js(
    "new ort.Tensor('float32', array, shape)"
)

private fun predict(session: JsAny, tensor: JsAny): Promise<Tensor> = js(
    "session.run({ input: tensor })"
)

actual object MahjongDetector {
    private lateinit var model: JsAny
    private var modelLoaded: Boolean = false
    private val modelLoadMutex = Mutex()

    private suspend fun prepareModel() {
        if (!modelLoaded) {
            modelLoadMutex.withLock {
                if (!modelLoaded) {
                    model = createSession("best.fp16.onnx")
                    modelLoaded = true
                }
            }
        }
    }

    actual suspend fun predict(image: ImageBitmap, confidenceThreshold: Float): List<Detection> {
        prepareModel()

        val (preprocessed, paddingInfo) = ImagePreprocessor.preprocessImage(image)
        val inputTensor = createInputTensor(preprocessed)
        val outputTensor = predict(model, inputTensor)
        val outputArr = outputTensor.array<JsArray<JsArray<JsArray<JsNumber>>>>()
            .await<JsArray<JsArray<JsArray<JsNumber>>>>()
        val output: Array<FloatArray> = Array(4 + CLASS_NAME.size) { i ->
            FloatArray(8400) { j ->
                outputArr[0]!![i]!![j]!!.toDouble().toFloat()
            }
        }
        val detections =
            YoloV8PostProcessor.postprocess(output, paddingInfo, CLASS_NAME, confidenceThreshold)
        detections.forEach {
            println(it)
        }

        inputTensor.dispose()
        return detections
    }

    private fun createInputTensor(image: ImageBitmap): JsAny {
        val siz = image.width * image.height

        val pixels = image.asSkiaBitmap().readPixels(
            ImageInfo(
                image.width,
                image.height,
                ColorType.RGBA_8888,
                ColorAlphaType.UNPREMUL
            )
        ) ?: error("cannot read pixels")

        val floats = Float32Array(siz * 3)
        for (i in 0 until siz) {
            floats[i] = pixels[i * 4].toFloat()  // r
            floats[i + siz] = pixels[i * 4 + 1].toFloat()  // g
            floats[i + siz + siz] = pixels[i * 4 + 2].toFloat()  // b
        }

        return createFloat32Tensor(floats, JsArray<JsNumber>().apply {
            set(0, 1.toJsNumber())
            set(1, 3.toJsNumber())
            set(2, image.width.toJsNumber())
            set(3, image.height.toJsNumber())
        })
    }
}