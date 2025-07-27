package mahjongutils.buildlogic.utils

import org.gradle.api.artifacts.transform.InputArtifact
import org.gradle.api.artifacts.transform.TransformAction
import org.gradle.api.artifacts.transform.TransformOutputs
import org.gradle.api.artifacts.transform.TransformParameters
import org.gradle.api.file.FileSystemLocation
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.kotlin.dsl.support.unzipTo
import org.gradle.kotlin.dsl.support.zipTo
import javax.inject.Inject
import kotlin.io.path.createTempDirectory

abstract class OnnxRuntimeLibraryFilter @Inject constructor(
    private val objects: ObjectFactory
) : TransformAction<OnnxRuntimeLibraryFilter.Parameter> {
    abstract class Parameter : TransformParameters {
        @get:Input
        abstract val platform: Property<String>
    }

    @get:InputArtifact
    abstract val inputArtifact: Provider<FileSystemLocation>

    override fun transform(outputs: TransformOutputs) {
        val inputFile = inputArtifact.get().asFile
        val outputFile = outputs.file(inputFile.name)
        val unzipDir = createTempDirectory().toFile()
        val zipDir = createTempDirectory().toFile()

        // 解压jar
        unzipTo(unzipDir, inputFile)

        objects.fileTree().apply {
            from(unzipDir)
            include("**/*")
            exclude("**/*.dSYM/**")
            listOf("linux-aarch64", "linux-x64", "osx-aarch64", "osx-x64", "win-x64")
                .minus(parameters.platform.get())
                .forEach {
                    exclude("ai/onnxruntime/native/${it}/*")
                }
        }.forEach {
            val dest = zipDir.resolve(it.toRelativeString(unzipDir))
            dest.parentFile.mkdirs()
            it.copyTo(dest)
        }

        // 重新打包
        zipTo(outputFile, zipDir)

        unzipDir.deleteRecursively()
        zipDir.deleteRecursively()
    }
}