package com.example.atividade1

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject

class aluno_Treino : AppCompatActivity() {

    private lateinit var trocaTreino: TextView
    private lateinit var imgVoltar: ImageButton
    private lateinit var menuBtn: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ExerciciosAdapter
    private var listaExercicios = mutableListOf<Exercise>()
    private lateinit var progressoBarra: ProgressBar
    private lateinit var progressoTexto: TextView
    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var btnFinalizarTreino: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.aluno_treino)

        trocaTreino = findViewById(R.id.tvTrocarTreino)
        imgVoltar = findViewById(R.id.btnVoltar)
        progressoBarra = findViewById(R.id.progressoBarra)
        menuBtn = findViewById(R.id.btnMenu)
        progressoTexto = findViewById(R.id.txtProgresso)
        recyclerView = findViewById(R.id.recyclerViewExercicios)
        btnFinalizarTreino = findViewById(R.id.btnFinalizarTreino)

        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        sharedPrefs = getSharedPreferences("progresso_$userId", MODE_PRIVATE)

        adapter = ExerciciosAdapter(listaExercicios) { pos ->
            listaExercicios[pos].noTreino = true
            salvarLocalmente(listaExercicios[pos].id ?: listaExercicios[pos].nome ?: "")
            atualizarProgresso()
            adapter.notifyItemChanged(pos)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        carregarExerciciosDoFirestore()

        setupBottomNavigation.setup(this)

        btnFinalizarTreino.setOnClickListener {
            finalizarTreino()
        }

        menuBtn.setOnClickListener {
            aluno_PopMenuHelper.mostrarPopupMenu(this, it)
        }

        imgVoltar.setOnClickListener {
            val intent = Intent(this, aluno_Home::class.java)
            startActivity(intent)
        }

        trocaTreino.setOnClickListener {
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@setOnClickListener
            val treinoSelecionado = intent.getStringExtra("treinoSelecionado") ?: "Indefinido"
            val db = FirebaseFirestore.getInstance()

            db.collection("usuarios").document(userId).get().addOnSuccessListener { doc ->
                val nomeAluno = doc.getString("nome") ?: "Aluno"
                NotificacaoService.solicitarMudancaDeTreino(treinoSelecionado, nomeAluno, userId)
                Toast.makeText(this, "Mudança de treino solicitada!", Toast.LENGTH_SHORT).show()
            }
        }

    }




    private fun salvarLocalmente(id: String) {
        val editor = sharedPrefs.edit()
        editor.putBoolean(id, true)
        editor.apply()
    }

    private fun foiFeito(id: String): Boolean {
        return sharedPrefs.getBoolean(id, false)
    }

    private fun carregarExerciciosDoFirestore() {
        val treinoId = intent.getStringExtra("treinoSelecionado") ?: return
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()

        db.collection("usuarios")
            .document(userId)
            .collection("treinos")
            .document(treinoId)
            .collection("exercicios")
            .get()
            .addOnSuccessListener { result ->
                listaExercicios.clear()
                for (doc in result) {
                    val exercicio = doc.toObject<Exercise>().apply {
                        id = doc.id
                        noTreino = foiFeito(id ?: nome ?: "")
                    }
                    listaExercicios.add(exercicio)
                }
                adapter.notifyDataSetChanged()
                atualizarProgresso()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao carregar exercícios do treino $treinoId", Toast.LENGTH_SHORT).show()
            }
    }


    private fun atualizarProgresso() {
        val total = listaExercicios.size
        val feitos = listaExercicios.count { it.noTreino == true }
        val progresso = if (total > 0) (feitos * 100 / total) else 0

        progressoBarra.progress = progresso
        progressoTexto.text = "$progresso%"
    }

    private fun finalizarTreino() {
        val progressoAtual = progressoBarra.progress

        if (progressoAtual < 100) {
            Toast.makeText(this, "Complete todos os exercícios antes de finalizar o treino!", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()

        // 🧠 Limpar progresso local
        sharedPrefs.edit().clear().apply()
        listaExercicios.forEach { it.noTreino = false }
        adapter.notifyDataSetChanged()
        atualizarProgresso()

        Toast.makeText(this, "Treino finalizado. Progresso reiniciado!", Toast.LENGTH_SHORT).show()

        // ✅ Atualizar contador no Firestore
        val userDocRef = db.collection("usuarios").document(userId)
        db.runTransaction { transaction ->
            val snapshot = transaction.get(userDocRef)
            val treinosAtuais = snapshot.getLong("totalTreinosFeitos") ?: 0
            transaction.update(userDocRef, "totalTreinosFeitos", treinosAtuais + 1)
        }.addOnSuccessListener {
            Toast.makeText(this, "Treino registrado com sucesso!", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(this, "Erro ao atualizar contador de treinos.", Toast.LENGTH_SHORT).show()
        }

        // Notificação (opcional)
        db.collection("usuarios").document(userId).get().addOnSuccessListener { doc ->
            val nomeAluno = doc.getString("nome") ?: "Aluno"
            NotificacaoService.notificarTreinoConcluido(userId, nomeAluno)
        }

        // Ir para Home
        val intent = Intent(this, aluno_Home::class.java)
        startActivity(intent)
    }



}
