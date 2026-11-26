package br.com.gyema.ui.telas

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.gyema.data.RepositorioAluno
import br.com.gyema.data.ResumoExecucaoAluno
import br.com.gyema.data.mensagemDeErro
import br.com.gyema.data.tentar
import br.com.gyema.ui.components.BotaoPrincipal
import br.com.gyema.ui.components.EstadoCarregando
import br.com.gyema.ui.components.EstadoErro
import br.com.gyema.ui.components.formatarData
import br.com.gyema.ui.components.minutosEntre
import br.com.gyema.ui.components.numeroBr
import br.com.gyema.ui.components.contagem
import br.com.gyema.ui.tema.*

@Composable
fun TelaResumoTreino(
    execucaoId: Long,
    onVoltar: () -> Unit = {}
) {
    var resumo by remember { mutableStateOf<ResumoExecucaoAluno?>(null) }
    var erro by remember { mutableStateOf<String?>(null) }
    var tentativa by remember { mutableIntStateOf(0) }

    LaunchedEffect(execucaoId, tentativa) {
        erro = null
        tentar { RepositorioAluno.resumoExecucao(execucaoId) }
            .onSuccess { resumo = it }
            .onFailure { erro = mensagemDeErro(it) }
    }

    Column(Modifier.fillMaxSize().background(Fundo).padding(24.dp)) {
        Column(
            Modifier.weight(1f).verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val atual = resumo
            when {
                erro != null -> EstadoErro(erro!!, onTentarNovamente = { tentativa++ })
                atual == null -> EstadoCarregando()
                else -> {
                    Box(Modifier.size(52.dp).border(2.dp, Verde, CircleShape), contentAlignment = Alignment.Center) {
                        Icon(Icons.Filled.Check, null, tint = Verde, modifier = Modifier.size(29.dp))
                    }
                    Spacer(Modifier.height(16.dp))
                    Text("Treino concluído!", fontWeight = FontWeight.Bold, fontSize = 23.sp)
                    Spacer(Modifier.height(6.dp))
                    Text(atual.ficha?.nome ?: "Treino livre", fontSize = 14.sp)
                    Text(
                        formatarData(atual.inicio, "dd/MM/yyyy") +
                            (atual.fim?.let { " • ${minutosEntre(atual.inicio, it)} min" } ?: ""),
                        color = TextoSecundario, fontSize = 12.sp
                    )
                    Spacer(Modifier.height(22.dp))

                    val volume = atual.series.sumOf { (it.cargaKg ?: 0.0) * it.repeticoes }
                    Box(
                        Modifier.fillMaxWidth()
                            .background(LaranjaEscuro, RoundedCornerShape(9.dp))
                            .border(1.dp, Laranja, RoundedCornerShape(9.dp))
                            .padding(14.dp)
                    ) {
                        Text(
                            "${contagem(atual.series.size, "série", "séries")} • ${numeroBr(volume, 0)} kg levantados",
                            color = Laranja, fontWeight = FontWeight.Bold, fontSize = 14.sp
                        )
                    }
                    Spacer(Modifier.height(14.dp))

                    atual.series.groupBy { it.exercicios.nome }.forEach { (nome, series) ->
                        val maior = series.mapNotNull { it.cargaKg }.maxOrNull()
                        Row(
                            Modifier.fillMaxWidth().padding(bottom = 8.dp)
                                .background(Superficie, RoundedCornerShape(9.dp)).padding(13.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(nome, fontWeight = FontWeight.Bold, fontSize = 13.sp, modifier = Modifier.weight(1f))
                            Text(
                                contagem(series.size, "série", "séries") + (maior?.let { " • até ${numeroBr(it)} kg" } ?: ""),
                                color = TextoSecundario, fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(14.dp))
        BotaoPrincipal("Voltar aos treinos", onVoltar, Modifier.fillMaxWidth())
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
private fun PreviewTelaResumoTreino() {
    GyemaTheme { TelaResumoTreino(execucaoId = 0) }
}
