package com.example.atividade1

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ExerciseAdapter(
    private var exercises: List<Exercise>,
    private val onAddClick: (Exercise) -> Unit
) : RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder>() {

    inner class ExerciseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.exerciseName)
        val ivExercise: ImageView = itemView.findViewById(R.id.exerciseImage)
        val btnAdd: ImageView = itemView.findViewById(R.id.btnAddExercise)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_exercise, parent, false)
        return ExerciseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        val exercise = exercises[position]

        holder.tvName.text = exercise.nome ?: "Sem nome"

        Glide.with(holder.itemView.context)
            .load(exercise.imagemUrl)
            .placeholder(R.drawable.unifit)
            .into(holder.ivExercise)

        holder.btnAdd.setOnClickListener {
            onAddClick(exercise)
        }
    }


    override fun getItemCount(): Int = exercises.size

    fun updateList(newList: List<Exercise>) {
        exercises = newList
        notifyDataSetChanged()
    }
}
