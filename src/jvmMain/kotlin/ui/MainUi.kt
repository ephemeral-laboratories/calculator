package garden.ephemeral.calculator.ui

import androidx.compose.material3.DrawerValue
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.sp

@Composable
fun MainUi(appState: AppState) {
    val valueTextStyle = LocalTextStyle.current.copy(fontSize = 32.sp)
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerContent = @Composable { SettingsScreen(appState) },
        drawerState = drawerState,
    ) {
        Scaffold(
            bottomBar = @Composable { SearchBar(appState, valueTextStyle, scope) },
            modifier = Modifier.testTag("MainScaffold"),
        ) { padding ->
            MainContent(appState, drawerState, valueTextStyle, scope, padding)
        }
    }
}
