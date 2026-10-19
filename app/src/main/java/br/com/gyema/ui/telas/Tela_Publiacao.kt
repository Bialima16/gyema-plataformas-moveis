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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.gyema.ui.components.AvatarIniciais
import br.com.gyema.ui.components.CardGyema
import br.com.gyema.ui.components.TituloSecao
import br.com.gyema.ui.tema.*

private data class Comentario(val iniciais: String, val autor: String, val texto: String, val tempo: String)

private val comentarios = listOf(
    Comentario("AL", "Ana Lima", "Muito bom!", "há 5 min"),
    Comentario("PC", "Pedro Costa", "Concordo!", "há 2 min")
)

@Composable
fun TelaPublicacao(onVoltar: () -> Unit = {}) {
    Column(Modifier.fillMaxSize().background(Fundo)) {
        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Topo
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CardGyema(
                    Modifier.align(Alignment.CenterStart).size(34.dp),
                    shape = RoundedCornerShape(10.dp),
                    onClick = onVoltar
                ) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar", modifier = Modifier.size(18.dp))
                    }
                }
                Text("Publicação", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            // Post
            CardGyema(Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AvatarIniciais("MS", tamanho = 44.dp, fonte = 14.sp)
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text("Maria Santos", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            Text("há 10 min • Treino de Força", fontSize = 11.sp, color = TextoSecundario)
                        }
                    }
                    Text(
                        "Hoje o treino de perna foi insano! 50 minutos bem focados na estrutura da Unifor. Alguém mais treinou hoje? 💪🔥",
                        fontSize = 14.sp, lineHeight = 20.sp
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.FavoriteBorder, null, tint = Laranja, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("24 curtidas", fontSize = 12.sp, color = Laranja, fontWeight = FontWeight.Medium)
                    }
                }
            }

            TituloSecao("COMENTÁRIOS")
            comentarios.forEach { ComentarioCard(it) }
        }

        CampoComentario()
    }
}

@Composable
private fun ComentarioCard(c: Comentario) {
    CardGyema(Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            AvatarIniciais(c.iniciais, tamanho = 34.dp, fonte = 11.sp)
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Row {
                    Text(c.autor, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    Text(c.tempo, fontSize = 10.sp, color = TextoSecundario)
                }
                Text(c.texto, fontSize = 12.sp, color = TextoSecundario)
            }
        }
    }
}

@Composable
private fun CampoComentario() {
    Row(
        Modifier
            .fillMaxWidth()
            .background(SuperficieEscura)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val shape = RoundedCornerShape(12.dp)
        Box(
            Modifier
                .weight(1f)
                .clip(shape)
                .background(Fundo)
                .border(1.dp, Borda, shape)
                .padding(horizontal = 14.dp, vertical = 13.dp)
        ) {
            Text("Escreva um comentário...", fontSize = 13.sp, color = TextoSecundario)
        }
        Spacer(Modifier.width(10.dp))
        Box(
            Modifier.size(40.dp).clip(CircleShape).background(Laranja),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.AutoMirrored.Filled.Send, "Enviar", tint = Color.White, modifier = Modifier.size(18.dp))
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
private fun PreviewTelaPublicacao() {
    GyemaTheme { TelaPublicacao() }
}