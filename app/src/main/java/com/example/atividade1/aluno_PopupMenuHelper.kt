package com.example.atividade1

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate

object aluno_PopMenuHelper {

    private const val PREFS_NAME = "ThemePrefs"
    private const val KEY_THEME = "SelectedTheme"

    // Theme modes constants matching AppCompatDelegate.MODE_NIGHT_*
    const val THEME_LIGHT = AppCompatDelegate.MODE_NIGHT_NO
    const val THEME_DARK = AppCompatDelegate.MODE_NIGHT_YES
    const val THEME_SYSTEM = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM

    // Apply the saved theme or default system theme on app start
    // Esta função deve ser chamada na inicialização do App ou da Activity base
    fun applyTheme(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val selectedTheme = prefs.getInt(KEY_THEME, THEME_SYSTEM) // Default to System
        AppCompatDelegate.setDefaultNightMode(selectedTheme)
    }

    private fun saveThemePreference(context: Context, themeMode: Int) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putInt(KEY_THEME, themeMode).apply()
    }

    private fun getCurrentTheme(context: Context): Int {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getInt(KEY_THEME, THEME_SYSTEM)
    }

    fun mostrarPopupMenu(activity: Activity, anchorView: View) {
        val popupMenu = PopupMenu(activity, anchorView)
        // Assumindo que o menu XML é o mesmo (R.menu.menu_popup)
        // Se houver um menu específico para aluno, altere R.menu.menu_popup aqui
        popupMenu.menuInflater.inflate(R.menu.menu_popup, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.menu_perfil -> {
                    // Verificar se TelaPerfil é a correta para aluno
                    activity.startActivity(Intent(activity, TelaPerfil::class.java))
                    true // Indicate the click was handled
                }
                R.id.menu_treinos -> {
                    // Implementar navegação para tela de treinos do aluno se existir
                    Toast.makeText(activity, "Treinos selecionado", Toast.LENGTH_SHORT).show()
                    true // Indicate the click was handled
                }
                R.id.menu_bot -> {
                    // Verificar se aluno_TelaChat é a classe correta
                    activity.startActivity(Intent(activity, aluno_TelaChat::class.java)) // Usando TelaChat padrão, ajustar se necessário
                    true // Indicate the click was handled
                }
                R.id.contatos -> {
                    // Verificar se aluno_Contato é a classe correta
                    activity.startActivity(Intent(activity, aluno_Contato::class.java)) // Usando AlunoContato padrão, ajustar se necessário
                    true // Indicate the click was handled
                }
                R.id.menu_notificacoes -> {
                    // Verificar se aluno_TelaNotificacao é a classe correta
                    activity.startActivity(Intent(activity, aluno_TelaNotificacao::class.java)) // Usando TelaNotificacao padrão, ajustar se necessário
                    true // Indicate the click was handled
                }
                R.id.menu_dark_mode -> {
                    val currentTheme = getCurrentTheme(activity)
                    val nextTheme = when (currentTheme) {
                        THEME_LIGHT -> THEME_DARK
                        THEME_DARK -> THEME_SYSTEM
                        else -> THEME_LIGHT // Includes THEME_SYSTEM or any unexpected value
                    }
                    saveThemePreference(activity, nextTheme)
                    AppCompatDelegate.setDefaultNightMode(nextTheme)
                    // Force activity recreation to apply the theme immediately
                    activity.recreate()
                    true // Indicate the click was handled
                }
                R.id.menu_calendario -> {
                    // Verificar se aluno_TelaCalendario é a classe correta
                    activity.startActivity(Intent(activity, aluno_TelaCalendario::class.java)) // Usando TelaCalendario padrão, ajustar se necessário
                    true // Indicate the click was handled
                }
                R.id.menu_sair -> {
                    // Verificar se login é a classe correta para logout do aluno
                    activity.startActivity(Intent(activity, inicio::class.java)) // Usando Inicio padrão, ajustar se necessário
                    activity.finishAffinity() // Close all activities in the task
                    true // Indicate the click was handled
                }
                else -> false // Indicate the click was not handled
            }
        }

        popupMenu.show()
    }
}