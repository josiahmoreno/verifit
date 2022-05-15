package com.example.verifit.addexercise.composables

import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.example.verifit.WorkoutSetSampleProvider
import com.example.verifit.WorkoutExercise

@ExperimentalMaterialApi
@Preview
@Composable
fun WorkoutExerciseRow(@PreviewParameter(WorkoutSetSampleProvider::class) workoutSet: WorkoutExercise, click :(()-> Unit)? = null ){
//    Card(onClick = {click?.invoke()}, elevation = 0.dp) {
//        Column {
//
//
//            Row(modifier = Modifier.padding(bottom = 8.dp)) {
//                Spacer(modifier = Modifier.width(40.dp))
//                Text(text = workoutSet.weight.toString(),
//                    fontSize = 20.sp,
//                    color = Color.Black,
//                    fontWeight = FontWeight.Bold,
//                    modifier = Modifier.padding(top = 10.dp, bottom = 10.dp))
//                Text(text = "kg",
//                    color = MaterialTheme.typography.body1.color,
//                    modifier = Modifier.padding(start = 5.dp, top = 15.dp))
//                Spacer(modifier = Modifier.weight(1.0f))
//                Text(text = workoutSet.reps.toString(),
//                    fontSize = 20.sp,
//                    color = Color.Black,
//                    fontWeight = FontWeight.Bold,
//                    modifier = Modifier.padding(top = 10.dp, bottom = 10.dp))
//                Text(text = "reps",
//                    color = MaterialTheme.typography.body1.color,
//                    modifier = Modifier.padding(start = 5.dp, end = 40.dp, top = 15.dp))
//            }
//            Divider(color = Color.LightGray, thickness = 1.dp)
//        }
//    }
}