package org.yuvViewer.utils
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.nio.file.Path
class ExtensionFileFilterTest {
    @Test
    fun testAcceptYuv() {
        val filter = ExtensionFileFilter("yuv")
        assertThat(filter.accept(File("test.yuv"))).isTrue()
        assertThat(filter.accept(File("test.YUV"))).isTrue() // ExtensionUtils.getExtension returns lowercase
        assertThat(filter.accept(File("test.txt"))).isFalse()
    }
    @Test
    fun testAcceptQcif() {
        val filter = ExtensionFileFilter("qcif")
        assertThat(filter.accept(File("test.qcif"))).isTrue()
        assertThat(filter.accept(File("test.yuv"))).isFalse()
    }
    @Test
    fun testAcceptDirectory(@TempDir tempDir: Path) {
        val filter = ExtensionFileFilter("yuv")
        val dir = tempDir.toFile()
        assertThat(filter.accept(dir)).isTrue()
    }
    @Test
    fun testGetDescription() {
        val filter = ExtensionFileFilter("yuv")
        assertThat(filter.description).isEqualTo("yuv Files (*.yuv)")
    }

    @Test
    fun testAcceptFileWithoutExtension() {
        // File without extension → accept() returns false (covers the null-extension branch)
        val filter = ExtensionFileFilter("yuv")
        assertThat(filter.accept(File("noextension"))).isFalse()
    }
}