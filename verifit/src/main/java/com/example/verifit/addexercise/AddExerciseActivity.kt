package com.example.verifit.addexercise

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.data.LineData
import android.view.*
import android.widget.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.*
import com.example.verifit.*
import com.example.verifit.addexercise.composables.AddExerciseViewModel
import com.example.verifit.workoutservice.PrefWorkoutServiceImpl
import com.example.verifit.addexercise.composables.CountDownTimerService
import com.example.verifit.common.*
import com.example.verifit.workoutservice.WorkoutService
import com.example.verifit.singleton.DateSelectStore
import com.github.mikephil.charting.charts.LineChart
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

class AddExerciseActivity : AppCompatActivity() {
    // Helper Data Structures
    lateinit var recyclerView: RecyclerView
    var exercise_name: String? = null
    lateinit var workoutSetAdapter2: AddExerciseWorkoutSetAdapter
    var plus_reps: ImageButton? = null
    var minus_reps: ImageButton? = null
    var plus_weight: ImageButton? = null
    var minus_weight: ImageButton? = null
    var bt_save: Button? = null
    var bt_clear: Button? = null

    // Timer Dialog Components
    var et_seconds: EditText? = null
    lateinit var minus_seconds: ImageButton
    lateinit var plus_seconds: ImageButton
    var bt_start: Button? = null
    lateinit var bt_reset: Button
    lateinit var et_reps: EditText
    lateinit var et_weight: EditText

    // Comment Items
    lateinit var et_exercise_comment: EditText


    private lateinit var workoutService : WorkoutService
    private lateinit var knownExerciseService: KnownExerciseService
    private val dateSelectStore : DateSelectStore = DateSelectStore

    @Inject
    lateinit var addExerciseViewModel: AddExerciseViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_exercise)

        // find views
        et_reps = findViewById(R.id.et_reps)
        et_weight = findViewById(R.id.et_seconds)
        plus_reps = findViewById(R.id.plus_reps)
        minus_reps = findViewById(R.id.minus_reps)
        plus_weight = findViewById(R.id.plus_weight)
        minus_weight = findViewById(R.id.minus_weight)
        bt_clear = findViewById(R.id.bt_clear)
        bt_save = findViewById(R.id.bt_save)

        // Self Explanatory I guess
        initActivity()
        knownExerciseService = PrefKnownExerciseServiceImpl(applicationContext)
        workoutService = PrefWorkoutServiceImpl(this, dateSelectStore, knownExerciseService)

        //addExerciseViewModel = hiltViewModel()
        initMVI()
        // Self Explanatory I guess
        initrecyclerView()
        //println("date_selected: " + MainActivity.date_selected)
    }

    @Suppress("DEPRECATION")
    private fun initMVI() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                addExerciseViewModel.viewState
                    .collect {
                        bt_clear!!.text = it.clearButtonText
                        et_reps.setText(it.repText)
                        et_weight.setText(it.weightText)
                        TODO("reconnect this now that workoutSets is a flow now, not a livedata")
//                        it.workoutSets.collect(owner = this@AddExerciseActivity, onChanged = { exercise ->
//                            workoutSetAdapter2.submitList(exercise.sets)
//                        })
                    }
            }
        }
    }


    // Button On Click Methods
    fun clickSave(view: View) = addExerciseViewModel.onAction(AddExerciseViewModel.UiAction.SaveExercise(
        et_weight.text.toString(),
        et_reps.text.toString(),
        exercise_name!!

    ))
    // Clear / Delete
    fun clickClear(view: View) = addExerciseViewModel.onAction(AddExerciseViewModel.UiAction.Clear)

    // Save Changes in main data structure, save data structure in shared preferences
    override fun onStop() {
        super.onStop()
        println("On Stop1")

        // Sort Before Saving
        workoutService.saveWorkoutData()
        println("On Stop3")
    }


    fun clickPlusWeight(view: View) = addExerciseViewModel.onAction(AddExerciseViewModel.UiAction.WeightIncrement)


    fun clickMinusWeight(view: View) = addExerciseViewModel.onAction(AddExerciseViewModel.UiAction.WeightDecrement)


    fun clickPlusReps(view: View) = addExerciseViewModel.onAction(AddExerciseViewModel.UiAction.RepIncrement)


    fun clickMinusReps(view: View) = addExerciseViewModel.onAction(AddExerciseViewModel.UiAction.RepDecrement)

    // Handles Intent Stuff
    fun initActivity() {
        val `in` = intent
        exercise_name = `in`.getStringExtra("exercise")
        supportActionBar!!.title = exercise_name
    }

    // Initialize Recycler View Object
    fun initrecyclerView() {

        // Clear since we don't want duplicates
        // Find Recycler View Object
        recyclerView = findViewById(R.id.recycler_view)
        workoutSetAdapter2 = AddExerciseWorkoutSetAdapter { pos ->
            addExerciseViewModel.onAction(AddExerciseViewModel.UiAction.WorkoutClick(pos))
        }
        recyclerView.adapter = workoutSetAdapter2
        recyclerView.layoutManager = LinearLayoutManager(this)
    }


    // Menu Stuff
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.add_exercise_activity_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Timer
        when (item.itemId) {
            R.id.timer -> addExerciseViewModel.onAction(AddExerciseViewModel.UiAction.ShowTimer)
            R.id.history -> addExerciseViewModel.onAction(AddExerciseViewModel.UiAction.ShowHistory)
            R.id.graph -> addExerciseViewModel.onAction(AddExerciseViewModel.UiAction.ShowGraph)
            R.id.comment -> addExerciseViewModel.onAction(AddExerciseViewModel.UiAction.ShowComments)
        }
        return super.onOptionsItemSelected(item)
    }
    
}