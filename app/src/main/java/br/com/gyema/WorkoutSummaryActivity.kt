package br.com.gyema

import android.app.Activity
import android.os.Bundle
import android.widget.Button

/** Resumo com os dados de exemplo da referência; não registra um treino real. */
class WorkoutSummaryActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout_summary)
        WorkoutUi.prepare(this)
        // O chamador será a tela Meus Treinos quando ela for integrada.
        findViewById<Button>(R.id.workout_return).setOnClickListener { finish() }
    }
}
