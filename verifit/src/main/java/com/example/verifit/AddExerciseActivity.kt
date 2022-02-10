package com.example.verifit

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.data.LineData
import android.view.*
import android.widget.*
import androidx.lifecycle.*
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

    private lateinit var mviViewModel : MviViewModel

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
        mviViewModel = MviViewModel(PrefWorkoutServiceImpl(exercise_name, this), TimerServiceImpl(this),exercise_name)
        initMVI()
        // Self Explanatory I guess
        initrecyclerView()
        println("date_selected: " + MainActivity.date_selected)
    }

    @Suppress("DEPRECATION")
    private fun initMVI() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mviViewModel.viewState
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
                mviViewModel.oneShotEvents
                    .onEach {
                        when (it) {
                            is MviViewModel.OneShotEvent.SetLogged -> {
                                //updateTodaysExercises()
                                Toast.makeText(applicationContext, it.message, Toast.LENGTH_SHORT)
                                    .show()
                            }
                            is MviViewModel.OneShotEvent.Toast -> Toast.makeText(
                                applicationContext,
                                it.toast,
                                Toast.LENGTH_SHORT
                            ).show()
                            is MviViewModel.OneShotEvent.ShowTimerDialog -> {

                                showTimer(it.seconds, it.buttonText)
                            }
                            is MviViewModel.OneShotEvent.ShowGraphDialog -> {
                                showGraphDialog(it.lineData)
                            }
                            is MviViewModel.OneShotEvent.ShowHistoryDialog -> {
                                    showHistoryGraph(it.exerciseName, it.history)
                            }
                            is MviViewModel.OneShotEvent.ShowCommentDialog -> showCommentDialog(it.exerciseComment)
                            MviViewModel.OneShotEvent.ShowDeleteDialog -> createDeleteDialog()
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
            mviViewModel.onAction(MviViewModel.UiAction.NoDelete)
            alertDialog.dismiss()
        }

        // Actually Delete set and update local data structure
        bt_yes.setOnClickListener { // Get soon to be deleted set
            mviViewModel.onAction(MviViewModel.UiAction.YesDelete)
            alertDialog.dismiss()
        }

        // Show delete confirmation dialog box
        alertDialog.show()
    }

    // Button On Click Methods
    fun clickSave() = mviViewModel.onAction(MviViewModel.UiAction.SaveExercise(
        et_weight.text.toString(),
        et_reps.text.toString(),
        exercise_name!!,
        MainActivity.getExerciseCategory(exercise_name),
        MainActivity.getDayPosition(MainActivity.date_selected)
    ))
    // Clear / Delete
    fun clickClear() = mviViewModel.onAction(MviViewModel.UiAction.Clear)

    // Save Changes in main data structure, save data structure in shared preferences
    override fun onStop() {
        super.onStop()
        println("On Stop1")

        // Sort Before Saving
        MainActivity.sortWorkoutDaysDate()
        println("On Stop2")

        // Actually Save Changes in shared preferences
        MainActivity.saveWorkoutData(applicationContext)
        println("On Stop3")
    }


    fun clickPlusWeight() = mviViewModel.onAction(MviViewModel.UiAction.WeightIncrement)


    fun clickMinusWeight() = mviViewModel.onAction(MviViewModel.UiAction.WeightDecrement)


    fun clickPlusReps() = mviViewModel.onAction(MviViewModel.UiAction.RepIncrement)


    fun clickMinusReps() = mviViewModel.onAction(MviViewModel.UiAction.RepDecrement)

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
            mviViewModel.onAction(MviViewModel.UiAction.WorkoutClick(pos))
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
            R.id.timer -> mviViewModel.onAction(MviViewModel.UiAction.ShowTimer)
            R.id.history -> mviViewModel.onAction(MviViewModel.UiAction.ShowHistory)
            R.id.graph -> mviViewModel.onAction(MviViewModel.UiAction.ShowGraph)
            R.id.comment -> mviViewModel.onAction(MviViewModel.UiAction.ShowComments)
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
        bt_reset.setOnClickListener { mviViewModel.onAction(MviViewModel.UiAction.ResetTimer) }

        // Start Timer Button
        bt_start?.setOnClickListener { mviViewModel.onAction(MviViewModel.UiAction.StartTimer(et_seconds?.text.toString())) }

        // Minus Button
        minus_seconds.setOnClickListener { mviViewModel.onAction(MviViewModel.UiAction.MinusSeconds(et_seconds?.text.toString())) }

        // Plus Button
        plus_seconds.setOnClickListener { mviViewModel.onAction(MviViewModel.UiAction.PlusSeconds(et_seconds?.text.toString())) }

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

    private fun saveComment() = mviViewModel.onAction(MviViewModel.UiAction.SaveComment(et_exercise_comment.text.toString()))

    private fun clearComment() {
        et_exercise_comment.setText("")
        mviViewModel.onAction(MviViewModel.UiAction.ClearComment)
    }
    
}