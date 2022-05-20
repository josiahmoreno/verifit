package com.example.verifit.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.verifit.addexercise.composables.AddExerciseViewModel
import com.example.verifit.addexercise.composables.Model
import com.example.verifit.addexercise.composables.AddExerciseViewState
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*

interface IBaseViewModel<ViewState,UiAction> {
    val viewState: StateFlow<ViewState>
    val oneShotEvents: Flow<Any?>

    //abstract fun fetchInitialViewState(): ViewState
    fun onAction(uiAction: UiAction)
}

abstract class BaseViewModel<ViewState, UiAction, OneShotEvent>(val initialViewState : ViewState): ViewModel(),
    IBaseViewModel<ViewState,UiAction> {
    open val coroutineScope = MainScope()
    protected val _viewState: MutableStateFlow<ViewState> = MutableStateFlow(initialViewState)
    override val viewState = _viewState.asStateFlow()

    // See https://proandroiddev.com/android-singleliveevent-redux-with-kotlin-flow-b755c70bb055
    // For why channel > SharedFlow/StateFlow in this case
    val _oneShotEvents = Channel<OneShotEvent>(Channel.BUFFERED)
    override val oneShotEvents = _oneShotEvents.receiveAsFlow()


}
