package br.com.gyema

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button

class WelcomeActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        AuthUi.prepare(this)
        findViewById<Button>(R.id.welcome_login).setOnClickListener {
            it.isEnabled = false
            startActivity(Intent(this, LoginActivity::class.java))
        }
        listOf(R.id.welcome_register, R.id.welcome_terms, R.id.welcome_privacy, R.id.welcome_support).forEach { id ->
            findViewById<Button>(id).setOnClickListener { AuthUi.message(this) }
        }
    }

    override fun onResume() {
        super.onResume()
        findViewById<Button>(R.id.welcome_login).isEnabled = true
    }
}
