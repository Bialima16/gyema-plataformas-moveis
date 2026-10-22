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
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Height
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Wc
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.gyema.ui.tema.*

@Composable
fun TelaCadastroPasso2(
    onVoltar: () -> Unit = {},
    onAvancar: () -> Unit = {}
) {
    var cpf by remember { mutableStateOf("") }
    var matricula by remember { mutableStateOf("") }
    var dataNascimento by remember { mutableStateOf("") }
    var genero by remember { mutableStateOf("") }
    var peso by remember { mutableStateOf("") }
    var altura by remember { mutableStateOf("") }

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
                text = "Finalize o cadastro",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Laranja,
                textAlign = TextAlign.Center
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
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Text("1", color = Fundo, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text("Cadastro", fontSize = 11.sp, color = TextoSecundario, fontWeight = FontWeight.Normal)
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
                        .background(Laranja),
                    contentAlignment = Alignment.Center
                ) {
                    Text("2", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Informações\nPessoais",
                    fontSize = 11.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
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

        // campos de identificacao
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CampoInputPasso2(
                valor = cpf,
                onValorChange = { cpf = it },
                placeholder = "Digite o seu cpf",
                icone = Icons.Outlined.Badge
            )

            Text(
                text = "Apenas para alunos ou colaboradores",
                fontSize = 11.sp,
                color = TextoSecundario,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 4.dp),
                textAlign = TextAlign.Center
            )

            CampoInputPasso2(
                valor = matricula,
                onValorChange = { matricula = it },
                placeholder = "Matrícula (opcional)",
                icone = Icons.Outlined.Person
            )
        }

        // campos de medidas
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CampoInputPasso2(
                    valor = dataNascimento,
                    onValorChange = { dataNascimento = it },
                    placeholder = "27/06/1995",
                    icone = Icons.Outlined.CalendarToday,
                    modifier = Modifier.weight(1f)
                )

                CampoSeletorGenero(
                    generoSelecionado = genero,
                    onGeneroSelecionado = { genero = it },
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CampoInputPasso2(
                    valor = peso,
                    onValorChange = { peso = it },
                    placeholder = "Peso",
                    icone = Icons.Outlined.FitnessCenter,
                    modifier = Modifier.weight(1f)
                )

                CampoInputPasso2(
                    valor = altura,
                    onValorChange = { altura = it },
                    placeholder = "Altura",
                    icone = Icons.Outlined.Height,
                    modifier = Modifier.weight(1f)
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
                    text = "Avançar",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
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

        // links de rodape
        Row(
            modifier = Modifier.padding(top = 4.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Termos de uso",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Laranja
            )
            Text(
                text = "•",
                fontSize = 11.sp,
                color = TextoSecundario
            )
            Text(
                text = "Privacidade",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Laranja
            )
            Text(
                text = "•",
                fontSize = 11.sp,
                color = TextoSecundario
            )
            Text(
                text = "Suporte TI",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Laranja
            )
        }
    }
}

@Composable
private fun CampoSeletorGenero(
    generoSelecionado: String,
    onGeneroSelecionado: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expandido by remember { mutableStateOf(false) }
    val opcoesGenero = listOf("Feminino", "Masculino", "Prefiro não informar")
    val shape = RoundedCornerShape(14.dp)

    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .clip(shape)
                .background(SuperficieEscura)
                .border(1.dp, Borda, shape)
                .clickable { expandido = !expandido }
                .padding(horizontal = 14.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Wc,
                    contentDescription = null,
                    tint = TextoSecundario,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = generoSelecionado.ifEmpty { "Gênero" },
                    color = if (generoSelecionado.isEmpty()) TextoSecundario else Color.White,
                    fontSize = 13.sp,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Outlined.KeyboardArrowDown,
                    contentDescription = "Selecionar gênero",
                    tint = TextoSecundario,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        DropdownMenu(
            expanded = expandido,
            onDismissRequest = { expandido = false },
            modifier = Modifier.background(Superficie)
        ) {
            opcoesGenero.forEach { opcao ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = opcao,
                            color = Color.White,
                            fontSize = 13.sp
                        )
                    },
                    onClick = {
                        onGeneroSelecionado(opcao)
                        expandido = false
                    }
                )
            }
        }
    }
}

@Composable
private fun CampoInputPasso2(
    valor: String,
    onValorChange: (String) -> Unit,
    placeholder: String,
    icone: ImageVector,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(14.dp)
    Box(
        modifier = modifier
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
        }
    }
}

// preview da tela
@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
private fun PreviewTelaCadastroPasso2() {
    GyemaTheme {
        TelaCadastroPasso2()
    }
}
