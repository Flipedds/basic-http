package core.enums

import org.junit.jupiter.api.Test
import shared.BaseTest

class LogColorsTest: BaseTest() {
    @Test
    fun `should get a correct ansi code for RED`() {
        "\u001B[31m" eq LogColors.RED.ansiCode
    }

    @Test
    fun `should get a correct ansi code for GREEN`() {
        "\u001B[32m" eq LogColors.GREEN.ansiCode
    }

    @Test
    fun `should get a correct ansi code for YELLOW`() {
        "\u001B[33m" eq LogColors.YELLOW.ansiCode
    }

    @Test
    fun `should get a correct ansi code for BLUE`() {
        "\u001B[34m" eq LogColors.BLUE.ansiCode
    }

    @Test
    fun `should get a correct ansi code for RESET`() {
        "\u001B[0m" eq LogColors.RESET.ansiCode
    }
}