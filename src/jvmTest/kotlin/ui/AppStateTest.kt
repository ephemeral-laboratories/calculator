package garden.ephemeral.calculator.ui

import com.russhwolf.settings.MapSettings
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.scopes.FreeSpecRootScope
import io.kotest.matchers.shouldBe
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty1

class AppStateTest : FreeSpec({
    lateinit var settings: MapSettings
    lateinit var appState: AppState

    beforeEach {
        settings = MapSettings()
        appState = AppState(settings)
    }

    fun <E : Enum<E>> FreeSpecRootScope.commonEnumOptionSpecs(
        nameForTest: String,
        enumClass: KClass<E>,
        property: KMutableProperty1<AppState, E>,
        expectedDefault: E,
    ) {
        "$nameForTest default should be $expectedDefault" {
            property.get(appState) shouldBe expectedDefault
            settings.getStringOrNull(property.name) shouldBe null
        }

        enumClass.java.enumConstants.forEach { option ->
            "setting $nameForTest to $option should also store it to settings" {
                property.set(appState, option)
                settings.getStringOrNull(property.name) shouldBe option.name
            }
        }
    }

    fun FreeSpecRootScope.commonBooleanOptionSpecs(
        nameForTest: String,
        property: KMutableProperty1<AppState, Boolean>,
        expectedDefault: Boolean,
    ) {
        "$nameForTest default should be $expectedDefault" {
            property.get(appState) shouldBe expectedDefault
            settings.getBooleanOrNull(property.name) shouldBe null
        }

        sequenceOf(false, true).forEach { option ->
            "setting $nameForTest to $option should also store it to settings" {
                property.set(appState, option)
                settings.getBooleanOrNull(property.name) shouldBe option
            }
        }
    }

    commonEnumOptionSpecs(
        nameForTest = "theme",
        enumClass = ThemeOption::class,
        property = AppState::themeOption,
        expectedDefault = ThemeOption.SYSTEM_DEFAULT,
    )

    commonEnumOptionSpecs(
        nameForTest = "number format",
        enumClass = NumberFormatOption::class,
        property = AppState::numberFormatOption,
        expectedDefault = NumberFormatOption.DECIMAL,
    )

    commonEnumOptionSpecs(
        nameForTest = "decimal radix separator",
        enumClass = RadixSeparatorOption::class,
        property = AppState::decimalRadixSeparatorOption,
        expectedDefault = RadixSeparatorOption.PERIOD,
    )

    commonEnumOptionSpecs(
        nameForTest = "dozenal radix separator",
        enumClass = RadixSeparatorOption::class,
        property = AppState::dozenalRadixSeparatorOption,
        expectedDefault = RadixSeparatorOption.SEMICOLON,
    )

    commonBooleanOptionSpecs(
        nameForTest = "enable crash reporting",
        property = AppState::enableCrashReporting,
        expectedDefault = false,
    )
})
