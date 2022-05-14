package com.example.verifit.workoutservice

import android.content.res.Resources
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.example.verifit.*
import com.example.verifit.common.isNull
import com.example.verifit.singleton.DateSelectStore
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

abstract class WorkoutServiceImpl(val dateSelectStore: DateSelectStore, val knownExerciseService: KnownExerciseService) :
    WorkoutService {

    lateinit var data : MutableLiveData<List<WorkoutSet>>
    private var VolumePRs = HashMap<String, Double>()
    private var ActualOneRepMaxPRs = HashMap<String, Double>()
    private var EstimatedOneRMPRs = HashMap<String, Double>()
    private var MaxRepsPRs = HashMap<String, Double>()
    private var MaxWeightPRs = HashMap<String, Double>()
    private var LastTimeVolume = HashMap<String, Double>() // Holds last workout's volume for each exercise
    val workoutDays : ArrayList<WorkoutDay> by lazy { initFetch() }

    init {
        //calculatePersonalRecords(knownExerciseService.knownExercises)
    }
    override fun addSet(position: Int, workoutSet: WorkoutSet) {
        workoutDays[position].addSet(workoutSet)
        //fetchWorkoutExercise(workoutSet.exercise, workoutSet.date)
        saveToSharedPreferences()
        calculatePersonalRecords(knownExerciseService.knownExercises, workoutDays = workoutDays)
        if(channel.containsKey(workoutSet.exercise+workoutSet.date)){
            //val list = ArrayList(fetchSetsFromDate(workoutSet.exercise,workoutSet.date))
            val d = channel[workoutSet.exercise+workoutSet.date] as MutableLiveData<WorkoutExercise>
            d?.postValue(fetchExerciseFromDate(workoutSet.exercise,workoutSet.date))
            if(channel.containsKey(workoutSet.date)){
                (fetchDayLive(workoutSet.date) as MutableLiveData<WorkoutDay>).postValue(workoutDays[position])
            }
        } else {
            channel[workoutSet.exercise+workoutSet.date] = MutableLiveData(fetchExerciseFromDate(workoutSet.exercise,workoutSet.date))
            if(channel.containsKey(workoutSet.date)){
                (fetchDayLive(workoutSet.date) as MutableLiveData<WorkoutDay>).postValue(workoutDays[position])
            }
        }
        Log.d("Crud.Add.Set","workoutDays size = ${workoutDays.size}")
        //data.postValue(ArrayList(fetchSetsFromDate(workoutSet.exercise,dateSelectStore.date_selected)))
    }

    override fun addWorkoutDay(workoutDay: WorkoutDay, exerciseName: String?) {
        if (workoutDay.isNull){
            throw Exception("never can add a null date")
        }
        workoutDays.add(workoutDay)
        Log.d("Crud.Add","workoutDays size = ${workoutDays.size}")
        saveToSharedPreferences()
        //data.postValue(ArrayList(fetchSetsFromDate(exerciseName,dateSelectStore.date_selected)))
        calculatePersonalRecords(knownExerciseService.knownExercises, workoutDays = workoutDays)
        if(channel.containsKey(exerciseName+workoutDay.date)){
            //val list = ArrayList(fetchSetsFromDate(workoutSet.exercise,workoutSet.date))
            val d = channel[exerciseName+workoutDay.date] as MutableLiveData<WorkoutExercise>
            d?.postValue(fetchExerciseFromDate(exerciseName,workoutDay.date))
            if(channel.containsKey(workoutDay.date)){
                (fetchDayLive(workoutDay.date) as MutableLiveData<WorkoutDay>).postValue(fetchDay(workoutDay.date))
            }
        } else {

            channel[exerciseName+workoutDay.date] = MutableLiveData(fetchExerciseFromDate(exerciseName,workoutDay.date))
            if(channel.containsKey(workoutDay.date)){

                (channel[workoutDay.date] as MutableLiveData<WorkoutDay>).postValue(workoutDay)
            }
        }
        if(channel.containsKey("all_days")){
            calculatePersonalRecords(knownExerciseService.knownExercises, workoutDays = workoutDays)
            (channel["all_days"] as MutableLiveData<List<WorkoutDay>>).postValue(fetchWorkoutDays())
        }


    }

    override fun removeSet(workoutSet: WorkoutSet) {
        Log.d("Crud.Remove.Set","starting...")
        var totallyRemoved = false
        for (i in workoutDays.indices) {
            if (workoutDays[i].sets.contains(workoutSet)) {
                // If last set the delete the whole object
                if (workoutDays[i].sets.size == 1) {
                    totallyRemoved = true
                    workoutDays.remove(workoutDays[i])
                    Log.d("Crud.Remove.Set and Day","workoutDays size = ${workoutDays.size}")
                    calculatePersonalRecords(knownExerciseService.knownExercises, workoutDays = workoutDays)
                    if(channel.containsKey("all_days"))
                    (channel["all_days"] as MutableLiveData<List<WorkoutDay>>).postValue(fetchWorkoutDays())
                    break
                } else {
                    workoutDays[i].removeSet(workoutSet)
                    Log.d("Crud.Remove.Set Only","workoutDays size = ${workoutDays.size}")
                    break
                }
            }
        }
        saveToSharedPreferences()
        if(channel.containsKey(workoutSet.exercise+workoutSet.date)){
            //val list = ArrayList(fetchSetsFromDate(workoutSet.exercise,workoutSet.date))
            val d = channel[workoutSet.exercise+workoutSet.date] as MutableLiveData<WorkoutExercise>

            if(!totallyRemoved){
                try {
                    d?.postValue(fetchExerciseFromDate(workoutSet.exercise,workoutSet.date))
                } catch (e: Exception){
                    d?.postValue(WorkoutExercise.Null())
                    if(channel.containsKey(workoutSet.date)){
                        (channel[workoutSet.date] as MutableLiveData<WorkoutDay>).postValue(fetchDay(workoutSet.date))
                    }
                }

            } else {
                d?.postValue(WorkoutExercise.Null())
                if(channel.containsKey(workoutSet.date)){
                    (channel[workoutSet.date] as MutableLiveData<WorkoutDay>).postValue(WorkoutDay.Null())
                }
            }

        }
    }

    override fun fetchSet(identifier: String): WorkoutSet {
        for (i in workoutDays.indices) {
           for (j in workoutDays[i].sets.indices){
               if(workoutDays[i].sets[j].hashCode().toString() == identifier){
                   return workoutDays[i].sets[j]
               }
           }
        }
        throw Exception("set not found with $identifier")
    }



    private fun fetchSetsFromDate(exerciseName: String?, dateString: String): ArrayList<WorkoutSet> {
        if(dateString.isEmpty()){
            //throw IllegalArgumentException()
        }
        val Todays_Exercise_Sets = ArrayList<WorkoutSet>()
        // Find Sets for a specific date and exercise
        for (i in workoutDays.indices) {
            // If date matches
            if (workoutDays[i].date == dateString) {
                for (j in workoutDays[i].sets.indices) {
                    // If exercise matches
                    if (exerciseName == workoutDays[i].sets[j].exercise) {
                        Todays_Exercise_Sets.add(workoutDays[i].sets[j])
                    }
                }
            }
        }
        calculatePersonalRecords(knownExerciseService.knownExercises, workoutDays = workoutDays)
        return Todays_Exercise_Sets
    }

    private fun fetchExerciseFromDate(exerciseName: String?, dateString: String): WorkoutExercise {
        if(dateString.isEmpty()){
            //throw IllegalArgumentException()
        }
        val Todays_Exercise_Sets = ArrayList<WorkoutSet>()
        // Find Sets for a specific date and exercise
        for (i in workoutDays.indices) {
            // If date matches
            if (workoutDays[i].date == dateString) {
                for (j in workoutDays[i].exercises.indices) {
                    // If exercise matches
                    if (exerciseName == workoutDays[i].exercises[j].exercise) {
                        calculatePersonalRecords(knownExerciseService.knownExercises, workoutDays = workoutDays)
                        return (workoutDays[i].exercises[j])
                    }
                }
            }
        }
        throw java.lang.Exception("exerciseName = $exerciseName,date = $dateString")
        //calculatePersonalRecords(knownExerciseService.knownExercises, workoutDays = workoutDays)
        //return Todays_Exercise_Sets
    }



    fun calculatePersonalRecords( KnownExercises : List<Exercise> , workoutDays : MutableList<WorkoutDay>) {
        //val KnownExercises = knownExerciseService.knownExercises
        VolumePRs.clear()
        ActualOneRepMaxPRs.clear()
        EstimatedOneRMPRs.clear()
        MaxRepsPRs.clear()
        MaxWeightPRs.clear()
        LastTimeVolume.clear()

        // Initialize Volume Record Hashmap
        for (i in KnownExercises.indices) {
            VolumePRs[KnownExercises[i].name] = 0.0
            ActualOneRepMaxPRs[KnownExercises[i].name] = 0.0
            EstimatedOneRMPRs[KnownExercises[i].name] = 0.0
            MaxRepsPRs[KnownExercises[i].name] = 0.0
            MaxWeightPRs[KnownExercises[i].name] = 0.0
            LastTimeVolume[KnownExercises[i].name] = 0.0
        }

        workoutDays.forEach {
            it.exercises.forEach{ exer ->
                exer.isVolumePR = false
                exer.isActualOneRepMaxPR = false
                exer.isEstimatedOneRepMaxPR = false
                exer.isMaxRepsPR = false
                exer.isMaxWeightPR = false
                exer.isHTLT = false
            }
        }

        // Calculate Volume PRs
        for (i in KnownExercises.indices) {
            for (j in workoutDays.indices) {
                for (k in workoutDays[j].exercises.indices) {


                    if (workoutDays[j].exercises[k].exercise == KnownExercises[i].name) {
                        // Volume Personal Records
                        if (VolumePRs[KnownExercises[i].name]!! < workoutDays[j].exercises[k].volume) {
                            workoutDays[j].exercises[k].isVolumePR = true
                            VolumePRs[KnownExercises[i].name] = workoutDays[j].exercises[k].volume
                        }

                        // Actual One Repetition Maximum
                        if (ActualOneRepMaxPRs[KnownExercises[i].name]!! < workoutDays[j].exercises[k].actualOneRepMax) {
                            workoutDays[j].exercises[k].isActualOneRepMaxPR = true
                            ActualOneRepMaxPRs[KnownExercises[i].name] = workoutDays[j].exercises[k].actualOneRepMax
                        }

                        // Estimated One Repetition Maximum
                        if (EstimatedOneRMPRs[KnownExercises[i].name]!! < workoutDays[j].exercises[k].estimatedOneRepMax) {
                            workoutDays[j].exercises[k].isEstimatedOneRepMaxPR = true
                            EstimatedOneRMPRs[KnownExercises[i].name] = workoutDays[j].exercises[k].estimatedOneRepMax
                        }

                        // Max Repetitions Personal Records
                        if (MaxRepsPRs[KnownExercises[i].name]!! < workoutDays[j].exercises[k].maxReps) {
                            workoutDays[j].exercises[k].isMaxRepsPR = true
                            MaxRepsPRs[KnownExercises[i].name] = workoutDays[j].exercises[k].maxReps
                        }

                        // Max Weight Personal Records
                        if (MaxWeightPRs[KnownExercises[i].name]!! < workoutDays[j].exercises[k].maxWeight) {
                            workoutDays[j].exercises[k].isMaxWeightPR = true
                            MaxWeightPRs[KnownExercises[i].name] = workoutDays[j].exercises[k].maxWeight
                        }

                        // Harder Than Last Time!
                        if (LastTimeVolume[KnownExercises[i].name]!! < workoutDays[j].exercises[k].volume) {
                            workoutDays[j].exercises[k].isHTLT = true
                            LastTimeVolume[KnownExercises[i].name] = workoutDays[j].exercises[k].volume
                        } else {
                            LastTimeVolume[KnownExercises[i].name] = workoutDays[j].exercises[k].volume
                        }
                    }
                }
            }
        }
    }

    val channel = HashMap<String,MutableLiveData<*>>()

    override fun fetchWorkSets(exerciseName: String?, date : String): LiveData<List<WorkoutSet>> {

        val Todays_Exercise_Sets = fetchSetsFromDate(exerciseName = exerciseName,date)
        if(!channel.containsKey(exerciseName+date)){
            channel[exerciseName+date] = MutableLiveData(Todays_Exercise_Sets)
        }
        data = (channel[exerciseName+date] as LiveData<WorkoutExercise>).map { it.sets } as MutableLiveData<List<WorkoutSet>>
        return (channel[exerciseName+date] as LiveData<WorkoutExercise>).map { it.sets } as MutableLiveData<List<WorkoutSet>>
    }


    override fun fetchWorkoutExercise(exerciseName: String?, date : String): LiveData<WorkoutExercise> {

//        TODO("remove the fetch sets and switch to fetch workout specifically")
        //val Todays_Exercise_Sets = fetchSetsFromDate(exerciseName = exerciseName,date)
        val exercise = fetchExerciseFromDate(exerciseName = exerciseName,date)
        calculatePersonalRecords(knownExerciseService.knownExercises, workoutDays = workoutDays)
        if(!channel.containsKey(exerciseName+date)){
            channel[exerciseName+date] = MutableLiveData(exercise)
        }
        data = (channel[exerciseName+date] as MutableLiveData<WorkoutExercise>).map { it.sets } as MutableLiveData<List<WorkoutSet>>

        return channel[exerciseName+date] as LiveData<WorkoutExercise>
    }




    override fun updateComment(
        dateSelected: String?,
        exerciseKey: String?,
        exerciseComment: String,
    ) {
        val exercise_position =
            fetchExercisePosition(dateSelected, exerciseKey)
        if (exercise_position >= 0) {
            println("We can comment, exercise exists")
        } else {
            println("We can't comment, exercise doesn't exist")
            return
        }
        //TODO Replace with  storage
        // Get the date for today
        val day_position = fetchDayPosition(dateSelected)
        // Modify the data structure to add the comment
        workoutDays[day_position].exercises[exercise_position].comment = exerciseComment
        saveToSharedPreferences()
        val d = (fetchWorkoutExercise(exerciseKey,dateSelected!!)) as MutableLiveData<WorkoutExercise>
        d?.postValue(fetchExerciseFromDate(exerciseKey,dateSelected))
    }

    override fun updateWorkoutSet(
        dateSelected: String?,
        workoutSet: WorkoutSet
    ) {
        val exercise_position =
            fetchExercisePosition(dateSelected, workoutSet.exercise)
        if (exercise_position >= 0) {
            println("We can comment, exercise exists")
        } else {
            println("We can't comment, exercise doesn't exist")
            return
        }
        //TODO Replace with  storage
        // Get the date for today
        val day_position = fetchDayPosition(dateSelected)
        // Modify the data structure to add the comment
        val index = workoutDays[day_position].exercises[exercise_position].sets.indexOfFirst { it == workoutSet }
        workoutDays[day_position].exercises[exercise_position].sets[index] = workoutSet
        saveToSharedPreferences()
        val d = (fetchWorkoutExercise(workoutSet.exercise,dateSelected!!)) as MutableLiveData<WorkoutExercise>
        d?.postValue(fetchExerciseFromDate(workoutSet.exercise,dateSelected))
    }

    override fun getExercise(exerciseName: String?): WorkoutExercise? {
        val exercise_position =
            fetchExercisePosition(dateSelectStore.date_selected, exerciseName)
        // Exists, then show the comment
        if (exercise_position >= 0) {
            println("We can comment, exercise exists")
            val day_position = fetchDayPosition(dateSelectStore.date_selected)
            return workoutDays[day_position].exercises[exercise_position]
        }
        return null
    }

    override fun getExercisesWithName(exerciseName: String): List<WorkoutExercise> {
        // Find all performed sessions of a specific exercise and add them to local data structure
        val All_Performed_Sessions = ArrayList<WorkoutExercise>()
        for (i in workoutDays.indices.reversed()) {
            for (j in workoutDays[i].exercises.indices) {
                if (workoutDays[i].exercises[j].exercise == exerciseName) {
                    All_Performed_Sessions.add(workoutDays[i].exercises[j])
                }
            }
        }
        return All_Performed_Sessions
    }

    override fun calculateMaxWeight(exerciseName: String?): Pair<String, String> {
        var max_weight = 0.0
        var max_reps = 0
        var max_exercise_volume = 0.0

        // Find Max Weight and Reps for a specific exercise
        val workoutDays =  fetchWorkoutDays()
        for (i in workoutDays.indices) {
            for (j in workoutDays[i].sets.indices) {
                if (workoutDays[i].sets[j].volume > max_exercise_volume && workoutDays[i].sets[j].exercise == exerciseName) {
                    max_exercise_volume = workoutDays[i].sets[j].volume
                    max_reps = Math.round(workoutDays[i].sets[j].reps)
                        .toInt()
                    max_weight = workoutDays[i].sets[j].weight
                }
            }
        }

        // If never performed the exercise leave Edit Texts blank
        return if (max_reps == 0 || max_weight == 0.0) {
            Pair("", "")
        } else {
            Pair(max_reps.toString(), max_weight.toString())
        }
    }

    override fun fetchWorkoutDays(): List<WorkoutDay> {
       return workoutDays
    }

    override fun fetchWorkoutDaysLive(): LiveData<List<WorkoutDay>>{
        if(!channel.contains("all_days")){
            calculatePersonalRecords(knownExerciseService.knownExercises, workoutDays = workoutDays)
            channel["all_days"] = MutableLiveData(fetchWorkoutDays())
        }
       return channel["all_days"] as LiveData<List<WorkoutDay>>
    }



    override fun fetchDay(date: String): WorkoutDay {
        return workoutDays.find { it.date == date } ?: throw Exception("can't find day: $date")
    }

    override fun fetchDayLive(date: String): LiveData<WorkoutDay> {
        val day = try{
           fetchDay(date = date)
        } catch(e : Exception)
            {
                WorkoutDay.Null()
            }
            if(!channel.containsKey(date)){
                channel[date] = MutableLiveData(day)
            }
            return channel[date] as LiveData<WorkoutDay>



        //return workoutDays.find { it.date == date } ?: throw Exception("can't find day: $date")
    }

    override fun clearWorkoutData() {
        workoutDays.clear()
        saveWorkoutData()
    }


    //abstract override fun saveWorkoutData()

    override fun saveWorkoutData() {
        saveToSharedPreferences()
//        TODO("Not yet implemented")
//        val date_clicked = Date()
//        val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd")

    }


    override fun saveData(mutableListOf: List<WorkoutDay>) {
        workoutDays.clear()
        workoutDays.addAll(mutableListOf)
        saveWorkoutData()
        (channel["all_days"] as MutableLiveData<List<WorkoutDay>>).postValue(fetchWorkoutDays())
    }

    fun initFetch() : ArrayList<WorkoutDay> {
        val data=  initialFetchWorkoutDaysFromPreferences()
        calculatePersonalRecords(knownExerciseService.knownExercises, workoutDays = data)
        return data
    }

    abstract fun initialFetchWorkoutDaysFromPreferences(): ArrayList<WorkoutDay>

    override fun saveToSharedPreferences(){
        // Sort Before Saving
        sortWorkoutDaysDate()

        // Actually Save Changes in shared preferences
    }

    override fun fetchDayPosition(dateSelected: String?): Int {
        for (i in workoutDays.indices) {
            if (workoutDays[i].date == dateSelected) {
                return i
            }
        }
        return -1
    }

    private fun sortWorkoutDaysDate(){
        Collections.sort(workoutDays, object : Comparator<WorkoutDay?> {
            override fun compare(workoutDay: WorkoutDay?, t1: WorkoutDay?): Int {
                val date1 = workoutDay?.date
                val date2 = t1?.date
                var date_object1 = Date()
                var date_object2: Date? = Date()
                try {
                    date_object1 = SimpleDateFormat("yyyy-MM-dd").parse(date1)
                    date_object2 = SimpleDateFormat("yyyy-MM-dd").parse(date2)
                } catch (e: Exception) {
                    println(e.message)
                }
                return date_object1.compareTo(date_object2)
            }
        })
    }


    fun fetchExercisePosition(Date: String?, exerciseName: String?): Int {
        val day_position: Int = fetchDayPosition(Date)

        // The day doesn't even have an exercise
        if (day_position == -1) {
            return -1
        }
        val exercises = workoutDays[day_position].exercises
        for (i in exercises.indices) {
            if (exercises[i].exercise == exerciseName) {
                return i
            }
        }
        return -1
    }



}
