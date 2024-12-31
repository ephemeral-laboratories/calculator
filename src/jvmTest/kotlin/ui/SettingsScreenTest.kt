package garden.ephemeral.calculator.ui

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.russhwolf.settings.MapSettings
import io.kotest.matchers.shouldBe
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SettingsScreenTest {
    // XXX: Forced to use JUnit 4 for tests here because Compose only provides JUnit 4 support
    @get:Rule
    val compose = createComposeRule()

    private lateinit var appState: AppState

    @Before
    fun setUp() {
        compose.setContent {
            appState = rememberAppState(settings = MapSettings())
            SettingsScreen(appState = appState)
        }
    }

    @Test
    fun `changing theme to light mode`() {
        compose.onNodeWithTag("ThemeDropDown").performClick()
        compose.onNodeWithTag("ThemeDropDownMenuItem.LIGHT").performClick()
        appState.themeOption shouldBe ThemeOption.LIGHT
    }

    @Test
    fun `changing theme to dark mode`() {
        compose.onNodeWithTag("ThemeDropDown").performClick()
        compose.onNodeWithTag("ThemeDropDownMenuItem.DARK").performClick()
        appState.themeOption shouldBe ThemeOption.DARK
    }

    @Test
    fun `changing number format to dozenal`() {
        compose.onNodeWithTag("NumberFormatDropDown").performClick()
        compose.onNodeWithTag("NumberFormatDropDownMenuItem.DOZENAL").performClick()
        appState.numberFormatOption shouldBe NumberFormatOption.DOZENAL
    }

    @Test
    fun `changing decimal radix separator to comma`() {
        compose.onNodeWithTag("RadixSeparatorDropDown").performClick()
        compose.onNodeWithTag("RadixSeparatorDropDownMenuItem.COMMA").performClick()
        appState.decimalRadixSeparatorOption shouldBe RadixSeparatorOption.COMMA
    }

    @Test
    fun `changing dozenal radix separator to comma`() {
        compose.onNodeWithTag("NumberFormatDropDown").performClick()
        compose.onNodeWithTag("NumberFormatDropDownMenuItem.DOZENAL").performClick()
        compose.onNodeWithTag("RadixSeparatorDropDown").performClick()
        compose.onNodeWithTag("RadixSeparatorDropDownMenuItem.COMMA").performClick()
        appState.dozenalRadixSeparatorOption shouldBe RadixSeparatorOption.COMMA
    }

    @Test
    fun `changing crash reporting to enabled by clicking the checkbox`() {
        compose.onNodeWithTag("EnableCrashReportingCheckbox").performClick()
        appState.enableCrashReporting shouldBe true
    }

    @Test
    fun `changing crash reporting back to disabled by clicking the checkbox again`() {
        compose.onNodeWithTag("EnableCrashReportingCheckbox").performClick()
        compose.onNodeWithTag("EnableCrashReportingCheckbox").performClick()
        appState.enableCrashReporting shouldBe false
    }

    @Test
    fun `changing crash reporting to enabled by clicking the checkbox label`() {
        compose.onNodeWithTag("EnableCrashReportingCheckboxLabel").performClick()
        appState.enableCrashReporting shouldBe true
    }

    @Test
    fun `changing crash reporting back to disabled by clicking the checkbox label again`() {
        compose.onNodeWithTag("EnableCrashReportingCheckboxLabel").performClick()
        compose.onNodeWithTag("EnableCrashReportingCheckboxLabel").performClick()
        appState.enableCrashReporting shouldBe false
    }
}
