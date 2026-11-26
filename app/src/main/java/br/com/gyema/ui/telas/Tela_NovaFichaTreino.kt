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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.gyema.data.Aluno
import br.com.gyema.data.Exercicio
import br.com.gyema.data.ItemNovaFicha
import br.com.gyema.data.RepositorioInstrutor
import br.com.gyema.data.mensagemDeErro
import br.com.gyema.data.tentar
import br.com.gyema.ui.components.EstadoCarregando
import br.com.gyema.ui.components.EstadoErro
import br.com.gyema.ui.tema.GyemaTheme
import kotlinx.coroutines.launch

private val CorFundo = Color(0xFF0B1021)
private val CorCampo = Color(0xFF131B31)
private val CorBorda = Color(0xFF1E2640)
private val CorSecundaria = Color(0xFF8B93A5)
private val CorBotao = Color(0xFFE65100)
private val CorErro = Color(0xFFEF5350)

/** Um exercício da ficha enquanto o instrutor edita (os números ficam como texto até salvar). */
private class ItemEditavel(val exercicio: Exercicio) {
    var series by mutableStateOf("3")
    var repeticoes by mutableStateOf("10")
    var carga by mutableStateOf("")
    var descanso by mutableStateOf("60")
    var observacoes by mutableStateOf("")
    var tecnica by mutableStateOf("")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaNovaFichaTreino(
    alunoIdInicial: String? = null,
    fichaId: Long? = null,
    onBackClick: () -> Unit = {},
    onSaveClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val escopo = rememberCoroutineScope()

    var alunos by remember { mutableStateOf<List<Aluno>?>(null) }
    var exercicios by remember { mutableStateOf<List<Exercicio>>(emptyList()) }
    var erroCarregar by remember { mutableStateOf<String?>(null) }
    var tentativa by remember { mutableIntStateOf(0) }

    var alunoId by remember { mutableStateOf(alunoIdInicial) }
    var nomeFicha by remember { mutableStateOf("") }
    var pesquisaExercicio by remember { mutableStateOf("") }
    val itens = remember { mutableStateListOf<ItemEditavel>() }

    var erroSalvar by remember { mutableStateOf<String?>(null) }
    var salvando by remember { mutableStateOf(false) }

    LaunchedEffect(tentativa) {
        erroCarregar = null
        tentar {
            val a = RepositorioInstrutor.listarAlunos()
            val e = RepositorioInstrutor.listarExercicios()
            // Edição: carrega a ficha e preenche aluno, nome e exercícios.
            if (fichaId != null && itens.isEmpty()) {
                val ficha = RepositorioInstrutor.buscarFichaEdicao(fichaId)
                alunoId = ficha.alunoId
                nomeFicha = ficha.nome
                ficha.itens.forEach { item ->
                    itens.add(ItemEditavel(item.exercicios).apply {
                        series = item.series.toString()
                        repeticoes = item.repeticoes.toString()
                        carga = item.cargaKg?.let { c -> if (c % 1.0 == 0.0) c.toInt().toString() else c.toString().replace('.', ',') }.orEmpty()
                        descanso = item.descansoSeg?.toString().orEmpty()
                        observacoes = item.observacoes.orEmpty()
                        tecnica = item.tecnica.orEmpty()
                    })
                }
            }
            a to e
        }
            .onSuccess { (a, e) -> alunos = a; exercicios = e }
            .onFailure { erroCarregar = mensagemDeErro(it) }
    }

    fun salvar() {
        val erro = validar(alunoId, nomeFicha, itens)
        erroSalvar = erro
        if (erro != null) return
        salvando = true
        escopo.launch {
            tentar {
                if (fichaId != null) RepositorioInstrutor.atualizarFicha(fichaId, nomeFicha.trim(), itens.map { it.paraItem() })
                else RepositorioInstrutor.salvarFicha(alunoId!!, nomeFicha.trim(), itens.map { it.paraItem() })
            }.onSuccess {
                Toast.makeText(context, "Ficha salva", Toast.LENGTH_SHORT).show()
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
                        text = if (fichaId != null) "Editar ficha de treino" else "Nova ficha de treino",
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CorFundo)
            )
        },
        containerColor = CorFundo
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val listaAlunos = alunos
            if (erroCarregar != null) {
                EstadoErro(erroCarregar!!, onTentarNovamente = { tentativa++ })
            } else if (listaAlunos == null) {
                EstadoCarregando()
            } else {
                // Aluno
                SeletorAluno(
                    alunos = listaAlunos,
                    selecionadoId = alunoId,
                    // Na edição o aluno não muda (a ficha é dele).
                    onSelecionar = { if (fichaId == null) { alunoId = it; erroSalvar = null } }
                )

                // Nome da ficha
                CampoTextoPersonalizado(
                    label = "Nome da ficha",
                    value = nomeFicha,
                    placeholder = "Ex: Treino A — Peito e Tríceps",
                    onValueChange = { nomeFicha = it; erroSalvar = null }
                )

                // Adicionar exercícios
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Adicionar exercícios",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    OutlinedTextField(
                        value = pesquisaExercicio,
                        onValueChange = { pesquisaExercicio = it },
                        placeholder = { Text("Buscar por nome ou grupo muscular", color = CorSecundaria) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = CorSecundaria) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        colors = coresCampo(CorCampo)
                    )

                    val termo = pesquisaExercicio.trim()
                    if (termo.isNotEmpty()) {
                        val usados = itens.map { it.exercicio.id }.toSet()
                        val sugestoes = exercicios.filter {
                            it.id !in usados && (it.nome.contains(termo, true) || it.grupoMuscular.contains(termo, true))
                        }.take(6)
                        if (sugestoes.isEmpty()) {
                            Text("Nenhum exercício encontrado.", color = CorSecundaria, fontSize = 12.sp)
                        }
                        sugestoes.forEach { ex ->
                            SugestaoExercicio(ex) {
                                itens.add(ItemEditavel(ex))
                                pesquisaExercicio = ""
                                erroSalvar = null
                            }
                        }
                    }
                }

                if (itens.isEmpty()) {
                    Text(
                        "Nenhum exercício adicionado ainda.",
                        color = CorSecundaria,
                        fontSize = 13.sp
                    )
                }

                itens.forEach { item ->
                    CardExercicioTreino(item = item, onDelete = { itens.remove(item) })
                }

                erroSalvar?.let { Text(it, color = CorErro, fontSize = 13.sp) }

                Button(
                    onClick = { salvar() },
                    enabled = !salvando,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CorBotao),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(
                        text = if (salvando) "Salvando..." else "Salvar ficha",
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

/** Retorna a mensagem do primeiro problema encontrado, ou null se está tudo certo. */
private fun validar(alunoId: String?, nome: String, itens: List<ItemEditavel>): String? {
    if (alunoId == null) return "Selecione o aluno."
    if (nome.isBlank()) return "Informe o nome da ficha."
    if (itens.isEmpty()) return "Adicione pelo menos um exercício."
    itens.forEach {
        val n = it.exercicio.nome
        if ((it.series.toIntOrNull() ?: 0) !in 1..20) return "$n: séries deve ser um número de 1 a 20."
        if ((it.repeticoes.toIntOrNull() ?: 0) !in 1..100) return "$n: repetições deve ser um número de 1 a 100."
        if (it.carga.isNotBlank() && numero(it.carga).let { c -> c == null || c < 0 || c > 999 })
            return "$n: carga inválida."
        if (it.descanso.isNotBlank() && (it.descanso.toIntOrNull() ?: -1) !in 0..600)
            return "$n: descanso deve ser de 0 a 600 segundos."
    }
    return null
}

private fun numero(texto: String): Double? = texto.trim().replace(',', '.').toDoubleOrNull()

private fun ItemEditavel.paraItem() = ItemNovaFicha(
    exercicio = exercicio,
    series = series.toInt(),
    repeticoes = repeticoes.toInt(),
    cargaKg = carga.takeIf { it.isNotBlank() }?.let(::numero),
    descansoSeg = descanso.trim().toIntOrNull(),
    observacoes = observacoes.trim().ifEmpty { null },
    tecnica = tecnica.trim().ifEmpty { null }
)

@Composable
private fun SeletorAluno(alunos: List<Aluno>, selecionadoId: String?, onSelecionar: (String) -> Unit) {
    var aberto by remember { mutableStateOf(false) }
    val selecionado = alunos.firstOrNull { it.id == selecionadoId }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Aluno", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        Box {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CorCampo, MaterialTheme.shapes.medium)
                    .clickable(enabled = alunos.isNotEmpty()) { aberto = true }
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selecionado?.nome
                        ?: if (alunos.isEmpty()) "Nenhum aluno cadastrado" else "Selecione o aluno",
                    color = if (selecionado != null) Color.White else CorSecundaria,
                    fontSize = 14.sp,
                    modifier = Modifier.weight(1f)
                )
                Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = CorSecundaria)
            }
            DropdownMenu(
                expanded = aberto,
                onDismissRequest = { aberto = false },
                modifier = Modifier.heightIn(max = 320.dp)
            ) {
                alunos.forEach { aluno ->
                    DropdownMenuItem(
                        text = {
                            Column {
                                Text(aluno.nome)
                                aluno.curso?.let { Text(it, fontSize = 11.sp, color = CorSecundaria) }
                            }
                        },
                        onClick = { onSelecionar(aluno.id); aberto = false }
                    )
                }
            }
        }
    }
}

@Composable
private fun SugestaoExercicio(exercicio: Exercicio, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(CorCampo, MaterialTheme.shapes.small)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(exercicio.nome, color = Color.White, fontSize = 14.sp)
            Text(exercicio.grupoMuscular, color = CorSecundaria, fontSize = 11.sp)
        }
        Icon(Icons.Default.Add, contentDescription = "Adicionar", tint = CorBotao)
    }
}

@Composable
fun CampoTextoPersonalizado(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = ""
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
            placeholder = { Text(placeholder, color = CorSecundaria) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            colors = coresCampo(CorCampo)
        )
    }
}

@Composable
private fun CardExercicioTreino(item: ItemEditavel, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CorCampo),
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        text = item.exercicio.nome,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(item.exercicio.grupoMuscular, color = CorSecundaria, fontSize = 12.sp)
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Remover exercício",
                        tint = CorErro
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ItemMetrica("Séries", item.series, { item.series = it }, KeyboardType.Number, Modifier.weight(1f))
                ItemMetrica("Reps", item.repeticoes, { item.repeticoes = it }, KeyboardType.Number, Modifier.weight(1f))
                ItemMetrica("Carga (kg)", item.carga, { item.carga = it }, KeyboardType.Decimal, Modifier.weight(1f))
                ItemMetrica("Desc. (s)", item.descanso, { item.descanso = it }, KeyboardType.Number, Modifier.weight(1f))
            }

            CampoTextoLongo("Observações técnicas", item.observacoes) { item.observacoes = it }
            CampoTextoLongo("Técnicas especiais", item.tecnica) { item.tecnica = it }
        }
    }
}

@Composable
private fun CampoTextoLongo(label: String, value: String, onValueChange: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(text = label, color = CorSecundaria, fontSize = 12.sp)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.small,
            colors = coresCampo(CorFundo)
        )
    }
}

@Composable
private fun ItemMetrica(
    label: String,
    valor: String,
    onValorChange: (String) -> Unit,
    teclado: KeyboardType,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(text = label, color = CorSecundaria, fontSize = 11.sp, maxLines = 1)
        OutlinedTextField(
            value = valor,
            onValueChange = { novo -> onValorChange(novo.filter { it.isDigit() || it == ',' || it == '.' }.take(6)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = teclado),
            textStyle = LocalTextStyle.current.copy(fontWeight = FontWeight.Bold, fontSize = 14.sp),
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.small,
            colors = coresCampo(CorFundo)
        )
    }
}

@Composable
private fun coresCampo(fundo: Color) = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = CorBorda,
    unfocusedBorderColor = CorBorda,
    focusedContainerColor = fundo,
    unfocusedContainerColor = fundo,
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White
)

@Preview(showBackground = true, backgroundColor = 0xFF0B1021)
@Composable
fun TelaNovaFichaTreinoPreview() {
    GyemaTheme {
        TelaNovaFichaTreino()
    }
}
