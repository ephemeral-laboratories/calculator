package garden.ephemeral.calculator.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.russhwolf.settings.MapSettings
import garden.ephemeral.calculator.util.get
import io.kotest.matchers.shouldBe
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MainUiSettingsTest {
    // XXX: Forced to use JUnit 4 for tests here because Compose only provides JUnit 4 support
    @get:Rule
    val compose = createComposeRule()

    private lateinit var settings: MapSettings
    private lateinit var appState: AppState

    @Before
    fun setUp() {
        settings = MapSettings()
        compose.setContent {
            appState = rememberAppState(settings = settings)
            MainUi(appState = appState)
        }
    }

    @Test
    fun `settings are not open by default`() {
        compose.onNodeWithTag("Settings").assertIsNotDisplayed()
    }

    @Test
    fun `opening settings`() {
        compose.onNodeWithTag("SettingsButton").performClick()
        compose.onNodeWithTag("Settings").assertIsDisplayed()
    }

    @Test
    fun `theme should default to system`() {
        appState.themeOption shouldBe ThemeOption.SYSTEM_DEFAULT
        settings["themeOption", ThemeOption.SYSTEM_DEFAULT] shouldBe ThemeOption.SYSTEM_DEFAULT
    }

    @Test
    fun `changing theme to light mode`() {
        compose.onNodeWithTag("SettingsButton").performClick()
        compose.onNodeWithTag("ThemeDropDown").performClick()
        compose.onNodeWithTag("ThemeDropDownMenuItem.LIGHT").performClick()
        appState.themeOption shouldBe ThemeOption.LIGHT
        settings["themeOption", ThemeOption.SYSTEM_DEFAULT] shouldBe ThemeOption.LIGHT
    }

    @Test
    fun `changing theme to dark mode`() {
        compose.onNodeWithTag("SettingsButton").performClick()
        compose.onNodeWithTag("ThemeDropDown").performClick()
        compose.onNodeWithTag("ThemeDropDownMenuItem.DARK").performClick()
        appState.themeOption shouldBe ThemeOption.DARK
        settings["themeOption", ThemeOption.SYSTEM_DEFAULT] shouldBe ThemeOption.DARK
    }

    @Test
    fun `number format should default to decimal`() {
        appState.numberFormatOption shouldBe NumberFormatOption.DECIMAL
        settings["numberFormatOption", NumberFormatOption.DECIMAL] shouldBe NumberFormatOption.DECIMAL
    }

    @Test
    fun `changing number format to dozenal`() {
        compose.onNodeWithTag("SettingsButton").performClick()
        compose.onNodeWithTag("NumberFormatDropDown").performClick()
        compose.onNodeWithTag("NumberFormatDropDownMenuItem.DOZENAL").performClick()
        appState.numberFormatOption shouldBe NumberFormatOption.DOZENAL
        settings["numberFormatOption", NumberFormatOption.DECIMAL] shouldBe NumberFormatOption.DOZENAL
    }

    @Test
    fun `default decimal radix separator should be period`() {
        appState.decimalRadixSeparatorOption shouldBe RadixSeparatorOption.PERIOD
        settings["decimalRadixSeparatorOption", RadixSeparatorOption.PERIOD] shouldBe RadixSeparatorOption.PERIOD
    }

    @Test
    fun `changing decimal radix separator to comma`() {
        compose.onNodeWithTag("SettingsButton").performClick()
        compose.onNodeWithTag("RadixSeparatorDropDown").performClick()
        compose.onNodeWithTag("RadixSeparatorDropDownMenuItem.COMMA").performClick()
        appState.decimalRadixSeparatorOption shouldBe RadixSeparatorOption.COMMA
        settings["decimalRadixSeparatorOption", RadixSeparatorOption.PERIOD] shouldBe RadixSeparatorOption.COMMA
    }

    @Test
    fun `default dozenal radix separator should be semicolon`() {
        appState.dozenalRadixSeparatorOption shouldBe RadixSeparatorOption.SEMICOLON
        settings["dozenalRadixSeparatorOption", RadixSeparatorOption.SEMICOLON] shouldBe RadixSeparatorOption.SEMICOLON
    }

    @Test
    fun `changing dozenal radix separator to comma`() {
        compose.onNodeWithTag("SettingsButton").performClick()
        compose.onNodeWithTag("NumberFormatDropDown").performClick()
        compose.onNodeWithTag("NumberFormatDropDownMenuItem.DOZENAL").performClick()
        compose.onNodeWithTag("RadixSeparatorDropDown").performClick()
        compose.onNodeWithTag("RadixSeparatorDropDownMenuItem.COMMA").performClick()
        appState.dozenalRadixSeparatorOption shouldBe RadixSeparatorOption.COMMA
        settings["dozenalRadixSeparatorOption", RadixSeparatorOption.PERIOD] shouldBe RadixSeparatorOption.COMMA
    }
}
