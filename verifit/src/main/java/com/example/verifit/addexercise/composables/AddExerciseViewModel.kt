package com.example.verifit.addexercise.composables

import android.graphics.Color
import android.os.Debug
import android.util.Log
import androidx.lifecycle.*
import com.example.verifit.KnownExerciseService
import com.example.verifit.WorkoutDay
import com.example.verifit.WorkoutExercise
import com.example.verifit.WorkoutSet
import com.example.verifit.common.*
import com.example.verifit.singleton.DateSelectStore
import com.example.verifit.workoutservice.WorkoutService
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.ArrayList
import kotlin.Exception


class AddExerciseViewModel @AssistedInject constructor(
        val localDataSource: WorkoutService,
        private val knownExerciseService: KnownExerciseService,
        private val NavigateToHistoryDialogUseCase: NavigateToHistoryDialogUseCase,
        private val NavigateToGraphDialogUseCase: NavigateToGraphDialogUseCase,
        private val NavigateToTimerUseCase: NavigateToTimerUseCase,
        private val NavigateToCommentUseCase: NavigateToCommentUseCase,
        private val NavigateToDeleteSetDialogUseCase: NavigateToDeleteSetDialogUseCase,
        @Assisted("exerciseKey") var exerciseKey: String,
        @Assisted("date") var date: String,
        private val liveData: ListenToCommentResultsUseCase?
) : ViewModel() {
    @AssistedFactory
    interface Factory {
        fun create(@Assisted("exerciseKey") exerciseKey: String,@Assisted("date") date: String): AddExerciseViewModel
    }
    companion object {
        fun provideFactory(
                assistedFactory: Factory,
                exerciseKey: String,
                date: String
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(exerciseKey,date) as T
            }
        }
    }

    private val coroutineScope = MainScope()

    var model = Model()
    private val _viewState: MutableStateFlow<AddExerciseViewState> =
            MutableStateFlow(AddExerciseViewState.initialState(date = date,
                    workoutService = localDataSource,
                    exerciseKey = exerciseKey)
            )

    val viewState = _viewState.asStateFlow()

    // See https://proandroiddev.com/android-singleliveevent-redux-with-kotlin-flow-b755c70bb055
    // For why channel > SharedFlow/StateFlow in this case
    private val _oneShotEvents = Channel<OneShotEvent>(Channel.BUFFERED)
    val oneShotEvents = _oneShotEvents.receiveAsFlow()

    init {



        viewModelScope.launch {
            val sets = try {
                localDataSource.fetchWorkoutExercise(exerciseKey, date).asFlow().collect {
                    if (it.isNull || it.sets.size == 0) {
                        model.ClickedSet = null
                    } else {
                        model.ClickedSet = it.sets.last()
                    }
                    val clearText = if (model.ClickedSet == null) "Clear" else "Delete"
                    val weightText =
                        if (model.ClickedSet == null) "" else "${model.ClickedSet?.weight}"
                    val repsText =
                        if (model.ClickedSet == null) "" else "${model.ClickedSet?.reps?.toInt()}"
                    _viewState.value = viewState.value.copy(clearButtonText = clearText,
                        weightText = weightText,
                        repText = repsText)
                }
            } catch (e: Exception){
                MutableLiveData(WorkoutExercise.Null())
            }
            liveData?.invoke()?.asFlow()?.collect{
                model.ExerciseComment = it
                Log.d("Add.Comment","comment = $it")
            }
        }

//        val triple = localDataSource.calculateMaxWeight(exerciseKey)
//        model.WeightText = triple.second
//        model.RepText = triple.first
            model.ExerciseComment = localDataSource.getExercise(exerciseKey)?.comment ?: ""
//        _viewState.value = _viewState.value.copy(
//            weightText = triple.second,
//            repText = triple.first
//        )
    }



    fun onAction(uiAction: UiAction) {
        when (uiAction) {
            is UiAction.SaveExercise -> {
                coroutineScope.launch {
                    save(uiAction)
                }
            }
            is UiAction.Clear -> {
                if (model.ClickedSet == null) {
                    _viewState.value = _viewState.value.copy(isLoading= false, clearButtonText = "Clear", repText =  "", weightText = "")
                } else {
                    // Show confirmation dialog  box
                    // Prepare to show exercise dialog box
                    NavigateToDeleteSetDialogUseCase(model.ClickedSet.hashCode().toString())
//                    coroutineScope.launch {
//                        _oneShotEvents.send(OneShotEvent.ShowDeleteDialog)
//                    }
                }
            }
            is UiAction.YesDelete -> {
                val to_be_removed_set = model.ClickedSet

                localDataSource.removeSet(to_be_removed_set!!)
                // Find the set in main data structure and delete it

                coroutineScope.launch {
                    _oneShotEvents.send(OneShotEvent.Toast("Set Deleted"))
                }

                //model.ClickedSet = _viewState.value.workoutSets
                TODO("Fix the yes delete dialog")
                val clearText = if(model.ClickedSet == null) "Clear" else "Delete"
                val weightText =  if(model.ClickedSet == null) "" else "${model.ClickedSet?.weight}"
                val repsText =  if(model.ClickedSet == null) "" else "${model.ClickedSet?.reps?.toInt()}"
                _viewState.value = viewState.value.copy(clearButtonText = clearText, weightText =  weightText, repText = repsText)

            }
            is UiAction.NoDelete -> {

                coroutineScope.launch {
                    //_oneShotEvents.send(OneShotEvent.ShowDeleteDialog)
                }
            }
            is UiAction.WorkoutClick -> {
                // Update Edit Texts
                model.ClickedSet = uiAction.workoutSet
                _viewState.value = viewState.value.copy(weightText = uiAction.workoutSet.weight.toString(),
                        repText = uiAction.workoutSet.reps.toInt().toString(),
                        clearButtonText = "Delete"
                )
            }
            UiAction.WeightIncrement -> {
                if (model.WeightText.isNotEmpty()) {
                    model.WeightText = (model.WeightText.toDouble() + 1).toString()
                } else {
                    model.WeightText = "1.0"
                }
                _viewState.value = viewState.value.copy(weightText = model.WeightText)
            }
            UiAction.WeightDecrement -> {
                if (model.WeightText.isNotEmpty()) {
                    var decrementedValue = (model.WeightText.toDouble() -1 )
                    if(decrementedValue < 0.0){
                        decrementedValue = 0.0
                    }
                    model.WeightText = decrementedValue.toString()
                } else {
                    model.WeightText = "0.0"
                }
                _viewState.value = viewState.value.copy(weightText = model.WeightText)
            }
            UiAction.RepIncrement -> {
                if (model.RepText.isNotEmpty()) {
                    model.RepText = (model.RepText.toDouble() + 1).toInt().toString()
                } else {
                    model.RepText = "1"
                }
                _viewState.value = viewState.value.copy(repText = model.RepText)
            }
            UiAction.RepDecrement -> {
                if (model.RepText.isNotEmpty()) {
                    var decrementedValue = (model.RepText.toDouble() -1 )
                    if(decrementedValue < 0){
                        decrementedValue = 0.0
                    }
                    model.RepText = decrementedValue.toInt().toString()
                } else {
                    model.RepText = "0"
                }
                _viewState.value = viewState.value.copy(repText = model.RepText)
            }
            UiAction.ClearComment -> {
                model.ExerciseComment = ""
                localDataSource.updateComment(DateSelectStore.date_selected, exerciseKey ,model.ExerciseComment)
                coroutineScope.launch {
                    _oneShotEvents.send(OneShotEvent.Toast("Comment Cleared"))
                }
            }
            is UiAction.SaveComment -> {
                model.ExerciseComment = uiAction.comment
                localDataSource.updateComment(DateSelectStore.date_selected, exerciseKey ,model.ExerciseComment)
                coroutineScope.launch {
                    _oneShotEvents.send(OneShotEvent.Toast("Comment Logged"))
                }
            }
            UiAction.ShowComments -> {
                NavigateToCommentUseCase(DateSelectStore.date_selected, exerciseKey, model.ExerciseComment)
//                coroutineScope.launch {
//                    _oneShotEvents.send(OneShotEvent.ShowCommentDialog(model.ExerciseComment))
//                }
            }
            UiAction.ShowGraph -> {

                NavigateToGraphDialogUseCase(exerciseKey?: "")
//                coroutineScope.launch {
//                    _oneShotEvents.send(OneShotEvent.ShowGraphDialog(data))
//                }
            }
            UiAction.ShowHistory -> {
                // Declare local data structure
                val All_Performed_Sessions = ArrayList<WorkoutExercise>()
                exerciseKey?.let {
                    All_Performed_Sessions.addAll(localDataSource.getExercisesWithName(exerciseKey).reversed())
                }

                NavigateToHistoryDialogUseCase(exerciseKey?: "")
//                coroutineScope.launch {
//                    _oneShotEvents.send(OneShotEvent.ShowHistoryDialog(exerciseKey ?: "",
//                        All_Performed_Sessions))
//                }
//                _viewState.value = _viewState.value.copy(history = All_Performed_Sessions)
            }
            UiAction.ShowTimer -> {
                // Set default seconds value to 180 i.e 3 minutes
//                val secString : String = if (!model.TimerRunning) {
//                    val seconds : String = timerService.getCurrentTime()
//                    // Change actual values that timer uses
//                    model.START_TIME_IN_MILLIS = (seconds.toInt() * 1000).toLong()
//                    model.TimeLeftInMillis = model.START_TIME_IN_MILLIS
//                    seconds
//                } else {
//                    val seconds = model.TimeLeftInMillis.toInt() / 1000
//                    seconds.toString()
//                }
//                coroutineScope.launch {
//                    _oneShotEvents.send(OneShotEvent.ShowTimerDialog(secString,
//                        if (model.TimerRunning) "Pause" else "Start"))
//                }
//                changeSeconds(secString)
                NavigateToTimerUseCase()
            }
            is UiAction.OnWeightChange -> {
                _viewState.value = viewState.value.copy(weightText = uiAction.edt)
            }
            is UiAction.OnRepChange -> {
                _viewState.value = viewState.value.copy(repText = uiAction.edt)
            }
            is UiAction.SaveExercise2 -> TODO()
        }
    }







    private suspend fun save(event: UiAction.SaveExercise){
        if (event.weight.isEmpty() || event.reps.isEmpty()) {
            //send toast
            _oneShotEvents.send(OneShotEvent.Toast("Please write Weight and Reps"))

        } else {
            // Get user sets && reps
            val reps = event.reps.toDouble()
            val weight = event.weight.toDouble()

            val category = knownExerciseService.fetchExerciseCategory(exerciseKey)
            val dayPosition = localDataSource.fetchDayPosition(date!!)
            // Create New Set Object
            val workoutSet = WorkoutSet(
                date,
                event.exerciseName,
                category,
                reps,
                weight,
                model.ExerciseComment
            )

            // Ignore wrong input
            if (reps == 0.0 || weight == 0.0 || reps < 0 || weight < 0) {
                _oneShotEvents.send(OneShotEvent.Toast("Please write correct Weight and Reps"))
            } else {
                // Find if workout day already exists
                val position = dayPosition

                // If workout day exists
                if (position >= 0) {
                    //add set to local stoage
                    localDataSource.addSet(position,workoutSet)
                } else {
                    val workoutDay = WorkoutDay()
                    workoutDay.addSet(workoutSet)
                    //add new day to local storage
                    localDataSource.addWorkoutDay(workoutDay, exerciseKey)
                }
                if(viewState.value.workoutSets.value!!.isNull){
                    _viewState.value = viewState.value.copy(workoutSets = localDataSource.fetchWorkoutExercise(exerciseName = workoutSet.exercise,date = workoutSet.date))
                }

                // Update Local Data Structure
                //refresh the recycerlview and the buttons
                //send tost
                _oneShotEvents.send(OneShotEvent.Toast("Set Logged"))
            }
        }
    }

    //val category: String,val dayPosition: Int
    sealed class UiAction {
        class SaveExercise(val weight: String, val reps: String, val exerciseName: String, ) : UiAction()
        class SaveExercise2(val weight: String, val reps: String, val exerciseName: String, val category: String,val dayPosition: Int) : UiAction()
        class WorkoutClick(val workoutSet: WorkoutSet) : UiAction()
        object Clear : UiAction()
        object YesDelete : UiAction()
        object NoDelete : UiAction()
        object WeightIncrement : UiAction()
        object WeightDecrement : UiAction()
        object RepIncrement : UiAction()
        object RepDecrement : UiAction()
        object ClearComment : UiAction()
        object ShowComments : UiAction()
        object ShowGraph : UiAction()
        object ShowHistory : UiAction()

        object ShowTimer : UiAction()
        class StartTimer(val secondText : String) : UiAction()
        object ResetTimer : UiAction()
        class OnWeightChange(val edt: String) : UiAction()
        class OnRepChange(val edt: String) : UiAction()


        class MinusSeconds(val secondText : String) : UiAction()
        class PlusSeconds(val secondText : String) : UiAction()
        class SaveComment(val comment: String) : UiAction()
        class OnSecondsChange(val secondsLeftString: String) : UiAction()
    }



    sealed class OneShotEvent {
        class Toast(val toast: String) : OneShotEvent()
        class ShowTimerDialog(val seconds: String, val buttonText : String) : OneShotEvent()
        class ShowGraphDialog(val lineData: LineData) : OneShotEvent()
        class ShowStatsDialog(val lineData: LineData) : OneShotEvent()
        class ShowHistoryDialog(val exerciseName: String, val history: List<WorkoutExercise>) : OneShotEvent()
        class ShowCommentDialog(val exerciseComment: String) : OneShotEvent()
        object ShowDeleteDialog : OneShotEvent()
    }
}

