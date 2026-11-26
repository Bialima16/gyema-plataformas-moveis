package br.com.gyema.ui.telas

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.gyema.data.EquipamentoAluno
import br.com.gyema.data.RepositorioAluno
import br.com.gyema.data.mensagemErroAluno
import br.com.gyema.data.tentar
import br.com.gyema.ui.components.CardGyema
import br.com.gyema.ui.components.EstadoCarregando
import br.com.gyema.ui.components.EstadoErro
import br.com.gyema.ui.components.iconeEquipamento
import br.com.gyema.ui.components.statusAmbiente
import br.com.gyema.ui.tema.*
import kotlinx.coroutines.launch

@Composable
fun TelaReportarProblema(
    equipamentoId: Int? = null,
    onVoltar: () -> Unit = {},
    onEnviarRelato: () -> Unit = {}
) {
    var categoria by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }

    var equipamentos by remember { mutableStateOf<List<EquipamentoAluno>?>(null) }
    var selecionado by remember { mutableStateOf<EquipamentoAluno?>(null) }
    var erroCarga by remember { mutableStateOf<String?>(null) }
    var tentativa by remember { mutableIntStateOf(0) }
    var erroEnvio by remember { mutableStateOf<String?>(null) }
    var enviando by remember { mutableStateOf(false) }
    val escopo = rememberCoroutineScope()

    LaunchedEffect(tentativa) {
        erroCarga = null
        tentar { RepositorioAluno.listarEquipamentos() }
            .onSuccess { lista ->
                equipamentos = lista
                if (equipamentoId != null) selecionado = lista.firstOrNull { it.id == equipamentoId }
            }
            .onFailure { erroCarga = mensagemErroAluno(it) }
    }

    fun enviar() {
        val eq = selecionado
        erroEnvio = when {
            eq == null -> "Escolha o equipamento com problema."
            descricao.isBlank() -> "Descreva o problema."
            else -> null
        }
        if (erroEnvio != null || eq == null) return
        enviando = true
        escopo.launch {
            tentar { RepositorioAluno.enviarRelato(eq.id, categoria.ifEmpty { "Outros" }, descricao) }
                .onSuccess { onEnviarRelato() }
                .onFailure { erroEnvio = mensagemErroAluno(it) }
            enviando = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Fundo)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // topo da tela
            HeaderReportarProblema(onVoltar = onVoltar)

            val lista = equipamentos
            when {
                erroCarga != null -> EstadoErro(erroCarga!!, onTentarNovamente = { tentativa++ })
                lista == null -> EstadoCarregando()
                else -> {
                    CardEquipamentoSelecionado(
                        equipamento = selecionado,
                        opcoes = if (equipamentoId == null) lista else emptyList(),
                        onEscolher = { selecionado = it }
                    )

                    // campo de categoria
                    CampoCategoriaProblema(
                        categoriaSelecionada = categoria,
                        onCategoriaSelecionada = { categoria = it }
                    )

                    // campo de descricao
                    CampoDescricaoProblema(
                        descricao = descricao,
                        onDescricaoChange = { descricao = it }
                    )

                    erroEnvio?.let { Text(it, color = Laranja, fontSize = 12.sp) }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // clique para enviar relato
        BotaoEnviarRelato(texto = if (enviando) "Enviando..." else "Enviar relato") {
            if (!enviando && equipamentos != null) enviar()
        }
    }
}

// topo da tela
@Composable
private fun HeaderReportarProblema(
    onVoltar: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CardGyema(
            modifier = Modifier.size(36.dp),
            shape = RoundedCornerShape(10.dp),
            onClick = onVoltar
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Voltar",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = "Reportar problema",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "Detalhes do equipamento",
                fontSize = 12.sp,
                color = TextoSecundario
            )
        }
    }
}

// card do equipamento
@Composable
private fun CardEquipamentoSelecionado(
    equipamento: EquipamentoAluno?,
    opcoes: List<EquipamentoAluno>,
    onEscolher: (EquipamentoAluno) -> Unit
) {
    var expandido by remember { mutableStateOf(false) }
    val podeEscolher = opcoes.isNotEmpty()
    val status = equipamento?.let { statusAmbiente(it.status) }

    Box {
        CardGyema(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            onClick = if (podeEscolher) ({ expandido = true }) else null
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(SuperficieEscura),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = iconeEquipamento(equipamento?.grupoMuscular),
                        contentDescription = null,
                        tint = Verde,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "EQUIPAMENTO SELECIONADO",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Verde,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = equipamento?.nome ?: "Toque para escolher",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (equipamento == null) TextoSecundario else Color.White
                    )
                    equipamento?.let {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${it.codigo} • Área de ${it.setorNome.lowercase()}",
                            fontSize = 12.sp,
                            color = TextoSecundario
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                if (status != null) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(SuperficieEscura)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = status.rotulo.uppercase(),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = status.cor
                        )
                    }
                }
                if (podeEscolher) {
                    Icon(Icons.Outlined.KeyboardArrowDown, null, tint = TextoSecundario, modifier = Modifier.size(20.dp))
                }
            }
        }

        DropdownMenu(
            expanded = expandido,
            onDismissRequest = { expandido = false },
            modifier = Modifier.background(Superficie)
        ) {
            opcoes.forEach { item ->
                DropdownMenuItem(
                    text = { Text("${item.nome} (${item.codigo})", color = Color.White, fontSize = 13.sp) },
                    onClick = {
                        onEscolher(item)
                        expandido = false
                    }
                )
            }
        }
    }
}

// campo de categoria
@Composable
private fun CampoCategoriaProblema(
    categoriaSelecionada: String,
    onCategoriaSelecionada: (String) -> Unit
) {
    var expandido by remember { mutableStateOf(false) }
    val categorias = listOf("Estrutura/Banco solto", "Cabo de aço/Roldana", "Pesos/Anilhas", "Outros")

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Categoria do problema",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "Opcional",
                fontSize = 12.sp,
                color = TextoSecundario
            )
        }
        val shapeCategoria = RoundedCornerShape(12.dp)
        Box {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shapeCategoria)
                    .background(Superficie)
                    .border(1.dp, Borda, shapeCategoria)
                    .clickable { expandido = !expandido }
                    .padding(horizontal = 14.dp, vertical = 14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Build,
                        contentDescription = null,
                        tint = TextoSecundario,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = categoriaSelecionada.ifEmpty { "Selecione uma categoria" },
                        fontSize = 13.sp,
                        color = if (categoriaSelecionada.isEmpty()) TextoSecundario else Color.White,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.Outlined.KeyboardArrowDown,
                        contentDescription = null,
                        tint = TextoSecundario,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            DropdownMenu(
                expanded = expandido,
                onDismissRequest = { expandido = false },
                modifier = Modifier.background(Superficie)
            ) {
                categorias.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(text = item, color = Color.White, fontSize = 13.sp) },
                        onClick = {
                            onCategoriaSelecionada(item)
                            expandido = false
                        }
                    )
                }
            }
        }
    }
}

// campo de descricao
@Composable
private fun CampoDescricaoProblema(
    descricao: String,
    onDescricaoChange: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Descreva o problema",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "Obrigatório",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Laranja
            )
        }
        val shapeDescricao = RoundedCornerShape(12.dp)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .clip(shapeDescricao)
                .background(Superficie)
                .border(1.dp, Laranja.copy(alpha = 0.5f), shapeDescricao)
                .padding(12.dp)
        ) {
            BasicTextField(
                value = descricao,
                onValueChange = { if (it.length <= 500) onDescricaoChange(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(bottom = 20.dp),
                textStyle = TextStyle(
                    color = Color.White,
                    fontSize = 13.sp
                ),
                cursorBrush = SolidColor(Laranja),
                decorationBox = { innerTextField ->
                    if (descricao.isEmpty()) {
                        Text(
                            text = "Ex.: o banco está solto e faz barulho durante o movimento.",
                            color = TextoSecundario,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                    }
                    innerTextField()
                }
            )
            Text(
                text = "${descricao.length}/500",
                fontSize = 11.sp,
                color = TextoSecundario,
                modifier = Modifier.align(Alignment.BottomEnd)
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = null,
                tint = Verde,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = "Informe o que aconteceu para agilizar a avaliação.",
                fontSize = 11.sp,
                color = TextoSecundario
            )
        }
    }
}

// botao de envio
@Composable
private fun BotaoEnviarRelato(
    texto: String,
    onClick: () -> Unit
) {
    val shapeBotao = RoundedCornerShape(12.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(shapeBotao)
            .background(Laranja)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = texto,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

// preview da tela
@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
private fun PreviewTelaReportarProblema() {
    GyemaTheme {
        TelaReportarProblema()
    }
}
