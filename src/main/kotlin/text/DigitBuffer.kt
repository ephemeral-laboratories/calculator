package garden.ephemeral.calculator.text

class DigitBuffer(private val radix: Int) {
    var radixPointPlace: Int? = null
    var digits = mutableListOf<Int>()

    val integerDigitCount get() = radixPointPlace ?: digits.size
    val integerDigits get() = radixPointPlace?.let { digits.subList(0, it) } ?: digits
    val fractionDigitCount get() = digits.size - integerDigitCount
    val fractionDigits get() = radixPointPlace?.let { digits.subList(it, digits.size) } ?: emptyList()

    fun prepend(digit: Int) {
        checkDigit(digit)
        digits.add(0, digit)
    }

    fun append(digit: Int) {
        checkDigit(digit)
        digits.add(digit)
    }

    fun appendInt(value: Int) {
        val insertPoint = digits.size
        if (value == 0) {
            digits.add(insertPoint, 0)
            return
        }
        var remainder = value
        while (remainder != 0) {
            val digit = remainder % radix
            remainder /= radix
            digits.add(insertPoint, digit)
        }
    }

    private fun checkDigit(digit: Int) {
        require(digit in 0 until radix) { "Illegal digit: $digit" }
    }

    fun markRadixPoint() {
        radixPointPlace = digits.size
    }

    fun enforceLimits(
        minimumIntegerDigits: Int,
        minimumFractionDigits: Int,
        maximumFractionDigits: Int,
    ) {
        if (minimumIntegerDigits > 0 && integerDigitCount == 0) {
            prepend(0)
            radixPointPlace = 1
        }

        roundOff(maximumFractionDigits)
        trimTrailingZeroes(minimumFractionDigits)
    }

    private fun roundOff(maximumFractionDigits: Int) {
        val roundOffPosition = integerDigitCount + maximumFractionDigits
        if (roundOffPosition >= digits.size) return

        val last = digits[roundOffPosition]
        if (last * 2 > radix) {
            // round up
            roundUp(roundOffPosition - 1)
        } else if (last * 2 < radix) {
            // round down (nothing to do)
        } else {
            // half, round to even
            if (digits[roundOffPosition - 1] % 2 != 0) {
                roundUp(roundOffPosition - 1)
            }
        }

        while (digits.size > roundOffPosition) {
            digits.removeLast()
        }
    }

    private fun roundUp(position: Int) {
        for (index in position downTo 0) {
            val newDigit = digits[index] + 1
            digits[index] = newDigit
            if (newDigit >= radix) {
                digits[index] = 0
            } else {
                break
            }
        }
    }

    private fun trimTrailingZeroes(minimumFractionDigits: Int) {
        while (fractionDigitCount > minimumFractionDigits) {
            if (digits[digits.lastIndex] == 0) {
                digits.removeLast()
            } else {
                break
            }
        }
    }
}
