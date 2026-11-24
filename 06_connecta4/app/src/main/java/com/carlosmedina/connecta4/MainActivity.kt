package com.carlosmedina.connecta4

import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    private val cols = 7
    private val rows = 6
    // IDs dels botons de cada columna
    private val btnIdArray = arrayOf(R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4, R.id.btn5, R.id.btn6, R.id.btn7)
    // IDs de les imageViews per columna
    private val imgViewIdArray = arrayOf(
        arrayOf(R.id.imgViewA1, R.id.imgViewB1, R.id.imgViewC1, R.id.imgViewD1, R.id.imgViewE1, R.id.imgViewF1),
        arrayOf(R.id.imgViewA2, R.id.imgViewB2, R.id.imgViewC2, R.id.imgViewD2, R.id.imgViewE2, R.id.imgViewF2),
        arrayOf(R.id.imgViewA3, R.id.imgViewB3, R.id.imgViewC3, R.id.imgViewD3, R.id.imgViewE3, R.id.imgViewF3),
        arrayOf(R.id.imgViewA4, R.id.imgViewB4, R.id.imgViewC4, R.id.imgViewD4, R.id.imgViewE4, R.id.imgViewF4),
        arrayOf(R.id.imgViewA5, R.id.imgViewB5, R.id.imgViewC5, R.id.imgViewD5, R.id.imgViewE5, R.id.imgViewF5),
        arrayOf(R.id.imgViewA6, R.id.imgViewB6, R.id.imgViewC6, R.id.imgViewD6, R.id.imgViewE6, R.id.imgViewF6),
        arrayOf(R.id.imgViewA7, R.id.imgViewB7, R.id.imgViewC7, R.id.imgViewD7, R.id.imgViewE7, R.id.imgViewF7)
    )
    // Cel·les
    private val cells = mutableListOf<MutableList<Cell>>()
    // True → Torn de vermell, False → Torn de groc
    private var redTurn = true
    private lateinit var turnText : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        for (col in 0 until cols) {
            val button = findViewById< ImageButton>(btnIdArray[col])
            button.setOnClickListener {
                onButtonClicked(col)
            }
        }

        for (col in 0 until cols) {
            val cellsCol = mutableListOf<Cell>()
            for (row in 0 until rows) {
                cellsCol.add(row, Cell())
            }
            cells.add(col, cellsCol)
        }

        turnText = findViewById<TextView>(R.id.turnText)
    }

    private fun onButtonClicked(chipCol: Int) {
        var chipDropped = false;
        var chipRow = 0;
        for (row in 0 until rows) {
            // Canvia l'imatge de la primera cel·la de la columna on no hi hagi cap fitxa
            val cell = cells[chipCol][row]
            if (cell.chipColor == Cell.ChipColor.NONE) {
                val imgView = findViewById<ImageView>(imgViewIdArray[chipCol][row])
                // Escull l'imatge adequada segons el torn
                if (redTurn) {
                    cell.chipColor = Cell.ChipColor.RED
                    imgView.setImageResource(R.drawable.red_chip)
                } else {
                    cell.chipColor = Cell.ChipColor.YELLOW
                    imgView.setImageResource(R.drawable.yellow_chip)
                }

                chipRow = row;
                chipDropped = true;
                break
            }
        }

        if (chipDropped) {
            if (checkForWinner(chipCol, chipRow)) {
                // Mostrar missatge de victoria
                val winner = if (redTurn) "Vermell" else "Groc"
                val winnerText = "$winner guanya!"
                Toast.makeText(this, winnerText, Toast.LENGTH_LONG).show()
                turnText.text = winnerText

                // Desactivar els botons
                for (btnId in btnIdArray) {
                    findViewById<ImageButton>(btnId).isEnabled = false
                }
                return
            }

            redTurn = !redTurn
            if (redTurn) {
                turnText.setText(R.string.red_turn)
                turnText.setTextColor(resources.getColor(R.color.red, null))
            } else {
                turnText.setText(R.string.yellow_turn)
                turnText.setTextColor(resources.getColor(R.color.yellow, null))
            }
        }
    }

    private fun checkForWinner(chipCol: Int, chipRow: Int): Boolean {
        // Mirem si hi ha una línia en com a mínim una direcció
        return checkDirection(chipRow, chipCol, 1, 0) ||   // Horitzontal
                checkDirection(chipRow, chipCol, 0, 1) ||   // Vertical
                checkDirection(chipRow, chipCol, 1, 1) ||   // Diagonal ascendent
                checkDirection(chipRow, chipCol, 1, -1)     // Diagonal descendent
    }

    private fun checkDirection(startRow: Int, startCol: Int, deltaRow: Int, deltaCol: Int): Boolean {
        val currentChipColor = cells[startCol][startRow].chipColor
        var count = 1 // (La fitxa que acaba de caure)

        // Direcció positiva
        // No comprovem en el cas de la direcció vertical, ja que la línia vertical sempre serà cap avall
        if (deltaRow != 0) {
            count += countInDirection(startRow, startCol, deltaRow, deltaCol, currentChipColor)
        }

        // Direcció negativa
        count += countInDirection(startRow, startCol, -deltaRow, -deltaCol, currentChipColor)

        // Com a mínim 4 en línia per guanyar
        return count >= 4
    }

    private fun countInDirection(startRow: Int, startCol: Int, deltaRow: Int, deltaCol: Int, currentChipColor: Cell.ChipColor): Int {
        var count = 0
        var row = startRow + deltaRow
        var col = startCol + deltaCol

        // Continuar mirant fins que ens sortim dels límits o no trobem una fitxa del mateix color
        while (row in 0 until rows && col in 0 until cols &&
               cells[col][row].chipColor == currentChipColor) {
            count++
            row += deltaRow
            col += deltaCol
        }

        return count
    }

    class Cell () {
        enum class ChipColor {NONE, RED, YELLOW}
        var chipColor = ChipColor.NONE
    }
}