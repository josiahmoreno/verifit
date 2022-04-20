package com.example.verifit

import com.example.verifit.main.FetchViewPagerDataUseCase
import com.example.verifit.singleton.DateSelectStore
import com.example.verifit.workoutservice.FakeWorkoutService2
import org.junit.Test

class MainViewPagerTest {


    @Test
    public fun ViewModel(){
        val case = FetchViewPagerDataUseCase(workoutService = FakeWorkoutService2(DateSelectStore),
                ColorGetterImpl(DefaultKnownExercise()))
        case()
    }

    class FakeColorGetter: ColorGetter{
        override fun getCategoryIconTint(exercise_name: String?): Int {
            return 255
        }

    }
}