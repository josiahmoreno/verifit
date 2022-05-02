package com.example.verifit.me

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.example.verifit.R
import com.example.verifit.SettingsActivity
import com.example.verifit.bottomnavigation.BottomNavigationComposable
import com.example.verifit.main.BottomNavItem
import com.example.verifit.main.getActivity
import com.example.verifit.settings.Compose_SettingsActivity
import com.google.accompanist.appcompattheme.AppCompatTheme
import com.google.accompanist.pager.ExperimentalPagerApi

@OptIn(ExperimentalMaterialApi::class)
@ExperimentalComposeUiApi
@Composable
@Preview(showBackground = true)
fun MeScreen(){
    val context = LocalContext.current
    val bodyContent = remember { mutableStateOf("Select menu to change content") }
    Scaffold(
        topBar = {
            TopAppBar(
                backgroundColor = MaterialTheme.colors.primary,
                title = {

                    Text(text = "Me",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis) // titl
                },
                actions = {
                    TopAppBarDropdownMenu(listOf("Settings"), bodyContent) {
                        when (it) {
                            "Settings" -> {
                                val intent = Intent(context, Compose_SettingsActivity::class.java)
                                context.startActivity(intent)
                            }
                        }
                    }
                }
            )
        },
        content = {
            Box(modifier = Modifier.background((colorResource( R.color.core_grey_05))).fillMaxWidth().fillMaxHeight()){
                //Text(bodyContent.value)
             }


        },
        bottomBar = {
            //BottomNavigationComposable(BottomNavItem.Me)
        }
    )
}
@ExperimentalComposeUiApi
class Compose_MeActivity: AppCompatActivity() {
    // Helper Data Structure


    @OptIn(ExperimentalPagerApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppCompatTheme {
               // MeScreen()
            }
        }
    }
}

@Composable
fun TopAppBarDropdownMenu(list: List<String>, bodyContent: MutableState<String>,settings: (String) -> Unit) {
    val expanded = remember { mutableStateOf(false) }

    Box(
        Modifier
            .wrapContentSize(Alignment.TopEnd)
    ) {
        IconButton(onClick = {
            expanded.value = true
            bodyContent.value =  "Menu Opening"
        }) {
            Icon(
                Icons.Filled.MoreVert,
                contentDescription = "More Menu"
            )
        }
    }


    DropdownMenu(
        expanded = expanded.value,
        onDismissRequest = { expanded.value = false },
    ) {
        list.forEachIndexed { index, s ->
            DropdownMenuItem(onClick = {
                expanded.value = false
                bodyContent.value = s
                settings(s)
            }) {
                Text(s)
            }
            if(index < list.count() -1 )
            Divider()
        }
    }
}