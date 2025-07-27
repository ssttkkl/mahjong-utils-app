package io.ssttkkl.mahjongdetector

import kotlin.math.max
import kotlin.math.min


data class Detection(
    val x1: Int, val y1: Int, val x2: Int, val y2: Int,
    val classId: Int, val className: String,
    val confidence: Float
)

data class PaddingInfo(
    val scale: Float,
    val padX: Int,
    val padY: Int,
    val originHeight: Int,
    val originWidth: Int,
)

object YoloV8PostProcessor {
    fun postprocess(
        output: Array<FloatArray>,
        padding: PaddingInfo,
        classNameMapping: List<String>,
        confThreshold: Float = 0.5f,
        iouThreshold: Float = 0.3f
    ): List<Detection> {
        val detections = mutableListOf<Detection>()
        val numClasses = classNameMapping.size

        // 1. 检查输出维度 (YOLOv11: [1,4+classes,8400])
        // when the output shape is (1,84,8400) this is how it is interpreted:
        //
        // 1 = batch size
        // 84 = x_center + y_center + width + height + confidence of each class = 4 + 80
        // 8400 = number of detected boxes
        if (output.size < 4 + numClasses || output[0].isEmpty()) {
            throw IllegalArgumentException("非法输出格式，预期[${4 + numClasses},8400]，实际${output.size}x${output[0].size}")
        }

        // 2. 遍历所有预测框
        for (i in output[0].indices) {
            // 3. 获取当前框数据
            val xc = output[0][i]  // 中心x (归一化)
            val yc = output[1][i]  // 中心y
            val w = output[2][i]   // 宽度
            val h = output[3][i]   // 高度

            // 4. 获取类别概率 (从第4位开始)
            var maxConf = 0f
            var classId = -1
            for (c in 0 until numClasses) {
                val conf = output[4 + c][i]
                if (conf > maxConf) {
                    maxConf = conf
                    classId = c
                }
            }

            // 5. 过滤低置信度检测
            if (maxConf > confThreshold) {
                // 6. 转换到原始图像坐标 (补偿填充和缩放)
                val x1 = (xc - w / 2 - padding.padX) / padding.scale
                val y1 = (yc - h / 2 - padding.padY) / padding.scale
                val x2 = (xc + w / 2 - padding.padX) / padding.scale
                val y2 = (yc + h / 2 - padding.padY) / padding.scale

                detections.add(
                    Detection(
                        x1 = x1.toInt(),
                        y1 = y1.toInt(),
                        x2 = x2.toInt(),
                        y2 = y2.toInt(),
                        classId = classId,
                        className = classNameMapping[classId],
                        confidence = maxConf
                    )
                )
            }
        }

        // 7. 应用非极大抑制
        return applyNMS(detections, iouThreshold)
    }

    // 非极大抑制实现
    private fun applyNMS(detections: List<Detection>, iouThreshold: Float): List<Detection> {
        val sorted = detections.sortedByDescending { it.confidence }.toMutableList()
        val selected = mutableListOf<Detection>()

        while (sorted.isNotEmpty()) {
            val current = sorted.removeAt(0)
            selected.add(current)

            sorted.removeAll { box ->
                val iou = calculateIOU(current, box)
                iou > iouThreshold
            }
        }
        return selected
    }

    // 计算IoU
    private fun calculateIOU(a: Detection, b: Detection): Float {
        // 计算相交矩形
        val interx1 = max(a.x1, b.x1)
        val intery1 = max(a.y1, b.y1)
        val interx2 = min(a.x2, b.x2)
        val intery2 = min(a.y2, b.y2)

        // 如果没有相交区域
        if (interx2 < interx1 || intery2 < intery1) {
            return 0f
        }

        // 计算相交区域面积
        val interArea = (interx2 - interx1) * (intery2 - intery1)

        // 计算两个矩形的面积
        val area1 = (a.x2 - a.x1) * (a.y2 - a.y1)
        val area2 = (b.x2 - b.x1) * (b.y2 - b.y1)

        // 计算并集面积
        val unionArea = area1 + area2 - interArea

        // 计算IoU
        return interArea.toFloat() / unionArea
    }
}