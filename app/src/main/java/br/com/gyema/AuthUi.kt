package br.com.gyema

import android.app.Activity
import android.app.AlertDialog
import android.os.Build
import android.util.Patterns
import android.view.View
import android.view.WindowInsets
import android.widget.EditText

internal object AuthUi {
    fun prepare(activity: Activity) {
        // API 35+ impõe edge-to-edge: preservar barras e teclado sem esconder campos.
        if (Build.VERSION.SDK_INT >= 30) {
            activity.window.setDecorFitsSystemWindows(false)
            val root = activity.findViewById<View>(R.id.auth_scroll)
            root.setOnApplyWindowInsetsListener { view, insets ->
                val bars = insets.getInsets(WindowInsets.Type.systemBars() or WindowInsets.Type.displayCutout())
                val ime = insets.getInsets(WindowInsets.Type.ime())
                view.setPadding(bars.left, bars.top, bars.right, maxOf(bars.bottom, ime.bottom))
                insets
            }
            root.requestApplyInsets()
        }
    }

    fun message(activity: Activity, text: Int = R.string.auth_pending) {
        AlertDialog.Builder(activity)
            .setTitle(R.string.auth_pending_title)
            .setMessage(text)
            .setPositiveButton(R.string.auth_ok, null)
            .show()
    }

    fun validEmail(field: EditText): Boolean {
        val email = field.text.toString().trim()
        val error = when {
            email.isEmpty() -> R.string.auth_email_required
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> R.string.auth_email_invalid
            else -> null
        }
        field.error = error?.let { field.context.getString(it) }
        return error == null
    }
}
