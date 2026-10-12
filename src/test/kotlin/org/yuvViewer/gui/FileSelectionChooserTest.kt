package org.yuvViewer.gui

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assumptions.assumeFalse
import org.junit.jupiter.api.io.TempDir
import java.awt.GraphicsEnvironment
import java.nio.file.Files
import java.nio.file.Path
import javax.swing.JFileChooser
import javax.swing.SwingUtilities
import javax.swing.UIManager
import javax.swing.UnsupportedLookAndFeelException

class FileSelectionChooserTest {
    @TempDir
    lateinit var directory: Path

    @Test
    fun `dialog button follows selection and directory changes still work`() {
        assumeFalse(GraphicsEnvironment.isHeadless(), "Dialog requires a display")
        val file = Files.createFile(directory.resolve("test.yuv")).toFile()
        val subdirectory = Files.createDirectory(directory.resolve("subdirectory")).toFile()
        SwingUtilities.invokeAndWait {
            val chooser = object : FileSelectionChooser() {
                fun createTestDialog() = createDialog(null)
            }
            chooser.currentDirectory = directory.toFile()
            chooser.approveButtonText = "Open"
            val dialog = chooser.createTestDialog()
            try {
                val button = dialog.rootPane.defaultButton
                assertThat(button).isSameAs(chooser.ui.getDefaultButton(chooser))
                assertThat(button.isEnabled).isFalse
                chooser.selectedFile = file
                assertThat(button.isEnabled).isTrue
                chooser.selectedFile = null
                assertThat(button.isEnabled).isFalse

                chooser.selectedFile = subdirectory
                chooser.currentDirectory = subdirectory
                assertThat(chooser.currentDirectory).isEqualTo(subdirectory)
                assertThat(button.isEnabled).isFalse
            } finally {
                dialog.dispose()
            }
        }
    }

    @Test
    fun `open is enabled only while an existing file is selected`() {
        val file = Files.createFile(directory.resolve("test.yuv")).toFile()
        SwingUtilities.invokeAndWait {
            val chooser = FileSelectionChooser()
            chooser.currentDirectory = directory.toFile()
            val button = chooser.ui.getDefaultButton(chooser)
            assertThat(button).isNotNull
            assertThat(button.isEnabled).isFalse

            chooser.selectedFile = file
            assertThat(button.isEnabled).isTrue

            chooser.selectedFile = null
            assertThat(button.isEnabled).isFalse

            chooser.selectedFile = directory.toFile()
            assertThat(button.isEnabled).isFalse

            chooser.selectedFile = directory.resolve("missing.yuv").toFile()
            assertThat(button.isEnabled).isFalse

            chooser.selectedFile = file
            chooser.currentDirectory = Files.createDirectory(directory.resolve("subdirectory")).toFile()
            assertThat(button.isEnabled).isFalse
        }
    }

    @Test
    fun `approval cannot bypass the selection check and cancellation still works`() {
        val file = Files.createFile(directory.resolve("test.yuv")).toFile()
        SwingUtilities.invokeAndWait {
            val chooser = FileSelectionChooser()
            val commands = mutableListOf<String>()
            chooser.addActionListener { commands.add(it.actionCommand) }

            chooser.approveSelection()
            chooser.selectedFile = directory.toFile()
            chooser.approveSelection()
            chooser.selectedFile = directory.resolve("missing.yuv").toFile()
            chooser.approveSelection()
            assertThat(commands).isEmpty()

            chooser.selectedFile = file
            chooser.approveSelection()
            assertThat(commands).containsExactly(JFileChooser.APPROVE_SELECTION)

            Files.delete(file.toPath())
            chooser.approveSelection()
            assertThat(commands).containsExactly(JFileChooser.APPROVE_SELECTION)
            assertThat(chooser.ui.getDefaultButton(chooser).isEnabled).isFalse

            chooser.cancelSelection()
            assertThat(commands).containsExactly(
                JFileChooser.APPROVE_SELECTION, JFileChooser.CANCEL_SELECTION
            )
        }
    }

    @Test
    fun `selection state is preserved across supported look and feels`() {
        val file = Files.createFile(directory.resolve("test.yuv")).toFile()
        SwingUtilities.invokeAndWait {
            val original = UIManager.getLookAndFeel()
            try {
                for (info in UIManager.getInstalledLookAndFeels()) {
                    try {
                        UIManager.setLookAndFeel(info.className)
                    } catch (_: UnsupportedLookAndFeelException) {
                        continue
                    }
                    val chooser = FileSelectionChooser()
                    assertThat(chooser.ui.getDefaultButton(chooser).isEnabled)
                        .describedAs(info.name).isFalse
                    chooser.selectedFile = file
                    assertThat(chooser.ui.getDefaultButton(chooser).isEnabled)
                        .describedAs(info.name).isTrue
                    chooser.updateUI()
                    assertThat(chooser.ui.getDefaultButton(chooser).isEnabled)
                        .describedAs(info.name).isTrue
                    chooser.selectedFile = null
                    assertThat(chooser.ui.getDefaultButton(chooser).isEnabled)
                        .describedAs(info.name).isFalse
                    chooser.updateUI()
                    assertThat(chooser.ui.getDefaultButton(chooser).isEnabled)
                        .describedAs(info.name).isFalse
                }
            } finally {
                UIManager.setLookAndFeel(original)
            }
        }
    }
}
