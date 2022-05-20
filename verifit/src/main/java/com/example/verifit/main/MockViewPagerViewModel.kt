package com.example.verifit.main

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MockViewPagerViewModel(initialViewState: ViewState) : IViewPagerViewModel {
    override val viewState: StateFlow<ViewState> = MutableStateFlow(initialViewState)
    override val oneShotEvents: Flow<Any?>
        get() =  MutableStateFlow(null)

    override fun onAction(uiAction: UiAction) {

    }
}