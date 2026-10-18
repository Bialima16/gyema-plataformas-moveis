package br.com.gyema

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView

class ExerciseDetailsActivity : Activity() {
    private var selectedTab = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise_details)
        WorkoutUi.prepare(this)
        findViewById<ImageButton>(R.id.workout_back).setOnClickListener { finish() }
        findViewById<ImageButton>(R.id.exercise_play).setOnClickListener {
            WorkoutUi.pending(this, R.string.workout_media_notice)
        }
        val tabs = listOf(R.id.exercise_tab_video, R.id.exercise_tab_muscles,
            R.id.exercise_tab_description, R.id.exercise_tab_history)
        tabs.forEachIndexed { index, id ->
            findViewById<Button>(id).setOnClickListener { showTab(index) }
        }
        showTab(savedInstanceState?.getInt("selectedTab")?.coerceIn(0, 3) ?: 0)
    }

    private fun showTab(index: Int) {
        selectedTab = index
        listOf(R.id.exercise_tab_video, R.id.exercise_tab_muscles,
            R.id.exercise_tab_description, R.id.exercise_tab_history).forEachIndexed { position, id ->
            findViewById<Button>(id).apply {
                isSelected = position == index
            }
        }
        findViewById<View>(R.id.exercise_media).visibility = if (index == 0) View.VISIBLE else View.GONE
        findViewById<View>(R.id.exercise_muscle_panel).visibility = if (index <= 1) View.VISIBLE else View.GONE
        findViewById<View>(R.id.exercise_tips_panel).visibility = if (index == 0) View.VISIBLE else View.GONE
        findViewById<TextView>(R.id.exercise_info).apply {
            visibility = if (index >= 2) View.VISIBLE else View.GONE
            setText(if (index == 3) R.string.workout_history_pending else R.string.workout_description_pending)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("selectedTab", selectedTab)
        super.onSaveInstanceState(outState)
    }
}
