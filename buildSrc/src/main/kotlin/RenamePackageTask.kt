import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.StandardCopyOption.REPLACE_EXISTING

abstract class RenamePackageTask : DefaultTask() {
    init {
        description = """
            Walks through project files and renames matching package mentions and
            directories.

            Options --to and --from, and setting 'directoryBlockList' should be provided
            carefully, as they can cause drastic changes if not set properly.
            Optionally, project can be cleaned before executing this task, however by
            default 'build' directories are excluded by 'directoryBlockList'.

            Directory renaming tries to use Files.move() first, then recursive moving,
            both can leave some old directories not removed.
        """.trimIndent()
    }

    @Input
    @Option(
        option = "from",
        description = "Name of the module to be renamed.",
    )
    var fromPackageName = ""

    @Input
    @Option(
        option = "to",
        description = "Name of the module to rename previous module to.",
    )
    var toPackageName = ""

    /** Directories that won't be visited for renaming. Can be extended in `tasks.register`. */
    @Input
    var directoryBlockList = hashSetOf(".idea", ".gradle", "build", ".git")

    /** Provides a [REPLACE_EXISTING] flag if true. */
    @Input
    var replaceExistingFiles = false

    @TaskAction
    fun execute() {
        when {
            fromPackageName.isBlank() ->
                throw IllegalArgumentException("'--from <package>' argument should be provided.")

            toPackageName.isBlank() ->
                throw IllegalArgumentException("'--to <package>' argument should be provided.")
        }
        val fromFolders = fromPackageName.replace('.', File.separatorChar)
        val toFolders = toPackageName.replace('.', File.separatorChar)

        // BottomUp is needed to edit files first, then move them into a new directory
        project.projectDir.walkBottomUp()
            .onEnter { it.name !in directoryBlockList }
            .forEach { entry ->
                if (entry.isFile && entry.canRead() && entry.canWrite()) {
                    val oldText = entry.readText()
                    val newText = oldText.replace(fromPackageName, toPackageName)

                    if (oldText != newText) {
                        entry.writeText(newText)
                        println("Rewrote ${entry.projectPath()}")
                    }
                } else if (entry.isDirectory && entry.absolutePath.endsWith(fromFolders)) {
                    val outputPath = File(entry.absolutePath.replaceLast(fromFolders, toFolders))

                    try {
                        // Otherwise can fail with NoSuchFileException
                        outputPath.parentFile.mkdirs()

                        // Try easy route first. REPLACE_EXISTING here just allows to overwrite
                        // directory if it's empty.
                        Files.move(entry.toPath(), outputPath.toPath(), REPLACE_EXISTING)
                        println("Moved ${entry.projectPath()} to ${outputPath.projectPath()}")
                    } catch (e: IOException) {
                        // Fall back to manual move
                        moveRecursively(entry, outputPath)
                    }
                }
            }
    }

    /**
     * Tries to copy all directories and move all files found by [File.walkTopDown], but leaves
     * old directories behind.
     */
    private fun moveRecursively(
        srcDir: File,
        dstDir: File,
        overwrite: Boolean = replaceExistingFiles
    ): Boolean {
        require(srcDir.startsWith(project.projectDir))
        require(dstDir.startsWith(project.projectDir))
        var errorHappened = false

        srcDir.walkTopDown()
            .onEnter { it.name !in directoryBlockList }
            .forEach { entry ->
                val relativePath = entry.toRelativeString(srcDir)
                val outputPath = dstDir.resolve(relativePath)

                if (entry.isDirectory) {
                    // Directories are visited first: create necessary
                    outputPath.mkdirs()

                    // mkdirs returns false if directory already exists, so check with exists()
                    if (!outputPath.exists()) {
                        System.err.println("Failed to create ${outputPath.projectPath()}")
                        errorHappened = true
                    }
                } else if (entry.isFile) {
                    // Files are visited last: move to already created directories
                    try {
                        if (overwrite) {
                            Files.move(entry.toPath(), outputPath.toPath(), REPLACE_EXISTING)
                        } else {
                            Files.move(entry.toPath(), outputPath.toPath())
                        }
                        println("Moved ${entry.projectPath()} to ${outputPath.projectPath()}")
                    } catch (e: IOException) {
                        errorHappened = true
                        System.err.println(
                            """
                            Failed to move ${entry.projectPath()} to ${outputPath.projectPath()}
                                Caused by $e
                            """.trimIndent(),
                        )
                    }
                }
            }
        return errorHappened
    }

    private fun String.replaceLast(oldValue: String, newValue: String): String {
        val index = lastIndexOf(oldValue)
        return if (index < 0) this else replaceRange(index, index + oldValue.length, newValue)
    }

    /** Utility function to provide path relative to project directory. */
    private fun File.projectPath(): String = toRelativeString(project.projectDir)
}
