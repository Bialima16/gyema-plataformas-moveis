package br.com.gyema.ui.telas

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.gyema.data.Grupo
import br.com.gyema.data.RepositorioComunidade
import br.com.gyema.data.mensagemDeErro
import br.com.gyema.data.tentar
import br.com.gyema.ui.components.EstadoCarregando
import br.com.gyema.ui.components.EstadoErro
import br.com.gyema.ui.components.EstadoVazio
import br.com.gyema.ui.components.iniciais
import kotlinx.coroutines.launch

private val FundoGrupos = Color(0xFF0B1030)
private val CartaoGrupo = Color(0xFF171D40)
private val LaranjaGrupo = Color(0xFFF34B00)
private val MutadoGrupo = Color(0xFFA8A9C2)

@Composable
private fun BotaoGrupo(texto: String, aoClicar: () -> Unit, modifier: Modifier = Modifier, habilitado: Boolean = true) {
    Button(onClick = aoClicar, enabled = habilitado, modifier = modifier.fillMaxWidth().height(50.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(containerColor = LaranjaGrupo, disabledContainerColor = CartaoGrupo)) {
        Text(texto, color = Color.White, fontSize = 14.sp)
    }
}

@Composable
fun TelaGrupos(onCriar: () -> Unit, onChatbot: () -> Unit, onVoltar: () -> Unit = {}) {
    val context = LocalContext.current
    val escopo = rememberCoroutineScope()

    var grupos by remember { mutableStateOf<List<Grupo>?>(null) }
    var erro by remember { mutableStateOf<String?>(null) }
    var tentativa by remember { mutableIntStateOf(0) }
    var atualizandoId by remember { mutableStateOf<Long?>(null) }

    // Recarrega ao abrir e ao voltar da criação de grupo
    LaunchedEffect(tentativa) {
        erro = null
        tentar { RepositorioComunidade.listarGrupos() }
            .onSuccess { grupos = it }
            .onFailure { erro = mensagemDeErro(it) }
    }

    fun mudarParticipacao(grupo: Grupo, entrar: Boolean) {
        atualizandoId = grupo.id
        escopo.launch {
            tentar {
                if (entrar) RepositorioComunidade.entrarNoGrupo(grupo.id)
                else RepositorioComunidade.sairDoGrupo(grupo.id)
            }.onSuccess {
                grupos = grupos?.map {
                    if (it.id != grupo.id) it
                    else it.copy(souMembro = entrar, membros = it.membros + if (entrar) 1 else -1)
                }
            }.onFailure { Toast.makeText(context, mensagemDeErro(it), Toast.LENGTH_SHORT).show() }
            atualizandoId = null
        }
    }

    Column(Modifier.fillMaxSize().background(FundoGrupos).padding(20.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = onVoltar) { Text("‹", color = Color.White, fontSize = 27.sp) }
            Text("Grupos", color = Color.White, fontSize = 18.sp,
                fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            TextButton(onClick = onChatbot) { Text("Assistente", color = LaranjaGrupo) }
        }
        Spacer(Modifier.height(12.dp))

        Column(Modifier.weight(1f).verticalScroll(rememberScrollState())) {
            val lista = grupos
            when {
                erro != null -> EstadoErro(erro!!, onTentarNovamente = { tentativa++ })
                lista == null -> EstadoCarregando()
                lista.isEmpty() -> EstadoVazio(
                    "Nenhum grupo ainda",
                    "Crie o primeiro grupo para treinar junto com outros alunos."
                )
                else -> {
                    val meus = lista.filter { it.souMembro }
                    val outros = lista.filter { !it.souMembro }
                    SecaoGrupos("Meus grupos", meus, "Você ainda não participa de nenhum grupo.",
                        atualizandoId, ::mudarParticipacao)
                    Spacer(Modifier.height(12.dp))
                    SecaoGrupos("Descobrir", outros, "Nenhum outro grupo público no momento.",
                        atualizandoId, ::mudarParticipacao)
                }
            }
        }

        Spacer(Modifier.height(12.dp))
        BotaoGrupo("Criar grupo +", onCriar)
    }
}

@Composable
private fun SecaoGrupos(
    titulo: String,
    grupos: List<Grupo>,
    textoVazio: String,
    atualizandoId: Long?,
    onParticipar: (Grupo, Boolean) -> Unit
) {
    Text(titulo.uppercase(), color = MutadoGrupo, fontSize = 12.sp,
        fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
    Spacer(Modifier.height(10.dp))
    if (grupos.isEmpty()) {
        Text(textoVazio, color = MutadoGrupo, fontSize = 12.sp)
        Spacer(Modifier.height(8.dp))
    }
    grupos.forEach { g ->
        CardGrupo(
            grupo = g,
            atualizando = atualizandoId == g.id,
            onParticipar = { entrar -> onParticipar(g, entrar) }
        )
    }
}

@Composable
private fun CardGrupo(grupo: Grupo, atualizando: Boolean, onParticipar: (entrar: Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth().padding(bottom = 12.dp)
        .background(CartaoGrupo, RoundedCornerShape(12.dp)).padding(14.dp),
        verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(48.dp).border(2.dp, LaranjaGrupo, CircleShape),
            contentAlignment = Alignment.Center) {
            Text(iniciais(grupo.nome), color = LaranjaGrupo, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(grupo.nome, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            val membros = if (grupo.membros == 1) "1 membro" else "${grupo.membros} membros"
            Text("$membros • ${if (grupo.publico) "Público" else "Privado"}", color = MutadoGrupo, fontSize = 12.sp)
            if (grupo.descricao.isNotBlank()) {
                Text(grupo.descricao, color = MutadoGrupo, fontSize = 11.sp,
                    maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
        }
        Spacer(Modifier.width(8.dp))
        when {
            grupo.souCriador -> EtiquetaGrupo("Criador")
            grupo.souMembro -> BotaoPequenoGrupo(if (atualizando) "..." else "Sair", destaque = false,
                habilitado = !atualizando) { onParticipar(false) }
            else -> BotaoPequenoGrupo(if (atualizando) "..." else "Entrar", destaque = true,
                habilitado = !atualizando) { onParticipar(true) }
        }
    }
}

@Composable
private fun EtiquetaGrupo(texto: String) {
    Text(texto, color = LaranjaGrupo, fontSize = 11.sp, fontWeight = FontWeight.Bold,
        modifier = Modifier.background(LaranjaGrupo.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp))
}

@Composable
private fun BotaoPequenoGrupo(texto: String, destaque: Boolean, habilitado: Boolean, aoClicar: () -> Unit) {
    Box(
        Modifier
            .background(if (destaque) LaranjaGrupo else FundoGrupos, RoundedCornerShape(8.dp))
            .border(1.dp, if (destaque) LaranjaGrupo else Color(0xFF292F55), RoundedCornerShape(8.dp))
            .clickable(enabled = habilitado, onClick = aoClicar)
            .padding(horizontal = 12.dp, vertical = 7.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(texto, color = if (destaque) Color.White else MutadoGrupo, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun TelaCriarGrupo(onVoltar: () -> Unit, onCriado: () -> Unit = onVoltar, iniciarComErro: Boolean = false) {
    val context = LocalContext.current
    val escopo = rememberCoroutineScope()
    var nome by rememberSaveable { mutableStateOf("") }
    var descricao by rememberSaveable { mutableStateOf("") }
    var publico by rememberSaveable { mutableStateOf(true) }
    var mensagemErro by rememberSaveable {
        mutableStateOf(if (iniciarComErro) "Insira todos os dados do grupo antes de salvar." else null)
    }
    var salvando by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().background(FundoGrupos).padding(20.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = onVoltar) { Text("‹", color = Color.White, fontSize = 27.sp) }
            Text("Criar grupo", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(20.dp))
        Text("Nome do grupo", color = MutadoGrupo, fontSize = 13.sp)
        Spacer(Modifier.height(8.dp))
        CampoGrupo(nome, { if (it.length <= 60) nome = it; mensagemErro = null }, "Ex: Treino Avançado Unifor")
        Spacer(Modifier.height(22.dp))
        Text("Descrição", color = MutadoGrupo, fontSize = 13.sp)
        Spacer(Modifier.height(8.dp))
        CampoGrupo(descricao, { if (it.length <= 300) descricao = it; mensagemErro = null },
            "Sobre o que é o grupo, horários de encontro...", altura = 106)
        Spacer(Modifier.height(22.dp))
        Text("Privacidade", color = MutadoGrupo, fontSize = 13.sp)
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            listOf(true to "Público", false to "Privado").forEach { (valor, titulo) ->
                Box(Modifier.weight(1f).height(44.dp)
                    .background(if (publico == valor) LaranjaGrupo else FundoGrupos,
                        RoundedCornerShape(9.dp))
                    .border(1.dp, if (publico == valor) LaranjaGrupo else CartaoGrupo,
                        RoundedCornerShape(9.dp))
                    .clickable { publico = valor }, contentAlignment = Alignment.Center) {
                    Text(titulo, color = if (publico == valor) Color.White else MutadoGrupo,
                        fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(
            if (publico) "Qualquer aluno pode ver e entrar no grupo."
            else "Só os membros veem o grupo.",
            color = MutadoGrupo, fontSize = 11.sp
        )
        Spacer(Modifier.weight(1f))
        mensagemErro?.let {
            Text(it, color = Color(0xFFFF7C8A), fontSize = 12.sp,
                textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(12.dp))
        }
        BotaoGrupo(if (salvando) "Criando..." else "Criar grupo", habilitado = !salvando, aoClicar = {
            if (nome.isBlank() || descricao.isBlank()) {
                mensagemErro = "Insira todos os dados do grupo antes de salvar."
            } else {
                salvando = true
                escopo.launch {
                    tentar { RepositorioComunidade.criarGrupo(nome.trim(), descricao.trim(), publico) }
                        .onSuccess {
                            Toast.makeText(context, "Grupo criado", Toast.LENGTH_SHORT).show()
                            onCriado()
                        }
                        .onFailure { mensagemErro = mensagemDeErro(it) }
                    salvando = false
                }
            }
        })
    }
}

@Composable
private fun CampoGrupo(valor: String, alterar: (String) -> Unit, dica: String, altura: Int = 52) {
    Box(Modifier.fillMaxWidth().height(altura.dp)
        .background(CartaoGrupo, RoundedCornerShape(10.dp))
        .border(1.dp, Color(0xFF292F55), RoundedCornerShape(10.dp))
        .padding(14.dp)) {
        BasicTextField(value = valor, onValueChange = alterar,
            modifier = Modifier.fillMaxSize(), singleLine = altura == 52,
            textStyle = TextStyle(color = Color.White, fontSize = 14.sp),
            cursorBrush = androidx.compose.ui.graphics.SolidColor(LaranjaGrupo),
            decorationBox = { inner ->
                if (valor.isEmpty()) Text(dica, color = MutadoGrupo, fontSize = 13.sp)
                inner()
            })
    }
}
