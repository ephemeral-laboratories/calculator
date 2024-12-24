package garden.ephemeral.calculator

import androidx.compose.runtime.remember
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import garden.ephemeral.calculator.calculator.generated.resources.AppIcon
import garden.ephemeral.calculator.calculator.generated.resources.Res
import garden.ephemeral.calculator.ui.AppState
import garden.ephemeral.calculator.ui.MainUi
import garden.ephemeral.calculator.ui.errors.BetterErrorHandling
import garden.ephemeral.calculator.ui.theme.AppTheme
import io.sentry.Sentry
import org.jetbrains.compose.resources.painterResource

// `singleWindowApplication` doesn't work if I want an icon:
// https://github.com/JetBrains/compose-jb/issues/2369
fun main() = application {
    // Sentry initialisation should occur at most once, but we also want to complete
    // its initialisation before anything else happens.
    remember {
        Sentry.init { options ->
            options.dsn = BuildKonfig.SentryDSN
            options.release = BuildKonfig.Version
            // TODO: To decide - decrease this value for production builds, but to what?
            options.tracesSampleRate = 1.0
        }
    }

    val appState = remember { AppState() }

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
