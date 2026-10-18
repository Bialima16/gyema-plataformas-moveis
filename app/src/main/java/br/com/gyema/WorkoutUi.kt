package br.com.gyema

import android.app.Activity
import android.app.AlertDialog
import android.os.Build
import android.view.View
import android.view.WindowInsets

internal object WorkoutUi {
    fun prepare(activity: Activity) {
        if (Build.VERSION.SDK_INT >= 30) {
            @Suppress("DEPRECATION")
            activity.window.setDecorFitsSystemWindows(false)
            val root = activity.findViewById<View>(R.id.workout_root)
            root.setOnApplyWindowInsetsListener { view, insets ->
                val safe = insets.getInsets(WindowInsets.Type.systemBars() or WindowInsets.Type.displayCutout())
                view.setPadding(safe.left, safe.top, safe.right, safe.bottom)
                insets
            }
            root.requestApplyInsets()
        }
    }

    fun pending(activity: Activity, message: Int) {
        AlertDialog.Builder(activity)
            .setTitle(R.string.workout_pending_title)
            .setMessage(message)
            .setPositiveButton(R.string.workout_understood, null)
            .show()
    }
}
