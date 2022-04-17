package com.example.verifit.diary


import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.example.verifit.WorkoutDay
import com.example.verifit.WorkoutExercise
import com.example.verifit.main.BaseViewModel
import com.example.verifit.singleton.DateSelectStore
import kotlinx.coroutines.launch

class DiaryViewModel(val FetchDiaryUseCase : FetchDiaryUseCase, val CalculatedDiaryEntryUseCase: CalculatedDiaryEntryUseCase, val CalculatedExerciseEntryUseCase:  CalculatedExerciseEntryUseCase)
    : BaseViewModel<ViewState, UiAction, OneShotEvents>(
        initialViewState = ViewState(emptyList(), null,null, null, mutableStateOf(null))
) {
    override fun onAction(uiAction: UiAction) {
        when(uiAction){
            UiAction.OnResume -> {
                _viewState.value = if (viewState.value.showExerciseEntryStats == null) {
                    _viewState.value.copy(diaryEntries = FetchDiaryUseCase())

                } else {
                    val oldEntry = viewState.value.showExerciseEntryStats!!.value!!.exeriseEntry
                    val oldDate = fetchDate(oldEntry)
                    val newEntries = FetchDiaryUseCase()
                    val newEntry = newEntries.flatMap {
                        it.exerciseEntries
                    }.find {
                        fetchDate(it) == oldDate && it.exerciseName == oldEntry.exerciseName
                    }
                    if (newEntry == null) {
                        viewState.value.copy(diaryEntries = FetchDiaryUseCase(), showExerciseEntryStats = mutableStateOf(null))
                    } else {
                        viewState.value.copy(diaryEntries = FetchDiaryUseCase(),
                                showExerciseEntryStats = mutableStateOf(
                                        ExerciseEntryStats(exeriseEntry = newEntry!!,
                                                dialogData = CalculatedExerciseEntryUseCase(newEntry))))
                    }
                }
                Log.d("DiaryViewModel", "viewState.OnResume")
            }

            is UiAction.ClickDiaryEntry -> {
                _viewState.value = _viewState.value.copy(showDiaryStats = CalculatedDiaryEntryUseCase(uiAction.diaryEntry))
            }
            is UiAction.ClickExerciseEntry -> _viewState.value = _viewState.value.copy(showExerciseEntryStats =  mutableStateOf(
                    ExerciseEntryStats(uiAction.entry,CalculatedExerciseEntryUseCase(uiAction.entry))
            )
            )
            UiAction.DiaryEntryDialogDismiss -> _viewState.value = _viewState.value.copy(showDiaryStats = null)
            is UiAction.ClickPersonalRecord -> _viewState.value = viewState.value.copy(showPersonalRecords = mutableStateOf(uiAction.entry.records))
            is UiAction.EditExerciseEntry -> viewModelScope.launch {
                DateSelectStore.date_selected = fetchDate(uiAction.entry)
                _oneShotEvents.send(OneShotEvents.GoToAddExercise(uiAction.entry.exerciseName))
            }
            is UiAction.ClickComment -> {
            _viewState.value.showComment.value = fetchComment(uiAction.entry)
            //_viewState.value = _viewState.value.copy(showComment = mutableStateOf(fetchComment(uiAction.entry)))
            }
        }
    }

    private fun fetchDate( exerciseEntry: ExerciseEntry): String{
        return if(exerciseEntry is ExerciseEntryImpl){
            exerciseEntry.workoutExercise.date
        } else {
            "2022-03-11"
        }
    }

    private fun fetchComment( exerciseEntry: ExerciseEntry): String{
        return if(exerciseEntry is ExerciseEntryImpl){
            exerciseEntry.workoutExercise.comment
        } else {
            "Test Comment"
        }
    }
}

data class ViewState(
        val diaryEntries: List<DiaryEntry>,
        val showDiaryStats: DialogData?,
        val showPersonalRecords: MutableState<List<String>?>?,
        val showExerciseEntryStats: MutableState<ExerciseEntryStats?>?,
        val showComment: MutableState<String?>
)

data class ExerciseEntryStats(val exeriseEntry: ExerciseEntry, override val dialogData: DialogData) : DialogDataProvider

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


interface ExerciseEntry {
    val exerciseName: String
    val amountOfSets: String
    val color: Int
    val showFire: Boolean
    val showPrOnly: Boolean
    val showComment: Boolean
    val records: List<String>
}

data class MockExerciseEntry(
        override val exerciseName: String,
        override val amountOfSets: String,
        override val color: Int,
        override val showFire: Boolean,
        override val showPrOnly: Boolean,
        override val showComment: Boolean,
        override val records : List<String>,
): ExerciseEntry

data class ExerciseEntryImpl(
        override val exerciseName: String,
        override val amountOfSets: String,
        override val color: Int,
        override val showFire: Boolean,
        override val showPrOnly: Boolean,
        override val showComment: Boolean,
        override val records : List<String>,
        val workoutExercise: WorkoutExercise
): ExerciseEntry

sealed class UiAction{
    object OnResume : UiAction()
    class ClickDiaryEntry(val diaryEntry: DiaryEntry) : UiAction()
    class ClickPersonalRecord(val entry: ExerciseEntry) : UiAction()
    class EditExerciseEntry(val entry: ExerciseEntry) : UiAction()
    class ClickExerciseEntry(val entry: ExerciseEntry) : UiAction()
    class ClickComment(val entry: ExerciseEntry) : UiAction()

    object DiaryEntryDialogDismiss : UiAction()

}
sealed class OneShotEvents{
    class GoToAddExercise(val exerciseName: String): OneShotEvents()
}