package demo 

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test

@Tag("unitTest")
class MessageServiceTest {


    @BeforeEach
    fun configureSystemUnderTest() {
    }

    @Test
    @DisplayName("Should return the correct message")
    fun shouldReturnCorrectMessage() {
        val message = "Hello World!"
        assertThat(message).isEqualTo("Hello World!")
    }
}
