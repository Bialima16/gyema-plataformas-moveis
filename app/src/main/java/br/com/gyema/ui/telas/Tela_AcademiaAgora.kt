package br.com.gyema.ui.telas

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.gyema.data.RepositorioAluno
import br.com.gyema.data.SetorOcupacao
import br.com.gyema.data.mensagemDeErro
import br.com.gyema.data.tentar
import br.com.gyema.ui.components.BarraProgresso
import br.com.gyema.ui.components.BotaoSino
import br.com.gyema.ui.components.CabecalhoAcademia
import br.com.gyema.ui.components.CardGyema
import br.com.gyema.ui.components.EstadoCarregando
import br.com.gyema.ui.components.EstadoErro
import br.com.gyema.ui.components.EstadoVazio
import br.com.gyema.ui.components.PontoLegenda
import br.com.gyema.ui.components.TituloSecao
import br.com.gyema.ui.components.percentualLotacao
import br.com.gyema.ui.tema.*
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun TelaAcademiaAgora(
    onAbrirMapa: () -> Unit = {},
    onAbrirSetor: (setorId: Int) -> Unit = {}
) {
    var setores by remember { mutableStateOf<List<SetorOcupacao>?>(null) }
    var erro by remember { mutableStateOf<String?>(null) }
    var tentativa by remember { mutableIntStateOf(0) }
    var atualizadoEm by remember { mutableStateOf("") }

    LaunchedEffect(tentativa) {
        erro = null
        tentar { RepositorioAluno.ocupacaoSetores() }
            .onSuccess {
                setores = it
                atualizadoEm = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
            }
            .onFailure { erro = mensagemDeErro(it) }
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(Fundo)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        CabecalhoAcademia("Academia agora") { BotaoSino() }

        val lista = setores
        when {
            erro != null -> EstadoErro(erro!!, onTentarNovamente = { tentativa++ })
            lista == null -> EstadoCarregando()
            lista.isEmpty() -> EstadoVazio("Nenhum ambiente cadastrado", "Os setores da academia aparecem aqui.")
            else -> {
                val total = lista.sumOf { it.totalEquipamentos }
                val livres = lista.sumOf { it.disponiveis }
                val ocupados = lista.sumOf { it.ocupados }
                val manutencao = lista.sumOf { it.emManutencao }
                fun pct(n: Int) = if (total == 0) 0 else n * 100 / total

                Column(
                    Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    AnelLotacao(percentualLotacao(lista))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        PontoLegenda(Verde, "Livre — ${pct(livres)}%", 10.sp)
                        PontoLegenda(Laranja, "Ocupado — ${pct(ocupados)}%", 10.sp)
                        PontoLegenda(TextoSecundario, "Manutenção — ${pct(manutencao)}%", 10.sp)
                    }
                }

                TituloSecao("AMBIENTES")
                lista.forEach { setor ->
                    val emManutencao = setor.status == "manutencao"
                    AmbienteCard(
                        nome = setor.nome,
                        percentual = percentualLotacao(setor),
                        cor = when {
                            emManutencao -> TextoSecundario
                            percentualLotacao(setor) >= 60 -> Laranja
                            else -> Verde
                        },
                        emManutencao = emManutencao,
                        onClick = { onAbrirSetor(setor.id) }
                    )
                }

                CardGyema(Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), onClick = onAbrirMapa) {
                    Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Map, null, tint = Laranja, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(10.dp))
                        Text("Ver mapa da academia", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Row(
                    Modifier.fillMaxWidth().clickable { tentativa++ },
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Atualizado às $atualizadoEm", fontSize = 11.sp, color = TextoSecundario, textAlign = TextAlign.Center)
                    Spacer(Modifier.width(6.dp))
                    Icon(Icons.Outlined.Refresh, "Atualizar", tint = TextoSecundario, modifier = Modifier.size(14.dp))
                }
            }
        }
    }
}

@Composable
private fun AnelLotacao(percentual: Int) {
    Box(Modifier.size(180.dp), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val espessura = 22.dp.toPx()
            val topo = Offset(espessura / 2, espessura / 2)
            val tamanho = Size(size.width - espessura, size.height - espessura)
            drawArc(
                color = Borda, startAngle = 0f, sweepAngle = 360f, useCenter = false,
                topLeft = topo, size = tamanho, style = Stroke(espessura)
            )
            drawArc(
                color = Laranja, startAngle = -90f, sweepAngle = 360f * percentual / 100f, useCenter = false,
                topLeft = topo, size = tamanho, style = Stroke(espessura)
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("$percentual%", fontSize = 34.sp, fontWeight = FontWeight.Bold)
            Text("LOTAÇÃO ATUAL", fontSize = 9.sp, color = TextoSecundario, letterSpacing = 1.sp)
        }
    }
}

@Composable
private fun AmbienteCard(nome: String, percentual: Int, cor: Color, emManutencao: Boolean, onClick: () -> Unit) {
    CardGyema(Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), onClick = onClick) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(nome, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Text(if (emManutencao) "Manutenção" else "$percentual%", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = cor)
            }
            BarraProgresso(percentual / 100f, cor)
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
private fun PreviewTelaAcademiaAgora() {
    GyemaTheme { TelaAcademiaAgora() }
}
