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
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.gyema.ui.components.CardGyema
import br.com.gyema.ui.components.TituloSecao
import br.com.gyema.ui.tema.*

private data class Carga(val mes: String, val kg: Int)
private data class Treino(val data: String, val titulo: String, val duracao: String, val exercicios: String)

private val cargas = listOf(Carga("Jun", 45), Carga("Jul", 60), Carga("Ago", 75), Carga("Set", 90))

private val treinos = listOf(
    Treino("25/09", "Treino A — Peito e Tríceps", "45min", "Supino reto, Supino inclinado, Crucifixo..."),
    Treino("23/09", "Treino B — Costas e Bíceps", "50min", "Puxada frontal, Remada curvada, Rosca direta..."),
    Treino("20/09", "Treino C — Pernas Completo", "40min", "Agachamento, Leg press, Cadeira extensora...")
)

@Composable
fun TelaEvolucao(onVoltar: () -> Unit = {}) {
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

        Calendario()

        TituloSecao("EVOLUÇÃO DE CARGA")
        GraficoCarga()

        TituloSecao("ÚLTIMOS TREINOS")
        treinos.forEach { TreinoCard(it) }
    }
}

@Composable
private fun Calendario() {
    val diasSemana = listOf("D", "S", "T", "Q", "Q", "S", "S")
    // Setembro/2025: 31 (ago) + 1..30 + 1..4 (out)
    val celulas = listOf(31) + (1..30).toList() + (1..4).toList()
    val marcados = setOf(2, 5, 8, 12, 15, 19, 20, 23, 25)

    CardGyema(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Setembro 2025", fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Icon(Icons.Outlined.ChevronLeft, null, tint = TextoSecundario, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Icon(Icons.Outlined.ChevronRight, null, tint = TextoSecundario, modifier = Modifier.size(20.dp))
            }
            Row(Modifier.fillMaxWidth()) {
                diasSemana.forEach {
                    Text(
                        it, fontSize = 11.sp, color = TextoSecundario,
                        modifier = Modifier.weight(1f),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
            celulas.withIndex().chunked(7).forEach { linha ->
                Row(Modifier.fillMaxWidth()) {
                    linha.forEach { (i, dia) ->
                        val doMes = i in 1..30
                        DiaCalendario(dia, doMes, doMes && dia in marcados, Modifier.weight(1f))
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
private fun GraficoCarga() {
    val maxKg = cargas.maxOf { it.kg }
    CardGyema(Modifier.fillMaxWidth()) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            cargas.forEach { c ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("${c.kg} kg", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Laranja)
                    Spacer(Modifier.height(4.dp))
                    Box(
                        Modifier
                            .width(30.dp)
                            .height((64 * c.kg / maxKg.toFloat()).dp)
                            .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                            .background(Laranja)
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(c.mes, fontSize = 12.sp, color = TextoSecundario)
                }
            }
        }
    }
}

@Composable
private fun TreinoCard(t: Treino) {
    CardGyema(Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier.clip(RoundedCornerShape(6.dp)).background(RoxoEscuro)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) { Text(t.data, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Laranja) }
                Spacer(Modifier.width(8.dp))
                Text(
                    t.titulo, fontSize = 13.sp, fontWeight = FontWeight.Bold,
                    maxLines = 1, overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Icon(Icons.Outlined.AccessTime, null, modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(3.dp))
                Text(t.duracao, fontSize = 12.sp)
            }
            Text(
                t.exercicios, fontSize = 11.sp, color = TextoSecundario,
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