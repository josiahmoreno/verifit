package com.example.verifit.common

import android.graphics.Color
import androidx.lifecycle.*
import androidx.navigation.NavHostController
import com.example.verifit.navigationhost.AuroraNavigator
import com.example.verifit.workoutservice.WorkoutService
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import dagger.hilt.android.scopes.ViewModelScoped
import java.util.ArrayList
import javax.inject.Inject

interface ListenToCommentResultsUseCase {

    operator fun invoke(): LiveData<String> {
        return MutableLiveData("")
    }
}

@ViewModelScoped
class ListenToCommentResultsUseCaseImpl @Inject constructor(val savedStateHandle: SavedStateHandle): ListenToCommentResultsUseCase {

    override fun invoke(): LiveData<String> { return savedStateHandle.getLiveData<String>("comment")
    }
}

class NoOpListenToCommentResultsUseCase: ListenToCommentResultsUseCase
