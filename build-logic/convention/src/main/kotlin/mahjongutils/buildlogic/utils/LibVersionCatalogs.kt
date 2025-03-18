package mahjongutils.buildlogic.utils

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension


internal val Project.libs
    get() = rootProject.extensions
        .getByType(VersionCatalogsExtension::class.java)
        .named("libs")
