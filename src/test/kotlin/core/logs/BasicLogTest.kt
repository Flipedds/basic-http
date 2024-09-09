package core.logs

import core.enums.LogColors
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class BasicLogTest {
    private infix fun String.eq(expected: String) = assertEquals(expected, this)

    @Test
    fun `should show a green log`(){
        val outputStream = ByteArrayOutputStream()
        val originalOut = System.out
        System.setOut(PrintStream(outputStream))
        try {
            BasicLog.getLogWithColorFor<BasicLogTest>(LogColors.GREEN, "Hello, World!")
            val output = outputStream.toString().trim()
            val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
            "${LogColors.GREEN.ansiCode}[BasicLogTest] "+ LocalDateTime.now().format(formatter) + " - Hello, World! ${LogColors.RESET.ansiCode}" eq output

        } finally {
            System.setOut(originalOut)
        }
    }

    @Test
    fun `should show a red log`(){
        val outputStream = ByteArrayOutputStream()
        val originalOut = System.out
        System.setOut(PrintStream(outputStream))
        try {
            BasicLog.getLogWithColorFor<BasicLogTest>(LogColors.RED, "Hello, World!")
            val output = outputStream.toString().trim()
            val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
            "${LogColors.RED.ansiCode}[BasicLogTest] "+ LocalDateTime.now().format(formatter) + " - Hello, World! ${LogColors.RESET.ansiCode}" eq output

        } finally {
            System.setOut(originalOut)
        }
    }

    @Test
    fun `should show a yellow log`(){
        val outputStream = ByteArrayOutputStream()
        val originalOut = System.out
        System.setOut(PrintStream(outputStream))
        try {
            BasicLog.getLogWithColorFor<BasicLogTest>(LogColors.YELLOW, "Hello, World!")
            val output = outputStream.toString().trim()
            val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
            "${LogColors.YELLOW.ansiCode}[BasicLogTest] "+ LocalDateTime.now().format(formatter) + " - Hello, World! ${LogColors.RESET.ansiCode}" eq output

        } finally {
            System.setOut(originalOut)
        }
    }

    @Test
    fun `should show a blue log`(){
        val outputStream = ByteArrayOutputStream()
        val originalOut = System.out
        System.setOut(PrintStream(outputStream))
        try {
            BasicLog.getLogWithColorFor<BasicLogTest>(LogColors.BLUE, "Hello, World!")
            val output = outputStream.toString().trim()
            val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
            "${LogColors.BLUE.ansiCode}[BasicLogTest] "+ LocalDateTime.now().format(formatter) + " - Hello, World! ${LogColors.RESET.ansiCode}" eq output

        } finally {
            System.setOut(originalOut)
        }
    }
}