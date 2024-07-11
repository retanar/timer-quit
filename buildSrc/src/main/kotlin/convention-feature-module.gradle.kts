/**
 * Convention plugin for dependencies that would go into feature_impl modules,
 * but not in feature_api, :core, :data or similar modules.
 */

import org.gradle.accessors.dm.LibrariesForLibs

// Cannot access libs otherwise
val libs = the<LibrariesForLibs>()

plugins {
    id("convention-android-library")
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
}

dependencies {
    implementation(project(":core"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    implementation(libs.bundles.compose)
    implementation(libs.navigation.compose)
    debugImplementation(libs.compose.ui.tooling)

    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation)
    ksp(libs.hilt.compiler)
}
