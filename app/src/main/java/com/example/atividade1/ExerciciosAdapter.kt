package com.example.atividade1

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ExerciciosAdapter(
    private val lista: List<Exercise>,
    private val onFeitoClick: (Int) -> Unit
) : RecyclerView.Adapter<ExerciciosAdapter.ExercicioViewHolder>() {

    inner class ExercicioViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nome = view.findViewById<TextView>(R.id.nomeExercicio)
        val series = view.findViewById<TextView>(R.id.seriesRepsExercicio)
        val botaoFeito = view.findViewById<Button>(R.id.button)
        val imagem = view.findViewById<ImageView>(R.id.imagemExercicio)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExercicioViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_exercicio, parent, false)
        return ExercicioViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExercicioViewHolder, position: Int) {
        val exercicio = lista[position]

        holder.nome.text = exercicio.nome ?: "Exercício"
        val seriesText = exercicio.series ?: "-"
        val repsText = exercicio.repeticoes ?: "-"
        holder.series.text = "${seriesText}x${repsText}"

        Glide.with(holder.itemView.context)
            .load(exercicio.imagemUrl)
            .placeholder(R.drawable.supinoimg)
            .into(holder.imagem)

        if (exercicio.noTreino == true) {
            holder.botaoFeito.text = "Completo"
            holder.botaoFeito.setBackgroundColor(Color.parseColor("#4CAF50"))
            holder.botaoFeito.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_solid, 0, 0, 0)
            holder.botaoFeito.isEnabled = false
        } else {
            holder.botaoFeito.text = "Feito"
            holder.botaoFeito.setBackgroundColor(Color.parseColor("#007AFF"))
            holder.botaoFeito.setTextColor(Color.WHITE)
            holder.botaoFeito.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_outline, 0, 0, 0)
            holder.botaoFeito.isEnabled = true
        }

        holder.botaoFeito.setOnClickListener {
            holder.botaoFeito.animate()
                .alpha(0f)
                .setDuration(150)
                .withEndAction {
                    exercicio.noTreino = true
                    holder.botaoFeito.text = "Verificado"
                    holder.botaoFeito.setBackgroundColor(Color.parseColor("#4CAF50"))
                    holder.botaoFeito.setTextColor(Color.WHITE)
                    holder.botaoFeito.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_solid, 0, 0, 0)
                    holder.botaoFeito.alpha = 1f
                    holder.botaoFeito.isEnabled = false
                    onFeitoClick(position)
                }.start()
        }
    }

    override fun getItemCount(): Int = lista.size
}