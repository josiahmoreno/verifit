package com.example.verifit.diary


import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.verifit.WorkoutDay
import com.example.verifit.WorkoutExercise
import com.example.verifit.common.*
import com.example.verifit.main.BaseViewModel
import com.example.verifit.singleton.DateSelectStore
import dagger.hilt.EntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiaryViewModel @Inject constructor(
    val FetchDiaryUseCase: FetchDiaryUseCase,
    val GoToAddExerciseUseCase: NavigateToAddExerciseUseCase,
    val NavigateToDayActivityUseCase: NavigateToDayActivityUseCase,
    val NavigateToCommentUseCase: NavigateToCommentUseCase,
    val NavigateToDiaryDayUseCase: NavigateToDiaryDayUseCase,

    val savedStateHandle: SavedStateHandle? = null,
    val NavigateToExerciseEntryStatsUseCase: NavigateToExerciseEntryStatsUseCase = NoOpNavigateToExerciseEntryStatsUseCase(),
)
    : BaseViewModel<ViewState, UiAction, OneShotEvents>(
        initialViewState = ViewState(FetchDiaryUseCase(), null,null, null, mutableStateOf(null))
) {


    override fun onAction(uiAction: UiAction) {
        when(uiAction){
            is UiAction.ClickDiaryEntry -> {
                //val stats : DialogData = CalculatedDiaryEntryUseCase(uiAction.diaryEntry)
                NavigateToDiaryDayUseCase(fetchDate(diaryEntry = uiAction.diaryEntry))
                //_viewState.value = _viewState.value.copy(showDiaryStats = stats)
            }
            is UiAction.ClickExerciseEntry -> NavigateToExerciseEntryStatsUseCase(uiAction.entry.exerciseName,fetchDate(exerciseEntry = uiAction.entry))
//
//                _viewState.value = _viewState.value.copy(showExerciseEntryStats =  mutableStateOf(
//                    ExerciseEntryStats(uiAction.entry,CalculatedExerciseEntryUseCase(uiAction.entry))
//            )
            //)
            UiAction.DiaryEntryDialogDismiss -> _viewState.value = _viewState.value.copy(showDiaryStats = null)
            is UiAction.ClickPersonalRecord -> _viewState.value = viewState.value.copy(showPersonalRecords = mutableStateOf(uiAction.entry.records))
            is UiAction.EditExerciseEntry -> viewModelScope.launch {
                DateSelectStore.date_selected = fetchDate(uiAction.entry)
                //_oneShotEvents.send(OneShotEvents.GoToAddExercise(uiAction.entry.exerciseName))
                GoToAddExerciseUseCase(uiAction.entry.exerciseName, date =fetchDate(uiAction.entry))
            }
            is UiAction.ClickComment -> {
                NavigateToCommentUseCase(date = fetchDate(uiAction.entry),
                    exerciseName = uiAction.entry.exerciseName,
                    fetchComment(uiAction.entry)
                    )
            //_viewState.value.showComment.value = fetchComment(uiAction.entry)
            //_viewState.value = _viewState.value.copy(showComment = mutableStateOf(fetchComment(uiAction.entry)))
            }
            UiAction.DiaryEntryDialogView -> viewModelScope.launch {
                val date = fetchDate(viewState.value.showDiaryStats!!)
                DateSelectStore.date_selected = date
                NavigateToDayActivityUseCase(DateSelectStore.date_selected)
            }
            //UiAction.OnResume -> _viewState.value = viewState.value.copy(diaryEntries =  FetchDiaryUseCase())
        }
    }

    private fun fetchDate( dialogData: DialogData): String{
        return if(dialogData is DialogDataImpl){
            return (dialogData.diaryEntry as DiaryEntryImpl2).workoutDay.date
        } else {
            "2022-03-11"
        }
    }



    private fun fetchDate( diaryEntry: DiaryEntry): String{
        return if(diaryEntry is DiaryEntryImpl2){
            return diaryEntry.workoutDay.date
        } else {
            "2022-03-11"
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
        val diaryEntries: LiveData<List<DiaryEntry>>,
        val showDiaryStats: DialogData?,
        val showPersonalRecords: MutableState<List<String>?>?,
        val showExerciseEntryStats: MutableState<ExerciseEntryStats?>?,
        val showComment: MutableState<String?>
)

data class ExerciseEntryStats(val exeriseEntry: ExerciseEntry, override val dialogData: DialogData) : DialogDataProvider

interface DiaryEntry {
    val dayString: String
    val dateString: String
    val exerciseEntries: LiveData<List<LiveData<ExerciseEntry>>>

}

data class DiaryEntryViewOnly(
    override val dayString: String = "NullsDay",
    override val dateString: String = "March 12, 2022",
    override val exerciseEntries: LiveData<List<LiveData<ExerciseEntry>>> = MutableLiveData(),

    ): DiaryEntry {
}

data class DiaryEntryImpl2(
        override val dayString: String = "NullsDay",
        override val dateString: String = "March 12, 2022",
        override val exerciseEntries: LiveData<List<LiveData<ExerciseEntry>>> = MutableLiveData(),
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
    //val liveData: LiveData<WorkoutExercise>
}

data class MockExerciseEntry(
    override val exerciseName: String,
    override val amountOfSets: String,
    override val color: Int,
    override val showFire: Boolean,
    override val showPrOnly: Boolean,
    override val showComment: Boolean,
    override val records : List<String>,
    //override val liveData: LiveData<WorkoutExercise>,
): ExerciseEntry

data class ExerciseEntryImpl(
        override val exerciseName: String,
        override val amountOfSets: String,
        override val color: Int,
        override val showFire: Boolean,
        override val showPrOnly: Boolean,
        override val showComment: Boolean,
        override val records : List<String>,
        val workoutExercise: WorkoutExercise,
        //override val liveData: LiveData<WorkoutExercise>
): ExerciseEntry

sealed class UiAction{
    class ClickDiaryEntry(val diaryEntry: DiaryEntry) : UiAction()
    class ClickPersonalRecord(val entry: ExerciseEntry) : UiAction()
    class EditExerciseEntry(val entry: ExerciseEntry) : UiAction()
    class ClickExerciseEntry(val entry: ExerciseEntry) : UiAction()
    class ClickComment(val entry: ExerciseEntry) : UiAction()

    object DiaryEntryDialogDismiss : UiAction()
    object DiaryEntryDialogView : UiAction()
}
sealed class OneShotEvents{
   // class GoToAddExercise(val exerciseName: String): OneShotEvents()
   // class GoToDayActivity(val dateString: String) : OneShotEvents() {

   // }
}