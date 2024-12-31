package garden.ephemeral.calculator.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PrivacyTip
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RichTooltip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.rememberComponentRectPositionProvider
import com.russhwolf.settings.PropertiesSettings
import garden.ephemeral.calculator.BuildKonfig
import garden.ephemeral.calculator.calculator.generated.resources.Res
import garden.ephemeral.calculator.calculator.generated.resources.enable_crash_reporting
import garden.ephemeral.calculator.calculator.generated.resources.enable_crash_reporting_privacy
import garden.ephemeral.calculator.calculator.generated.resources.number_format
import garden.ephemeral.calculator.calculator.generated.resources.privacy_tip
import garden.ephemeral.calculator.calculator.generated.resources.radix_separator
import garden.ephemeral.calculator.calculator.generated.resources.theme
import garden.ephemeral.calculator.ui.common.Localizable
import garden.ephemeral.calculator.ui.theme.AppTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringArrayResource
import org.jetbrains.compose.resources.stringResource
import java.util.Properties
import kotlin.reflect.KMutableProperty0

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun <T : Localizable> OptionDropDown(
    testTag: String,
    label: String,
    values: Iterable<T>,
    property: KMutableProperty0<T>,
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { b -> expanded = b },
        modifier = Modifier.testTag(testTag),
    ) {
        Column {
            TextField(
                value = property.get().localizedName,
                label = {
                    Text(text = label)
                },
                onValueChange = {},
                modifier = Modifier.menuAnchor().testTag("${testTag}TextField"),
                readOnly = true,
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.testTag("${testTag}Menu"),
            ) {
                values.forEach { value ->
                    val scope = rememberCoroutineScope()
                    DropdownMenuItem(
                        text = {
                            Text(value.localizedName)
                        },
                        onClick = {
                            property.set(value)
                            scope.launch {
                                delay(150)
                                expanded = false
                            }
                        },
                        modifier = Modifier.testTag("${testTag}MenuItem.$value"),
                    )
                }
            }
        }
    }
}

@Composable
fun OptionCheckbox(
    testTag: String,
    label: String,
    property: KMutableProperty0<Boolean>,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable { property.set(!property.get()) },
    ) {
        Checkbox(
            checked = property.get(),
            onCheckedChange = { property.set(it) },
            modifier = Modifier.testTag(testTag),
        )
        Text(
            text = label,
            modifier = Modifier.testTag("${testTag}Label").padding(end = 8.dp),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PrivacyTipButton(textLines: List<String>) {
    val tooltipState = rememberTooltipState(isPersistent = true)
    val coroutineScope = rememberCoroutineScope()
    TooltipBox(
        positionProvider = rememberComponentRectPositionProvider(alignment = Alignment.BottomCenter),
        tooltip = {
            RichTooltip(title = { Text(text = stringResource(Res.string.privacy_tip)) }) {
                Text(text = textLines.joinToString("\n"))
            }
        },
        state = tooltipState,
        enableUserInput = false,
    ) {
        IconButton(
            onClick = {
                coroutineScope.launch {
                    tooltipState.show(MutatePriority.UserInput)
                }
            }
        ) {
            Icon(
                imageVector = Icons.Outlined.PrivacyTip,
                contentDescription = stringResource(Res.string.privacy_tip),
            )
        }
    }
}

@Composable
fun SettingsScreen(appState: AppState) {
    Surface(color = MaterialTheme.colorScheme.background) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .testTag("Settings")
                .padding(16.dp),
        ) {
            OptionDropDown(
                testTag = "ThemeDropDown",
                label = stringResource(Res.string.theme),
                values = ThemeOption.entries,
                property = appState::themeOption,
            )
            OptionDropDown(
                testTag = "NumberFormatDropDown",
                label = stringResource(Res.string.number_format),
                values = NumberFormatOption.entries,
                property = appState::numberFormatOption,
            )
            OptionDropDown(
                testTag = "RadixSeparatorDropDown",
                label = stringResource(Res.string.radix_separator),
                values = RadixSeparatorOption.entries,
                property = when (appState.numberFormatOption) {
                    NumberFormatOption.DECIMAL -> appState::decimalRadixSeparatorOption
                    NumberFormatOption.DOZENAL -> appState::dozenalRadixSeparatorOption
                },
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                OptionCheckbox(
                    testTag = "EnableCrashReportingCheckbox",
                    label = stringResource(Res.string.enable_crash_reporting),
                    property = appState::enableCrashReporting,
                )
                PrivacyTipButton(textLines = stringArrayResource(Res.array.enable_crash_reporting_privacy))
            }

            Box(
                modifier = Modifier.fillMaxHeight().padding(16.dp),
                contentAlignment = Alignment.BottomStart,
            ) {
                val version = BuildKonfig.Version
                Text("Version $version")
            }
        }
    }
}

@Composable
@Preview
internal fun SettingsScreenPreview() {
    AppTheme(ThemeOption.DARK) {
        SettingsScreen(rememberAppState(settings = PropertiesSettings(Properties())))
    }
}
