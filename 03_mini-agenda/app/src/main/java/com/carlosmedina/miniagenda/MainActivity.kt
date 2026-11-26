package com.carlosmedina.miniagenda

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.io.IOException

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<Button>(R.id.buttonSave)
            .setOnClickListener {
                val name = findViewById<EditText>(R.id.fieldName).text.toString()
                val surnames = findViewById<EditText>(R.id.fieldSurnames).text.toString()
                val phone = findViewById<EditText>(R.id.fieldPhone).text.toString()
                val email = findViewById<EditText>(R.id.fieldEmail).text.toString()

                val filename = "contactes.txt"
                val data = listOf<String>(name, surnames, phone, email)
                for (item in data) {
                    if (item.isEmpty()) {
                        Toast.makeText(this, "Si us plau empleni tots els camps.", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                }
                val line = data.joinToString("; ")
                saveToFile(filename, line)
            }
    }

    private fun saveToFile(filename: String, line: String) {
        try {
            openFileOutput(filename, Context.MODE_APPEND).use { outputStream ->
                outputStream.write(line.toByteArray())
                Toast.makeText(this, "Contacte desat", Toast.LENGTH_SHORT).show()
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Error d'escriptura", Toast.LENGTH_LONG).show()
        }
    }


}