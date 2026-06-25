package org.yuvViewer

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.yuvViewer.gui.MainFrame
import java.awt.Frame
import java.awt.GraphicsEnvironment

class MainTest {

    @BeforeEach
    fun init() {
        logger.info("@BeforeEach - executes before each test method in this class")
    }

    @AfterEach
    fun tearDown() {
        Frame.getFrames().forEach { it.dispose() }
    }

    @Test
    fun createMainFrameTest() {
        Assumptions.assumeFalse(GraphicsEnvironment.isHeadless(), "Skipping test in headless environment")
        Main.createMainFrame()

        val frames = Frame.getFrames()
        assertThat(frames).anyMatch { f ->
            f is MainFrame && f.isVisible && f.title == "YUV Viewer"
        }
    }

    companion object {
        private val logger: Logger = LogManager.getLogger(MainTest::class.java)

        @BeforeAll
        @JvmStatic
        fun initAll() {
            logger.info("@BeforeAll - executes once before all test methods in this class")
        }
    }
}
