package br.com.gyema.ui.components

import  androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.gyema.ui.tema.*

enum class StatusAmbiente(val rotulo: String, val cor: Color, val fundo: Color) {
    Disponivel("Disponível", Verde, VerdeFundo),
    Ocupado("Ocupado", Laranja, OcupadoFundo),
    Manutencao("Manutenção", ManutencaoBorda, ManutencaoFundo)
}

@Composable
fun CabecalhoAcademia(
    titulo: String,
    onVoltar: (() -> Unit)? = null,
    acao: @Composable () -> Unit = {}
) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        if (onVoltar != null) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack, "Voltar",
                modifier = Modifier.size(24.dp).clickable(onClick = onVoltar)
            )
            Spacer(Modifier.width(12.dp))
        }
        Text(titulo, fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
        acao()
    }
}

@Composable
fun BotaoSino() {
    Box(
        Modifier.size(36.dp).clip(CircleShape).background(AzulBotao),
        contentAlignment = Alignment.Center
    ) {
        Icon(Icons.Outlined.Notifications, "Notificações", tint = TextoSecundario, modifier = Modifier.size(18.dp))
    }
}

@Composable
fun BarraProgresso(fracao: Float, cor: Color, modifier: Modifier = Modifier) {
    Box(
        modifier
            .fillMaxWidth()
            .height(6.dp)
            .clip(RoundedCornerShape(50))
            .background(Borda)
    ) {
        Box(Modifier.fillMaxWidth(fracao).fillMaxHeight().background(cor))
    }
}

@Composable
fun PontoLegenda(cor: Color, texto: String, tamanhoFonte: TextUnit = 11.sp) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(7.dp).background(cor, CircleShape))
        Spacer(Modifier.width(5.dp))
        Text(texto, fontSize = tamanhoFonte)
    }
}