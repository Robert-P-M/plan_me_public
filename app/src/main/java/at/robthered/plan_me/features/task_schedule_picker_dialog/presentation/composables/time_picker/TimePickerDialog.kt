package at.robthered.plan_me.features.task_schedule_picker_dialog.presentation.composables.time_picker

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import at.robthered.plan_me.R
import at.robthered.plan_me.features.task_schedule_picker_dialog.domain.model.Time
import at.robthered.plan_me.features.task_schedule_picker_dialog.presentation.composables.picker.VerticalPicker
import at.robthered.plan_me.features.ui.presentation.composables.AppDialog

@Composable
fun TimePickerDialog(
    modifier: Modifier = Modifier,
    title: String,
    isVisible: Boolean,
    initialTime: Time,
    onDismissRequest: () -> Unit,
    onPickTime: (time: Time?) -> Unit,
) {
    AnimatedVisibility(
        visible = isVisible,
    ) {
        AppDialog(
            modifier = modifier,
            onDismissRequest = onDismissRequest
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = title)
                }

                val hours = (0..23).toList()
                val minutes = (0..59).toList()
                var selectedHour by remember(initialTime) {
                    mutableIntStateOf(initialTime.hours)
                }
                var selectedMinute by remember(initialTime) {
                    mutableIntStateOf(initialTime.minutes)
                }
                Row(
                    modifier = Modifier
                        .padding(horizontal = 32.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    VerticalPicker(
                        modifier = Modifier.weight(1f),
                        items = hours,
                        startIndex = selectedHour,
                        visibleItemsCount = 9,
                        label = { modifier ->
                            Text(
                                text = stringResource(R.string.time_picker_dialog_hours),
                                modifier = modifier
                            )
                        },
                        itemContent = { _, item ->
                            Text(
                                text = item.toString().padStart(2, '0'),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        },
                        onChangeValue = { value: Int ->
                            selectedHour = value
                        }
                    )
                    Text(" : ")
                    VerticalPicker(
                        modifier = Modifier.weight(1f),
                        items = minutes,
                        startIndex = selectedMinute,
                        visibleItemsCount = 9,
                        label = { modifier ->
                            Text(
                                text = stringResource(R.string.time_picker_dialog_minutes),
                                modifier = modifier
                            )
                        },
                        itemContent = { _, item ->
                            Text(
                                text = item.toString().padStart(2, '0'),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        },
                        onChangeValue = { value: Int ->
                            selectedMinute = value
                        }
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(
                        8.dp,
                        alignment = Alignment.CenterHorizontally
                    )
                ) {
                    OutlinedButton(
                        modifier = Modifier
                            .weight(1f)
                            .height(IntrinsicSize.Min),
                        onClick = {
                            onDismissRequest()
                        }
                    ) {
                        Text(
                            text = stringResource(R.string.time_picker_dialog_cancel_text)
                        )
                    }
                    FilledTonalIconButton(
                        modifier = Modifier
                            .wrapContentSize(align = Alignment.Center),
                        onClick = {
                            onPickTime(
                                null
                            )
                            onDismissRequest()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = stringResource(R.string.time_picker_dialog_delete_time_icon_description),
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                    FilledTonalButton(
                        modifier = Modifier
                            .weight(1f)
                            .height(IntrinsicSize.Min),
                        colors = ButtonDefaults.filledTonalButtonColors().copy(
                            contentColor = MaterialTheme.colorScheme.onSurface,
                        ),
                        onClick = {
                            onPickTime(
                                Time(hours = selectedHour, minutes = selectedMinute)
                            )
                            onDismissRequest()
                        }
                    ) {
                        Text(
                            text = stringResource(R.string.time_picker_dialog_save_text)
                        )
                    }
                }

            }
        }
    }
}