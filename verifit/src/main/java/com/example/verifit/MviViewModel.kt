package com.example.verifit

import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MviViewModel(val localDataSource: WorkoutService,  val exerciseKey: String?) {
    private val coroutineScope = MainScope()


    private val _viewState: MutableStateFlow<ViewState> = MutableStateFlow(ViewState(workoutSets = MutableLiveData()))
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
        model.RepText =  triple.first
        _viewState.value = _viewState.value.copy(workoutSets = sets, weightText = triple.second, repText = triple.first)

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
        if (max_reps == 0 || max_weight == 0.0) {
            //AddExerciseActivity.et_reps!!.setText("")
            //AddExerciseActivity.et_weight!!.setText("")
            return Pair("", "")
        } else {
            //AddExerciseActivity.et_reps!!.setText(max_reps.toString())
            //AddExerciseActivity.et_weight!!.setText(max_weight.toString())
            return Pair(max_reps.toString(), max_weight.toString())
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



                //val sets = localDataSource.fetchWorkSets().value

                model.ClickedSet = _viewState.value.workoutSets.value?.lastOrNull()
                val clearText = if(model.ClickedSet == null) "Clear" else "Delete"
                val weightText =  if(model.ClickedSet == null) "" else "${model.ClickedSet?.weight}"
                val repsText =  if(model.ClickedSet == null) "" else "${model.ClickedSet?.reps?.toInt()}"
                _viewState.value = viewState.value.copy(showDeleteDialog = false, clearButtonText = clearText, weightText =  weightText, repText = repsText)
                coroutineScope.launch {
                    _oneShotEvents.send(OneShotEvent.Toast("Set Selected is now: ${model.ClickedSet?.reps}, ${model.ClickedSet?.weight}"))
                }
                // Update Clicked set to avoid crash
                //AddExerciseActivity.Clicked_Set = Todays_Exercise_Sets.size - 1

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

                //TODO Replace with  storage
                // Check if exercise exists (cannot comment on non-existant exercise)
                // Find if workout day already exists
                val exercise_position =
                    MainActivity.getExercisePosition(MainActivity.date_selected, exerciseKey)
                if (exercise_position >= 0) {
                    println("We can comment, exercise exists")
                } else {
                    println("We can't comment, exercise doesn't exist")
                    coroutineScope.launch {
                        _oneShotEvents.send(OneShotEvent.Toast("Can't comment without sets"))
                    }
                    return
                }

                //TODO Replace with  storage
                // Get the date for today
                val day_position = MainActivity.getDayPosition(MainActivity.date_selected)
                // Modify the data structure to add the comment
                MainActivity.Workout_Days[day_position].exercises[exercise_position].comment = model.ExerciseComment
                coroutineScope.launch {
                    _oneShotEvents.send(OneShotEvent.Toast("Comment Cleared"))
                }

            }
            is UiAction.SaveComment -> {
                // Check if exercise exists (cannot comment on non-existant exercise)
                // Find if workout day already exists
                val exercise_position =
                    MainActivity.getExercisePosition(MainActivity.date_selected, exerciseKey)
                if (exercise_position >= 0) {
                    println("We can comment, exercise exists")
                } else {
                    println("We can't comment, exercise doesn't exist")
                    coroutineScope.launch {
                        _oneShotEvents.send(OneShotEvent.Toast("Can't comment without sets"))
                    }
                    return
                }

                model.ExerciseComment = uiAction.comment
                // Get user comment

                // Print it for sanity check
                println("$uiAction.comment")

                //TODO Replace with  storage
                // Get the date for today
                val day_position = MainActivity.getDayPosition(MainActivity.date_selected)
                // Modify the data structure to add the comment
                MainActivity.Workout_Days[day_position].exercises[exercise_position].comment = model.ExerciseComment
                coroutineScope.launch {
                    _oneShotEvents.send(OneShotEvent.Toast("Comment Logged"))
                }
            }
        }
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
        class SaveComment(val comment: String) : UiAction()
    }

    data class ViewState(
        val isLoading: Boolean = false,
        val clearButtonText:String = "Clear",
        val repText: String = "",
        val weightText: String = "",
        val showDeleteDialog: Boolean = false,
        val workoutSets: LiveData<List<WorkoutSet>>
    )

    sealed class OneShotEvent {
        class ErrorEmptyWeightAndReps(val message: String): OneShotEvent()
        class ErrorInvalidWeightAndReps(val message: String): OneShotEvent()
        class SetLogged(val message: String): OneShotEvent()
        class Toast(val toast: String) : OneShotEvent()
    }


}

