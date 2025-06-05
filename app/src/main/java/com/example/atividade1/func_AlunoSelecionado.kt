package com.example.atividade1

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class func_AlunoSelecionado : AppCompatActivity() {

    private lateinit var uidAluno: String
    private lateinit var nomeAluno: String
    private lateinit var menuBtn: ImageView
    private lateinit var txtNomeAluno: TextView
    private lateinit var imgFotoAluno: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.func_alunoselecionado)
        menuBtn = findViewById(R.id.btnMenu)

        uidAluno = intent.getStringExtra("uid_aluno") ?: ""
        nomeAluno = intent.getStringExtra("nome_aluno") ?: ""

        if (uidAluno.isBlank()) {
            Toast.makeText(this, "Dados do aluno não fornecidos", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            onBackPressed()
        }

        menuBtn.setOnClickListener{
            func_PopupMenuHelper.mostrarPopupMenu(this, it)
        }

        txtNomeAluno = findViewById(R.id.txtNomeAlunoSelecionado)
        imgFotoAluno = findViewById(R.id.imgFotoAluno)

        txtNomeAluno.text = nomeAluno

        findViewById<Button>(R.id.btnTreinoA).setOnClickListener {
            abrirEdicaoTreino("A")
        }

        findViewById<Button>(R.id.btnTreinoB).setOnClickListener {
            abrirEdicaoTreino("B")
        }

        findViewById<Button>(R.id.btnTreinoC).setOnClickListener {
            abrirEdicaoTreino("C")
        }

        carregarFotoAluno()
        setupBottomNavigationADM.setup(this)
    }

    private fun abrirEdicaoTreino(tipo: String) {
        val intent = Intent(this, func_EdicaoTreino::class.java)
        intent.putExtra("alunoId", uidAluno)
        intent.putExtra("treinoTipo", tipo)
        startActivity(intent)
    }

    private fun carregarFotoAluno() {
        val db = FirebaseFirestore.getInstance()
        db.collection("usuarios").document(uidAluno).get()
            .addOnSuccessListener { doc ->
                if (doc != null && doc.exists()) {
                    val imagemBase64 = doc.getString("image")
                    if (!imagemBase64.isNullOrEmpty()) {
                        val imagemBitmap = base64ToBitmap(imagemBase64)
                        imgFotoAluno.setImageBitmap(imagemBitmap)
                    }
                }
            }
            .addOnFailureListener {
                Log.e("FIREBASE", "Erro ao carregar imagem do aluno", it)
            }
    }

    private fun base64ToBitmap(base64Str: String): Bitmap {
        val decodedBytes = Base64.decode(base64Str, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    }
}
