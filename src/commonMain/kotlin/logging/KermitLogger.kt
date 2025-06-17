package garden.ephemeral.calculator.logging

import co.touchlab.kermit.Logger
import co.touchlab.kermit.Severity

private fun SimpleLogger.LogLevel.toKermitSeverity() = when (this) {
    SimpleLogger.LogLevel.Verbose -> Severity.Verbose
    SimpleLogger.LogLevel.Debug -> Severity.Debug
    SimpleLogger.LogLevel.Info -> Severity.Info
    SimpleLogger.LogLevel.Warning -> Severity.Warn
    SimpleLogger.LogLevel.Error -> Severity.Error
}

internal open class KermitLogger(private val ourTag: String) : SimpleLogger {
    override fun withSubTag(subTag: String): SimpleLogger {
        val newTag = if (ourTag.isEmpty()) subTag else "$ourTag.$subTag"
        return KermitLogger(newTag)
    }

    override fun isLoggable(level: SimpleLogger.LogLevel) =
        level.toKermitSeverity() >= Logger.config.minSeverity

    override fun log(level: SimpleLogger.LogLevel, throwable: Throwable?, message: () -> String) {
        Logger.logBlock(
            severity = level.toKermitSeverity(),
            tag = ourTag,
            throwable = throwable,
            message = message,
        )
    }

    companion object : KermitLogger("")
}
