package garden.ephemeral.calculator.logging

interface SimpleLogger {
    enum class LogLevel {
        Verbose,
        Debug,
        Info,
        Warning,
        Error,
    }

    fun withSubTag(subTag: String): SimpleLogger

    fun isLoggable(level: LogLevel): Boolean

    fun log(level: LogLevel, throwable: Throwable? = null, message: () -> String)

    fun verbose(message: () -> String) = log(level = LogLevel.Verbose, message = message)
    fun debug(message: () -> String) = log(level = LogLevel.Debug, message = message)
    fun info(message: () -> String) = log(level = LogLevel.Info, message = message)
    fun warning(message: () -> String) = log(level = LogLevel.Warning, message = message)
    fun error(message: () -> String) = log(level = LogLevel.Error, message = message)


}
