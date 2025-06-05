package com.example.atividade1

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class NotificacaoAdapter(
    private val notificacoes: List<Notificacao>
) : RecyclerView.Adapter<NotificacaoAdapter.NotificacaoViewHolder>() {

    inner class NotificacaoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtMensagem: TextView = itemView.findViewById(R.id.txtMensagem)
        val txtDatas: TextView = itemView.findViewById(R.id.txtDatas)
        val btnAcao: ImageButton = itemView.findViewById(R.id.btnAcaos)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificacaoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_notificacao, parent, false)
        return NotificacaoViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificacaoViewHolder, position: Int) {
        val notificacao = notificacoes[position]

        // Formatando data
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val dataFormatada = dateFormat.format(Date(notificacao.timestamp))

        holder.txtDatas.text = "Recebido em: $dataFormatada"

        // Mensagem dinâmica baseada no tipo
        holder.txtMensagem.text = when (notificacao.tipo) {
            "solicitacao" -> "\uD83D\uDC64 ${notificacao.nomeAluno} solicitou mudança de treino: ${notificacao.treinoSolicitado}"
            "confirmacao" -> notificacao.mensagem
            "concluido" -> notificacao.mensagem
            else -> notificacao.mensagem
        }

        // Botão de ação: só aparece para tipo "solicitacao"
        if (notificacao.tipo == "solicitacao") {
            holder.btnAcao.visibility = View.VISIBLE
            // Remova ou comente temporariamente:
            /*
            holder.btnAcao.setOnClickListener {
                val context = holder.itemView.context
                val intent = Intent(context, func_EditarAluno::class.java)
                intent.putExtra("idAluno", notificacao.idAluno)
                intent.putExtra("treinoSolicitado", notificacao.treinoSolicitado)
                context.startActivity(intent)
            }
            */
        } else {
            holder.btnAcao.visibility = View.GONE
        }
    }

    override fun getItemCount() = notificacoes.size
}
