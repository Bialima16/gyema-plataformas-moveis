package br.com.gyema.ui.telas

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.gyema.R
import br.com.gyema.ui.components.*
import br.com.gyema.ui.tema.*

@Composable
private fun FundoTreino(content: @Composable ColumnScope.() -> Unit) {
    Column(modifier = Modifier.fillMaxSize().background(Fundo).verticalScroll(rememberScrollState())
        .padding(24.dp), content = content)
}
@Composable
private fun Painel(content: @Composable ColumnScope.() -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().background(Superficie, RoundedCornerShape(14.dp))
        .padding(16.dp), content = content)
}

@Composable
fun TelaDetalhesExercicio(onVoltar: () -> Unit) {
    var aba by rememberSaveable { mutableIntStateOf(0) }
    var aviso by remember { mutableStateOf(false) }
    val titulos = listOf(R.string.workout_video, R.string.workout_muscles,
        R.string.workout_description, R.string.workout_history)
    FundoTreino {
        CabecalhoVoltar(stringResource(R.string.workout_supino_title), onVoltar)
        Spacer(Modifier.height(30.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            titulos.forEachIndexed { indice, titulo ->
                TextButton(onClick = { aba = indice }, modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.textButtonColors(
                        containerColor = if (aba == indice) Laranja else Superficie
                    ), contentPadding = PaddingValues(2.dp)) {
                    Text(stringResource(titulo), color = Color.White, fontSize = 10.sp)
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        if (aba == 0) {
            Box(Modifier.fillMaxWidth().height(180.dp)
                .background(Superficie, RoundedCornerShape(12.dp))
                .clickable { aviso = true }, contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.workout_media_pending), color = Color.LightGray,
                    textAlign = TextAlign.Center)
            }
            Spacer(Modifier.height(20.dp))
        }
        if (aba <= 1) {
            Painel {
                Text(stringResource(R.string.workout_muscles_heading), fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Text(stringResource(R.string.workout_primary))
                Text(stringResource(R.string.workout_secondary), color = Color.LightGray)
            }
        }
        if (aba == 0) {
            Spacer(Modifier.height(20.dp))
            Text(stringResource(R.string.workout_tips_heading), fontWeight = FontWeight.Bold)
            listOf(R.string.workout_tip_one, R.string.workout_tip_two, R.string.workout_tip_three).forEach {
                Spacer(Modifier.height(10.dp))
                Text("• " + stringResource(it), color = Color.LightGray)
            }
        }
        if (aba >= 2) {
            Spacer(Modifier.height(20.dp))
            Text(stringResource(if (aba == 3) R.string.workout_history_pending
                else R.string.workout_description_pending), color = Color.LightGray)
        }
    }
    if (aviso) AvisoPendente(stringResource(R.string.workout_media_notice)) { aviso = false }
}

@Composable
fun TelaEquipamentoOcupado(onVoltar: () -> Unit) {
    var aviso by remember { mutableStateOf<Int?>(null) }
    FundoTreino {
        CabecalhoVoltar("", onVoltar)
        Spacer(Modifier.height(70.dp))
        Painel {
            Text("⚠", fontSize = 44.sp, color = Laranja,
                modifier = Modifier.align(Alignment.CenterHorizontally))
            Spacer(Modifier.height(18.dp))
            Text(stringResource(R.string.workout_occupied_title), fontSize = 21.sp,
                fontWeight = FontWeight.Bold, textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(12.dp))
            Text(stringResource(R.string.workout_occupied_message), color = Color.LightGray,
                textAlign = TextAlign.Center)
            Spacer(Modifier.height(24.dp))
            BotaoGyema(stringResource(R.string.workout_reorder),
                { aviso = R.string.workout_reorder_pending })
            Spacer(Modifier.height(12.dp))
            BotaoGyema(stringResource(R.string.workout_keep_order), onVoltar, secundario = true)
            Spacer(Modifier.height(12.dp))
            TextButton(onClick = { aviso = R.string.workout_alternatives_pending },
                modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.workout_alternatives), color = Laranja)
            }
        }
    }
    aviso?.let { id -> AvisoPendente(stringResource(id)) { aviso = null } }
}

@Composable
fun TelaResumoTreino(onVoltar: () -> Unit) {
    val exercicios = listOf(
        R.string.workout_supino to R.string.workout_supino_stats,
        R.string.workout_inclinado to R.string.workout_inclinado_stats,
        R.string.workout_crucifixo to R.string.workout_crucifixo_stats,
        R.string.workout_crossover to R.string.workout_crossover_stats,
        R.string.workout_corda to R.string.workout_corda_stats,
        R.string.workout_frances to R.string.workout_frances_stats
    )
    FundoTreino {
        Text("✓", fontSize = 44.sp, color = Color(0xFF00D9A0),
            modifier = Modifier.align(Alignment.CenterHorizontally))
        Text(stringResource(R.string.workout_complete), fontSize = 24.sp,
            fontWeight = FontWeight.Bold, textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        Text(stringResource(R.string.workout_name), textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth())
        Text(stringResource(R.string.workout_date), color = Color.LightGray,
            textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(20.dp))
        Painel {
            Text(stringResource(R.string.workout_sets), color = Laranja,
                fontWeight = FontWeight.Bold)
        }
        exercicios.forEach { (nome, dados) ->
            Spacer(Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth().background(Superficie, RoundedCornerShape(12.dp))
                .padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(stringResource(nome), modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
                Text(stringResource(dados), color = Color.LightGray, fontSize = 12.sp)
            }
        }
        Spacer(Modifier.height(36.dp))
        BotaoGyema(stringResource(R.string.workout_return), onVoltar)
    }
}