package com.example.atividade1

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

data class ExercTreino(
    val id: String = "",
    val nome: String = "",
    val series: Int = 0,
    val repeticoes: Int = 0
)

class ExercicioAdminAdapter(
    private val context: Context,
    private val lista: List<ExercTreino>,
    private val alunoId: String,
    private val treinoTipo: String,
    private val onExclusao: () -> Unit
) : RecyclerView.Adapter<ExercicioAdminAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nome: TextView = itemView.findViewById(R.id.txtNomeExercicio)
        val btnEditar: ImageButton = itemView.findViewById(R.id.btnEditar)
        val btnExcluir: ImageButton = itemView.findViewById(R.id.btnExcluir)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_exercicio_edicao, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = lista.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val exercicio = lista[position]

        // Mostra nome com formatação de séries e repetições
        holder.nome.text = "${exercicio.nome} (${exercicio.series}x${exercicio.repeticoes})"

        // Editar exercício
        holder.btnEditar.setOnClickListener {
            val intent = Intent(context, func_EditaExercicio::class.java).apply {
                putExtra("alunoId", alunoId)
                putExtra("treinoTipo", treinoTipo)
                putExtra("exercicioId", exercicio.id)
            }
            context.startActivity(intent)
        }

        // Excluir exercício do Firestore
        holder.btnExcluir.setOnClickListener {
            FirebaseFirestore.getInstance()
                .collection("usuarios")
                .document(alunoId)
                .collection("treinos")
                .document(treinoTipo)
                .collection("exercicios")
                .document(exercicio.id)
                .delete()
                .addOnSuccessListener {
                    Toast.makeText(context, "Exercício removido", Toast.LENGTH_SHORT).show()
                    onExclusao()
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Erro ao excluir exercício", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
