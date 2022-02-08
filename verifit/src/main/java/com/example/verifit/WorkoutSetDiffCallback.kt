package com.example.verifit

import androidx.recyclerview.widget.DiffUtil

class WorkoutSetDiffCallback: DiffUtil.ItemCallback<WorkoutSet>() {
    override fun areItemsTheSame(oldItem: WorkoutSet, newItem: WorkoutSet): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: WorkoutSet, newItem: WorkoutSet): Boolean {
        if (if (oldItem.date != null) oldItem.date != newItem.date else newItem.date != null) return false
        if (if (oldItem.exercise != null) oldItem.exercise != newItem.exercise else newItem.exercise != null) return false
        if (if (oldItem.category != null) oldItem.category != newItem.category else newItem.category != null) return false
        if (if (oldItem.reps != null) oldItem.reps != newItem.reps else newItem.reps != null) return false
        if (if (oldItem.weight != null) oldItem.weight != newItem.weight else newItem.weight != null) return false
        return if (oldItem.comment != null) oldItem.comment == newItem.comment else newItem.comment == null
    }

}
