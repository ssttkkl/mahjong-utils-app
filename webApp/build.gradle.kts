plugins {
    id("mahjongutils.buildlogic.app.web")
}

kotlin.sourceSets.wasmJsMain {
    // 之前给fdroid的配置里，模型就放在这里，不好改了
    resources.srcDir(project(":mahjong-detector").file("src/wasmJsMain/resources"))
    dependencies {
        api(project(":shared"))
    }
}
