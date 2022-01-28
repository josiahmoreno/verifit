package com.example.verifit

import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.ArrayList

class MviViewModel(val localDataSource: WorkoutService) {
    private val coroutineScope = MainScope()

    private val _viewState: MutableStateFlow<ViewState> = MutableStateFlow(ViewState())
    val viewState = _viewState.asStateFlow()

    // See https://proandroiddev.com/android-singleliveevent-redux-with-kotlin-flow-b755c70bb055
    // For why channel > SharedFlow/StateFlow in this case
    private val _oneShotEvents = Channel<OneShotEvent>(Channel.BUFFERED)
    val oneShotEvents = _oneShotEvents.receiveAsFlow()
    var model = Model()
    fun onAction(uiAction: UiAction) {
        when (uiAction) {
            is UiAction.SaveExercise -> {
                coroutineScope.launch {
                    //_viewState.value = _viewState.value.copy(isLoading = true)
                    save(uiAction)
                    //withContext(Dispatchers.IO) { answerService.save(uiAction.answer) }
//                    val text = if (uiAction.answer == "Nacho cheese") {
//                        "You've heard too many cheese jokes"
//                    } else {
//                        "Nacho cheese"
//                    }
//                    _viewState.value = _viewState.value.copy(textToDisplay = text)
//                    _oneShotEvents.send(OneShotEvent.NavigateToResults)
//                    _viewState.value = _viewState.value.copy(isLoading = false)
                }
            }
            is UiAction.Clear -> {
                if (model.Todays_Exercise_Sets.isEmpty()) {
                    _viewState.value = _viewState.value.copy(false, "Clear", "","")
                } else {
                    // Show confirmation dialog  box
                    // Prepare to show exercise dialog box
                    _viewState.value = viewState.value.copy(showDeleteDialog = true)
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
        AddExerciseActivity.Clicked_Set = AddExerciseActivity.Todays_Exercise_Sets.size - 1
    }
    sealed class UiAction {
        class SaveExercise(val weight: String, val reps: String, val exerciseName: String, val category: String,val dayPosition: Int) : UiAction()
        class Clear : UiAction()
    }

    data class ViewState(
        val isLoading: Boolean = false,
        val clearButtonText:String = "Clear",
        val repText: String = "",
        val weightText: String = "",
        val showDeleteDialog: Boolean = false
    )

    sealed class OneShotEvent {
        class ErrorEmptyWeightAndReps(val message: String): OneShotEvent()
        class ErrorInvalidWeightAndReps(val message: String): OneShotEvent()
        class SetLogged(val message: String): OneShotEvent()
    }


}

