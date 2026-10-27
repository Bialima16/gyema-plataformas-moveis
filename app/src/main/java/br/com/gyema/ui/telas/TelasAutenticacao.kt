package br.com.gyema.ui.telas

import android.content.Context
import android.util.Patterns
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.gyema.R
import br.com.gyema.ui.components.AvisoPendente

private val FundoAuthCor = Color(0xFF090D29)
private val LaranjaAuth = Color(0xFFD74700)
private val TextoLaranja = Color(0xFFF4A16D)
private val TextoMutado = Color(0xFFC4BED9)
private val CartaoAuth = Color(0xFF302C50)

@Composable
private fun FundoAutenticacao(content: @Composable BoxScope.() -> Unit) {
    Box(Modifier.fillMaxSize().background(FundoAuthCor)) {
        Image(painterResource(R.drawable.model_bg_pattern), null,
            Modifier.fillMaxSize(), contentScale = ContentScale.FillBounds)
        content()
    }
}

@Composable
private fun MarcaGyema(mensagem: String, login: Boolean = false) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(mensagem, color = TextoMutado, fontSize = 11.sp, letterSpacing = 2.sp,
            textAlign = TextAlign.Center)
        Spacer(Modifier.height(18.dp))
        Image(painterResource(R.drawable.model_ic_gyema),
            stringResource(R.string.model_brand_description),
            Modifier.size(if (login) 150.dp else 140.dp))
        Spacer(Modifier.height(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("GYEMA", fontSize = 27.sp, fontWeight = FontWeight.Black, color = Color.White)
            if (!login) {
                Spacer(Modifier.width(9.dp))
                Text(stringResource(R.string.model_training), fontSize = 9.sp,
                    color = TextoMutado, modifier = Modifier.background(CartaoAuth, RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp))
            }
        }
        Text(stringResource(R.string.model_academy), fontSize = 10.sp,
            color = if (login) TextoMutado else TextoLaranja)
    }
}

@Composable
private fun BotaoAutenticacao(texto: String, onClick: () -> Unit, secundario: Boolean = false,
    modifier: Modifier = Modifier) {
    Button(onClick = onClick, modifier = modifier.fillMaxWidth().height(55.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (secundario) CartaoAuth else LaranjaAuth,
            contentColor = Color.White),
        contentPadding = PaddingValues(horizontal = 12.dp)) {
        Text(texto, fontSize = 13.sp, textAlign = TextAlign.Center)
        if (!secundario) { Spacer(Modifier.width(12.dp)); Text("➜", fontSize = 18.sp) }
    }
}

@Composable
private fun VoltarAutenticacao(onVoltar: () -> Unit) {
    Box(Modifier.fillMaxWidth()) {
        TextButton(onClick = onVoltar, modifier = Modifier.align(Alignment.CenterStart)) {
            Text("❮", color = TextoMutado, fontSize = 22.sp)
        }
    }
}

@Composable
private fun CampoAutenticacao(valor: String, aoMudar: (String) -> Unit,
    dica: String, icone: Int, senha: Boolean = false, visivel: Boolean = false,
    alternar: (() -> Unit)? = null, erro: String? = null) {
    Column {
        Row(Modifier.fillMaxWidth().height(52.dp)
            .background(Color(0xFF211C48), RoundedCornerShape(9.dp))
            .border(1.dp, if (erro == null) Color(0xFF4D416B) else Color.Red, RoundedCornerShape(9.dp))
            .padding(horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            Image(painterResource(icone), null, Modifier.size(20.dp))
            Spacer(Modifier.width(12.dp))
            BasicTextField(value = valor, onValueChange = aoMudar,
                modifier = Modifier.weight(1f),
                textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = 13.sp),
                cursorBrush = SolidColor(TextoLaranja),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = if (senha) KeyboardType.Password else KeyboardType.Email),
                visualTransformation = if (senha && !visivel) PasswordVisualTransformation() else VisualTransformation.None,
                decorationBox = { inner ->
                    Box {
                        if (valor.isEmpty()) Text(dica, color = TextoMutado, fontSize = 12.sp)
                        inner()
                    }
                })
            if (alternar != null) TextButton(onClick = alternar,
                contentPadding = PaddingValues(0.dp)) {
                Text(if (visivel) "◉" else "◎", color = TextoMutado, fontSize = 19.sp)
            }
        }
        if (erro != null) Text(erro, color = Color(0xFFFF9999), fontSize = 11.sp)
    }
}

@Composable
fun TelaBoasVindas(onEntrar: () -> Unit) {
    var aviso by remember { mutableStateOf(false) }
    FundoAutenticacao {
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())
            .padding(horizontal = 36.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(Modifier.height(18.dp))
            MarcaGyema(stringResource(R.string.model_welcome))
            Spacer(Modifier.height(22.dp))
            Text(stringResource(R.string.model_tagline), color = Color.White,
                fontSize = 20.sp, fontWeight = FontWeight.Bold, lineHeight = 23.sp,
                textAlign = TextAlign.Center)
            Spacer(Modifier.height(12.dp))
            Text(stringResource(R.string.model_description), color = TextoLaranja,
                fontSize = 12.sp, textAlign = TextAlign.Center)
            Spacer(Modifier.height(72.dp))
            BotaoAutenticacao(stringResource(R.string.model_login), onEntrar)
            Spacer(Modifier.height(22.dp))
            BotaoAutenticacao(stringResource(R.string.model_register), { aviso = true }, true)
            Spacer(Modifier.height(45.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                listOf(R.string.model_terms, R.string.model_privacy, R.string.model_support).forEach { id ->
                    TextButton(onClick = { aviso = true }, contentPadding = PaddingValues(0.dp)) {
                        Text(stringResource(id), fontSize = 10.sp, color = TextoLaranja)
                    }
                }
            }
        }
    }
    if (aviso) AvisoPendente(stringResource(R.string.auth_pending)) { aviso = false }
}

@Composable
private fun ConteudoLogin(
    email: String, onEmail: (String) -> Unit,
    senha: String, onSenha: (String) -> Unit,
    lembrar: Boolean, onLembrar: (Boolean) -> Unit,
    visivel: Boolean, onVisivel: () -> Unit,
    erroEmail: String?, erroSenha: String?,
    onRecuperar: () -> Unit, onEntrar: () -> Unit, onCadastro: () -> Unit,
    onVoltar: () -> Unit
) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())
        .imePadding().padding(horizontal = 32.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {
        VoltarAutenticacao(onVoltar)
        MarcaGyema(stringResource(R.string.model_login_welcome), login = true)
        Spacer(Modifier.height(18.dp))
        Text(stringResource(R.string.model_login), color = TextoLaranja,
            fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(7.dp))
        Text(stringResource(R.string.model_login_description), color = TextoMutado,
            fontSize = 12.sp, textAlign = TextAlign.Center)
        Spacer(Modifier.height(18.dp))
        Text(stringResource(R.string.model_email_label), modifier = Modifier.fillMaxWidth(),
            color = TextoMutado, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        Spacer(Modifier.height(7.dp))
        CampoAutenticacao(email, onEmail, stringResource(R.string.model_email_hint),
            R.drawable.model_ic_email, erro = erroEmail)
        Row(Modifier.fillMaxWidth().height(45.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(stringResource(R.string.model_password_label), color = TextoMutado,
                fontWeight = FontWeight.Bold, fontSize = 13.sp, modifier = Modifier.weight(1f))
            TextButton(onClick = onRecuperar) {
                Text(stringResource(R.string.model_forgot_password), color = TextoLaranja, fontSize = 11.sp)
            }
        }
        CampoAutenticacao(senha, onSenha, stringResource(R.string.model_password_hint),
            R.drawable.model_ic_lock, senha = true, visivel = visivel,
            alternar = onVisivel, erro = erroSenha)
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = lembrar, onCheckedChange = onLembrar,
                colors = CheckboxDefaults.colors(checkedColor = LaranjaAuth))
            Text(stringResource(R.string.model_remember_user), color = TextoMutado, fontSize = 11.sp)
        }
        Spacer(Modifier.height(10.dp))
        BotaoAutenticacao(stringResource(R.string.model_login), onEntrar)
        TextButton(onClick = onCadastro) {
            Text(stringResource(R.string.model_no_account), color = TextoLaranja, fontSize = 11.sp)
        }
    }
}

@Composable
fun TelaLogin(onVoltar: () -> Unit, onRecuperar: () -> Unit) {
    val context = LocalContext.current
    val prefs = remember(context) { context.getSharedPreferences("gyema_identification", Context.MODE_PRIVATE) }
    var email by rememberSaveable { mutableStateOf(prefs.getString("email", "").orEmpty()) }
    var senha by remember { mutableStateOf("") }
    var lembrar by rememberSaveable { mutableStateOf(prefs.contains("email")) }
    var visivel by remember { mutableStateOf(false) }
    var erroEmail by remember { mutableStateOf<String?>(null) }
    var erroSenha by remember { mutableStateOf<String?>(null) }
    var aviso by remember { mutableStateOf<Int?>(null) }
    FundoAutenticacao {
        ConteudoLogin(email, { email = it; erroEmail = null },
            senha, { senha = it; erroSenha = null }, lembrar, {
                lembrar = it
                if (!it) prefs.edit().remove("email").apply()
            }, visivel, { visivel = !visivel }, erroEmail, erroSenha,
            onRecuperar, {
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
            }, { aviso = R.string.auth_pending }, onVoltar)
    }
    aviso?.let { id -> AvisoPendente(stringResource(id)) { aviso = null } }
}

@Composable
fun TelaRecuperacao(onVoltar: () -> Unit) {
    val context = LocalContext.current
    var email by rememberSaveable { mutableStateOf("") }
    var erro by remember { mutableStateOf<String?>(null) }
    var enviado by rememberSaveable { mutableStateOf(false) }
    var aviso by remember { mutableStateOf(false) }
    FundoAutenticacao {
        Box(Modifier.fillMaxSize()) {
            Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())
                .padding(horizontal = 32.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally) {
                VoltarAutenticacao(onVoltar)
                MarcaGyema(stringResource(R.string.model_login_welcome), login = true)
                Spacer(Modifier.height(20.dp))
                Text(stringResource(R.string.model_login), color = TextoLaranja, fontSize = 20.sp)
                Spacer(Modifier.height(8.dp))
                Text(stringResource(R.string.model_login_description), color = TextoMutado,
                    fontSize = 12.sp, textAlign = TextAlign.Center)
                Spacer(Modifier.height(18.dp))
                Text(stringResource(R.string.model_email_label), color = TextoMutado)
                Spacer(Modifier.height(10.dp))
                CampoAutenticacao("", {}, stringResource(R.string.model_email_hint), R.drawable.model_ic_email)
                Spacer(Modifier.height(20.dp))
                BotaoAutenticacao(stringResource(R.string.model_login), {})
            }
            Box(Modifier.fillMaxSize().background(Color(0xB3080620)))
            Column(Modifier.align(Alignment.Center).padding(horizontal = 24.dp)
                .fillMaxWidth().clip(RoundedCornerShape(12.dp))
                .background(CartaoAuth).padding(horizontal = 20.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally) {
                Text(if (enviado) "SENHA RECUPERADA" else stringResource(R.string.model_recovery_title),
                    color = TextoLaranja, fontSize = 19.sp, fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center)
                Spacer(Modifier.height(14.dp))
                Image(painterResource(R.drawable.model_ic_recovery), null, Modifier.size(58.dp))
                Spacer(Modifier.height(18.dp))
                Text(if (enviado) "A solicitação foi concluída com sucesso! Verifique o link de recuperação enviado para seu e-mail cadastrado."
                    else stringResource(R.string.model_recovery_description),
                    color = TextoMutado, fontSize = 12.sp, textAlign = TextAlign.Center)
                Spacer(Modifier.height(22.dp))
                if (!enviado) {
                    CampoAutenticacao(email, { email = it; erro = null },
                        stringResource(R.string.model_recovery_email_hint),
                        R.drawable.model_ic_email, erro = erro)
                    Spacer(Modifier.height(26.dp))
                }
                BotaoAutenticacao(if (enviado) "Realizar Login"
                    else stringResource(R.string.model_request_recovery), {
                    if (enviado) onVoltar()
                    else {
                        erro = when {
                            email.isBlank() -> context.getString(R.string.auth_email_required)
                            !Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches() -> context.getString(R.string.auth_email_invalid)
                            else -> null
                        }
                        if (erro == null) { enviado = true; aviso = true }
                    }
                })
            }
        }
    }
    if (aviso) AvisoPendente(stringResource(R.string.auth_recovery_pending)) { aviso = false }
}
