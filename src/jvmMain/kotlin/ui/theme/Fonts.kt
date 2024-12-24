package garden.ephemeral.calculator.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import garden.ephemeral.calculator.calculator.generated.resources.Res
import garden.ephemeral.calculator.calculator.generated.resources.RobotoCondensed_Regular
import org.jetbrains.compose.resources.Font

val CustomTypography: Typography
    @Composable
    get() {
        val defaultTypography = Typography()
        val robotoCondensed = FontFamily(Font(Res.font.RobotoCondensed_Regular))
        return defaultTypography.copy(
            displayLarge = defaultTypography.displayLarge.copy(fontFamily = robotoCondensed),
            displayMedium = defaultTypography.displayMedium.copy(fontFamily = robotoCondensed),
            displaySmall = defaultTypography.displaySmall.copy(fontFamily = robotoCondensed),
            headlineLarge = defaultTypography.headlineLarge.copy(fontFamily = robotoCondensed),
            headlineMedium = defaultTypography.headlineMedium.copy(fontFamily = robotoCondensed),
            headlineSmall = defaultTypography.headlineSmall.copy(fontFamily = robotoCondensed),
            titleLarge = defaultTypography.titleLarge.copy(fontFamily = robotoCondensed),
            titleMedium = defaultTypography.titleMedium.copy(fontFamily = robotoCondensed),
            titleSmall = defaultTypography.titleSmall.copy(fontFamily = robotoCondensed),
            bodyLarge = defaultTypography.bodyLarge.copy(fontFamily = robotoCondensed),
            bodyMedium = defaultTypography.bodyMedium.copy(fontFamily = robotoCondensed),
            bodySmall = defaultTypography.bodySmall.copy(fontFamily = robotoCondensed),
            labelLarge = defaultTypography.labelLarge.copy(fontFamily = robotoCondensed),
            labelMedium = defaultTypography.labelMedium.copy(fontFamily = robotoCondensed),
            labelSmall = defaultTypography.labelSmall.copy(fontFamily = robotoCondensed),
        )
    }
