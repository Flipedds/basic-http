package core.logs

import core.enums.LogColors
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object BasicLog {
    inline fun <reified T> getLogWithColorFor(color: LogColors, msg: String){
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
        println("${color.ansiCode}[${T::class.java.simpleName}] " +
                "${LocalDateTime.now().format(formatter)} " +
                "- $msg ${LogColors.RESET.ansiCode}"
        )
    }
}