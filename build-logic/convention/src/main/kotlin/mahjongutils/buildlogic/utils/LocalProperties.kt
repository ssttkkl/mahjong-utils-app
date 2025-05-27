import org.gradle.api.Project
import org.gradle.internal.extensions.core.extra
import org.gradle.kotlin.dsl.get
import java.util.Properties

private val Project.localProperties: Properties
    get() = rootProject.run {
        if (extra.has("localProperties")) {
            extra.get("localProperties") as Properties
        } else {
            val props = Properties().apply {
                rootProject.file("local.properties").reader().use { rd ->
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
    }
    return localProperties[name]?.toString()
        ?: System.getenv(underscoreName)
}