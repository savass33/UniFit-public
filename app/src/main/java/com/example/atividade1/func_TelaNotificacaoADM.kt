package com.example.atividade1

import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class func_TelaNotificacaoADM : AppCompatActivity() {

    private lateinit var recycler: RecyclerView
    private lateinit var adapter: NotificacaoAdapter
    private val lista = mutableListOf<Notificacao>()
    private lateinit var menuBtn: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.func_notificacoes)
        setupBottomNavigationADM.setup(this)
        menuBtn = findViewById(R.id.menuIcon)
        recycler = findViewById(R.id.recyclerViewNotificacoes)
        recycler.layoutManager = LinearLayoutManager(this)
        adapter = NotificacaoAdapter(lista)
        recycler.adapter = adapter
        menuBtn.setOnClickListener {
            func_PopupMenuHelper.mostrarPopupMenu(this, it)
        }

        carregarNotificacoes()
    }

    private fun carregarNotificacoes() {
        val db = FirebaseFirestore.getInstance()
        db.collection("notificacoes")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                lista.clear()
                for (doc in result) {
                    val notificacao = doc.toObject(Notificacao::class.java)
                    lista.add(notificacao)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao carregar notificações", Toast.LENGTH_SHORT).show()
            }
    }
}
