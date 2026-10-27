package br.com.gyema.ui.telas

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.gyema.ui.tema.*

@Composable
fun TelaCadastroPasso1(
    onVoltar: () -> Unit = {},
    onAvancar: () -> Unit = {},
    onLogin: () -> Unit = {}
) {
    var nome by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var senhaVisivel by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Fundo)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // botao voltar
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterStart
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(SuperficieEscura)
                    .clickable(onClick = onVoltar),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Voltar",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // cabecalho com logo
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(SuperficieEscura)
                    .border(2.dp, Laranja, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.FitnessCenter,
                    contentDescription = null,
                    tint = Laranja,
                    modifier = Modifier.size(28.dp)
                )
            }

            Text(
                text = "GYEMA",
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                letterSpacing = 1.sp
            )

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(SuperficieEscura)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "ACADEMIA UNIFOR",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextoSecundario,
                    letterSpacing = 0.5.sp
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Cadastre a sua conta",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Laranja,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Registre-se para aproveitar ao máximo a\nestrutura da Academia UNIFOR",
                fontSize = 12.sp,
                color = TextoSecundario,
                textAlign = TextAlign.Center,
                lineHeight = 16.sp
            )
        }

        // indicador de passos
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Laranja),
                    contentAlignment = Alignment.Center
                ) {
                    Text("1", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text("Cadastro", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
            }

            Box(
                modifier = Modifier
                    .weight(0.6f)
                    .padding(top = 16.dp)
                    .height(1.dp)
                    .background(Borda)
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Text("2", color = Fundo, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Informações\nPessoais",
                    fontSize = 11.sp,
                    color = TextoSecundario,
                    textAlign = TextAlign.Center
                )
            }

            Box(
                modifier = Modifier
                    .weight(0.6f)
                    .padding(top = 16.dp)
                    .height(1.dp)
                    .background(Borda)
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Text("3", color = Fundo, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Teste de\nprontidão",
                    fontSize = 11.sp,
                    color = TextoSecundario,
                    textAlign = TextAlign.Center
                )
            }
        }

        // campos de texto
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CampoInputPasso1(
                valor = nome,
                onValorChange = { nome = it },
                placeholder = "Nome completo",
                icone = Icons.Outlined.Person
            )

            CampoInputPasso1(
                valor = email,
                onValorChange = { email = it },
                placeholder = "E-mail",
                icone = Icons.Outlined.Email
            )

            CampoInputPasso1(
                valor = senha,
                onValorChange = { senha = it },
                placeholder = "Senha",
                icone = Icons.Outlined.Lock,
                ehSenha = true,
                senhaVisivel = senhaVisivel,
                onToggleSenha = { senhaVisivel = !senhaVisivel }
            )

            Column(
                modifier = Modifier.padding(start = 4.dp, top = 2.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = "Sua senha deve conter:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Pelo menos 8 caracteres, letras e números e um caractere especial (ex: @, #, )",
                    fontSize = 11.sp,
                    color = Laranja.copy(alpha = 0.85f),
                    lineHeight = 15.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // botao avancar
        val shapeBotao = RoundedCornerShape(14.dp)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .clip(shapeBotao)
                .background(Laranja)
                .clickable(onClick = onAvancar),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "AVANÇAR",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // link de login
        Row(
            modifier = Modifier.padding(top = 4.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Ja tem uma conta? ",
                fontSize = 12.sp,
                color = TextoSecundario
            )
            Text(
                text = "Entrar em sua conta",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Laranja,
                modifier = Modifier.clickable(onClick = onLogin)
            )
        }
    }
}

@Composable
private fun CampoInputPasso1(
    valor: String,
    onValorChange: (String) -> Unit,
    placeholder: String,
    icone: ImageVector,
    ehSenha: Boolean = false,
    senhaVisivel: Boolean = false,
    onToggleSenha: () -> Unit = {}
) {
    val shape = RoundedCornerShape(14.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .clip(shape)
            .background(SuperficieEscura)
            .border(1.dp, Borda, shape)
            .padding(horizontal = 14.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icone,
                contentDescription = null,
                tint = TextoSecundario,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            BasicTextField(
                value = valor,
                onValueChange = onValorChange,
                modifier = Modifier.weight(1f),
                textStyle = TextStyle(
                    color = Color.White,
                    fontSize = 13.sp
                ),
                cursorBrush = SolidColor(Laranja),
                visualTransformation = if (ehSenha && !senhaVisivel) PasswordVisualTransformation() else VisualTransformation.None,
                singleLine = true,
                decorationBox = { innerTextField ->
                    if (valor.isEmpty()) {
                        Text(
                            text = placeholder,
                            color = TextoSecundario,
                            fontSize = 13.sp
                        )
                    }
                    innerTextField()
                }
            )
            if (ehSenha) {
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = if (senhaVisivel) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                    contentDescription = "Alternar visibilidade",
                    tint = TextoSecundario,
                    modifier = Modifier
                        .size(20.dp)
                        .clickable(onClick = onToggleSenha)
                )
            }
        }
    }
}

// preview da tela
@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
private fun PreviewTelaCadastroPasso1() {
    GyemaTheme {
        TelaCadastroPasso1()
    }
}
