package com.example.verifit

import android.content.Context
import com.example.verifit.AddExerciseActivity.Companion.UpdateViewOnClick
import com.example.verifit.WorkoutSet
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import com.example.verifit.R
import android.widget.TextView
import androidx.cardview.widget.CardView
import java.util.ArrayList

// Adapter for WorkoutSet Class
class AddExerciseWorkoutSetAdapter(var ct: Context, var Workout_Sets: ArrayList<WorkoutSet>, val click: (Int)-> Void) : RecyclerView.Adapter<AddExerciseWorkoutSetAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(ct)
        val view = inflater.inflate(R.layout.workout_set_row, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // Double -> String
        holder.tv_weight.text = Workout_Sets[position].weight.toString()

        // Double -> Integer -> String
        holder.tv_reps.text = Workout_Sets[position].reps.toInt().toString()

        // Updates Edit Texts and Buttons when clicked
        holder.cardView.setOnClickListener { updateView(position) }
    }

    // Notify AddExerciseActivity of the clicked position
    fun updateView(position: Int) {
        // Updates the position of the user selected set in AddExerciseActivity
        //AddExerciseActivity.Clicked_Set = position
        click(position)

        // Updates ets buttons and sets in AddExerciseActivity
        UpdateViewOnClick()
    }

    override fun getItemCount(): Int {
        return Workout_Sets.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tv_reps: TextView
        var tv_weight: TextView
        var cardView: CardView

        init {
            tv_reps = itemView.findViewById(R.id.set_reps)
            tv_weight = itemView.findViewById(R.id.tv_date)
            cardView = itemView.findViewById(R.id.cardview_set)
        }
    }
}