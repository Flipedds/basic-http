package core.server.filters

import com.sun.net.httpserver.Filter
import com.sun.net.httpserver.HttpExchange
import common.LogColors
import common.BasicLog
import java.io.IOException
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.jvm.Throws

/**
 * PreRequestHandlerLogFilter class is a filter class that logs the request to the server
 * before the request is handled by the server.
 */
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