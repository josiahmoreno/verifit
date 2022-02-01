package com.example.verifit

import android.app.AlertDialog
import android.graphics.Color
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.ArrayList

class MviViewModel(val localDataSource: WorkoutService,val timerService: TimerService ,val exerciseKey: String?) {
    private val coroutineScope = MainScope()


    private val _viewState: MutableStateFlow<ViewState> = MutableStateFlow(ViewState(workoutSets = MutableLiveData(),))
    val viewState = _viewState.asStateFlow()

    // See https://proandroiddev.com/android-singleliveevent-redux-with-kotlin-flow-b755c70bb055
    // For why channel > SharedFlow/StateFlow in this case
    private val _oneShotEvents = Channel<OneShotEvent>(Channel.BUFFERED)
    val oneShotEvents = _oneShotEvents.receiveAsFlow()
    var model = Model()

    init {
        val sets = localDataSource.fetchWorkSets()
        val triple = CalculateMaxWeight()
        model.WeightText = triple.second
        model.RepText = triple.first
        model.ExerciseComment = localDataSource.GetExercise()?.comment ?: ""
        _viewState.value = _viewState.value.copy(
            workoutSets = sets,
            weightText = triple.second,
            repText = triple.first
        )
    }

    private fun CalculateMaxWeight(): Pair<String,String> {
        var max_weight = 0.0
        var max_reps = 0
        var max_exercise_volume = 0.0

        // Find Max Weight and Reps for a specific exercise
        for (i in MainActivity.Workout_Days.indices) {
            for (j in MainActivity.Workout_Days[i].sets.indices) {
                if (MainActivity.Workout_Days[i].sets[j].volume > max_exercise_volume && MainActivity.Workout_Days[i].sets[j].exercise == exerciseKey) {
                    max_exercise_volume = MainActivity.Workout_Days[i].sets[j].volume
                    max_reps = Math.round(MainActivity.Workout_Days[i].sets[j].reps)
                            .toInt()
                    max_weight = MainActivity.Workout_Days[i].sets[j].weight
                }
            }
        }

        // If never performed the exercise leave Edit Texts blank
        return if (max_reps == 0 || max_weight == 0.0) {
            Pair("", "")
        } else {
            Pair(max_reps.toString(), max_weight.toString())
        }
    }

    fun onAction(uiAction: UiAction) {
        when (uiAction) {
            is UiAction.SaveExercise -> {
                coroutineScope.launch {
                    //_viewState.value = _viewState.value.copy(isLoading = true)
                    save(uiAction)
                }
            }
            is UiAction.Clear -> {
                if (model.ClickedSet == null) {
                    _viewState.value = _viewState.value.copy(false, "Clear", "","")
                } else {
                    // Show confirmation dialog  box
                    // Prepare to show exercise dialog box
                    _viewState.value = viewState.value.copy(showDeleteDialog = true)
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
                _viewState.value = viewState.value.copy(showDeleteDialog = false, clearButtonText = clearText, weightText =  weightText, repText = repsText)
                coroutineScope.launch {
                    _oneShotEvents.send(OneShotEvent.Toast("Set Selected is now: ${model.ClickedSet?.reps}, ${model.ClickedSet?.weight}"))
                }

            }
            is UiAction.NoDelete -> {
                _viewState.value = viewState.value.copy(showDeleteDialog = false)
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
                localDataSource.updateComment(MainActivity.date_selected, exerciseKey ,model.ExerciseComment)
                coroutineScope.launch {
                    _oneShotEvents.send(OneShotEvent.Toast("Comment Cleared"))
                }
            }
            is UiAction.SaveComment -> {
                model.ExerciseComment = uiAction.comment
                localDataSource.updateComment(MainActivity.date_selected, exerciseKey ,model.ExerciseComment)
                coroutineScope.launch {
                    _oneShotEvents.send(OneShotEvent.Toast("Comment Logged"))
                }
            }
            UiAction.ShowComments -> {
                _viewState.value = viewState.value.copy(showingCommentDialog = true, commentText = model.ExerciseComment)
            }
            UiAction.DialogDismissed -> _viewState.value = viewState.value.copy(showingCommentDialog =  false)
            UiAction.ShowGraph -> {
                // Create Array List that will hold graph data
                val Volume_Values = ArrayList<Entry>()
                var x = 0

                // Get Exercise Volume
                for (i in MainActivity.Workout_Days.indices) {
                    for (j in MainActivity.Workout_Days[i].exercises.indices) {
                        val current_exercise = MainActivity.Workout_Days[i].exercises[j]
                        if (current_exercise.exercise == exerciseKey) {
                            Volume_Values.add(Entry(x.toFloat(), current_exercise.volume.toFloat()))
                            x++
                        }
                    }
                }
                val volumeSet = LineDataSet(Volume_Values, "Volume")
                val data = LineData(volumeSet)
                volumeSet.lineWidth = 2f
                volumeSet.valueTextSize = 10f
                volumeSet.valueTextColor = Color.BLACK
                _viewState.value = viewState.value.copy(showingGraphDialog = true, lineData = data)
            }
            UiAction.ShowHistory -> {
                // Declare local data structure
                val All_Performed_Sessions = ArrayList<WorkoutExercise>()
                exerciseKey?.let {
                    All_Performed_Sessions.addAll(localDataSource.getExercisesWithName(exerciseKey).reversed())
                }
                _viewState.value = viewState.value.copy(showingHistoryDialog = true,history = All_Performed_Sessions);
            }
            UiAction.HistoryDismissed -> _viewState.value = viewState.value.copy(showingHistoryDialog =  false)
            UiAction.ShowTimer -> {
                // Set default seconds value to 180 i.e 3 minutes
                if (!model.TimerRunning) {
                    // Derive String value from chosen start time
                    // et_seconds.setText(String.valueOf((int) START_TIME_IN_MILLIS /1000));
                    loadSeconds()
                } else {
                    updateCountDownText()
                }


                // Minus Button
                minus_seconds.setOnClickListener(View.OnClickListener {
                    if (!et_seconds.getText().toString().isEmpty()) {
                        var seconds = et_seconds.getText().toString().toDouble()
                        seconds = seconds - 1
                        if (seconds < 0) {
                            seconds = 0.0
                        }
                        val seconds_int = seconds.toInt()
                        et_seconds.setText(seconds_int.toString())
                    }
                })

                // Plus Button
                plus_seconds.setOnClickListener(View.OnClickListener {
                    if (!et_seconds.getText().toString().isEmpty()) {
                        var seconds = et_seconds.getText().toString().toDouble()
                        seconds = seconds + 1
                        if (seconds < 0) {
                            seconds = 0.0
                        }
                        val seconds_int = seconds.toInt()
                        et_seconds.setText(seconds_int.toString())
                    }
                })
            }
            UiAction.StartTimer -> {
                if (model.TimerRunning) {
                    pauseTimer()
                } else {
                    saveSeconds(uiAction.)
                    startTimer()
                }
            }
            UiAction.ResetTimer -> {
                resetTimer()
            }
            UiAction.MinusSeconds -> TODO()
            UiAction.PlusSeconds -> TODO()
        }
    }

    private fun resetTimer() {
        if (model.TimerRunning) {
            pauseTimer()
            model.TimeLeftInMillis = model.START_TIME_IN_MILLIS
            updateCountDownText()
        }
    }

    public fun startTimer(){
        timerService.start()
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

    private fun loadSeconds(){

        val seconds : String = timerService.GetCurrentTime()

        // Change actual values that timer uses
        model.START_TIME_IN_MILLIS = (seconds.toInt() * 1000).toLong()
        model.TimeLeftInMillis = model.START_TIME_IN_MILLIS
        _viewState.value = viewState.value.copy(secondsLeft = seconds)
    }

    private fun updateCountDownText(){
        val seconds = model.TimeLeftInMillis.toInt() / 1000
        val minutes = seconds / 60
        _viewState.value = viewState.value.copy(secondsLeft = seconds.toString())
    }

    private suspend fun save(event: UiAction.SaveExercise){
        if (event.weight.isEmpty() || event.reps.isEmpty()) {
            //send toast
            _oneShotEvents.send(OneShotEvent.ErrorEmptyWeightAndReps("Please write Weight and Reps"))

        } else {
            // Get user sets && reps
            val reps = event.reps.toDouble()
            val weight = event.weight.toDouble()

            // Create New Set Object
            val workoutSet = WorkoutSet(
                MainActivity.date_selected,
                event.exerciseName,
                event.category,
                reps,
                weight
            )

            // Ignore wrong input
            if (reps == 0.0 || weight == 0.0 || reps < 0 || weight < 0) {
                _oneShotEvents.send(OneShotEvent.ErrorInvalidWeightAndReps("Please write correct Weight and Reps"))
            } else {
                // Find if workout day already exists
                val position = event.dayPosition
                    //MainActivity.getDayPosition(MainActivity.date_selected)

                // If workout day exists
                if (position >= 0) {
                    //add set to local stoage
                    localDataSource.addSet(position,workoutSet)
                    //model.Todays_Exercise_Sets.add(workoutSet)
                    //_viewState.value = viewState.value.workoutSets.po
                    //MainActivity.Workout_Days[position].addSet(workoutSet)
                } else {
                    val workoutDay = WorkoutDay()
                    workoutDay.addSet(workoutSet)
                    //add new day to local storage
                    localDataSource.addWorkoutDay(workoutDay)
                    //MainActivity.Workout_Days.add(workoutDay)
                }

                // Update Local Data Structure
                //refresh the recycerlview and the buttons
                //AddExerciseActivity.updateTodaysExercises()

                //send tost
                _oneShotEvents.send(OneShotEvent.SetLogged("Set Logged"))
                //Toast.makeText(applicationContext, "Set Logged", Toast.LENGTH_SHORT).show()
            }
        }

    }
    sealed class UiAction {
        class SaveExercise(val weight: String, val reps: String, val exerciseName: String, val category: String,val dayPosition: Int) : UiAction()
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
        object DialogDismissed : UiAction()
        object ShowGraph : UiAction()
        object ShowHistory : UiAction()
        object HistoryDismissed : UiAction()
        object ShowTimer : UiAction()
        object StartTimer : UiAction()
        object ResetTimer : UiAction()
        object MinusSeconds : UiAction()
        object PlusSeconds : UiAction()
        class SaveComment(val comment: String) : UiAction()
    }

    data class ViewState(
        val isLoading: Boolean = false,
        val clearButtonText: String = "Clear",
        val repText: String = "",
        val weightText: String = "",
        val showDeleteDialog: Boolean = false,
        val showingCommentDialog: Boolean = false,
        val workoutSets: LiveData<List<WorkoutSet>>,
        val commentText: String = "",
        val showingGraphDialog: Boolean = false,
        val lineData: LineData? = null,
        val showingHistoryDialog: Boolean = false,
        val history: List<WorkoutExercise> = ArrayList(),
        val secondsLeft: String = "",
        val timerButtonText: String = ""
    )

    sealed class OneShotEvent {
        class ErrorEmptyWeightAndReps(val message: String): OneShotEvent()
        class ErrorInvalidWeightAndReps(val message: String): OneShotEvent()
        class SetLogged(val message: String): OneShotEvent()
        class Toast(val toast: String) : OneShotEvent()
    }
}

