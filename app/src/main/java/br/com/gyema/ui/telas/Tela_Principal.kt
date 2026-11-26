package br.com.gyema.ui.telas

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import br.com.gyema.data.ExecucaoAluno
import br.com.gyema.data.NotificacaoAluno
import br.com.gyema.data.RepositorioAluno
import br.com.gyema.data.SetorOcupacao
import br.com.gyema.data.mensagemDeErro
import br.com.gyema.data.tentar
import br.com.gyema.data.usuarioLogadoId
import br.com.gyema.ui.components.CardGyema
import br.com.gyema.ui.components.EstadoCarregando
import br.com.gyema.ui.components.EstadoErro
import br.com.gyema.ui.components.TituloSecao
import br.com.gyema.ui.components.percentualLotacao
import br.com.gyema.ui.components.tempoRelativo
import br.com.gyema.ui.components.textoLotacao
import br.com.gyema.ui.tema.*
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.YearMonth
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters

private data class DiaSemana(val sigla: String, val numero: Int, val treinou: Boolean)

private data class DadosInicio(
    val nome: String,
    val setores: List<SetorOcupacao>,
    val execucoes: List<ExecucaoAluno>
)

@Composable
fun TelaPrincipal(
    onMeuTreino: () -> Unit = {},
    onAcademia: () -> Unit = {},
    onComunidade: () -> Unit = {},
    onChatbot: () -> Unit = {},
    onSugestoes: () -> Unit = {},
    onNotificacoes: () -> Unit = {}
) {
    var dados by remember { mutableStateOf<DadosInicio?>(null) }
    var erro by remember { mutableStateOf<String?>(null) }
    var tentativa by remember { mutableIntStateOf(0) }

    var avisos by remember { mutableStateOf<List<NotificacaoAluno>>(emptyList()) }
    var mostrarAvisos by remember { mutableStateOf(false) }
    val contexto = LocalContext.current
    var ultimaAbertura by remember { mutableStateOf(lerUltimaAbertura(contexto)) }

    LaunchedEffect(tentativa) {
        erro = null
        tentar {
            DadosInicio(
                RepositorioAluno.meuPerfil().nome.trim().substringBefore(' '),
                RepositorioAluno.ocupacaoSetores(),
                RepositorioAluno.minhasExecucoes()
            )
        }
            .onSuccess { dados = it }
            .onFailure { erro = mensagemDeErro(it) }

        tentar { RepositorioAluno.minhasNotificacoes() }.onSuccess { avisos = it }
    }

    val vistoEm = ultimaAbertura
    val naoLidos = avisos.count { aviso ->
        val criado = runCatching { OffsetDateTime.parse(aviso.criadoEm).toInstant() }.getOrNull()
        criado != null && (vistoEm == null || criado.isAfter(vistoEm))
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(Fundo)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Cabecalho(naoLidos) {
            mostrarAvisos = true
            val agora = Instant.now()
            ultimaAbertura = agora
            salvarUltimaAbertura(contexto, agora)
        }

        val atual = dados
        if (erro != null) {
            EstadoErro(erro!!, onTentarNovamente = { tentativa++ })
            return@Column
        }
        if (atual == null) {
            EstadoCarregando()
            return@Column
        }

        Column {
            Text("Olá, ${atual.nome}! 👋", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text("Vamos para mais um treino hoje?", fontSize = 13.sp, color = TextoSecundario)
        }

        LotacaoCard(percentualLotacao(atual.setores))

        TituloSecao("ACESSO RÁPIDO")
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Atalho("Meu treino", Icons.Outlined.FitnessCenter, Modifier.weight(1f), onMeuTreino)
                Atalho("Academia", Icons.Outlined.LocationOn, Modifier.weight(1f), onAcademia)
                Atalho("Comunidade", Icons.Outlined.Groups, Modifier.weight(1f), onComunidade)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                AtalhoLinha("Chatbot", Icons.Outlined.ChatBubbleOutline, Modifier.weight(1f), onChatbot)
                AtalhoLinha("Sugestões", Icons.Outlined.ThumbUp, Modifier.weight(1f), onSugestoes)
            }
        }

        val semana = montarSemana(atual.execucoes)
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.weight(1f)) { TituloSecao("MINHA SEMANA") }
            Text("${semana.count { it.treinou }} / ${semana.size} Dias", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Laranja)
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            semana.forEach { DiaChip(it, Modifier.weight(1f)) }
        }

        val fuso = ZoneId.systemDefault()
        val mes = YearMonth.now(fuso)
        val treinosNoMes = atual.execucoes.count { dataLocal(it.inicio, fuso)?.let(YearMonth::from) == mes }
        TituloSecao("QUANT. TREINOS REALIZADOS:")
        Text(
            buildAnnotatedString {
                withStyle(SpanStyle(color = Laranja)) { append("$treinosNoMes") }
                append(" / ${mes.lengthOfMonth()} Mensal")
            },
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
    }

    if (mostrarAvisos) DialogoAvisos(avisos) { mostrarAvisos = false }
}

private fun dataLocal(iso: String, fuso: ZoneId): LocalDate? =
    runCatching { OffsetDateTime.parse(iso).atZoneSameInstant(fuso).toLocalDate() }.getOrNull()

private fun montarSemana(execucoes: List<ExecucaoAluno>): List<DiaSemana> {
    val fuso = ZoneId.systemDefault()
    val diasComTreino = execucoes.mapNotNull { dataLocal(it.inicio, fuso) }.toSet()
    val segunda = LocalDate.now(fuso).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    val siglas = listOf("Seg", "Ter", "Qua", "Qui", "Sex", "Sáb")
    return siglas.mapIndexed { i, sigla ->
        val dia = segunda.plusDays(i.toLong())
        DiaSemana(sigla, dia.dayOfMonth, dia in diasComTreino)
    }
}

private const val PREFS = "gyema_notificacoes"

private fun chaveAbertura(): String = "ultima_abertura_" + runCatching { usuarioLogadoId() }.getOrDefault("anonimo")

private fun lerUltimaAbertura(contexto: Context): Instant? =
    contexto.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        .getString(chaveAbertura(), null)
        ?.let { runCatching { Instant.parse(it) }.getOrNull() }

private fun salvarUltimaAbertura(contexto: Context, momento: Instant) {
    contexto.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        .edit().putString(chaveAbertura(), momento.toString()).apply()
}

@Composable
private fun DialogoAvisos(avisos: List<NotificacaoAluno>, onFechar: () -> Unit) {
    AlertDialog(
        onDismissRequest = onFechar,
        containerColor = Superficie,
        title = { Text("Notificações", fontWeight = FontWeight.Bold) },
        text = {
            if (avisos.isEmpty()) {
                Text("Você não tem notificações.", color = TextoSecundario, fontSize = 13.sp)
            } else {
                Column(Modifier.verticalScroll(rememberScrollState())) {
                    avisos.forEachIndexed { i, aviso ->
                        Column(Modifier.padding(vertical = 10.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(aviso.titulo, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(aviso.mensagem, fontSize = 12.sp, color = TextoSecundario)
                            Text(tempoRelativo(aviso.criadoEm), fontSize = 10.sp, color = TextoApagado)
                        }
                        if (i < avisos.lastIndex) HorizontalDivider(color = Borda)
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onFechar) { Text("Fechar", color = Laranja) } }
    )
}

@Composable
private fun Cabecalho(naoLidos: Int, onNotificacoes: () -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Row(Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
            // Troque por Image(painterResource(R.drawable.logo), ...) se tiver o logo
            Box(
                Modifier.size(28.dp).border(2.dp, Laranja, CircleShape),
                contentAlignment = Alignment.Center
            ) { Icon(Icons.Outlined.FitnessCenter, null, tint = Laranja, modifier = Modifier.size(14.dp)) }
            Spacer(Modifier.width(8.dp))
            Text("GYEMA", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
        }
        Box {
            CardGyema(shape = RoundedCornerShape(10.dp), onClick = onNotificacoes) {
                Icon(
                    Icons.Outlined.Notifications, "Notificações",
                    tint = TextoSecundario,
                    modifier = Modifier.padding(8.dp).size(18.dp)
                )
            }
            if (naoLidos > 0) {
                Box(
                    Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 4.dp, y = (-4).dp)
                        .size(16.dp)
                        .background(Laranja, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(if (naoLidos > 9) "9+" else "$naoLidos", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun LotacaoCard(percentual: Int) {
    CardGyema(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.Groups, null, tint = Laranja, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Lotação da Academia", fontSize = 13.sp, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
                Row(
                    Modifier
                        .clip(RoundedCornerShape(50))
                        .background(Verde.copy(alpha = 0.18f))
                        .padding(horizontal = 8.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(Modifier.size(6.dp).background(Verde, CircleShape))
                    Spacer(Modifier.width(4.dp))
                    Text("AO VIVO", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Verde)
                }
            }
            Row(verticalAlignment = Alignment.Bottom) {
                Text("$percentual%", fontSize = 26.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Text(textoLotacao(percentual), fontSize = 11.sp, color = TextoSecundario)
            }
            Box(
                Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(50)).background(Borda)
            ) {
                Box(Modifier.fillMaxWidth(percentual / 100f).fillMaxHeight().background(Laranja))
            }
        }
    }
}

@Composable
private fun Atalho(titulo: String, icone: ImageVector, modifier: Modifier, onClick: () -> Unit = {}) {
    CardGyema(modifier, onClick = onClick) {
        Column(
            Modifier.fillMaxWidth().padding(vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(Laranja.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) { Icon(icone, null, tint = Laranja, modifier = Modifier.size(20.dp)) }
            Text(titulo, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun AtalhoLinha(titulo: String, icone: ImageVector, modifier: Modifier, onClick: () -> Unit) {
    CardGyema(modifier, onClick = onClick) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icone, null, tint = Laranja, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text(titulo, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun DiaChip(dia: DiaSemana, modifier: Modifier) {
    val shape = RoundedCornerShape(10.dp)
    Column(
        modifier
            .clip(shape)
            .background(if (dia.treinou) Laranja else Superficie)
            .padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            dia.sigla, fontSize = 11.sp,
            fontWeight = if (dia.treinou) FontWeight.Bold else FontWeight.Normal,
            color = if (dia.treinou) Color.White else TextoSecundario
        )
        Text(dia.numero.toString(), fontSize = 15.sp, fontWeight = FontWeight.Bold)
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
private fun PreviewTelaPrincipal() {
    GyemaTheme { TelaPrincipal() }
}
