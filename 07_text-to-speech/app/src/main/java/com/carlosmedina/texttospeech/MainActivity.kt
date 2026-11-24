package com.carlosmedina.texttospeech

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.Locale


class MainActivity : AppCompatActivity() {
    var tts: TextToSpeech? = null
    lateinit var editText: EditText
    lateinit var button: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        editText = findViewById(R.id.editText)
        button = findViewById(R.id.button)
        val spinner = findViewById<Spinner>(R.id.spinner)

        tts = TextToSpeech(applicationContext) { status ->
            if (status != TextToSpeech.ERROR) {
                tts!!.language = Locale.UK
            }
        }

        button.setOnClickListener {
            val toSpeak = editText.getText().toString()
            val selectedLanguage = spinner.selectedItem.toString()
            when (selectedLanguage) {
                "English" -> tts!!.language = Locale.UK
                "Spanish" -> tts!!.language = Locale.forLanguageTag("es-ES")
                "French" -> tts!!.language = Locale.FRANCE
                "Catalan" -> tts!!.language = Locale.forLanguageTag("ca-ES")
            }
            Toast.makeText(applicationContext, toSpeak, Toast.LENGTH_SHORT).show()
            tts!!.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null, null)
        }

    }

    public override fun onPause() {
        if (tts != null) {
            tts!!.stop()
            tts!!.shutdown()
        }
        super.onPause()
    }

}