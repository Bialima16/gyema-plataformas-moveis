package br.com.gyema.ui.telas

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.Dns
import androidx.compose.material.icons.outlined.HorizontalRule
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.QrCode2
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.gyema.ui.components.AbasSegmentadas
import br.com.gyema.ui.components.BotaoPrincipal
import br.com.gyema.ui.components.CabecalhoAcademia
import br.com.gyema.ui.components.CardGyema
import br.com.gyema.ui.components.StatusAmbiente
import br.com.gyema.ui.tema.*

private data class Equipamento(
    val nome: String,
    val setor: String,
    val status: StatusAmbiente,
    val icone: ImageVector
)

private val equipamentos = listOf(
    Equipamento("Supino Reto", "Musculação", StatusAmbiente.Disponivel, Icons.Outlined.Inventory2),
    Equipamento("Smith Machine", "Musculação", StatusAmbiente.Ocupado, Icons.Outlined.Dns),
    Equipamento("Esteira 3", "Cardio", StatusAmbiente.Disponivel, Icons.Outlined.Bolt),
    Equipamento("Hack Squat", "Musculação", StatusAmbiente.Manutencao, Icons.Outlined.HorizontalRule)
)

@Composable
fun TelaEquipamentos(onVoltar: () -> Unit = {}) {
    Column(
        Modifier
            .fillMaxSize()
            .background(Fundo)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        CabecalhoAcademia("Equipamentos", onVoltar) {
            CardGyema(Modifier.size(36.dp), shape = RoundedCornerShape(10.dp)) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Icon(Icons.Outlined.QrCode2, "Ler QR Code", tint = Laranja, modifier = Modifier.size(20.dp))
                }
            }
        }

        AbasSegmentadas(listOf("Todos", "Disponíveis", "Ocupados"), selecionado = 0)

        UsandoAgoraCard()

        equipamentos.forEach { EquipamentoCard(it) }
    }
}

@Composable
private fun UsandoAgoraCard() {
    val shape = RoundedCornerShape(14.dp)
    Column(
        Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(Superficie)
            .border(1.5.dp, Laranja, shape)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("VOCÊ ESTÁ USANDO", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Laranja, letterSpacing = 0.5.sp)
                Text("Leg Press 45°", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            MiniCronometro()
        }
        BotaoPrincipal("Finalizar utilização", onClick = {}, modifier = Modifier.fillMaxWidth())
    }
}

@Composable
private fun MiniCronometro() {
    Box(Modifier.size(44.dp), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val espessura = 3.5.dp.toPx()
            val topo = Offset(espessura / 2, espessura / 2)
            val tamanho = Size(size.width - espessura, size.height - espessura)
            drawArc(
                color = Borda, startAngle = 0f, sweepAngle = 360f, useCenter = false,
                topLeft = topo, size = tamanho, style = Stroke(espessura)
            )
            drawArc(
                color = Laranja, startAngle = -90f, sweepAngle = 270f, useCenter = false,
                topLeft = topo, size = tamanho, style = Stroke(espessura, cap = StrokeCap.Round)
            )
        }
        Text("08:45", fontSize = 9.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun EquipamentoCard(e: Equipamento) {
    CardGyema(Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(e.status.cor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(e.icone, null, tint = e.status.cor, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(e.nome, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text(e.setor, fontSize = 11.sp, color = TextoSecundario)
            }
            Box(
                Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(e.status.cor.copy(alpha = 0.18f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(e.status.rotulo.uppercase(), fontSize = 9.sp, fontWeight = FontWeight.Bold, color = e.status.cor)
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
private fun PreviewTelaEquipamentos() {
    GyemaTheme { TelaEquipamentos() }
}