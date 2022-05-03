package com.example.verifit.addexercise.composables

import android.graphics.Color
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.verifit.KnownExerciseService
import com.example.verifit.WorkoutDay
import com.example.verifit.WorkoutExercise
import com.example.verifit.WorkoutSet
import com.example.verifit.common.NavigateToCommentUseCase
import com.example.verifit.common.NavigateToGraphDialogUseCase
import com.example.verifit.common.NavigateToHistoryDialogUseCase
import com.example.verifit.common.NavigateToTimerUseCase
import com.example.verifit.singleton.DateSelectStore
import com.example.verifit.workoutservice.WorkoutService
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.ArrayList

class AddExerciseViewModel(val localDataSource: WorkoutService,
                           private val timerService: TimerService,
                           private val knownExerciseService: KnownExerciseService,
                           private val NavigateToHistoryDialogUseCase: NavigateToHistoryDialogUseCase,
                           private val NavigateToGraphDialogUseCase: NavigateToGraphDialogUseCase,
                           private val NavigateToTimerUseCase: NavigateToTimerUseCase,
                           private val NavigateToCommentUseCase: NavigateToCommentUseCase,
                           private val exerciseKey: String?) : ViewModel() {
    private val coroutineScope = MainScope()

    var model = Model()
    private val _viewState: MutableStateFlow<AddExerciseViewState> = MutableStateFlow(AddExerciseViewState(exerciseName = exerciseKey,workoutSets = MutableLiveData(), secondsLeftLiveData = model.secondsLiveData))
    val viewState = _viewState.asStateFlow()

    // See https://proandroiddev.com/android-singleliveevent-redux-with-kotlin-flow-b755c70bb055
    // For why channel > SharedFlow/StateFlow in this case
    private val _oneShotEvents = Channel<OneShotEvent>(Channel.BUFFERED)
    val oneShotEvents = _oneShotEvents.receiveAsFlow()

    init {
        val sets = localDataSource.fetchWorkSets(exerciseKey)
        val triple = localDataSource.calculateMaxWeight(exerciseKey)
        model.WeightText = triple.second
        model.RepText = triple.first
        model.ExerciseComment = localDataSource.getExercise(exerciseKey)?.comment ?: ""
        _viewState.value = _viewState.value.copy(
            workoutSets = sets,
            weightText = triple.second,
            repText = triple.first,
                secondsLeftLiveData = model.secondsLiveData
        )
        timerService.onTick = {
            model.TimeLeftInMillis = it
            updateCountDownText()
        }
        timerService.onFinish = {
            model.TimerRunning = false
            _viewState.value = viewState.value.copy(timerButtonText = "Start")
        }
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
                    coroutineScope.launch {
                        _oneShotEvents.send(OneShotEvent.ShowDeleteDialog)
                    }
                }
            }
            is UiAction.YesDelete -> {
                val to_be_removed_set = model.ClickedSet

                localDataSource.removeSet(to_be_removed_set!!)
                // Find the set in main data structure and delete it

                coroutineScope.launch {
                    _oneShotEvents.send(OneShotEvent.Toast("Set Deleted"))
                }

                model.ClickedSet = _viewState.value.workoutSets.value?.lastOrNull()
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
                NavigateToCommentUseCase(exerciseKey!!)
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
            is UiAction.StartTimer -> {
                if (model.TimerRunning) {
                    pauseTimer()
                } else {
                    saveSeconds(uiAction.secondText)
                    startTimer()
                }
            }
            UiAction.ResetTimer -> resetTimer()
            is UiAction.MinusSeconds -> {
                if (uiAction.secondText.isNotEmpty()) {
                    var seconds = uiAction.secondText.toDouble()
                    seconds -= 1
                    if (seconds < 0) {
                        seconds = 0.0
                    }
                    val seconds_int = seconds.toInt()
                    changeSeconds(seconds_int.toString())
                }
            }
            is UiAction.PlusSeconds -> {
                if (uiAction.secondText.isNotEmpty()) {
                    var seconds = uiAction.secondText.toDouble()
                    seconds += 1
                    if (seconds < 0) {
                        seconds = 0.0
                    }
                    val seconds_int = seconds.toInt()
                    changeSeconds(seconds_int.toString())
                }
            }
            is UiAction.OnWeightChange -> {
                _viewState.value = viewState.value.copy(weightText = uiAction.edt)
            }
            is UiAction.OnRepChange -> {
                _viewState.value = viewState.value.copy(repText = uiAction.edt)
            }
            is UiAction.SaveExercise2 -> TODO()
            is UiAction.OnSecondsChange -> _viewState.value = viewState.value.copy(secondsLeftString = uiAction.secondsLeftString)
        }
    }

    private fun changeSeconds(seconds : String){
       // model._secondsLiveData.value = seconds
        _viewState.value = viewState.value.copy(secondsLeftString = seconds)
    }

    private fun resetTimer() {
        if (model.TimerRunning) {
            pauseTimer()
            model.START_TIME_IN_MILLIS = 180 * 1000
            model.TimeLeftInMillis = model.START_TIME_IN_MILLIS
            updateCountDownText()
        }
    }

    private fun startTimer(){
        timerService.start(model.TimeLeftInMillis)
        model.TimerRunning = true
        _viewState.value = viewState.value.copy(timerButtonText= "Pause")
    }

    private fun pauseTimer(){
        timerService.cancel()
        model.TimerRunning = false
        _viewState.value = viewState.value.copy(timerButtonText= "Start")
    }

    private fun saveSeconds(seconds: String){
        if (seconds.isNotEmpty()) {
            // Change actual values that timer uses
            model.START_TIME_IN_MILLIS = (seconds.toInt() * 1000).toLong()
            model.TimeLeftInMillis = model.START_TIME_IN_MILLIS

            // Save to shared preferences
            timerService.save(seconds)
        }
    }

    private fun updateCountDownText(){
        val seconds = model.TimeLeftInMillis.toInt() / 1000
        changeSeconds(seconds.toString())
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
            val dayPosition = localDataSource.fetchDayPosition(DateSelectStore.date_selected)
            // Create New Set Object
            val workoutSet = WorkoutSet(
                DateSelectStore.date_selected,
                event.exerciseName,
                category,
                reps,
                weight
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

