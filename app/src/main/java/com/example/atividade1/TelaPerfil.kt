package com.example.atividade1

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.DecimalFormat

class TelaPerfil : AppCompatActivity() {

    private lateinit var btnVoltar: ImageButton
    private lateinit var btnEditarPerfil: Button
    private lateinit var txtNome: TextView
    private lateinit var txtLocalCargo: TextView
    private lateinit var txtAssinatura: TextView
    private lateinit var txtAltura: TextView
    private lateinit var txtPeso: TextView
    private lateinit var txtIdade: TextView
    private lateinit var txtIMC: TextView
    private lateinit var txtStatusIMC: TextView
    private lateinit var consultar: TextView
    private lateinit var fotoPerfil: ImageView

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val decimalFormat = DecimalFormat("#.##")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        // Inicializar componentes
        initComponents()
        setupListeners()
        carregarDadosUsuario()
        carregarImagemPerfil()
    }

    private fun initComponents() {
        btnVoltar = findViewById(R.id.btnVoltar)
        btnEditarPerfil = findViewById(R.id.btnEditarPerfil)
        txtNome = findViewById(R.id.txtNome)
        txtLocalCargo = findViewById(R.id.txtLocalCargo)
        txtAssinatura = findViewById(R.id.txtAssinatura)
        consultar = findViewById(R.id.consulta)
        fotoPerfil = findViewById(R.id.fotoPerfil)

        txtAltura = findViewById(R.id.txtAltura)
        txtPeso = findViewById(R.id.txtPeso)
        txtIdade = findViewById(R.id.txtIdade)
        txtIMC = findViewById(R.id.txtIMC)
        txtStatusIMC = findViewById(R.id.txtStatusIMC)
    }

    private fun setupListeners() {
        btnVoltar.setOnClickListener { onBackPressed() }

        btnEditarPerfil.setOnClickListener {
            startActivity(Intent(this, TelaEditarPerfil::class.java))
        }

        consultar.setOnClickListener {
            startActivity(Intent(this, aluno_Consulta::class.java))
        }
    }

    private fun carregarImagemPerfil() {
        val usuarioAtual = auth.currentUser
        if (usuarioAtual != null) {
            val uid = usuarioAtual.uid
            db.collection("usuarios").document(uid).get()
                .addOnSuccessListener { doc ->
                    if (doc != null && doc.exists()) {
                        val base64Image = doc.getString("image")
                        if (!base64Image.isNullOrEmpty()) {
                            val bitmap = base64ToBitmap(base64Image)
                            fotoPerfil.setImageBitmap(bitmap)
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("TELA_PERFIL", "Erro ao carregar imagem do Firestore", e)
                }
        }
    }


    private fun carregarDadosUsuario() {
        val usuarioAtual = auth.currentUser
        if (usuarioAtual != null) {
            val uid = usuarioAtual.uid
            db.collection("usuarios").document(uid).get()
                .addOnSuccessListener { doc ->
                    if (doc != null && doc.exists()) {
                        val nome = doc.getString("nome") ?: "Usuário"
                        val isAdmin = doc.getBoolean("isAdmin") ?: false
                        val cidadeCargo =
                            if (isAdmin) "Fortaleza - CE\nAdministrador" else "Fortaleza - CE\nAluno"
                        val assinatura = "● Assinatura ativa"

                        txtNome.text = nome
                        txtLocalCargo.text = cidadeCargo
                        txtAssinatura.text = assinatura

                        val altura = doc.getString("altura") ?: "0"
                        val peso = doc.getString("peso") ?: "0"
                        val idade = doc.getString("idade") ?: "0"

                        txtAltura.text = "$altura cm"
                        txtPeso.text = "$peso kg"
                        txtIdade.text = "$idade anos"

                        calcularEExibirIMC(altura, peso)

                        // Removido o carregamento da imagem daqui
                    } else {
                        Toast.makeText(
                            this,
                            "Dados do usuário não encontrados.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("TELA_PERFIL", "Erro ao carregar dados do Firestore", e)
                    Toast.makeText(this, "Erro ao carregar dados: ${e.message}", Toast.LENGTH_LONG)
                        .show()
                }
        } else {
            Toast.makeText(this, "Usuário não autenticado.", Toast.LENGTH_SHORT).show()
        }
    }


    private fun calcularEExibirIMC(alturaStr: String, pesoStr: String) {
        try {
            val altura = alturaStr.replace("m", "").replace(",", ".").trim().toDouble()
            val peso = pesoStr.replace("kg", "").trim().toDouble()
            val newAltura = altura / 100
            val imc = peso / (newAltura * newAltura)
            val imcFormatado = decimalFormat.format(imc)
            txtIMC.text = "⚖️ IMC | $imcFormatado"

            val status = when {
                imc < 18.5 -> "Seu IMC está abaixo (18.5), assim como a taxa de gordura corporal"
                imc < 25 -> "Seu IMC está na faixa saudável (18.5-24.9)"
                imc < 30 -> "Seu IMC está acima (25-29.9), indicando sobrepeso"
                else -> "Seu IMC está muito acima (30+), indicando obesidade"
            }

            txtStatusIMC.text = status
        } catch (e: NumberFormatException) {
            Log.e("TELA_PERFIL", "Erro ao converter altura/peso para cálculo do IMC", e)
            txtIMC.text = "⚖️ IMC | 0.00"
            txtStatusIMC.text = "Dados incompletos para cálculo do IMC"
        }
    }

    private fun base64ToBitmap(base64Str: String): android.graphics.Bitmap {
        val decodedBytes = Base64.decode(base64Str, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    }
}
