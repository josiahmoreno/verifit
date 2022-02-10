package com.example.verifit

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import android.os.CountDownTimer
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.data.LineData
import android.view.*
import android.widget.*
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.*
import com.github.mikephil.charting.charts.LineChart
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.ArrayList
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider

class Compose_AddExerciseActivity : AppCompatActivity() {
    // Helper Data Structures

    var exercise_name: String? = null



    private val mviViewModel : MviViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AddExerciseScreen(mviViewModel)
        }
    }

    @Preview
    @Composable
    fun AddExerciseScreen(@PreviewParameter(MviPreviewProvider::class) viewModel: MviViewModel) {


        Text("Hello World $viewModel")
    }



    
}

class MviPreviewProvider : PreviewParameterProvider<MviViewModel> {
    override val values: Sequence<MviViewModel>
        get() = sequenceOf(MviViewModel(FakeWorkoutService(),FakeTimer(),null))

}
class FakeWorkoutService:WorkoutService{
    override fun addSet(position: Int, workoutSet: WorkoutSet) {
        TODO("Not yet implemented")
    }

    override fun addWorkoutDay(workoutDay: WorkoutDay) {
        TODO("Not yet implemented")
    }

    override fun removeSet(toBeRemovedSet: WorkoutSet) {
        TODO("Not yet implemented")
    }

    override fun fetchWorkSets(): LiveData<List<WorkoutSet>> {
        return MutableLiveData(ArrayList<WorkoutSet>().toList())
    }

    override fun updateComment(dateSelected: String?, exerciseKey: String?, exerciseComment: String) {
        TODO("Not yet implemented")
    }

    override fun GetExercise(): WorkoutExercise? {
        return null
    }

    override fun getExercisesWithName(exerciseName: String): List<WorkoutExercise> {
        TODO("Not yet implemented")
    }

    override fun calculateMaxWeight(): Pair<String, String> {
        return Pair("1.0","1.0")
    }

}
class FakeTimer(): TimerService{
    override var onFinish: (() -> Unit)?
        get() = TODO("Not yet implemented")
        set(@Suppress("UNUSED_PARAMETER") value) {}
    override var onTick: ((Long) -> Unit)?
        get() = TODO("Not yet implemented")
        set(@Suppress("UNUSED_PARAMETER") value) {}

    override fun getCurrentTime(): String {
        TODO("Not yet implemented")
    }

    override fun cancel() {
        TODO("Not yet implemented")
    }

    override fun save(seconds: String) {
        TODO("Not yet implemented")
    }

    override fun start(timeLeftInMillis: Long) {
        TODO("Not yet implemented")
    }

}