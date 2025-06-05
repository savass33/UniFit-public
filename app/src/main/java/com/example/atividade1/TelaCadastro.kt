package com.example.atividade1

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class TelaCadastro : AppCompatActivity() {

    private lateinit var edtNome: EditText
    private lateinit var edtEmail: EditText
    private lateinit var edtSenha: EditText
    private lateinit var btnCadastrar: Button
    private lateinit var setaVoltar: ImageView

    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastro)

        auth = Firebase.auth

        edtNome = findViewById(R.id.edtNome)
        edtEmail = findViewById(R.id.edtEmail)
        edtSenha = findViewById(R.id.edtSenha)
        btnCadastrar = findViewById(R.id.btnCadastrar)
        setaVoltar = findViewById(R.id.imgSetaVoltar)

        setaVoltar.setOnClickListener {
            voltarParaLogin()
        }

        btnCadastrar.setOnClickListener {
            val nome = edtNome.text.toString().trim()
            val email = edtEmail.text.toString().trim()
            val senha = edtSenha.text.toString().trim()

            if (validarCampos(nome, email, senha)) {
                cadastrarUsuario(nome, email, senha)
            }
        }
    }

    private fun validarCampos(nome: String, email: String, senha: String): Boolean {
        if (nome.isEmpty()) {
            edtNome.error = "Digite seu nome completo"
            edtNome.requestFocus()
            return false
        }

        if (email.isEmpty()) {
            edtEmail.error = "Digite seu email"
            edtEmail.requestFocus()
            return false
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtEmail.error = "Digite um email válido"
            edtEmail.requestFocus()
            return false
        }

        if (senha.isEmpty()) {
            edtSenha.error = "Digite uma senha"
            edtSenha.requestFocus()
            return false
        }

        if (senha.length < 6) {
            edtSenha.error = "A senha deve ter pelo menos 6 caracteres"
            edtSenha.requestFocus()
            return false
        }

        return true
    }

    private fun cadastrarUsuario(nome: String, email: String, senha: String) {
        val progressDialog = ProgressDialog(this).apply {
            setMessage("Criando sua conta...")
            setCancelable(false)
            show()
        }

        auth.createUserWithEmailAndPassword(email, senha)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val userId = user?.uid

                    if (userId != null) {
                        val userData = hashMapOf(
                            "nome" to nome,
                            "email" to email,
                            "isAdmin" to false,
                            "emailVerificado" to false,
                            "uid" to userId,
                            "dataCriacao" to com.google.firebase.Timestamp.now()
                        )

                        db.collection("usuarios").document(userId)
                            .set(userData)
                            .addOnSuccessListener {
                                progressDialog.dismiss()
                                showCadastroSucessoDialog(email)
                            }
                            .addOnFailureListener { e ->
                                progressDialog.dismiss()
                                Toast.makeText(
                                    this,
                                    "Erro ao salvar dados no Firestore: ${e.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                                voltarParaLogin()
                            }
                    } else {
                        progressDialog.dismiss()
                        Toast.makeText(this, "Erro ao obter UID do usuário.", Toast.LENGTH_LONG).show()
                    }

                } else {
                    progressDialog.dismiss()
                    val erro = task.exception
                    val mensagem = when (erro) {
                        is FirebaseAuthUserCollisionException -> "Esse e-mail já está em uso."
                        is FirebaseAuthWeakPasswordException -> "Senha fraca. Use pelo menos 6 caracteres."
                        else -> "Erro ao criar conta: ${erro?.message}"
                    }
                    Toast.makeText(this, mensagem, Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun showCadastroSucessoDialog(email: String) {
        AlertDialog.Builder(this)
            .setTitle("Cadastro Realizado")
            .setMessage("Conta criada com sucesso para $email.\nVocê deve verificar seu e-mail antes de fazer login.")
            .setPositiveButton("OK") { _, _ ->
                voltarParaLogin()
            }
            .setCancelable(false)
            .show()
    }

    private fun voltarParaLogin() {
        startActivity(Intent(this, login::class.java))
        finish()
    }
}
