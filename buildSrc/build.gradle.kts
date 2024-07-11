plugins {
    `kotlin-dsl`
}

repositories {
    google()
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    implementation(libs.android.plugin)
    implementation(libs.kotlin.plugin)
    implementation(libs.hilt.plugin)
    implementation(libs.ksp.plugin)
    implementation(libs.detekt.plugin)
    implementation(libs.ktlint.plugin)

    // Workaround to use Hilt with proper javapoet version
    // Refer to https://github.com/google/dagger/issues/3068#issuecomment-999118496
    implementation(libs.javapoet)

    // Workaround to use libs in convention plugin scripts (and possibly other)
    // Refer to https://stackoverflow.com/questions/67795324/gradle7-version-catalog-how-to-use-it-with-buildsrc
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
}
