package com.example.atividade1

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class func_Home : AppCompatActivity() {
    private lateinit var menuBtn: ImageView
    lateinit var slctAluno: MaterialButton


    override fun onCreate(savedInstanceState: Bundle?) {
        aluno_PopMenuHelper.applyTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.func_home)
        slctAluno = findViewById(R.id.selectAlunoButton)
        menuBtn = findViewById(R.id.menuButton)
        slctAluno.setOnClickListener{
            val intent = Intent(this, func_EditaAlunos::class.java)
            startActivity(intent)
        }
        setupBottomNavigationADM.setup(this)
        menuBtn.setOnClickListener{
            func_PopupMenuHelper.mostrarPopupMenu(this, it)
        }
    }
    override fun onRestart() {
        super.onRestart()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onStart() {
        super.onStart()
    }
}