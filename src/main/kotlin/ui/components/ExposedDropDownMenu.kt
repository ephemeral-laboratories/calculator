package garden.ephemeral.calculator.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.ZeroCornerSize
import androidx.compose.material.ContentAlpha
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import garden.ephemeral.calculator.ui.common.Localizable
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// ExposedDropDownMenu was supposed to be added in Jetpack Compose 1.1.0
// but doesn't seem to be there yet. This is a reimplementation while waiting.
// See https://stackoverflow.com/questions/67111020/exposed-drop-down-menu-for-jetpack-compose/6904285

@Composable
fun <T : Localizable> ExposedDropDownMenu(
    values: Iterable<T>,
    selectedValue: T,
    onChange: (T) -> Unit,
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colors.onSurface.copy(alpha = TextFieldDefaults.BackgroundOpacity),
    shape: Shape = MaterialTheme.shapes.small.copy(bottomEnd = ZeroCornerSize, bottomStart = ZeroCornerSize),
) {
    SimpleExposedDropDownMenuImpl(
        values = values,
        selectedValue = selectedValue,
        onChange = onChange,
        label = label,
        modifier = modifier,
        backgroundColor = backgroundColor,
        shape = shape,
        decorator = { color, width, content ->
            Box(
                Modifier
                    .drawBehind {
                        val strokeWidth = width.value * density
                        val y = size.height - strokeWidth / 2
                        drawLine(
                            color,
                            Offset(0f, y),
                            Offset(size.width, y),
                            strokeWidth,
                        )
                    },
            ) {
                content()
            }
        },
    )
}

@Composable
fun <T : Localizable> OutlinedExposedDropDownMenu(
    values: Iterable<T>,
    selectedValue: T,
    onChange: (T) -> Unit,
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colors.onSurface.copy(alpha = TextFieldDefaults.BackgroundOpacity),
    shape: Shape = MaterialTheme.shapes.small,
) {
    SimpleExposedDropDownMenuImpl(
        values = values,
        selectedValue = selectedValue,
        onChange = onChange,
        label = label,
        modifier = modifier,
        backgroundColor = backgroundColor,
        shape = shape,
        decorator = { color, width, content ->
            Box(Modifier.border(width, color, shape)) {
                content()
            }
        },
    )
}

@Composable
private fun <T : Localizable> SimpleExposedDropDownMenuImpl(
    values: Iterable<T>,
    selectedValue: T,
    onChange: (T) -> Unit,
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color,
    shape: Shape,
    decorator: @Composable (Color, Dp, @Composable () -> Unit) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    var textFieldSize by remember { mutableStateOf(Size.Zero) }

    val indicatorColor =
        if (expanded) {
            MaterialTheme.colors.primary.copy(alpha = ContentAlpha.high)
        } else {
            MaterialTheme.colors.onSurface.copy(alpha = TextFieldDefaults.UnfocusedIndicatorLineOpacity)
        }
    val indicatorWidth = (if (expanded) 2 else 1).dp
    val labelColor =
        if (expanded) {
            MaterialTheme.colors.primary.copy(alpha = ContentAlpha.high)
        } else {
            MaterialTheme.colors.onSurface.copy(ContentAlpha.medium)
        }
    val trailingIconColor = MaterialTheme.colors.onSurface.copy(alpha = TextFieldDefaults.IconOpacity)

    val rotation: Float by animateFloatAsState(if (expanded) 180f else 0f)

    val focusManager = LocalFocusManager.current

    Column(modifier = modifier.width(IntrinsicSize.Min)) {
        decorator(indicatorColor, indicatorWidth) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .background(color = backgroundColor, shape = shape)
                    .onGloballyPositioned { textFieldSize = it.size.toSize() }
                    .clip(shape)
                    .clickable {
                        expanded = !expanded
                        focusManager.clearFocus()
                    }
                    .padding(start = 16.dp, end = 12.dp, top = 7.dp, bottom = 10.dp),
            ) {
                Column(Modifier.padding(end = 32.dp)) {
                    ProvideTextStyle(value = MaterialTheme.typography.caption.copy(color = labelColor)) {
                        label()
                    }
                    Text(
                        text = selectedValue.localizedName,
                        modifier = Modifier.padding(top = 1.dp),
                    )
                }
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = "Change",
                    tint = trailingIconColor,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(top = 4.dp)
                        .rotate(rotation),
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .width(with(LocalDensity.current) { textFieldSize.width.toDp() }),
        ) {
            values.forEach { value ->
                val scope = rememberCoroutineScope()
                DropdownMenuItem(
                    onClick = {
                        onChange(value)
                        scope.launch {
                            delay(150)
                            expanded = false
                        }
                    },
                ) {
                    Text(value.localizedName)
                }
            }
        }
    }
}
