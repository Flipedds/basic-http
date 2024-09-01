package core.logs

import core.enums.LogColors
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object BasicLog {
    inline fun <reified T> getLogWithColorFor(color: String, msg: String){
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
        println("${color}[${T::class.java.simpleName}] " +
                "${LocalDateTime.now().format(formatter)} " +
                "- $msg ${LogColors.RESET.ansiCode}"
        )
    }
}