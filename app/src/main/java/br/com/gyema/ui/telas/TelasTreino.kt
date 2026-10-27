package br.com.gyema.ui.telas

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.gyema.R
import br.com.gyema.ui.components.AvisoPendente

private val Azul = Color(0xFF0B0F2E)
private val Cartao = Color(0xFF161B3E)
private val Laranja = Color(0xFFF34B00)
private val Suave = Color(0xFFADB0C9)
private val Verde = Color(0xFF00D9A0)

@Composable
private fun FundoTreino(conteudo: @Composable ColumnScope.() -> Unit) {
    Column(Modifier.fillMaxSize().background(Azul).verticalScroll(rememberScrollState())
        .padding(24.dp), content = conteudo)
}

@Composable
private fun PainelTreino(modifier: Modifier = Modifier, conteudo: @Composable ColumnScope.() -> Unit) {
    Column(modifier.fillMaxWidth().background(Cartao, RoundedCornerShape(12.dp))
        .padding(14.dp), content = conteudo)
}

@Composable
private fun BotaoTreino(texto: String, aoClicar: () -> Unit, secundario: Boolean = false) {
    Button(onClick = aoClicar, modifier = Modifier.fillMaxWidth().height(48.dp),
        shape = RoundedCornerShape(9.dp),
        colors = ButtonDefaults.buttonColors(containerColor = if (secundario) Cartao else Laranja,
            contentColor = Color.White)) {
        Text(texto, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun CabecalhoTreino(titulo: String, voltar: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(40.dp).clip(RoundedCornerShape(10.dp))
            .background(Cartao).clickable(onClick = voltar), contentAlignment = Alignment.Center) {
            Text("‹", color = Color.White, fontSize = 28.sp)
        }
        Spacer(Modifier.width(12.dp))
        Text(titulo, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
    }
}

@Composable
fun TelaDetalhesExercicio(onVoltar: () -> Unit) {
    var aba by rememberSaveable { mutableIntStateOf(0) }
    var aviso by remember { mutableStateOf(false) }
    val abas = listOf(R.string.workout_video, R.string.workout_muscles,
        R.string.workout_description, R.string.workout_history)
    FundoTreino {
        CabecalhoTreino(stringResource(R.string.workout_supino_title), onVoltar)
        Spacer(Modifier.height(30.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            abas.forEachIndexed { indice, titulo ->
                Box(Modifier.weight(1f).height(38.dp)
                    .clip(RoundedCornerShape(7.dp))
                    .background(if (aba == indice) Laranja else Cartao)
                    .clickable { aba = indice }, contentAlignment = Alignment.Center) {
                    Text(stringResource(titulo), color = if (aba == indice) Color.White else Suave,
                        fontSize = 11.sp, maxLines = 1)
                }
            }
        }
        Spacer(Modifier.height(14.dp))
        if (aba == 0) {
            Box(Modifier.fillMaxWidth().height(180.dp)
                .clip(RoundedCornerShape(10.dp)).background(Cartao)
                .clickable { aviso = true }) {
                // Espaço reservado para a mídia real do exercício.
                Box(Modifier.align(Alignment.TopStart).padding(12.dp)
                    .size(48.dp).background(Laranja, CircleShape),
                    contentAlignment = Alignment.Center) {
                    Text("▶", color = Color.White, fontSize = 20.sp)
                }
                Text(stringResource(R.string.workout_media_pending),
                    color = Suave, fontSize = 12.sp,
                    modifier = Modifier.align(Alignment.Center), textAlign = TextAlign.Center)
            }
            Spacer(Modifier.height(20.dp))
        }
        if (aba <= 1) {
            PainelTreino {
                Text(stringResource(R.string.workout_muscles_heading), color = Color.White,
                    fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(9.dp))
                Text(stringResource(R.string.workout_primary), color = Suave, fontSize = 13.sp)
                Spacer(Modifier.height(5.dp))
                Text(stringResource(R.string.workout_secondary), color = Suave, fontSize = 13.sp)
            }
        }
        if (aba == 0) {
            Spacer(Modifier.height(20.dp))
            Text(stringResource(R.string.workout_tips_heading), color = Color.White,
                fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Spacer(Modifier.height(10.dp))
            listOf(R.string.workout_tip_one, R.string.workout_tip_two,
                R.string.workout_tip_three).forEach { id ->
                Row(Modifier.padding(bottom = 11.dp), verticalAlignment = Alignment.Top) {
                    Box(Modifier.padding(top = 5.dp).size(12.dp).background(Verde, CircleShape))
                    Spacer(Modifier.width(10.dp))
                    Text(stringResource(id), color = Suave, fontSize = 13.sp)
                }
            }
        }
        if (aba >= 2) {
            Spacer(Modifier.height(20.dp))
            Text(stringResource(if (aba == 2) R.string.workout_description_pending
                else R.string.workout_history_pending), color = Suave, fontSize = 13.sp)
        }
    }
    if (aviso) AvisoPendente(stringResource(R.string.workout_media_notice)) { aviso = false }
}

@Composable
fun TelaEquipamentoOcupado(onVoltar: () -> Unit) {
    var aviso by remember { mutableStateOf<Int?>(null) }
    Box(Modifier.fillMaxSize().background(Azul)) {
        Column(Modifier.fillMaxSize().padding(24.dp)) {
            CabecalhoTreino("", onVoltar)
        }
        PainelTreino(Modifier.align(Alignment.Center).padding(horizontal = 24.dp)) {
            Box(Modifier.align(Alignment.CenterHorizontally).size(60.dp)
                .background(Color(0xFF302039), CircleShape), contentAlignment = Alignment.Center) {
                Text("⚠", color = Laranja, fontSize = 32.sp)
            }
            Spacer(Modifier.height(20.dp))
            Text(stringResource(R.string.workout_occupied_title), color = Color.White,
                fontWeight = FontWeight.Bold, fontSize = 20.sp,
                modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
            Spacer(Modifier.height(12.dp))
            Text(stringResource(R.string.workout_occupied_message), color = Suave,
                fontSize = 13.sp, textAlign = TextAlign.Center)
            Spacer(Modifier.height(22.dp))
            BotaoTreino(stringResource(R.string.workout_reorder),
                { aviso = R.string.workout_reorder_pending })
            Spacer(Modifier.height(10.dp))
            BotaoTreino(stringResource(R.string.workout_keep_order), onVoltar, true)
            Spacer(Modifier.height(8.dp))
            TextButton(onClick = { aviso = R.string.workout_alternatives_pending },
                modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.workout_alternatives),
                    color = Laranja, fontSize = 12.sp)
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
    Column(Modifier.fillMaxSize().background(Azul).padding(24.dp)) {
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally) {
            Box(Modifier.size(52.dp).border(2.dp, Verde, CircleShape),
                contentAlignment = Alignment.Center) {
                Text("✓", color = Verde, fontSize = 29.sp)
            }
            Spacer(Modifier.height(16.dp))
            Text(stringResource(R.string.workout_complete), color = Color.White,
                fontWeight = FontWeight.Bold, fontSize = 23.sp)
            Spacer(Modifier.height(6.dp))
            Text(stringResource(R.string.workout_name), color = Color.White, fontSize = 14.sp)
            Text(stringResource(R.string.workout_date), color = Suave, fontSize = 12.sp)
            Spacer(Modifier.height(22.dp))
            Box(Modifier.fillMaxWidth().border(1.dp, Laranja, RoundedCornerShape(9.dp))
                .background(Color(0xFF302039), RoundedCornerShape(9.dp))
                .padding(14.dp)) {
                Text("♙  " + stringResource(R.string.workout_sets), color = Laranja,
                    fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
            Spacer(Modifier.height(14.dp))
            exercicios.forEach { (nome, dados) ->
                Row(Modifier.fillMaxWidth().padding(bottom = 8.dp)
                    .background(Cartao, RoundedCornerShape(9.dp)).padding(13.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    Text(stringResource(nome), color = Color.White,
                        fontWeight = FontWeight.Bold, fontSize = 13.sp,
                        modifier = Modifier.weight(1f))
                    Text(stringResource(dados), color = Suave, fontSize = 12.sp)
                }
            }
        }
        Spacer(Modifier.height(14.dp))
        BotaoTreino(stringResource(R.string.workout_return), onVoltar)
    }
}
