package garden.ephemeral.calculator

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.russhwolf.settings.PreferencesSettings
import garden.ephemeral.calculator.calculator.generated.resources.AppIcon
import garden.ephemeral.calculator.calculator.generated.resources.Res
import garden.ephemeral.calculator.logging.asSentryLogger
import garden.ephemeral.calculator.ui.MainUi
import garden.ephemeral.calculator.ui.errors.BetterErrorHandling
import garden.ephemeral.calculator.ui.rememberAppState
import garden.ephemeral.calculator.ui.theme.AppTheme
import io.sentry.Sentry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import java.util.prefs.Preferences
import kotlin.time.measureTime

private val preferencesNode = Preferences.userRoot().node("garden/ephemeral/calculator")

// `singleWindowApplication` doesn't work if I want an icon:
// https://github.com/JetBrains/compose-jb/issues/2369
fun main() = application {
    val appState = rememberAppState(settings = PreferencesSettings(preferencesNode))

    // Sentry initialisation is unfortunately very slow at times, and we don't want it blocking
    // UI if the user decides to click the checkbox fast.
    LaunchedEffect(appState.enableCrashReporting) {
        launch(Dispatchers.IO) {
            val sentryInitTime = measureTime {
                Sentry.init { options ->
                    options.isEnabled = appState.enableCrashReporting

                    options.dsn = BuildKonfig.SentryDSN
                    options.release = BuildKonfig.ApplicationName + '@' + BuildKonfig.Version
                    // Add later once we build more than one variety. I think it's freeform text?
                    // options.dist = "x86"

                    options.setLogger(Logger.asSentryLogger())
                }
            }
            Logger.info {
                val enabledOrDisabled = if (appState.enableCrashReporting) "enabled" else "disabled"
                "Time to (re)initialise Sentry with crash reporting $enabledOrDisabled: $sentryInitTime"
            }
        }
    }

    AppTheme(appState.themeOption) {
        BetterErrorHandling {
            Window(
                onCloseRequest = ::exitApplication,
                title = "Calculator",
                icon = painterResource(Res.drawable.AppIcon),
            ) {
                MainUi(appState)
            }
        }
    }
}
