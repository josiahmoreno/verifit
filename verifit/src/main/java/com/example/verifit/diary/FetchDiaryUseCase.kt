package com.example.verifit.diary

import android.view.View
import androidx.compose.ui.ExperimentalComposeUiApi
import com.example.verifit.DiaryExerciseAdapter
import com.example.verifit.R
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

class FetchDiaryUseCaseImpl(val exerciseService: WorkoutService): FetchDiaryUseCase {
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
                 ExerciseEntry(exerciseName = it.exercise, amountOfSets = "${it.totalSets} set", color = getCategoryIconTint(it), showFire = true)
            }.toList()


            // Get exercise name
            val exercise_name: String = Exercises.get(position).getExercise()
            holder.tv_exercise_name.setText(exercise_name)
            val sets = Math.round(Exercises.get(position).getTotalSets()).toInt()
            holder.sets.setText(sets.toString())
            setCategoryIconTint(holder, exercise_name)
            val Records: ArrayList<String> = initializePersonalRecordIcon(holder, position)

            initializeCommentButton(holder, position)
            return DiaryEntryImpl2(dayString = dayString, dateString =  dateString, exerciseEntries =  entries, workoutDay = workoutDay)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        throw Exception()
    }

    private fun getCategoryIconTint(workoutExercise: WorkoutExercise): Int{

    }

    // Sets the PR icon color accordingly
    fun initializePersonalRecordIcon(holder: DiaryExerciseAdapter.MyViewHolder, position: Int): java.util.ArrayList<String>? {
        // Count the number of PRs
        var records = 0

        // Records
        val Records = java.util.ArrayList<String>()

        // Set Volume PR Icon if exercise was a Volume PR
        if (Exercises.get(position).isVolumePR()) {
            holder.pr_button.visibility = View.VISIBLE
            // holder.pr_button.setColorFilter(Color.argb(255, 	255, 153, 171));
            records++
            Records.add("Volume PR")
        }
        if (Exercises.get(position).isActualOneRepMaxPR()) {
            holder.pr_button.visibility = View.VISIBLE
            // holder.pr_button.setColorFilter(Color.argb(255,    204, 154, 0));
            records++
            Records.add("One Rep Max PR")
        }
        if (Exercises.get(position).isEstimatedOneRepMaxPR()) {
            holder.pr_button.visibility = View.VISIBLE
            // holder.pr_button.setColorFilter(Color.argb(255, 	255, 50, 50));
            records++
            Records.add("Estimated One Rep Max PR")
        }
        if (Exercises.get(position).isMaxRepsPR()) {
            holder.pr_button.visibility = View.VISIBLE
            // holder.pr_button.setColorFilter(Color.argb(255, 	92, 88, 157));
            records++
            Records.add("Maximum Repetitions PR")
        }
        if (Exercises.get(position).isMaxWeightPR()) {
            holder.pr_button.visibility = View.VISIBLE
            // holder.pr_button.setColorFilter(Color.argb(255, 40, 176, 192));
            records++
            Records.add("Maximum Weight PR")
        }
        if (Exercises.get(position).isHTLT()) {
            holder.pr_button.visibility = View.VISIBLE
            // holder.pr_button.setColorFilter(Color.argb(255, 	0, 116, 189)); // Primary Color
            records++
            Records.add("Harder Than Last Time")
        } else {
            holder.pr_button.visibility = View.GONE
        }

        // When having multiple PRs
        if (records > 1) {
            holder.pr_button.setImageResource(R.drawable.ic_whatshot_24px)
            holder.pr_button.visibility = View.VISIBLE
            holder.pr_button.setColorFilter(android.graphics.Color.argb(255, 255, 0, 0))
        }
        return Records
    }
}

class MockFetchDiaryUseCase(val data: List<DiaryEntry>): FetchDiaryUseCase {
    @OptIn(ExperimentalComposeUiApi::class)
    override fun fetchDiary() : List<DiaryEntry> = data
}


