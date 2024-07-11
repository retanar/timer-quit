import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option
import java.io.File
import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.div
import kotlin.io.path.exists
import kotlin.io.path.writeText

/**
 * Adds the following hierarchy to the project root:
 * ```
 * - subdirectories/module_name
 *   - .gitignore
 *   - build.gradle.kts
 *   - src/main/java/base_package/module_name
 * ```
 */
abstract class CreateAndroidModuleTask : DefaultTask() {
    private val nameRegex = "(:?[A-Za-z][A-Za-z0-9_]*)+"

    init {
        description = "Creates necessary directories and files for a new android library module."
    }

    /**
     * Specifies a package name in a form like "com.base.package" to which a module name will be
     * added to make necessary directories and android namespace for build.gradle.kts file.
     */
    @Input
    lateinit var basePackageName: String

    @Input
    @Option(
        option = "name",
        description = "Sets a name for the new module. To place a module in a subdirectory, use " +
            "syntax :subdirectory:module_name.",
    )
    var moduleName: String = ""

    @TaskAction
    fun createModule() {
        when {
            moduleName.isBlank() -> throw IllegalArgumentException("Module name cannot be blank.")
            !moduleName.matches(Regex(nameRegex)) ->
                throw IllegalArgumentException("Module name doesn't match regex '$nameRegex'.")
        }

        val subdirs = moduleName.trim().trimStart(':').split(':')
        val justModuleName = subdirs.last()
        val namespace = "$basePackageName.$justModuleName"

        val modulePath = project.projectDir.absoluteFile.toPath() /
            subdirs.joinToString(File.separator)

        if (modulePath.exists()) {
            throw IllegalArgumentException("Path '$modulePath' already exists.")
        }

        println("Creating '$modulePath'.")
        modulePath.createDirectories()
        modulePath.writeGitignore()
        modulePath.writeBuildGradle(namespace)

        val javaAndPackage = modulePath / "src" / "main" / "java" /
            namespace.split('.').joinToString(File.separator)
        javaAndPackage.createDirectories()

        println(
            "Module $moduleName created, please include it in the settings.gradle.kts file " +
                "and add to :app module.",
        )
    }

    private fun Path.writeGitignore() = this
        .resolve(".gitignore")
        .writeText("""/build""")

    // In case something needs to be added to all modules, firstly, please check if it can be added
    // to convention plugins, instead of adding it here
    private fun Path.writeBuildGradle(namespace: String) = this
        .resolve("build.gradle.kts")
        .writeText(
            """
                plugins {
                    // TODO: Pick one of the convention plugins
                    id(libs.plugins.convention.android.library.get().pluginId)
                    id(libs.plugins.convention.feature.module.get().pluginId)
                }

                android {
                    namespace = "$namespace"
                }

                dependencies {
                }
            """.trimIndent(),
        )
}
