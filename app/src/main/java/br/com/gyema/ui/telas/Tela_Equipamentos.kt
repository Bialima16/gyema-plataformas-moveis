package br.com.gyema.ui.telas

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.QrCode2
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.gyema.data.EquipamentoAluno
import br.com.gyema.data.RepositorioAluno
import br.com.gyema.data.UtilizacaoAtiva
import br.com.gyema.data.mensagemErroAluno
import br.com.gyema.data.tentar
import br.com.gyema.ui.components.BotaoPrincipal
import br.com.gyema.ui.components.CabecalhoAcademia
import br.com.gyema.ui.components.CardGyema
import br.com.gyema.ui.components.EstadoCarregando
import br.com.gyema.ui.components.EstadoErro
import br.com.gyema.ui.components.EstadoVazio
import br.com.gyema.ui.components.formatarCronometro
import br.com.gyema.ui.components.fracaoRestante
import br.com.gyema.ui.components.iconeEquipamento
import br.com.gyema.ui.components.rememberAgora
import br.com.gyema.ui.components.segundosRestantes
import br.com.gyema.ui.components.statusAmbiente
import br.com.gyema.ui.tema.*
import kotlinx.coroutines.launch

private data class DadosEquipamentos(val equipamentos: List<EquipamentoAluno>, val usoAtivo: UtilizacaoAtiva?)

@Composable
fun TelaEquipamentos(
    setorId: Int? = null,
    onVoltar: () -> Unit = {},
    onDetalhesEquipamento: (equipamentoId: Int) -> Unit = {},
    onAbrirUso: () -> Unit = {},
    onLerQrCode: () -> Unit = {}
) {
    var abaSelecionada by remember { mutableIntStateOf(0) }
    var setorFiltro by remember { mutableStateOf(setorId) }
    var dados by remember { mutableStateOf<DadosEquipamentos?>(null) }
    var erro by remember { mutableStateOf<String?>(null) }
    var tentativa by remember { mutableIntStateOf(0) }
    var finalizando by remember { mutableStateOf(false) }
    var aviso by remember { mutableStateOf<String?>(null) }
    val escopo = rememberCoroutineScope()

    LaunchedEffect(tentativa, setorFiltro) {
        erro = null
        tentar {
            DadosEquipamentos(
                RepositorioAluno.listarEquipamentos(setorFiltro),
                RepositorioAluno.minhaUtilizacaoAtiva()
            )
        }
            .onSuccess { dados = it }
            .onFailure { erro = mensagemErroAluno(it) }
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(Fundo)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        CabecalhoAcademia("Equipamentos", onVoltar) {
            CardGyema(Modifier.size(36.dp), shape = RoundedCornerShape(10.dp), onClick = onLerQrCode) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Icon(Icons.Outlined.QrCode2, "Ler QR Code", tint = Laranja, modifier = Modifier.size(20.dp))
                }
            }
        }

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Todos", "Disponíveis", "Ocupados").forEachIndexed { index, titulo ->
                FilterChip(
                    selected = abaSelecionada == index,
                    onClick = { abaSelecionada = index },
                    label = { Text(titulo) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Laranja, selectedLabelColor = Color.White
                    ),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        val atual = dados
        when {
            erro != null -> EstadoErro(erro!!, onTentarNovamente = { tentativa++ })
            atual == null -> EstadoCarregando()
            else -> {
                if (setorFiltro != null) {
                    val nomeSetor = atual.equipamentos.firstOrNull()?.setorNome ?: "Setor selecionado"
                    Row(
                        Modifier
                            .clip(RoundedCornerShape(50))
                            .background(Superficie)
                            .clickable { setorFiltro = null }
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Setor: $nomeSetor", fontSize = 12.sp)
                        Spacer(Modifier.width(6.dp))
                        Icon(Icons.Outlined.Close, "Tirar filtro", modifier = Modifier.size(14.dp))
                    }
                }

                atual.usoAtivo?.let { uso ->
                    UsandoAgoraCard(
                        uso = uso,
                        finalizando = finalizando,
                        onAbrir = onAbrirUso,
                        onFinalizar = {
                            finalizando = true
                            aviso = null
                            escopo.launch {
                                tentar { RepositorioAluno.finalizarUso(uso) }
                                    .onSuccess { tentativa++ }
                                    .onFailure { aviso = mensagemErroAluno(it) }
                                finalizando = false
                            }
                        }
                    )
                }
                aviso?.let { Text(it, color = Laranja, fontSize = 12.sp) }

                val visiveis = when (abaSelecionada) {
                    1 -> atual.equipamentos.filter { it.status == "disponivel" }
                    2 -> atual.equipamentos.filter { it.status == "ocupado" }
                    else -> atual.equipamentos
                }
                if (visiveis.isEmpty()) {
                    EstadoVazio("Nenhum equipamento", "Não há equipamentos nesse filtro agora.")
                } else {
                    visiveis.forEach { EquipamentoCard(it) { onDetalhesEquipamento(it.id) } }
                }
            }
        }
    }
}

@Composable
private fun UsandoAgoraCard(uso: UtilizacaoAtiva, finalizando: Boolean, onAbrir: () -> Unit, onFinalizar: () -> Unit) {
    val shape = RoundedCornerShape(14.dp)
    val agora = rememberAgora()
    Column(
        Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(Superficie)
            .border(1.5.dp, Laranja, shape)
            .clickable(onClick = onAbrir)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("VOCÊ ESTÁ USANDO", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Laranja, letterSpacing = 0.5.sp)
                Text(uso.equipamentos.nome, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            MiniCronometro(
                texto = formatarCronometro(segundosRestantes(uso.fimPrevisto, agora)),
                fracao = fracaoRestante(uso.inicio, uso.fimPrevisto, agora)
            )
        }
        BotaoPrincipal(
            if (finalizando) "Finalizando..." else "Finalizar utilização",
            onClick = { if (!finalizando) onFinalizar() },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun MiniCronometro(texto: String, fracao: Float) {
    Box(Modifier.size(44.dp), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val espessura = 3.5.dp.toPx()
            val topo = Offset(espessura / 2, espessura / 2)
            val tamanho = Size(size.width - espessura, size.height - espessura)
            drawArc(
                color = Borda, startAngle = 0f, sweepAngle = 360f, useCenter = false,
                topLeft = topo, size = tamanho, style = Stroke(espessura)
            )
            drawArc(
                color = Laranja, startAngle = -90f, sweepAngle = 360f * fracao, useCenter = false,
                topLeft = topo, size = tamanho, style = Stroke(espessura, cap = StrokeCap.Round)
            )
        }
        Text(texto, fontSize = 9.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun EquipamentoCard(e: EquipamentoAluno, onClick: () -> Unit) {
    val status = statusAmbiente(e.status)
    CardGyema(Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), onClick = onClick) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(status.cor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(iconeEquipamento(e.grupoMuscular), null, tint = status.cor, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(e.nome, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text("${e.setorNome} • ${e.codigo}", fontSize = 11.sp, color = TextoSecundario)
            }
            Box(
                Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(status.cor.copy(alpha = 0.18f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(status.rotulo.uppercase(), fontSize = 9.sp, fontWeight = FontWeight.Bold, color = status.cor)
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
private fun PreviewTelaEquipamentos() {
    GyemaTheme { TelaEquipamentos() }
}
