package garden.ephemeral.calculator.ui

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.text.TextRange
import assertk.assertThat
import assertk.assertions.isEqualTo

/**
 * Asserts that the given node has the expected background colour.
 *
 * @param expectedBackground the expected background colour.
 */
fun SemanticsNodeInteraction.assertBackgroundColor(expectedBackground: Color) {
    val array = IntArray(20)
    captureToImage().readPixels(array, startY = 500, startX = 200, width = 5, height = 4)
    array.forEach { pixel ->
        assertThat(Color(pixel)).isEqualTo(expectedBackground)
    }
}

/**
 * Asserts that the given node has the given text selection range.
 *
 * @param expectedStart the start of the range.
 * @param expectedEndExclusive the exclusive end of the range.
 */
fun SemanticsNodeInteraction.assertTextSelectionRange(expectedStart: Int, expectedEndExclusive: Int) {
    this.assert(hasTextSelectionRange(expectedStart, expectedEndExclusive))
}

/**
 * Creates a matcher to test for the selection range.
 *
 * @param expectedStart the start of the range.
 * @param expectedEndExclusive the exclusive end of the range.
 * @return the semantics matcher.
 */
fun hasTextSelectionRange(expectedStart: Int, expectedEndExclusive: Int) = SemanticsMatcher(
    description = "has text selection range $expectedStart until $expectedEndExclusive",
    matcher = { node ->
        val textRange: TextRange = node.config[SemanticsProperties.TextSelectionRange]
        textRange.start == expectedStart && textRange.end == expectedEndExclusive
    },
)
