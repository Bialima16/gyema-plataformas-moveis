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
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.gyema.ui.tema.*
import androidx.compose.material.icons.outlined.Phone

private data class PerguntaProntidao(
    val id: Int,
    val texto: String
)

private val perguntasProntidao = listOf(
    PerguntaProntidao(1, "Já praticou musculação ou treinamento funcional?"),
    PerguntaProntidao(2, "Já sofreu alguma lesão muscular?"),
    PerguntaProntidao(3, "Apresenta alguma dor muscular ou articular?"),
    PerguntaProntidao(4, "Fez alguma cirurgia nos últimos meses?"),
    PerguntaProntidao(5, "Faz uso de algum medicamento?"),
    PerguntaProntidao(6, "Faz exames cardiológicos?"),
    PerguntaProntidao(7, "Tem pressão alta?"),
    PerguntaProntidao(8, "Tem diabetes?"),
    PerguntaProntidao(9, "Apresenta alguma restrição física ou dolorosa?"),
    PerguntaProntidao(10, "Já se sentiu mal durante a prática esportiva?")
)

@Composable
fun TelaCadastroPasso3(
    onVoltar: () -> Unit = {},
    onAvancar: () -> Unit = {},
    onConcluir: () -> Unit = onAvancar
) {
    var numeroContato by remember { mutableStateOf("") }
    val respostas = remember { mutableStateMapOf<Int, Boolean>() }

    // coluna com rolagem vertical
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Fundo)
            .verticalScroll(rememberScrollState())
            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 24.dp),
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
                Text("Cadastro", fontSize = 11.sp, color = TextoSecundario)
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
                        .background(Laranja),
                    contentAlignment = Alignment.Center
                ) {
                    Text("3", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Teste de\nprontidão",
                    fontSize = 11.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }

        // campo de contato
        val shapeCampo = RoundedCornerShape(14.dp)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .clip(shapeCampo)
                .background(SuperficieEscura)
                .border(1.dp, Borda, shapeCampo)
                .padding(horizontal = 14.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Phone,
                    contentDescription = null,
                    tint = TextoSecundario,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                BasicTextField(
                    value = numeroContato,
                    onValueChange = { numeroContato = it },
                    modifier = Modifier.weight(1f),
                    textStyle = TextStyle(
                        color = Color.White,
                        fontSize = 13.sp
                    ),
                    cursorBrush = SolidColor(Laranja),
                    singleLine = true,
                    decorationBox = { innerTextField ->
                        if (numeroContato.isEmpty()) {
                            Text(
                                text = "Número de contato",
                                color = TextoSecundario,
                                fontSize = 13.sp
                            )
                        }
                        innerTextField()
                    }
                )
            }
        }

        // perguntas de prontidao
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(SuperficieEscura)
                .border(1.dp, Borda, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                perguntasProntidao.forEach { p ->
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "${p.id}. ${p.texto}",
                            fontSize = 13.sp,
                            color = Color.White,
                            lineHeight = 18.sp
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(24.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable { respostas[p.id] = true }
                            ) {
                                RadioButton(
                                    selected = respostas[p.id] == true,
                                    onClick = { respostas[p.id] = true },
                                    colors = RadioButtonDefaults.colors(selectedColor = Laranja, unselectedColor = TextoSecundario)
                                )
                                Text("Sim", fontSize = 13.sp, color = Color.White)
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable { respostas[p.id] = false }
                            ) {
                                RadioButton(
                                    selected = respostas[p.id] == false,
                                    onClick = { respostas[p.id] = false },
                                    colors = RadioButtonDefaults.colors(selectedColor = Laranja, unselectedColor = TextoSecundario)
                                )
                                Text("Não", fontSize = 13.sp, color = Color.White)
                            }
                        }
                    }
                }
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
                .clickable(onClick = onConcluir),
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
            Text("Termos de uso", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Laranja)
            Text("•", fontSize = 11.sp, color = TextoSecundario)
            Text("Privacidade", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Laranja)
            Text("•", fontSize = 11.sp, color = TextoSecundario)
            Text("Suporte TI", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Laranja)
        }

        // espacamento inferior
        Spacer(modifier = Modifier.height(24.dp))
    }
}

// preview da tela
@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
private fun PreviewTelaCadastroPasso3() {
    GyemaTheme {
        TelaCadastroPasso3()
    }
}
