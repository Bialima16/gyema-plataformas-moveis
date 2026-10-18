package br.com.gyema

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText

class LoginActivity : Activity() {
    private lateinit var email: EditText
    private lateinit var remember: CheckBox
    private val preferences by lazy { getSharedPreferences("gyema_identification", MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        AuthUi.prepare(this)
        email = findViewById(R.id.login_email)
        remember = findViewById(R.id.login_remember)
        val password = findViewById<EditText>(R.id.login_password)
        val toggle = findViewById<android.widget.ImageButton>(R.id.login_toggle_password)
        // Apenas e-mail ÃƒÂ© persistido. A senha tambÃƒÂ©m fica fora do estado salvo da View.
        if (savedInstanceState == null) {
            val savedEmail = preferences.getString("email", null)
            email.setText(savedEmail.orEmpty())
            remember.isChecked = savedEmail != null
        }
        remember.setOnCheckedChangeListener { _, checked ->
            if (!checked) preferences.edit().remove("email").apply()
        }
        toggle.setOnClickListener {
            val show = password.transformationMethod is PasswordTransformationMethod
            password.transformationMethod = if (show) HideReturnsTransformationMethod.getInstance()
                else PasswordTransformationMethod.getInstance()
            password.setSelection(password.text.length)
            toggle.contentDescription = getString(if (show) R.string.auth_hide_password else R.string.auth_show_password)
            toggle.setImageResource(if (show) R.drawable.model_ic_eye else R.drawable.model_ic_eye_off)
        }
        findViewById<android.widget.ImageButton>(R.id.auth_back).setOnClickListener { finish() }
        findViewById<Button>(R.id.login_forgot).setOnClickListener {
            it.isEnabled = false
            startActivity(Intent(this, PasswordRecoveryActivity::class.java))
        }
        findViewById<Button>(R.id.login_register).setOnClickListener { AuthUi.message(this) }
        fun submit() {
            val emailValid = AuthUi.validEmail(email)
            val passwordValid = password.text.isNotBlank()
            password.error = if (passwordValid) null else getString(R.string.auth_password_required)
            when {
                !emailValid -> email.requestFocus()
                !passwordValid -> password.requestFocus()
                else -> {
                    saveIdentification()
                    AuthUi.message(this, R.string.auth_login_pending)
                }
            }
        }
        findViewById<Button>(R.id.login_submit).setOnClickListener { submit() }
        password.setOnEditorActionListener { _, action, _ ->
            if (action == EditorInfo.IME_ACTION_DONE) { submit(); true } else false
        }
    }

    private fun saveIdentification() {
        val value = email.text.toString().trim()
        if (remember.isChecked && android.util.Patterns.EMAIL_ADDRESS.matcher(value).matches()) {
            preferences.edit().putString("email", value).apply()
        } else {
            preferences.edit().remove("email").apply()
        }
    }

    override fun onStop() {
        saveIdentification()
        super.onStop()
    }

    override fun onResume() {
        super.onResume()
        findViewById<Button>(R.id.login_forgot).isEnabled = true
    }
}
