package br.com.gyema

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton

/** Alerta visual de demonstração: não detecta ocupação nem altera a ficha. */
class EquipmentOccupiedActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_equipment_occupied)
        WorkoutUi.prepare(this)
        findViewById<ImageButton>(R.id.workout_back).setOnClickListener { finish() }
        findViewById<Button>(R.id.equipment_keep).setOnClickListener { finish() }
        findViewById<Button>(R.id.equipment_reorder).setOnClickListener {
            WorkoutUi.pending(this, R.string.workout_reorder_pending)
        }
        findViewById<Button>(R.id.equipment_alternatives).setOnClickListener {
            WorkoutUi.pending(this, R.string.workout_alternatives_pending)
        }
    }
}
