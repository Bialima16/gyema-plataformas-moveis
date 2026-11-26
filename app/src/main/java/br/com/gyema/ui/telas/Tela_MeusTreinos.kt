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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.ShowChart
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.gyema.data.ExecucaoResumo
import br.com.gyema.data.FichaAluno
import br.com.gyema.data.RepositorioAluno
import br.com.gyema.data.mensagemDeErro
import br.com.gyema.data.tentar
import br.com.gyema.ui.components.AbasSegmentadas
import br.com.gyema.ui.components.CardGyema
import br.com.gyema.ui.components.EstadoCarregando
import br.com.gyema.ui.components.EstadoErro
import br.com.gyema.ui.components.EstadoVazio
import br.com.gyema.ui.components.formatarData
import br.com.gyema.ui.components.minutosEntre
import br.com.gyema.ui.components.contagem
import br.com.gyema.ui.tema.*

private data class DadosTreinos(
    val fichas: List<FichaAluno>,
    val historico: List<ExecucaoResumo>,
    val fichaDoDiaId: Long?
)

@Composable
fun TelaMeusTreinos(
    onAbrirTreino: (fichaId: Long) -> Unit = {},
    onAbrirExecucao: (execucaoId: Long) -> Unit = {},
    onEvolucao: () -> Unit = {}
) {
    var aba by rememberSaveable { mutableIntStateOf(0) }
    var dados by remember { mutableStateOf<DadosTreinos?>(null) }
    var erro by remember { mutableStateOf<String?>(null) }
    var tentativa by remember { mutableIntStateOf(0) }

    LaunchedEffect(tentativa) {
        erro = null
        tentar {
            val fichas = RepositorioAluno.minhasFichas()
            DadosTreinos(fichas, RepositorioAluno.historicoTreinos(), fichaDoDia(fichas, RepositorioAluno.fichaDoUltimoTreino()))
        }
            .onSuccess { dados = it }
            .onFailure { erro = mensagemDeErro(it) }
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(Fundo)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Meus treinos", fontSize = 22.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            CardGyema(shape = RoundedCornerShape(10.dp), onClick = onEvolucao) {
                Row(Modifier.padding(horizontal = 10.dp, vertical = 7.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.AutoMirrored.Outlined.ShowChart, null, tint = Laranja, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Evolução", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
        AbasSegmentadas(listOf("Atual", "Histórico"), selecionado = aba, onSelecionar = { aba = it })

        val atual = dados
        when {
            erro != null -> EstadoErro(erro!!, onTentarNovamente = { tentativa++ })
            atual == null -> EstadoCarregando()
            aba == 0 && atual.fichas.isEmpty() ->
                EstadoVazio("Nenhum treino ainda", "Quando seu instrutor montar sua ficha, ela aparece aqui.")
            aba == 0 -> atual.fichas.forEach { ficha ->
                FichaCard(ficha, doDia = ficha.id == atual.fichaDoDiaId) { onAbrirTreino(ficha.id) }
            }
            atual.historico.isEmpty() ->
                EstadoVazio("Nenhum treino feito", "Seus treinos concluídos aparecem aqui.")
            else -> atual.historico.forEach { exec ->
                HistoricoCard(exec) { onAbrirExecucao(exec.id) }
            }
        }
    }
}

private fun fichaDoDia(fichas: List<FichaAluno>, ultimaFichaId: Long?): Long? {
    if (fichas.isEmpty()) return null
    val posicao = fichas.indexOfFirst { it.id == ultimaFichaId }
    return fichas[(posicao + 1) % fichas.size].id
}

@Composable
private fun FichaCard(ficha: FichaAluno, doDia: Boolean, onClick: () -> Unit) {
    val shape = RoundedCornerShape(14.dp)
    val corBorda = if (doDia) Laranja else Borda

    Column(
        Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(Superficie)
            .border(if (doDia) 1.5.dp else 1.dp, corBorda, shape)
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                ficha.nome, fontSize = 14.sp, fontWeight = FontWeight.Bold,
                maxLines = 1, overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            if (doDia) {
                Spacer(Modifier.width(6.dp))
                Box(
                    Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(LaranjaEscuro)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text("TREINO DO DIA", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Laranja)
                }
            }
        }
        Text(ficha.grupoFoco ?: "Treino ${'A' + (ficha.ordem - 1)}", fontSize = 12.sp, color = TextoSecundario)
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Outlined.FitnessCenter, null, tint = Laranja, modifier = Modifier.size(14.dp))
            Spacer(Modifier.width(6.dp))
            Text(contagem(ficha.itens.size, "exercício", "exercícios"), fontSize = 11.sp, color = TextoSecundario, modifier = Modifier.weight(1f))
            Box(
                Modifier
                    .size(24.dp)
                    .background(if (doDia) Laranja else Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward, null,
                    tint = if (doDia) Color.White else Fundo,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

@Composable
private fun HistoricoCard(exec: ExecucaoResumo, onClick: () -> Unit) {
    val exercicios = exec.series.map { it.exercicios.nome }.distinct()
    CardGyema(Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), onClick = onClick) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier.clip(RoundedCornerShape(6.dp)).background(LaranjaEscuro)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) { Text(formatarData(exec.inicio, "dd/MM"), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Laranja) }
                Spacer(Modifier.width(8.dp))
                Text(
                    exec.ficha?.nome ?: "Treino livre", fontSize = 13.sp, fontWeight = FontWeight.Bold,
                    maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f)
                )
                Icon(Icons.Outlined.AccessTime, null, modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(3.dp))
                Text(if (exec.fim == null) "em aberto" else "${minutosEntre(exec.inicio, exec.fim)}min", fontSize = 12.sp)
            }
            Text(
                if (exercicios.isEmpty()) "Nenhuma série registrada" else exercicios.joinToString(", "),
                fontSize = 11.sp, color = TextoSecundario, maxLines = 1, overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
private fun PreviewTelaMeusTreinos() {
    GyemaTheme { TelaMeusTreinos() }
}
