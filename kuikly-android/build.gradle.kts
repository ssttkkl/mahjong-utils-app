import mahjongutils.buildlogic.APPLICATION_ID

plugins {
    id("mahjongutils.buildlogic.android.lib")
}

android {
    namespace = "$APPLICATION_ID.kuikly"
    
    kotlinOptions {
        jvmTarget = libs.versions.java.targetJvm.get()
    }
}

dependencies {
    implementation(project(":kuikly-shared"))

    implementation("androidx.recyclerview:recyclerview:1.2.1")
    implementation("androidx.appcompat:appcompat:1.3.1")

    implementation("com.squareup.picasso:picasso:2.71828")

    implementation("androidx.core:core-ktx:1.6.0")
    implementation("androidx.dynamicanimation:dynamicanimation:1.0.0")
    implementation("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.12.0")
}