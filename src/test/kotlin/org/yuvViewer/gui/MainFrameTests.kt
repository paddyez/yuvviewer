package org.yuvViewer.gui
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.yuvViewer.utils.YUVDeclaration
import java.awt.Dimension
import java.awt.GraphicsEnvironment
import java.awt.event.ActionEvent
import java.io.File
import java.nio.file.Files
import javax.swing.*
class MainFrameTests {
    private var mainFrame: MainFrame? = null
    private var tempYuvFile: File? = null
    private var createdTempFile = false

    @BeforeEach
    fun setUp() {
        Assumptions.assumeFalse(GraphicsEnvironment.isHeadless(), "Skipping test in headless environment")
        mainFrame = MainFrame()
    }
    @AfterEach
    fun tearDown() {
        mainFrame?.dispose()
        if (createdTempFile) {
            tempYuvFile?.delete()
            createdTempFile = false
        }
        java.awt.Window.getWindows().forEach { it.dispose() }
    }

    /** Opens a YUV file in the viewer by simulating what jMenuFileOpen() does internally. */
    private fun loadViewer(dimension: Dimension = Dimension(176, 144)) {
        val sourceFile = File("test_176x144.yuv")
        if (sourceFile.exists()) {
            tempYuvFile = sourceFile
        } else {
            val size = dimension.width * dimension.height + dimension.width * dimension.height / 2
            tempYuvFile = File.createTempFile("test", ".yuv")
            Files.write(tempYuvFile!!.toPath(), ByteArray(size))
            createdTempFile = true
        }
        mainFrame!!.yuvDimension = dimension
        mainFrame!!.colorSpace = YUVDeclaration.CC_YUV
        // access private yuvFile field via reflection
        val yuvFileField = MainFrame::class.java.getDeclaredField("yuvFile")
        yuvFileField.isAccessible = true
        yuvFileField.set(mainFrame, tempYuvFile)
        SwingUtilities.invokeAndWait { mainFrame!!.startupViewer() }
    }

    private fun fireAction(command: String) {
        mainFrame!!.actionPerformed(ActionEvent(mainFrame!!, ActionEvent.ACTION_PERFORMED, command))
    }

    // ── Tests ohne Viewer ──────────────────────────────────────────────────────

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
        assertThat(buttonTooltips).contains("step one frame back", "play", "step one frame forward", "pause", "rewind")
    }

    /** Scale without viewer → Scale 1:1 radio button should be re-selected */
    @Test
    fun testScaleWithoutViewerResetsSelection() {
        val menuBar = mainFrame!!.jMenuBar
        val toolsMenu = (0 until menuBar.menuCount).map { menuBar.getMenu(it) }.find { it.text == "Tools" }!!
        val scale2Item = (0 until toolsMenu.itemCount).mapNotNull { toolsMenu.getItem(it) }.find { it.text == "Scale 1:2" } as JRadioButtonMenuItem
        val scale1Item = (0 until toolsMenu.itemCount).mapNotNull { toolsMenu.getItem(it) }.find { it.text == "Scale 1:1" } as JRadioButtonMenuItem
        scale2Item.isSelected = true
        fireAction("Scale 1:2")
        assertThat(scale1Item.isSelected).isTrue()
        fireAction("Scale 1:4")
        assertThat(scale1Item.isSelected).isTrue()
        fireAction("Scale 1:8")
        assertThat(scale1Item.isSelected).isTrue()
    }

    /** Show Y/U/V without viewer → checkboxes stay checked */
    @Test
    fun testShowYUVWithoutViewerResetsCheckboxes() {
        val menuBar = mainFrame!!.jMenuBar
        val toolsMenu = (0 until menuBar.menuCount).map { menuBar.getMenu(it) }.find { it.text == "Tools" }!!
        val showY = (0 until toolsMenu.itemCount).mapNotNull { toolsMenu.getItem(it) }.find { it.text == "Show Y" } as JCheckBoxMenuItem
        val showU = (0 until toolsMenu.itemCount).mapNotNull { toolsMenu.getItem(it) }.find { it.text == "Show U" } as JCheckBoxMenuItem
        val showV = (0 until toolsMenu.itemCount).mapNotNull { toolsMenu.getItem(it) }.find { it.text == "Show V" } as JCheckBoxMenuItem
        showY.state = false
        fireAction("Show Y")
        assertThat(showY.state).isTrue()
        showU.state = false
        fireAction("Show U")
        assertThat(showU.state).isTrue()
        showV.state = false
        fireAction("Show V")
        assertThat(showV.state).isTrue()
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
        val aboutBox = java.awt.Window.getWindows().filterIsInstance<FrameAboutBox>().firstOrNull()
        assertThat(aboutBox?.isVisible ?: false).isFalse()
        SwingUtilities.invokeLater {
            aboutItem?.actionListeners?.forEach {
                it.actionPerformed(ActionEvent(aboutItem, ActionEvent.ACTION_PERFORMED, "About"))
            }
        }
        var visible = false
        for (i in 1..20) {
            if (java.awt.Window.getWindows().filterIsInstance<FrameAboutBox>().any { it.isVisible }) {
                visible = true
                break
            }
            Thread.sleep(100)
        }
        assertThat(visible).`as`("About dialog should be visible").isTrue()
        SwingUtilities.invokeLater {
            java.awt.Window.getWindows().filterIsInstance<FrameAboutBox>().forEach { it.dispose() }
        }
    }

    // ── Tests mit Viewer ───────────────────────────────────────────────────────

    @Test
    fun testScaleWithViewer() {
        loadViewer()
        SwingUtilities.invokeAndWait { fireAction("Scale 1:2") }
        val scaleField = MainFrame::class.java.getDeclaredField("yuvViewer")
        scaleField.isAccessible = true
        val viewer = scaleField.get(mainFrame) as YUVViewer
        val viewerScaleField = YUVViewer::class.java.getDeclaredField("scale")
        viewerScaleField.isAccessible = true
        assertThat(viewerScaleField.get(viewer) as Int).isEqualTo(2)

        SwingUtilities.invokeAndWait { fireAction("Scale 1:4") }
        assertThat(viewerScaleField.get(viewer) as Int).isEqualTo(4)

        SwingUtilities.invokeAndWait { fireAction("Scale 1:8") }
        assertThat(viewerScaleField.get(viewer) as Int).isEqualTo(8)

        SwingUtilities.invokeAndWait { fireAction("Scale 1:1") }
        assertThat(viewerScaleField.get(viewer) as Int).isEqualTo(1)
    }

    @Test
    fun testShowYUVWithViewer() {
        loadViewer()
        val viewerField = MainFrame::class.java.getDeclaredField("yuvViewer")
        viewerField.isAccessible = true
        val viewer = viewerField.get(mainFrame) as YUVViewer
        val showYField = YUVViewer::class.java.getDeclaredField("showY")
        val showUField = YUVViewer::class.java.getDeclaredField("showU")
        val showVField = YUVViewer::class.java.getDeclaredField("showV")
        showYField.isAccessible = true; showUField.isAccessible = true; showVField.isAccessible = true

        // doClick() toggles the checkbox AND fires the action listener
        val menuBar = mainFrame!!.jMenuBar
        val toolsMenu = (0 until menuBar.menuCount).map { menuBar.getMenu(it) }.find { it.text == "Tools" }!!
        val showY = (0 until toolsMenu.itemCount).mapNotNull { toolsMenu.getItem(it) }.find { it.text == "Show Y" } as JCheckBoxMenuItem
        val showU = (0 until toolsMenu.itemCount).mapNotNull { toolsMenu.getItem(it) }.find { it.text == "Show U" } as JCheckBoxMenuItem
        val showV = (0 until toolsMenu.itemCount).mapNotNull { toolsMenu.getItem(it) }.find { it.text == "Show V" } as JCheckBoxMenuItem

        SwingUtilities.invokeAndWait { showY.doClick() }
        assertThat(showYField.get(viewer) as Boolean).isFalse()

        SwingUtilities.invokeAndWait { showU.doClick() }
        assertThat(showUField.get(viewer) as Boolean).isFalse()

        SwingUtilities.invokeAndWait { showV.doClick() }
        assertThat(showVField.get(viewer) as Boolean).isFalse()
    }

    @Test
    fun testStepForwardAndBack() {
        loadViewer()
        val frameTextField = MainFrame::class.java.getDeclaredField("frameText")
        frameTextField.isAccessible = true
        val textField = frameTextField.get(mainFrame) as org.yuvViewer.utils.WholeNumberTextField

        val framesBefore = textField.getValue()
        SwingUtilities.invokeAndWait { fireAction("StepForward") }
        assertThat(textField.getValue()).isGreaterThanOrEqualTo(framesBefore)

        SwingUtilities.invokeAndWait { fireAction("StepBack") }
    }

    @Test
    fun testRewind() {
        loadViewer()
        // Step forward a couple of frames first
        SwingUtilities.invokeAndWait { fireAction("StepForward") }
        SwingUtilities.invokeAndWait { fireAction("StepForward") }
        // Rewind should reset to frame 1
        SwingUtilities.invokeAndWait { fireAction("Rewind") }
        val frameTextField = MainFrame::class.java.getDeclaredField("frameText")
        frameTextField.isAccessible = true
        val textField = frameTextField.get(mainFrame) as org.yuvViewer.utils.WholeNumberTextField
        assertThat(textField.getValue()).isEqualTo(1)
    }

    @Test
    fun testPauseStartsAndStopsPlay() {
        loadViewer()
        val playField = MainFrame::class.java.getDeclaredField("play")
        playField.isAccessible = true

        // First Pause starts playback
        SwingUtilities.invokeAndWait { fireAction("Pause") }
        assertThat(playField.get(mainFrame)).isNotNull()

        // Second Pause stops playback
        SwingUtilities.invokeAndWait { fireAction("Pause") }
        assertThat(playField.get(mainFrame)).isNull()
    }

    @Test
    fun testDefaultActionCommand() {
        // Unknown command should not throw, just log to stderr
        assertDoesNotThrow { fireAction("UnknownCommand") }
    }

    // ── Helpers ────────────────────────────────────────────────────────────────

    private fun assertDoesNotThrow(block: () -> Unit) {
        try { block() } catch (e: Exception) { throw AssertionError("Expected no exception but got: $e", e) }
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