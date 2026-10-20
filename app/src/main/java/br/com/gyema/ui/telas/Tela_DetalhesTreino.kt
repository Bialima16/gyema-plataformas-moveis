package br.com.gyema.ui.telas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.gyema.ui.components.BotaoPrincipal
import br.com.gyema.ui.components.BotaoVoltarQuadrado
import br.com.gyema.ui.components.CardGyema
import br.com.gyema.ui.tema.*

private data class ExercicioItem(val nome: String, val series: Int, val repeticoes: Int)

private val exercicios = listOf(
    ExercicioItem("Supino reto", 4, 12),
    ExercicioItem("Supino inclinado", 4, 10),
    ExercicioItem("Crucifixo", 3, 12),
    ExercicioItem("Crossover", 3, 15),
    ExercicioItem("Tríceps corda", 4, 12),
    ExercicioItem("Tríceps francês", 3, 12)
)

@Composable
fun TelaDetalhesTreino(onVoltar: () -> Unit = {}, onIniciar: () -> Unit = {}) {
    Column(Modifier.fillMaxSize().background(Fundo)) {
        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                BotaoVoltarQuadrado(onVoltar)
                Spacer(Modifier.width(12.dp))
                Text("Treino A", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Row(
                    Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(LaranjaEscuro)
                        .padding(horizontal = 8.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Outlined.AccessTime, null, tint = Laranja, modifier = Modifier.size(13.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("~45 min", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Laranja)
                }
            }

            Text("Peito e Tríceps — Hipertrofia", fontSize = 13.sp, color = TextoSecundario)
            Spacer(Modifier.height(4.dp))

            exercicios.forEachIndexed { i, ex -> ExercicioCard(i + 1, ex) }
        }

        BotaoPrincipal(
            "Iniciar treino", onIniciar,
            Modifier.fillMaxWidth().padding(16.dp)
        )
    }
}

@Composable
private fun ExercicioCard(numero: Int, ex: ExercicioItem) {
    CardGyema(Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(32.dp).clip(RoundedCornerShape(8.dp)).background(LaranjaEscuro),
                contentAlignment = Alignment.Center
            ) {
                Text(numero.toString(), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Laranja)
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(ex.nome, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text("${ex.series} séries x ${ex.repeticoes} repetições", fontSize = 11.sp, color = TextoSecundario)
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
private fun PreviewTelaDetalhesTreino() {
    GyemaTheme { TelaDetalhesTreino() }
}