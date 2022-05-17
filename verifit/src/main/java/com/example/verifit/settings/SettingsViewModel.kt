package com.example.verifit.settings

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.verifit.WorkoutExercise
import com.example.verifit.main.BaseViewModel
import com.example.verifit.main.WorkoutExercisesViewData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    //val ExportDataUseCase: ExportDataUseCase,
    val importDataUseCase: ImportDataUseCase,
    val importCSVDataUseCase: ImportCSVDataUseCase,
    //val deleteAllDataUseCase: DeleteAllDataUseCase
)
    : BaseViewModel<ViewState, UiAction, OneShotEvents>(
    initialViewState = ViewState(
        date = "Monday, April 9 3333", mutableStateOf(false), mutableStateOf(false))
) {
    override fun onAction(uiAction: UiAction) {
        when(uiAction){
            UiAction.DeleteDataCLick -> viewState.value.showDeleteDialog.value = true
            UiAction.OnExportMenuCLick -> viewModelScope.launch{
               // val result = ExportDataUseCase()
            }
            UiAction.OnImportMenuCLick -> viewState.value.showImportDialog.value = true

            //UiAction.YesDeleteAllData -> deleteAllDataUseCase()
            UiAction.YesImportData -> viewModelScope.launch{
                //importDataUseCase()
            }
            is UiAction.OnImportCSVMenuCLick ->viewModelScope.launch{
                importCSVDataUseCase(uiAction.launcher)
            }
        }
    }
}

data class ViewState(
    val date: String,
    val showImportDialog: MutableState<Boolean>,
    val showDeleteDialog: MutableState<Boolean>
)

sealed class UiAction{
    object OnImportMenuCLick : UiAction()
    object OnExportMenuCLick : UiAction()
    object DeleteDataCLick : UiAction()
    object YesDeleteAllData : UiAction()
    object YesImportData : UiAction()
    class OnImportCSVMenuCLick(val launcher: ImportDataUseCase.ResultLauncherWrapper) : UiAction() {

    }


}
sealed class OneShotEvents{
    class GoToAddExercise(val exerciseName: String): OneShotEvents()
}