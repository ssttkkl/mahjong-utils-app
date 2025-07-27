package io.ssttkkl.mahjongdetector

import org.khronos.webgl.Float32Array
import org.khronos.webgl.Uint8ClampedArray
import org.w3c.dom.ImageData
import kotlin.js.Promise

external interface TensorBuffer : JsAny {
    fun get(vararg locs: Int): JsNumber
    fun set(value: JsNumber, vararg locs: Int)
}

/**
 * https://js.tensorflow.org/api/latest/?hl=zh-cn#Tensor
 */
external interface Tensor : JsAny {
    fun <T : JsArray<*>> array(): Promise<T>
    fun buffer(): Promise<TensorBuffer>
    fun dispose()
    fun div(float: Float): Tensor
    fun expandDims(dim: Int): Tensor
    fun print(verbose: Boolean = definedExternally)
    fun toFloat(): Tensor
}

/**
 * https://js.tensorflow.org/api/latest/?hl=zh-cn#GraphModel
 */
external interface GraphModel : JsAny {
    fun predict(inputs: Tensor, config: JsAny = definedExternally): Tensor
    fun predictAsync(inputs: Tensor, config: JsAny = definedExternally): Promise<Tensor>
}

@JsModule("@tensorflow/tfjs")
external object Tf {

    /**
     * https://js.tensorflow.org/api/latest/?hl=zh-cn#tensor
     */
    fun tensor(
        values: Uint8ClampedArray,
        shape: JsArray<JsNumber> = definedExternally,
        dtype: String = definedExternally
    ): Tensor

    /**
     * https://js.tensorflow.org/api/latest/?hl=zh-cn#tensor
     */
    fun tensor(
        values: Float32Array,
        shape: JsArray<JsNumber> = definedExternally,
        dtype: String = definedExternally
    ): Tensor

    /**
     * https://js.tensorflow.org/api/latest/?hl=zh-cn#loadGraphModel
     */
    fun loadGraphModel(path: String, options: JsAny = definedExternally): Promise<JsAny>

    @JsName("browser")
    object Browser {
        fun fromPixels(pixels: ImageData, numChannels: Int? = definedExternally): Tensor
    }
}
