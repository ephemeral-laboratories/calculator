package garden.ephemeral.calculator.ui

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performTextInput
import garden.ephemeral.calculator.ui.theme.md_theme_dark_surface
import garden.ephemeral.calculator.ui.theme.md_theme_light_surface
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MainUiTest {
    // XXX: Forced to use JUnit 4 for tests here because Compose only provides JUnit 4 support
    @get:Rule
    val compose = createComposeRule()

    @Before
    fun setUp() {
        compose.setContent {
            MainUi()
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
    fun `changing theme to light mode`() {
        compose.onNodeWithTag("SettingsButton").performClick()
        compose.onNodeWithTag("ThemeDropDown").performClick()
        compose.onNodeWithTag("ThemeDropDownMenuItem.LIGHT").performClick()
        compose.onNodeWithTag("MainScaffold").assertBackgroundColor(md_theme_light_surface)
    }

    @Test
    fun `changing theme to dark mode`() {
        compose.onNodeWithTag("SettingsButton").performClick()
        compose.onNodeWithTag("ThemeDropDown").performClick()
        compose.onNodeWithTag("ThemeDropDownMenuItem.DARK").performClick()
        compose.onNodeWithTag("MainScaffold").assertBackgroundColor(md_theme_dark_surface)
    }

    @Test
    fun `the history is empty by default`() {
        compose.onNodeWithTag("HistoryList").onChildren().assertCountEquals(0)
    }

    @Test
    fun `entering a calculation`() {
        compose.onNodeWithTag("MainTextField").performTextInput("1+2")
        compose.onNodeWithTag("MainTextField").performImeAction()
        compose.onNodeWithTag("MainTextFieldErrorIcon", useUnmergedTree = true).assertDoesNotExist()
        val children = compose.onNodeWithTag("HistoryList").onChildren()
        children.assertCountEquals(2)
        children[0].assertTextEquals("1 + 2 =\n")
        children[1].assertTextEquals("3\n")
        compose.onNodeWithTag("MainTextField").assertTextSelectionRange(0, 0)
    }

    @Test
    fun `entering an expression containing an invalid character`() {
        compose.onNodeWithTag("MainTextField").performTextInput("1+2\\")
        compose.onNodeWithTag("MainTextField").performImeAction()
        compose.onNodeWithTag("MainTextFieldErrorIcon", useUnmergedTree = true).assertIsDisplayed()
        compose.onNodeWithTag("MainTextField").assertTextEquals("1+2\\")
        compose.onNodeWithTag("MainTextField").assertTextSelectionRange(3, 4)
    }

    @Test
    fun `entering an incomplete expression`() {
        compose.onNodeWithTag("MainTextField").performTextInput("1+")
        compose.onNodeWithTag("MainTextField").performImeAction()
        compose.onNodeWithTag("MainTextFieldErrorIcon", useUnmergedTree = true).assertIsDisplayed()
        compose.onNodeWithTag("MainTextField").assertTextEquals("1+?")
        compose.onNodeWithTag("MainTextField").assertTextSelectionRange(2, 3)
    }
}