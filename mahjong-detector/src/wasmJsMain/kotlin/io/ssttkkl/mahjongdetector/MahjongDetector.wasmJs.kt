package io.ssttkkl.mahjongdetector


import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asSkiaBitmap
import kotlinx.coroutines.await
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock


private fun predict(model: GraphModel, tensor: Tensor): Tensor? = js(
    """
      function(){
        try {
          return model.predict(tensor, {verbose: true})
        } catch(e) { 
          console.error(e)
          return null 
        }
      }()
    """
)

actual object MahjongDetector : AbsMahjongDetector() {
    private lateinit var model: GraphModel
    private var modelLoaded: Boolean = false
    private val modelLoadMutex = Mutex()

    private suspend fun prepareModel() {
        if (!modelLoaded) {
            modelLoadMutex.withLock {
                if (!modelLoaded) {
                    model = Tf.loadGraphModel("best_web_model/model.json").await()
                    modelLoaded = true
                }
            }
        }
    }

    actual override suspend fun run(preprocessedImage: ImageBitmap): Array<FloatArray> {
        prepareModel()

        var inputTensor: Tensor? = null
        var outputTensor: Tensor? = null

        try {
            inputTensor = createInputTensor(preprocessedImage)  // 1*640*640*3
            inputTensor.print(verbose = true)

            outputTensor = checkNotNull(predict(model, inputTensor))  // 1*(4+classes)*8400
            outputTensor.print(verbose = true)

            val outputArr = outputTensor.array<JsArray<JsArray<JsArray<JsNumber>>>>()
                .await<JsArray<JsArray<JsArray<JsNumber>>>>()
            val output: Array<FloatArray> = Array(4 + CLASS_NAME.size) { i ->
                FloatArray(8400) { j ->
                    outputArr[0]!![i]!![j]!!.toDouble().toFloat()
                }
            }
            return output
        } finally {
            // 释放资源
            inputTensor?.dispose()
            outputTensor?.dispose()
        }
    }


    private fun createInputTensor(image: ImageBitmap): Tensor {
        val imgData = image.asSkiaBitmap().toImageData()
        val tensor = Tf.Browser.fromPixels(imgData)
            .toFloat()
            .div(255.0f)
            .expandDims(0)
        return tensor
    }
}