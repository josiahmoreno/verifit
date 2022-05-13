package com.example.verifit.common

import android.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavHostController
import com.example.verifit.workoutservice.WorkoutService
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import java.util.ArrayList

interface ListenToCommentResultsUseCase {

    operator fun invoke(): LiveData<String> {
        return MutableLiveData("")
    }
}

class ListenToCommentResultsUseCaseImpl(val navHostController: NavHostController): ListenToCommentResultsUseCase {

    override fun invoke(): LiveData<String> {
        return navHostController.currentBackStackEntry?.savedStateHandle?.getLiveData<String>("comment")!!
    }
}

class NoOpListenToCommentResultsUseCase: ListenToCommentResultsUseCase
