package com.example.atividade1

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class aluno_Contato : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.aluno_contato)

        setupBottomNavigation.setup(this)

        findViewById<ImageView>(R.id.menuButton).setOnClickListener {
            aluno_PopMenuHelper.mostrarPopupMenu(this, it)
        }

        findViewById<MaterialButton>(R.id.buttonWhatsapp).setOnClickListener {
            abrirWhatsApp("5585991625291")
        }

        findViewById<MaterialButton>(R.id.buttonTelefone).setOnClickListener {
            ligarParaTelefone("8534773616")
        }
    }

    private fun abrirWhatsApp(numero: String) {
        val url = "https://wa.me/$numero"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))

        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            Toast.makeText(this, "WhatsApp não está instalado.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun ligarParaTelefone(numero: String) {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:$numero")
        startActivity(intent)
    }
}
