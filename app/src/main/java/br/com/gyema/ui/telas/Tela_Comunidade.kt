package br.com.gyema.ui.telas

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.gyema.data.Post
import br.com.gyema.data.RepositorioComunidade
import br.com.gyema.data.mensagemDeErro
import br.com.gyema.data.tentar
import br.com.gyema.ui.components.AbasSegmentadas
import br.com.gyema.ui.components.AvatarIniciais
import br.com.gyema.ui.components.CardGyema
import br.com.gyema.ui.components.EstadoCarregando
import br.com.gyema.ui.components.EstadoErro
import br.com.gyema.ui.components.EstadoVazio
import br.com.gyema.ui.components.LogoGyema
import br.com.gyema.ui.components.iniciais
import br.com.gyema.ui.components.tempoRelativo
import br.com.gyema.ui.tema.*
import kotlinx.coroutines.launch

/** Categorias sugeridas ao publicar (a coluna aceita qualquer texto). */
private val categorias = listOf("Treino de Força", "Cardio", "Dica", "Conquista")

@Composable
fun TelaComunidade(
    onAbrirPost: (postId: Long) -> Unit = {},
    onGrupos: () -> Unit = {}
) {
    val context = LocalContext.current
    val escopo = rememberCoroutineScope()

    var aba by rememberSaveable { mutableIntStateOf(0) } // 0: Todas, 1: Minhas
    var posts by remember { mutableStateOf<List<Post>?>(null) }
    var erro by remember { mutableStateOf<String?>(null) }
    var tentativa by remember { mutableIntStateOf(0) }

    // Recarrega ao trocar de aba, ao publicar e ao voltar de uma publicação
    LaunchedEffect(aba, tentativa) {
        erro = null
        tentar { RepositorioComunidade.listarFeed(soMinhas = aba == 1) }
            .onSuccess { posts = it }
            .onFailure { erro = mensagemDeErro(it) }
    }

    // Curtir/descurtir: muda na tela na hora e desfaz se o banco recusar
    fun alternarCurtida(post: Post) {
        val curtir = !post.curtidoPorMim
        fun aplicar(curtido: Boolean) {
            posts = posts?.map {
                if (it.id != post.id) it
                else it.copy(curtidoPorMim = curtido, curtidas = it.curtidas + if (curtido) 1 else -1)
            }
        }
        aplicar(curtir)
        escopo.launch {
            tentar {
                if (curtir) RepositorioComunidade.curtir(post.id) else RepositorioComunidade.descurtir(post.id)
            }.onFailure {
                aplicar(!curtir)
                Toast.makeText(context, mensagemDeErro(it), Toast.LENGTH_SHORT).show()
            }
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(Fundo)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        LogoGyema()
        AbasSegmentadas(
            listOf("Todas", "Minhas", "Grupos"),
            selecionado = aba,
            onSelecionar = { if (it == 2) onGrupos() else aba = it }
        )

        NovaPublicacao(onPublicado = { tentativa++ })

        val lista = posts
        when {
            erro != null -> EstadoErro(erro!!, onTentarNovamente = { tentativa++ })
            lista == null -> EstadoCarregando()
            lista.isEmpty() -> EstadoVazio(
                if (aba == 1) "Você ainda não publicou" else "Nenhuma publicação ainda",
                "Compartilhe seu treino ou uma dica no campo acima."
            )
            else -> lista.forEach { post ->
                PostCard(
                    post = post,
                    onClick = { onAbrirPost(post.id) },
                    onCurtir = { alternarCurtida(post) }
                )
            }
        }
    }
}

@Composable
private fun NovaPublicacao(onPublicado: () -> Unit) {
    val context = LocalContext.current
    val escopo = rememberCoroutineScope()
    var texto by rememberSaveable { mutableStateOf("") }
    var categoria by rememberSaveable { mutableStateOf<String?>(null) }
    var enviando by remember { mutableStateOf(false) }

    CardGyema(Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            val shape = RoundedCornerShape(10.dp)
            Box(
                Modifier
                    .fillMaxWidth()
                    .heightIn(min = 72.dp)
                    .clip(shape)
                    .background(Fundo)
                    .border(1.dp, Borda, shape)
                    .padding(12.dp)
            ) {
                BasicTextField(
                    value = texto,
                    onValueChange = { if (it.length <= 500) texto = it },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(color = Color.White, fontSize = 13.sp, lineHeight = 19.sp),
                    cursorBrush = SolidColor(Laranja),
                    decorationBox = { campo ->
                        if (texto.isEmpty()) {
                            Text("Compartilhe seu treino ou uma dica...", fontSize = 13.sp, color = TextoSecundario)
                        }
                        campo()
                    }
                )
            }

            // Categoria (opcional): toque de novo para desmarcar
            Row(
                Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categorias.forEach { c ->
                    val ativa = c == categoria
                    Box(
                        Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (ativa) Laranja else SuperficieEscura)
                            .clickable { categoria = if (ativa) null else c }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(c, fontSize = 11.sp, color = if (ativa) Color.White else TextoSecundario)
                    }
                }
            }

            val podePublicar = texto.isNotBlank() && !enviando
            Box(
                Modifier
                    .align(Alignment.End)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (podePublicar) Laranja else SuperficieEscura)
                    .clickable(enabled = podePublicar) {
                        enviando = true
                        escopo.launch {
                            tentar { RepositorioComunidade.publicar(texto.trim(), categoria) }
                                .onSuccess {
                                    texto = ""
                                    categoria = null
                                    onPublicado()
                                }
                                .onFailure { Toast.makeText(context, mensagemDeErro(it), Toast.LENGTH_LONG).show() }
                            enviando = false
                        }
                    }
                    .padding(horizontal = 18.dp, vertical = 9.dp)
            ) {
                Text(
                    if (enviando) "Publicando..." else "Publicar",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (podePublicar) Color.White else TextoSecundario
                )
            }
        }
    }
}

@Composable
private fun PostCard(post: Post, onClick: () -> Unit, onCurtir: () -> Unit) {
    CardGyema(Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), onClick = onClick) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AvatarIniciais(iniciais(post.autorNome))
                Spacer(Modifier.width(10.dp))
                Column {
                    Text(post.autorNome, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text(
                        listOfNotNull(tempoRelativo(post.criadoEm), post.categoria).joinToString(" • "),
                        fontSize = 11.sp,
                        color = TextoSecundario
                    )
                }
            }
            Text(post.texto, fontSize = 13.sp, lineHeight = 19.sp)
            Row(verticalAlignment = Alignment.CenterVertically) {
                BotaoCurtir(post.curtidoPorMim, post.curtidas, onCurtir)
                Spacer(Modifier.width(16.dp))
                Icon(Icons.Outlined.ChatBubbleOutline, null, tint = TextoSecundario, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text(
                    if (post.comentarios == 1) "1 comentário" else "${post.comentarios} comentários",
                    fontSize = 12.sp,
                    color = TextoSecundario
                )
            }
        }
    }
}

/** Coração com o total de curtidas. Usado no feed e na tela da publicação. */
@Composable
fun BotaoCurtir(curtido: Boolean, total: Int, onClick: () -> Unit) {
    val cor = if (curtido) Laranja else TextoSecundario
    Row(
        Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 4.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            if (curtido) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
            contentDescription = if (curtido) "Descurtir" else "Curtir",
            tint = cor,
            modifier = Modifier.size(16.dp)
        )
        Spacer(Modifier.width(4.dp))
        Text(
            if (total == 1) "1 curtida" else "$total curtidas",
            fontSize = 12.sp,
            color = cor,
            fontWeight = FontWeight.Medium
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
private fun PreviewTelaComunidade() {
    GyemaTheme { TelaComunidade() }
}
