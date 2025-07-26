@file:OptIn(ExperimentalForeignApi::class)
@file:Suppress("SENSELESS_COMPARISON", "UNCHECKED_CAST")

package io.ssttkkl.mahjongdetector

import androidx.compose.ui.graphics.ImageBitmap
import com.rickclephas.kmp.nserrorkt.asThrowable
import io.ssttkkl.mahjongutils.app.base.utils.toUIImage
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCObjectVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import okio.readByteString
import platform.CoreML.MLMultiArray
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


        val outputBytes = raw.dataPointer!!.readByteString((4 + CLASS_NAME.size) * 8400 * 4)
        val outputArray = Array<FloatArray>(4 + CLASS_NAME.size) { i ->
            FloatArray(8400) { j ->
                val idx = 8400 * i + j
                val a = outputBytes[idx * 4]
                val b = outputBytes[idx * 4 + 1] * 1 shl 8
                val c = outputBytes[idx * 4 + 2] * 1 shl 16
                val d = outputBytes[idx * 4 + 3] * 1 shl 24
                Float.fromBits(a + b + c + d)
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
