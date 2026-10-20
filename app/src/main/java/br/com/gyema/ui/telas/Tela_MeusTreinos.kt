package br.com.gyema.ui.telas

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.gyema.ui.components.AbasSegmentadas
import br.com.gyema.ui.tema.*

private data class FichaTreino(
    val titulo: String,
    val grupo: String,
    val exercicios: Int,
    val doDia: Boolean = false
)

private val fichas = listOf(
    FichaTreino("Treino A — Ficha Principal", "Peito e Tríceps", 6, doDia = true),
    FichaTreino("Treino B — Ficha Principal", "Costas e Bíceps", 5),
    FichaTreino("Treino C — Ficha Principal", "Pernas", 6),
    FichaTreino("Treino D — Ficha Principal", "Ombros e Abdômen", 5)
)

@Composable
fun TelaMeusTreinos(onAbrirTreino: () -> Unit = {}) {
    Column(
        Modifier
            .fillMaxSize()
            .background(Fundo)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Meus treinos", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        AbasSegmentadas(listOf("Atual", "Histórico"), selecionado = 0)
        fichas.forEach { FichaCard(it, onAbrirTreino) }
    }
}

@Composable
private fun FichaCard(ficha: FichaTreino, onClick: () -> Unit) {
    val shape = RoundedCornerShape(14.dp)
    val corBorda = if (ficha.doDia) Laranja else Borda

    Column(
        Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(Superficie)
            .border(if (ficha.doDia) 1.5.dp else 1.dp, corBorda, shape)
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                ficha.titulo, fontSize = 14.sp, fontWeight = FontWeight.Bold,
                maxLines = 1, overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            if (ficha.doDia) {
                Spacer(Modifier.width(6.dp))
                Box(
                    Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(LaranjaEscuro)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text("TREINO DO DIA", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Laranja)
                }
            }
        }
        Text(ficha.grupo, fontSize = 12.sp, color = TextoSecundario)
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Outlined.FitnessCenter, null, tint = Laranja, modifier = Modifier.size(14.dp))
            Spacer(Modifier.width(6.dp))
            Text("${ficha.exercicios} exercícios", fontSize = 11.sp, color = TextoSecundario, modifier = Modifier.weight(1f))
            Box(
                Modifier
                    .size(24.dp)
                    .background(if (ficha.doDia) Laranja else Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward, null,
                    tint = if (ficha.doDia) Color.White else Fundo,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
private fun PreviewTelaMeusTreinos() {
    GyemaTheme { TelaMeusTreinos() }
}