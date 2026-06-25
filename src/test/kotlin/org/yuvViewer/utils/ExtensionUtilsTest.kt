package org.yuvViewer.utils
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.nio.file.Path
class ExtensionUtilsTest {
    @Test
    fun testGetExtension() {
        assertThat(ExtensionUtils.getExtension(File("test.yuv"))).isEqualTo("yuv")
        assertThat(ExtensionUtils.getExtension(File("test.QCIF"))).isEqualTo("qcif")
        assertThat(ExtensionUtils.getExtension(File("noextension"))).isNull()
        assertThat(ExtensionUtils.getExtension(File(".hidden"))).isNull()
        // Dot at end but no extension → null (kills boundary mutations on i < s.length()-1)
        assertThat(ExtensionUtils.getExtension(File("test."))).isNull()
    }
    @Test
    fun testApproveSelection() {
        assertThat(ExtensionUtils.approveSelection(File("test.yuv"))).isTrue()
        assertThat(ExtensionUtils.approveSelection(File("test.qcif"))).isTrue()
        assertThat(ExtensionUtils.approveSelection(File("test.SQCIF"))).isTrue()
        assertThat(ExtensionUtils.approveSelection(File("test.CIF"))).isTrue()
        assertThat(ExtensionUtils.approveSelection(File("test.txt"))).isFalse()
        // file without extension → false (covers the null-extension branch)
        assertThat(ExtensionUtils.approveSelection(File("noextension"))).isFalse()
    }
    @Test
    fun testGetDimension() {
        assertThat(ExtensionUtils.getDimension(File("test.sqcif"))).isEqualTo(YUVDeclaration.YUVNames.SQCIF.getDimension())
        assertThat(ExtensionUtils.getDimension(File("test.qcif"))).isEqualTo(YUVDeclaration.YUVNames.QCIF.getDimension())
        assertThat(ExtensionUtils.getDimension(File("test.sif"))).isEqualTo(YUVDeclaration.YUVNames.SIF.getDimension())
        assertThat(ExtensionUtils.getDimension(File("test.cif"))).isEqualTo(YUVDeclaration.YUVNames.CIF.getDimension())
        assertThat(ExtensionUtils.getDimension(File("test.cif4"))).isEqualTo(YUVDeclaration.YUVNames.CIF4.getDimension())
        assertThat(ExtensionUtils.getDimension(File("test.tv"))).isEqualTo(YUVDeclaration.YUVNames.TV.getDimension())
        assertThat(ExtensionUtils.getDimension(File("test.yuv"))).isNull()
    }
    @Test
    fun testGetNameEnding() {
        assertThat(ExtensionUtils.getNameEnding(File("movie.qcif"))).isEqualTo("movie.yuv")
        assertThat(ExtensionUtils.getNameEnding(File("test.yuv"))).isEqualTo("test.yuv")
        // no extension → null
        assertThat(ExtensionUtils.getNameEnding(File("noextension"))).isNull()
        // dot at start (hidden file) → null
        assertThat(ExtensionUtils.getNameEnding(File(".hidden"))).isNull()
        // dot at end, empty extension → null
        assertThat(ExtensionUtils.getNameEnding(File("test."))).isNull()
    }
    @Test
    fun testGetBaseName() {
        assertThat(ExtensionUtils.getBaseName(File("/path/to/test.yuv"))).isEqualTo("/path/to/test")
        // no extension → null
        assertThat(ExtensionUtils.getBaseName(File("noextension"))).isNull()
        // dot at start (hidden file) → null
        assertThat(ExtensionUtils.getBaseName(File(".hidden"))).isNull()
        // dot at end, empty extension → null
        assertThat(ExtensionUtils.getBaseName(File("/path/to/test."))).isNull()
    }
    @Test
    fun testGetDirectoryPath(@TempDir tempDir: Path) {
        val file = tempDir.resolve("test.yuv").toFile()
        val dir = ExtensionUtils.getDirectoryPath(file)
        assertThat(dir.absolutePath).isEqualTo(tempDir.toFile().absolutePath)
    }

    @Test
    fun testGetDirectoryPathNoParent() {
        // File with no parent path → should return "." (covers the null-parent branch)
        val file = File("test.yuv")
        val dir = ExtensionUtils.getDirectoryPath(file)
        assertThat(dir.path).isEqualTo(".")
    }
    @Test
    fun testGetFilesInDirectory(@TempDir tempDir: Path) {
        val file1 = tempDir.resolve("test1.yuv").toFile()
        file1.createNewFile()
        val file2 = tempDir.resolve("test2.yuv").toFile()
        file2.createNewFile()
        val files = ExtensionUtils.getFilesInDirectory(file1)
        assertThat(files).hasSize(2)
        assertThat(files).contains(file1, file2)
    }
}