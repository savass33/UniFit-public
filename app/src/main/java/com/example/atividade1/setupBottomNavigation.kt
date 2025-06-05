package com.example.atividade1

import android.app.Activity
import android.content.Intent
import android.view.View
import android.widget.ImageView

object setupBottomNavigation {
    fun setup(activity: Activity) {
        val rootView = activity.findViewById<View>(android.R.id.content)
        val chat = rootView.findViewById<ImageView>(R.id.btnChat)
        val home = rootView.findViewById<ImageView>(R.id.btnHome)
        val notifications = rootView.findViewById<ImageView>(R.id.btnNotificacao)

        chat?.setOnClickListener {
            activity.startActivity(Intent(activity, aluno_TelaChat::class.java))
        }

        home?.setOnClickListener {
            activity.startActivity(Intent(activity, aluno_Home::class.java))
        }

        notifications?.setOnClickListener {
            activity.startActivity(Intent(activity, aluno_TelaNotificacao::class.java))
        }
    }
}
