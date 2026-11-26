package br.com.gyema.ui.telas

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.ChevronLeft
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.gyema.data.CargaMensalAluno
import br.com.gyema.data.ExecucaoResumo
import br.com.gyema.data.RepositorioAluno
import br.com.gyema.data.mensagemDeErro
import br.com.gyema.data.tentar
import br.com.gyema.ui.components.CardGyema
import br.com.gyema.ui.components.EstadoCarregando
import br.com.gyema.ui.components.EstadoErro
import br.com.gyema.ui.components.EstadoVazio
import br.com.gyema.ui.components.TituloSecao
import br.com.gyema.ui.components.formatarData
import br.com.gyema.ui.components.minutosEntre
import br.com.gyema.ui.components.numeroBr
import br.com.gyema.ui.components.contagem
import br.com.gyema.ui.tema.*
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Locale

private data class DadosEvolucao(
    val diasComTreino: Set<LocalDate>,
    val cargas: List<CargaMensalAluno>,
    val ultimos: List<ExecucaoResumo>
)

private val ptBR = Locale("pt", "BR")

@Composable
fun TelaEvolucao(onVoltar: () -> Unit = {}) {
    var dados by remember { mutableStateOf<DadosEvolucao?>(null) }
    var erro by remember { mutableStateOf<String?>(null) }
    var tentativa by remember { mutableIntStateOf(0) }
    var mes by remember { mutableStateOf(YearMonth.now()) }

    LaunchedEffect(tentativa) {
        erro = null
        tentar {
            val fuso = ZoneId.systemDefault()
            DadosEvolucao(
                RepositorioAluno.minhasExecucoes().mapNotNull {
                    runCatching { OffsetDateTime.parse(it.inicio).atZoneSameInstant(fuso).toLocalDate() }.getOrNull()
                }.toSet(),
                RepositorioAluno.minhasCargasMensais(),
                RepositorioAluno.historicoTreinos(limite = 5)
            )
        }
            .onSuccess { dados = it }
            .onFailure { erro = mensagemDeErro(it) }
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(Fundo)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Topo com botão voltar
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Box(
                Modifier
                    .align(Alignment.CenterStart)
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(Roxo)
                    .clickable(onClick = onVoltar),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, "Voltar", tint = Color.White)
            }
            Text("Minha evolução", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        val atual = dados
        when {
            erro != null -> EstadoErro(erro!!, onTentarNovamente = { tentativa++ })
            atual == null -> EstadoCarregando()
            else -> {
                Calendario(mes, atual.diasComTreino, onAnterior = { mes = mes.minusMonths(1) }, onProximo = { mes = mes.plusMonths(1) })

                TituloSecao("EVOLUÇÃO DE CARGA")
                val ultimosMeses = atual.cargas.filter { (it.cargaMaxKg ?: 0.0) > 0 }.take(6).reversed()
                if (ultimosMeses.isEmpty()) {
                    EstadoVazio("Sem cargas ainda", "Registre séries com carga na execução do treino.")
                } else {
                    GraficoCarga(ultimosMeses)
                }

                TituloSecao("ÚLTIMOS TREINOS")
                if (atual.ultimos.isEmpty()) {
                    EstadoVazio("Nenhum treino feito", "Seus treinos aparecem aqui.")
                } else {
                    atual.ultimos.forEach { TreinoCard(it) }
                }
            }
        }
    }
}

@Composable
private fun Calendario(mes: YearMonth, diasComTreino: Set<LocalDate>, onAnterior: () -> Unit, onProximo: () -> Unit) {
    val diasSemana = listOf("D", "S", "T", "Q", "Q", "S", "S")
    val primeiro = mes.atDay(1)
    val deslocamento = primeiro.dayOfWeek.value % 7
    val totalCelulas = ((deslocamento + mes.lengthOfMonth() + 6) / 7) * 7
    val celulas = (0 until totalCelulas).map { primeiro.minusDays(deslocamento.toLong()).plusDays(it.toLong()) }
    val titulo = mes.month.getDisplayName(TextStyle.FULL, ptBR).replaceFirstChar { it.uppercase() } + " ${mes.year}"
    val treinosNoMes = diasComTreino.count { YearMonth.from(it) == mes }

    CardGyema(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(titulo, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text("${contagem(treinosNoMes, "dia", "dias")} com treino", fontSize = 11.sp, color = TextoSecundario)
                }
                Icon(Icons.Outlined.ChevronLeft, "Mês anterior", tint = TextoSecundario,
                    modifier = Modifier.size(28.dp).clickable(onClick = onAnterior))
                Spacer(Modifier.width(8.dp))
                Icon(Icons.Outlined.ChevronRight, "Próximo mês", tint = TextoSecundario,
                    modifier = Modifier.size(28.dp).clickable(onClick = onProximo))
            }
            Row(Modifier.fillMaxWidth()) {
                diasSemana.forEach {
                    Text(it, fontSize = 11.sp, color = TextoSecundario, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                }
            }
            celulas.chunked(7).forEach { linha ->
                Row(Modifier.fillMaxWidth()) {
                    linha.forEach { data ->
                        val doMes = YearMonth.from(data) == mes
                        DiaCalendario(data.dayOfMonth, doMes, doMes && data in diasComTreino, Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun DiaCalendario(dia: Int, doMes: Boolean, marcado: Boolean, modifier: Modifier) {
    Box(modifier.height(34.dp), contentAlignment = Alignment.Center) {
        Box(
            Modifier
                .size(28.dp)
                .background(if (marcado) Laranja else Color.Transparent, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                dia.toString(), fontSize = 12.sp,
                fontWeight = if (marcado) FontWeight.Bold else FontWeight.Normal,
                color = if (doMes) Color.White else TextoApagado
            )
        }
    }
}

@Composable
private fun GraficoCarga(cargas: List<CargaMensalAluno>) {
    val maxKg = cargas.maxOf { it.cargaMaxKg ?: 0.0 }.coerceAtLeast(1.0)
    CardGyema(Modifier.fillMaxWidth()) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            cargas.forEach { c ->
                val kg = c.cargaMaxKg ?: 0.0
                val rotulo = runCatching {
                    LocalDate.parse(c.mes.take(10)).month.getDisplayName(TextStyle.SHORT, ptBR)
                        .replace(".", "").replaceFirstChar { it.uppercase() }
                }.getOrDefault(c.mes)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("${numeroBr(kg)} kg", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Laranja)
                    Spacer(Modifier.height(4.dp))
                    Box(
                        Modifier
                            .width(30.dp)
                            .height((64 * kg / maxKg).dp)
                            .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                            .background(Laranja)
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(rotulo, fontSize = 12.sp, color = TextoSecundario)
                }
            }
        }
    }
}

@Composable
private fun TreinoCard(t: ExecucaoResumo) {
    val exercicios = t.series.map { it.exercicios.nome }.distinct()
    CardGyema(Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier.clip(RoundedCornerShape(6.dp)).background(RoxoEscuro)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) { Text(formatarData(t.inicio, "dd/MM"), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Laranja) }
                Spacer(Modifier.width(8.dp))
                Text(
                    t.ficha?.nome ?: "Treino livre", fontSize = 13.sp, fontWeight = FontWeight.Bold,
                    maxLines = 1, overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Icon(Icons.Outlined.AccessTime, null, modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(3.dp))
                Text(if (t.fim == null) "em aberto" else "${minutosEntre(t.inicio, t.fim)}min", fontSize = 12.sp)
            }
            Text(
                if (exercicios.isEmpty()) "Nenhuma série registrada" else exercicios.joinToString(", "),
                fontSize = 11.sp, color = TextoSecundario,
                maxLines = 1, overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
private fun PreviewTelaEvolucao() {
    GyemaTheme { TelaEvolucao() }
}
