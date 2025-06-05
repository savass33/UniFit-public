package com.example.atividade1

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class aluno_Home : AppCompatActivity() {

    private lateinit var menuBtn: ImageView
    private lateinit var comecarA: Button
    private lateinit var comecarB: Button
    private lateinit var comecarC: Button
    private lateinit var txtPerfil: TextView
    private lateinit var btnAvatar: ImageView
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        aluno_PopMenuHelper.applyTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.aluno_home)

        // Inicializa elementos da interface
        menuBtn = findViewById(R.id.menuIcon)
        comecarA = findViewById(R.id.btnComecarA)
        comecarB = findViewById(R.id.btnComecarB)
        comecarC = findViewById(R.id.btnComecarC)
        txtPerfil = findViewById(R.id.txtPerfil)
        btnAvatar = findViewById(R.id.avatar)

        setupBottomNavigation.setup(this)

        // Busca nome do usuário no Firestore
        val usuarioAtual = auth.currentUser
        if (usuarioAtual != null) {
            val uid = usuarioAtual.uid
            db.collection("usuarios").document(uid).get()
                .addOnSuccessListener { documento ->
                    Log.d("DEBUG_UID", "UID atual: $uid")
                    if (documento.exists()) {
                        val nome = documento.getString("nome") ?: "Usuário"
                        txtPerfil.text = nome

                        val imagemBase64 = documento.getString("image")
                        if (!imagemBase64.isNullOrEmpty()) {
                            val bitmap = base64ToBitmap(imagemBase64)
                            btnAvatar.setImageBitmap(bitmap)
                        }

                    } else {
                        txtPerfil.text = "Usuário"
                        Toast.makeText(this, "Dados do usuário não encontrados.", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { erro ->
                    txtPerfil.text = "Usuário"
                    Log.e("FIRESTORE", "Erro ao buscar nome do usuário", erro)
                }
        }

        // Ações dos botões
        btnAvatar.setOnClickListener {
            val intent = Intent(this, TelaPerfil::class.java)
            startActivity(intent)
        }

        menuBtn.setOnClickListener {
            aluno_PopMenuHelper.mostrarPopupMenu(this, it)
        }

        comecarA.setOnClickListener {
            abrirTelaTreino("A")
        }

        comecarB.setOnClickListener {
            abrirTelaTreino("B")
        }

        comecarC.setOnClickListener {
            abrirTelaTreino("C")
        }
    }

    private fun abrirTelaTreino(treinoId: String) {
        val intent = Intent(this, aluno_Treino::class.java)
        intent.putExtra("treinoSelecionado", treinoId)
        startActivity(intent)
    }

    private fun base64ToBitmap(base64Str: String): Bitmap {
        val decodedBytes = Base64.decode(base64Str, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    }

    override fun onRestart() {
        super.onRestart()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onStart() {
        super.onStart()
    }
}
