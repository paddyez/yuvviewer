package org.yuvViewer.gui
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import java.awt.GraphicsEnvironment
import java.awt.event.ActionEvent
import java.awt.event.WindowEvent
import javax.swing.JButton
import javax.swing.JLabel
class FrameAboutBoxTest {
    private var mainFrame: MainFrame? = null
    private var aboutBox: FrameAboutBox? = null
    @BeforeEach
    fun setUp() {
        Assumptions.assumeFalse(GraphicsEnvironment.isHeadless(), "Skipping test in headless environment")
        mainFrame = MainFrame()
        aboutBox = FrameAboutBox(mainFrame)
    }
    @AfterEach
    fun tearDown() {
        aboutBox?.dispose()
        mainFrame?.dispose()
        java.awt.Window.getWindows().forEach { it.dispose() }
    }
    @Test
    fun testInitialization() {
        assertThat(aboutBox?.title).isEqualTo("About")
        assertThat(aboutBox?.isModal).isFalse()
    }
    @Test
    fun testLabels() {
        val labels = findComponents(aboutBox!!, JLabel::class.java)
        val labelTexts = labels.map { it.text }
        assertThat(labelTexts).contains("YUV Viewer")
        assertThat(labelTexts).contains("1.0")
        assertThat(labelTexts).contains("Copyright © 2002")
        assertThat(labelTexts).contains("Versatile YUV viewing utility")
    }
    @Test
    fun testOkButtonAction() {
        val buttons = findComponents(aboutBox!!, JButton::class.java)
        val okButton = buttons.find { it.text == "Ok" }
        assertThat(okButton).isNotNull
        // Simulate click
        okButton?.actionListeners?.forEach {
            it.actionPerformed(ActionEvent(okButton, ActionEvent.ACTION_PERFORMED, "Ok"))
        }
        assertThat(aboutBox?.isDisplayable).isFalse()
    }
    @Test
    fun testWindowClosing() {
        aboutBox?.dispatchEvent(WindowEvent(aboutBox, WindowEvent.WINDOW_CLOSING))
        assertThat(aboutBox?.isDisplayable).isFalse()
    }
    private fun <T> findComponents(container: java.awt.Container, type: Class<T>): List<T> {
        val result = mutableListOf<T>()
        for (component in container.components) {
            if (type.isInstance(component)) {
                @Suppress("UNCHECKED_CAST")
                result.add(component as T)
            }
            if (component is java.awt.Container) {
                result.addAll(findComponents(component, type))
            }
        }
        return result
    }
}