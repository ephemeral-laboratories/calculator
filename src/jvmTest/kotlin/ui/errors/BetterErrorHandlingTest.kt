package garden.ephemeral.calculator.ui.errors

import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.window.Window
import kotlinx.coroutines.launch
import org.junit.Rule
import org.junit.Test

class BetterErrorHandlingTest {
    // XXX: Forced to use JUnit 4 for tests here because Compose only provides JUnit 4 support
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun `error dialog is not shown if nothing bad is happening`() {
        compose.setContent {
            OuterContainer {
                TextButton(modifier = Modifier.testTag("TestButton"), onClick = {}) {
                    Text(text = "Button")
                }
            }
        }

        compose.onNodeWithTag("BetterErrorPane").assertDoesNotExist()
    }

    @Test
    fun `error thrown from coroutine causes error dialog to appear`() {
        compose.setContent {
            OuterContainer {
                val scope = rememberCoroutineScope()
                TextButton(modifier = Modifier.testTag("TestButton"), onClick = {
                    scope.launch {
                        throw SyntheticException()
                    }
                }) {
                    Text(text = "Button")
                }
            }
        }

        compose.onNodeWithTag("TestButton").performClick()
        compose.waitForIdle()
        compose.onNodeWithTag("BetterErrorPane").assertExists()
    }

    @Test
    fun `error thrown from click action causes error dialog to appear`() {
        compose.setContent {
            OuterContainer {
                TextButton(modifier = Modifier.testTag("TestButton"), onClick = {
                    throw SyntheticException()
                }) {
                    Text(text = "Button")
                }
            }
        }

        compose.onNodeWithTag("TestButton").performClick()
        compose.waitForIdle()
        compose.onNodeWithTag("BetterErrorPane").assertExists()
    }

    @Test
    fun `error thrown from composition causes error dialog to appear`() {
        compose.setContent {
            OuterContainer {
                TextButton(modifier = Modifier.testTag("TestButton"), onClick = {}) {
                    Text(text = "Button")
                }
                throw SyntheticException()
            }
        }

        compose.waitForIdle()
        compose.onNodeWithTag("BetterErrorPane").assertExists()
    }

    @Composable
    fun OuterContainer(content: @Composable () -> Unit) {
        BetterErrorHandling {
            Window(onCloseRequest = {}) {
                content()
            }
        }
    }

    class SyntheticException : RuntimeException("Synthetic exception for test")
}
