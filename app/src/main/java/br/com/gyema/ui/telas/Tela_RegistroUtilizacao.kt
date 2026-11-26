package br.com.gyema.ui.telas

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.QrCode2
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.gyema.data.EquipamentoAluno
import br.com.gyema.data.RepositorioAluno
import br.com.gyema.data.UtilizacaoAtiva
import br.com.gyema.data.mensagemErroAluno
import br.com.gyema.data.tentar
import br.com.gyema.ui.components.EstadoCarregando
import br.com.gyema.ui.components.EstadoErro
import br.com.gyema.ui.components.iconeEquipamento
import br.com.gyema.ui.components.statusAmbiente
import br.com.gyema.ui.tema.*
import kotlinx.coroutines.launch

@Composable
fun TelaRegistroUtilizacao(
    equipamentoId: Int? = null,
    onVoltar: () -> Unit = {},
    onUsoIniciado: () -> Unit = {},
    onReportarProblema: (equipamentoId: Int?) -> Unit = {}
) {
    var carregando by remember { mutableStateOf(true) }
    var erroCarga by remember { mutableStateOf<String?>(null) }
    var tentativa by remember { mutableIntStateOf(0) }
    var usoAtivo by remember { mutableStateOf<UtilizacaoAtiva?>(null) }
    var equipamento by remember { mutableStateOf<EquipamentoAluno?>(null) }
    var codigo by remember { mutableStateOf("") }
    var mensagem by remember { mutableStateOf<String?>(null) }
    var ocupado by remember { mutableStateOf(false) }
    val escopo = rememberCoroutineScope()

    LaunchedEffect(equipamentoId, tentativa) {
        carregando = true
        erroCarga = null
        tentar {
            usoAtivo = RepositorioAluno.minhaUtilizacaoAtiva()
            if (equipamentoId != null) equipamento = RepositorioAluno.buscarEquipamentoResumo(equipamentoId)
        }.onFailure { erroCarga = mensagemErroAluno(it) }
        carregando = false
    }

    fun buscar() {
        if (codigo.isBlank()) { mensagem = "Digite o código do equipamento."; return }
        ocupado = true
        mensagem = null
        escopo.launch {
            tentar { RepositorioAluno.buscarPorCodigo(codigo) }
                .onSuccess { achado ->
                    equipamento = achado
                    if (achado == null) mensagem = "Nenhum equipamento com o código ${codigo.trim().uppercase()}."
                }
                .onFailure { mensagem = mensagemErroAluno(it) }
            ocupado = false
        }
    }

    fun iniciar(eq: EquipamentoAluno) {
        ocupado = true
        mensagem = null
        escopo.launch {
            tentar { RepositorioAluno.iniciarUso(eq.id) }
                .onSuccess { onUsoIniciado() }
                .onFailure {
                    mensagem = mensagemErroAluno(it)
                    tentar { RepositorioAluno.buscarEquipamentoResumo(eq.id) }.onSuccess { novo -> equipamento = novo }
                }
            ocupado = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Fundo)
            .padding(16.dp)
    ) {
        // cabecalho
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar",
                modifier = Modifier.size(24.dp).clickable(onClick = onVoltar))
            Spacer(Modifier.width(12.dp))
            Text("Registro de utilização", fontSize = 20.sp, fontWeight = FontWeight.Bold,
                color = Color.White, modifier = Modifier.weight(1f))
        }

        Spacer(Modifier.height(20.dp))

        Column(
            Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val uso = usoAtivo
            val eq = equipamento
            when {
                erroCarga != null -> EstadoErro(erroCarga!!, onTentarNovamente = { tentativa++ })
                carregando -> EstadoCarregando()

                uso != null -> {
                    Aviso("Você já está usando ${uso.equipamentos.nome}. Finalize esse uso antes de iniciar outro.")
                    BotaoCheio("Ver minha utilização", Laranja, onClick = onUsoIniciado)
                }

                eq == null -> {
                    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            Modifier.size(96.dp).clip(CircleShape).background(Superficie),
                            contentAlignment = Alignment.Center
                        ) { Icon(Icons.Outlined.QrCode2, null, tint = Laranja, modifier = Modifier.size(48.dp)) }
                        Spacer(Modifier.height(12.dp))
                        Text("Digite o código que aparece no QR Code do equipamento.",
                            fontSize = 13.sp, color = TextoSecundario, textAlign = TextAlign.Center)
                    }
                    OutlinedTextField(
                        value = codigo,
                        onValueChange = { codigo = it },
                        label = { Text("Código (ex.: SUP-01)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters, imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = { buscar() }),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Laranja, unfocusedBorderColor = Borda,
                            focusedLabelColor = Laranja, cursorColor = Laranja
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    mensagem?.let { Text(it, color = Laranja, fontSize = 12.sp) }
                    BotaoCheio(if (ocupado) "Buscando..." else "Buscar equipamento", Laranja) { if (!ocupado) buscar() }
                }

                else -> {
                    CartaoEquipamento(eq)
                    mensagem?.let { Text(it, color = Laranja, fontSize = 13.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()) }
                    if (eq.status == "disponivel") {
                        BotaoCheio(if (ocupado) "Iniciando..." else "Iniciar uso", Laranja) { if (!ocupado) iniciar(eq) }
                    } else {
                        Aviso(
                            if (eq.status == "ocupado") "Equipamento ocupado no momento. Tente novamente em alguns minutos."
                            else "Equipamento em manutenção. Escolha outro por enquanto."
                        )
                    }
                    if (equipamentoId == null) {
                        Text("Usar outro código", color = TextoSecundario, fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth().clickable { equipamento = null; mensagem = null })
                    }
                }
            }
        }

        // botoes fixados na parte inferior
        BotaoCheio("Reportar problema", Color.Transparent, TextoSecundario, comBorda = true) { onReportarProblema(equipamento?.id) }
    }
}

@Composable
private fun CartaoEquipamento(eq: EquipamentoAluno) {
    val status = statusAmbiente(eq.status)
    Column(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(Superficie).padding(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(iconeEquipamento(eq.grupoMuscular), null, tint = Laranja, modifier = Modifier.size(40.dp))
        eq.grupoMuscular?.let {
            Box(
                Modifier.clip(RoundedCornerShape(6.dp)).background(LaranjaEscuro)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) { Text(it.uppercase(), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Laranja) }
        }
        Text(eq.nome, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Text("Setor de ${eq.setorNome} — ${eq.codigo}", fontSize = 12.sp, color = TextoSecundario)
        Text(status.rotulo, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = status.cor)
        Text("Você terá até ${eq.tempoMaximoMin} min de uso.", fontSize = 12.sp, color = TextoSecundario)
    }
}

@Composable
private fun Aviso(texto: String) {
    Text(
        texto, color = TextoSecundario, fontSize = 13.sp, textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Superficie).padding(16.dp)
    )
}

@Composable
private fun BotaoCheio(
    texto: String,
    fundo: Color,
    corTexto: Color = Color.White,
    comBorda: Boolean = false,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(12.dp)
    Box(
        Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(shape)
            .background(fundo)
            .then(if (comBorda) Modifier.border(1.dp, Borda, shape) else Modifier)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(texto, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = corTexto)
    }
}

// preview da tela
@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
private fun PreviewTelaRegistroUtilizacao() {
    GyemaTheme {
        TelaRegistroUtilizacao()
    }
}
