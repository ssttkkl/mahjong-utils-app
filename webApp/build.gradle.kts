plugins {
    id("mahjongutils.buildlogic.app.web")
}

kotlin.sourceSets.wasmJsMain {
    dependencies {
        api(project(":shared"))
    }
}
