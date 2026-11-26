package br.com.gyema.ui.telas

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.gyema.data.DetalheEquipamentoAluno
import br.com.gyema.data.RepositorioAluno
import br.com.gyema.data.UtilizacaoAtiva
import br.com.gyema.data.mensagemErroAluno
import br.com.gyema.data.tentar
import br.com.gyema.ui.components.EstadoCarregando
import br.com.gyema.ui.components.EstadoErro
import br.com.gyema.ui.components.iconeEquipamento
import br.com.gyema.ui.components.statusAmbiente
import br.com.gyema.ui.tema.*

private data class DadosDetalhe(val equipamento: DetalheEquipamentoAluno, val usoAtivo: UtilizacaoAtiva?)

@Composable
fun TelaDetalhesEquipamento(
    equipamentoId: Int,
    onVoltar: () -> Unit = {},
    onIniciarUso: (equipamentoId: Int) -> Unit = {},
    onAbrirUso: () -> Unit = {},
    onReportarProblema: (equipamentoId: Int) -> Unit = {}
) {
    var dados by remember { mutableStateOf<DadosDetalhe?>(null) }
    var erro by remember { mutableStateOf<String?>(null) }
    var tentativa by remember { mutableIntStateOf(0) }

    LaunchedEffect(equipamentoId, tentativa) {
        erro = null
        tentar {
            DadosDetalhe(
                RepositorioAluno.buscarEquipamento(equipamentoId),
                RepositorioAluno.minhaUtilizacaoAtiva()
            )
        }
            .onSuccess { dados = it }
            .onFailure { erro = mensagemErroAluno(it) }
    }

    Column(Modifier.fillMaxSize().background(Fundo)) {
        Row(
            Modifier.fillMaxWidth().height(56.dp).padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar",
                modifier = Modifier.size(24.dp).clickable(onClick = onVoltar), tint = Color.White)
            Spacer(Modifier.width(12.dp))
            Text("GYEMA", fontWeight = FontWeight.Bold, fontSize = 20.sp,
                color = Color.White, modifier = Modifier.weight(1f))
            Icon(Icons.Outlined.Notifications, "Notificações", tint = TextoSecundario)
        }

        val atual = dados
        if (erro != null) {
            EstadoErro(erro!!, onTentarNovamente = { tentativa++ }, modifier = Modifier.padding(24.dp))
            return@Column
        }
        if (atual == null) {
            EstadoCarregando()
            return@Column
        }

        val eq = atual.equipamento
        val status = statusAmbiente(eq.status)
        Column(
            Modifier.weight(1f).verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Superficie),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(iconeEquipamento(eq.grupoMuscular), null, tint = Laranja,
                            modifier = Modifier.size(36.dp))
                        Spacer(Modifier.weight(1f))
                        Surface(color = status.fundo, shape = RoundedCornerShape(8.dp)) {
                            Text(status.rotulo.uppercase(), color = status.cor,
                                fontWeight = FontWeight.Bold, fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp))
                        }
                    }
                    Text(eq.nome, color = Color.White,
                        fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Text(
                        when (eq.status) {
                            "disponivel" -> "Este equipamento está disponível para uso agora."
                            "ocupado" -> "Este equipamento está em uso no momento."
                            else -> "Este equipamento está em manutenção."
                        },
                        color = TextoSecundario, fontSize = 13.sp
                    )
                    Informacao("Setor", eq.setores.nome)
                    Informacao("Código", eq.codigo)
                    eq.grupoMuscular?.let { Informacao("Grupo muscular", it) }
                    Informacao("Pessoas ao mesmo tempo", "${eq.capacidadeSimultanea}")
                    Informacao("Tempo máximo de uso", "${eq.tempoMaximoMin} min")
                }
            }

            eq.instrucoesUso?.takeIf { it.isNotBlank() }?.let {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Como usar", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
                    Text(it, color = TextoSecundario, fontSize = 13.sp)
                }
            }

            eq.videoUrl?.takeIf { it.isNotBlank() }?.let { url ->
                val abrirLink = LocalUriHandler.current
                Surface(color = Superficie, shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().clickable { runCatching { abrirLink.openUri(url) } }) {
                    Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.PlayCircle, null, tint = Laranja)
                        Spacer(Modifier.width(10.dp))
                        Text("Ver vídeo demonstrativo", color = Color.White, fontSize = 13.sp)
                    }
                }
            }

            val principais = eq.exercicios.filter { it.tipo == "principal" }.map { it.exercicios.nome }
            val alternativos = eq.exercicios.filter { it.tipo == "alternativo" }.map { it.exercicios.nome }
            if (principais.isNotEmpty()) SecaoExercicios("Exercícios principais", principais)
            if (alternativos.isNotEmpty()) SecaoExercicios("Exercícios alternativos", alternativos)
        }

        Column(Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)) {
            val uso = atual.usoAtivo
            when {
                uso != null && uso.equipamentos.id == eq.id ->
                    BotaoLaranja("Ver minha utilização", onAbrirUso)
                uso != null ->
                    AvisoBotao("Você está usando ${uso.equipamentos.nome}. Finalize esse uso para iniciar outro.")
                eq.status == "disponivel" ->
                    BotaoLaranja("Iniciar uso") { onIniciarUso(eq.id) }
                eq.status == "ocupado" ->
                    AvisoBotao("Equipamento ocupado. Tente novamente em alguns minutos.")
                else ->
                    AvisoBotao("Equipamento em manutenção. Escolha outro por enquanto.")
            }
            OutlinedButton(onClick = { onReportarProblema(eq.id) },
                modifier = Modifier.fillMaxWidth().height(44.dp),
                shape = RoundedCornerShape(12.dp)) {
                Text("Reportar problema", color = Color.White)
            }
        }
    }
}

@Composable
private fun Informacao(rotulo: String, valor: String) {
    Row(Modifier.fillMaxWidth()) {
        Text(rotulo, color = TextoSecundario, fontSize = 12.sp, modifier = Modifier.weight(1f))
        Text(valor, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun BotaoLaranja(texto: String, onClick: () -> Unit) {
    Button(onClick = onClick, modifier = Modifier.fillMaxWidth().height(56.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Laranja),
        shape = RoundedCornerShape(12.dp)) {
        Text(texto, fontWeight = FontWeight.Bold, color = Color.White)
    }
}

@Composable
private fun AvisoBotao(texto: String) {
    Surface(color = Superficie, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
        Text(texto, color = TextoSecundario, fontSize = 13.sp, textAlign = TextAlign.Center,
            modifier = Modifier.padding(16.dp))
    }
}

@Composable
private fun SecaoExercicios(titulo: String, itens: List<String>) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(titulo, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
        itens.forEach { nome ->
            Surface(color = Superficie, shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()) {
                Row(Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    Text("•", color = Laranja, fontSize = 18.sp)
                    Spacer(Modifier.width(12.dp))
                    Text(nome, color = Color.White, fontSize = 13.sp)
                }
            }
        }
    }
}
