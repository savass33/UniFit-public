package com.example.atividade1

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class func_EdicaoTreino : AppCompatActivity() {

    private lateinit var adicionaExec: TextView
    private lateinit var confirma: Button
    private lateinit var menuBtn: ImageView
    private lateinit var back: ImageButton
    private lateinit var scrollContainer: LinearLayout
    private lateinit var alunoId: String
    private lateinit var treinoTipo: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.func_edicaotreino)
        setupBottomNavigationADM.setup(this)

        menuBtn = findViewById(R.id.menuIcon)
        alunoId = intent.getStringExtra("alunoId") ?: ""
        treinoTipo = intent.getStringExtra("treinoTipo") ?: ""

        menuBtn.setOnClickListener{
            func_PopupMenuHelper.mostrarPopupMenu(this, it)
        }

        if (alunoId.isEmpty() || treinoTipo.isEmpty()) {
            Toast.makeText(this, "Dados do aluno não fornecidos", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        adicionaExec = findViewById(R.id.addExerc)
        confirma = findViewById(R.id.btnConfirmar2)
        back = findViewById(R.id.btnVoltar2)
        scrollContainer = findViewById(R.id.layoutExercicios)

        carregarExercicios()

        adicionaExec.setOnClickListener {
            val intent = Intent(this, func_AdicionaExercicio::class.java)
            intent.putExtra("alunoId", alunoId)
            intent.putExtra("treinoTipo", treinoTipo)
            startActivity(intent)
        }

        confirma.setOnClickListener {
            finish()
        }

        back.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        carregarExercicios()
    }

    private fun carregarExercicios() {
        val db = FirebaseFirestore.getInstance()
        db.collection("usuarios")
            .document(alunoId)
            .collection("treinos")
            .document(treinoTipo)
            .collection("exercicios")
            .get()
            .addOnSuccessListener { result ->
                scrollContainer.removeAllViews()
                for (doc in result) {
                    val nome = doc.getString("nome") ?: continue
                    val series = doc.getString("series") ?: ""
                    val repeticoes = doc.getString("repeticoes") ?: ""

                    val itemView = LayoutInflater.from(this)
                        .inflate(R.layout.item_exercicio_edicao, scrollContainer, false)

                    val txtNome = itemView.findViewById<TextView>(R.id.txtNomeExercicio)
                    val btnEditar = itemView.findViewById<ImageButton>(R.id.btnEditar)
                    val btnExcluir = itemView.findViewById<ImageButton>(R.id.btnExcluir)

                    txtNome.text = "$nome ($series x $repeticoes)"

                    btnEditar.setOnClickListener {
                        val intent = Intent(this, func_EditaExercicio::class.java)
                        intent.putExtra("alunoId", alunoId)
                        intent.putExtra("treinoTipo", treinoTipo)
                        intent.putExtra("exercicioId", doc.id)
                        startActivity(intent)
                    }

                    btnExcluir.setOnClickListener {
                        db.collection("usuarios")
                            .document(alunoId)
                            .collection("treinos")
                            .document(treinoTipo)
                            .collection("exercicios")
                            .document(doc.id)
                            .delete()
                            .addOnSuccessListener {
                                Toast.makeText(this, "Exercício excluído", Toast.LENGTH_SHORT).show()
                                carregarExercicios()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Erro ao excluir exercício", Toast.LENGTH_SHORT).show()
                            }
                    }

                    scrollContainer.addView(itemView)
                }
            }
            .addOnFailureListener { e ->
                Log.e("FIREBASE", "Erro ao carregar exercícios: ", e)
                Toast.makeText(this, "Erro ao carregar exercícios", Toast.LENGTH_SHORT).show()
            }
    }
}
