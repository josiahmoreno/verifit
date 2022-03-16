package com.example.verifit.diary


import com.example.verifit.WorkoutDay
import com.example.verifit.main.BaseViewModel

class DiaryViewModel(val FetchDiaryUseCase : FetchDiaryUseCase, val CalculatedDiaryEntryUseCase: CalculatedDiaryEntryUseCase)
    : BaseViewModel<ViewState, UiAction, OneShotEvents>(
        initialViewState = ViewState(emptyList(), null)
) {
    override fun onAction(uiAction: UiAction) {
        when(uiAction){
            UiAction.OnResume -> _viewState.value = _viewState.value.copy(diaryEntries = FetchDiaryUseCase())
            is UiAction.ClickDiaryEntry -> {
                _viewState.value = _viewState.value.copy(showDiaryStats = CalculatedDiaryEntryUseCase(uiAction.diaryEntry))
            }
            UiAction.ClickExerciseEntry -> TODO()
            UiAction.DiaryEntryDialogDismiss -> _viewState.value = _viewState.value.copy(showDiaryStats = null)
        }
    }
}

data class ViewState(
        val diaryEntries: List<DiaryEntry>,
        val showDiaryStats: DialogData?
)

interface DiaryEntry {
    val dayString: String
    val dateString: String
    val exerciseEntries: List<ExerciseEntry>

}

data class DiaryEntryImpl(
        override val dayString: String = "NullsDay",
        override val dateString: String = "March 12, 2022",
        override val exerciseEntries: List<ExerciseEntry> = emptyList(),

): DiaryEntry {
}

data class DiaryEntryImpl2(
        override val dayString: String = "NullsDay",
        override val dateString: String = "March 12, 2022",
        override val exerciseEntries: List<ExerciseEntry> = emptyList(),
        val workoutDay: WorkoutDay
): DiaryEntry {
}


data class ExerciseEntry(
        val exerciseName: String,
        val amountOfSets: String,
        val color: Int,
        val showFire: Boolean
)

sealed class UiAction{
    object OnResume : UiAction()
    class ClickDiaryEntry(val diaryEntry: DiaryEntry) : UiAction()
    object ClickExerciseEntry : UiAction()
    object DiaryEntryDialogDismiss : UiAction()

}
sealed class OneShotEvents{

}