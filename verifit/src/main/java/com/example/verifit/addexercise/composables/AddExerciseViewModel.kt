package com.example.verifit.addexercise.composables

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.example.verifit.KnownExerciseService
import com.example.verifit.WorkoutDay
import com.example.verifit.WorkoutExercise
import com.example.verifit.WorkoutSet
import com.example.verifit.addexercise.history.date
import com.example.verifit.addexercise.history.exerciseName
import com.example.verifit.common.*
import com.example.verifit.main.BaseViewModel
import com.example.verifit.settings.ToastMaker
import com.example.verifit.singleton.DateSelectStore
import com.example.verifit.workoutservice.WorkoutService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddExerciseViewModel @Inject constructor(
        private val toastMaker: ToastMaker,
        val localDataSource: WorkoutService,
        private val knownExerciseService: KnownExerciseService,
        private val NavigateToHistoryDialogUseCase: NavigateToHistoryDialogUseCase,
        private val NavigateToGraphDialogUseCase: NavigateToGraphDialogUseCase,
        private val NavigateToTimerUseCase: NavigateToTimerUseCase,
        private val NavigateToCommentUseCase: NavigateToCommentUseCase,
        private val NavigateToDeleteSetDialogUseCase: NavigateToDeleteSetDialogUseCase,
        private val UpdateWorkoutSetUseCase: UpdateWorkoutSetUseCase,
        val savedStateHandle: SavedStateHandle?,
        private val liveData: ListenToCommentResultsUseCase?
) : BaseViewModel<AddExerciseViewState, AddExerciseViewModel.UiAction, AddExerciseViewModel.OneShotEvent>(AddExerciseViewState.initialState(date = savedStateHandle?.date,
    workoutService = localDataSource,
    exerciseKey = savedStateHandle?.exerciseName)
) {

    var exerciseKey: String = savedStateHandle?.exerciseName!!
    var date: String = savedStateHandle?.date!!
    var model = Model()

    init {
        viewModelScope.launch {
            val sets = try {
                localDataSource.fetchWorkoutExercise(exerciseKey, date).asFlow().collect {
                    if (it.isNull || it.sets.size == 0) {
                        model.ClickedSet = null
                    } else if(model.ClickedSet != null){
                        model.ClickedSet = null
                        //model.ClickedSet = it.sets.last()
                    }
                    val clearText = if (model.ClickedSet == null) "Clear" else "Delete"
                    val (reps,weight) = localDataSource.calculateMaxWeight(exerciseKey)
                    _viewState.value = viewState.value.copy(clearButtonText = clearText,
                        selected = model.ClickedSet,
                        weightText = weight,
                        repText = reps)
                }
            } catch (e: Exception){
                MutableLiveData(WorkoutExercise.Null())
            }
            liveData?.invoke()?.asFlow()?.collect{
                model.ExerciseComment = it
                Log.d("Add.Comment","comment = $it")
            }
        }
            model.ExerciseComment = localDataSource.getExercise(exerciseKey)?.comment ?: ""
    }

    override fun onAction(uiAction: UiAction) {
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
                }
            }
            is UiAction.YesDelete -> {
                val to_be_removed_set = model.ClickedSet

                localDataSource.removeSet(to_be_removed_set!!)
                // Find the set in main data structure and delete it
                toastMaker.makeText("Set Deleted")

            }
            is UiAction.WorkoutClick -> {
                    model.ClickedSet = uiAction.workoutSet
                    _viewState.value = viewState.value.copy(weightText = uiAction.workoutSet.weight.toString(),
                        repText = uiAction.workoutSet.reps.toInt().toString(),
                        clearButtonText = "Delete", selected = uiAction.workoutSet
                    )
            }
            is UiAction.SelectedWorkoutClick -> {
                // Update Edit Texts
                    model.ClickedSet = null
                    _viewState.value = viewState.value.copy(
                        clearButtonText = "Clear", selected =  model.ClickedSet
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
            UiAction.ShowComments -> {
                NavigateToCommentUseCase(DateSelectStore.date_selected, exerciseKey, model.ExerciseComment)
            }
            UiAction.ShowGraph -> {
                NavigateToGraphDialogUseCase(exerciseKey?: "")
            }
            UiAction.ShowHistory -> {
                NavigateToHistoryDialogUseCase(exerciseKey?: "")
            }
            UiAction.ShowTimer -> {
                NavigateToTimerUseCase()
            }
            is UiAction.OnWeightChange -> {
                _viewState.value = viewState.value.copy(weightText = uiAction.edt)
            }
            is UiAction.OnRepChange -> {
                _viewState.value = viewState.value.copy(repText = uiAction.edt)
            }
            is UiAction.UpdateExercise -> {
                model.ClickedSet?.reps = viewState.value.repText.toDouble()
                model.ClickedSet?.weight = viewState.value.weightText.toDouble()
                UpdateWorkoutSetUseCase(model.ClickedSet!!)
            }
        }
    }

    private suspend fun save(event: UiAction.SaveExercise){
        if (event.weight.isEmpty() || event.reps.isEmpty()) {
            //send toast
            toastMaker.makeText("Please write Weight and Reps")
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
                toastMaker.makeText("Please write correct Weight and Reps")
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
                toastMaker.makeText("Set Logged")
            }
        }
    }

    //val category: String,val dayPosition: Int
    sealed class UiAction {
        class SaveExercise(val weight: String, val reps: String, val exerciseName: String, ) : UiAction()
        class WorkoutClick(val workoutSet: WorkoutSet) : UiAction()
        object Clear : UiAction()
        object YesDelete : UiAction()
        object WeightIncrement : UiAction()
        object WeightDecrement : UiAction()
        object RepIncrement : UiAction()
        object RepDecrement : UiAction()
        object ShowComments : UiAction()
        object ShowGraph : UiAction()
        object ShowHistory : UiAction()
        object ShowTimer : UiAction()
        class OnWeightChange(val edt: String) : UiAction()
        class OnRepChange(val edt: String) : UiAction()
        class UpdateExercise(weightText: String, repText: String, exerciseName: String) :
            UiAction()

        class SelectedWorkoutClick: UiAction()

    }

    sealed class OneShotEvent {
    }
}

