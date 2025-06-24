package at.robthered.plan_me.features.ui.presentation.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import at.robthered.plan_me.R

@Composable
fun AppAbortDialog(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    onAccept: () -> Unit,
    title: String,
    text: AnnotatedString,
    confirmText: String = stringResource(R.string.abort_dialog_button_drop_text),
    headerBackgroundColor: Color = MaterialTheme.colorScheme.error.copy(alpha = 0.2f),
    infoColor: Color = MaterialTheme.colorScheme.error,
    onInfoColor: Color = MaterialTheme.colorScheme.onError,
) {
    AppDialog(
        modifier = modifier,
        onDismissRequest = onDismissRequest
    ) {
        Column(
            modifier = Modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = headerBackgroundColor)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Warning,
                    contentDescription = null,
                    tint = infoColor
                )
                Text(
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    text = title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 32.dp),
                textAlign = TextAlign.Center,
                text = text
            )

            HorizontalDivider()
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    onClick = onDismissRequest
                ) {
                    Text(
                        text = stringResource(R.string.abort_dialog_button_cancel_text),
                        fontWeight = FontWeight.Bold
                    )
                }

                FilledTonalButton(
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.filledTonalButtonColors()
                        .copy(
                            containerColor = infoColor.copy(
                                alpha = 0.7f,
                            ),
                            contentColor = onInfoColor
                        ),
                    onClick = onAccept
                ) {
                    Text(
                        text = confirmText,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}