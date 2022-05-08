package com.example.verifit.diary

import com.example.verifit.WorkoutDay
import java.text.ParseException
import java.text.SimpleDateFormat

interface CalculatedDiaryEntryUseCase {
    operator fun  invoke(diaryEntry: DiaryEntry) = calculate(diaryEntry)
    operator fun  invoke(workoutDay: WorkoutDay) = calculate(workoutDay)

    fun calculate(diaryEntry: DiaryEntry) : DialogData
    fun calculate(workoutDay: WorkoutDay) : DialogData
}

class CalculatedDiaryEntryUseCaseImpl: CalculatedDiaryEntryUseCase {



      override fun calculate(workoutDay: WorkoutDay): DialogDataViewOnly {
        // Crash Here


        // Crash Here

        val setSize = workoutDay.sets.size.toString()
        val totalSets : Triple<String,String,String> = Triple("Total Sets",setSize,"sets")

        val totalReps : Triple<String,String,String> = Triple("Total Reps",workoutDay.reps.toString(),"reps")

        val totalExercises : Triple<String,String,String> = Triple("Total Exercises",workoutDay.exercises.size.toString(),"exercises")

        val totalVolume : Triple<String,String,String> = Triple("Total Volume",workoutDay.dayVolume.toString(),"kg")

        val parser = SimpleDateFormat("yyyy-MM-dd")

        // Possible Error

        // Possible Error
        var date : String = ""
        try {
            val date_object = parser.parse(workoutDay.getDate())
            val formatter = SimpleDateFormat("EEEE, MMM dd YYYY")

            // Format Date like a human being
            // date.setText(Workout_Days.get(position).getDate());
            date = formatter.format(date_object)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return DialogDataViewOnly(title = date,
                data = listOf(totalSets, totalReps, totalVolume, totalExercises))
    }

    override fun calculate(diaryEntry: DiaryEntry): DialogData {
        return calculate2(diaryEntry = diaryEntry as DiaryEntryImpl2)
    }

    private fun calculate2(diaryEntry: DiaryEntryImpl2): DialogData {
        val viewOnly =  calculate(workoutDay = diaryEntry.workoutDay)
        return DialogDataImpl(viewOnly.title,viewOnly.data,diaryEntry)
    }


}

class MockCalculatedDiaryEntryUseCase: CalculatedDiaryEntryUseCase {




    override fun calculate(diaryEntry: DiaryEntry): DialogData {
        return calculate(diaryEntry = diaryEntry)
    }

    override fun calculate(workoutDay: WorkoutDay): DialogData {
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

