package org.yuvViewer

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assumptions.assumeFalse
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.awt.GraphicsEnvironment
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.TimeUnit
import java.util.jar.JarFile

class PackagedApplicationTest {
    @TempDir
    lateinit var directory: Path

    private val applicationJar = Path.of("build/libs/yuvViewer-1.0-SNAPSHOT.jar")

    @Test
    fun `jar references supplied runtime dependencies and contains logging configuration`() {
        JarFile(applicationJar.toFile()).use { jar ->
            val attributes = jar.manifest.mainAttributes
            assertThat(attributes.getValue("Main-Class")).isEqualTo("org.yuvViewer.Main")
            val classPath = attributes.getValue("Class-Path").split(" ")
            assertThat(classPath).anyMatch { it.startsWith("lib/log4j-api-") }
            assertThat(classPath).anyMatch { it.startsWith("lib/log4j-core-") }
            for (entry in classPath) {
                assertThat(applicationJar.parent.resolve(entry)).isRegularFile()
            }
            assertThat(jar.getEntry("log4j2.xml")).isNotNull()
        }
    }

    @Test
    fun `java jar starts after copying application and libraries to another directory`() {
        assumeFalse(GraphicsEnvironment.isHeadless(), "Application requires a display")
        val destination = Files.createDirectory(directory.resolve("packaged application"))
        val copiedJar = Files.copy(applicationJar, destination.resolve(applicationJar.fileName))
        val libraries = Files.createDirectory(destination.resolve("lib"))
        Files.list(applicationJar.parent.resolve("lib")).use { files ->
            files.forEach { Files.copy(it, libraries.resolve(it.fileName)) }
        }
        val output = directory.resolve("startup.log")
        val java = Path.of(System.getProperty("java.home"), "bin", "java").toString()
        val process = ProcessBuilder(java, "-jar", copiedJar.toString())
            .directory(destination.toFile())
            .redirectErrorStream(true)
            .redirectOutput(output.toFile())
            .start()
        try {
            val exited = process.waitFor(3, TimeUnit.SECONDS)
            val log = Files.readString(output)
            assertThat(exited).withFailMessage("Application exited during startup:\n%s", log).isFalse()
            assertThat(log).doesNotContain("NoClassDefFoundError", "ClassNotFoundException",
                "Exception in thread", "ERROR")
        } finally {
            process.destroy()
            if (!process.waitFor(5, TimeUnit.SECONDS)) {
                process.destroyForcibly()
                assertThat(process.waitFor(5, TimeUnit.SECONDS)).isTrue()
            }
        }
    }
}
