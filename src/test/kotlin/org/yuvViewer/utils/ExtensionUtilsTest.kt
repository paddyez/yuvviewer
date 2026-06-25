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
    }
    @Test
    fun testApproveSelection() {
        assertThat(ExtensionUtils.approveSelection(File("test.yuv"))).isTrue()
        assertThat(ExtensionUtils.approveSelection(File("test.qcif"))).isTrue()
        assertThat(ExtensionUtils.approveSelection(File("test.SQCIF"))).isTrue()
        assertThat(ExtensionUtils.approveSelection(File("test.CIF"))).isTrue()
        assertThat(ExtensionUtils.approveSelection(File("test.txt"))).isFalse()
    }
    @Test
    fun testGetDimension() {
        assertThat(ExtensionUtils.getDimension(File("test.sqcif"))).isEqualTo(YUVDeclaration.SQCIF_DIMENSION)
        assertThat(ExtensionUtils.getDimension(File("test.qcif"))).isEqualTo(YUVDeclaration.QCIF_DIMENSION)
        assertThat(ExtensionUtils.getDimension(File("test.sif"))).isEqualTo(YUVDeclaration.SIF_DIMENSION)
        assertThat(ExtensionUtils.getDimension(File("test.cif"))).isEqualTo(YUVDeclaration.CIF_DIMENSION)
        assertThat(ExtensionUtils.getDimension(File("test.cif4"))).isEqualTo(YUVDeclaration.CIF4_DIMENSION)
        assertThat(ExtensionUtils.getDimension(File("test.tv"))).isEqualTo(YUVDeclaration.TV_DIMENSION)
        assertThat(ExtensionUtils.getDimension(File("test.yuv"))).isNull()
    }
    @Test
    fun testGetNameEnding() {
        assertThat(ExtensionUtils.getNameEnding(File("movie.qcif"))).isEqualTo("movie.yuv")
        assertThat(ExtensionUtils.getNameEnding(File("test.yuv"))).isEqualTo("test.yuv")
    }
    @Test
    fun testGetBaseName() {
        assertThat(ExtensionUtils.getBaseName(File("/path/to/test.yuv"))).isEqualTo("/path/to/test")
    }
    @Test
    fun testGetDirectoryPath(@TempDir tempDir: Path) {
        val file = tempDir.resolve("test.yuv").toFile()
        val dir = ExtensionUtils.getDirectoryPath(file)
        assertThat(dir.absolutePath).isEqualTo(tempDir.toFile().absolutePath)
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