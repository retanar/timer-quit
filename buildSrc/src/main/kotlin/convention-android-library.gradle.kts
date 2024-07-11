/** Convention plugin for android library modules. Includes plugins and android{} setup. */

import org.gradle.accessors.dm.LibrariesForLibs

// Cannot access libs otherwise
val libs = the<LibrariesForLibs>()

plugins {
    // Still can't access libs in plugins
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("convention-linters")
}

kotlin {
    jvmToolchain(libs.versions.jvm.toolchain.get().toInt())
}

android {
    compileSdk = libs.versions.sdk.compile.get().toInt()

    defaultConfig {
        minSdk = libs.versions.sdk.min.get().toInt()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }
}

dependencies {
    // Compose runtime was included for setting up compose in android{}
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.runtime)
}
