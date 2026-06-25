package org.yuvViewer.gui

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.yuvViewer.utils.YUVDeclaration
import java.awt.Dimension
import java.awt.GraphicsEnvironment
import java.awt.Window
import java.awt.event.ActionEvent
import javax.swing.JButton
import javax.swing.JRadioButton
import javax.swing.JTabbedPane
import javax.swing.JTextField

class SettingsDialogTest {

    private var mainFrame: MainFrame? = null
    private var settingsDialog: SettingsDialog? = null

    @BeforeEach
    fun setUp() {
        Assumptions.assumeFalse(GraphicsEnvironment.isHeadless(), "Skipping test in headless environment")
        mainFrame = MainFrame()
        settingsDialog = SettingsDialog(mainFrame)
    }

    @AfterEach
    fun tearDown() {
        settingsDialog?.dispose()
        mainFrame?.dispose()
        Window.getWindows().forEach { it.dispose() }
    }

    @Test
    fun testInitialization() {
        assertThat(settingsDialog?.title).isEqualTo("Settings only 4:2:0 enabled!")
        assertThat(settingsDialog?.isModal).isFalse()
    }

    @Test
    fun testDefaultVideoSize() {
        val qcifButton = findComponentByName(settingsDialog!!, "QCIF", JRadioButton::class.java)
        assertThat(qcifButton).isNotNull
        assertThat(qcifButton!!.isSelected).isTrue()
    }

    @Test
    fun testSelectDifferentPreset() {
        val cifButton = findComponentByName(settingsDialog!!, "CIF", JRadioButton::class.java)
        assertThat(cifButton).isNotNull

        cifButton!!.doClick()
        assertThat(cifButton.isSelected).isTrue()

        val okButton = findButtonByText(settingsDialog!!, "Ok")
        assertThat(okButton).isNotNull
        okButton!!.doClick()

        assertThat(mainFrame?.yuvDimension).isEqualTo(YUVDeclaration.YUVNames.CIF.getDimension())
    }

    @Test
    fun testCustomDimension() {
        val customButton = findComponentByName(settingsDialog!!, "CUSTOM", JRadioButton::class.java)
        assertThat(customButton).isNotNull
        customButton!!.doClick()

        val textFields = findComponents(settingsDialog!!, JTextField::class.java)
        // xText and yText are WholeNumberTextField which extend JTextField
        // We expect at least 2
        assertThat(textFields.size).isGreaterThanOrEqualTo(2)

        val xText = textFields[0]
        val yText = textFields[1]

        xText.text = "640"
        yText.text = "480"

        val okButton = findButtonByText(settingsDialog!!, "Ok")
        okButton!!.doClick()

        assertThat(mainFrame?.yuvDimension).isEqualTo(Dimension(640, 480))
    }

    @Test
    fun testColorSpaceSelection() {
        val tabbedPane = findComponent(settingsDialog!!, JTabbedPane::class.java)
        tabbedPane?.selectedIndex = 1 // Switch to Color tab

        val yOnlyButton = findAbstractButtonByText(settingsDialog!!, "Y-Only") as? JRadioButton
        assertThat(yOnlyButton).isNotNull
        yOnlyButton!!.doClick()

        val okButton = findButtonByText(settingsDialog!!, "Ok")
        okButton!!.doClick()

        assertThat(mainFrame?.colorSpace).isEqualTo(YUVDeclaration.CC_Y)
    }

    @Test
    fun testCancelButton() {
        val cancelButton = findButtonByText(settingsDialog!!, "Cancel")
        assertThat(cancelButton).isNotNull

        cancelButton!!.doClick()
        assertThat(settingsDialog?.isDisplayable).isFalse()
    }

    private fun findButtonByText(container: java.awt.Container, text: String): JButton? {
        return findComponents(container, JButton::class.java).find { it.text == text }
    }

    private fun findAbstractButtonByText(container: java.awt.Container, text: String): javax.swing.AbstractButton? {
        return findComponents(container, javax.swing.AbstractButton::class.java).find { it.text == text }
    }

    private fun <T : javax.swing.AbstractButton> findComponentByName(container: java.awt.Container, name: String, type: Class<T>): T? {
        return findComponents(container, type).find { it.text == name }
    }

    private fun <T> findComponent(container: java.awt.Container, type: Class<T>): T? {
        for (component in container.components) {
            if (type.isInstance(component)) {
                @Suppress("UNCHECKED_CAST")
                return component as T
            }
            if (component is java.awt.Container) {
                val found = findComponent(component, type)
                if (found != null) return found
            }
        }
        return null
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
