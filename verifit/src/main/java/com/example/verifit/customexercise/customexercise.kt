package com.example.verifit.customexercise

import android.content.Context
import android.os.Bundle
import android.widget.Spinner
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.verifit.KnownExerciseService
import com.example.verifit.KnownExerciseServiceImpl
import com.example.verifit.exercises.ExercisesListViewModel
import com.example.verifit.exercises.FetchExercisesListUseCase
import com.google.accompanist.appcompattheme.AppCompatTheme
import com.google.accompanist.pager.ExperimentalPagerApi

@ExperimentalComposeUiApi
class Compose_CustomExerciseActivity : AppCompatActivity() {
    // Helper Data Structure
    private val viewModel: CustomExerciseViewModel by viewModels {
        CustomExerciseViewModelFactory(KnownExerciseServiceImpl.getKnownExerciseService(applicationContext))
    }

    @OptIn(ExperimentalPagerApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppCompatTheme  {
                CustomExerciseScreen(viewModel)
            }
        }
    }
}


@ExperimentalPagerApi
@ExperimentalComposeUiApi
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CustomExerciseScreen(viewModel: CustomExerciseViewModel){

    var expanded  =  remember { mutableStateOf(false)}
    var state = viewModel.viewState.collectAsState()
    Column(){
        Text("Choose Exercise Name:", fontSize = 24.sp, color = MaterialTheme.colors.primary, modifier = Modifier.padding(start = 20.dp, top = 20.dp))
        Divider(thickness = 1.dp, modifier = Modifier.padding(start = 20.dp, top = 8.dp, end = 20.dp), color = MaterialTheme.colors.primary)
        TextField(value = state.value.exerciseNameString, modifier = Modifier.padding(start = 20.dp, top = 20.dp, end = 20.dp),onValueChange = {
            viewModel.onAction(UiAction.OnExerciseName(it))
        })
        Text("Choose Exercise Category:", fontSize = 24.sp, color = MaterialTheme.colors.primary, modifier = Modifier.padding(start = 20.dp, top = 20.dp))
        Divider(thickness = 1.dp, modifier = Modifier.padding(start = 20.dp, top = 10.dp, end = 20.dp), color = MaterialTheme.colors.primary)
        DropdownMenu(
            expanded = expanded.value,
            onDismissRequest = {}) {
            state.value.categories.forEach { category ->
                DropdownMenuItem(onClick = { }) {
                    Text(category)
                }
            }
        }
    }


}

/*
 <TextView
        android:id="@+id/exercise_name1"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginStart="20dp"
        android:layout_marginLeft="20dp"
        android:layout_marginTop="170dp"
        android:text="Choose Exercise Category:"
        android:textColor="@color/colorPrimary"
        android:textSize="24sp"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent">

    </TextView>

    <View
        android:id="@+id/view1"
        android:layout_width="0dp"
        android:layout_height="1dp"
        android:layout_marginStart="20dp"
        android:layout_marginLeft="20dp"
        android:layout_marginTop="10dp"
        android:layout_marginEnd="20dp"
        android:layout_marginRight="20dp"
        android:background="@color/colorPrimary"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/exercise_name1" />

    <Spinner
        android:id="@+id/spinner"
        android:layout_width="300dp"
        android:layout_height="50dp"
        android:layout_marginStart="20dp"
        android:layout_marginLeft="20dp"
        android:layout_marginTop="20dp"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/view1">

    </Spinner>

 */
class CustomExerciseViewModelFactory(
    val knownExerciseService: KnownExerciseService
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CustomExerciseViewModel(
            FetchExercisesListUseCase(knownExerciseService)
        ) as T
    }
}