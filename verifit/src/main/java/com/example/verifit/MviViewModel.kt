package com.example.verifit

import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.ArrayList

class MviViewModel(val localDataSource: WorkoutService) {
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
        _viewState.value = _viewState.value.copy(workoutSets = sets)
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
                if (model.Todays_Exercise_Sets.isEmpty()) {
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
                    // Let the user know I guess
                //Toast.makeText(applicationContext, "Set Deleted", Toast.LENGTH_SHORT).show()

                // Update Local Data Structure
                //updateTodaysExercises()
                //alertDialog.dismiss()
                _viewState.value = viewState.value.copy(showDeleteDialog = false)
                // Update Clicked set to avoid crash
                //AddExerciseActivity.Clicked_Set = Todays_Exercise_Sets.size - 1
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
        class WorkoutClick(val pos: Int) : UiAction() {

        }

        object Clear : UiAction()
        object YesDelete : UiAction()
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

