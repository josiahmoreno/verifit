package com.example.verifit.diary

import com.example.verifit.WorkoutExercise
import java.text.ParseException
import java.text.SimpleDateFormat

interface CalculatedExerciseEntryUseCase {
    operator fun invoke(diaryEntry: ExerciseEntry) = calculate(diaryEntry)


    fun calculate(diaryEntry: ExerciseEntry): DialogData
}

class CalculatedExerciseEntryUseCaseImpl : CalculatedExerciseEntryUseCase {


    fun calculate(workoutExercise: WorkoutExercise): DialogData {
        // Crash Here


        // Crash Here
        // Double -> Integer


        // Set Values

        // Double -> Integer
//          val sets = Math.round(Exercises.get(position).getTotalSets()).toInt()
//          val reps = Math.round(Exercises.get(position).getTotalReps()).toInt()
//          val max_reps = Math.round(Exercises.get(position).getMaxReps()).toInt()
//
//          totalsets.setText(sets.toString())
//          totalreps.setText(reps.toString())
//          maxreps.setText(max_reps.toString())
//
//          // Double
//
//          // Double
//          totalvolume.setText(Exercises.get(position).getVolume().toString())
//          maxweight.setText(Exercises.get(position).getMaxWeight().toString())
//          onerepmax.setText(Exercises.get(position).getEstimatedOneRepMax().toString())
//          actualonerepmax.setText(Exercises.get(position).getActualOneRepMax().toString())
//          name.setText(Exercises.get(position).getExercise())
//          maxsetvolume.setText(Exercises.get(position).getMaxSetVolume().toString())
//
//          exercise_name = Exercises.get(position).getExercise()


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


        return DialogData(workoutExercise.exercise, listOf(totalSets,
                totalReps,
                totalVolume,
                maxWeight,
                maxReps,
                maxSetVolume,
                estimatedOneRepMax,
                actualOneRepMax))
    }

    override fun calculate(diaryEntry: ExerciseEntry): DialogData {
        return calculate(workoutExercise = (diaryEntry as ExerciseEntryImpl).workoutExercise)
    }
}

class MockCalculatedExerciseEntryUseCase : CalculatedExerciseEntryUseCase {


    override fun calculate(diaryEntry: ExerciseEntry): DialogData {
        return DialogData("Saturday, Mar 12 200",
                listOf(
                        Triple("Total Sets", "16", "sets"),
                        Triple("Total Reps", "36", "reps"),
                        Triple("Total Volume", "100.0", "kg"),
                        Triple("Total Exercises", "3", "exercises"),
                )
        )
    }
}

