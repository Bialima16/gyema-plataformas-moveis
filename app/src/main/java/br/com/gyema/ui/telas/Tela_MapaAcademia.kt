package br.com.gyema.ui.telas

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.gyema.data.RepositorioAluno
import br.com.gyema.data.SetorOcupacao
import br.com.gyema.data.mensagemDeErro
import br.com.gyema.data.tentar
import br.com.gyema.ui.components.BotaoSino
import br.com.gyema.ui.components.CabecalhoAcademia
import br.com.gyema.ui.components.CardGyema
import br.com.gyema.ui.components.EstadoCarregando
import br.com.gyema.ui.components.EstadoErro
import br.com.gyema.ui.components.EstadoVazio
import br.com.gyema.ui.components.PontoLegenda
import br.com.gyema.ui.components.StatusAmbiente
import br.com.gyema.ui.components.iconeSetor
import br.com.gyema.ui.components.statusAmbiente
import br.com.gyema.ui.tema.*

@Composable
fun TelaMapaAcademia(
    onVoltar: () -> Unit = {},
    onAbrirZona: (setorId: Int) -> Unit = {}
) {
    var setores by remember { mutableStateOf<List<SetorOcupacao>?>(null) }
    var erro by remember { mutableStateOf<String?>(null) }
    var tentativa by remember { mutableIntStateOf(0) }

    LaunchedEffect(tentativa) {
        erro = null
        tentar { RepositorioAluno.ocupacaoSetores() }
            .onSuccess { setores = it }
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
        CabecalhoAcademia("Mapa da academia", onVoltar) { BotaoSino() }

        val lista = setores
        when {
            erro != null -> EstadoErro(erro!!, onTentarNovamente = { tentativa++ })
            lista == null -> EstadoCarregando()
            lista.isEmpty() -> EstadoVazio("Nenhum ambiente cadastrado", "Os setores da academia aparecem aqui.")
            else -> CardGyema(Modifier.fillMaxWidth(), fundo = SuperficieEscura) {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    lista.chunked(2).forEach { linha ->
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            linha.forEach { ZonaCard(it, Modifier.weight(1f)) { onAbrirZona(it.id) } }
                            if (linha.size == 1) Spacer(Modifier.weight(1f))
                        }
                    }
                }
            }
        }

        CardGyema(Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
            Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    "LEGENDA DE STATUS",
                    fontSize = 10.sp, fontWeight = FontWeight.Bold,
                    color = TextoSecundario, letterSpacing = 0.5.sp
                )
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    StatusAmbiente.entries.forEach { PontoLegenda(it.cor, it.rotulo) }
                }
            }
        }
    }
}

@Composable
private fun ZonaCard(setor: SetorOcupacao, modifier: Modifier, onClick: () -> Unit) {
    val shape = RoundedCornerShape(12.dp)
    val status = statusAmbiente(setor.status)
    Box(
        modifier
            .aspectRatio(1f)
            .clip(shape)
            .background(status.fundo)
            .border(1.5.dp, status.cor, shape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Icon(iconeSetor(setor.icone), null, tint = status.cor, modifier = Modifier.size(22.dp))
            Spacer(Modifier.height(2.dp))
            Text(setor.nome, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Text(status.rotulo, fontSize = 10.sp, color = TextoSecundario)
            Text("${setor.disponiveis}/${setor.totalEquipamentos} livres", fontSize = 10.sp, color = TextoSecundario)
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
private fun PreviewTelaMapaAcademia() {
    GyemaTheme { TelaMapaAcademia() }
}
