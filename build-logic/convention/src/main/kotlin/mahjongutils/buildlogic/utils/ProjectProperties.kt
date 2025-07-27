package mahjongutils.buildlogic.utils

import org.gradle.api.Project
import java.util.Properties


fun Project.readVersion(): Pair<String, Int> {
    val versionProperties = Properties()
    rootDir.resolve("composeApp/version.properties")
        .inputStream().use { inputStream ->
            versionProperties.load(inputStream)
        }

    val versionName = versionProperties["versionName"].toString()
    val versionCode = versionProperties["versionCode"].toString().toInt()

    return Pair(versionName, versionCode)
}

fun Project.readGitCommitHash(): String {
    return Runtime.getRuntime().exec(
        arrayOf("git", "rev-parse", "HEAD")
    ).inputStream.bufferedReader().use { it.readLine() }
}