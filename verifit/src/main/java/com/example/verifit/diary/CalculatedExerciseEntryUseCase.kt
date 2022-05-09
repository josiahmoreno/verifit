package com.example.verifit.diary

import com.example.verifit.WorkoutExercise
import com.example.verifit.workoutservice.WorkoutService
import java.text.ParseException
import java.text.SimpleDateFormat

interface CalculatedExerciseEntryUseCase {
    operator fun invoke(diaryEntry: ExerciseEntry) = calculate(diaryEntry)
    operator fun invoke(exerciseName: String, date: String ): DialogData

    fun calculate(diaryEntry: ExerciseEntry): DialogData
}

class CalculatedExerciseEntryUseCaseImpl(val workoutService: WorkoutService) : CalculatedExerciseEntryUseCase {


    fun calculate(workoutExercise: WorkoutExercise): DialogData {
        val sets = Math.round(workoutExercise.totalSets).toInt().toString()
        val totalSets: Triple<String, String, String> = Triple("Total Sets", sets, "sets")

        val reps = Math.round(workoutExercise.totalReps).toInt().toString()
        val totalReps: Triple<String, String, String> = Triple("Total Reps", reps, "reps")

        val totalVolume: Triple<String, String, String> = Triple("Total Volume", workoutExercise.volume.toString(), "kg")
        val maxWeight: Triple<String, String, String> = Triple("Max Weight", workoutExercise.maxWeight.toString(), "kg")
        val maxReps: Triple<String, String, String> = Triple("Max Reps", Math.round(workoutExercise.maxReps).toInt().toString(), "reps")

        val maxSetVolume: Triple<String, String, String> = Triple("Max Set Volume", workoutExercise.maxSetVolume.toString(), "kg")

        val estimatedOneRepMax: Triple<String, String, String> = Triple("Estimated 1RM", workoutExercise.estimatedOneRepMax.toString(), "kg")
        val actualOneRepMax: Triple<String, String, String> = Triple("Actual 1RM", workoutExercise.actualOneRepMax.toString(), "kg")


        return DialogDataViewOnly(workoutExercise.exercise, listOf(totalSets,
                totalReps,
                totalVolume,
                maxWeight,
                maxReps,
                maxSetVolume,
                estimatedOneRepMax,
                actualOneRepMax))
    }

    override fun invoke(exerciseName: String, date: String): DialogData {
        return calculate(workoutExercise = workoutService.fetchWorkoutExercise(exerciseName = exerciseName, date = date).value!!)
    }

    override fun calculate(diaryEntry: ExerciseEntry): DialogData {
        return calculate(workoutExercise = (diaryEntry as ExerciseEntryImpl).workoutExercise)
    }
}

class MockCalculatedExerciseEntryUseCase : CalculatedExerciseEntryUseCase {


    override fun calculate(diaryEntry: ExerciseEntry): DialogData {
        return DialogDataViewOnly("Saturday, Mar 12 200",
                listOf(
                        Triple("Total Sets", "16", "sets"),
                        Triple("Total Reps", "36", "reps"),
                        Triple("Total Volume", "100.0", "kg"),
                        Triple("Total Exercises", "3", "exercises"),
                )
        )
    }
    override operator fun invoke(exerciseName: String, date: String ): DialogData {
        return DialogDataViewOnly("Saturday, Mar 12 200",
            listOf(
                Triple("Total Sets", "16", "sets"),
                Triple("Total Reps", "36", "reps"),
                Triple("Total Volume", "100.0", "kg"),
                Triple("Total Exercises", "3", "exercises"),
            )
        )
    }
}

