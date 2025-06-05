package com.example.atividade1

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class login : AppCompatActivity() {

    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var imgEye: ImageView
    private lateinit var btnLogin: Button
    private var isPasswordVisible = false
    private lateinit var btnCadastro: Button
    private lateinit var hyperLink: TextView
    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = Firebase.auth

        val fromLogout = intent.getBooleanExtra("fromLogout", false)

        if (fromLogout) {
            auth.signOut()
        }

        edtEmail = findViewById(R.id.edtEmail)
        edtPassword = findViewById(R.id.edtPassword)
        imgEye = findViewById(R.id.imgEye)
        btnLogin = findViewById(R.id.btnEntrar)
        btnCadastro = findViewById(R.id.btnCadastrar)
        hyperLink = findViewById(R.id.txtEsqueceuSenha)

        hyperLink.setOnClickListener {
            val intent = Intent(this, RecuperarSenha::class.java)
            startActivity(intent)
        }

        imgEye.setOnClickListener {
            togglePasswordVisibility()
        }

        btnLogin.setOnClickListener {
            val email = edtEmail.text.toString().trim()
            val password = edtPassword.text.toString().trim()

            if (validateInputs(email, password)) {
                authenticateUser(email, password)
            }
        }

        btnCadastro.setOnClickListener {
            val intent = Intent(this, TelaCadastro::class.java)
            startActivity(intent)
        }
    }

    private fun togglePasswordVisibility() {
        if (isPasswordVisible) {
            edtPassword.transformationMethod = PasswordTransformationMethod.getInstance()
            imgEye.setImageResource(R.drawable.ic_hide)
        } else {
            edtPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
            imgEye.setImageResource(R.drawable.ic_view)
        }
        isPasswordVisible = !isPasswordVisible
        edtPassword.setSelection(edtPassword.text.length)
    }

    private fun validateInputs(email: String, password: String): Boolean {
        var isValid = true

        if (email.isEmpty()) {
            edtEmail.error = "Preencha o email!"
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtEmail.error = "Email inválido!"
            isValid = false
        }

        if (password.isEmpty()) {
            edtPassword.error = "Preencha a senha!"
            isValid = false
        } else if (password.length < 6) {
            edtPassword.error = "Senha deve ter 6+ caracteres"
            isValid = false
        }

        return isValid
    }

    private fun authenticateUser(email: String, password: String) {
        val progressDialog = ProgressDialog(this).apply {
            setMessage("Fazendo login...")
            setCancelable(false)
            show()
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                progressDialog.dismiss()

                if (task.isSuccessful) {
                    val user = auth.currentUser
                    when {
                        user == null -> {
                            Toast.makeText(
                                this,
                                "Erro ao recuperar dados do usuário",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        !user.isEmailVerified -> {
                            showEmailNotVerifiedDialog(user)
                        }
                        else -> {
                            // Primeiro cria a estrutura de treinos, depois verifica o status de admin
                            createWorkoutsStructure(user.uid) {
                                checkUserAdminStatus(user.uid)
                            }
                        }
                    }
                } else {
                    handleLoginError(task.exception)
                }
            }
    }

    private fun createWorkoutsStructure(userId: String, onComplete: () -> Unit) {
        // Verificar se a estrutura já existe
        db.collection("usuarios").document(userId)
            .collection("treinos").document("A")
            .collection("exercicios").get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Se não conseguiu acessar a subcoleção ou ela não existe, vamos criar
                    if (task.exception != null || task.result?.size() == 0) {
                        createAllWorkoutCollections(userId, onComplete)
                    } else {
                        // Estrutura já existe, apenas continuar
                        onComplete()
                    }
                } else {
                    Log.w("Firestore", "Erro ao verificar estrutura de treinos", task.exception)
                    // Tentar criar mesmo com erro na verificação
                    createAllWorkoutCollections(userId, onComplete)
                }
            }
    }

    private fun createAllWorkoutCollections(userId: String, onComplete: () -> Unit) {
        val batch = db.batch()

        val initialTreinoData = hashMapOf(
            "inicializadoEm" to FieldValue.serverTimestamp(),
            "tipo" to "estrutura_treino"
        )

        // Referência para treino A com exercício "Supino Reto"
        val treinoARef = db.collection("usuarios").document(userId)
            .collection("treinos").document("A")
        batch.set(treinoARef, initialTreinoData)

        val exercicioA = hashMapOf(
            "nome" to "Supino Reto",
            "repeticoes" to "3x12"
        )
        batch.set(treinoARef.collection("exercicios").document("supino_reto"), exercicioA)

        // Referência para treino B com exercício "Puxada Alta"
        val treinoBRef = db.collection("usuarios").document(userId)
            .collection("treinos").document("B")
        batch.set(treinoBRef, initialTreinoData)

        val exercicioB = hashMapOf(
            "nome" to "Puxada Alta",
            "repeticoes" to "4x10"
        )
        batch.set(treinoBRef.collection("exercicios").document("puxada_alta"), exercicioB)

        // Referência para treino C com exercício "Leg Press 45"
        val treinoCRef = db.collection("usuarios").document(userId)
            .collection("treinos").document("C")
        batch.set(treinoCRef, initialTreinoData)

        val exercicioC = hashMapOf(
            "nome" to "Leg Press 45",
            "repeticoes" to "4x15"
        )
        batch.set(treinoCRef.collection("exercicios").document("leg_press_45"), exercicioC)

        // Enviar todas as operações de uma vez
        batch.commit()
            .addOnSuccessListener {
                Log.d("Firestore", "Treinos e exercícios criados com sucesso")
                onComplete()
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Erro ao criar estrutura de treinos", e)
                onComplete()
            }
    }


    private fun removeInitializationDocs(userId: String, onComplete: () -> Unit) {
        val batch = db.batch()

        // Remover documentos de inicialização das subcoleções de exercícios
        val treinos = listOf("A", "B", "C")
        treinos.forEach { treino ->
            val docRef = db.collection("usuarios").document(userId)
                .collection("treinos").document(treino)
                .collection("exercicios").document("inicial")
            batch.delete(docRef)
        }

        batch.commit()
            .addOnSuccessListener {
                Log.d("Firestore", "Documentos de inicialização removidos")
                onComplete()
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Erro ao remover documentos de inicialização", e)
                onComplete()
            }
    }

    private fun showEmailNotVerifiedDialog(user: FirebaseUser) {
        AlertDialog.Builder(this)
            .setTitle("Email Não Verificado")
            .setMessage("Seu email ainda não foi verificado. Deseja reenviar o link de verificação?")
            .setPositiveButton("Reenviar") { _, _ ->
                sendVerificationEmail(user)
            }
            .setNegativeButton("Sair") { _, _ ->
                auth.signOut()
            }
            .setCancelable(false)
            .show()
    }

    private fun sendVerificationEmail(user: FirebaseUser) {
        val progressDialog = ProgressDialog(this).apply {
            setMessage("Enviando email de verificação...")
            setCancelable(false)
            show()
        }

        user.sendEmailVerification()
            .addOnCompleteListener { task ->
                progressDialog.dismiss()
                if (task.isSuccessful) {
                    Toast.makeText(
                        this,
                        "Email de verificação reenviado para ${user.email}",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(
                        this,
                        "Falha ao reenviar email: ${task.exception?.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
                auth.signOut()
            }
    }

    private fun handleLoginError(exception: Exception?) {
        val errorMessage = when (exception) {
            is FirebaseAuthInvalidUserException -> "Email não cadastrado"
            is FirebaseAuthInvalidCredentialsException -> "Senha incorreta"
            else -> "Erro no login: ${exception?.message ?: "Erro desconhecido"}"
        }

        Log.e("LOGIN_ERROR", errorMessage, exception)
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
    }

    private fun checkUserAdminStatus(userId: String?) {
        if (userId == null) {
            Toast.makeText(this, "Usuário não encontrado", Toast.LENGTH_SHORT).show()
            return
        }

        db.collection("usuarios").document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val isAdmin = document.getBoolean("isAdmin") ?: false
                    redirectUser(isAdmin)
                } else {
                    redirectUser(false)
                }
            }
            .addOnFailureListener { e ->
                Log.e("FIRESTORE_ERROR", "Erro ao verificar status", e)
                redirectUser(false)
            }
    }

    private fun redirectUser(isAdmin: Boolean) {
        Toast.makeText(this, "Login bem-sucedido!", Toast.LENGTH_SHORT).show()
        val intent = if (isAdmin) {
            Intent(this, func_EditaAlunos::class.java)
        } else {
            Intent(this, aluno_Home::class.java)
        }
        startActivity(intent)
        finish()
    }
}