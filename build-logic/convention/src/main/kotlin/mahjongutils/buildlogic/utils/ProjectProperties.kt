package mahjongutils.buildlogic.utils

import org.gradle.api.Project
import java.util.Properties


fun Project.readVersion(): Pair<String, Int> {
    val versionProperties = Properties()
    rootProject.findProject(":composeApp")!!.file("version.properties")
        .inputStream().use { inputStream ->
            versionProperties.load(inputStream)
        }

    val versionName = versionProperties["versionName"].toString()
    val versionCode = versionProperties["versionCode"].toString().toInt()

    return Pair(versionName, versionCode)
}
