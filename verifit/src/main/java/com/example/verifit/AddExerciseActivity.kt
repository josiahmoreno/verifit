package com.example.verifit

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import android.os.CountDownTimer
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.LineData
import android.graphics.Color
import android.view.*
import android.widget.*
import androidx.lifecycle.*
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.ArrayList

class AddExerciseActivity : AppCompatActivity(), WorkoutService {
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

    // For Alarm
    var START_TIME_IN_MILLIS: Long = 180000
    var countDownTimer: CountDownTimer? = null
    var TimerRunning = false
    var TimeLeftInMillis = START_TIME_IN_MILLIS

    // Timer Dialog Components
    lateinit var et_seconds: EditText
    lateinit var minus_seconds: ImageButton
    lateinit var plus_seconds: ImageButton
    lateinit var bt_start: Button
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
        mviViewModel = MviViewModel(this, TimerServiceImpl(),exercise_name)
        initMVI()
        // Self Explanatory I guess
        initrecyclerView()
        println("date_selected: " + MainActivity.date_selected)
    }

    private fun initMVI() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mviViewModel.viewState
                    .collect {
                        bt_clear!!.text = it.clearButtonText
                        et_reps!!.setText(it.repText)
                        et_weight!!.setText(it.weightText)
                        it.workoutSets.observe(this@AddExerciseActivity, { list ->
                            workoutSetAdapter2.submitList(list)
                        })
                        if (it.showDeleteDialog) {
                            createDeleteDialog()
                        }
                        if (it.showingCommentDialog) {
                        }
                        if (it.showingGraphDialog) {
                            showGraphDialog(it.lineData!!)
                        }
                        if(it.showingHistoryDialog){
                            showHistoryGraph(exercise_name ?: "", it.history)
                        }
                            showCommentDialog( it.commentText)
                    }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mviViewModel.oneShotEvents
                    .onEach {
                        when (it) {
                            is MviViewModel.OneShotEvent.ErrorEmptyWeightAndReps -> Toast.makeText(
                                applicationContext,
                                it.message,
                                Toast.LENGTH_SHORT
                            ).show()
                            is MviViewModel.OneShotEvent.ErrorInvalidWeightAndReps -> Toast.makeText(
                                applicationContext,
                                it.message,
                                Toast.LENGTH_SHORT
                            ).show()
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
    fun clickSave(view: View?) {
        mviViewModel.onAction(MviViewModel.UiAction.SaveExercise(
            et_weight.text.toString(),
            et_reps.text.toString(),
            exercise_name!!,
            MainActivity.getExerciseCategory(exercise_name),
            MainActivity.getDayPosition(MainActivity.date_selected)
        ))
    }
    // Clear / Delete
    fun clickClear(view: View?) = mviViewModel.onAction(MviViewModel.UiAction.Clear)

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

    // Do I even need to explain this?
    fun clickPlusWeight(view: View?) = mviViewModel.onAction(MviViewModel.UiAction.WeightIncrement)

    // Do I even need to explain this?
    fun clickMinusWeight(view: View?) = mviViewModel.onAction(MviViewModel.UiAction.WeightDecrement)

    // Do I even need to explain this?
    fun clickPlusReps(view: View?) = mviViewModel.onAction(MviViewModel.UiAction.RepIncrement)

    // Do I even need to explain this?
    fun clickMinusReps(view: View?) = mviViewModel.onAction(MviViewModel.UiAction.RepDecrement)

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
        if (item.itemId == R.id.timer) {
            mviViewModel.onAction(MviViewModel.UiAction.ShowTimer)
        } else if (item.itemId == R.id.history) {
            mviViewModel.onAction(MviViewModel.UiAction.ShowHistory)
        } else if (item.itemId == R.id.graph) {
            mviViewModel.onAction(MviViewModel.UiAction.ShowGraph)
        } else if (item.itemId == R.id.comment) {
            mviViewModel.onAction(MviViewModel.UiAction.ShowComments)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showTimer(){
        // Prepare to show timer dialog box
        val inflater = LayoutInflater.from(this@AddExerciseActivity)
        val view = inflater.inflate(R.layout.timer_dialog, null)
        val alertDialog = AlertDialog.Builder(this@AddExerciseActivity).setView(view).create()

        // Get Objects (use view because dialog box from menu)
        et_seconds = view.findViewById(R.id.et_seconds)
        minus_seconds = view.findViewById(R.id.minus_seconds)
        plus_seconds = view.findViewById(R.id.plus_seconds)
        bt_start = view.findViewById(R.id.bt_start)
        bt_reset = view.findViewById(R.id.bt_close)

        // Set default seconds value to 180 i.e 3 minutes
        if (!TimerRunning) {
            // Derive String value from chosen start time
            // et_seconds.setText(String.valueOf((int) START_TIME_IN_MILLIS /1000));
            loadSeconds()
        } else {
            updateCountDownText()
        }

        // Reset Timer Button
        bt_reset.setOnClickListener(View.OnClickListener { resetTimer() })

        // Start Timer Button
        bt_start.setOnClickListener(View.OnClickListener {
            if (TimerRunning) {
                pauseTimer()
            } else {
                saveSeconds()
                startTimer()
            }
        })

        // Minus Button
        minus_seconds.setOnClickListener(View.OnClickListener {
            if (!et_seconds.getText().toString().isEmpty()) {
                var seconds = et_seconds.getText().toString().toDouble()
                seconds = seconds - 1
                if (seconds < 0) {
                    seconds = 0.0
                }
                val seconds_int = seconds.toInt()
                et_seconds.setText(seconds_int.toString())
            }
        })

        // Plus Button
        plus_seconds.setOnClickListener(View.OnClickListener {
            if (!et_seconds.getText().toString().isEmpty()) {
                var seconds = et_seconds.getText().toString().toDouble()
                seconds = seconds + 1
                if (seconds < 0) {
                    seconds = 0.0
                }
                val seconds_int = seconds.toInt()
                et_seconds.setText(seconds_int.toString())
            }
        })

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
        alertDialog.setOnDismissListener { mviViewModel.onAction(MviViewModel.UiAction.HistoryDismissed) }
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
        val bt_clear_comment : Button = view.findViewById(R.id.bt_clear_comment)
        et_exercise_comment = view.findViewById(R.id.et_exercise_comment)
        et_exercise_comment.setText(comment)
        bt_clear_comment.setOnClickListener { clearComment() }
        bt_save_comment.setOnClickListener { saveComment() }
        alertDialog.setOnDismissListener {
            mviViewModel.onAction(MviViewModel.UiAction.DialogDismissed)
        }
        // Show Comment Dialog box
        alertDialog.show()
    }

    fun saveComment() = mviViewModel.onAction(MviViewModel.UiAction.SaveComment(et_exercise_comment.text.toString()))

    fun clearComment() {
        et_exercise_comment.setText("")
        mviViewModel.onAction(MviViewModel.UiAction.ClearComment)
    }

    fun startTimer() {
        countDownTimer = object : CountDownTimer(TimeLeftInMillis, 1000) {
            override fun onTick(MillisUntilFinish: Long) {
                TimeLeftInMillis = MillisUntilFinish
                updateCountDownText()
            }

            override fun onFinish() {
                TimerRunning = false
                bt_start!!.text = "Start"
            }
        }.start()
        TimerRunning = true
        bt_start!!.text = "Pause"
    }

    fun loadSeconds() {
        val sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE)
        val seconds = sharedPreferences.getString("seconds", "180")

        // Change actual values that timer uses
        START_TIME_IN_MILLIS = (seconds!!.toInt() * 1000).toLong()
        TimeLeftInMillis = START_TIME_IN_MILLIS
        et_seconds!!.setText(seconds)
    }

    fun saveSeconds() {
        val sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        if (!et_seconds!!.text.toString().isEmpty()) {
            val seconds = et_seconds!!.text.toString()

            // Change actual values that timer uses
            START_TIME_IN_MILLIS = (seconds.toInt() * 1000).toLong()
            TimeLeftInMillis = START_TIME_IN_MILLIS

            // Save to shared preferences
            editor.putString("seconds", et_seconds!!.text.toString())
            editor.apply()
        }
    }

    fun pauseTimer() {
        countDownTimer!!.cancel()
        TimerRunning = false
        bt_start!!.text = "Start"
    }

    fun resetTimer() {
        if (TimerRunning) {
            pauseTimer()
            TimeLeftInMillis = START_TIME_IN_MILLIS
            updateCountDownText()
        }
    }

    fun updateCountDownText() {
        val seconds = TimeLeftInMillis.toInt() / 1000
        val minutes = seconds / 60
        et_seconds!!.setText(seconds.toString())
    }

    private fun saveToSharedPreferences(){
        // Sort Before Saving
        MainActivity.sortWorkoutDaysDate()
        // Actually Save Changes in shared preferences
        MainActivity.saveWorkoutData(applicationContext)
    }
    override fun addSet(position: Int, workoutSet: WorkoutSet) {
        MainActivity.Workout_Days[position].addSet(workoutSet)
        saveToSharedPreferences()
        data.postValue(ArrayList(MainActivity.Workout_Days[position].sets))
    }

    override fun addWorkoutDay(workoutDay: WorkoutDay) {
        MainActivity.Workout_Days.add(workoutDay)
        saveToSharedPreferences()
        data.postValue(ArrayList(workoutDay.sets))
    }

    override fun removeSet(toBeRemovedSet: WorkoutSet) {
        for (i in MainActivity.Workout_Days.indices) {
            if (MainActivity.Workout_Days[i].sets.contains(toBeRemovedSet)) {
                // If last set the delete the whole object
                if (MainActivity.Workout_Days[i].sets.size == 1) {
                    MainActivity.Workout_Days.remove(MainActivity.Workout_Days[i])
                } else {
                    MainActivity.Workout_Days[i].removeSet(toBeRemovedSet)
                    break
                }
            }
        }
        saveToSharedPreferences()
        data.value = ArrayList(fetch())
    }

    lateinit var data : MutableLiveData<List<WorkoutSet>>

    private fun fetch(): ArrayList<WorkoutSet> {
        val Todays_Exercise_Sets = ArrayList<WorkoutSet>()
        // Find Sets for a specific date and exercise
        for (i in MainActivity.Workout_Days.indices) {
            // If date matches
            if (MainActivity.Workout_Days[i].date == MainActivity.date_selected) {
                for (j in MainActivity.Workout_Days[i].sets.indices) {
                    // If exercise matches
                    if (exercise_name == MainActivity.Workout_Days[i].sets[j].exercise) {
                        Todays_Exercise_Sets.add(MainActivity.Workout_Days[i].sets[j])
                    }
                }
            }
        }
        return Todays_Exercise_Sets
    }
    override fun fetchWorkSets(): LiveData<List<WorkoutSet>> {
        val Todays_Exercise_Sets = fetch()
        data = MutableLiveData(Todays_Exercise_Sets)
        return data
    }

    override fun updateComment(
        dateSelected: String?,
        exerciseKey: String?,
        exerciseComment: String
    ) {
        val exercise_position =
            MainActivity.getExercisePosition(MainActivity.date_selected, exerciseKey)
        if (exercise_position >= 0) {
            println("We can comment, exercise exists")
        } else {
            println("We can't comment, exercise doesn't exist")
            return
        }
        //TODO Replace with  storage
        // Get the date for today
        val day_position = MainActivity.getDayPosition(MainActivity.date_selected)
        // Modify the data structure to add the comment
        MainActivity.Workout_Days[day_position].exercises[exercise_position].comment = exerciseComment
        saveToSharedPreferences()
    }

    override fun GetExercise(): WorkoutExercise? {
        val exercise_position =
            MainActivity.getExercisePosition(MainActivity.date_selected, exercise_name)

        // Exists, then show the comment

        // Exists, then show the comment
        if (exercise_position >= 0) {
            println("We can comment, exercise exists")
            val day_position = MainActivity.getDayPosition(MainActivity.date_selected)
            return MainActivity.Workout_Days[day_position].exercises[exercise_position]
        }
        return null
    }

    override fun getExercisesWithName(exerciseName: String): List<WorkoutExercise> {
        // Find all performed sessions of a specific exercise and add them to local data structure
        val All_Performed_Sessions = ArrayList<WorkoutExercise>()
        for (i in MainActivity.Workout_Days.indices.reversed()) {
            for (j in MainActivity.Workout_Days[i].exercises.indices) {
                if (MainActivity.Workout_Days[i].exercises[j].exercise == exerciseName) {
                    All_Performed_Sessions.add(MainActivity.Workout_Days[i].exercises[j])
                }
            }
        }
        return All_Performed_Sessions
    }
}