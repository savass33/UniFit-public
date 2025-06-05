package com.example.atividade1

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class aluno_TelaNotificacao : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: NotificacaoAdapter
    private lateinit var menuBtn: ImageView

    private val listaNotificacoes = mutableListOf<Notificacao>()
    private var listenerNotificacoes: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.aluno_notificacao)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        setupBottomNavigation.setup(this)
        menuBtn = findViewById(R.id.menuIcon)
        recyclerView = findViewById(R.id.recyclerViewNotificacoes)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = NotificacaoAdapter(listaNotificacoes)
        recyclerView.adapter = adapter

        menuBtn.setOnClickListener {
            aluno_PopMenuHelper.mostrarPopupMenu(this, it)
        }

    }

    override fun onStart() {
        super.onStart()
        val userId = auth.currentUser?.uid ?: return

        listenerNotificacoes = db.collection("usuarios").document(userId)
            .collection("notificacoes")
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener

                listaNotificacoes.clear()

                if (snapshot != null && !snapshot.isEmpty) {
                    for (doc in snapshot.documents) {
                        doc.toObject(Notificacao::class.java)?.let { listaNotificacoes.add(it) }
                    }
                } else {
                    listaNotificacoes.add(
                        Notificacao(
                            nomeAluno = "",
                            mensagem = "Nenhuma notificação disponível",
                            timestamp = System.currentTimeMillis()
                        )
                    )
                }

                adapter.notifyDataSetChanged()
            }
    }

    override fun onStop() {
        super.onStop()
        listenerNotificacoes?.remove()
    }
}
