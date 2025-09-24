package com.carlosmedina.andrevina

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    companion object {
        var numObjectiu = Random.nextInt(1, 100)
        var intents = 0
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<Button>(R.id.endevina)
            .setOnClickListener {
                val numIntent = findViewById<EditText>(R.id.entradaText).text.toString().toInt()
                if (numObjectiu > numIntent) {
                    Toast.makeText(this, "El número que busques és major.", Toast.LENGTH_SHORT).show()
                    findViewById<TextView>(R.id.textHistorial).text = findViewById<TextView>(R.id.textHistorial).text.toString() + "\n" + numIntent.toString() + ": Major"
                } else if (numObjectiu < numIntent) {
                    Toast.makeText(this, "El número que busques és menor.", Toast.LENGTH_SHORT).show()
                    findViewById<TextView>(R.id.textHistorial).text = findViewById<TextView>(R.id.textHistorial).text.toString() + "\n" + numIntent.toString() + ": Menor"
                } else {
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("Enhorabona!")
                    builder.setMessage("Has endevinat el número!")
                    builder.setPositiveButton("OK") { dialog, which ->
                        numObjectiu = Random.nextInt(1, 100)
                        findViewById<TextView>(R.id.textHistorial).text = ""
                        intents = 0
                        dialog.dismiss()
                    }
                    val dialog = builder.create()
                    dialog.show()
                }
                intents++
                findViewById<EditText>(R.id.entradaText).text.clear()
                findViewById<TextView>(R.id.comptadorIntents).text = "Intents: " + intents.toString()
            }
    }
}