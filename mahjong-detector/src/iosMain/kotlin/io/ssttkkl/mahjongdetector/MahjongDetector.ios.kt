@file:OptIn(ExperimentalForeignApi::class)
@file:Suppress("SENSELESS_COMPARISON", "UNCHECKED_CAST")

package io.ssttkkl.mahjongdetector

import androidx.compose.ui.graphics.ImageBitmap
import cocoapods.onnxruntime_objc.ORTEnv
import cocoapods.onnxruntime_objc.ORTSession
import cocoapods.onnxruntime_objc.ORTSessionOptions
import cocoapods.onnxruntime_objc.ORTTensorElementDataType
import cocoapods.onnxruntime_objc.ORTValue
import io.ssttkkl.mahjongdetector.ImagePreprocessor.preprocessImage
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCObjectVar
import kotlinx.cinterop.ShortVar
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.alloc
import kotlinx.cinterop.get
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.usePinned
import kotlinx.cinterop.value
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import platform.Foundation.NSBundle
import platform.Foundation.NSError
import platform.Foundation.NSMutableData
import platform.Foundation.appendBytes
import platform.Foundation.dataWithLength

actual object MahjongDetector {
    private const val MODEL_FILENAME = "best.fp16"
    private const val MODEL_TYPE = "onnx"

    private var modelLoaded: Boolean = false
    private val modelLoadMutex = Mutex()

    private val env: ORTEnv = ORTEnv()
    private lateinit var session: ORTSession

    private suspend fun prepareModel() {
        if (!modelLoaded) {
            modelLoadMutex.withLock {
                if (!modelLoaded) {
                    val modelPath = NSBundle.mainBundle.pathForResource(MODEL_FILENAME, MODEL_TYPE)
                        ?: error("Failed to find ONNX model file")
                    session = withErrPtr { errPtr ->
                        ORTSession(env, modelPath, ORTSessionOptions(), errPtr)
                    }
                }
                modelLoaded = true
            }
        }
    }

    private val inputNames: List<String> by lazy {
        checkNotNull(withErrPtr { errPtr ->
            session.inputNamesWithError(errPtr) as List<String>?
        })
    }

    fun close() {
    }

    actual suspend fun predict(image: ImageBitmap, confidenceThreshold: Float): List<Detection> =
        withContext(Dispatchers.Default) {
            prepareModel()

            var tensor: ORTValue? = null
            var results: Map<String, ORTValue>? = null

            // 预处理图像
            val (preprocessedImage, paddingInfo) = preprocessImage(image)

            // 将预处理后的图像数据转换为 ONNX 张量
            tensor = createTensor(preprocessedImage)

            // 执行推理
            results = withErrPtr { errPtr ->
                session.runWithInputs(
                    mapOf(inputNames.first() to tensor),
                    setOf("output0"),
                    null,
                    errPtr
                ) as Map<String, ORTValue>?
            }
            checkNotNull(results)

            // 获取输出张量 (你需要根据你的模型输出名称调整)
            val output = checkNotNull(results["output0"])
            val outputData = withErrPtr { errPtr ->
                output.tensorDataWithError(errPtr)
            }
            checkNotNull(outputData)

            val outputBytes = outputData.bytes as CPointer<ShortVar>
            val outputArray = Array<FloatArray>(4 + CLASS_NAME.size) { i ->
                FloatArray(8400) { j ->
                    val short = outputBytes[8400 * i + j]
                    fp16ToFloat(short)
                }
            } // [4+classes,8400]

            val detections =
                YoloV8PostProcessor.postprocess(
                    outputArray,
                    paddingInfo,
                    CLASS_NAME,
                    confidenceThreshold
                )
            return@withContext detections
        }

    // 创建ONNX输入Tensor
    private fun createTensor(image: ImageBitmap): ORTValue {
        val buffer = image.toNchwFp16Buffer()
        val len = buffer.size
        val data = checkNotNull(NSMutableData.dataWithLength(len.toULong()))
        buffer.readByteArray().usePinned {
            data.appendBytes(it.addressOf(0), len.toULong())
        }

        return withErrPtr { errPtr ->
            ORTValue(
                data,
                ORTTensorElementDataType.ORTTensorElementDataTypeFloat,
                listOf(1, 3, image.height, image.width), // NCHW格式
                errPtr
            )
        }
    }
}

@OptIn(BetaInteropApi::class)
private inline fun <T> withErrPtr(action: (CPointer<ObjCObjectVar<NSError?>>) -> T): T {
    memScoped {
        val err = alloc<ObjCObjectVar<NSError?>>()
        val result = action(err.ptr)
        val nsError = err.value
        if (nsError != null) {
            error(nsError.toString())
        }
        return result
    }
}
