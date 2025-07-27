@file:OptIn(ExperimentalForeignApi::class)
@file:Suppress("SENSELESS_COMPARISON", "UNCHECKED_CAST")

package io.ssttkkl.mahjongdetector

import androidx.compose.ui.graphics.ImageBitmap
import com.rickclephas.kmp.nserrorkt.asThrowable
import io.ssttkkl.mahjongutils.app.base.utils.toUIImage
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.FloatVar
import kotlinx.cinterop.ObjCObjectVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.get
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.value
import platform.CoreML.MLMultiArray
import platform.CoreML.MLMultiArrayDataTypeFloat32
import platform.Foundation.NSError
import platform.UIKit.UIImage

interface CoreMLMahjongDetector {
    suspend fun run(preprocessedImage: UIImage): MLMultiArray

    companion object {
        var impl: CoreMLMahjongDetector? = null
    }
}

actual object MahjongDetector : AbsMahjongDetector() {
    actual override suspend fun run(preprocessedImage: ImageBitmap): Array<FloatArray> {
        val raw = checkNotNull(CoreMLMahjongDetector.impl).run(preprocessedImage.toUIImage())
        check(raw.dataType == MLMultiArrayDataTypeFloat32) { "dataType expect Float32, but ${raw.dataType}" }

        var ptr = raw.dataPointer!!.reinterpret<FloatVar>()
        val outputArray = Array<FloatArray>(4 + CLASS_NAME.size) { i ->
            FloatArray(8400) { j ->
                ptr[i * 8400 + j]
            }
        } // [4+classes,8400]
        return outputArray
    }
}

@OptIn(BetaInteropApi::class)
private inline fun <T> withErrPtr(action: (CPointer<ObjCObjectVar<NSError?>>) -> T): T {
    memScoped {
        val err = alloc<ObjCObjectVar<NSError?>>()
        val result = action(err.ptr)
        val nsError = err.value
        if (nsError != null) {
            throw nsError.asThrowable()
        }
        return result
    }
}
