package br.com.gyema.ui.telas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.gyema.ui.components.AbasSegmentadas
import br.com.gyema.ui.components.AvatarIniciais
import br.com.gyema.ui.components.CardGyema
import br.com.gyema.ui.components.LogoGyema
import br.com.gyema.ui.tema.*

private data class Post(
    val iniciais: String,
    val autor: String,
    val tempo: String,
    val categoria: String,
    val texto: String,
    val curtidas: Int,
    val comentarios: Int,
    val curtido: Boolean
)

private val posts = listOf(
    Post(
        "MS", "Maria Santos", "há 10 min", "Treino de Força",
        "Hoje o treino de perna foi insano! 50 minutos bem focados na estrutura da Unifor. Alguém mais treinou hoje? 💪🔥",
        24, 5, true
    ),
    Post(
        "CO", "Carlos Oliveira", "há 2 horas", "Corrida Unifor",
        "Corrida matinal concluída! O clima estava perfeito hoje cedo na pista de atletismo. Bora manter a constância pessoal!🏃✨",
        18, 2, false
    )
)

@Composable
fun TelaComunidade(onAbrirPost: () -> Unit = {}) {
    Column(
        Modifier
            .fillMaxSize()
            .background(Fundo)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        LogoGyema()
        AbasSegmentadas(listOf("Todas", "Seguindo", "Grupos"), selecionado = 0)
        posts.forEach { PostCard(it, onAbrirPost) }
    }
}

@Composable
private fun PostCard(post: Post, onClick: () -> Unit) {
    CardGyema(Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), onClick = onClick) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AvatarIniciais(post.iniciais)
                Spacer(Modifier.width(10.dp))
                Column {
                    Text(post.autor, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text("${post.tempo} • ${post.categoria}", fontSize = 11.sp, color = TextoSecundario)
                }
            }
            Text(post.texto, fontSize = 13.sp, lineHeight = 19.sp)
            Row(verticalAlignment = Alignment.CenterVertically) {
                val corCurtida = if (post.curtido) Laranja else TextoSecundario
                Icon(Icons.Outlined.FavoriteBorder, null, tint = corCurtida, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text(post.curtidas.toString(), fontSize = 12.sp, color = corCurtida)
                Spacer(Modifier.width(16.dp))
                Icon(Icons.Outlined.ChatBubbleOutline, null, tint = TextoSecundario, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("${post.comentarios} comentários", fontSize = 12.sp, color = TextoSecundario)
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
private fun PreviewTelaComunidade() {
    GyemaTheme { TelaComunidade() }
}