package br.com.gyema.ui.telas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
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
import br.com.gyema.data.FichaDetalheAluno
import br.com.gyema.data.ItemFichaAluno
import br.com.gyema.data.RepositorioAluno
import br.com.gyema.data.mensagemDeErro
import br.com.gyema.data.tentar
import br.com.gyema.ui.components.BotaoPrincipal
import br.com.gyema.ui.components.BotaoVoltarQuadrado
import br.com.gyema.ui.components.CardGyema
import br.com.gyema.ui.components.EstadoCarregando
import br.com.gyema.ui.components.EstadoErro
import br.com.gyema.ui.components.EstadoVazio
import br.com.gyema.ui.components.numeroBr
import br.com.gyema.ui.components.contagem
import br.com.gyema.ui.tema.*

@Composable
fun TelaDetalhesTreino(
    fichaId: Long,
    onVoltar: () -> Unit = {},
    onIniciar: (fichaId: Long) -> Unit = {}
) {
    var ficha by remember { mutableStateOf<FichaDetalheAluno?>(null) }
    var erro by remember { mutableStateOf<String?>(null) }
    var tentativa by remember { mutableIntStateOf(0) }

    LaunchedEffect(fichaId, tentativa) {
        erro = null
        tentar { RepositorioAluno.buscarFicha(fichaId) }
            .onSuccess { ficha = it }
            .onFailure { erro = mensagemDeErro(it) }
    }

    Column(Modifier.fillMaxSize().background(Fundo)) {
        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val atual = ficha
            Row(verticalAlignment = Alignment.CenterVertically) {
                BotaoVoltarQuadrado(onVoltar)
                Spacer(Modifier.width(12.dp))
                Text(atual?.nome ?: "Treino", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                if (atual != null) {
                    Row(
                        Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(LaranjaEscuro)
                            .padding(horizontal = 8.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Outlined.AccessTime, null, tint = Laranja, modifier = Modifier.size(13.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("~${minutosEstimados(atual.itens)} min", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Laranja)
                    }
                }
            }

            when {
                erro != null -> EstadoErro(erro!!, onTentarNovamente = { tentativa++ })
                atual == null -> EstadoCarregando()
                atual.itens.isEmpty() -> EstadoVazio("Ficha sem exercícios", "Peça ao seu instrutor para completar a ficha.")
                else -> {
                    atual.grupoFoco?.let { Text(it, fontSize = 13.sp, color = TextoSecundario) }
                    Spacer(Modifier.height(4.dp))
                    atual.itens.forEachIndexed { i, item -> ExercicioCard(i + 1, item) }
                }
            }
        }

        if (ficha?.itens?.isNotEmpty() == true) {
            BotaoPrincipal(
                "Iniciar treino", { onIniciar(fichaId) },
                Modifier.fillMaxWidth().padding(16.dp)
            )
        }
    }
}

private fun minutosEstimados(itens: List<ItemFichaAluno>): Int =
    (itens.sumOf { it.series * (45 + (it.descansoSeg ?: 60)) } / 60).coerceAtLeast(1)

@Composable
private fun ExercicioCard(numero: Int, item: ItemFichaAluno) {
    CardGyema(Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.Top) {
            Box(
                Modifier.size(32.dp).clip(RoundedCornerShape(8.dp)).background(LaranjaEscuro),
                contentAlignment = Alignment.Center
            ) {
                Text(numero.toString(), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Laranja)
            }
            Spacer(Modifier.width(12.dp))
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(item.exercicios.nome, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                val partes = listOfNotNull(
                    "${contagem(item.series, "série", "séries")} x ${contagem(item.repeticoes, "repetição", "repetições")}",
                    item.cargaKg?.let { "${numeroBr(it)} kg" },
                    item.descansoSeg?.let { "$it s descanso" }
                )
                Text(partes.joinToString(" • "), fontSize = 11.sp, color = TextoSecundario)
                item.tecnica?.takeIf { it.isNotBlank() }?.let { Text("Técnica: $it", fontSize = 11.sp, color = Laranja) }
                item.observacoes?.takeIf { it.isNotBlank() }?.let { Text(it, fontSize = 11.sp, color = TextoSecundario) }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
private fun PreviewTelaDetalhesTreino() {
    GyemaTheme { TelaDetalhesTreino(fichaId = 0) }
}
