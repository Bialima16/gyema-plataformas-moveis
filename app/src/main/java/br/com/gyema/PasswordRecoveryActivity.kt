package br.com.gyema

import android.app.Activity
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText

class PasswordRecoveryActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_recovery)
        AuthUi.prepare(this)
        findViewById<android.widget.ImageButton>(R.id.auth_back).setOnClickListener { finish() }
        val email = findViewById<EditText>(R.id.recovery_email)
        fun submit() {
            if (AuthUi.validEmail(email)) {
                AuthUi.message(this, R.string.auth_recovery_pending)
            } else email.requestFocus()
        }
        findViewById<Button>(R.id.recovery_submit).setOnClickListener { submit() }
        email.setOnEditorActionListener { _, action, _ ->
            if (action == EditorInfo.IME_ACTION_DONE) { submit(); true } else false
        }
    }
}
