package com.example.atividade1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class FuncionarioAdapter(
    private val funcionarios: List<Funcionario>,
    private val onClick: (Funcionario) -> Unit
) : RecyclerView.Adapter<FuncionarioAdapter.FuncionarioViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FuncionarioViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_funcionario_card, parent, false)
        return FuncionarioViewHolder(view)
    }

    override fun onBindViewHolder(holder: FuncionarioViewHolder, position: Int) {
        val funcionario = funcionarios[position]
        holder.bind(funcionario)
        holder.itemView.setOnClickListener { onClick(funcionario) }
    }


    override fun getItemCount() = funcionarios.size

    inner class FuncionarioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nome: TextView = itemView.findViewById(R.id.tvNomeFuncionario)
        private val especialidade: TextView = itemView.findViewById(R.id.tvEspecialidadeFuncionario)
        internal val foto: ImageView = itemView.findViewById(R.id.imgFotoFuncionario)

        fun bind(funcionario: Funcionario) {
            nome.text = funcionario.nome
            especialidade.text = funcionario.Cargo ?: "Sem cargo"

            // Carregar imagem com Glide
            Glide.with(itemView.context)
                .load("https://via.placeholder.com/150")
                .placeholder(R.drawable.unifit)
                .error(R.drawable.unifit)
                .into(foto)

        }
    }
}
