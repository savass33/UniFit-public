package com.example.atividade1

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore

class func_EditaExercicio : AppCompatActivity() {

    private lateinit var seriesSpinner: Spinner
    private lateinit var repsSpinner: Spinner
    private lateinit var confirmButton: Button
    private lateinit var exerciseName: TextView
    private lateinit var exerciseImage: ImageView
    private lateinit var alunoId: String
    private lateinit var treinoTipo: String
    private lateinit var exercicioId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.func_editarexercicio)
        setupBottomNavigationADM.setup(this)

        // Recupera dados da Intent
        alunoId = intent.getStringExtra("alunoId") ?: ""
        treinoTipo = intent.getStringExtra("treinoTipo") ?: ""
        exercicioId = intent.getStringExtra("exercicioId") ?: ""

        if (alunoId.isEmpty() || treinoTipo.isEmpty() || exercicioId.isEmpty()) {
            Toast.makeText(this, "Dados incompletos", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Referências dos componentes
        seriesSpinner = findViewById(R.id.seriesSpinner)
        repsSpinner = findViewById(R.id.repsSpinner)
        confirmButton = findViewById(R.id.confirmButton)
        exerciseName = findViewById(R.id.exerciseName)
        exerciseImage = findViewById(R.id.exerciseImage)

        // Adapters para os spinners
        val seriesList = listOf("1", "2", "3", "4")
        val repsList = listOf("4", "6", "8", "10", "12", "14", "16", "18", "20")

        seriesSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, seriesList)
        repsSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, repsList)

        // Carrega dados do exercício
        carregarValoresAtuais()

        confirmButton.setOnClickListener {
            val novaSerie = seriesSpinner.selectedItem.toString()
            val novaRepeticao = repsSpinner.selectedItem.toString()

            val docRef = FirebaseFirestore.getInstance()
                .collection("usuarios")
                .document(alunoId)
                .collection("treinos")
                .document(treinoTipo)
                .collection("exercicios")
                .document(exercicioId)

            docRef.update(
                mapOf(
                    "series" to novaSerie,
                    "repeticoes" to novaRepeticao
                )
            ).addOnSuccessListener {
                Toast.makeText(this, "Exercício atualizado", Toast.LENGTH_SHORT).show()
                finish()
            }.addOnFailureListener {
                Toast.makeText(this, "Erro ao atualizar exercício", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun carregarValoresAtuais() {
        val db = FirebaseFirestore.getInstance()
        db.collection("usuarios")
            .document(alunoId)
            .collection("treinos")
            .document(treinoTipo)
            .collection("exercicios")
            .document(exercicioId)
            .get()
            .addOnSuccessListener { doc ->
                if (doc != null) {
                    val series = doc.getString("series") ?: "1"
                    val repeticoes = doc.getString("repeticoes") ?: "4"
                    val nomeExercicio = doc.getString("nome") ?: "Exercício"
                    val imagemUrl = doc.getString("imagemUrl") ?: ""

                    exerciseName.text = nomeExercicio
                    Glide.with(this).load(imagemUrl)
                        .placeholder(R.drawable.image2)
                        .into(exerciseImage)

                    val indexSerie = (seriesSpinner.adapter as ArrayAdapter<String>).getPosition(series)
                    val indexRep = (repsSpinner.adapter as ArrayAdapter<String>).getPosition(repeticoes)

                    seriesSpinner.setSelection(indexSerie)
                    repsSpinner.setSelection(indexRep)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao carregar exercício", Toast.LENGTH_SHORT).show()
            }
    }
}
