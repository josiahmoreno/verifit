package com.example.verifit.main

interface IViewPagerViewModel: IBaseViewModel<ViewState,UiAction> {
    override fun onAction(uiAction: UiAction)
}