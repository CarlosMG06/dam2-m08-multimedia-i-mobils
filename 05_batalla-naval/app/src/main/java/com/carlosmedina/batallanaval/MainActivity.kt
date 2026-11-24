package com.carlosmedina.batallanaval

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    private val rows = 7
    private val cols = 7
    private val ships = mutableListOf<Ship>()
    private val occupiedCells = mutableSetOf<String>()
    private var intents = 0

    // IDs dels botons per fila
    private val btnIdArray = arrayOf(
        arrayOf(R.id.btnA1, R.id.btnA2, R.id.btnA3, R.id.btnA4, R.id.btnA5, R.id.btnA6, R.id.btnA7),
        arrayOf(R.id.btnB1, R.id.btnB2, R.id.btnB3, R.id.btnB4, R.id.btnB5, R.id.btnB6, R.id.btnB7),
        arrayOf(R.id.btnC1, R.id.btnC2, R.id.btnC3, R.id.btnC4, R.id.btnC5, R.id.btnC6, R.id.btnC7),
        arrayOf(R.id.btnD1, R.id.btnD2, R.id.btnD3, R.id.btnD4, R.id.btnD5, R.id.btnD6, R.id.btnD7),
        arrayOf(R.id.btnE1, R.id.btnE2, R.id.btnE3, R.id.btnE4, R.id.btnE5, R.id.btnE6, R.id.btnE7),
        arrayOf(R.id.btnF1, R.id.btnF2, R.id.btnF3, R.id.btnF4, R.id.btnF5, R.id.btnF6, R.id.btnF7),
        arrayOf(R.id.btnG1, R.id.btnG2, R.id.btnG3, R.id.btnG4, R.id.btnG5, R.id.btnG6, R.id.btnG7)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        for (row in 0 until rows) {
            for (col in 0 until cols) {
                val button = findViewById<Button>(btnIdArray[row][col])
                button.setOnClickListener {
                    onButtonClicked(row, col)
                }
            }
        }

        // Generar vaixells
        generateShip(4) // Cuirassat (4 caselles)
        generateShip(3) // Destructor (3 caselles)
    }
    private fun generateShip(length: Int) {
        var ship: Ship
        // Crear vaixell aleatori fins que no coincideixi amb cap altre vaixell existent
        do {
            ship = createRandomShip(length)
        } while (ship.occupiedCells.any { it in occupiedCells })
        ships.add(ship)
        occupiedCells.addAll(ship.occupiedCells)
    }
    private fun createRandomShip(length: Int): Ship {
        val orientation = Ship.Orientation.entries.random()

        val maxRow = if (orientation == Ship.Orientation.VERTICAL) rows - length else rows - 1
        val maxCol = if (orientation == Ship.Orientation.HORIZONTAL) cols - length else cols - 1

        val row = Random.nextInt(0, maxRow + 1)
        val col = Random.nextInt(0, maxCol + 1)

        return Ship(row, col, length, orientation)
    }
    private fun onButtonClicked(row: Int, col: Int) {
        intents++
        val cellPos = "${'A' + row}$col"
        val button = findViewById<Button>(btnIdArray[row][col])

        if (cellPos in occupiedCells) {
            button.setBackgroundColor(ContextCompat.getColor(this, R.color.hit))
            button.text = ContextCompat.getString(this, R.string.hit)

            val hitShip = ships.find { cellPos in it.occupiedCells }
            if (hitShip != null) {
                hitShip.hits++
                if (hitShip.hits >= hitShip.length) {
                    Toast.makeText(this@MainActivity, "Vaixell enfonsat!", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            button.setBackgroundColor(ContextCompat.getColor(this, R.color.miss))
            button.text = ContextCompat.getString(this, R.string.miss)
        }

        button.isEnabled = false

        if (ships.all { it.hits >= it.length }) {
            Toast.makeText(this, "Has guanyat! Has enfonsat tots els vaixells en $intents intents.", Toast.LENGTH_LONG).show()
            disableAllButtons()
        }
    }
    private fun disableAllButtons() {
        for (row in 0 until rows) {
            for (col in 0 until cols) {
                val button = findViewById<Button>(btnIdArray[row][col])
                if (button.isEnabled) {
                    button.isEnabled = false
                }
            }
        }
    }

    private class Ship (val row: Int, val col: Int, val length: Int, val orientation: Orientation) {
        enum class Orientation { HORIZONTAL, VERTICAL }
        var hits = 0
        val occupiedCells = Array(length) { i ->
            if (orientation == Orientation.VERTICAL) {
                "${'A' + row + i}$col"
            } else {
                "${'A' + row}${col + i}"
            }
        }
    }
}