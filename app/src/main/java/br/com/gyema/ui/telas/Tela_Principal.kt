package br.com.gyema.ui.telas

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import br.com.gyema.ui.components.CardGyema
import br.com.gyema.ui.components.TituloSecao
import br.com.gyema.ui.tema.*

private data class DiaSemana(val sigla: String, val numero: Int, val treinou: Boolean)

private val semana = listOf(
    DiaSemana("Seg", 15, true),
    DiaSemana("Ter", 16, false),
    DiaSemana("Qua", 17, true),
    DiaSemana("Qui", 18, false),
    DiaSemana("Sex", 19, true),
    DiaSemana("Sáb", 20, false)
)

@Composable
fun TelaPrincipal(onMeuTreino: () -> Unit = {}) {
    Column(
        Modifier
            .fillMaxSize()
            .background(Fundo)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Cabecalho()

        Column {
            Text("Olá, João! 👋", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text("Vamos para mais um treino hoje?", fontSize = 13.sp, color = TextoSecundario)
        }

        LotacaoCard()

        TituloSecao("ACESSO RÁPIDO")
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Atalho("Meu treino", Icons.Outlined.FitnessCenter, Modifier.weight(1f), onMeuTreino)
                Atalho("Academia", Icons.Outlined.LocationOn, Modifier.weight(1f))
                Atalho("Comunidade", Icons.Outlined.Groups, Modifier.weight(1f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                AtalhoLinha("Chatbot", Icons.Outlined.ChatBubbleOutline, Modifier.weight(1f))
                AtalhoLinha("Sugestões", Icons.Outlined.ThumbUp, Modifier.weight(1f))
            }
        }

        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.weight(1f)) { TituloSecao("MINHA SEMANA") }
            Text("3 / 5 Dias", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Laranja)
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            semana.forEach { DiaChip(it, Modifier.weight(1f)) }
        }

        TituloSecao("QUANT. TREINOS REALIZADOS:")
        Text(
            buildAnnotatedString {
                withStyle(SpanStyle(color = Laranja)) { append("12") }
                append(" / 30 Mensal")
            },
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun Cabecalho() {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Row(Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
            // Troque por Image(painterResource(R.drawable.logo), ...) se tiver o logo
            Box(
                Modifier.size(28.dp).border(2.dp, Laranja, CircleShape),
                contentAlignment = Alignment.Center
            ) { Icon(Icons.Outlined.FitnessCenter, null, tint = Laranja, modifier = Modifier.size(14.dp)) }
            Spacer(Modifier.width(8.dp))
            Text("GYEMA", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
        }
        CardGyema(shape = RoundedCornerShape(10.dp)) {
            Icon(
                Icons.Outlined.Notifications, "Notificações",
                tint = TextoSecundario,
                modifier = Modifier.padding(8.dp).size(18.dp)
            )
        }
    }
}

@Composable
private fun LotacaoCard() {
    CardGyema(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.Groups, null, tint = Laranja, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Lotação da Academia", fontSize = 13.sp, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
                Row(
                    Modifier
                        .clip(RoundedCornerShape(50))
                        .background(Verde.copy(alpha = 0.18f))
                        .padding(horizontal = 8.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(Modifier.size(6.dp).background(Verde, CircleShape))
                    Spacer(Modifier.width(4.dp))
                    Text("AO VIVO", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Verde)
                }
            }
            Row(verticalAlignment = Alignment.Bottom) {
                Text("65%", fontSize = 26.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Text("Moderado (Ideal para treinar)", fontSize = 11.sp, color = TextoSecundario)
            }
            Box(
                Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(50)).background(Borda)
            ) {
                Box(Modifier.fillMaxWidth(0.65f).fillMaxHeight().background(Laranja))
            }
        }
    }
}

@Composable
private fun Atalho(titulo: String, icone: ImageVector, modifier: Modifier, onClick: () -> Unit = {}) {
    CardGyema(modifier, onClick = onClick) {
        Column(
            Modifier.fillMaxWidth().padding(vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(Laranja.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) { Icon(icone, null, tint = Laranja, modifier = Modifier.size(20.dp)) }
            Text(titulo, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun AtalhoLinha(titulo: String, icone: ImageVector, modifier: Modifier) {
    CardGyema(modifier, onClick = {}) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icone, null, tint = Laranja, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text(titulo, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun DiaChip(dia: DiaSemana, modifier: Modifier) {
    val shape = RoundedCornerShape(10.dp)
    Column(
        modifier
            .clip(shape)
            .background(if (dia.treinou) Laranja else Superficie)
            .padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            dia.sigla, fontSize = 11.sp,
            fontWeight = if (dia.treinou) FontWeight.Bold else FontWeight.Normal,
            color = if (dia.treinou) Color.White else TextoSecundario
        )
        Text(dia.numero.toString(), fontSize = 15.sp, fontWeight = FontWeight.Bold)
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
private fun PreviewTelaPrincipal() {
    GyemaTheme { TelaPrincipal() }
}
