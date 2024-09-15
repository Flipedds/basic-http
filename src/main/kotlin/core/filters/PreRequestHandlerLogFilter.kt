package core.filters

import com.sun.net.httpserver.Filter
import com.sun.net.httpserver.HttpExchange
import core.enums.LogColors
import core.logs.BasicLog
import java.io.IOException
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.jvm.Throws

class PreRequestHandlerLogFilter: Filter() {
    @Throws(IOException::class, IllegalArgumentException::class)
    override fun doFilter(exchange: HttpExchange?, chain: Chain?) {
        BasicLog.getLogWithColorFor<PreRequestHandlerLogFilter>(
            LogColors.GREEN,
            "Request to -> ${exchange?.requestURI?.path} with method: ${exchange?.requestMethod} at ${
                LocalTime.now().format(
                    DateTimeFormatter.ofPattern("HH:mm:ss")
                )
            }"
        )
        chain?.doFilter(exchange)
    }

    override fun description(): String {
        return "Pre Request Handler Log Filter"
    }
}