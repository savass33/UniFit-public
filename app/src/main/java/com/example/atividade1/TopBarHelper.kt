package com.example.atividade1

import android.app.Activity
import android.view.View
import android.widget.ImageView
import android.widget.TextView



object TopBarHelper {
    fun setup(activity: Activity, nomePerfil: String, onMenuClick: (view: ImageView) -> Unit) {
        val nomeTextView = activity.findViewById<TextView>(R.id.txtPerfil)
        val menuIcon = activity.findViewById<ImageView>(R.id.menuIcon)

        nomeTextView?.text = nomePerfil

        menuIcon?.setOnClickListener {
            onMenuClick(menuIcon)
        }
    }
}