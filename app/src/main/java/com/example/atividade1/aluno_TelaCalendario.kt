package com.example.atividade1

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class aluno_TelaCalendario: AppCompatActivity() {
    private lateinit var menuBtn: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.aluno_calendario)
        setupBottomNavigation.setup(this)

        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()
        val txtTotalFeitos = findViewById<TextView>(R.id.textView6)

        db.collection("usuarios").document(userId)
            .get()
            .addOnSuccessListener { doc ->
                val totalFeitos = doc.getLong("totalTreinosFeitos") ?: 0
                txtTotalFeitos.text = totalFeitos.toString()
            }
            .addOnFailureListener {
                txtTotalFeitos.text = "?"
            }

        menuBtn = findViewById(R.id.menuIcon)

        menuBtn.setOnClickListener {
            aluno_PopMenuHelper.mostrarPopupMenu(this, it)
        }
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