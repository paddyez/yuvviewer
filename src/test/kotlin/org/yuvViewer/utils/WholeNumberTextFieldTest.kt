package org.yuvViewer.utils
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
class WholeNumberTextFieldTest {
    @Test
    fun testInitializationWithValue() {
        val field = WholeNumberTextField(123, 10)
        assertThat(field.text).isEqualTo("123")
        assertThat(field.getValue()).isEqualTo(123)
        assertThat(field.columns).isEqualTo(10)
    }
    @Test
    fun testInitializationWithColumns() {
        val field = WholeNumberTextField(5)
        assertThat(field.text).isEmpty()
        assertThat(field.columns).isEqualTo(5)
    }
    @Test
    fun testSetValue() {
        val field = WholeNumberTextField(10)
        field.setValue(456)
        assertThat(field.text).isEqualTo("456")
        assertThat(field.getValue()).isEqualTo(456)
    }
    @Test
    fun testSetValueLarge() {
        val field = WholeNumberTextField(10)
        field.setValue(1234)
        // Note: NumberFormat(Locale.US) would format as "1,234", but WholeNumberDocument strips commas
        assertThat(field.text).isEqualTo("1234")
        assertThat(field.getValue()).isEqualTo(1234)
    }
    @Test
    fun testInsertDigits() {
        val field = WholeNumberTextField(10)
        field.document.insertString(0, "123", null)
        assertThat(field.text).isEqualTo("123")
        assertThat(field.getValue()).isEqualTo(123)
    }
    @Test
    fun testInsertNonDigits() {
        val field = WholeNumberTextField(10)
        field.document.insertString(0, "12a3b", null)
        assertThat(field.text).isEqualTo("123")
        assertThat(field.getValue()).isEqualTo(123)
    }
    @Test
    fun testInsertOnlyNonDigits() {
        val field = WholeNumberTextField(10)
        field.document.insertString(0, "abc", null)
        assertThat(field.text).isEmpty()
        assertThat(field.getValue()).isEqualTo(0)
    }
}