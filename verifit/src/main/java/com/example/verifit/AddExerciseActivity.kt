package com.example.verifit

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.verifit.AddExerciseWorkoutSetAdapter
import android.os.CountDownTimer
import android.os.Bundle
import com.example.verifit.R
import com.example.verifit.AddExerciseActivity
import com.example.verifit.MainActivity
import com.example.verifit.WorkoutSet
import com.example.verifit.WorkoutDay
import android.content.Intent
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.verifit.WorkoutExercise
import com.example.verifit.ExerciseHistoryExerciseAdapter
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.LineData
import android.content.SharedPreferences
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

    // Comment Items
    lateinit var bt_save_comment: Button
    lateinit var bt_clear_comment: Button
    lateinit var et_exercise_comment: EditText

    private val mviViewModel : MviViewModel = MviViewModel(this)

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
                        /* initial state clear
                    if (Todays_Exercise_Sets.isEmpty()) {
                        bt_clear!!.text = "Clear"
                    } else {
                        bt_clear!!.text = "Delete"
                    */
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
                                updateTodaysExercises()
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
        bt_no.setOnClickListener { alertDialog.dismiss() }

        // Actually Delete set and update local data structure
        bt_yes.setOnClickListener { // Get soon to be deleted set
            mviViewModel.onAction(MviViewModel.UiAction.YesDelete)
        }

        // Show delete confirmation dialog box
        alertDialog.show()
    }

    // Button On Click Methods
    fun clickSave(view: View?) {

        mviViewModel.onAction(MviViewModel.UiAction.SaveExercise(et_weight!!.text.toString(),
            et_reps!!.text.toString(),
            exercise_name!!,
            MainActivity.getExerciseCategory(exercise_name),
            MainActivity.getDayPosition(MainActivity.date_selected)
        ))

       /* if (et_weight!!.text.toString().isEmpty() || et_reps!!.text.toString().isEmpty()) {
            Toast.makeText(applicationContext, "Please write Weight and Reps", Toast.LENGTH_SHORT)
                .show()
        } else {
            // Get user sets && reps
            val reps = et_reps!!.text.toString().toDouble()
            val weight = et_weight!!.text.toString().toDouble()

            // Create New Set Object
            val workoutSet = WorkoutSet(
                MainActivity.date_selected,
                exercise_name,
                MainActivity.getExerciseCategory(exercise_name),
                reps,
                weight
            )

            // Ignore wrong input
            if (reps == 0.0 || weight == 0.0 || reps < 0 || weight < 0) {
                Toast.makeText(
                    applicationContext,
                    "Please write correct Weight and Reps",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                // Find if workout day already exists
                val position = MainActivity.getDayPosition(MainActivity.date_selected)

                // If workout day exists
                if (position >= 0) {
                    MainActivity.Workout_Days[position].addSet(workoutSet)
                } else {
                    val workoutDay = WorkoutDay()
                    workoutDay.addSet(workoutSet)
                    MainActivity.Workout_Days.add(workoutDay)
                }

                // Update Local Data Structure
                updateTodaysExercises()
                Toast.makeText(applicationContext, "Set Logged", Toast.LENGTH_SHORT).show()
            }
        }

        // Fixed Myria induced bug
        Clicked_Set = Todays_Exercise_Sets.size - 1*/
    }

    /*
    fun clickClearOld(view: View?){

        // Clear Function
        if (Todays_Exercise_Sets.isEmpty()) {
            bt_clear!!.text = "Clear"
            et_reps!!.setText("")
            et_weight!!.setText("")
        } else {
            // Show confirmation dialog  box
            // Prepare to show exercise dialog box
            val inflater = LayoutInflater.from(this)
            val view1 = inflater.inflate(R.layout.delete_set_dialog, null)
            val alertDialog = AlertDialog.Builder(this).setView(view1).create()
            val bt_yes = view1.findViewById<Button>(R.id.bt_yes3)
            val bt_no = view1.findViewById<Button>(R.id.bt_no3)

            // Dismiss dialog box
            bt_no.setOnClickListener { alertDialog.dismiss() }

            // Actually Delete set and update local data structure
            bt_yes.setOnClickListener { // Get soon to be deleted set
                val to_be_removed_set = Todays_Exercise_Sets[Clicked_Set]

                // Find the set in main data structure and delete it
                for (i in MainActivity.Workout_Days.indices) {
                    if (MainActivity.Workout_Days[i].sets.contains(to_be_removed_set)) {
                        // If last set the delete the whole object
                        if (MainActivity.Workout_Days[i].sets.size == 1) {
                            MainActivity.Workout_Days.remove(MainActivity.Workout_Days[i])
                        } else {
                            MainActivity.Workout_Days[i].removeSet(to_be_removed_set)
                            break
                        }
                    }
                }

                // Let the user know I guess
                Toast.makeText(applicationContext, "Set Deleted", Toast.LENGTH_SHORT).show()

                // Update Local Data Structure
                updateTodaysExercises()
                alertDialog.dismiss()

                // Update Clicked set to avoid crash
                Clicked_Set = Todays_Exercise_Sets.size - 1
            }

            // Show delete confirmation dialog box
            alertDialog.show()
        }
    }

     */
    // Clear / Delete
    fun clickClear(view: View?) {
        // Clear Function
        mviViewModel.onAction(MviViewModel.UiAction.Clear)

    }

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
    fun clickPlusWeight(view: View?) {
        if (!et_weight!!.text.toString().isEmpty()) {
            var weight = et_weight!!.text.toString().toDouble()
            weight = weight + 1
            et_weight!!.setText(weight.toString())
        } else {
            et_weight!!.setText("1.0")
        }
    }

    // Do I even need to explain this?
    fun clickPlusReps(view: View?) {
        if (!et_reps!!.text.toString().isEmpty()) {
            var reps = et_reps!!.text.toString().toInt()
            reps = reps + 1
            et_reps!!.setText(reps.toString())
        } else {
            et_reps!!.setText("1")
        }
    }

    // Do I even need to explain this?
    fun clickMinusWeight(view: View?) {
        if (!et_weight!!.text.toString().isEmpty()) {
            var weight = et_weight!!.text.toString().toDouble()
            weight = weight - 1
            if (weight < 0) {
                weight = 0.0
            }
            et_weight!!.setText(weight.toString())
        }
    }

    // Do I even need to explain this?
    fun clickMinusReps(view: View?) {
        if (!et_reps!!.text.toString().isEmpty()) {
            var reps = et_reps!!.text.toString().toInt()
            reps = reps - 1
            if (reps < 0) {
                reps = 0
            }
            et_reps!!.setText(reps.toString())
        }
    }

    // Handles Intent Stuff
    fun initActivity() {
        val `in` = intent
        exercise_name = `in`.getStringExtra("exercise")
        supportActionBar!!.title = exercise_name
    }

    // Updates Local Data Structure
    fun updateTodaysExercises() {
        // Clear since we don't want duplicates
//        Todays_Exercise_Sets.clear()
//
//        // Find Sets for a specific date and exercise
//        for (i in MainActivity.Workout_Days.indices) {
//            // If date matches
//            if (MainActivity.Workout_Days[i].date == MainActivity.date_selected) {
//                for (j in MainActivity.Workout_Days[i].sets.indices) {
//                    // If exercise matches
//                    if (exercise_name == MainActivity.Workout_Days[i].sets[j].exercise) {
//                        Todays_Exercise_Sets.add(MainActivity.Workout_Days[i].sets[j])
//                    }
//                }
//            }
//        }
//
//        // Change Button Functionality
//        if (Todays_Exercise_Sets.isEmpty()) {
//            bt_clear!!.text = "Clear"
//        } else {
//            bt_clear!!.text = "Delete"
//        }
//
//        // Update Recycler View
//        workoutSetAdapter2!!.notifyDataSetChanged()
    }

    // Initialize Recycler View Object
    fun initrecyclerView() {

        // Clear since we don't want duplicates
        /*
        Todays_Exercise_Sets.clear()

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

         */

        // Find Recycler View Object
        recyclerView = findViewById(R.id.recycler_view)
        workoutSetAdapter2 = AddExerciseWorkoutSetAdapter { pos ->
            mviViewModel.onAction(MviViewModel.UiAction.WorkoutClick(pos))
        }
        recyclerView.adapter = workoutSetAdapter2
        recyclerView.layoutManager = LinearLayoutManager(this)


        // Set Edit Text values to max set volume if possible
        initEditTexts()


        // Change Button Functionality


        // Initialize Integer position or else we get a crash
        //old Clicked_Set = Todays_Exercise_Sets.size - 1
    }

    // Set Edit Text values to max set volume if sets exist
    fun initEditTexts() {
        var max_weight = 0.0
        var max_reps = 0
        var max_exercise_volume = 0.0

        // Find Max Weight and Reps for a specific exercise
        for (i in MainActivity.Workout_Days.indices) {
            for (j in MainActivity.Workout_Days[i].sets.indices) {
                if (MainActivity.Workout_Days[i].sets[j].volume > max_exercise_volume && MainActivity.Workout_Days[i].sets[j].exercise == exercise_name) {
                    max_exercise_volume = MainActivity.Workout_Days[i].sets[j].volume
                    max_reps = Math.round(MainActivity.Workout_Days[i].sets[j].reps)
                        .toInt()
                    max_weight = MainActivity.Workout_Days[i].sets[j].weight
                }
            }
        }

        // If never performed the exercise leave Edit Texts blank
        if (max_reps == 0 || max_weight == 0.0) {
            et_reps!!.setText("")
            et_weight!!.setText("")
        } else {
            et_reps!!.setText(max_reps.toString())
            et_weight!!.setText(max_weight.toString())
        }
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
        } else if (item.itemId == R.id.history) {
            // Prepare to show exercise history dialog box
            val inflater = LayoutInflater.from(this@AddExerciseActivity)
            val view = inflater.inflate(R.layout.exercise_history_dialog, null)
            val alertDialog = AlertDialog.Builder(this@AddExerciseActivity).setView(view).create()


            // Declare local data structure
            val All_Performed_Sessions = ArrayList<WorkoutExercise>()

            // Find all performed sessions of a specific exercise and add them to local data structure
            for (i in MainActivity.Workout_Days.indices.reversed()) {
                for (j in MainActivity.Workout_Days[i].exercises.indices) {
                    if (MainActivity.Workout_Days[i].exercises[j].exercise == exercise_name) {
                        All_Performed_Sessions.add(MainActivity.Workout_Days[i].exercises[j])
                    }
                }
            }


            // Set Exercise Name
            val tv_exercise_name = view.findViewById<TextView>(R.id.tv_exercise_name)
            tv_exercise_name.text = exercise_name


            // Set Exercise History Recycler View
            val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView_Exercise_History)
            val workoutExerciseAdapter4 =
                ExerciseHistoryExerciseAdapter(this@AddExerciseActivity, All_Performed_Sessions)


            // Crash Here
            recyclerView.adapter = workoutExerciseAdapter4
            recyclerView.layoutManager = LinearLayoutManager(this@AddExerciseActivity)
            alertDialog.show()
        } else if (item.itemId == R.id.graph) {
            // Prepare to show exercise history dialog box
            val inflater = LayoutInflater.from(this@AddExerciseActivity)
            val view = inflater.inflate(R.layout.exercise_graph_dialog, null)
            val alertDialog = AlertDialog.Builder(this@AddExerciseActivity).setView(view).create()


            // Get Chart Object
            val lineChart = view.findViewById<View>(R.id.lineChart) as LineChart

            // Create Array List that will hold graph data
            val Volume_Values = ArrayList<Entry>()
            var x = 0

            // Get Exercise Volume
            for (i in MainActivity.Workout_Days.indices) {
                for (j in MainActivity.Workout_Days[i].exercises.indices) {
                    val current_exercise = MainActivity.Workout_Days[i].exercises[j]
                    if (current_exercise.exercise == exercise_name) {
                        Volume_Values.add(Entry(x.toFloat(), current_exercise.volume.toFloat()))
                        x++
                    }
                }
            }
            val volumeSet = LineDataSet(Volume_Values, "Volume")
            val data = LineData(volumeSet)
            volumeSet.lineWidth = 2f
            volumeSet.valueTextSize = 10f
            volumeSet.valueTextColor = Color.BLACK
            lineChart.data = data
            lineChart.description.isEnabled = false


            // Show Chart Dialog box
            alertDialog.show()
        } else if (item.itemId == R.id.comment) {
            // Prepare to show exercise history dialog box
            val inflater = LayoutInflater.from(this@AddExerciseActivity)
            val view = inflater.inflate(R.layout.add_exercise_comment_dialog, null)
            val alertDialog = AlertDialog.Builder(this@AddExerciseActivity).setView(view).create()
            bt_save_comment = view.findViewById(R.id.bt_save_comment)
            bt_clear_comment = view.findViewById(R.id.bt_clear_comment)
            et_exercise_comment = view.findViewById(R.id.et_exercise_comment)

            // Check if exercise exists (to show the comment if it has one)
            // Find if workout day already exists
            val exercise_position =
                MainActivity.getExercisePosition(MainActivity.date_selected, exercise_name)

            // Exists, then show the comment
            if (exercise_position >= 0) {
                println("We can comment, exercise exists")
                val day_position = MainActivity.getDayPosition(MainActivity.date_selected)
                val comment =
                    MainActivity.Workout_Days[day_position].exercises[exercise_position].comment
                et_exercise_comment.setText(comment)
            }
            bt_clear_comment.setOnClickListener(View.OnClickListener { clearComment() })
            bt_save_comment.setOnClickListener(View.OnClickListener { saveComment() })

            // Show Chart Dialog box
            alertDialog.show()
        }
        return super.onOptionsItemSelected(item)
    }

    // Makes necesary checks and saves comment
    fun saveComment() {
        // Check for empty input
        if (et_exercise_comment!!.text.toString().isEmpty()) {
            Toast.makeText(applicationContext, "Please write a comment", Toast.LENGTH_SHORT).show()
            return
        }

        // Check if exercise exists (cannot comment on non-existant exercise)
        // Find if workout day already exists
        val exercise_position =
            MainActivity.getExercisePosition(MainActivity.date_selected, exercise_name)
        if (exercise_position >= 0) {
            println("We can comment, exercise exists")
        } else {
            println("We can't comment, exercise doesn't exist")
            Toast.makeText(applicationContext, "Can't comment without sets", Toast.LENGTH_SHORT)
                .show()
            return
        }


        // Get user comment
        val comment = et_exercise_comment!!.text.toString()

        // Print it for sanity check
        println(comment)

        // Get the date for today
        val day_position = MainActivity.getDayPosition(MainActivity.date_selected)

        // Modify the data structure to add the comment
        MainActivity.Workout_Days[day_position].exercises[exercise_position].comment = comment
        Toast.makeText(applicationContext, "Comment Logged", Toast.LENGTH_SHORT).show()
    }

    // Makes necesary checks and clears comment
    fun clearComment() {
        et_exercise_comment!!.setText("")

        // Check if exercise exists (cannot comment on non-existant exercise)
        // Find if workout day already exists
        val exercise_position =
            MainActivity.getExercisePosition(MainActivity.date_selected, exercise_name)
        if (exercise_position >= 0) {
            println("We can comment, exercise exists")
        } else {
            println("We can't comment, exercise doesn't exist")
            Toast.makeText(applicationContext, "Can't comment without sets", Toast.LENGTH_SHORT)
                .show()
            return
        }

        // Get user comment
        val comment = et_exercise_comment!!.text.toString()

        // Print it for sanity check
        println(comment)

        // Get the date for today
        val day_position = MainActivity.getDayPosition(MainActivity.date_selected)

        // Modify the data structure to add the comment
        MainActivity.Workout_Days[day_position].exercises[exercise_position].comment = comment
        Toast.makeText(applicationContext, "Comment Cleared", Toast.LENGTH_SHORT).show()
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

    companion object {
        //var Todays_Exercise_Sets = ArrayList<WorkoutSet>()
        @JvmStatic
        //public var Clicked_Set = 0

        // Add Exercise Activity Specifics
        var et_reps: EditText? = null
        var et_weight: EditText? = null

        // Update this activity when a set is clicked
        @JvmStatic
        fun UpdateViewOnClick() {
            // Get selected set
            //old val clicked_set = Todays_Exercise_Sets[Clicked_Set]

            // Update Edit Texts
            //old et_weight!!.setText(clicked_set.weight.toString())
            //old et_reps!!.setText(clicked_set.reps.toInt().toString())
        }
    }

    override fun addSet(workoutSet: WorkoutSet) {
        TODO("Not yet implemented")
    }

    override fun addSet(position: Int, workoutSet: WorkoutSet) {

        MainActivity.Workout_Days[position].addSet(workoutSet)
        data.postValue(ArrayList(MainActivity.Workout_Days[position].sets))

    }

    override fun addWorkoutDay(workoutDay: WorkoutDay) {
       MainActivity.Workout_Days.add(workoutDay)
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
    }

    lateinit var data : MutableLiveData<List<WorkoutSet>>
    override fun fetchWorkSets(): LiveData<List<WorkoutSet>> {

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
        data = MutableLiveData(Todays_Exercise_Sets)
        return data
    }


}