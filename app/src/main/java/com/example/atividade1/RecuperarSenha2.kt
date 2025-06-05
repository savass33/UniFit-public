package com.example.atividade1

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.File

class RecuperarSenha2: AppCompatActivity() {
    private lateinit var setaVoltar: ImageView
    private lateinit var btnAlterarSenha: Button
    private lateinit var edtNovaSenha: EditText
    private lateinit var edtConfirmarSenha: EditText
    private var emailUsuario: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recuperarsenha2)

        setaVoltar = findViewById(R.id.imgSetaVoltar)
        btnAlterarSenha = findViewById(R.id.btnAlterarSenha)
        edtNovaSenha = findViewById(R.id.edtNovaSenha)
        edtConfirmarSenha = findViewById(R.id.edtRepetirSenha)

        emailUsuario = intent.getStringExtra("EMAIL_USUARIO")

        setaVoltar.setOnClickListener {
            val intent = Intent(this, RecuperarSenha::class.java)
            startActivity(intent)
        }

        btnAlterarSenha.setOnClickListener {
            val novaSenha = edtNovaSenha.text.toString()
            val confirmarSenha = edtConfirmarSenha.text.toString()

            if (novaSenha.isEmpty() || confirmarSenha.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
            } else if (novaSenha != confirmarSenha) {
                Toast.makeText(this, "As senhas não coincidem!", Toast.LENGTH_SHORT).show()
            } else if (emailUsuario != null) {
                if (trocarSenha(emailUsuario!!, novaSenha)) {
                    Toast.makeText(this, "Senha alterada com sucesso!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, login::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Erro ao alterar a senha", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun trocarSenha(email: String, novaSenha: String): Boolean {
        return try {
            val arquivo = File(filesDir, "usuarios.csv")
            val linhas = arquivo.readLines()

            val novasLinhas = linhas.map { linha ->
                val partes = linha.split(",")
                if (partes[0] == email) {
                    "$email,$novaSenha"
                } else {
                    linha
                }
            }

            arquivo.writeText(novasLinhas.joinToString("\n"))
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
