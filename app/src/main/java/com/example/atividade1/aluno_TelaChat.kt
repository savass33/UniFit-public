    package com.example.atividade1

    import android.os.Bundle
    import android.util.Log
    import android.widget.EditText
    import android.widget.ImageView
    import android.widget.LinearLayout
    import android.widget.TextView
    import androidx.appcompat.app.AppCompatActivity
    import androidx.lifecycle.lifecycleScope
    import com.google.ai.client.generativeai.GenerativeModel
    import kotlinx.coroutines.launch

    class aluno_TelaChat : AppCompatActivity() {

        private lateinit var sendButton: ImageView
        private lateinit var messageInput: EditText
        private lateinit var chatContainer: LinearLayout
        private lateinit var generativeModel: GenerativeModel

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.aluno_chat)

            // Inicializa os componentes do chat
            sendButton = findViewById(R.id.sendButton)
            messageInput = findViewById(R.id.messageInput)
            chatContainer = findViewById(R.id.chatContainer)

            findViewById<ImageView>(R.id.backButton).setOnClickListener {
                onBackPressed()
            }

            findViewById<ImageView>(R.id.menuButton).setOnClickListener {
                aluno_PopMenuHelper.mostrarPopupMenu(this, it)
            }

            generativeModel = GenerativeModel(
                modelName = "gemini-2.0-flash",
                apiKey = "chave-api-aqui"
            )

            // Saudação inicial
            showBotMessage("Olá! Como posso te ajudar hoje?")

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
