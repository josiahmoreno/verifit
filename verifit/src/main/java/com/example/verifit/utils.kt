package com.example.verifit

fun GetCategoryIconTint(exercise_name: String?, knownExerciseService: KnownExerciseService) : Int {
    val exercise_category = knownExerciseService.fetchExerciseCategory(exercise_name)
    when (exercise_category) {
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