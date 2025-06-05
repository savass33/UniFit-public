package com.example.atividade1

import AlunoAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.atividade1.model.Aluno
import com.google.firebase.firestore.FirebaseFirestore

class func_EditaAlunos : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var menuBtn: ImageView
    private val listaAlunos = mutableListOf<Aluno>()  // Lista com nome + imagem

    override fun onCreate(savedInstanceState: Bundle?) {
        aluno_PopMenuHelper.applyTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.func_editaralunos)

        menuBtn = findViewById(R.id.btnMenu)
        recyclerView = findViewById(R.id.recyclerAlunos)
        recyclerView.layoutManager = LinearLayoutManager(this)

        setupBottomNavigationADM.setup(this)

        menuBtn.setOnClickListener{
            func_PopupMenuHelper.mostrarPopupMenu(this, it)
        }

        carregarAlunosDoFirebase()
    }

    private fun carregarAlunosDoFirebase() {
        FirebaseFirestore.getInstance()
            .collection("usuarios")
            .get()
            .addOnSuccessListener { result ->
                listaAlunos.clear()
                for (doc in result) {
                    val uid = doc.id
                    val nome = doc.getString("nome") ?: "Sem nome"
                    val imagemBase64 = doc.getString("image") ?: ""

                    listaAlunos.add(Aluno(uid, nome, imagemBase64))
                }

                recyclerView.adapter = AlunoAdapter(listaAlunos) { aluno ->
                    val intent = Intent(this, func_AlunoSelecionado::class.java)
                    intent.putExtra("uid_aluno", aluno.uid)
                    intent.putExtra("nome_aluno", aluno.nome)
                    startActivity(intent)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao carregar alunos", Toast.LENGTH_SHORT).show()
                Log.e("FIREBASE", "Falha ao buscar usuários", it)
            }
    }
}
