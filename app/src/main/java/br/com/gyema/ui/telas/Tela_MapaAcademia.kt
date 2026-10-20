package br.com.gyema.ui.telas

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BusinessCenter
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.MonitorHeart
import androidx.compose.material.icons.outlined.SelfImprovement
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.gyema.ui.components.BotaoSino
import br.com.gyema.ui.components.CabecalhoAcademia
import br.com.gyema.ui.components.CardGyema
import br.com.gyema.ui.components.PontoLegenda
import br.com.gyema.ui.components.StatusAmbiente
import br.com.gyema.ui.tema.*

private data class Zona(val nome: String, val status: StatusAmbiente, val icone: ImageVector)

private val zonas = listOf(
    Zona("Musculação", StatusAmbiente.Ocupado, Icons.Outlined.FitnessCenter),
    Zona("Cardio", StatusAmbiente.Disponivel, Icons.Outlined.MonitorHeart),
    Zona("Alongar", StatusAmbiente.Disponivel, Icons.Outlined.SelfImprovement),
    Zona("Funcional", StatusAmbiente.Manutencao, Icons.Outlined.BusinessCenter)
)

@Composable
fun TelaMapaAcademia(onVoltar: () -> Unit = {}, onAbrirZona: () -> Unit = {}) {
    Column(
        Modifier
            .fillMaxSize()
            .background(Fundo)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CabecalhoAcademia("Mapa da academia", onVoltar) { BotaoSino() }

        CardGyema(Modifier.fillMaxWidth(), fundo = SuperficieEscura) {
            Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                zonas.chunked(2).forEach { linha ->
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        linha.forEach { ZonaCard(it, Modifier.weight(1f), onAbrirZona) }
                    }
                }
            }
        }

        CardGyema(Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
            Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    "LEGENDA DE STATUS",
                    fontSize = 10.sp, fontWeight = FontWeight.Bold,
                    color = TextoSecundario, letterSpacing = 0.5.sp
                )
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    StatusAmbiente.entries.forEach { PontoLegenda(it.cor, it.rotulo) }
                }
            }
        }
    }
}

@Composable
private fun ZonaCard(zona: Zona, modifier: Modifier, onClick: () -> Unit) {
    val shape = RoundedCornerShape(12.dp)
    Box(
        modifier
            .aspectRatio(1f)
            .clip(shape)
            .background(zona.status.fundo)
            .border(1.5.dp, zona.status.cor, shape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Icon(zona.icone, null, tint = zona.status.cor, modifier = Modifier.size(22.dp))
            Spacer(Modifier.height(2.dp))
            Text(zona.nome, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Text(zona.status.rotulo, fontSize = 10.sp, color = TextoSecundario)
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
private fun PreviewTelaMapaAcademia() {
    GyemaTheme { TelaMapaAcademia() }
}