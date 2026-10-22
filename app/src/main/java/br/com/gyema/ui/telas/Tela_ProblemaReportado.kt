package br.com.gyema.ui.telas

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.gyema.ui.tema.*

@Composable
fun TelaProblemaReportado(
    onVoltar: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Fundo)
            .padding(20.dp)
    ) {
        // conteudo central
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // icone de confirmacao
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(SuperficieEscura)
                    .border(2.dp, Verde, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Verde,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // textos informativos
            Text(
                text = "Problema reportado com sucesso!",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Seu relato foi encaminhado para avaliação.",
                fontSize = 13.sp,
                color = TextoSecundario,
                textAlign = TextAlign.Center
            )
        }

        // botao inferior
        val shapeBotao = RoundedCornerShape(12.dp)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(shapeBotao)
                .background(Laranja)
                .clickable(onClick = onVoltar),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Voltar para equipamentos",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

// preview da tela
@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
private fun PreviewTelaProblemaReportado() {
    GyemaTheme {
        TelaProblemaReportado()
    }
}
