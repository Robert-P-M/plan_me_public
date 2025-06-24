package at.robthered.plan_me.features.ui.presentation.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import at.robthered.plan_me.features.ui.presentation.theme.PlanMeTheme

@Composable
fun AppModalTitle(
    modifier: Modifier = Modifier,
    title: String,
    titleStyle: TextStyle = MaterialTheme.typography.headlineMedium,
    leadingIcon: @Composable BoxScope.() -> Unit = {},
    trailingIcon: @Composable BoxScope.() -> Unit = {},
) {
    Box(
        modifier = modifier.fillMaxWidth()
    ) {
        leadingIcon(this)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = titleStyle
            )
        }
        trailingIcon(this)
    }
}


@Composable
fun AppModalTitle(
    modifier: Modifier = Modifier,
    title: AnnotatedString,
    titleStyle: TextStyle = MaterialTheme.typography.headlineMedium,
    leadingIcon: @Composable BoxScope.() -> Unit = {},
    trailingIcon: @Composable BoxScope.() -> Unit = {},
) {
    Box(
        modifier = modifier.fillMaxWidth()
    ) {
        leadingIcon(this)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = titleStyle
            )
        }
        trailingIcon(this)
    }
}

@PreviewLightDark
@Preview(showSystemUi = true)
@Composable
fun AppModalTitlePreview() {
    PlanMeTheme {
        AppModalTitle(
            title = "Title",
            trailingIcon = {
                IconButton(
                    modifier = Modifier.align(Alignment.CenterEnd),
                    onClick = { }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = null
                    )
                }
            }
        )
    }
}