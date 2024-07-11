// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id(libs.plugins.convention.linters.get().pluginId)
}

tasks.register<CreateAndroidModuleTask>("createLibraryModule") {
    basePackageName = "com.featuremodule"
}

tasks.register<RenamePackageTask>("renamePackage")
tasks.register<RenamePackageTask>("renamePackageForce") { replaceExistingFiles = true }
