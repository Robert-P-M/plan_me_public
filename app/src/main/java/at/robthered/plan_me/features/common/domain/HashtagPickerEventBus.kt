package at.robthered.plan_me.features.common.domain

import at.robthered.plan_me.features.common.domain.event_bus.EventBus
import at.robthered.plan_me.features.hashtag_picker_dialog.presentation.hashtag_picker_event.HashtagPickerEvent

interface HashtagPickerEventBus : EventBus<HashtagPickerEvent>