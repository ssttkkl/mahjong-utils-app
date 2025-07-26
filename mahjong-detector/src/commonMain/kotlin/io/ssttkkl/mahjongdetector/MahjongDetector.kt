package io.ssttkkl.mahjongdetector

import androidx.compose.ui.graphics.ImageBitmap
import io.ssttkkl.mahjongdetector.ImagePreprocessor.preprocessImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

abstract class AbsMahjongDetector {
    suspend fun predict(image: ImageBitmap, confidenceThreshold: Float = 0.5f): List<Detection> =
        withContext(Dispatchers.Default) {
            // 预处理图像
            val (preprocessedImage, paddingInfo) = preprocessImage(image)

            // 执行推理
            val output = run(preprocessedImage)

            val detections =
                YoloV8PostProcessor.postprocess(
                    output,
                    paddingInfo,
                    MahjongDetector.CLASS_NAME,
                    confidenceThreshold
                )
            return@withContext detections
        }

    abstract suspend fun run(preprocessedImage: ImageBitmap): Array<FloatArray>
}


expect object MahjongDetector : AbsMahjongDetector {
    override suspend fun run(preprocessedImage: ImageBitmap): Array<FloatArray>
}

internal val MahjongDetector.CLASS_NAME
    get() = listOf(
        "1m", "1p", "1s",
        "2m", "2p", "2s",
        "3m", "3p", "3s",
        "4m", "4p", "4s",
        "5m", "5p", "5s",
        "6m", "6p", "6s",
        "7m", "7p", "7s",
        "8m", "8p", "8s",
        "9m", "9p", "9s",
        "7z", "5z", "6z", "2z", "4z", "3z", "1z"
    )