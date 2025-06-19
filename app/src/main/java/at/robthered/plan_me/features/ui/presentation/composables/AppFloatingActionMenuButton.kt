package at.robthered.plan_me.features.ui.presentation.composables

import androidx.activity.compose.BackHandler
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButtonMenu
import androidx.compose.material3.FloatingActionButtonMenuScope
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ToggleFloatingActionButton
import androidx.compose.material3.ToggleFloatingActionButtonDefaults.animateIcon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AppFloatingActionMenuButton(
    modifier: Modifier = Modifier,
    buttons: @Composable FloatingActionButtonMenuScope.(
        onHideFabMenu: () -> Unit,
    ) -> Unit,

    ) {
    var isExpanded by rememberSaveable {
        mutableStateOf(false)
    }
    BackHandler(enabled = isExpanded) { isExpanded = false }
    FloatingActionButtonMenu(
        modifier = modifier,
        expanded = isExpanded,
        button = {
            ToggleFloatingActionButton(
                modifier = Modifier.Companion
                    .semantics(
                        properties = {
                            traversalIndex = -1f
                            stateDescription =
                                if (isExpanded) "Menu expanded" else "Menu collapsed"
                            contentDescription = "Toggle Menu"
                        }
                    ),
                checked = isExpanded,
                onCheckedChange = { isExpanded = isExpanded.not() }
            ) {
                val imageVector by remember {
                    derivedStateOf {
                        if (checkedProgress > 0.5f) Icons.Filled.Close else Icons.Filled.Add
                    }
                }
                Icon(
                    painter = rememberVectorPainter(imageVector),
                    contentDescription = null,
                    modifier = Modifier.animateIcon({ checkedProgress })
                )
            }
        }
    ) {
        val onHide: () -> Unit = {
            isExpanded = false
        }
        buttons(onHide)
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
@Preview
private fun AppFloatingActionMenuButtonPreview() {
    MaterialTheme {
        AppFloatingActionMenuButton(
            buttons = {}
        )
    }
}