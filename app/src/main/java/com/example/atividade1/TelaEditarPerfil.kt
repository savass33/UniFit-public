    package com.example.atividade1

    import android.content.Intent
    import android.graphics.Bitmap
    import android.graphics.BitmapFactory
    import android.net.Uri
    import android.os.Bundle
    import android.util.Log
    import android.widget.*
    import androidx.appcompat.app.AppCompatActivity
    import androidx.core.graphics.drawable.toBitmap
    import com.google.firebase.auth.FirebaseAuth
    import com.google.firebase.auth.UserProfileChangeRequest
    import com.google.firebase.firestore.FirebaseFirestore
    import kotlinx.coroutines.CoroutineStart
    import java.io.ByteArrayOutputStream
    import java.util.Calendar
    import kotlin.io.encoding.Base64
    import kotlin.io.encoding.Base64.Default.decode
    import kotlin.io.encoding.Base64.Default.encode
    import kotlin.io.encoding.ExperimentalEncodingApi

    class TelaEditarPerfil : AppCompatActivity() {

        private lateinit var btnVoltar: ImageView
        private lateinit var btnSalvar: Button
        private lateinit var imgPerfil: ImageView
        private lateinit var edtNome: EditText
        private lateinit var edtEmail: EditText
        private lateinit var edtCpf: EditText
        private lateinit var edtIdade: EditText
        private lateinit var edtPeso: EditText
        private lateinit var edtAltura: EditText
        private lateinit var spinnerGenero: Spinner
        private lateinit var spinnerDia: Spinner
        private lateinit var spinnerMes: Spinner
        private lateinit var spinnerAno: Spinner
         var stringimage :String = ""
        private val auth = FirebaseAuth.getInstance()
        private val db = FirebaseFirestore.getInstance()

        override fun onCreate(savedInstanceState: Bundle?) {
            onRestart()
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_editar_perfil)

            initComponents()
            setupListeners()
            loadUserData()
        }

        private fun initComponents() {
            btnVoltar = findViewById(R.id.btnVoltar)
            btnSalvar = findViewById(R.id.btnSalvar)
            imgPerfil = findViewById(R.id.imgPerfil)
            edtNome = findViewById(R.id.edtNome)
            edtEmail = findViewById(R.id.edtEmail)
            edtCpf = findViewById(R.id.edtCpf)
            edtIdade = findViewById(R.id.edtIdade)
            edtPeso = findViewById(R.id.edtPeso)
            edtAltura = findViewById(R.id.edtAltura)
            spinnerGenero = findViewById(R.id.spinnerGenero)
            spinnerDia = findViewById(R.id.spinnerDia)
            spinnerMes = findViewById(R.id.spinnerMes)
            spinnerAno = findViewById(R.id.spinnerAno)

            // Configurar spinner de gênero
            ArrayAdapter.createFromResource(
                this,
                R.array.generos_array,
                android.R.layout.simple_spinner_item
            ).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerGenero.adapter = adapter
            }

            // Configurar spinners de data (exemplo com dias)
            val dias = (1..31).map { it.toString() }
            ArrayAdapter(this, android.R.layout.simple_spinner_item, dias).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerDia.adapter = adapter
            }

            // Configurar meses
            val meses = resources.getStringArray(R.array.mes_array)
            ArrayAdapter(this, android.R.layout.simple_spinner_item, meses).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerMes.adapter = adapter
            }

            // Configurar anos (últimos 100 anos)
            val anoAtual = Calendar.getInstance().get(Calendar.YEAR)
            val anos = (anoAtual - 100..anoAtual).reversed().map { it.toString() }
            ArrayAdapter(this, android.R.layout.simple_spinner_item, anos).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerAno.adapter = adapter
            }
        }

        private fun setupListeners() {
            btnVoltar.setOnClickListener {
                onBackPressed()
            }

            btnSalvar.setOnClickListener {
                saveUserData()
            }

            imgPerfil.setOnClickListener {
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                startActivityForResult(intent, PICK_IMAGE_REQUEST)
            }
        }

        private fun loadUserData() {
            val user = auth.currentUser
            user?.let {
                // Carregar dados básicos do Firebase Auth
                edtNome.setText(it.displayName ?: "")
                edtEmail.setText(it.email ?: "")

                // Carregar dados adicionais do Firestore
                db.collection("usuarios").document(it.uid).get()
                    .addOnSuccessListener { document ->
                        if (document != null && document.exists()) {
                            edtCpf.setText(document.getString("cpf") ?: "")
                            edtIdade.setText(document.getString("idade") ?: "")
                            edtPeso.setText(document.getString("peso") ?: "")
                            edtAltura.setText(document.getString("altura") ?: "")
                            val imageBase64 = document.getString("image")
                            if (!imageBase64.isNullOrEmpty()) {
                                imgPerfil.setImageBitmap(base64ToBitmap(imageBase64))
                            }

                            // Definir valores dos spinners
                            setSpinnerValue(spinnerGenero, document.getString("genero") ?: "")

                            // Configurar data de nascimento se existir
                            val dataNascimento = document.getString("dataNascimento")
                            if (!dataNascimento.isNullOrEmpty()) {
                                val partes = dataNascimento.split("/")
                                if (partes.size == 3) {
                                    setSpinnerValue(spinnerDia, partes[0])
                                    setSpinnerValue(spinnerMes, partes[1])
                                    setSpinnerValue(spinnerAno, partes[2])
                                }
                            }
                        }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Erro ao carregar dados: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }

        private fun setSpinnerValue(spinner: Spinner, value: String) {
            val adapter = spinner.adapter as ArrayAdapter<*>
            for (i in 0 until adapter.count) {
                if (adapter.getItem(i).toString() == value) {
                    spinner.setSelection(i)
                    break
                }
            }
        }

        private fun saveUserData() {
            val user = auth.currentUser
            user?.let {
                // Validar campos obrigatórios
                if (edtNome.text.toString().isEmpty()) {
                    edtNome.error = "Nome é obrigatório"
                    return
                }

                if (edtEmail.text.toString().isEmpty()) {
                    edtEmail.error = "E-mail é obrigatório"
                    return
                }

                // Atualizar dados básicos no Firebase Auth
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(edtNome.text.toString())
                    .build()

                it.updateProfile(profileUpdates)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Atualizar email se foi alterado
                            if (edtEmail.text.toString() != it.email) {
                                it.updateEmail(edtEmail.text.toString())
                                    .addOnCompleteListener { emailTask ->
                                        if (emailTask.isSuccessful) {
                                            updateFirestoreData(it.uid)
                                        } else {
                                            Toast.makeText(this, "Erro ao atualizar email: ${emailTask.exception?.message}", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                            } else {
                                updateFirestoreData(it.uid)
                            }
                        } else {
                            Toast.makeText(this, "Erro ao atualizar perfil: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            } ?: run {
                Toast.makeText(this, "Usuário não autenticado", Toast.LENGTH_SHORT).show()
            }
        }

        private fun updateFirestoreData(uid: String) {
            // Criar objeto com todos os dados do perfil
            val userData = hashMapOf(
                "nome" to edtNome.text.toString(),
                "email" to edtEmail.text.toString(),
                "cpf" to edtCpf.text.toString(),
                "idade" to edtIdade.text.toString(),
                "genero" to spinnerGenero.selectedItem.toString(),
                "peso" to edtPeso.text.toString(),
                "altura" to edtAltura.text.toString(),
                "image" to stringimage,
                "dataNascimento" to "${spinnerDia.selectedItem}/${spinnerMes.selectedItem}/${spinnerAno.selectedItem}",
                "ultimaAtualizacao" to System.currentTimeMillis()
            )

            db.collection("usuarios").document(uid)
                .set(userData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Perfil atualizado com sucesso!", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Erro ao salvar dados: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)
            if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
                val imageUri: Uri = data.data!!
                imgPerfil.setImageURI(imageUri)
                stringimage = bitmapToBase64(imgPerfil.drawable.toBitmap())

            }
        }

        companion object {
            private const val PICK_IMAGE_REQUEST = 1
        }

        @OptIn(ExperimentalEncodingApi::class)
        private fun bitmapToBase64(bitmap: Bitmap): String {
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)
            val byteArray = outputStream.toByteArray()
            return encode(byteArray,0,byteArray.size)
        }
        @OptIn(ExperimentalEncodingApi::class)
        private fun base64ToBitmap(base64Str: String): Bitmap {

            val decodedBytes = decode(base64Str)
            return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        }
    }