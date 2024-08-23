package garden.ephemeral.calculator.text

import garden.ephemeral.calculator.creals.Real
import garden.ephemeral.calculator.creals.shouldBeCloseTo
import garden.ephemeral.calculator.ui.NumberFormatOption
import garden.ephemeral.calculator.util.row
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.instanceOf

class PositionalFormatTest : FreeSpec({
    fun newPositionalFormat() = PositionalFormat(10, NumberFormatOption.DECIMAL.defaultSymbols)

    "parse valid examples" - {
        withData(
            row("0", "0.00000000000000000000"),
            row("0.0", "0.00000000000000000000"),
            row(".0", "0.00000000000000000000"),
            row("0.5", "0.50000000000000000000"),
            row(".5", "0.50000000000000000000"),
            row("1", "1.00000000000000000000"),
            row("1.0", "1.00000000000000000000"),
            row("1.00", "1.00000000000000000000"),
            row("1.000", "1.00000000000000000000"),
            row("1.2", "1.20000000000000000000"),
            row("1.20", "1.20000000000000000000"),
            row("1.23", "1.23000000000000000000"),
            row("1.234", "1.23400000000000000000"),
            row("-0.5", "-0.50000000000000000000"),
            row("-.5", "-0.50000000000000000000"),
            row("-1", "-1.00000000000000000000"),
            row("-1.0", "-1.00000000000000000000"),
            row("-1.00", "-1.00000000000000000000"),
            row("-1.000", "-1.00000000000000000000"),
            row("-1.2", "-1.20000000000000000000"),
            row("-1.20", "-1.20000000000000000000"),
            row("-1.23", "-1.23000000000000000000"),
            row("-1.234", "-1.23400000000000000000"),
            row("+1.23", "1.23000000000000000000"),
            row("1000000000000000000000000", "1000000000000000000000000.00000000000000000000"),
            row("1000000000000000000000000000000000000000", "1000000000000000000000000000000000000000.00000000000000000000"),
            row("10000000000000000000000000000000000000000", "10000000000000000000000000000000000000000.00000000000000000000"),
        ) { (example, expected) ->
            val format = newPositionalFormat()
            val number = format.parse(example)
            number shouldBeCloseTo expected
        }

        "returning result object" {
            val format = newPositionalFormat()
            val result = format.parseSafely("1.2")
            result shouldBe instanceOf<RealParseResult.Success>()
            result as RealParseResult.Success
            result.index shouldBe 3
            result.parsedValue shouldBeCloseTo "1.20000000000000000000"
        }
    }

    "parse invalid examples" - {
        val parseInvalidExamples = listOf(
            row("barf", 0, 0),
            row("1barf", 0, 1),
            row("1.barf", 0, 2),
        )

        "throwing exception" - {
            withData(parseInvalidExamples) { (text, _, expectedErrorIndex) ->
                val format = newPositionalFormat()
                shouldThrow<ParseException> {
                    format.parse(text)
                }.errorOffset shouldBe expectedErrorIndex
            }
        }

        "returning result object" - {
            withData(parseInvalidExamples) { (text, expectedIndex, expectedErrorIndex) ->
                val format = newPositionalFormat()
                val result = format.parseSafely(text)
                result shouldBe RealParseResult.Failure(index = expectedIndex, errorIndex = expectedErrorIndex)
            }
        }
    }

    "format" - {
        withData(
            row("0.0", "0"),
            row("0.5", "0.5"),
            row("0.99999", "1"),
            row("1.0", "1"),
            row("1.2", "1.2"),
            row("1.23", "1.23"),
            row("1.234", "1.234"),
            row("1.2345", "1.234"),
            row("1.2355", "1.236"),
            row("1.99999", "2"),
            row("-0.5", "-0.5"),
            row("-0.99999", "-1"),
            row("-1.0", "-1"),
            row("-1.2", "-1.2"),
            row("-1.23", "-1.23"),
            row("-1.234", "-1.234"),
            row("-1.2345", "-1.234"),
            row("-1.2355", "-1.236"),
            row("-1.99999", "-2"),
            row("1000000000000000000000000.00000000000000000000", "1000000000000000000000000"),
            row("1000000000000000000000000000000000000000", "1000000000000000000000000000000000000000"),
            row("10000000000000000000000000000000000000000", "1E40"),
        ) { (example, expected) ->
            val format = newPositionalFormat()
            val result = format.format(Real.valueOf(example))
            result shouldBe expected
        }
    }
})
