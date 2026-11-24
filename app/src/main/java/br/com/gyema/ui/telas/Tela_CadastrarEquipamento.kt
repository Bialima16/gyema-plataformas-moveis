package br.com.gyema.ui.telas

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.gyema.data.DadosEquipamento
import br.com.gyema.data.EquipamentoCompleto
import br.com.gyema.data.Exercicio
import br.com.gyema.data.RepositorioInstrutor
import br.com.gyema.data.Setor
import br.com.gyema.data.mensagemDeErro
import br.com.gyema.data.tentar
import br.com.gyema.ui.components.EstadoCarregando
import br.com.gyema.ui.components.EstadoErro
import br.com.gyema.ui.tema.GyemaTheme
import kotlinx.coroutines.launch

private class DadosFormulario(
    val setores: List<Setor>,
    val exercicios: List<Exercicio>,
    val equipamento: EquipamentoCompleto?
)

/**
 * Cadastro de equipamento. Com [equipamentoId] a tela abre em modo de edição,
 * já preenchida com os dados do banco.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaCadastrarEquipamento(
    equipamentoId: Int? = null,
    onBackClick: () -> Unit = {},
    onSaveClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val escopo = rememberCoroutineScope()
    val editando = equipamentoId != null

    var dados by remember { mutableStateOf<DadosFormulario?>(null) }
    var erroCarregar by remember { mutableStateOf<String?>(null) }
    var tentativa by remember { mutableIntStateOf(0) }

    var nomeEquipamento by remember { mutableStateOf("") }
    var identificacao by remember { mutableStateOf("") }
    var setorId by remember { mutableStateOf<Int?>(null) }
    var grupoMuscular by remember { mutableStateOf<String?>(null) }
    var capacidadeSimultanea by remember { mutableStateOf("1") }
    var tempoMaximo by remember { mutableStateOf("30") }
    var emManutencao by remember { mutableStateOf(false) }
    val principais = remember { mutableStateListOf<Exercicio>() }
    val alternativos = remember { mutableStateListOf<Exercicio>() }
    var instrucoesUso by remember { mutableStateOf("") }
    var videoLink by remember { mutableStateOf("") }

    var erroSalvar by remember { mutableStateOf<String?>(null) }
    var salvando by remember { mutableStateOf(false) }

    LaunchedEffect(tentativa) {
        erroCarregar = null
        tentar {
            DadosFormulario(
                setores = RepositorioInstrutor.listarSetores(),
                exercicios = RepositorioInstrutor.listarExercicios(),
                equipamento = equipamentoId?.let { RepositorioInstrutor.buscarEquipamento(it) }
            )
        }.onSuccess { d ->
            d.equipamento?.let { e ->
                nomeEquipamento = e.nome
                identificacao = e.codigo
                setorId = e.setorId
                grupoMuscular = e.grupoMuscular
                capacidadeSimultanea = e.capacidadeSimultanea.toString()
                tempoMaximo = e.tempoMaximoMin.toString()
                emManutencao = e.status == "manutencao"
                instrucoesUso = e.instrucoesUso.orEmpty()
                videoLink = e.videoUrl.orEmpty()
                principais.clear(); alternativos.clear()
                e.exercicios.forEach { v ->
                    if (v.tipo == "principal") principais.add(v.exercicios) else alternativos.add(v.exercicios)
                }
            }
            dados = d
        }.onFailure { erroCarregar = mensagemDeErro(it) }
    }

    fun salvar() {
        val capacidade = capacidadeSimultanea.trim().toIntOrNull()
        val tempo = tempoMaximo.trim().toIntOrNull()
        val video = videoLink.trim()
        erroSalvar = when {
            nomeEquipamento.isBlank() -> "Informe o nome do equipamento."
            identificacao.isBlank() -> "Informe a identificação (ex.: SUP-01)."
            setorId == null -> "Selecione o setor."
            capacidade == null || capacidade !in 1..20 -> "Capacidade simultânea deve ser de 1 a 20 pessoas."
            tempo == null || tempo !in 1..240 -> "Tempo máximo deve ser de 1 a 240 minutos."
            video.isNotEmpty() && !video.startsWith("http") -> "O link do vídeo deve começar com http:// ou https://."
            else -> null
        }
        if (erroSalvar != null) return

        val dadosEquipamento = DadosEquipamento(
            codigo = identificacao.trim().uppercase(),
            nome = nomeEquipamento.trim(),
            setorId = setorId!!,
            grupoMuscular = grupoMuscular,
            capacidadeSimultanea = capacidade!!,
            tempoMaximoMin = tempo!!,
            emManutencao = emManutencao,
            instrucoesUso = instrucoesUso.trim().ifEmpty { null },
            videoUrl = video.ifEmpty { null },
            principais = principais.map { it.id },
            alternativos = alternativos.map { it.id }
        )
        salvando = true
        escopo.launch {
            tentar {
                RepositorioInstrutor.salvarEquipamento(equipamentoId, dadosEquipamento, dados?.equipamento?.status)
            }.onSuccess {
                Toast.makeText(context, if (editando) "Equipamento atualizado" else "Equipamento cadastrado", Toast.LENGTH_SHORT).show()
                onSaveClick()
            }.onFailure { erroSalvar = mensagemDeErro(it) }
            salvando = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (editando) "Editar equipamento" else "Cadastrar equipamento",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0B1021)
                )
            )
        },
        containerColor = Color(0xFF0B1021)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val d = dados
            if (erroCarregar != null) {
                EstadoErro(erroCarregar!!, onTentarNovamente = { tentativa++ })
            } else if (d == null) {
                EstadoCarregando()
            } else {
                CampoFormEquipamento(
                    label = "Nome do equipamento",
                    value = nomeEquipamento,
                    onValueChange = { nomeEquipamento = it },
                    placeholder = "Ex: Supino Reto"
                )

                CampoFormEquipamento(
                    label = "Identificação (usada no QR Code)",
                    value = identificacao,
                    onValueChange = { identificacao = it },
                    placeholder = "Ex: SUP-01",
                    teclado = KeyboardOptions(capitalization = KeyboardCapitalization.Characters)
                )

                CampoDropdownEquipamento(
                    label = "Setor",
                    valor = d.setores.firstOrNull { it.id == setorId }?.nome,
                    textoVazio = "Selecione o setor",
                    opcoes = d.setores.map { it.nome },
                    onSelecionar = { i -> setorId = d.setores[i].id }
                )

                val grupos = remember(d) { d.exercicios.map { it.grupoMuscular }.distinct().sorted() }
                CampoDropdownEquipamento(
                    label = "Grupo muscular relacionado",
                    valor = grupoMuscular,
                    textoVazio = "Selecione o grupo muscular",
                    opcoes = listOf("Nenhum") + grupos,
                    onSelecionar = { i -> grupoMuscular = if (i == 0) null else grupos[i - 1] }
                )

                CampoFormEquipamento(
                    label = "Capacidade simultânea (pessoas)",
                    value = capacidadeSimultanea,
                    onValueChange = { capacidadeSimultanea = it.filter(Char::isDigit).take(2) },
                    placeholder = "Ex: 1",
                    teclado = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                CampoFormEquipamento(
                    label = "Tempo máximo de utilização (min)",
                    value = tempoMaximo,
                    onValueChange = { tempoMaximo = it.filter(Char::isDigit).take(3) },
                    placeholder = "Ex: 30",
                    teclado = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                // Estado de funcionamento
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Estado de funcionamento",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF131B31), shape = MaterialTheme.shapes.medium)
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        TabStatusEquipamento(
                            texto = "Disponível",
                            selecionado = !emManutencao,
                            modifier = Modifier.weight(1f),
                            onClick = { emManutencao = false }
                        )
                        TabStatusEquipamento(
                            texto = "Em manutenção",
                            selecionado = emManutencao,
                            modifier = Modifier.weight(1f),
                            onClick = { emManutencao = true }
                        )
                    }
                    if (editando && d.equipamento?.status == "ocupado" && !emManutencao) {
                        Text(
                            "Em uso agora. O status volta para disponível quando o aluno finalizar.",
                            color = Color(0xFF8B93A5),
                            fontSize = 12.sp
                        )
                    }
                }

                val escolhidos = (principais + alternativos).map { it.id }.toSet()
                SeletorExercicios(
                    label = "Exercícios principais",
                    placeholder = "Selecionar exercícios",
                    todos = d.exercicios,
                    selecionados = principais,
                    indisponiveis = escolhidos
                )
                SeletorExercicios(
                    label = "Exercícios alternativos",
                    placeholder = "Selecionar exercícios alternativos",
                    todos = d.exercicios,
                    selecionados = alternativos,
                    indisponiveis = escolhidos
                )

                Text(
                    text = "Instruções e conteúdos relacionados",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Instruções de uso",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    OutlinedTextField(
                        value = instrucoesUso,
                        onValueChange = { instrucoesUso = it },
                        placeholder = {
                            Text(
                                "Descreva a posição inicial, o movimento correto, pontos de atenção e o ajuste ideal para o equipamento.",
                                color = Color(0xFF8B93A5),
                                fontSize = 13.sp
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 110.dp),
                        shape = MaterialTheme.shapes.medium,
                        colors = estilocampos()
                    )
                }

                CampoFormEquipamento(
                    label = "Vídeo demonstrativo (link)",
                    value = videoLink,
                    onValueChange = { videoLink = it },
                    placeholder = "Ex: https://academia.com/supino-reto",
                    teclado = KeyboardOptions(keyboardType = KeyboardType.Uri)
                )

                erroSalvar?.let { Text(it, color = Color(0xFFEF5350), fontSize = 13.sp) }

                Button(
                    onClick = { salvar() },
                    enabled = !salvando,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE65100)
                    ),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(
                        text = if (salvando) "Salvando..." else "Salvar",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

/** Campo de busca que adiciona exercícios à lista [selecionados] (cada um só pode estar em uma lista). */
@Composable
private fun SeletorExercicios(
    label: String,
    placeholder: String,
    todos: List<Exercicio>,
    selecionados: MutableList<Exercicio>,
    indisponiveis: Set<Int>
) {
    var busca by remember { mutableStateOf("") }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = label,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
        OutlinedTextField(
            value = busca,
            onValueChange = { busca = it },
            placeholder = { Text(placeholder, color = Color(0xFF8B93A5)) },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF8B93A5))
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            colors = estilocampos()
        )

        val termo = busca.trim()
        if (termo.isNotEmpty()) {
            val sugestoes = todos.filter {
                it.id !in indisponiveis &&
                    (it.nome.contains(termo, true) || it.grupoMuscular.contains(termo, true))
            }.take(5)
            if (sugestoes.isEmpty()) {
                Text("Nenhum exercício encontrado.", color = Color(0xFF8B93A5), fontSize = 12.sp)
            }
            sugestoes.forEach { ex ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF131B31), MaterialTheme.shapes.small)
                        .clickable { selecionados.add(ex); busca = "" }
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(ex.nome, color = Color.White, fontSize = 14.sp)
                        Text(ex.grupoMuscular, color = Color(0xFF8B93A5), fontSize = 11.sp)
                    }
                    Icon(Icons.Default.Add, contentDescription = "Adicionar", tint = Color(0xFFE65100))
                }
            }
        }

        selecionados.toList().forEach { ex ->
            CardItemRemovivel(titulo = ex.nome, onDelete = { selecionados.remove(ex) })
        }
    }
}

@Composable
fun CampoFormEquipamento(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    teclado: KeyboardOptions = KeyboardOptions.Default
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = label,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = Color(0xFF8B93A5)) },
            singleLine = true,
            keyboardOptions = teclado,
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            colors = estilocampos()
        )
    }
}

@Composable
fun CampoDropdownEquipamento(
    label: String,
    valor: String?,
    textoVazio: String,
    opcoes: List<String>,
    onSelecionar: (indice: Int) -> Unit
) {
    var aberto by remember { mutableStateOf(false) }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = label,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
        Box {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF131B31), shape = MaterialTheme.shapes.medium)
                    .clickable { aberto = true }
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = valor ?: textoVazio,
                    color = if (valor == null) Color(0xFF8B93A5) else Color.White,
                    fontSize = 14.sp
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = Color(0xFF8B93A5)
                )
            }
            DropdownMenu(expanded = aberto, onDismissRequest = { aberto = false }) {
                opcoes.forEachIndexed { i, opcao ->
                    DropdownMenuItem(
                        text = { Text(opcao) },
                        onClick = { onSelecionar(i); aberto = false }
                    )
                }
            }
        }
    }
}

@Composable
fun TabStatusEquipamento(
    texto: String,
    selecionado: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val backgroundColor = if (selecionado) Color(0xFFE65100) else Color.Transparent
    val textColor = if (selecionado) Color.White else Color(0xFF8B93A5)

    Box(
        modifier = modifier
            .background(backgroundColor, shape = MaterialTheme.shapes.small)
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = texto,
            color = textColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun CardItemRemovivel(
    titulo: String,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF131B31)),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = titulo,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Remover",
                    tint = Color(0xFFE65100)
                )
            }
        }
    }
}

@Composable
fun estilocampos() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Color(0xFF1E2640),
    unfocusedBorderColor = Color(0xFF1E2640),
    focusedContainerColor = Color(0xFF131B31),
    unfocusedContainerColor = Color(0xFF131B31),
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White
)

@Preview(showBackground = true, backgroundColor = 0xFF0B1021)
@Composable
fun TelaCadastrarEquipamentoPreview() {
    GyemaTheme {
        TelaCadastrarEquipamento()
    }
}
