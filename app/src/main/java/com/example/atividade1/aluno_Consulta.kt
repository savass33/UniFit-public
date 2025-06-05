package com.example.atividade1

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.atividade1.FuncionarioAdapter
import com.google.firebase.firestore.FirebaseFirestore

class aluno_Consulta : AppCompatActivity() {

    private lateinit var recyclerViewFuncionarios: RecyclerView
    private lateinit var adapter: FuncionarioAdapter
    private val listaFuncionarios = mutableListOf<Funcionario>()
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.aluno_consulta)

        db = FirebaseFirestore.getInstance()
        recyclerViewFuncionarios = findViewById(R.id.recyclerViewFuncionarios)

        adapter = FuncionarioAdapter(listaFuncionarios) { funcionario ->
            if (funcionario.id == null || funcionario.nome == null || funcionario.horariosDisponiveis.isNullOrEmpty()) {
                Toast.makeText(this, "Funcionário com dados incompletos", Toast.LENGTH_SHORT).show()
                return@FuncionarioAdapter
            }

            val intent = Intent(this, aluno_AgendarConsulta::class.java).apply {
                putExtra("funcionarioId", funcionario.id)
                putExtra("funcionarioNome", funcionario.nome)
                putStringArrayListExtra("horariosDisponiveis", ArrayList(funcionario.horariosDisponiveis!!))
            }
            startActivity(intent)
        }
        recyclerViewFuncionarios.layoutManager = LinearLayoutManager(this)
        recyclerViewFuncionarios.adapter = adapter

        carregarFuncionarios()
    }

    private fun carregarFuncionarios() {
        db.collection("funcionarios")
            .get()
            .addOnSuccessListener { documentos ->
                listaFuncionarios.clear()
                for (doc in documentos) {
                    val func = doc.toObject(Funcionario::class.java)
                    func.id = doc.id
                    listaFuncionarios.add(func)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao carregar funcionários", Toast.LENGTH_SHORT).show()
            }
    }
}
