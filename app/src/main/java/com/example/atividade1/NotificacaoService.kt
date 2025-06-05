package com.example.atividade1

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

object NotificacaoService {

    fun solicitarMudancaDeTreino(treinoAtual: String, nomeAluno: String, alunoId: String) {
        val db = FirebaseFirestore.getInstance()
        val notificacao = Notificacao(
            nomeAluno = nomeAluno,
            mensagem = "$nomeAluno solicitou mudança no treino $treinoAtual",
            timestamp = System.currentTimeMillis(),
            idAluno = alunoId,
            treinoSolicitado = treinoAtual,
            tipo = "solicitacao"
        )

        Log.d("NotificacaoService", "Enviando solicitação de troca: $notificacao")

        db.collection("notificacoes").add(notificacao)
            .addOnSuccessListener {
                Log.d("NotificacaoService", "Solicitação salva com sucesso!")
            }
            .addOnFailureListener { e ->
                Log.e("NotificacaoService", "Erro ao salvar solicitação", e)
            }
    }

    fun confirmarMudancaDeTreino(alunoId: String, novoTreino: String, nomeADM: String, nomeAluno: String) {
        val db = FirebaseFirestore.getInstance()
        val notificacao = Notificacao(
            nomeAluno = nomeAluno,
            mensagem = "Treinador $nomeADM mudou seu treino para $novoTreino",
            timestamp = System.currentTimeMillis(),
            idAluno = alunoId,
            treinoSolicitado = novoTreino,
            tipo = "confirmacao"
        )

        Log.d("NotificacaoService", "Enviando confirmação de troca: $notificacao")

        db.collection("usuarios").document(alunoId).collection("notificacoes").add(notificacao)
            .addOnSuccessListener {
                Log.d("NotificacaoService", "Confirmação salva com sucesso!")
            }
            .addOnFailureListener { e ->
                Log.e("NotificacaoService", "Erro ao salvar confirmação", e)
            }
    }

    fun notificarTreinoConcluido(alunoId: String, nomeAluno: String) {
        val db = FirebaseFirestore.getInstance()
        val notificacao = Notificacao(
            nomeAluno = nomeAluno,
            mensagem = "Parabéns $nomeAluno, você terminou o treino de hoje!",
            timestamp = System.currentTimeMillis(),
            idAluno = alunoId,
            tipo = "concluido"
        )

        Log.d("NotificacaoService", "Tentando salvar notificação: $notificacao")

        db.collection("usuarios")
            .document(alunoId)
            .collection("notificacoes")
            .add(notificacao)
            .addOnSuccessListener {
                Log.d("NotificacaoService", "Notificação SALVA com sucesso!")
            }
            .addOnFailureListener { e ->
                Log.e("NotificacaoService", "Erro ao salvar notificação", e)
            }
    }
}
