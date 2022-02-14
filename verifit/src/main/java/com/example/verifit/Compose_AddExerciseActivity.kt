package com.example.verifit

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
// for a `var` variable also add
import androidx.compose.runtime.setValue

import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.*

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
        Scaffold(
                drawerContent = { /*...*/ },
                topBar = {
                    TopAppBar(

                            title = {

                                    Text("Exercise Name") // titl
                            },
                            actions = {
                                IconButton(onClick = {}){
                                    Icon(Icons.Filled.SettingsBackupRestore, "history")
                                }
                                IconButton(onClick = {}){
                                    Icon(Icons.Filled.Poll, "graph")
                                }
                                IconButton(onClick = {}){
                                    Icon(Icons.Filled.Alarm, "timer")
                                }
                                IconButton(onClick = {}){
                                    Icon(Icons.Filled.Comment, "comment")
                                }
                            }
                    )
                },
                content = { Column(modifier = Modifier.padding(Dp(16.0f))){

                    Text("Weight:")
                    // use the material divider
                    Divider(color = MaterialTheme.colors.primary, thickness = 1.dp)
                    Row{
                        var weight : String by remember { mutableStateOf("4.0") }
                        IconButton(onClick = {

                        }){
                            Icon(Icons.Filled.Remove,"plus one")
                        }
                        //textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.End)
                        TextField(
                                value = weight,
                                textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
                                onValueChange = { nextText : String -> weight = nextText },

                        )
                        IconButton(onClick = {

                        }){
                            Icon(Icons.Filled.Add,"plus one")
                        }
                    }
                    Text("Reps:")
                    // use the material divider
                    Divider(color = MaterialTheme.colors.primary, thickness = 1.dp)
                    Row{
                        var reps : String by remember { mutableStateOf("4.0") }
                        IconButton(onClick = {

                        }){
                            Icon(Icons.Filled.Remove,"plus one")
                        }
                        //textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.End)
                        TextField(
                                value = reps,
                                textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
                                onValueChange = { nextText : String -> reps = nextText },

                                )
                        IconButton(onClick = {

                        }){
                            Icon(Icons.Filled.Add,"plus one")
                        }

                    }
                    Row(
                            modifier = Modifier.fillMaxWidth(),
                    ){
                        Button(
                                onClick = {},
                                modifier = Modifier.fillMaxWidth().weight(1f).padding(end = 2.5.dp)
                        ){
                            Text("Save")
                        }
                        Button(
                                onClick = {},
                                modifier = Modifier.fillMaxWidth().weight(1f).padding(start = 2.5.dp)
                        ){
                            Text("Clear")
                        }

                    }

                }}
        )

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