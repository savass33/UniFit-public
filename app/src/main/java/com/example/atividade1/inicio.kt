package com.example.atividade1

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class inicio : AppCompatActivity() {
    lateinit var button : Button
    lateinit var layout : LinearLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_inicio)

        button = findViewById(R.id.button2)

    }

    override fun onStart() {
        super.onStart()

        button.setOnClickListener{
            var intent = Intent(this, login::class.java)
            startActivity(intent)
        }
    }

    override fun onRestart() {
        super.onRestart()
    }

    override fun onResume() {
        super.onResume()
    }


}