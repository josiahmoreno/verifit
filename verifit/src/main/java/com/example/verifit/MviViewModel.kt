package com.example.verifit

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


                _viewState.value = viewState.value.copy(showDeleteDialog = false )
                //val sets = localDataSource.fetchWorkSets().value

                model.ClickedSet = _viewState.value.workoutSets.value?.lastOrNull()
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

        // Fixed Myria induced bug
        //AddExerciseActivity.Clicked_Set = AddExerciseActivity.Todays_Exercise_Sets.size - 1
    }
    sealed class UiAction {
        class SaveExercise(val weight: String, val reps: String, val exerciseName: String, val category: String,val dayPosition: Int) : UiAction()
        class WorkoutClick(val workoutSet: WorkoutSet) : UiAction() {

        }

        object Clear : UiAction()
        object YesDelete : UiAction()
        object NoDelete : UiAction()
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

