package com.example.verifit.addexercise.composables

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
import com.example.verifit.WorkoutSetSampleProvider
import com.example.verifit.WorkoutSet


private val String.d: Unit
    get() {
        Log.d("WorkoutSetRow", this)
    }

@ExperimentalMaterialApi
@Preview
@Composable
fun WorkoutSetRowCard(
    @PreviewParameter(WorkoutSetSampleProvider::class) workoutSet: WorkoutSet,
    click: (() -> Unit)? = null,
    isSelected: Boolean = false,
){


    Card()
    {
        WorkoutSetRow(workoutSet,click,isSelected)
    }
}

@ExperimentalMaterialApi
@Preview
@Composable
fun WorkoutSetRow(
    @PreviewParameter(WorkoutSetSampleProvider::class) workoutSet: WorkoutSet,
    click: (() -> Unit)? = null,
    isSelected: Boolean = false,
){

    val interactionSource = remember { MutableInteractionSource() }
    val focusRequester = FocusRequester()
    val colorClick = MaterialTheme.colors.primary
    val ripple =         rememberRipple(color = colorClick)
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

        Column(modifier = Modifier
            .background(if (isSelected) colorClick.copy(alpha = .12f) else MaterialTheme.colors.surface)
            .selectable(selected = isSelected,
                interactionSource = interactionSource,
                indication = ripple,
                onClick = {
                    click?.invoke()
                })
            .focusable(true)
            .focusTarget()
            .focusRequester(focusRequester)
        ) {
            Row(modifier = Modifier.height(52.dp), verticalAlignment = Alignment.CenterVertically) {
                Spacer(modifier = Modifier.width(40.dp))
                Box(modifier = Modifier.width(40.dp)){
                    if(workoutSet.isWeightPr){
                        Icon(imageVector = Icons.Filled.EmojiEvents,
                            contentDescription = "PR",
                        tint = MaterialTheme.colors.primary)
                    }
                }
                Text(text = workoutSet.weight.toString(),
                    fontSize = 20.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding())
                Text(text = "kg",
                    color = MaterialTheme.typography.body1.color,
                    modifier = Modifier.padding(start = 5.dp ))
                Spacer(modifier = Modifier.weight(1.0f))
                Text(text = workoutSet.reps.toString(),
                    fontSize = 20.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding())
                Text(text = "reps",
                    color = MaterialTheme.typography.body1.color,
                    modifier = Modifier.padding(start = 5.dp, end = 40.dp))
            }
            Divider(color = Color.LightGray, thickness = 1.dp)
        }
}