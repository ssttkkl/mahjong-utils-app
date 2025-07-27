import org.gradle.api.Project
import org.gradle.internal.extensions.core.extra
import java.io.File
import java.util.Properties

private val Project.localPropertiesFile: File
    get() = rootProject.file("local.properties")

private val Project.localProperties: Properties
    get() = rootProject.run {
        if (extra.has("localProperties")) {
            extra.get("localProperties") as Properties
        } else {
            if (!localPropertiesFile.exists()) {
                return Properties()
            }

            val props = Properties().apply {
                localPropertiesFile.reader().use { rd ->
                    load(rd)
                }
            }
            extra.set("localProperties", props)
            props
        }
    }

fun Project.getLocalProperty(name: String): String? {
    val underscoreName = name.split(".").joinToString("_") {
        it.replace(Regex("(?<=.)([A-Z])"), "_$1")
    }.uppercase()
    println("getLocalProperty: $name, $underscoreName")
    return localProperties[name]?.toString()
        ?: System.getenv(underscoreName)
}