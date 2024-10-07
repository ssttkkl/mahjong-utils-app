import org.gradle.api.Project
import java.util.Properties

private val localPropertiesMap = HashMap<String, Properties>()

val Project.localProperties: Properties
    get() = localPropertiesMap.getOrPut(rootProject.name) {
        Properties().apply {
            if (rootProject.file("local.properties").exists()) {
                rootProject.file("local.properties").inputStream().use { inputStream ->
                    load(inputStream)
                }
            }
        }
    }
