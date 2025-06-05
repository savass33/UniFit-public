package com.example.atividade1

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.*
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class aluno_AgendarConsulta : AppCompatActivity() {

    private lateinit var spinnerHorarios: Spinner
    private lateinit var btnConfirmar: Button
    private lateinit var db: FirebaseFirestore

    private lateinit var inputNome: EditText
    private lateinit var inputEmail: EditText
    private lateinit var inputCPF: EditText
    private lateinit var inputTelefone: EditText
    private lateinit var menuBtn: ImageView

    private var listaHorariosDisponiveis: List<String> = emptyList()
    private var horarioSelecionado: String? = null
    private var funcionarioId: String? = null
    private var funcionarioNome: String? = null

    private val CHANNEL_ID = "agendamento_channel"
    private val NOTIFICATION_ID = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.aluno_agendarconsulta)

        db = FirebaseFirestore.getInstance()
        setupBottomNavigation.setup(this)

        spinnerHorarios = findViewById(R.id.spinnerHorarios)
        btnConfirmar = findViewById(R.id.agendarConsulta)
        inputNome = findViewById(R.id.inputNome)
        inputEmail = findViewById(R.id.inputEmail)
        inputCPF = findViewById(R.id.inputCPF)
        inputTelefone = findViewById(R.id.inputTelefone)
        menuBtn = findViewById(R.id.menuIcon)

        funcionarioId = intent.getStringExtra("funcionarioId")
        funcionarioNome = intent.getStringExtra("funcionarioNome")
        listaHorariosDisponiveis = intent.getStringArrayListExtra("horariosDisponiveis") ?: emptyList()

        menuBtn.setOnClickListener {
            aluno_PopMenuHelper.mostrarPopupMenu(this, it)
        }

        if (listaHorariosDisponiveis.isEmpty()) {
            Toast.makeText(this, "Funcionário não possui horários disponíveis.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        solicitarPermissaoNotificacao()
        createNotificationChannel()
        setupSpinnerHorarios()
        setupListeners()
    }

    private fun solicitarPermissaoNotificacao() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    1001
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1001) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permissão para notificações concedida", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permissão para notificações negada", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupSpinnerHorarios() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, listaHorariosDisponiveis)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerHorarios.adapter = adapter

        spinnerHorarios.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                horarioSelecionado = listaHorariosDisponiveis[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                horarioSelecionado = null
            }
        }
    }

    private fun setupListeners() {
        btnConfirmar.setOnClickListener {
            if (funcionarioId.isNullOrEmpty() || funcionarioNome.isNullOrEmpty()) {
                Toast.makeText(this, "Funcionário inválido.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (horarioSelecionado == null) {
                Toast.makeText(this, "Selecione um horário", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val nome = inputNome.text.toString().trim()
            val email = inputEmail.text.toString().trim()
            val cpf = inputCPF.text.toString().trim()
            val telefone = inputTelefone.text.toString().trim()

            if (nome.isEmpty() || email.isEmpty() || cpf.isEmpty() || telefone.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            salvarAgendamento(nome, email, cpf, telefone)
        }
    }

    private fun salvarAgendamento(nomeUser: String, emailUser: String, cpfUser: String, telefoneUser: String) {
        db.collection("agendamentos")
            .whereEqualTo("funcionarioId", funcionarioId)
            .whereEqualTo("horario", horarioSelecionado)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty) {
                    val agendamento = hashMapOf(
                        "funcionarioId" to funcionarioId,
                        "funcionarioNome" to funcionarioNome,
                        "horario" to horarioSelecionado,
                        "nomeUsuario" to nomeUser,
                        "emailUsuario" to emailUser,
                        "cpfUsuario" to cpfUser,
                        "telefoneUsuario" to telefoneUser
                    )

                    db.collection("agendamentos")
                        .add(agendamento)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Consulta agendada com sucesso!", Toast.LENGTH_SHORT).show()
                            showNotification("Consulta agendada", "Sua consulta com $funcionarioNome às $horarioSelecionado foi agendada.")
                            finish()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Erro ao agendar consulta", Toast.LENGTH_SHORT).show()
                        }

                    val usuarioId = FirebaseAuth.getInstance().currentUser?.uid

                    val notificacaoUsuario = hashMapOf(
                        "titulo" to "Consulta agendada",
                        "mensagem" to "Sua consulta com $funcionarioNome às $horarioSelecionado foi agendada.",
                        "timestamp" to com.google.firebase.Timestamp.now(),
                        "lida" to false
                    )

                    db.collection("usuarios").document(usuarioId!!)
                        .collection("notificacoes")
                        .add(notificacaoUsuario)


                    val notiConsultaADM = hashMapOf(
                        "nomeAluno" to nomeUser,
                        "mensagem" to "📅 $nomeUser marcou uma consulta para o dia $horarioSelecionado",
                        "timestamp" to System.currentTimeMillis(),
                        "tipo" to "consulta"
                    )

                    db.collection("notificacoes")
                        .add(notiConsultaADM)
                        .addOnSuccessListener {
                            Log.d("NotificacaoService", "Notificação de consulta enviada com sucesso para ADM.")
                        }
                        .addOnFailureListener {
                            Log.e("NotificacaoService", "Erro ao salvar notificação de consulta", it)
                        }

                } else {
                    Toast.makeText(this, "Este horário já está ocupado para esse funcionário.", Toast.LENGTH_LONG).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao verificar disponibilidade", Toast.LENGTH_SHORT).show()
            }
    }


    private fun notificarFuncionarios(nome: String) {
        db.collection("usuarios")
            .whereEqualTo("tipo", "funcionario")
            .get()
            .addOnSuccessListener { admins: QuerySnapshot ->
                for (adminDoc in admins) {
                    val adminId = adminDoc.id
                    val notificacaoADM = hashMapOf(
                        "titulo" to "Nova consulta marcada",
                        "mensagem" to "O usuário $nome marcou uma consulta pro dia $horarioSelecionado",
                        "timestamp" to com.google.firebase.Timestamp.now(),
                        "tipo" to "consulta"
                    )
                    db.collection("usuarios")
                        .document(adminId)
                        .collection("notificacoes")
                        .add(notificacaoADM)
                }
            }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Agendamentos"
            val descriptionText = "Notificações de agendamentos"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showNotification(title: String, content: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                return
            }
        }

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.unifit)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(this)) {
            notify(NOTIFICATION_ID, builder.build())
        }
    }
}
