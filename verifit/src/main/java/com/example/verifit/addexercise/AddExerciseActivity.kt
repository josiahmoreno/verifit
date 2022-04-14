package com.example.verifit.addexercise

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.data.LineData
import android.view.*
import android.widget.*
import androidx.lifecycle.*
import com.example.verifit.*
import com.example.verifit.addexercise.composables.AddExerciseViewModel
import com.example.verifit.workoutservice.PrefWorkoutServiceImpl
import com.example.verifit.addexercise.composables.TimerServiceImpl
import com.example.verifit.workoutservice.WorkoutService
import com.example.verifit.singleton.DateSelectStore
import com.github.mikephil.charting.charts.LineChart
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

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

    private lateinit var addExerciseViewModel : AddExerciseViewModel
    private lateinit var workoutService : WorkoutService
    private lateinit var knownExerciseService: KnownExerciseService
    private val dateSelectStore : DateSelectStore = DateSelectStore
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
        addExerciseViewModel = AddExerciseViewModel(workoutService, TimerServiceImpl(this),exercise_name)
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
                        it.workoutSets.observe(owner = this@AddExerciseActivity, onChanged = workoutSetAdapter2::submitList)
                        et_seconds?.setText(it.secondsLeftString)
                        bt_start?.text = it.timerButtonText
                    }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                addExerciseViewModel.oneShotEvents
                    .onEach {
                        when (it) {
                            is AddExerciseViewModel.OneShotEvent.Toast -> Toast.makeText(
                                applicationContext,
                                it.toast,
                                Toast.LENGTH_SHORT
                            ).show()
                            is AddExerciseViewModel.OneShotEvent.ShowTimerDialog -> {

                                showTimer(it.seconds, it.buttonText)
                            }
                            is AddExerciseViewModel.OneShotEvent.ShowGraphDialog -> {
                                showGraphDialog(it.lineData)
                            }
                            is AddExerciseViewModel.OneShotEvent.ShowHistoryDialog -> {
                                    showHistoryGraph(it.exerciseName, it.history)
                            }
                            is AddExerciseViewModel.OneShotEvent.ShowCommentDialog -> showCommentDialog(it.exerciseComment)
                            AddExerciseViewModel.OneShotEvent.ShowDeleteDialog -> createDeleteDialog()
                            is AddExerciseViewModel.OneShotEvent.ShowStatsDialog -> TODO()
                        }
                    }
                    .collect()

            }
        }
    }

    private fun createDeleteDialog() {
        val inflater = LayoutInflater.from(this)
        val view1 = inflater.inflate(R.layout.delete_set_dialog, null)
        val alertDialog = AlertDialog.Builder(this).setView(view1).create()
        val bt_yes = view1.findViewById<Button>(R.id.bt_yes3)
        val bt_no = view1.findViewById<Button>(R.id.bt_no3)

        // Dismiss dialog box
        bt_no.setOnClickListener {
            addExerciseViewModel.onAction(AddExerciseViewModel.UiAction.NoDelete)
            alertDialog.dismiss()
        }

        // Actually Delete set and update local data structure
        bt_yes.setOnClickListener { // Get soon to be deleted set
            addExerciseViewModel.onAction(AddExerciseViewModel.UiAction.YesDelete)
            alertDialog.dismiss()
        }

        // Show delete confirmation dialog box
        alertDialog.show()
    }

    // Button On Click Methods
    fun clickSave(view: View) = addExerciseViewModel.onAction(AddExerciseViewModel.UiAction.SaveExercise(
        et_weight.text.toString(),
        et_reps.text.toString(),
        exercise_name!!,
        knownExerciseService.fetchExerciseCategory(exercise_name),
        workoutService.fetchDayPosition(DateSelectStore.date_selected)
    ))
    // Clear / Delete
    fun clickClear(view: View) = addExerciseViewModel.onAction(AddExerciseViewModel.UiAction.Clear)

    // Save Changes in main data structure, save data structure in shared preferences
    override fun onStop() {
        super.onStop()
        println("On Stop1")

        // Sort Before Saving
        workoutService.saveToSharedPreferences()
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

    private fun showTimer(seconds : String, buttonText: String){
        // Prepare to show timer dialog box
        val inflater = LayoutInflater.from(this@AddExerciseActivity)
        val view = inflater.inflate(R.layout.timer_dialog, null)
        val alertDialog = AlertDialog.Builder(this@AddExerciseActivity).setView(view).create()

        // Get Objects (use view because dialog box from menu)
        et_seconds = view.findViewById(R.id.et_seconds)
        et_seconds?.setText(seconds)
        minus_seconds = view.findViewById(R.id.minus_seconds)
        plus_seconds = view.findViewById(R.id.plus_seconds)
        bt_start = view.findViewById(R.id.bt_start)
        bt_start?.text = buttonText
        bt_reset = view.findViewById(R.id.bt_close)

        // Reset Timer Button
        bt_reset.setOnClickListener { addExerciseViewModel.onAction(AddExerciseViewModel.UiAction.ResetTimer) }

        // Start Timer Button
        bt_start?.setOnClickListener { addExerciseViewModel.onAction(AddExerciseViewModel.UiAction.StartTimer(
            et_seconds?.text.toString())) }

        // Minus Button
        minus_seconds.setOnClickListener { addExerciseViewModel.onAction(AddExerciseViewModel.UiAction.MinusSeconds(
            et_seconds?.text.toString())) }

        // Plus Button
        plus_seconds.setOnClickListener { addExerciseViewModel.onAction(AddExerciseViewModel.UiAction.PlusSeconds(
            et_seconds?.text.toString())) }

        // Show Timer Dialog Box
        alertDialog.show()
    }

    private fun showHistoryGraph( exerciseName : String,All_Performed_Sessions :List<WorkoutExercise> ){
        // Prepare to show exercise history dialog box
        val inflater = LayoutInflater.from(this@AddExerciseActivity)
        val view = inflater.inflate(R.layout.exercise_history_dialog, null)
        val alertDialog = AlertDialog.Builder(this@AddExerciseActivity).setView(view).create()

        // Set Exercise Name
        val tv_exercise_name = view.findViewById<TextView>(R.id.tv_exercise_name)
        tv_exercise_name.text = exerciseName


        // Set Exercise History Recycler View
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView_Exercise_History)
        val workoutExerciseAdapter4 =
            ExerciseHistoryExerciseAdapter(this@AddExerciseActivity, All_Performed_Sessions)


        // Crash Here
        recyclerView.adapter = workoutExerciseAdapter4
        recyclerView.layoutManager = LinearLayoutManager(this@AddExerciseActivity)
        alertDialog.show()
    }

    private fun showGraphDialog(data: LineData){
        // Prepare to show exercise history dialog box
        val inflater = LayoutInflater.from(this@AddExerciseActivity)
        val view = inflater.inflate(R.layout.exercise_graph_dialog, null)
        val alertDialog = AlertDialog.Builder(this@AddExerciseActivity).setView(view).create()
        // Get Chart Object
        val lineChart = view.findViewById<View>(R.id.lineChart) as LineChart
        lineChart.data = data
        lineChart.description.isEnabled = false
        // Show Chart Dialog box
        alertDialog.show()
    }

    private fun  showCommentDialog(comment: String){
        // Prepare to show exercise history dialog box
        val inflater = LayoutInflater.from(this@AddExerciseActivity)
        val view = inflater.inflate(R.layout.add_exercise_comment_dialog, null)
        val alertDialog = AlertDialog.Builder(this@AddExerciseActivity).setView(view).create()
        val bt_save_comment : Button = view.findViewById(R.id.bt_save_comment)
        val bt_clear_comment : Button = view.findViewById(R.id.bt_clear_comment)
        et_exercise_comment = view.findViewById(R.id.et_exercise_comment)
        et_exercise_comment.setText(comment)
        bt_clear_comment.setOnClickListener { clearComment() }
        bt_save_comment.setOnClickListener { saveComment() }
        // Show Comment Dialog box
        alertDialog.show()
    }

    private fun saveComment() = addExerciseViewModel.onAction(AddExerciseViewModel.UiAction.SaveComment(
        et_exercise_comment.text.toString()))

    private fun clearComment() {
        et_exercise_comment.setText("")
        addExerciseViewModel.onAction(AddExerciseViewModel.UiAction.ClearComment)
    }
    
}