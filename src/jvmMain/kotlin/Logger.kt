package garden.ephemeral.calculator

import io.sentry.ILogger
import io.sentry.SentryLevel
import java.util.logging.Level
import java.util.logging.Logger

private object DummyObject

//LogManager.getLogManager().readConfiguration()

//val Logger = LogManager.getLogManager().getLogger(DummyObject::class.java.packageName)

val Logger by lazy {
    java.util.logging.Logger.getLogger(DummyObject::class.java.packageName)
}

private class JulDelegatedSentryLogger(private val delegate: Logger) : ILogger {
    private fun SentryLevel.toJulLevel() = when (this) {
        SentryLevel.DEBUG -> Level.FINE
        SentryLevel.INFO -> Level.INFO
        SentryLevel.WARNING -> Level.WARNING
        SentryLevel.ERROR, SentryLevel.FATAL -> Level.SEVERE
    }

    override fun log(level: SentryLevel, message: String, vararg args: Any?) {
        delegate.log(level.toJulLevel()) { String.format(message, *args) }
    }

    override fun log(level: SentryLevel, message: String, throwable: Throwable?) {
        delegate.log(level.toJulLevel(), throwable) { message }
    }

    override fun log(level: SentryLevel, throwable: Throwable?, message: String, vararg args: Any?) {
        delegate.log(level.toJulLevel(), throwable) { String.format(message, *args) }
    }

    override fun isEnabled(level: SentryLevel?): Boolean {
        if (level == null) return false
        return delegate.isLoggable(level.toJulLevel())
    }
}

/**
 * Gets an adapter to Sentry's logger interface, thus allowing using this logger as a logger for Sentry.
 *
 * @receiver the Java `Logger`.
 * @return the Sentry `ILogger`.
 */
fun Logger.asSentryLogger(): ILogger = JulDelegatedSentryLogger(this)
