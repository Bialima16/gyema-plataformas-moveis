package br.com.gyema.ui.telas

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.gyema.ui.components.BarraProgresso
import br.com.gyema.ui.components.BotaoSino
import br.com.gyema.ui.components.CabecalhoAcademia
import br.com.gyema.ui.components.CardGyema
import br.com.gyema.ui.components.PontoLegenda
import br.com.gyema.ui.components.TituloSecao
import br.com.gyema.ui.tema.*

@Composable
fun TelaAcademiaAgora(onAbrirMapa: () -> Unit = {}) {
    Column(
        Modifier
            .fillMaxSize()
            .background(Fundo)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        CabecalhoAcademia("Academia agora") { BotaoSino() }

        Column(
            Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AnelLotacao()
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                PontoLegenda(Verde, "Livre — 35%", 10.sp)
                PontoLegenda(Laranja, "Ocupado — 55%", 10.sp)
                PontoLegenda(TextoSecundario, "Manutenção — 10%", 10.sp)
            }
        }

        TituloSecao("AMBIENTES")
        AmbienteCard("Musculação", 72, Laranja, onAbrirMapa)
        AmbienteCard("Cardio", 45, Verde, onAbrirMapa)

        Text(
            "Atualizado às 14:35",
            fontSize = 11.sp, color = TextoSecundario,
            modifier = Modifier.fillMaxWidth(),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

@Composable
private fun AnelLotacao() {
    Box(Modifier.size(180.dp), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val espessura = 22.dp.toPx()
            val topo = Offset(espessura / 2, espessura / 2)
            val tamanho = Size(size.width - espessura, size.height - espessura)
            drawArc(
                color = Borda, startAngle = 0f, sweepAngle = 360f, useCenter = false,
                topLeft = topo, size = tamanho, style = Stroke(espessura)
            )
            drawArc(
                color = Laranja, startAngle = -90f, sweepAngle = 360f * 0.65f, useCenter = false,
                topLeft = topo, size = tamanho, style = Stroke(espessura)
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("65%", fontSize = 34.sp, fontWeight = FontWeight.Bold)
            Text("LOTAÇÃO ATUAL", fontSize = 9.sp, color = TextoSecundario, letterSpacing = 1.sp)
        }
    }
}

@Composable
private fun AmbienteCard(nome: String, percentual: Int, cor: Color, onClick: () -> Unit) {
    CardGyema(Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), onClick = onClick) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(nome, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Text("$percentual%", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = cor)
            }
            BarraProgresso(percentual / 100f, cor)
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
private fun PreviewTelaAcademiaAgora() {
    GyemaTheme { TelaAcademiaAgora() }
}