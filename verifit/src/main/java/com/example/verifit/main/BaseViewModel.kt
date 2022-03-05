package com.example.verifit.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.verifit.addexercise.composables.AddExerciseViewModel
import com.example.verifit.addexercise.composables.Model
import com.example.verifit.addexercise.composables.AddExerciseViewState
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow

abstract class BaseViewModel<ViewState, UiAction, OneShotEvent>(val initialViewState : ViewState): ViewModel() {
    open val coroutineScope = MainScope()
    val _viewState: MutableStateFlow<ViewState> = MutableStateFlow(initialViewState)
    val viewState = _viewState.asStateFlow()

    // See https://proandroiddev.com/android-singleliveevent-redux-with-kotlin-flow-b755c70bb055
    // For why channel > SharedFlow/StateFlow in this case
    val _oneShotEvents = Channel<OneShotEvent>(Channel.BUFFERED)
    val oneShotEvents = _oneShotEvents.receiveAsFlow()


    //abstract fun fetchInitialViewState(): ViewState
    abstract fun onAction(uiAction: UiAction)
}
