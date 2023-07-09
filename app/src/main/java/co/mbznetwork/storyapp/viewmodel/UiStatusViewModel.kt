package co.mbznetwork.storyapp.viewmodel

import androidx.lifecycle.ViewModel
import co.mbznetwork.storyapp.eventbus.UIStatusEventBus
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UiStatusViewModel @Inject constructor(
    uiStatusEventBus: UIStatusEventBus
): ViewModel() {
    val uiStatus = uiStatusEventBus.uiStatus
}