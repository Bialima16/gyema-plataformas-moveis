package br.com.gyema.ui.telas

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.gyema.ui.components.BotaoContorno
import br.com.gyema.ui.components.BotaoPrincipal
import br.com.gyema.ui.components.BotaoVoltarQuadrado
import br.com.gyema.ui.components.CardGyema
import br.com.gyema.ui.tema.*

@Composable
fun TelaExecucao(onVoltar: () -> Unit = {}) {
    var carga by remember { mutableIntStateOf(40) }
    var repeticoes by remember { mutableIntStateOf(12) }

    Column(
        Modifier
            .fillMaxSize()
            .background(Fundo)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            BotaoVoltarQuadrado(onVoltar)
            Spacer(Modifier.width(12.dp))
            Text("Execução", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Column {
            Text("Supino Reto", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text("Série 1 de 4", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Laranja)
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
                "Carga (kg)", carga,
                onMenos = { if (carga >= 5) carga -= 5 },
                onMais = { carga += 5 },
                modifier = Modifier.weight(1f)
            )
            Contador(
                "Repetições", repeticoes,
                onMenos = { if (repeticoes > 1) repeticoes-- },
                onMais = { repeticoes++ },
                modifier = Modifier.weight(1f)
            )
        }

        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("TEMPO DE DESCANSO", fontSize = 10.sp, color = TextoSecundario, letterSpacing = 1.sp)
            Spacer(Modifier.height(10.dp))
            AnelDescanso()
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            BotaoContorno("Pausar", onClick = {}, modifier = Modifier.weight(1f))
            BotaoPrincipal("Concluir série", onClick = {}, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun Contador(
    rotulo: String,
    valor: Int,
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
                Text(valor.toString(), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Box(Modifier.weight(1f).clickable(onClick = onMais), contentAlignment = Alignment.Center) {
                    Text("+", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Laranja)
                }
            }
        }
    }
}

@Composable
private fun AnelDescanso() {
    Box(Modifier.size(104.dp), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val espessura = 5.dp.toPx()
            drawArc(
                color = Laranja, startAngle = 0f, sweepAngle = 360f, useCenter = false,
                topLeft = Offset(espessura / 2, espessura / 2),
                size = Size(size.width - espessura, size.height - espessura),
                style = Stroke(espessura)
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("01:30", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text("restante", fontSize = 10.sp, color = Laranja)
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
private fun PreviewTelaExecucao() {
    GyemaTheme { TelaExecucao() }
}