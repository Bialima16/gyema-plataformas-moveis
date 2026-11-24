package br.com.gyema.ui.telas

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
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
import br.com.gyema.data.Comentario
import br.com.gyema.data.Post
import br.com.gyema.data.RepositorioComunidade
import br.com.gyema.data.mensagemDeErro
import br.com.gyema.data.tentar
import br.com.gyema.ui.components.AvatarIniciais
import br.com.gyema.ui.components.CardGyema
import br.com.gyema.ui.components.EstadoCarregando
import br.com.gyema.ui.components.EstadoErro
import br.com.gyema.ui.components.TituloSecao
import br.com.gyema.ui.components.iniciais
import br.com.gyema.ui.components.tempoRelativo
import br.com.gyema.ui.tema.*
import kotlinx.coroutines.launch

@Composable
fun TelaPublicacao(postId: Long, onVoltar: () -> Unit = {}) {
    val context = LocalContext.current
    val escopo = rememberCoroutineScope()

    var post by remember { mutableStateOf<Post?>(null) }
    var comentarios by remember { mutableStateOf<List<Comentario>>(emptyList()) }
    var meuId by remember { mutableStateOf("") }
    var souInstrutor by remember { mutableStateOf(false) }
    var erro by remember { mutableStateOf<String?>(null) }
    var tentativa by remember { mutableIntStateOf(0) }
    var confirmarExclusao by remember { mutableStateOf(false) }

    LaunchedEffect(postId, tentativa) {
        erro = null
        tentar {
            meuId = RepositorioComunidade.meuId()
            souInstrutor = RepositorioComunidade.souInstrutor()
            post = RepositorioComunidade.buscarPost(postId)
            comentarios = RepositorioComunidade.listarComentarios(postId)
        }.onFailure { erro = mensagemDeErro(it) }
    }

    fun avisar(e: Throwable) = Toast.makeText(context, mensagemDeErro(e), Toast.LENGTH_SHORT).show()

    fun alternarCurtida() {
        val atual = post ?: return
        val curtir = !atual.curtidoPorMim
        fun aplicar(curtido: Boolean) {
            post = post?.copy(
                curtidoPorMim = curtido,
                curtidas = (post?.curtidas ?: 0) + if (curtido) 1 else -1
            )
        }
        aplicar(curtir)
        escopo.launch {
            tentar {
                if (curtir) RepositorioComunidade.curtir(postId) else RepositorioComunidade.descurtir(postId)
            }.onFailure { aplicar(!curtir); avisar(it) }
        }
    }

    fun excluirComentario(c: Comentario) {
        escopo.launch {
            tentar { RepositorioComunidade.excluirComentario(c.id) }
                .onSuccess {
                    comentarios = comentarios - c
                    post = post?.copy(comentarios = comentarios.size)
                }
                .onFailure { avisar(it) }
        }
    }

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
                val p = post
                if (p != null && (p.autorId == meuId || souInstrutor)) {
                    Icon(
                        Icons.Outlined.Delete,
                        contentDescription = "Excluir publicação",
                        tint = TextoSecundario,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .clip(CircleShape)
                            .clickable { confirmarExclusao = true }
                            .padding(6.dp)
                            .size(20.dp)
                    )
                }
            }

            val p = post
            when {
                erro != null -> EstadoErro(erro!!, onTentarNovamente = { tentativa++ })
                p == null -> EstadoCarregando()
                else -> {
                    // Post
                    CardGyema(Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) {
                        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                AvatarIniciais(iniciais(p.autorNome), tamanho = 44.dp, fonte = 14.sp)
                                Spacer(Modifier.width(10.dp))
                                Column {
                                    Text(p.autorNome, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                    Text(
                                        listOfNotNull(tempoRelativo(p.criadoEm), p.categoria).joinToString(" • "),
                                        fontSize = 11.sp,
                                        color = TextoSecundario
                                    )
                                }
                            }
                            Text(p.texto, fontSize = 14.sp, lineHeight = 20.sp)
                            BotaoCurtir(p.curtidoPorMim, p.curtidas, onClick = { alternarCurtida() })
                        }
                    }

                    TituloSecao("COMENTÁRIOS")
                    if (comentarios.isEmpty()) {
                        Text("Seja o primeiro a comentar.", fontSize = 12.sp, color = TextoSecundario)
                    }
                    comentarios.forEach { c ->
                        ComentarioCard(
                            c = c,
                            podeExcluir = c.autorId == meuId || souInstrutor,
                            onExcluir = { excluirComentario(c) }
                        )
                    }
                }
            }
        }

        if (post != null) {
            CampoComentario(onEnviar = { texto ->
                tentar {
                    RepositorioComunidade.comentar(postId, texto)
                    RepositorioComunidade.listarComentarios(postId)
                }.onSuccess {
                    comentarios = it
                    post = post?.copy(comentarios = it.size)
                }.onFailure { avisar(it) }.isSuccess
            })
        }
    }

    if (confirmarExclusao) {
        AlertDialog(
            onDismissRequest = { confirmarExclusao = false },
            title = { Text("Excluir publicação?") },
            text = { Text("A publicação, as curtidas e os comentários serão apagados.") },
            confirmButton = {
                TextButton(onClick = {
                    confirmarExclusao = false
                    escopo.launch {
                        tentar { RepositorioComunidade.excluirPost(postId) }
                            .onSuccess {
                                Toast.makeText(context, "Publicação excluída", Toast.LENGTH_SHORT).show()
                                onVoltar()
                            }
                            .onFailure { avisar(it) }
                    }
                }) { Text("Excluir", color = Laranja) }
            },
            dismissButton = {
                TextButton(onClick = { confirmarExclusao = false }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
private fun ComentarioCard(c: Comentario, podeExcluir: Boolean, onExcluir: () -> Unit) {
    CardGyema(Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            AvatarIniciais(iniciais(c.autorNome), tamanho = 34.dp, fonte = 11.sp)
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Row {
                    Text(c.autorNome, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    Text(tempoRelativo(c.criadoEm), fontSize = 10.sp, color = TextoSecundario)
                }
                Text(c.texto, fontSize = 12.sp, color = TextoSecundario)
            }
            if (podeExcluir) {
                Spacer(Modifier.width(6.dp))
                Icon(
                    Icons.Outlined.Delete,
                    contentDescription = "Excluir comentário",
                    tint = TextoSecundario,
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable(onClick = onExcluir)
                        .padding(4.dp)
                        .size(16.dp)
                )
            }
        }
    }
}

/** Campo fixo no rodapé. [onEnviar] grava o comentário e devolve true se deu certo (aí o campo é limpo). */
@Composable
private fun CampoComentario(onEnviar: suspend (texto: String) -> Boolean) {
    val escopo = rememberCoroutineScope()
    var texto by remember { mutableStateOf("") }
    var enviando by remember { mutableStateOf(false) }
    val podeEnviar = texto.isNotBlank() && !enviando

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
            BasicTextField(
                value = texto,
                onValueChange = { if (it.length <= 300) texto = it },
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(color = Color.White, fontSize = 13.sp),
                cursorBrush = SolidColor(Laranja),
                maxLines = 4,
                decorationBox = { campo ->
                    if (texto.isEmpty()) Text("Escreva um comentário...", fontSize = 13.sp, color = TextoSecundario)
                    campo()
                }
            )
        }
        Spacer(Modifier.width(10.dp))
        Box(
            Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(if (podeEnviar) Laranja else Borda)
                .clickable(enabled = podeEnviar) {
                    enviando = true
                    escopo.launch {
                        if (onEnviar(texto.trim())) texto = ""
                        enviando = false
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.AutoMirrored.Filled.Send, "Enviar", tint = Color.White, modifier = Modifier.size(18.dp))
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
private fun PreviewTelaPublicacao() {
    GyemaTheme { TelaPublicacao(postId = 0) }
}
