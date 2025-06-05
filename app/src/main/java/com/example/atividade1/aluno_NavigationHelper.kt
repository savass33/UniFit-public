package com.example.atividade1

import android.app.Activity
import android.content.Intent
import android.widget.ImageView


object aluno_NavigationHelper {
    fun setup(activity: Activity) {
        val btnChat = activity.findViewById<ImageView>(R.id.btnChat)
        val btnHome = activity.findViewById<ImageView>(R.id.btnHome)
        val btnNotificacao = activity.findViewById<ImageView>(R.id.btnNotificacao)

        btnChat.setOnClickListener {
            activity.startActivity(Intent(activity, aluno_TelaChat::class.java))
        }
        btnHome.setOnClickListener {
            activity.startActivity(Intent(activity, aluno_Home::class.java))
        }
        btnNotificacao.setOnClickListener {
            activity.startActivity(Intent(activity, aluno_TelaNotificacao::class.java))
        }
    }
}