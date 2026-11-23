package br.com.gyema.ui.telas

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.gyema.data.EquipamentoLista
import br.com.gyema.data.RepositorioInstrutor
import br.com.gyema.data.mensagemDeErro
import br.com.gyema.data.tentar
import br.com.gyema.ui.components.EstadoErro
import br.com.gyema.ui.components.EstadoVazio
import br.com.gyema.ui.tema.GyemaTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaGerenciarEquipamentos(
    onBackClick: () -> Unit = {},
    onCadastrarClick: () -> Unit = {},
    onEquipamentoClick: (equipamentoId: Int) -> Unit = {}
) {
    var abaSelecionada by remember { mutableIntStateOf(0) } // 0: Todos, 1: Manutenção
    var equipamentos by remember { mutableStateOf<List<EquipamentoLista>?>(null) }
    var erro by remember { mutableStateOf<String?>(null) }
    var tentativa by remember { mutableIntStateOf(0) }

    // Recarrega ao abrir e ao voltar do cadastro/edição
    LaunchedEffect(tentativa) {
        erro = null
        tentar { RepositorioInstrutor.listarEquipamentos() }
            .onSuccess { equipamentos = it }
            .onFailure { erro = mensagemDeErro(it) }
    }

    val lista = equipamentos
    val visiveis = when {
        lista == null -> emptyList()
        abaSelecionada == 1 -> lista.filter { it.status == "manutencao" }
        else -> lista
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Equipamentos",
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
        containerColor = Color(0xFF0B1021),
        bottomBar = {
            Box(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                Button(
                    onClick = onCadastrarClick,
                    enabled = erro == null && lista != null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE65100),
                        disabledContainerColor = Color(0xFF131B31)
                    ),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(
                        text = "Cadastrar equipamento +",
                        color = if (erro == null && lista != null) Color.White else Color(0xFF8B93A5),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Abas: Todos / Manutenção
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF131B31), shape = MaterialTheme.shapes.medium)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                TabBotao(
                    texto = "Todos",
                    selecionado = abaSelecionada == 0,
                    modifier = Modifier.weight(1f),
                    onClick = { abaSelecionada = 0 }
                )
                TabBotao(
                    texto = "Manutenção",
                    selecionado = abaSelecionada == 1,
                    modifier = Modifier.weight(1f),
                    onClick = { abaSelecionada = 1 }
                )
            }

            when {
                erro != null -> EstadoErro(
                    erro!!,
                    onTentarNovamente = { equipamentos = null; tentativa++ }
                )
                lista == null -> repeat(3) { CardSkeletonEquipamento() }
                visiveis.isEmpty() -> EstadoVazio(
                    titulo = if (abaSelecionada == 1) "Nenhum equipamento em manutenção"
                    else "Nenhum equipamento cadastrado",
                    texto = if (abaSelecionada == 1) "Todos os equipamentos estão funcionando."
                    else "Toque em \"Cadastrar equipamento\" para adicionar o primeiro."
                )
                else -> visiveis.forEach { eq ->
                    val (texto, cor) = rotuloStatus(eq.status)
                    CardEquipamento(
                        nome = eq.nome,
                        setor = "${eq.setorNome} • ${eq.codigo}",
                        status = texto,
                        statusColor = cor,
                        onClick = { onEquipamentoClick(eq.id) }
                    )
                }
            }
        }
    }
}

private fun rotuloStatus(status: String): Pair<String, Color> = when (status) {
    "disponivel" -> "DISPONÍVEL" to Color(0xFF4CAF50)
    "ocupado" -> "OCUPADO" to Color(0xFFE65100)
    else -> "MANUTENÇÃO" to Color(0xFF8B93A5)
}

@Composable
fun TabBotao(
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardEquipamento(
    nome: String,
    setor: String,
    status: String,
    statusColor: Color,
    onClick: () -> Unit = {}
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF131B31)),
        shape = MaterialTheme.shapes.large
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = nome,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = setor,
                    color = Color(0xFF8B93A5),
                    fontSize = 13.sp
                )
            }
            Spacer(Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .background(statusColor.copy(alpha = 0.2f), shape = MaterialTheme.shapes.small)
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = status,
                    color = statusColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun CardSkeletonEquipamento() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF131B31)),
        shape = MaterialTheme.shapes.large
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier
                        .width(140.dp)
                        .height(16.dp)
                        .background(Color(0xFF1E2640), shape = MaterialTheme.shapes.small)
                )
                Box(
                    modifier = Modifier
                        .width(80.dp)
                        .height(12.dp)
                        .background(Color(0xFF1E2640), shape = MaterialTheme.shapes.small)
                )
            }
            Box(
                modifier = Modifier
                    .width(70.dp)
                    .height(24.dp)
                    .background(Color(0xFF1E2640), shape = MaterialTheme.shapes.small)
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0B1021)
@Composable
fun TelaGerenciarEquipamentosPreview() {
    GyemaTheme {
        TelaGerenciarEquipamentos()
    }
}
