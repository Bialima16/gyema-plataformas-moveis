package br.com.gyema.ui.telas

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.gyema.data.ExecucaoIniciada
import br.com.gyema.data.FichaDetalheAluno
import br.com.gyema.data.RepositorioAluno
import br.com.gyema.data.mensagemDeErro
import br.com.gyema.data.tentar
import br.com.gyema.ui.components.BotaoContorno
import br.com.gyema.ui.components.BotaoPrincipal
import br.com.gyema.ui.components.BotaoVoltarQuadrado
import br.com.gyema.ui.components.CardGyema
import br.com.gyema.ui.components.EstadoCarregando
import br.com.gyema.ui.components.EstadoErro
import br.com.gyema.ui.components.EstadoVazio
import br.com.gyema.ui.components.formatarCronometro
import br.com.gyema.ui.components.numeroBr
import br.com.gyema.ui.components.contagem
import br.com.gyema.ui.tema.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun TelaExecucao(
    fichaId: Long,
    onVoltar: () -> Unit = {},
    onConcluido: (execucaoId: Long) -> Unit = {}
) {
    var ficha by remember { mutableStateOf<FichaDetalheAluno?>(null) }
    var erro by remember { mutableStateOf<String?>(null) }
    var tentativa by remember { mutableIntStateOf(0) }

    var execucao by remember { mutableStateOf<ExecucaoIniciada?>(null) }
    var indice by remember { mutableIntStateOf(0) }
    var serie by remember { mutableIntStateOf(1) }
    var carga by remember { mutableDoubleStateOf(0.0) }
    var repeticoes by remember { mutableIntStateOf(0) }
    var seriesFeitas by remember { mutableIntStateOf(0) }
    var completo by remember { mutableStateOf(false) }

    var descansoTotal by remember { mutableIntStateOf(0) }
    var descansoRestante by remember { mutableIntStateOf(0) }
    var pausado by remember { mutableStateOf(false) }

    var salvando by remember { mutableStateOf(false) }
    var aviso by remember { mutableStateOf<String?>(null) }
    var confirmarSaida by remember { mutableStateOf(false) }
    val escopo = rememberCoroutineScope()

    LaunchedEffect(fichaId, tentativa) {
        erro = null
        tentar { RepositorioAluno.buscarFicha(fichaId) }
            .onSuccess { ficha = it }
            .onFailure { erro = mensagemDeErro(it) }
    }

    val itens = ficha?.itens.orEmpty()
    val item = itens.getOrNull(indice)

    LaunchedEffect(item?.id) {
        if (item != null) {
            carga = item.cargaKg ?: 0.0
            repeticoes = item.repeticoes
        }
    }

    LaunchedEffect(descansoRestante, pausado) {
        if (descansoRestante > 0 && !pausado) {
            delay(1000)
            descansoRestante--
        }
    }

    BackHandler {
        if (seriesFeitas > 0) confirmarSaida = true else onVoltar()
    }

    fun avancar() {
        val atual = item ?: return
        when {
            serie < atual.series -> serie++
            indice < itens.lastIndex -> { indice++; serie = 1 }
            else -> completo = true
        }
    }

    fun concluirSerie() {
        val atual = item ?: return
        salvando = true
        aviso = null
        escopo.launch {
            tentar {
                val exec = execucao ?: RepositorioAluno.iniciarExecucao(fichaId).also { execucao = it }
                RepositorioAluno.registrarSerie(
                    exec.id, atual.exercicios.id, serie, repeticoes,
                    carga.takeIf { it > 0 }
                )
            }
                .onSuccess {
                    seriesFeitas++
                    avancar()
                    descansoTotal = atual.descansoSeg ?: 60
                    descansoRestante = if (completo) 0 else descansoTotal
                    pausado = false
                }
                .onFailure { aviso = mensagemDeErro(it) }
            salvando = false
        }
    }

    fun finalizar() {
        val exec = execucao ?: return
        salvando = true
        escopo.launch {
            tentar { RepositorioAluno.finalizarExecucao(exec) }
                .onSuccess { onConcluido(exec.id) }
                .onFailure { aviso = mensagemDeErro(it) }
            salvando = false
        }
    }

    fun descartar() {
        val exec = execucao
        if (exec == null) { onVoltar(); return }
        salvando = true
        escopo.launch {
            tentar { RepositorioAluno.descartarExecucao(exec.id) }
                .onSuccess { onVoltar() }
                .onFailure { aviso = mensagemDeErro(it) }
            salvando = false
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(Fundo)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            BotaoVoltarQuadrado({ if (seriesFeitas > 0) confirmarSaida = true else onVoltar() })
            Spacer(Modifier.width(12.dp))
            Text("Execução", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            Text(contagem(seriesFeitas, "série feita", "séries feitas"), fontSize = 11.sp, color = TextoSecundario)
        }

        when {
            erro != null -> EstadoErro(erro!!, onTentarNovamente = { tentativa++ })
            ficha == null -> EstadoCarregando()
            itens.isEmpty() -> EstadoVazio("Ficha sem exercícios", "Peça ao seu instrutor para completar a ficha.")

            completo -> {
                Column(Modifier.fillMaxWidth().padding(vertical = 32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Outlined.CheckCircle, null, tint = Verde, modifier = Modifier.size(56.dp))
                    Spacer(Modifier.height(12.dp))
                    Text("Todas as séries concluídas!", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("Toque em finalizar para salvar o treino.", fontSize = 13.sp, color = TextoSecundario)
                }
                aviso?.let { Text(it, color = Laranja, fontSize = 12.sp) }
                BotaoPrincipal(if (salvando) "Salvando..." else "Finalizar treino",
                    onClick = { if (!salvando) finalizar() }, modifier = Modifier.fillMaxWidth())
            }

            item != null -> {
                Column {
                    Text(item.exercicios.nome, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Text(
                        "Série $serie de ${item.series} • Exercício ${indice + 1} de ${itens.size}",
                        fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Laranja
                    )
                    Text(
                        "Prescrito: ${item.repeticoes} repetições" + (item.cargaKg?.let { " com ${numeroBr(it)} kg" } ?: ""),
                        fontSize = 11.sp, color = TextoSecundario
                    )
                    item.tecnica?.takeIf { it.isNotBlank() }?.let { Text("Técnica: $it", fontSize = 11.sp, color = Laranja) }
                }

                // Troque por Image(painterResource(R.drawable.supino_reto), ...) quando tiver o arquivo
                val shape = RoundedCornerShape(12.dp)
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clip(shape)
                        .background(SuperficieEscura)
                        .border(1.dp, Borda, shape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.FitnessCenter, null, tint = TextoApagado, modifier = Modifier.size(40.dp))
                }

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Contador(
                        "Carga (kg)", numeroBr(carga),
                        onMenos = { carga = (carga - 2.5).coerceAtLeast(0.0) },
                        onMais = { carga += 2.5 },
                        modifier = Modifier.weight(1f)
                    )
                    Contador(
                        "Repetições", repeticoes.toString(),
                        onMenos = { if (repeticoes > 1) repeticoes-- },
                        onMais = { repeticoes++ },
                        modifier = Modifier.weight(1f)
                    )
                }

                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("TEMPO DE DESCANSO", fontSize = 10.sp, color = TextoSecundario, letterSpacing = 1.sp)
                    Spacer(Modifier.height(10.dp))
                    AnelDescanso(
                        texto = formatarCronometro(descansoRestante.toLong()),
                        fracao = if (descansoTotal == 0) 0f else descansoRestante / descansoTotal.toFloat(),
                        legenda = when {
                            descansoRestante == 0 -> "pronto"
                            pausado -> "pausado"
                            else -> "restante"
                        }
                    )
                }

                aviso?.let { Text(it, color = Laranja, fontSize = 12.sp) }

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    BotaoContorno(
                        if (pausado) "Retomar" else "Pausar",
                        onClick = { if (descansoRestante > 0) pausado = !pausado },
                        modifier = Modifier.weight(1f)
                    )
                    BotaoPrincipal(
                        if (salvando) "Salvando..." else "Concluir série",
                        onClick = { if (!salvando) concluirSerie() },
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    if (indice < itens.lastIndex) {
                        Text("Pular exercício", fontSize = 12.sp, color = TextoSecundario,
                            modifier = Modifier.clickable { indice++; serie = 1; descansoRestante = 0 })
                    } else Spacer(Modifier.width(1.dp))
                    if (seriesFeitas > 0) {
                        Text("Finalizar treino", fontSize = 12.sp, color = Laranja, fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { if (!salvando) finalizar() })
                    }
                }
            }
        }
    }

    if (confirmarSaida) {
        AlertDialog(
            onDismissRequest = { confirmarSaida = false },
            containerColor = Superficie,
            title = { Text("Sair do treino?", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Você já registrou ${contagem(seriesFeitas, "série", "séries")}. Pode finalizar e salvar o treino, " +
                        "ou sair sem salvar (as séries serão apagadas).",
                    fontSize = 13.sp, color = TextoSecundario, textAlign = TextAlign.Start
                )
            },
            confirmButton = {
                TextButton(onClick = { confirmarSaida = false; finalizar() }) {
                    Text("Finalizar e salvar", color = Laranja, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                Row {
                    TextButton(onClick = { confirmarSaida = false; descartar() }) { Text("Sair sem salvar", color = TextoSecundario) }
                    TextButton(onClick = { confirmarSaida = false }) { Text("Continuar", color = TextoSecundario) }
                }
            }
        )
    }
}

@Composable
private fun Contador(
    rotulo: String,
    valor: String,
    onMenos: () -> Unit,
    onMais: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(rotulo, fontSize = 11.sp, color = TextoSecundario)
        CardGyema(Modifier.fillMaxWidth(), fundo = SuperficieEscura, shape = RoundedCornerShape(10.dp)) {
            Row(
                Modifier.fillMaxWidth().padding(vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(Modifier.weight(1f).clickable(onClick = onMenos), contentAlignment = Alignment.Center) {
                    Text("-", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Laranja)
                }
                Text(valor, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Box(Modifier.weight(1f).clickable(onClick = onMais), contentAlignment = Alignment.Center) {
                    Text("+", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Laranja)
                }
            }
        }
    }
}

@Composable
private fun AnelDescanso(texto: String, fracao: Float, legenda: String) {
    Box(Modifier.size(104.dp), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val espessura = 5.dp.toPx()
            val topo = Offset(espessura / 2, espessura / 2)
            val tamanho = Size(size.width - espessura, size.height - espessura)
            drawArc(
                color = Borda, startAngle = 0f, sweepAngle = 360f, useCenter = false,
                topLeft = topo, size = tamanho, style = Stroke(espessura)
            )
            drawArc(
                color = Laranja, startAngle = -90f, sweepAngle = 360f * fracao, useCenter = false,
                topLeft = topo, size = tamanho, style = Stroke(espessura, cap = StrokeCap.Round)
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(texto, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(legenda, fontSize = 10.sp, color = Laranja)
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
private fun PreviewTelaExecucao() {
    GyemaTheme { TelaExecucao(fichaId = 0) }
}
