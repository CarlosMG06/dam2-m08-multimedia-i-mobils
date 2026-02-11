package com.carlosmedina.galeria

import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    val getContent = registerForActivityResult(GetContent()) { uri: Uri? ->
        val imageView = findViewById<ImageView>(R.id.imageView)
        if (uri != null) {
            // Mostrar imatge seleccionada
            imageView.setImageURI(uri)
        } else {
            Toast.makeText(
                this, "No s'ha pogut carregar la imatge.",
                Toast.LENGTH_SHORT).show()
        }
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

        val selectButton = findViewById<Button>(R.id.selectButton)

        selectButton.setOnClickListener {
            // Input: imatge (Obrir galeria)
            getContent.launch("image/*")
        }
    }
}