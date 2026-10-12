package org.yuvViewer

import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.core.LogEvent
import org.apache.logging.log4j.core.LoggerContext
import org.apache.logging.log4j.core.appender.AbstractAppender
import org.apache.logging.log4j.core.appender.ConsoleAppender
import org.apache.logging.log4j.core.config.Property
import org.apache.logging.log4j.core.layout.PatternLayout
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.yuvViewer.utils.WholeNumberTextField
import javax.swing.SwingUtilities

class LoggingTest {
    @Test
    fun `bundled configuration enables info and sends exceptions to stderr`() {
        val context = LogManager.getContext(false) as LoggerContext
        val configuration = context.configuration
        assertThat(configuration.name).isEqualTo("yuvviewer")
        assertThat(configuration.rootLogger.level).isEqualTo(Level.INFO)
        val console = configuration.rootLogger.appenders["console"] as ConsoleAppender
        assertThat(console.target).isEqualTo(ConsoleAppender.Target.SYSTEM_ERR)
        val layout = console.layout as PatternLayout
        val exception = IllegalStateException("logging test")
        val event = org.apache.logging.log4j.core.impl.Log4jLogEvent.newBuilder()
            .setLoggerName(LoggingTest::class.java.name)
            .setLevel(Level.ERROR)
            .setMessage(org.apache.logging.log4j.message.SimpleMessage("Test failure"))
            .setThrown(exception)
            .build()
        val output = layout.toSerializable(event)
        assertThat(output).contains("ERROR", "LoggingTest", "Test failure",
            "java.lang.IllegalStateException: logging test", "at ")
    }

    @Test
    fun `non digit input is logged at debug without changing accepted input`() {
        val logger = LogManager.getLogger(WholeNumberTextField::class.java)
            as org.apache.logging.log4j.core.Logger
        val events = mutableListOf<LogEvent>()
        val appender = object : AbstractAppender(
            "logging-test", null, PatternLayout.createDefaultLayout(), false, Property.EMPTY_ARRAY
        ) {
            override fun append(event: LogEvent) {
                events.add(event.toImmutable())
            }
        }
        val previousLevel = logger.level
        val previousAdditive = logger.isAdditive
        appender.start()
        logger.addAppender(appender)
        logger.level = Level.DEBUG
        logger.isAdditive = false
        try {
            SwingUtilities.invokeAndWait {
                val field = WholeNumberTextField(5)
                field.document.insertString(0, "12a3", null)
                assertThat(field.text).isEqualTo("123")
            }
            assertThat(events).hasSize(1)
            assertThat(events.single().level).isEqualTo(Level.DEBUG)
            assertThat(events.single().message.formattedMessage)
                .isEqualTo("Ignoring non-digit input: a")
        } finally {
            logger.removeAppender(appender)
            logger.level = previousLevel
            logger.isAdditive = previousAdditive
            appender.stop()
        }
    }
}
