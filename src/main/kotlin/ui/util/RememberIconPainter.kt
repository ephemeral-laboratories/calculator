import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toComposeImageBitmap
import java.awt.image.BufferedImage
import javax.swing.Icon

/**
 * A composable function which remembers a [Painter] which can paint itself by delegating to a Swing [Icon].
 *
 * @param iconFactory a factory called to create the icon.
 * @return the painter.
 */
@Composable
fun rememberIconPainter(iconFactory: () -> Icon): Painter = remember {
    val icon = iconFactory()
    val image = BufferedImage(icon.iconWidth, icon.iconHeight, BufferedImage.TYPE_INT_ARGB)
    val g = image.createGraphics()
    try {
        icon.paintIcon(null, g, 0, 0)
    } finally {
        g.dispose()
    }
    BitmapPainter(image.toComposeImageBitmap())
}
