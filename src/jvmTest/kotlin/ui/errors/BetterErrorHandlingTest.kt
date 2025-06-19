package garden.ephemeral.calculator.ui.errors

import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.IdlingResource
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.window.Window
import kotlinx.coroutines.launch
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.awt.EventQueue

class BetterErrorHandlingTest {
    // XXX: Forced to use JUnit 4 for tests here because Compose only provides JUnit 4 support
    @get:Rule
    val compose = createComposeRule()

    @Before
    fun setUp() {
        // Make the test run more reliably when windows appear or disappear by waiting
        // until the event queue is actually empty.
        compose.registerIdlingResource(object : IdlingResource {
            override val isIdleNow: Boolean
                get() {
                    EventQueue.invokeAndWait {
                        // There's no point asking the event queue at this point whether there are
                        // any pending events, because our invokeAndWait task would have been
                        // enqueued after all the events we were waiting for.
                    }
                    return true
                }
        })
    }

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

    @Test
    fun `pressing Dismiss on the error dialog closes it`() {
        compose.setContent {
            OuterContainer {
                throw SyntheticException()
            }
        }

        compose.waitForIdle()
        compose.onNodeWithTag("BetterErrorPane").assertExists()
        compose.onNodeWithTag("Dismiss").performClick()
        compose.waitForIdle()
        compose.onNodeWithTag("BetterErrorPane").assertDoesNotExist()
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
