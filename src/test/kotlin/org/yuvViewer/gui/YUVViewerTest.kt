package org.yuvViewer.gui

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.yuvViewer.utils.YUVDeclaration
import java.awt.Dimension
import java.awt.GraphicsEnvironment
import java.awt.Window
import java.io.File
import java.nio.file.Files
import javax.swing.SwingUtilities

class YUVViewerTest {

    private var mainFrame: MainFrame? = null
    private var yuvViewer: YUVViewer? = null
    private var tempYuvFile: File? = null
    private var createdTempFile = false

    @BeforeEach
    fun setUp() {
        Assumptions.assumeFalse(GraphicsEnvironment.isHeadless(), "Skipping test in headless environment")
        mainFrame = MainFrame()

        // Use the existing test file or create a dummy one
        val sourceFile = File("test_176x144.yuv")
        if (sourceFile.exists()) {
            tempYuvFile = sourceFile
            createdTempFile = false
        } else {
            // Create a dummy YUV file (176x144, 4:2:0)
            // Y: 176*144, U: 176*144/4, V: 176*144/4
            val size = 176 * 144 + (176 * 144 / 2)
            tempYuvFile = File.createTempFile("test", ".yuv")
            Files.write(tempYuvFile!!.toPath(), ByteArray(size))
            createdTempFile = true
        }

        yuvViewer = YUVViewer(mainFrame, tempYuvFile, Dimension(176, 144), YUVDeclaration.CC_YUV)
    }

    @AfterEach
    fun tearDown() {
        yuvViewer?.dispose()
        mainFrame?.dispose()
        if (createdTempFile) {
            tempYuvFile?.delete()
        }
        Window.getWindows().forEach { it.dispose() }
    }

    @Test
    fun testInitialization() {
        assertThat(yuvViewer).isNotNull
        assertThat(yuvViewer?.isVisible).isTrue()
        assertThat(yuvViewer?.size).isEqualTo(Dimension(176, 144))
    }

    @Test
    fun testScaleUpdate() {
        assertThat(yuvViewer?.size).isEqualTo(Dimension(176, 144))

        SwingUtilities.invokeAndWait {
            yuvViewer?.setScale(2)
        }

        // Check scale field via reflection to verify setScale was called and internal state updated
        val scaleField = YUVViewer::class.java.getDeclaredField("scale")
        scaleField.isAccessible = true
        val currentScale = scaleField.get(yuvViewer) as Int
        assertThat(currentScale).isEqualTo(2)

        // Note: We don't strictly check yuvViewer?.size here because in some
        // headless/CI environments, the window manager might ignore setSize()
        // calls on top-level Windows.
    }

    @Test
    fun testVisibilityToggles() {
        yuvViewer?.setY(false)
        // We can't easily check the internal state without reflection or changing the class,
        // but we can at least ensure it doesn't crash.
        yuvViewer?.setU(false)
        yuvViewer?.setV(false)

        yuvViewer?.setY(true)
        yuvViewer?.setU(true)
        yuvViewer?.setV(true)
    }

    @Test
    fun testNativeLinkage() {
        // This test verifies that the native methods are correctly linked and can be called.
        // We use a small buffer to avoid long execution times.
        val width = 16
        val height = 16
        val yData = ByteArray(width * height)
        val uData = ByteArray(width * height / 4)
        val vData = ByteArray(width * height / 4)
        val rgbImage = IntArray(width * height)

        val result = YUVViewer.calculateRGBImage(true, true, true, width, height, yData, uData, vData, rgbImage)
        assertThat(result).isNotNull
        assertThat(result.size).isEqualTo(width * height)
    }

    @Test
    fun testFastNativeLinkage() {
        val width = 16
        val height = 16
        val yData = ByteArray(width * height)
        val uData = ByteArray(width * height / 4)
        val vData = ByteArray(width * height / 4)
        val rgbImage = IntArray(width * height)

        val result = YUVViewer.calculateFastRGBImage(true, true, true, width, height, yData, uData, vData, rgbImage)
        assertThat(result).isNotNull
        assertThat(result.size).isEqualTo(width * height)

        val resultColored = YUVViewer.calculateFastColoredRGBImage(width, height, yData, uData, vData, rgbImage)
        assertThat(resultColored).isNotNull
        assertThat(resultColored.size).isEqualTo(width * height)
    }
}
