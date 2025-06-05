package com.example.atividade1

data class Funcionario(
    var Cargo: String? = null,
    var fotoUrl: String? = null,
    var horariosDisponiveis: List<String>? = null,
    var isAdmin: Boolean? = true,
    var nome: String? = null,
    var id: String? = null   // adiciona id para facilitar uso
)