package common

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * BasicLog class is a singleton class that provides a basic log with color
 * @constructor Creates a basic log with color
 */
object BasicLog {
    inline fun <reified T> getLogWithColorFor(color: LogColors, msg: String){
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
        println("${color.ansiCode}[${T::class.java.simpleName}] " +
                "${LocalDateTime.now().format(formatter)} " +
                "- $msg ${LogColors.RESET.ansiCode}"
        )
    }
}