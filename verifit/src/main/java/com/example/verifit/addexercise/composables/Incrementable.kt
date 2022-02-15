package com.example.verifit.addexercise.composables

import androidx.compose.compiler.plugins.kotlin.ComposeFqNames.remember
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import java.util.regex.Pattern


@Preview(showBackground = true)
@Composable
fun Incrementable( amount : String = "4.0",
                   decrement: (()->Unit)? = null,
                   increment: (()->Unit)? = null,
                   onTextChanged: ((String)->Unit)? = null,
                   options: IncrementableOptions = IncrementableOptions()
                   ){
    val oldWeight = remember{ mutableStateOf("") }
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = {
            decrement?.invoke()
        }) {
            Icon(imageVector = Icons.Filled.Remove, contentDescription = null, tint = MaterialTheme.colors.primary)
            //Icon(Icons.Filled.Remove, "plus one",  MaterialTheme.colors.primary)
        }
        //textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.End)
        TextField(
            value = amount,
            modifier = Modifier.weight(1f),
            textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center, fontSize = 28.sp, fontWeight = FontWeight.Bold),
            colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.Transparent),
            onValueChange = {
                var weight = it
                if(options.regex == null || Pattern.compile(options.regex).matcher(weight).find()){
                    oldWeight.value = it
                } else {
                    weight = oldWeight.value
                }
                onTextChanged?.invoke(weight)
            },
            keyboardOptions = options.keyboardOptions

            )
        IconButton(onClick = {
            increment?.invoke()
        }) {
            Icon(imageVector = Icons.Filled.Add, contentDescription = null, tint = MaterialTheme.colors.primary)
        }
    }
}

data class IncrementableOptions(val keyboardOptions: KeyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                         val regex: String? = null,
                                ){

}