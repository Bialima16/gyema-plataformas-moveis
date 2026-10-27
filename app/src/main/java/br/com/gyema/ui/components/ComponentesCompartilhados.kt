package br.com.gyema.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.gyema.ui.tema.Borda
import br.com.gyema.ui.tema.Laranja
import br.com.gyema.ui.tema.Superficie
import br.com.gyema.ui.tema.SuperficieEscura
import br.com.gyema.ui.tema.TextoSecundario

@Composable
fun CardGyema(
    modifier: Modifier = Modifier,
    fundo: Color = Superficie,
    shape: Shape = RoundedCornerShape(16.dp),
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Box(
        modifier
            .clip(shape)
            .background(fundo)
            .border(1.dp, Borda, shape)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
    ) { content() }
}

@Composable
fun TituloSecao(texto: String) {
    Text(
        text = texto,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.5.sp
    )
}

@Composable
fun AvatarIniciais(iniciais: String, tamanho: Dp = 40.dp, fonte: TextUnit = 13.sp) {
    Box(
        Modifier.size(tamanho).border(2.dp, Laranja, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(iniciais, fontSize = fonte, fontWeight = FontWeight.Bold, color = Laranja)
    }
}

@Composable
fun LogoGyema() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        // Troque pelo logo real quando tiver o arquivo
        Box(
            Modifier.size(28.dp).border(2.dp, Laranja, CircleShape),
            contentAlignment = Alignment.Center
        ) { Icon(Icons.Outlined.FitnessCenter, null, tint = Laranja, modifier = Modifier.size(14.dp)) }
        Spacer(Modifier.width(8.dp))
        Text("GYEMA", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
    }
}

@Composable
fun AbasSegmentadas(
    itens: List<String>,
    selecionado: Int,
    modifier: Modifier = Modifier,
    onSelecionar: (Int) -> Unit = {}
) {
    Row(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(SuperficieEscura)
            .padding(4.dp)
    ) {
        itens.forEachIndexed { i, nome ->
            val ativa = i == selecionado
            Box(
                Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (ativa) Laranja else Color.Transparent)
                    .clickable { onSelecionar(i) }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    nome, fontSize = 12.sp,
                    fontWeight = if (ativa) FontWeight.Bold else FontWeight.Medium,
                    color = if (ativa) Color.White else TextoSecundario
                )
            }
        }
    }
}