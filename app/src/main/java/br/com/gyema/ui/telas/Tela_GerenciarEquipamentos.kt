package br.com.gyema.ui.telas

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.gyema.ui.tema.GyemaTheme
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaGerenciarEquipamentos(
    isLoadingInicial: Boolean = false,
    onBackClick: () -> Unit = {},
    onCadastrarClick: () -> Unit = {}
) {
    var abaSelecionada by remember { mutableStateOf(0) } // 0: Todos, 1: Manutenção

    // Estado interno de carregamento que começa com o valor recebido
    var isLoading by remember { mutableStateOf(isLoadingInicial) }

    val scrollState = rememberScrollState()

    // Se entrou com o carregamento ativo, simulamos a espera de 2 segundos e destravamos
    LaunchedEffect(Unit) {
        if (isLoadingInicial) {
            delay(2000) // Simula 2 segundos a ir buscar os dados
            isLoading = false
        }
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
                            imageVector = Icons.Default.ArrowBack,
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
                .verticalScroll(scrollState)
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

            if (isLoading) {
                // Estado de Carregamento (Skeletons)
                CardSkeletonEquipamento()
                CardSkeletonEquipamento()
                CardSkeletonEquipamento()

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .background(Color(0xFF131B31), shape = MaterialTheme.shapes.medium)
                )
            } else {
                // Listagem normal de equipamentos após o carregamento
                if (abaSelecionada == 0) {
                    CardEquipamento(
                        nome = "Supino Reto",
                        setor = "Musculação",
                        status = "DISPONÍVEL",
                        statusColor = Color(0xFF4CAF50)
                    )
                    CardEquipamento(
                        nome = "Leg Press 45°",
                        setor = "Musculação",
                        status = "DISPONÍVEL",
                        statusColor = Color(0xFF4CAF50)
                    )
                    CardEquipamento(
                        nome = "Esteira 3",
                        setor = "Cardio",
                        status = "MANUTENÇÃO",
                        statusColor = Color(0xFF8B93A5)
                    )
                } else {
                    CardEquipamento(
                        nome = "Esteira 3",
                        setor = "Cardio",
                        status = "MANUTENÇÃO",
                        statusColor = Color(0xFF8B93A5)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = onCadastrarClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE65100)
                    ),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(
                        text = "Cadastrar equipamento +",
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

@Composable
fun CardEquipamento(
    nome: String,
    setor: String,
    status: String,
    statusColor: Color
) {
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
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
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
        TelaGerenciarEquipamentos(isLoadingInicial = false)
    }
}