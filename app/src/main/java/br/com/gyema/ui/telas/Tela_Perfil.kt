package br.com.gyema.ui.telas

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.gyema.data.CargaMensalAluno
import br.com.gyema.data.EdicaoPerfilAluno
import br.com.gyema.data.ExecucaoAluno
import br.com.gyema.data.PerfilAluno
import br.com.gyema.data.RepositorioAluno
import br.com.gyema.data.mensagemDeErro
import br.com.gyema.data.tentar
import br.com.gyema.ui.components.CardGyema
import br.com.gyema.ui.components.EstadoCarregando
import br.com.gyema.ui.components.EstadoErro
import br.com.gyema.ui.components.formatarData
import br.com.gyema.ui.components.iniciais
import br.com.gyema.ui.components.numeroBr
import br.com.gyema.ui.components.contagem
import br.com.gyema.ui.tema.*
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private data class ItemMenu(val titulo: String, val icone: ImageVector, val destaque: Boolean = false)

private val itensMenu = listOf(
    ItemMenu("Editar perfil", Icons.Outlined.Edit),
    ItemMenu("Notificações", Icons.Outlined.Notifications),
    ItemMenu("Privacidade e segurança", Icons.Outlined.Shield),
    ItemMenu("Acessibilidade", Icons.Outlined.Visibility),
    ItemMenu("Ajuda e suporte", Icons.AutoMirrored.Outlined.HelpOutline),
    ItemMenu("Sair da conta", Icons.AutoMirrored.Outlined.Logout, destaque = true)
)

private data class DadosPerfil(
    val perfil: PerfilAluno,
    val execucoes: List<ExecucaoAluno>,
    val cargas: List<CargaMensalAluno>
)

@Composable
fun TelaPerfil(onSair: () -> Unit = {}, onSuporte: () -> Unit = {}, onNotificacoes: () -> Unit = {}) {
    var abaSelecionada by remember { mutableIntStateOf(0) }
    var dados by remember { mutableStateOf<DadosPerfil?>(null) }
    var erro by remember { mutableStateOf<String?>(null) }
    var tentativa by remember { mutableIntStateOf(0) }
    var editando by remember { mutableStateOf(false) }

    LaunchedEffect(tentativa) {
        erro = null
        tentar {
            DadosPerfil(
                RepositorioAluno.meuPerfil(),
                RepositorioAluno.minhasExecucoes(),
                RepositorioAluno.minhasCargasMensais()
            )
        }
            .onSuccess { dados = it }
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
        Text("Meu Perfil", fontSize = 18.sp, fontWeight = FontWeight.Bold)

        val atual = dados
        when {
            erro != null -> EstadoErro(erro!!, onTentarNovamente = { tentativa++ })
            atual == null -> EstadoCarregando()
            else -> {
                val perfil = atual.perfil
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier.size(64.dp).border(2.dp, Laranja, CircleShape),
                        contentAlignment = Alignment.Center
                    ) { Text(iniciais(perfil.nome), fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Laranja) }
                    Spacer(Modifier.width(14.dp))
                    Column {
                        Text(perfil.nome, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text(
                            perfil.curso?.let { "$it • Unifor" } ?: perfil.email,
                            fontSize = 12.sp, color = TextoSecundario
                        )
                    }
                }

                CardGyema(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        // Abas
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(SuperficieEscura)
                                .padding(4.dp)
                        ) {
                            listOf("Resumo", "Treinos", "Cargas").forEachIndexed { i, nome ->
                                val ativa = i == abaSelecionada
                                Box(
                                    Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (ativa) Laranja else Color.Transparent)
                                        .clickable { abaSelecionada = i }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        nome, fontSize = 12.sp,
                                        fontWeight = if (ativa) FontWeight.Bold else FontWeight.Medium,
                                        color = if (ativa) Color.White else TextoSecundario
                                    )
                                }
                            }
                        }
                        val (a, b) = estatisticas(abaSelecionada, atual)
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Estatistica(a.first, a.second, Modifier.weight(1f))
                            Estatistica(b.first, b.second, Modifier.weight(1f))
                        }
                    }
                }

                CardGyema(Modifier.fillMaxWidth()) {
                    Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.MonitorHeart, null, tint = Laranja, modifier = Modifier.size(22.dp))
                        Spacer(Modifier.width(10.dp))
                        Column(Modifier.weight(1f)) {
                            Text("Medidas corporais", fontSize = 12.sp, color = TextoSecundario)
                            Text(
                                "Peso: ${perfil.pesoKg?.let { "${numeroBr(it)}kg" } ?: "—"} | " +
                                    "Altura: ${perfil.alturaCm?.let { "${numeroBr(it / 100, 2)}m" } ?: "—"}",
                                fontSize = 13.sp, fontWeight = FontWeight.Bold
                            )
                        }
                        Box(
                            Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Borda)
                                .clickable { editando = true }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) { Text("Editar", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                    }
                }

                if (editando) {
                    DialogoEditarPerfil(
                        perfil = perfil,
                        onFechar = { editando = false },
                        onSalvo = { novo ->
                            dados = atual.copy(perfil = novo)
                            editando = false
                        }
                    )
                }
            }
        }

        CardGyema(Modifier.fillMaxWidth()) {
            Column {
                itensMenu.forEachIndexed { i, item ->
                    val cor = if (item.destaque) Laranja else Color.White
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clickable {
                                when (item.titulo) {
                                    "Editar perfil" -> if (dados != null) editando = true
                                    "Sair da conta" -> onSair()
                                    "Ajuda e suporte", "Acessibilidade" -> onSuporte()
                                    "Notificações" -> onNotificacoes()
                                }
                            }
                            .padding(horizontal = 16.dp, vertical = 15.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(item.icone, null, tint = if (item.destaque) Laranja else TextoSecundario, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(14.dp))
                        Text(item.titulo, fontSize = 14.sp, color = cor, modifier = Modifier.weight(1f))
                        Icon(Icons.Outlined.ChevronRight, null, tint = TextoSecundario, modifier = Modifier.size(20.dp))
                    }
                    if (i < itensMenu.lastIndex) HorizontalDivider(color = Borda)
                }
            }
        }
    }
}

private fun estatisticas(aba: Int, dados: DadosPerfil): Pair<Pair<String, String>, Pair<String, String>> {
    val fuso = ZoneId.systemDefault()
    val mesAtual = YearMonth.now(fuso)
    val doMes = dados.execucoes.filter {
        runCatching { YearMonth.from(OffsetDateTime.parse(it.inicio).atZoneSameInstant(fuso)) }.getOrNull() == mesAtual
    }
    return when (aba) {
        0 -> {
            val minutos = doMes.sumOf { duracaoMin(it) }
            ("Frequência" to "${contagem(doMes.size, "treino", "treinos")} /mês") to
                ("Tempo total" to "${minutos / 60}h${"%02d".format(minutos % 60)} treinadas")
        }
        1 -> {
            val ultimo = dados.execucoes.firstOrNull()?.let { formatarData(it.inicio, "dd/MM/yyyy") }
            ("Total" to contagem(dados.execucoes.size, "treino", "treinos")) to ("Último treino" to (ultimo ?: "—"))
        }
        else -> {
            val ultimoMes = dados.cargas.firstOrNull()
            ("Carga máxima" to (ultimoMes?.cargaMaxKg?.let { "${numeroBr(it)} kg" } ?: "—")) to
                ("Volume no mês" to (ultimoMes?.volumeKg?.let { "${numeroBr(it, 0)} kg" } ?: "—"))
        }
    }
}

private fun duracaoMin(e: ExecucaoAluno): Long {
    val fim = e.fim ?: return 0
    return runCatching { Duration.between(OffsetDateTime.parse(e.inicio), OffsetDateTime.parse(fim)).toMinutes() }
        .getOrDefault(0)
}

private val formatoBanco = DateTimeFormatter.ISO_LOCAL_DATE
private val formatoTela = DateTimeFormatter.ofPattern("dd/MM/yyyy")

@Composable
private fun DialogoEditarPerfil(perfil: PerfilAluno, onFechar: () -> Unit, onSalvo: (PerfilAluno) -> Unit) {
    var nome by remember { mutableStateOf(perfil.nome) }
    var matricula by remember { mutableStateOf(perfil.matricula.orEmpty()) }
    var cpf by remember { mutableStateOf(perfil.cpf.orEmpty()) }
    var curso by remember { mutableStateOf(perfil.curso.orEmpty()) }
    var nascimento by remember {
        mutableStateOf(perfil.dataNascimento?.let { runCatching { LocalDate.parse(it).format(formatoTela) }.getOrNull() }.orEmpty())
    }
    var genero by remember { mutableStateOf(perfil.genero.orEmpty()) }
    var peso by remember { mutableStateOf(perfil.pesoKg?.let { numeroBr(it) }.orEmpty()) }
    var altura by remember { mutableStateOf(perfil.alturaCm?.let { numeroBr(it, 0) }.orEmpty()) }
    var contato by remember { mutableStateOf(perfil.contatoEmergencia.orEmpty()) }

    var erro by remember { mutableStateOf<String?>(null) }
    var salvando by remember { mutableStateOf(false) }
    val escopo = rememberCoroutineScope()

    fun salvar() {
        val pesoNum = peso.replace(',', '.').toDoubleOrNull()
        val alturaNum = altura.replace(',', '.').toDoubleOrNull()
        val dataBanco = nascimento.takeIf { it.isNotBlank() }?.let {
            runCatching { LocalDate.parse(it.trim(), formatoTela).format(formatoBanco) }.getOrNull()
        }
        erro = when {
            nome.isBlank() -> "Informe o nome."
            nascimento.isNotBlank() && dataBanco == null -> "Data de nascimento inválida. Use DD/MM/AAAA."
            peso.isNotBlank() && (pesoNum == null || pesoNum !in 20.0..400.0) -> "O peso deve ficar entre 20 e 400 kg."
            altura.isNotBlank() && (alturaNum == null || alturaNum !in 80.0..260.0) -> "A altura deve ficar entre 80 e 260 cm."
            else -> null
        }
        if (erro != null) return

        val edicao = EdicaoPerfilAluno(
            nome = nome.trim(),
            matricula = matricula.trim().ifBlank { null },
            cpf = cpf.trim().ifBlank { null },
            curso = curso.trim().ifBlank { null },
            dataNascimento = dataBanco,
            genero = genero.trim().ifBlank { null },
            pesoKg = pesoNum,
            alturaCm = alturaNum,
            contatoEmergencia = contato.trim().ifBlank { null }
        )
        salvando = true
        escopo.launch {
            tentar { RepositorioAluno.salvarPerfil(edicao) }
                .onSuccess { onSalvo(it) }
                .onFailure { erro = mensagemDeErro(it) }
            salvando = false
        }
    }

    AlertDialog(
        onDismissRequest = { if (!salvando) onFechar() },
        containerColor = Superficie,
        title = { Text("Editar perfil", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                erro?.let { Text(it, color = Laranja, fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                Campo("Nome", nome) { nome = it }
                Campo("Matrícula", matricula) { matricula = it }
                Campo("CPF", cpf, KeyboardType.Number) { cpf = it }
                Campo("Curso", curso) { curso = it }
                Campo("Data de nascimento (DD/MM/AAAA)", nascimento, KeyboardType.Number) { nascimento = it }
                Campo("Gênero", genero) { genero = it }
                Campo("Peso (kg)", peso, KeyboardType.Decimal) { peso = it }
                Campo("Altura (cm)", altura, KeyboardType.Decimal) { altura = it }
                Campo("Contato de emergência", contato, KeyboardType.Phone) { contato = it }
                Text("E-mail: ${perfil.email} (não pode ser alterado)", fontSize = 11.sp, color = TextoSecundario)
            }
        },
        confirmButton = {
            TextButton(onClick = ::salvar, enabled = !salvando) {
                Text(if (salvando) "Salvando..." else "Salvar", color = Laranja, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onFechar, enabled = !salvando) { Text("Cancelar", color = TextoSecundario) }
        }
    )
}

@Composable
private fun Campo(rotulo: String, valor: String, teclado: KeyboardType = KeyboardType.Text, onMudar: (String) -> Unit) {
    OutlinedTextField(
        value = valor,
        onValueChange = onMudar,
        label = { Text(rotulo, fontSize = 12.sp) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = teclado),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Laranja, unfocusedBorderColor = Borda,
            focusedLabelColor = Laranja, cursorColor = Laranja
        ),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun Estatistica(rotulo: String, valor: String, modifier: Modifier) {
    Column(
        modifier
            .clip(RoundedCornerShape(12.dp))
            .background(SuperficieEscura)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(rotulo, fontSize = 11.sp, color = TextoSecundario)
        Text(valor, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
private fun PreviewTelaPerfil() {
    GyemaTheme { TelaPerfil() }
}
