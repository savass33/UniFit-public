package com.example.atividade1

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class func_AdicionaExercicio : AppCompatActivity() {
    private lateinit var btnConf: Button
    private lateinit var back: ImageButton
    private lateinit var menuBtn: ImageView
    private lateinit var searchView: SearchView
    private lateinit var muscleGroupSpinner: Spinner
    private lateinit var exercisesRecyclerView: RecyclerView

    private lateinit var db: FirebaseFirestore
    private val allExercises = mutableListOf<Exercise>()
    private lateinit var adapter: ExerciseAdapter

    private lateinit var alunoId: String
    private lateinit var treinoTipo: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.func_adicionarexercicio)

        db = FirebaseFirestore.getInstance()
        menuBtn = findViewById(R.id.menuIcon)
        back = findViewById(R.id.btnVoltar2)

        back.setOnClickListener {
            onBackPressed()
        }

        menuBtn.setOnClickListener{
            func_PopupMenuHelper.mostrarPopupMenu(this, it)
        }

        // 🔹 Dados recebidos da tela anterior
        alunoId = intent.getStringExtra("alunoId") ?: ""
        treinoTipo = intent.getStringExtra("treinoTipo") ?: ""

        if (alunoId.isBlank() || treinoTipo.isBlank()) {
            Toast.makeText(this, "Erro: aluno ou treino não informado", Toast.LENGTH_LONG).show()
            finish()
            return
        }
        setupBottomNavigationADM.setup(this)
        setupViews()
        setupRecyclerView()
        setupSpinner()
        setupSearchView()
        loadExercises()
        setupClickListeners()
    }

    private fun setupViews() {
        btnConf = findViewById(R.id.confirmButtonAddExercicio)
        searchView = findViewById(R.id.searchView)
        muscleGroupSpinner = findViewById(R.id.muscleGroupSpinner)
        exercisesRecyclerView = findViewById(R.id.exercisesRecyclerView)
    }

    private fun setupRecyclerView() {
        adapter = ExerciseAdapter(allExercises) { exercise ->
            val newDocRef = db.collection("usuarios")
                .document(alunoId)
                .collection("treinos")
                .document(treinoTipo)
                .collection("exercicios")
                .document() // Gera ID automático

            val exerciseWithId = exercise.copy(id = newDocRef.id)

            newDocRef.set(exerciseWithId)
                .addOnSuccessListener {
                    Toast.makeText(this, "${exercise.nome} adicionado ao treino $treinoTipo!", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this, func_EditaExercicio::class.java).apply {
                        putExtra("alunoId", alunoId)
                        putExtra("treinoTipo", treinoTipo)
                        putExtra("exercicioId", newDocRef.id)
                    }
                    startActivity(intent)
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Erro ao adicionar exercício", Toast.LENGTH_SHORT).show()
                }
        }

        exercisesRecyclerView.layoutManager = LinearLayoutManager(this)
        exercisesRecyclerView.adapter = adapter
    }


    private fun setupSpinner() {
        val muscleGroups = arrayOf("Todos", "Peito", "Pernas", "Ombros", "Costas", "Braços")
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, muscleGroups)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        muscleGroupSpinner.adapter = spinnerAdapter

        muscleGroupSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                filterExercises()
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun setupSearchView() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String) = false
            override fun onQueryTextChange(newText: String): Boolean {
                filterExercises()
                return true
            }
        })
    }

    private fun loadExercises() {
        db.collection("exercicios")
            .get()
            .addOnSuccessListener { result ->
                allExercises.clear()
                for (document in result) {
                    val exercise = document.toObject(Exercise::class.java).apply {
                        id = document.id
                    }
                    allExercises.add(exercise)
                }
                filterExercises()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao carregar exercícios", Toast.LENGTH_SHORT).show()
            }
    }

    private fun filterExercises() {
        val query = searchView.query.toString().trim().lowercase()
        val selectedGroup = if (muscleGroupSpinner.selectedItemPosition == 0) null
        else muscleGroupSpinner.selectedItem.toString().trim().lowercase()

        val filtered = allExercises.filter { exercise ->
            val grupo = exercise.grupoMuscular?.trim()?.lowercase()
            val nome = exercise.nome?.trim()?.lowercase() ?: ""

            (selectedGroup == null || grupo == selectedGroup) &&
                    (query.isEmpty() || nome.contains(query))
        }

        adapter.updateList(filtered)
    }

    private fun setupClickListeners() {
        btnConf.setOnClickListener {
            onBackPressed()
        }
    }
}
