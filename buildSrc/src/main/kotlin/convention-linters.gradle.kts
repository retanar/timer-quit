import org.gradle.accessors.dm.LibrariesForLibs

val libs = the<LibrariesForLibs>()

plugins {
    id("io.gitlab.arturbosch.detekt")
    id("org.jlleitschuh.gradle.ktlint")
}

detekt {
    config.setFrom(rootProject.file("detekt-config.yml"))
    basePath = projectDir.absolutePath
}

ktlint {
    verbose = true
    android = true
    outputColorName = "RED"
}

dependencies {
    ktlintRuleset(libs.ktlint.compose.rules)
}
