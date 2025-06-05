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

object func_PopupMenuHelper {

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
        // Usando o menu específico para ADM (R.menu.menuadm_popup)
        popupMenu.menuInflater.inflate(R.menu.menuadm_popup, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                // Ajustar as classes de destino se os nomes func_... não existirem ou forem diferentes
                R.id.menu_bot -> {
                    // Verificar se TelaChatBotADM é a classe correta
                    activity.startActivity(Intent(activity, func_TelaChatBot::class.java))
                    // activity.finish() // Considerar se deve finalizar ou não
                    true // Indicate the click was handled
                }
                R.id.menu_notificacoes -> {
                    // Verificar se TelaNotificacaoADM é a classe correta
                    activity.startActivity(Intent(activity, func_TelaNotificacaoADM::class.java))
                    // activity.finish()
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
                R.id.menu_sair -> {
                    // Verificar se login é a classe correta para logout do funcionário/ADM
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
