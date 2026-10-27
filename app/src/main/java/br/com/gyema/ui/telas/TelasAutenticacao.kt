package br.com.gyema.ui.telas

import android.util.Patterns
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.gyema.R
import br.com.gyema.ui.components.*
import br.com.gyema.ui.tema.Roxo

@Composable
private fun FundoAuth(content: @Composable ColumnScope.() -> Unit) {
    Column(modifier = Modifier.fillMaxSize().background(Roxo)
        .verticalScroll(rememberScrollState()).imePadding().padding(horizontal = 32.dp, vertical = 36.dp),
        horizontalAlignment = Alignment.CenterHorizontally, content = content)
}

@Composable
fun TelaBoasVindas(onEntrar: () -> Unit) {
    var aviso by remember { mutableStateOf(false) }
    FundoAuth {
        Spacer(Modifier.height(28.dp))
        Text(stringResource(R.string.auth_greeting), color = Color.LightGray, fontSize = 13.sp)
        Spacer(Modifier.height(44.dp))
        TituloGyema(stringResource(R.string.auth_headline))
        Spacer(Modifier.height(16.dp))
        Text(stringResource(R.string.auth_description), color = Color(0xFFF4A16D), textAlign = TextAlign.Center)
        Spacer(Modifier.height(70.dp))
        BotaoGyema(stringResource(R.string.auth_sign_in), onEntrar)
        Spacer(Modifier.height(18.dp))
        BotaoGyema(stringResource(R.string.auth_first_access), { aviso = true }, secundario = true)
        Spacer(Modifier.height(60.dp))
        Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
            listOf(R.string.auth_terms, R.string.auth_privacy, R.string.auth_support).forEach { label ->
                TextButton(onClick = { aviso = true }) { Text(stringResource(label), fontSize = 10.sp) }
            }
        }
    }
    if (aviso) AvisoPendente(stringResource(R.string.auth_pending)) { aviso = false }
}

@Composable
fun TelaLogin(onVoltar: () -> Unit, onRecuperar: () -> Unit) {
    val context = LocalContext.current
    val prefs = remember(context) { context.getSharedPreferences("gyema_identification", android.content.Context.MODE_PRIVATE) }
    var email by rememberSaveable { mutableStateOf(prefs.getString("email", "").orEmpty()) }
    var senha by remember { mutableStateOf("") } // Nunca persistir a senha
    var lembrar by rememberSaveable { mutableStateOf(prefs.contains("email")) }
    var visivel by remember { mutableStateOf(false) }
    var erroEmail by remember { mutableStateOf<String?>(null) }
    var erroSenha by remember { mutableStateOf<String?>(null) }
    var aviso by remember { mutableStateOf<Int?>(null) }
    fun validar() {
        erroEmail = when {
            email.isBlank() -> context.getString(R.string.auth_email_required)
            !Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches() -> context.getString(R.string.auth_email_invalid)
            else -> null
        }
        erroSenha = if (senha.isBlank()) context.getString(R.string.auth_password_required) else null
        if (erroEmail == null && erroSenha == null) {
            if (lembrar) prefs.edit().putString("email", email.trim()).apply()
            else prefs.edit().remove("email").apply()
            aviso = R.string.auth_login_pending
        }
    }
    FundoAuth {
        CabecalhoVoltar("", onVoltar)
        TituloGyema(stringResource(R.string.auth_return))
        Spacer(Modifier.height(18.dp))
        Text(stringResource(R.string.auth_login_help), color = Color.LightGray, textAlign = TextAlign.Center)
        Spacer(Modifier.height(22.dp))
        OutlinedTextField(email, { email = it; erroEmail = null }, label = { Text(stringResource(R.string.auth_email)) },
            placeholder = { Text(stringResource(R.string.auth_email_hint)) }, singleLine = true,
            isError = erroEmail != null, supportingText = { erroEmail?.let { Text(it) } },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
            modifier = Modifier.fillMaxWidth())
        OutlinedTextField(senha, { senha = it; erroSenha = null },
            label = { Text(stringResource(R.string.auth_password)) },
            visualTransformation = if (visivel) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = { TextButton(onClick = { visivel = !visivel }) { Text(if (visivel) "Ocultar" else "Mostrar") } },
            isError = erroSenha != null, supportingText = { erroSenha?.let { Text(it) } },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { validar() }), singleLine = true,
            modifier = Modifier.fillMaxWidth())
        TextButton(onClick = onRecuperar) { Text(stringResource(R.string.auth_forgot)) }
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Checkbox(checked = lembrar, onCheckedChange = {
                lembrar = it
                if (!it) prefs.edit().remove("email").apply()
            })
            Text(stringResource(R.string.auth_remember), fontSize = 12.sp)
        }
        Spacer(Modifier.height(12.dp))
        BotaoGyema(stringResource(R.string.auth_sign_in), { validar() })
        TextButton(onClick = { aviso = R.string.auth_pending }) { Text(stringResource(R.string.auth_register)) }
    }
    aviso?.let { id -> AvisoPendente(stringResource(id)) { aviso = null } }
}

@Composable
fun TelaRecuperacao(onVoltar: () -> Unit) {
    val context = LocalContext.current
    var email by rememberSaveable { mutableStateOf("") }
    var erro by remember { mutableStateOf<String?>(null) }
    var aviso by remember { mutableStateOf(false) }
    fun enviar() {
        erro = when {
            email.isBlank() -> context.getString(R.string.auth_email_required)
            !Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches() -> context.getString(R.string.auth_email_invalid)
            else -> null
        }
        if (erro == null) aviso = true
    }
    FundoAuth {
        CabecalhoVoltar("", onVoltar)
        Spacer(Modifier.height(28.dp))
        TituloGyema(stringResource(R.string.auth_recovery_title))
        Spacer(Modifier.height(20.dp))
        Text(stringResource(R.string.auth_recovery_help), color = Color.LightGray, textAlign = TextAlign.Center)
        Spacer(Modifier.height(24.dp))
        OutlinedTextField(email, { email = it; erro = null },
            label = { Text(stringResource(R.string.auth_recovery_email)) },
            placeholder = { Text(stringResource(R.string.auth_recovery_hint)) },
            isError = erro != null, supportingText = { erro?.let { Text(it) } },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { enviar() }), singleLine = true,
            modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(24.dp))
        BotaoGyema(stringResource(R.string.auth_recovery_submit), { enviar() })
    }
    if (aviso) AvisoPendente(stringResource(R.string.auth_recovery_pending)) { aviso = false }
}