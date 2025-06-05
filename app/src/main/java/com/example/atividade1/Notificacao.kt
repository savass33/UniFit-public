package com.example.atividade1

data class Notificacao(
    val nomeAluno: String = "",
    val mensagem: String = "",
    val timestamp: Long = 0L,
    val idAluno: String = "",
    val treinoSolicitado: String = "",
    val tipo: String = ""
)
