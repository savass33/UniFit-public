package com.example.atividade1

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class RecuperarSenha : AppCompatActivity() {

    private lateinit var setaVoltar: ImageView
    private lateinit var btnConfirmar: Button
    private lateinit var edtEmail: EditText
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recuperarsenha1)

        // Inicializa FirebaseAuth
        auth = FirebaseAuth.getInstance()

        setaVoltar = findViewById(R.id.imgSetaVoltar)
        btnConfirmar = findViewById(R.id.btnConfirmar)
        edtEmail = findViewById(R.id.edtEmail)

        setaVoltar.setOnClickListener {
            startActivity(Intent(this, login::class.java))
            finish()
        }

        btnConfirmar.setOnClickListener {
            val email = edtEmail.text.toString().trim()

            if (email.isEmpty()) {
                edtEmail.error = "Preencha o e-mail!"
                return@setOnClickListener
            }

            val progressDialog = ProgressDialog(this).apply {
                setMessage("Enviando e-mail de recuperação...")
                setCancelable(false)
                show()
            }

            // Envia o link de redefinição de senha
            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    progressDialog.dismiss()
                    if (task.isSuccessful) {
                        Toast.makeText(
                            this,
                            "Link de recuperação enviado para $email.",
                            Toast.LENGTH_LONG
                        ).show()
                        startActivity(Intent(this, login::class.java))
                        finish()
                    } else {
                        Toast.makeText(
                            this,
                            "Erro ao enviar e-mail: ${task.exception?.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        }
    }
}
