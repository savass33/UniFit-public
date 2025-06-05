package com.example.atividade1

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.launch

class func_TelaChatBot : AppCompatActivity() {

    private lateinit var menuBtn: ImageView
    private lateinit var btnVoltar: ImageView
    private lateinit var sendButton: ImageView
    private lateinit var messageInput: EditText
    private lateinit var chatContainer: LinearLayout
    private lateinit var generativeModel: GenerativeModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.func_chatbot)

        // Inicializa os componentes de navegação
        menuBtn = findViewById(R.id.menuButton)
        btnVoltar = findViewById(R.id.backButton)

        // Inicializa os componentes do chat
        sendButton = findViewById(R.id.sendButton)
        messageInput = findViewById(R.id.messageInput)
        chatContainer = findViewById(R.id.chatContainer)

        // Navegação

        menuBtn.setOnClickListener {
            aluno_PopMenuHelper.mostrarPopupMenu(this, it)
        }

        btnVoltar.setOnClickListener {
            onBackPressed()
        }

        // Inicializa modelo de IA
        generativeModel = GenerativeModel(
            modelName = "gemini-2.0-flash",
            apiKey = "chave-api-aqui"
        )

        // Saudação inicial
        showBotMessage("Olá! Como posso te ajudar hoje?")

        // Envio de mensagem
        sendButton.setOnClickListener {
            val userMessage = messageInput.text.toString().trim()
            if (userMessage.isNotEmpty()) {
                showUserMessage(userMessage)
                messageInput.setText("")
                getBotResponse(userMessage)
            }
        }
    }

    private fun showUserMessage(text: String) {
        // Inflate the layout
        val bubbleView = layoutInflater.inflate(R.drawable.bubble_user, chatContainer, false)

        // Access the TextView and update the text
        val userMessageTextView: TextView = bubbleView.findViewById(R.id.userMessage)
        userMessageTextView.text = text

        // Add the inflated view to the container
        chatContainer.addView(bubbleView)
    }


    private fun showBotMessage(text: String) {
        // Infla o layout
        val bubbleView = layoutInflater.inflate(R.drawable.bubble_bot, chatContainer, false)

        // Acessa o TextView e atualiza o texto
        val botMessageTextView: TextView = bubbleView.findViewById(R.id.botMessage)
        botMessageTextView.text = text

        // Adiciona a view inflada ao container
        chatContainer.addView(bubbleView)
    }


    private fun getBotResponse(prompt: String) {
        lifecycleScope.launch {
            try {
                val response = generativeModel.generateContent(prompt)
                val output = response.text ?: "Desculpe, não entendi."
                showBotMessage(output)
                Log.d("IA-Resposta", output)
            } catch (e: Exception) {
                Log.e("IA-Erro", e.toString())
                showBotMessage("Erro ao se comunicar com a IA.")
            }
        }
    }
}
