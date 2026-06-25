package org.yuvViewer.gui
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import java.awt.Frame
import java.awt.GraphicsEnvironment
import java.awt.event.ActionEvent
import javax.swing.*
class MainFrameTests {
    private var mainFrame: MainFrame? = null
    @BeforeEach
    fun setUp() {
        Assumptions.assumeFalse(GraphicsEnvironment.isHeadless(), "Skipping test in headless environment")
        mainFrame = MainFrame()
    }
    @AfterEach
    fun tearDown() {
        mainFrame?.dispose()
        java.awt.Window.getWindows().forEach { it.dispose() }
    }
    @Test
    fun testInitialization() {
        assertThat(mainFrame?.title).isEqualTo("YUV Viewer")
        assertThat(mainFrame?.contentPane?.layout).isInstanceOf(java.awt.BorderLayout::class.java)
    }
    @Test
    fun testMenuBarPresence() {
        val menuBar = mainFrame?.jMenuBar
        assertThat(menuBar).isNotNull
        val menus = (0 until menuBar!!.menuCount).map { menuBar.getMenu(it).text }
        assertThat(menus).containsExactly("File", "Tools", "Options", "Help")
    }
    @Test
    fun testFileMenu() {
        val menuBar = mainFrame?.jMenuBar
        val fileMenu = (0 until menuBar!!.menuCount)
            .map { menuBar.getMenu(it) }
            .find { it.text == "File" }
        assertThat(fileMenu).isNotNull
        val items = (0 until fileMenu!!.itemCount)
            .mapNotNull { fileMenu.getItem(it)?.text }
        assertThat(items).contains("Open", "Exit")
    }
    @Test
    fun testToolsMenu() {
        val menuBar = mainFrame?.jMenuBar
        val toolsMenu = (0 until menuBar!!.menuCount)
            .map { menuBar.getMenu(it) }
            .find { it.text == "Tools" }
        assertThat(toolsMenu).isNotNull
        val items = (0 until toolsMenu!!.itemCount)
            .mapNotNull { toolsMenu.getItem(it)?.text }
        assertThat(items).contains("Scale 1:1", "Scale 1:2", "Scale 1:4", "Scale 1:8", "Show Y", "Show U", "Show V")
    }
    @Test
    fun testInternalFramePresence() {
        val desktopPane = findComponent(mainFrame!!, JDesktopPane::class.java)
        assertThat(desktopPane).isNotNull
        val internalFrames = desktopPane!!.allFrames
        val controlFrame = internalFrames.find { it.title == "Control" }
        assertThat(controlFrame).isNotNull
        val buttons = findComponents(controlFrame!!.contentPane, JButton::class.java)
        val buttonTooltips = buttons.map { it.toolTipText }
        assertThat(buttonTooltips).contains("step one frame back", "play", "step one frame foreward", "plause", "rewind")
    }
    @Test
    fun testAboutMenuAction() {
        val menuBar = mainFrame?.jMenuBar
        val helpMenu = (0 until menuBar!!.menuCount)
            .map { menuBar.getMenu(it) }
            .find { it.text == "Help" }
        assertThat(helpMenu).isNotNull
        val aboutItem = (0 until helpMenu!!.itemCount)
            .mapNotNull { helpMenu.getItem(it) }
            .find { it.text == "About" }
        assertThat(aboutItem).isNotNull
        // The AboutBox is created in MainFrame's constructor, so it should already exist but be hidden
        val aboutBox = java.awt.Window.getWindows().filterIsInstance<FrameAboutBox>().firstOrNull()
        assertThat(aboutBox?.isVisible ?: false).isFalse()
        // Trigger the About action
        // We must use invokeLater because MainFrame.jMenuHelpAbout() sets the dialog to modal,
        // which blocks the calling thread until the dialog is closed.
        SwingUtilities.invokeLater {
            aboutItem?.actionListeners?.forEach {
                it.actionPerformed(ActionEvent(aboutItem, ActionEvent.ACTION_PERFORMED, "About"))
            }
        }
        // Wait for the dialog to become visible (with timeout)
        var visible = false
        for (i in 1..20) {
            if (java.awt.Window.getWindows().filterIsInstance<FrameAboutBox>().any { it.isVisible }) {
                visible = true
                break
            }
            Thread.sleep(100)
        }
        assertThat(visible).`as`("About dialog should be visible").isTrue()
        // Clean up: close the dialog
        SwingUtilities.invokeLater {
            java.awt.Window.getWindows().filterIsInstance<FrameAboutBox>().forEach { it.dispose() }
        }
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
}