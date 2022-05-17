package com.example.verifit.workoutservice

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.verifit.*
import com.example.verifit.singleton.DateSelectStore
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class FakeWorkoutService: WorkoutService {
    override fun addSet(position: Int, workoutSet: WorkoutSet) {
        TODO("Not yet implemented")
    }

    override fun addWorkoutDay(workoutDay: WorkoutDay, exerciseName: String?) {
        TODO("Not yet implemented")
    }

    override fun removeSet(toBeRemovedSet: WorkoutSet) {
        TODO("Not yet implemented")
    }

    override fun fetchWorkSets(excerciseName: String?, date: String): LiveData<List<WorkoutSet>> {
        val list = listOf(WorkoutSet("", "", "", 11.0, 11.0),
            WorkoutSet("", "", "", 200.0, 8.0)
        )
        return MutableLiveData(
            list
        )
    }

    override fun fetchWorkoutExercise(
        exerciseName: String?,
        date: String,
    ): LiveData<WorkoutExercise> {
        TODO("Not yet implemented")
    }

    override fun updateComment(dateSelected: String?, exerciseKey: String?, exerciseComment: String) {
        TODO("Not yet implemented")
    }

    override fun updateWorkoutSet(dateSelected: String?, workoutSet: WorkoutSet) {
        TODO("Not yet implemented")
    }

    override fun getExercise(exerciseName: String?): WorkoutExercise? {
        return null
    }

    override fun getExercisesWithName(exerciseName: String): List<WorkoutExercise> {
        TODO("Not yet implemented")
    }

    override fun calculateMaxWeight(exerciseName: String?): Pair<String, String> {
        return Pair("1.0","1.0")
    }

    override fun fetchWorkoutDays(): List<WorkoutDay> {
        TODO("Not yet implemented")
    }

    override fun saveWorkoutData() {
        TODO("Not yet implemented")
    }

    override fun clearWorkoutData() {
        TODO("Not yet implemented")
    }

    override fun saveToSharedPreferences() {
        TODO("Not yet implemented")
    }

    override fun fetchDayPosition(dateSelected: String?): Int {
        TODO("Not yet implemented")
    }

    override fun saveData(mutableListOf: List<WorkoutDay>) {
        TODO("Not yet implemented")
    }

    override fun fetchDay(date: String): WorkoutDay {
        TODO("Not yet implemented")
    }

    override fun fetchDayLive(date: String): LiveData<WorkoutDay> {
        TODO("Not yet implemented")
    }

    override fun fetchSet(identifier: String): WorkoutSet {
        TODO("Not yet implemented")
    }

    override fun fetchWorkoutDaysLive(): LiveData<List<WorkoutDay>> {
        TODO("Not yet implemented")
    }

    override fun getRecordSet(exerciseName: String?): WorkoutSet? {
        TODO("Not yet implemented")
    }


}
class FakeKnownWorkoutService(additional: List<Exercise>) : DefaultKnownExercise(additional){
    override fun fetchKnownExercises(): List<Exercise> {
        TODO("Not yet implemented")
    }

    override fun saveKnownExerciseDataToPreferences() {
        TODO("Not yet implemented")
    }


}
class FakeWorkoutService2(dateStore: DateSelectStore, knownExerciseService: KnownExerciseService): WorkoutServiceImpl(dateStore, knownExerciseService) {

    override fun initialFetchWorkoutDaysFromPreferences(): ArrayList<WorkoutDay> {
        val date_clicked = Date()
        val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd")
        val date_str = dateFormat.format(date_clicked)
        val localDate = LocalDate.parse(date_str)
        val yesterday = localDate.minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        return ArrayList(arrayListOf(
            WorkoutDay().apply {
                date = date_str
                addSet(
                    WorkoutSet(
                        date_str,
                        "FirstExerciseName",
                        "Biceps",
                        1.0,
                        2.0
                    ))
//                addSet(
//                    WorkoutSet(
//                        date_str,
//                        "SecondExerciseName",
//                        "Glutes",
//                        2.0,
//                        3.0
//                    ))
//                addSet(
//                    WorkoutSet(
//                        date_str,
//                        "SecondExerciseName",
//                        "Glutes",
//                        3.0,
//                        4.0
//                    ))
            },
            WorkoutDay().apply {
                date = yesterday
                addSet(
                    WorkoutSet(yesterday,
                            "FirstExerciseName",
                            "Biceps",
                            2.0,
                            3.0,
                            "CommentComment"
                            ))
//                addSet(
//                    WorkoutSet(
//                        yesterday,
//                        "SecondExerciseName",
//                        "Glutes",
//                        2.0,
//                        3.0
//                    ))
//                addSet(
//                    WorkoutSet(
//                        yesterday,
//                        "SecondExerciseName",
//                        "Glutes",
//                        3.0,
//                        4.0
//                    ))
            }

        ).reversed())
    }

    override fun saveToSharedPreferences() {

    }


}