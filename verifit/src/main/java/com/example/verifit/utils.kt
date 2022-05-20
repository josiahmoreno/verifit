package com.example.verifit

import android.util.Log

interface ColorGetter{
    fun getCategoryIconTint(exerciseName: ExerciseName): Int
    fun getCategoryIconTint(category: WorkoutCategory): Int
}
@JvmInline
value class WorkoutCategory(val category: String)

@JvmInline
value class ExerciseName(val exerciseName: String?)

class ColorGetterImpl(val knownExerciseService: KnownExerciseService): ColorGetter{
    init {
        Log.d("ColorGetterImpl", "init")
    }
    override fun getCategoryIconTint(exercise_name: ExerciseName) : Int {
        val exercise_category = knownExerciseService.fetchExerciseCategory(exercise_name.exerciseName)
       return getCategoryIconTint(exercise_category)
    }

    override fun getCategoryIconTint(category: WorkoutCategory) : Int {
        return getCategoryIconTint(category.category)
    }

    private fun getCategoryIconTint(category: String): Int {
        when (category) {
            "Shoulders" -> {
                return android.graphics.Color.argb(255,
                    0,
                    116,
                    189) // Primary Color
            }
            "Back" -> {
                return android.graphics.Color.argb(255, 40, 176, 192)
            }
            "Chest" -> {
                return android.graphics.Color.argb(255, 92, 88, 157)
            }
            "Biceps" -> {
                return android.graphics.Color.argb(255, 255, 50, 50)
            }
            "Triceps" -> {
                return android.graphics.Color.argb(255, 204, 154, 0)
            }
            "Legs" -> {
                return android.graphics.Color.argb(255, 212, 25, 97)
            }
            "Abs" -> {
                return android.graphics.Color.argb(255, 255, 153, 171)
            }
            else -> {
                return android.graphics.Color.argb(255, 52, 58, 64) // Grey AF
            }
        }
    }
}
