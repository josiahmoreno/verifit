package com.example.verifit.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import com.example.verifit.*
import com.example.verifit.workoutservice.WorkoutService
import kotlinx.coroutines.CompletableDeferred
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream

class ImportDataUseCase(
    private val context: Context,
    private val launcher: ResultLauncherWrapper,
    private val toastMaker: ToastMaker,
    private val knownExerciseService: KnownExerciseService,
    private val workoutService: WorkoutService,
) {
    suspend operator fun invoke() = work()

    private suspend fun work(): Any {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "*/*"
        val response = CompletableDeferred<Boolean>()
        launcher.resultInvoke = {
            val data = it.data
            if (data != null) {
                val uri: Uri? = data.data
                readFile(uri)
                response.complete(true)
            } else {
                response.complete(false)
            }
        }
        launcher.launcher.launch(intent)


        return response.await()
    }

    fun readFile(uri: Uri?) {
        val csvList: List<*>
        try {
            println(context.getExternalFilesDir(null))
            //val textFile = File(context.getExternalFilesDir(null), filename)
            val inputStream = context.contentResolver.openInputStream(uri!!)
            val csvFile = CSVFile(inputStream)
            csvList = csvFile.read()

            // Here is where the magic happens
            val sets = CSVtoSets(csvList) // Read File and Construct Local Objects
            val workoutDays = SetsToEverything(sets) // Convert Set Objects to Day Objects
            println("csv to known...")
            val knownExercises = csvToKnownExercises(workoutDays) // Find all Exercises in CSV and add them to known exercises

            knownExerciseService.saveData(knownExercises) // Save KnownExercises in CSV
            workoutService.saveData(workoutDays) // Save WorkoutDays in Shared Preferences
        } catch (e: IOException) {
            println(e.message)
            toastMaker.makeText("Could not locate file")
            // Avoid Errors
            clearDataStructures()
        }
    }

    // Converts CSV file to Internally used Dat Structure
    fun CSVtoSets(csvList: List<*>): MutableList<WorkoutSet> {
        // Remove potential Duplicates
      val sets = mutableListOf<WorkoutSet>()

        // i = 1 since first row is only Strings
        for (i in 1 until csvList.size) {
            val row = csvList[i] as Array<String>

//            System.out.println(row.length);
            val Date = row[0]
            val Exercise = row[1]
            val Category = row[2]
            val Reps = row[3]
            val Weight = row[4]

//            System.out.println(row[5]);
            var Comment = ""
            if (row.size == 6) {
                Comment = row[5]
            }

            // String Comment = row[]
            val workoutSet =
                WorkoutSet(Date, Exercise, Category, Weight.toDouble(), Reps.toDouble(), Comment)
            sets.add(workoutSet)
        }
        return sets
    }

    // Updates All other Data Structures
    fun SetsToEverything( sets: MutableList<WorkoutSet>): MutableList<WorkoutDay> {
        // Clear Data Structures
        val days = HashSet<String>()

        val workoutDays = mutableListOf<WorkoutDay>()

        // i = 1 since first row is only Strings
        for (i in sets.indices) {
            days.add(sets[i].date)
        }
        val it: Iterator<String> = days.iterator()

        // Construct Workout_Days Array List
        while (it.hasNext()) {
            val Date = it.next()
            val temp_day = WorkoutDay()
            val temp_day_sets = java.util.ArrayList<WorkoutSet>()

            // For all Sets
            for (i in sets.indices) {
                // If Date matches add Set Object to Workout_Day Object
                if (Date == sets[i].date) {
                    temp_day.addSet(sets[i])
                }
            }
            workoutDays.add(temp_day)
        }
        return workoutDays
    }

    fun csvToKnownExercises(workoutDays: List<WorkoutDay>): MutableList<Exercise> {
        // Make new ArrayList which will hold duplicates
        val DuplicateKnownExercises = java.util.ArrayList<Exercise>()
        for (i in workoutDays.indices) {
            for (j in workoutDays[i].sets.indices) {
                val Name = workoutDays[i].sets[j].exercise
                val Bodypart = workoutDays[i].sets[j].category
                DuplicateKnownExercises.add(Exercise(Name, Bodypart))
            }
        }


        // Known Exercises is empty at this point but doesn't hurt to clear anyway
        //KnownExercises.clear()
        val knownExercises = mutableListOf<Exercise>()
        // Manual Implementation "borrowed" from stack overflow
        for (event in DuplicateKnownExercises) {
            var isFound = false
            // check if the event name exists in noRepeat
            for (e in knownExercises) {
                if (e.name == event.name || e == event) {
                    isFound = true
                    break
                }
            }
            if (!isFound) knownExercises.add(event)
        }
        return knownExercises
    }

    fun clearDataStructures() {
        // Clear everything just in case

        knownExerciseService.saveData(mutableListOf())
        workoutService.saveData(mutableListOf())
    }


    class ResultLauncherWrapper(val launcher : ActivityResultLauncher<Intent>) {
        var resultInvoke: ((ActivityResult) -> Unit)? = null
    }

}
