package com.example.verifit.diary

import java.text.ParseException
import java.text.SimpleDateFormat

interface CalculatedDiaryEntryUseCase {
    operator fun  invoke(diaryEntry: DiaryEntry) = calculate(diaryEntry)


    fun  calculate(diaryEntry: DiaryEntry) : DialogData
}

class CalculatedDiaryEntryUseCaseImpl: CalculatedDiaryEntryUseCase {



      fun calculate(diaryEntry: DiaryEntryImpl2): DialogData {
        // Crash Here


        // Crash Here

        val setSize = diaryEntry.workoutDay.sets.size.toString()
        val totalSets : Triple<String,String,String> = Triple("Total Sets",setSize,"sets")

        val totalReps : Triple<String,String,String> = Triple("Total Reps",diaryEntry.workoutDay.reps.toString(),"reps")

        val totalExercises : Triple<String,String,String> = Triple("Total Exercises",diaryEntry.workoutDay.exercises.size.toString(),"exercises")

        val totalVolume : Triple<String,String,String> = Triple("Total Volume",diaryEntry.workoutDay.dayVolume.toString(),"kg")

        val parser = SimpleDateFormat("yyyy-MM-dd")

        // Possible Error

        // Possible Error
        var date : String = ""
        try {
            val date_object = parser.parse(diaryEntry.workoutDay.getDate())
            val formatter = SimpleDateFormat("EEEE, MMM dd YYYY")

            // Format Date like a human being
            // date.setText(Workout_Days.get(position).getDate());
            date = formatter.format(date_object)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return DialogData(date,listOf(totalSets,totalReps,totalVolume,totalExercises))
    }

    override fun calculate(diaryEntry: DiaryEntry): DialogData {
        return calculate(diaryEntry = diaryEntry as DiaryEntryImpl2)
    }
}

class MockCalculatedDiaryEntryUseCase: CalculatedDiaryEntryUseCase {




    override fun calculate(diaryEntry: DiaryEntry): DialogData {
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

