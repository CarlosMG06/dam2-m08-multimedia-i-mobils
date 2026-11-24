package com.carlosmedina.listilla

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    // Model: ArrayList de Record (intents=puntuació, nom)
    class Record(var intents: Int, var nom: String)
    var records: ArrayList<Record> = ArrayList<Record>()

    // ArrayAdapter serà l'intermediari amb la ListView
    lateinit var adapter: ArrayAdapter<Record>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Afegim alguns exemples
        records.add(Record(33, "Manolo"))
        records.add(Record(12, "Pepe"))
        records.add(Record(42, "Laura"))

        // Inicialitzem l'ArrayAdapter amb el layout pertinent
        adapter = object : ArrayAdapter<Record>(this,R.layout.list_item,records)
        {
            override fun getView(pos: Int, convertView: View?, container: ViewGroup): View {
                // getView ens construeix el layout i hi "pinta" els valors de l'element en la posició pos
                var convertView = convertView
                if (convertView == null) {
                    // inicialitzem l'element la View amb el seu layout
                    convertView = layoutInflater.inflate(R.layout.list_item, container, false)
                }
                // pintem imatge
                val bitmap = BitmapFactory.decodeStream( assets.open("ieti_logo.png") )
                convertView.findViewById<ImageView>(R.id.imageView).setImageBitmap( bitmap )
                // "Pintem" valors (quan es refresca)
                convertView.findViewById<TextView>(R.id.nom).text = getItem(pos)?.nom
                convertView.findViewById<TextView>(R.id.intents).text = getItem(pos)?.intents.toString()
                return convertView
            }
        }

        // busquem la ListView i li endollem l'ArrayAdapter
        val lv = findViewById<ListView>(R.id.recordsView)
        lv.setAdapter(adapter)

        // Botó per afegir un nou rècord
        val buttonAdd = findViewById<Button>(R.id.buttonAdd)
        buttonAdd.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            val inflater = layoutInflater
            val dialogView = inflater.inflate(R.layout.dialog_add, null)
            builder.setView(dialogView)
                .setPositiveButton("OK", DialogInterface.OnClickListener { dialog, id ->
                    val nom = dialogView.findViewById<EditText>(R.id.inputNom).text.toString()
                    val intents = dialogView.findViewById<EditText>(R.id.inputIntents).text.toString().toInt()
                    records.add(Record(intents, nom))
                    adapter.notifyDataSetChanged()
                    dialog.dismiss()
                })
                .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, id ->
                    dialog.cancel()
                })
                .setTitle("Afegir rècord")
            builder.create()
            builder.show()
        }
        // Botó per ordenar la llista
        val buttonSort = findViewById<Button>(R.id.buttonSort)
        buttonSort.setOnClickListener {
            val builder = AlertDialog.Builder(this)
                .setTitle("Com ordenar?")
                .setPositiveButton("Per nom") { dialog, id ->
                    records.sortBy { it.nom }
                    adapter.notifyDataSetChanged()
                    dialog.dismiss()
                }
                .setNegativeButton("Per intents") { dialog, id ->
                    records.sortBy { it.intents }
                    adapter.notifyDataSetChanged()
                    dialog.dismiss()
                }
            builder.create()
            builder.show()
        }
    }
}

