package garden.ephemeral.calculator.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.platform.Font

val RobotoCondensed = FontFamily(Font("/RobotoCondensed-Regular.ttf"))

internal val DefaultTypography = Typography()

val CustomTypography = DefaultTypography.copy(
    displayLarge = DefaultTypography.displayLarge.copy(fontFamily = RobotoCondensed),
    displayMedium = DefaultTypography.displayMedium.copy(fontFamily = RobotoCondensed),
    displaySmall = DefaultTypography.displaySmall.copy(fontFamily = RobotoCondensed),
    headlineLarge = DefaultTypography.headlineLarge.copy(fontFamily = RobotoCondensed),
    headlineMedium = DefaultTypography.headlineMedium.copy(fontFamily = RobotoCondensed),
    headlineSmall = DefaultTypography.headlineSmall.copy(fontFamily = RobotoCondensed),
    titleLarge = DefaultTypography.titleLarge.copy(fontFamily = RobotoCondensed),
    titleMedium = DefaultTypography.titleMedium.copy(fontFamily = RobotoCondensed),
    titleSmall = DefaultTypography.titleSmall.copy(fontFamily = RobotoCondensed),
    bodyLarge = DefaultTypography.bodyLarge.copy(fontFamily = RobotoCondensed),
    bodyMedium = DefaultTypography.bodyMedium.copy(fontFamily = RobotoCondensed),
    bodySmall = DefaultTypography.bodySmall.copy(fontFamily = RobotoCondensed),
    labelLarge = DefaultTypography.labelLarge.copy(fontFamily = RobotoCondensed),
    labelMedium = DefaultTypography.labelMedium.copy(fontFamily = RobotoCondensed),
    labelSmall = DefaultTypography.labelSmall.copy(fontFamily = RobotoCondensed),
)
