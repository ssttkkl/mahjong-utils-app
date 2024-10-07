import org.gradle.api.Project
import java.util.Properties

private val versionPropertiesMap = HashMap<String, Properties>()

val Project.versionProperties: Properties
    get() = versionPropertiesMap.getOrPut(project.name) {
        Properties().apply {
            if (project.file("version.properties").exists()) {
                project.file("version.properties").inputStream().use { inputStream ->
                    load(inputStream)
                }
            }
        }
    }

val Project.versionName: String
    get() = versionProperties["versionName"].toString()

val Project.versionCode: Int
    get() = versionProperties["versionCode"].toString().toInt()
