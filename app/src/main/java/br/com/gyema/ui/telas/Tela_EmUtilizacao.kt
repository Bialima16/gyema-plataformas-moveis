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
import androidx.compose.runtime.Composable
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
import br.com.gyema.ui.tema.*

@Composable
fun TelaEmUtilizacao(
    onVoltar: () -> Unit = {},
    onFinalizar: () -> Unit = {},
    onReportar: () -> Unit = {}
) {
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

        Spacer(Modifier.height(40.dp))

        // Anel do cronômetro (visual estático: 270° preenchidos)
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
                    color = Laranja, startAngle = -90f, sweepAngle = 270f, useCenter = false,
                    topLeft = topo, size = tamanho,
                    style = Stroke(espessura, cap = StrokeCap.Round)
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("08:45", fontSize = 44.sp, fontWeight = FontWeight.Bold)
                Text("TEMPO RESTANTE", fontSize = 10.sp, color = TextoSecundario, letterSpacing = 1.sp)
            }
        }

        Spacer(Modifier.height(32.dp))

        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                Modifier.clip(RoundedCornerShape(4.dp)).background(LaranjaEscuro)
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) { Text("PERNAS", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Laranja) }
            Spacer(Modifier.height(10.dp))
            Text("Leg Press 45°", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text(
                "Setor de Musculação — Ficha A",
                fontSize = 12.sp, color = TextoSecundario, textAlign = TextAlign.Center
            )
        }

        Spacer(Modifier.weight(1f))

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            BotaoTela("Voltar ao Treino", AzulBotao, Color.White, onClick = onVoltar)
            BotaoTela("Finalizar utilização", Laranja, Color.White, onClick = onFinalizar)
            BotaoTela("Reportar problema", Color.Transparent, TextoSecundario, comBorda = true, onClick = onReportar)
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