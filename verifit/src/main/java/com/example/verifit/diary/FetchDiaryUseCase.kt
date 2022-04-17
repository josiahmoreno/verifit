package com.example.verifit.diary

import android.graphics.Color
import androidx.compose.ui.ExperimentalComposeUiApi
import com.example.verifit.KnownExerciseService
import com.example.verifit.WorkoutDay
import com.example.verifit.WorkoutExercise
import com.example.verifit.workoutservice.WorkoutService
import java.text.ParseException
import java.text.SimpleDateFormat

interface FetchDiaryUseCase {
    operator fun invoke() = fetchDiary()

    @OptIn(ExperimentalComposeUiApi::class)
     fun fetchDiary() : List<DiaryEntry>
}

class FetchDiaryUseCaseImpl(val exerciseService: WorkoutService, val knownExerciseService: KnownExerciseService): FetchDiaryUseCase {
    @OptIn(ExperimentalComposeUiApi::class)
    override fun fetchDiary() : List<DiaryEntry> {
        return exerciseService.fetchWorkoutDays().map {
            convert(it)
        }.toList()
    }

    private fun convert(workoutDay: WorkoutDay): DiaryEntryImpl2{
        // Set view's data


        // Set view's data
        val parser = SimpleDateFormat("yyyy-MM-dd")

        // Possible Error

        // Possible Error
        try {
            val date = parser.parse(workoutDay.date)
            val formatter1 = SimpleDateFormat("EEEE")
            val formatter2 = SimpleDateFormat("MMMM dd YYYY")

            // Change TextView texts
            val dayString = formatter1.format(date)
            val dateString = formatter2.format(date)


            // Change RecyclerView items
            val entries = workoutDay.exercises.map {
                val pair :  Pair<List<String>,Boolean> = initializePersonalRecordIcon( it)
                val records = pair.first
                // When having multiple PRs
                var showFire = false
                val showPrOnly = pair.second
                if (records.size > 1) {
                   showFire = true
                }
                 ExerciseEntryImpl(exerciseName = it.exercise,
                         amountOfSets = "${it.totalSets} set",
                         color = getCategoryIconTint(it),
                         showFire = showFire,
                         showPrOnly = showPrOnly,
                         showComment = !it.comment.isNullOrEmpty(),
                         records = records,
                         workoutExercise = it
                 )
            }.toList()
            return DiaryEntryImpl2(dayString = dayString,
                    dateString = dateString,
                    exerciseEntries = entries,
                    workoutDay = workoutDay)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        throw Exception()
    }

    private fun getCategoryIconTint(workoutExercise: WorkoutExercise): Int{
        val exercise_category: String = knownExerciseService.fetchExerciseCategory(workoutExercise.exercise)

        if (exercise_category == "Shoulders") {
            return Color.argb(255, 0, 116, 189) // Primary Color
        } else if (exercise_category == "Back") {
            return Color.argb(255, 40, 176, 192)
        } else if (exercise_category == "Chest") {
            return Color.argb(255, 92, 88, 157)
        } else if (exercise_category == "Biceps") {
            return Color.argb(255, 255, 50, 50)
        } else if (exercise_category == "Triceps") {
            return Color.argb(255, 204, 154, 0)
        } else if (exercise_category == "Legs") {
            return Color.argb(255, 212, 25, 97)
        } else if (exercise_category == "Abs") {
            return Color.argb(255, 255, 153, 171)
        } else {
            return Color.argb(255, 52, 58, 64) // Grey AF
        }
    }

    // Sets the PR icon color accordingly
    fun initializePersonalRecordIcon( workoutExercise: WorkoutExercise): Pair<List<String>,Boolean> {
        // Count the number of PRs
        var records = 0

        // Records
        val Records =  ArrayList<String>()
        var showPrButton = false

        // Set Volume PR Icon if exercise was a Volume PR
        if (workoutExercise.isVolumePR) {
            showPrButton = true
            // holder.pr_button.setColorFilter(Color.argb(255, 	255, 153, 171));
            records++
            Records.add("Volume PR")
        }
        if (workoutExercise.isActualOneRepMaxPR) {
            showPrButton = true
            // holder.pr_button.setColorFilter(Color.argb(255,    204, 154, 0));
            records++
            Records.add("One Rep Max PR")
        }
        if (workoutExercise.isEstimatedOneRepMaxPR) {
            showPrButton = true
            // holder.pr_button.setColorFilter(Color.argb(255, 	255, 50, 50));
            records++
            Records.add("Estimated One Rep Max PR")
        }
        if (workoutExercise.isMaxRepsPR) {
            showPrButton = true
            // holder.pr_button.setColorFilter(Color.argb(255, 	92, 88, 157));
            records++
            Records.add("Maximum Repetitions PR")
        }
        if (workoutExercise.isMaxWeightPR) {
            showPrButton = true
            // holder.pr_button.setColorFilter(Color.argb(255, 40, 176, 192));
            records++
            Records.add("Maximum Weight PR")
        }
        if (workoutExercise.isHTLT) {
            showPrButton = true
            // holder.pr_button.setColorFilter(Color.argb(255, 	0, 116, 189)); // Primary Color
            records++
            Records.add("Harder Than Last Time")
        }

        return Pair(Records,showPrButton)
    }
}

class MockFetchDiaryUseCase(val data: List<DiaryEntry>): FetchDiaryUseCase {
    @OptIn(ExperimentalComposeUiApi::class)
    override fun fetchDiary() : List<DiaryEntry> = data
}


