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
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.gyema.data.Aluno
import br.com.gyema.data.ExecucaoResumo
import br.com.gyema.data.FichaResumo
import br.com.gyema.data.RepositorioInstrutor
import br.com.gyema.data.mensagemDeErro
import br.com.gyema.data.tentar
import br.com.gyema.ui.components.CardGyema
import br.com.gyema.ui.components.EstadoCarregando
import br.com.gyema.ui.components.EstadoErro
import br.com.gyema.ui.components.formatarData
import br.com.gyema.ui.components.iniciais
import br.com.gyema.ui.tema.*
import java.time.Duration
import java.time.OffsetDateTime

private class DadosAluno(
    val aluno: Aluno,
    val fichas: List<FichaResumo>,
    val execucoes: List<ExecucaoResumo>
)

@Composable
fun TelaDetalhesAluno(
    alunoId: String,
    modifier: Modifier = Modifier,
    onVoltarClick: () -> Unit = {},
    onCriarNovaFichaClick: (alunoId: String) -> Unit = {},
    onEditarFichaClick: (alunoId: String, fichaId: Long) -> Unit = { _, _ -> }
) {
    var dados by remember { mutableStateOf<DadosAluno?>(null) }
    var erro by remember { mutableStateOf<String?>(null) }
    var tentativa by remember { mutableIntStateOf(0) }

    // Também recarrega ao voltar da tela de nova ficha
    LaunchedEffect(alunoId, tentativa) {
        erro = null
        tentar {
            DadosAluno(
                aluno = RepositorioInstrutor.buscarAluno(alunoId),
                fichas = RepositorioInstrutor.fichasDoAluno(alunoId),
                execucoes = RepositorioInstrutor.execucoesDoAluno(alunoId)
            )
        }.onSuccess { dados = it }.onFailure { erro = mensagemDeErro(it) }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Fundo)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // cabecalho
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(SuperficieEscura)
                        .clickable(onClick = onVoltarClick),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Voltar",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Detalhes do aluno",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            val d = dados
            when {
                erro != null -> EstadoErro(erro!!, onTentarNovamente = { tentativa++ })
                d == null -> EstadoCarregando()
                else -> ConteudoAluno(d) { fichaId -> onEditarFichaClick(alunoId, fichaId) }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // botao criar nova ficha
        val shapeBotao = RoundedCornerShape(14.dp)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .clip(shapeBotao)
                .background(Laranja)
                .clickable { onCriarNovaFichaClick(alunoId) },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Criar nova ficha",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
private fun ConteudoAluno(dados: DadosAluno, onEditarFicha: (fichaId: Long) -> Unit) {
    val aluno = dados.aluno

    // card perfil aluno
    CardGyema(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(SuperficieEscura)
                    .border(1.dp, Borda, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = iniciais(aluno.nome),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(
                    text = aluno.nome,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = listOfNotNull(aluno.curso, aluno.matricula?.let { "Mat. $it" })
                        .joinToString(" • ").ifEmpty { "Curso não informado" },
                    fontSize = 12.sp,
                    color = TextoSecundario
                )
            }
        }
    }

    // resumo da evolucao (últimos 30 dias)
    val minutos = dados.execucoes.sumOf { duracaoMinutos(it) ?: 0L }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Rotulo("RESUMO DOS ÚLTIMOS 30 DIAS")
        CardGyema(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val qtd = dados.execucoes.size
                BlocoMetrica(
                    rotulo = "Frequência",
                    valor = if (qtd == 1) "1 treino" else "$qtd treinos",
                    modifier = Modifier.weight(1f)
                )
                BlocoMetrica(
                    rotulo = "Tempo total",
                    valor = formatarDuracao(minutos),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }

    // fichas ativas
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Rotulo("FICHAS ATIVAS")
        if (dados.fichas.isEmpty()) {
            TextoInformativo("Este aluno ainda não tem fichas. Toque em \"Criar nova ficha\".")
        }
        dados.fichas.forEach { ficha -> CardFicha(ficha) { onEditarFicha(ficha.id) } }
    }

    // historico de treinos
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Rotulo("HISTÓRICO DE TREINOS")
        if (dados.execucoes.isEmpty()) {
            TextoInformativo("Nenhum treino registrado nos últimos 30 dias.")
        }
        dados.execucoes.take(10).forEach { CardHistoricoTreino(it) }
    }
}

@Composable
private fun Rotulo(texto: String) {
    Text(
        text = texto,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = TextoSecundario,
        letterSpacing = 0.5.sp
    )
}

@Composable
private fun TextoInformativo(texto: String) {
    Text(text = texto, fontSize = 12.sp, color = TextoSecundario)
}

@Composable
private fun CardFicha(ficha: FichaResumo, onClick: () -> Unit) {
    CardGyema(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = ficha.nome,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                ficha.grupoFoco?.let {
                    Text(text = it, fontSize = 12.sp, color = TextoSecundario)
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.FitnessCenter,
                    contentDescription = null,
                    tint = TextoSecundario,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                val qtd = ficha.itens.size
                Text(
                    text = (if (qtd == 1) "1 exercício" else "$qtd exercícios") +
                        if (qtd > 0) ": " + ficha.itens.joinToString(", ") { it.exercicios.nome } else "",
                    fontSize = 12.sp,
                    color = TextoSecundario,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun BlocoMetrica(
    rotulo: String,
    valor: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(SuperficieEscura)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = rotulo,
            fontSize = 11.sp,
            color = TextoSecundario
        )
        Text(
            text = valor,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

@Composable
private fun CardHistoricoTreino(execucao: ExecucaoResumo) {
    val exercicios = execucao.series.map { it.exercicios.nome }.distinct()
    CardGyema(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(RoxoEscuro)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = formatarData(execucao.inicio, "dd/MM"),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Laranja
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = execucao.ficha?.let { f ->
                        listOfNotNull(f.nome, f.grupoFoco).joinToString(" → ")
                    } ?: "Treino livre",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.AccessTime,
                        contentDescription = null,
                        tint = TextoSecundario,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = duracaoMinutos(execucao)?.let { formatarDuracao(it) } ?: "Em andamento",
                        fontSize = 11.sp,
                        color = TextoSecundario
                    )
                }
            }

            Text(
                text = exercicios.joinToString(", ").ifEmpty { "Nenhuma série registrada" },
                fontSize = 11.sp,
                color = TextoSecundario,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/** Duração em minutos, ou null se o treino ainda não terminou. */
private fun duracaoMinutos(execucao: ExecucaoResumo): Long? {
    val fim = execucao.fim ?: return null
    return runCatching {
        Duration.between(OffsetDateTime.parse(execucao.inicio), OffsetDateTime.parse(fim)).toMinutes()
    }.getOrNull()
}

private fun formatarDuracao(minutos: Long): String = when {
    minutos < 60 -> "${minutos}min"
    minutos % 60 == 0L -> "${minutos / 60}h"
    else -> "${minutos / 60}h${minutos % 60}min"
}

// preview da tela (sem banco, mostra o carregamento)
@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
private fun PreviewTelaDetalhesAluno() {
    GyemaTheme {
        TelaDetalhesAluno(alunoId = "preview")
    }
}
