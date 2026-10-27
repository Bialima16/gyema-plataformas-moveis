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
fun TelaRegistroUtilizacao(
    onVoltarTreino: () -> Unit = {},
    onFinalizarUtilizacao: () -> Unit = {},
    onReportarProblema: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Fundo)
            .padding(16.dp)
    ) {
        // cabecalho
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Em utilização",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.weight(1f)
            )
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(AzulBotao),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = "Notificações",
                    tint = TextoSecundario,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // secao central com temporizador e detalhes
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // grafico de progresso circular
            Box(
                modifier = Modifier.size(220.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val espessura = 14.dp.toPx()
                    val topo = Offset(espessura / 2, espessura / 2)
                    val tamanho = Size(size.width - espessura, size.height - espessura)
                    drawArc(
                        color = Borda,
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        topLeft = topo,
                        size = tamanho,
                        style = Stroke(espessura)
                    )
                    drawArc(
                        color = Laranja,
                        startAngle = -90f,
                        sweepAngle = 270f,
                        useCenter = false,
                        topLeft = topo,
                        size = tamanho,
                        style = Stroke(espessura, cap = StrokeCap.Round)
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "08:45",
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "TEMPO RESTANTE",
                        fontSize = 10.sp,
                        color = TextoSecundario,
                        letterSpacing = 1.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // badge da categoria muscular
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(LaranjaEscuro)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "PERNAS",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Laranja
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // nome do equipamento
            Text(
                text = "Leg Press 45°",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(4.dp))

            // detalhes adicionais
            Text(
                text = "Setor de Musculação — Ficha A",
                fontSize = 12.sp,
                color = TextoSecundario,
                textAlign = TextAlign.Center
            )
        }

        // botoes fixados na parte inferior
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            val shapeBotao = RoundedCornerShape(12.dp)

            // botao voltar ao treino
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(shapeBotao)
                    .background(AzulBotao)
                    .clickable(onClick = onVoltarTreino),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Voltar ao Treino",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            // botao finalizar utilizacao
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(shapeBotao)
                    .background(Laranja)
                    .clickable(onClick = onFinalizarUtilizacao),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Finalizar utilização",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            // botao reportar problema
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(shapeBotao)
                    .border(1.dp, Borda, shapeBotao)
                    .clickable(onClick = onReportarProblema),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Reportar problema",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextoSecundario
                )
            }
        }
    }
}

// preview da tela
@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
private fun PreviewTelaRegistroUtilizacao() {
    GyemaTheme {
        TelaRegistroUtilizacao()
    }
}
