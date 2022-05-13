package com.example.verifit.di

import android.app.Activity
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphNavigator
import androidx.navigation.NavHostController
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.DialogNavigator
import com.example.verifit.ColorGetter
import com.example.verifit.ColorGetterImpl
import com.example.verifit.KnownExerciseService
import com.example.verifit.navigationhost.ExerciseAppActivity
import com.example.verifit.settings.ToastMaker
import com.example.verifit.settings.ToastMakerImpl
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.migration.DisableInstallInCheck
import org.intellij.lang.annotations.PrintFormat
import javax.inject.Singleton

@InstallIn(SingletonComponent::class) // Scope our dependencies
@Module
object Common {



    @Provides
    fun getColorGetter(knownExerciseService: KnownExerciseService): ColorGetter {
        return ColorGetterImpl(knownExerciseService)
    }

    @Provides
    fun getToastMaker(@ApplicationContext context: Context): ToastMaker {
        return ToastMakerImpl(context)
    }

    @Provides
    @Singleton
    fun provideNavController(@ApplicationContext context: Context) = NavHostController(context).apply {
        navigatorProvider.addNavigator(ComposeNavigator())
        navigatorProvider.addNavigator(NavGraphNavigator(navigatorProvider))
        navigatorProvider.addNavigator(DialogNavigator())
    }
}
