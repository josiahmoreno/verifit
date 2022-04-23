package com.example.verifit.settings

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alorma.compose.settings.ui.SettingsGroup
import com.alorma.compose.settings.ui.SettingsList
import com.alorma.compose.settings.ui.SettingsMenuLink
import com.example.verifit.SettingsActivity
import com.example.verifit.bottomnavigation.BottomNavigationComposable
import com.example.verifit.exercises.UiAction
import com.example.verifit.main.BottomNavItem
import com.example.verifit.main.getActivity
import com.example.verifit.me.TopAppBarDropdownMenu
import com.google.accompanist.appcompattheme.AppCompatTheme
import com.google.accompanist.pager.ExperimentalPagerApi

@ExperimentalComposeUiApi
class Compose_SettingsActivity: AppCompatActivity() {
    // Helper Data Structure


    @OptIn(ExperimentalPagerApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppCompatTheme {
                Scaffold(
                        topBar = {
                            TopAppBar(
                                    backgroundColor = MaterialTheme.colors.primary,
                                    title = {

                                        Text(text = "Settings",
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis) // titl

                                    },
                                    navigationIcon = {IconButton(onClick = {
                                        getActivity()?.onBackPressed()
                                        //viewModel.onAction(MviViewModel.UiAction.ShowComments)
                                        //viewModel.onAction(UiAction.ExitSearch)
                                    }) {
                                        Icon(Icons.Filled.ArrowBack, "Back")
                                    }
                                    }
                            )
                        },
                        content = {
                            SettingsScreen()
                        },
                        bottomBar = {

                        }
                )



            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@ExperimentalComposeUiApi
@Composable
fun SettingsScreen(){

    Column() {
        Text(text = "Backup and Restore",
                modifier = Modifier.padding(start = 64.dp, top = 24.dp, bottom = 12.dp),
                fontSize = 14.sp,
                color = Color.Black,
                fontWeight = FontWeight.SemiBold
        )
        //SettingsGroup(modifier = Modifier,title = {Text(text = "Backup and Restore")}) {
            SettingsMenuLink(
                    title = { Text(text = "Import Workout Data") },
                    subtitle = { Text(text = "Import workout data from a previous backup") },
                    onClick = {},
            )
            SettingsMenuLink(
                    title = { Text(text = "Export Workout Data") },
                    subtitle = { Text(text = "Create a backup of your ") },
                    onClick = {},
            )
        SettingsMenuLink(
                title = { Text(text = "Delete Data") },
                subtitle = { Text(text = "Delete all local workout data") },
                onClick = {},
        )


        //}
        Divider(thickness = 1.dp)
        Column {
            Text(text = "General",
                    modifier = Modifier.padding(start = 64.dp, top = 24.dp, bottom = 12.dp),
                    fontSize = 14.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.SemiBold
            )
        SettingsMenuLink(
                title = { Text(text = "Source Code") },
                subtitle = { Text(text = "Contributions are always welcome") },
                onClick = {},
        )
        SettingsMenuLink(
                title = { Text(text = "Licence") },
                subtitle = { Text(text = "GNU General Public Licence, version 3") },
                onClick = {},
        )

    }
    }

}

@OptIn(ExperimentalMaterialApi::class)
@ExperimentalComposeUiApi
@Composable
@Preview(showBackground = true)
fun SettingsScreenPreview(){
    MaterialTheme {
        SettingsScreen()
    }
}