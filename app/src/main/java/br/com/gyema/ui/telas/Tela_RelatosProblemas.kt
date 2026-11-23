package br.com.gyema.ui.telas

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.gyema.data.Relato
import br.com.gyema.data.RepositorioInstrutor
import br.com.gyema.data.mensagemDeErro
import br.com.gyema.data.tentar
import br.com.gyema.ui.components.EstadoCarregando
import br.com.gyema.ui.components.EstadoErro
import br.com.gyema.ui.components.EstadoVazio
import br.com.gyema.ui.components.formatarData
import br.com.gyema.ui.tema.GyemaTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaRelatosProblemas(
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val escopo = rememberCoroutineScope()

    var aba by remember { mutableIntStateOf(0) } // 0: Pendentes, 1: Resolvidos
    var relatos by remember { mutableStateOf<List<Relato>?>(null) }
    var erro by remember { mutableStateOf<String?>(null) }
    var tentativa by remember { mutableIntStateOf(0) }
    var atualizandoId by remember { mutableStateOf<Long?>(null) }

    LaunchedEffect(tentativa) {
        erro = null
        tentar { RepositorioInstrutor.listarRelatos() }
            .onSuccess { relatos = it }
            .onFailure { erro = mensagemDeErro(it) }
    }

    fun mudarStatus(relato: Relato, status: String) {
        atualizandoId = relato.id
        escopo.launch {
            tentar { RepositorioInstrutor.mudarStatusRelato(relato.id, status) }
                .onSuccess {
                    relatos = relatos?.map { if (it.id == relato.id) it.copy(status = status) else it }
                }
                .onFailure { Toast.makeText(context, mensagemDeErro(it), Toast.LENGTH_LONG).show() }
            atualizandoId = null
        }
    }

    val lista = relatos
    val pendentes = lista.orEmpty().filter { it.status != "resolvido" }
    val resolvidos = lista.orEmpty().filter { it.status == "resolvido" }
    val visiveis = if (aba == 0) pendentes else resolvidos

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Relatos de problemas", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0B1021))
            )
        },
        containerColor = Color(0xFF0B1021)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF131B31), shape = MaterialTheme.shapes.medium)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                TabBotao("Pendentes (${pendentes.size})", aba == 0, Modifier.weight(1f)) { aba = 0 }
                TabBotao("Resolvidos (${resolvidos.size})", aba == 1, Modifier.weight(1f)) { aba = 1 }
            }

            when {
                erro != null -> EstadoErro(erro!!, onTentarNovamente = { tentativa++ })
                lista == null -> EstadoCarregando()
                visiveis.isEmpty() -> EstadoVazio(
                    titulo = if (aba == 0) "Nenhum relato pendente" else "Nenhum relato resolvido",
                    texto = if (aba == 0) "Quando um aluno reportar um problema em um equipamento, ele aparece aqui."
                    else "Os relatos marcados como resolvidos aparecem aqui."
                )
                else -> visiveis.forEach { relato ->
                    CardRelato(
                        relato = relato,
                        atualizando = atualizandoId == relato.id,
                        onEmAnalise = { mudarStatus(relato, "em_analise") },
                        onResolvido = { mudarStatus(relato, "resolvido") }
                    )
                }
            }
        }
    }
}

@Composable
private fun CardRelato(
    relato: Relato,
    atualizando: Boolean,
    onEmAnalise: () -> Unit,
    onResolvido: () -> Unit
) {
    val (rotulo, cor) = when (relato.status) {
        "aberto" -> "ABERTO" to Color(0xFFE65100)
        "em_analise" -> "EM ANÁLISE" to Color(0xFFFFB300)
        else -> "RESOLVIDO" to Color(0xFF4CAF50)
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF131B31))
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text(relato.equipamentos.nome, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text(
                        "${relato.equipamentos.codigo} • ${formatarData(relato.criadoEm)}",
                        color = Color(0xFF8B93A5),
                        fontSize = 12.sp
                    )
                }
                Box(
                    modifier = Modifier
                        .background(cor.copy(alpha = 0.2f), shape = MaterialTheme.shapes.small)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(rotulo, color = cor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(relato.categoria, color = Color(0xFFE65100), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text(relato.descricao, color = Color.White, fontSize = 14.sp)
                Text(
                    "Relatado por: ${relato.perfis?.nome ?: "aluno"}",
                    color = Color(0xFF8B93A5),
                    fontSize = 12.sp
                )
            }

            if (relato.status != "resolvido") {
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (relato.status == "aberto") {
                        BotaoRelato("Em análise", Color.White, atualizando, Modifier.weight(1f), onEmAnalise)
                    }
                    BotaoRelato("Marcar como resolvido", Color(0xFFE65100), atualizando, Modifier.weight(1f), onResolvido)
                }
            }
        }
    }
}

@Composable
private fun BotaoRelato(
    texto: String,
    corTexto: Color,
    atualizando: Boolean,
    modifier: Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = !atualizando,
        modifier = modifier.height(42.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF1C2642),
            disabledContainerColor = Color(0xFF1C2642)
        ),
        shape = MaterialTheme.shapes.small,
        contentPadding = PaddingValues(horizontal = 8.dp)
    ) {
        Text(
            if (atualizando) "Salvando..." else texto,
            color = if (atualizando) Color(0xFF8B93A5) else corTexto,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            maxLines = 1
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0B1021)
@Composable
private fun TelaRelatosProblemasPreview() {
    GyemaTheme { TelaRelatosProblemas() }
}
