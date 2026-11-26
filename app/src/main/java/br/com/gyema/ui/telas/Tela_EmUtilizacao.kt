package br.com.gyema.ui.telas

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.gyema.data.RepositorioAluno
import br.com.gyema.data.UtilizacaoAtiva
import br.com.gyema.data.mensagemErroAluno
import br.com.gyema.data.tentar
import br.com.gyema.ui.components.EstadoCarregando
import br.com.gyema.ui.components.EstadoErro
import br.com.gyema.ui.components.EstadoVazio
import br.com.gyema.ui.components.formatarCronometro
import br.com.gyema.ui.components.fracaoRestante
import br.com.gyema.ui.components.rememberAgora
import br.com.gyema.ui.components.segundosRestantes
import br.com.gyema.ui.tema.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun TelaEmUtilizacao(
    onVoltar: () -> Unit = {},
    onFinalizado: () -> Unit = {},
    onReportar: (equipamentoId: Int) -> Unit = {}
) {
    var carregando by remember { mutableStateOf(true) }
    var uso by remember { mutableStateOf<UtilizacaoAtiva?>(null) }
    var erro by remember { mutableStateOf<String?>(null) }
    var tentativa by remember { mutableIntStateOf(0) }
    var encerradoPorTempo by remember { mutableStateOf(false) }
    var finalizando by remember { mutableStateOf(false) }
    var aviso by remember { mutableStateOf<String?>(null) }
    val escopo = rememberCoroutineScope()

    LaunchedEffect(tentativa) {
        erro = null
        tentar { RepositorioAluno.minhaUtilizacaoAtiva() }
            .onSuccess { novo ->
                if (uso != null && novo == null) encerradoPorTempo = true
                uso = novo
            }
            .onFailure { erro = mensagemErroAluno(it) }
        carregando = false
    }

    val agora = rememberAgora()
    val atual = uso
    val restante = atual?.let { segundosRestantes(it.fimPrevisto, agora) } ?: 0L
    val acabou = atual != null && restante == 0L

    LaunchedEffect(acabou) {
        if (acabou) {
            delay(2000)
            tentativa++
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(FundoEscuro)
            .padding(16.dp)
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("Em utilização", fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            Box(
                Modifier.size(36.dp).clip(CircleShape).background(AzulBotao),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.Notifications, "Notificações", tint = TextoSecundario, modifier = Modifier.size(18.dp))
            }
        }

        when {
            erro != null -> EstadoErro(erro!!, onTentarNovamente = { tentativa++ })
            carregando -> EstadoCarregando()
            atual == null -> {
                EstadoVazio(
                    if (encerradoPorTempo) "Tempo de uso encerrado" else "Nenhum equipamento em uso",
                    if (encerradoPorTempo) "O tempo máximo acabou e a utilização foi finalizada automaticamente."
                    else "Escolha um equipamento disponível e toque em \"Iniciar uso\"."
                )
                Spacer(Modifier.weight(1f))
                BotaoTela("Voltar", AzulBotao, Color.White, onClick = onVoltar)
            }
            else -> {
                Spacer(Modifier.height(40.dp))

                val fracao = fracaoRestante(atual.inicio, atual.fimPrevisto, agora)
                Box(
                    Modifier.size(240.dp).align(Alignment.CenterHorizontally),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(Modifier.fillMaxSize()) {
                        val espessura = 14.dp.toPx()
                        val topo = Offset(espessura / 2, espessura / 2)
                        val tamanho = Size(size.width - espessura, size.height - espessura)
                        drawArc(
                            color = Borda, startAngle = 0f, sweepAngle = 360f, useCenter = false,
                            topLeft = topo, size = tamanho, style = Stroke(espessura)
                        )
                        drawArc(
                            color = Laranja, startAngle = -90f, sweepAngle = 360f * fracao, useCenter = false,
                            topLeft = topo, size = tamanho,
                            style = Stroke(espessura, cap = StrokeCap.Round)
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(formatarCronometro(restante), fontSize = 44.sp, fontWeight = FontWeight.Bold)
                        Text("TEMPO RESTANTE", fontSize = 10.sp, color = TextoSecundario, letterSpacing = 1.sp)
                    }
                }

                Spacer(Modifier.height(32.dp))

                val eq = atual.equipamentos
                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    eq.grupoMuscular?.let {
                        Box(
                            Modifier.clip(RoundedCornerShape(4.dp)).background(LaranjaEscuro)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) { Text(it.uppercase(), fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Laranja) }
                        Spacer(Modifier.height(10.dp))
                    }
                    Text(eq.nome, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Setor de ${eq.setores.nome} — ${eq.codigo}",
                        fontSize = 12.sp, color = TextoSecundario, textAlign = TextAlign.Center
                    )
                    aviso?.let {
                        Spacer(Modifier.height(8.dp))
                        Text(it, fontSize = 12.sp, color = Laranja, textAlign = TextAlign.Center)
                    }
                }

                Spacer(Modifier.weight(1f))

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    BotaoTela("Voltar ao Treino", AzulBotao, Color.White, onClick = onVoltar)
                    BotaoTela(if (finalizando) "Finalizando..." else "Finalizar utilização", Laranja, Color.White) {
                        if (finalizando) return@BotaoTela
                        finalizando = true
                        aviso = null
                        escopo.launch {
                            tentar { RepositorioAluno.finalizarUso(atual) }
                                .onSuccess { onFinalizado() }
                                .onFailure { aviso = mensagemErroAluno(it) }
                            finalizando = false
                        }
                    }
                    BotaoTela("Reportar problema", Color.Transparent, TextoSecundario, comBorda = true) { onReportar(eq.id) }
                }
            }
        }
    }
}

@Composable
private fun BotaoTela(
    texto: String,
    fundo: Color,
    corTexto: Color,
    comBorda: Boolean = false,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(10.dp)
    Box(
        Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(shape)
            .background(fundo)
            .then(if (comBorda) Modifier.border(1.dp, Borda, shape) else Modifier)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(texto, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = corTexto)
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
private fun PreviewTelaEmUtilizacao() {
    GyemaTheme { TelaEmUtilizacao() }
}
