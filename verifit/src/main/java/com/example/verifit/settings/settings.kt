package com.example.verifit.settings

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.size
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.alorma.compose.settings.ui.SettingsGroup
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
                SettingsScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@ExperimentalComposeUiApi
@Composable
fun SettingsScreen(){
    SettingsGroup(modifier = Modifier,title = {Text(text = "Backup and Restore")}) {
        
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