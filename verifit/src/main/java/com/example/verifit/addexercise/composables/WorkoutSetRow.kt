package com.example.verifit.addexercise.composables

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusTarget
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.motion.widget.Debug.getLocation
import androidx.lifecycle.LiveData
import com.example.verifit.SampleObjProvider
import com.example.verifit.WorkoutSet
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


private val String.d: Unit
    get() {
        Log.d("WorkoutSetRow", this)
    }



@ExperimentalMaterialApi
@Preview
@Composable
fun WorkoutSetRow(@PreviewParameter(SampleObjProvider::class) workoutSet: WorkoutSet, click :(()-> Unit)? = null, isSelected : Boolean = false ){

    val interactionSource = remember { MutableInteractionSource() }
    var pressedState = interactionSource.collectIsPressedAsState()
    val focusRequester = FocusRequester()
    val colorClick = MaterialTheme.colors.primary
    val ripple =         rememberRipple(color = colorClick)
    var color = remember{ mutableStateOf(Color.White)}
    LaunchedEffect(key1 = "WorkoutSetRow", block =  {
        while(isSelected){
            "press".d
            interactionSource.emit(PressInteraction.Press(Offset.Zero))
            }

        interactionSource.interactions.collect{
            "$it".d
            if(it is FocusInteraction.Focus){
                ripple
            }
        }
    })

    val coroutineScope = rememberCoroutineScope()
    Card()
    {
        Column(modifier = Modifier
            .background( if(isSelected) colorClick.copy(alpha = .12f) else  MaterialTheme.colors.surface )
            .selectable(selected = isSelected,
                interactionSource = interactionSource,
                indication = ripple,
                onClick = {
                    click?.invoke()
                    "selectable2 click".d
                    //focusRequester.requestFocus()
//                    coroutineScope.launch {
//                        //delay(290)
//                        interactionSource.emit(FocusInteraction.Focus())
//                    }
                })
            .focusable(true)
            .focusTarget()
            .focusRequester(focusRequester)
//            .toggleable(true, onValueChange = {
//                "toggleable2 $it".d
//            })
        ) {
            Row(modifier = Modifier.padding(bottom = 8.dp)) {
                Spacer(modifier = Modifier.width(40.dp))
                Text(text = workoutSet.weight.toString(),
                    fontSize = 20.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 10.dp, bottom = 10.dp))
                Text(text = "kg",
                    color = MaterialTheme.typography.body1.color,
                    modifier = Modifier.padding(start = 5.dp, top = 15.dp))
                Spacer(modifier = Modifier.weight(1.0f))
                Text(text = workoutSet.reps.toString(),
                    fontSize = 20.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 10.dp, bottom = 10.dp))
                Text(text = "reps",
                    color = MaterialTheme.typography.body1.color,
                    modifier = Modifier.padding(start = 5.dp, end = 40.dp, top = 15.dp))
            }
            Divider(color = Color.LightGray, thickness = 1.dp)
        }
    }
}