package garden.ephemeral.calculator.logging

import io.sentry.ILogger
import io.sentry.SentryLevel

private fun SentryLevel.toLogLevel() = when (this) {
    SentryLevel.DEBUG -> SimpleLogger.LogLevel.Debug
    SentryLevel.INFO -> SimpleLogger.LogLevel.Info
    SentryLevel.WARNING -> SimpleLogger.LogLevel.Warning
    SentryLevel.ERROR, SentryLevel.FATAL -> SimpleLogger.LogLevel.Error
}

private class DelegatedSentryLogger(private val delegate: SimpleLogger) : ILogger {
    override fun log(level: SentryLevel, message: String, vararg args: Any?) {
        delegate.log(level.toLogLevel()) { String.format(message, *args) }
    }

    override fun log(level: SentryLevel, message: String, throwable: Throwable?) {
        delegate.log(level.toLogLevel(), throwable) { message }
    }

    override fun log(level: SentryLevel, throwable: Throwable?, message: String, vararg args: Any?) {
        delegate.log(level.toLogLevel(), throwable) { String.format(message, *args) }
    }

    override fun isEnabled(level: SentryLevel?): Boolean {
        if (level == null) return false
        return delegate.isLoggable(level.toLogLevel())
    }
}

fun SimpleLogger.asSentryLogger(): ILogger = DelegatedSentryLogger(this)
