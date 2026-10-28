package br.com.gyema.ui.telas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// 1. Tela de Equipamentos - Vazio
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaGerenciarEquipamentosVazio(
    onBackClick: () -> Unit = {},
    onCadastrarClick: () -> Unit = {}
) {
    var abaSelecionada by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Equipamentos", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Voltar", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0B1021))
            )
        },
        containerColor = Color(0xFF0B1021),
        bottomBar = {
            Box(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                Button(
                    onClick = onCadastrarClick,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE65100)),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text("Cadastrar equipamento +", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF131B31), shape = MaterialTheme.shapes.medium)
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    TabBotao(texto = "Todos", selecionado = abaSelecionada == 0, modifier = Modifier.weight(1f)) { abaSelecionada = 0 }
                    TabBotao(texto = "Manutenção", selecionado = abaSelecionada == 1, modifier = Modifier.weight(1f)) { abaSelecionada = 1 }
                }
            }

            // Conteúdo Vazio ao centro
            Column(
                modifier = Modifier.fillMaxWidth().weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .background(Color(0xFF131B31), shape = MaterialTheme.shapes.extraLarge),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Outlined.Warning, contentDescription = null, tint = Color(0xFFE65100), modifier = Modifier.size(36.dp))
                }
                Spacer(modifier = Modifier.height(20.dp))
                Text("Nenhum equipamento\nencontrado", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(10.dp))
                Text("Tente novamente mais tarde. Se o erro\npersistir, fale com a administração.", color = Color(0xFF8B93A5), fontSize = 14.sp, textAlign = TextAlign.Center)
            }
        }
    }
}

// 2. Tela de Equipamentos - Erro
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaGerenciarEquipamentosErro(
    onBackClick: () -> Unit = {},
    onTentarNovamente: () -> Unit = {}
) {
    var abaSelecionada by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Equipamentos", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Voltar", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0B1021))
            )
        },
        containerColor = Color(0xFF0B1021),
        bottomBar = {
            Box(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                Button(
                    onClick = {},
                    enabled = false,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(disabledContainerColor = Color(0xFF131B31)),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text("Cadastrar equipamento +", color = Color(0xFF8B93A5), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF131B31), shape = MaterialTheme.shapes.medium)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                TabBotao(texto = "Todos", selecionado = abaSelecionada == 0, modifier = Modifier.weight(1f)) { abaSelecionada = 0 }
                TabBotao(texto = "Manutenção", selecionado = abaSelecionada == 1, modifier = Modifier.weight(1f)) { abaSelecionada = 1 }
            }

            Column(
                modifier = Modifier.fillMaxWidth().weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .background(Color(0xFF131B31), shape = MaterialTheme.shapes.extraLarge),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Outlined.Warning, contentDescription = null, tint = Color(0xFFE65100), modifier = Modifier.size(36.dp))
                }
                Spacer(modifier = Modifier.height(20.dp))
                Text("Não foi possível carregar", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(10.dp))
                Text("Verifique sua conexão de rede ou as\nconfigurações do servidor da Unifor.", color = Color(0xFF8B93A5), fontSize = 14.sp, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onTentarNovamente,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE65100)),
                    shape = MaterialTheme.shapes.small
                ) {
                    Text("Tentar novamente", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// 3. Tela de Relatos de Problemas
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaRelatosProblemas(
    onBackClick: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Relatos de problemas", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Voltar", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0B1021))
            )
        },
        containerColor = Color(0xFF0B1021)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CardRelato("Hack Squat", "22/09/2026", "Cabo de aço frouxo", "Relatado por: Ana Lima")
            CardRelato("Supino reto", "20/09/2026", "Banco rasgado no assento", "Relatado por: Carlos Souza")
            CardRelato("Crossover", "18/09/2026", "Roldana fazendo barulho", "Relatado por: Marina Costa")
        }
    }
}

@Composable
fun CardRelato(equipamento: String, data: String, problema: String, autor: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF131B31))
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(equipamento, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text(data, color = Color(0xFF8B93A5), fontSize = 12.sp)
            }
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(problema, color = Color.White, fontSize = 14.sp)
                Text(autor, color = Color(0xFF8B93A5), fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Button(
                onClick = {},
                modifier = Modifier.fillMaxWidth().height(42.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1C2642)),
                shape = MaterialTheme.shapes.small
            ) {
                Text("Marcar como resolvido", color = Color(0xFFE65100), fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    }
}