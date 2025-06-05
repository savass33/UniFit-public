package com.example.atividade1

import android.app.Activity
import android.content.Intent
import android.view.View
import android.widget.ImageView

object setupBottomNavigationADM {
    fun setup(activity: Activity) {
        val rootView = activity.findViewById<View>(android.R.id.content)
        val chat = rootView.findViewById<ImageView>(R.id.btnChatADM)
        val home = rootView.findViewById<ImageView>(R.id.btnHomeADM)
        val notifications = rootView.findViewById<ImageView>(R.id.btnNotificacaoADM)
        val search = rootView.findViewById<ImageView>(R.id.btnSearchADM)

        chat?.setOnClickListener {
            activity.startActivity(Intent(activity, func_TelaChatBot::class.java))
        }

        home?.setOnClickListener {
            activity.startActivity(Intent(activity, func_EditaAlunos::class.java))
        }

        notifications?.setOnClickListener {
            activity.startActivity(Intent(activity, func_TelaNotificacaoADM::class.java))
        }
        search?.setOnClickListener {
            activity.startActivity(Intent(activity, func_EditaAlunos::class.java))
        }
    }
}
