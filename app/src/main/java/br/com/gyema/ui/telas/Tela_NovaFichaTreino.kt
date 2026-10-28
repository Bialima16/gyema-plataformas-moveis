package br.com.gyema.ui.telas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaNovaFichaTreino(
    onBackClick: () -> Unit = {},
    onSaveClick: () -> Unit = {}
) {
    var aluno by remember { mutableStateOf("João Silva") }
    var nomeFicha by remember { mutableStateOf("Treino A — Peito e Tríceps") }
    var pesquisaExercicio by remember { mutableStateOf("") }

    var obsSupinoReto by remember { mutableStateOf("Manter peito baixo e empurrar com o peito.") }
    var tecSupinoReto by remember { mutableStateOf("Bi-set com tríceps barra reta.") }

    var obsSupinoInclinado by remember { mutableStateOf("Inclinação leve e foco no peito superior.") }
    var tecSupinoInclinado by remember { mutableStateOf("Drop-set na última série.") }

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Nova ficha de treino",
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
        // CORREÇÃO: Usar .fillMaxWidth() em vez de .fillMaxSize() para o scroll fluir livremente
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Identificação do aluno
            CampoTextoPersonalizado(
                label = "Identificação do aluno",
                value = aluno,
                onValueChange = { aluno = it }
            )

            // Nome da ficha
            CampoTextoPersonalizado(
                label = "Nome da ficha",
                value = nomeFicha,
                onValueChange = { nomeFicha = it }
            )

            // Adicionar exercícios (Barra de pesquisa)
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
                    placeholder = { Text("Selecionar exercícios", color = Color(0xFF8B93A5)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = Color(0xFF8B93A5)
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF1E2640),
                        unfocusedBorderColor = Color(0xFF1E2640),
                        focusedContainerColor = Color(0xFF131B31),
                        unfocusedContainerColor = Color(0xFF131B31),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )
            }

            // Card 1: Supino reto
            CardExercicioTreino(
                titulo = "Supino reto",
                series = "4",
                reps = "12",
                carga = "40kg",
                descanso = "90s",
                observacoes = obsSupinoReto,
                onObservacoesChange = { obsSupinoReto = it },
                tecnicas = tecSupinoReto,
                onTecnicasChange = { tecSupinoReto = it },
                onDelete = {}
            )

            // Card 2: Supino inclinado
            CardExercicioTreino(
                titulo = "Supino inclinado",
                series = "4",
                reps = "10",
                carga = "35kg",
                descanso = "90s",
                observacoes = obsSupinoInclinado,
                onObservacoesChange = { obsSupinoInclinado = it },
                tecnicas = tecSupinoInclinado,
                onTecnicasChange = { tecSupinoInclinado = it },
                onDelete = {}
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Botão Salvar Ficha
            Button(
                onClick = onSaveClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE65100)
                ),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    text = "Salvar ficha",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Espaçamento extra no final para garantir respiro ao rolar tudo
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun CampoTextoPersonalizado(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
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
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF1E2640),
                unfocusedBorderColor = Color(0xFF1E2640),
                focusedContainerColor = Color(0xFF131B31),
                unfocusedContainerColor = Color(0xFF131B31),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )
    }
}

@Composable
fun CardExercicioTreino(
    titulo: String,
    series: String,
    reps: String,
    carga: String,
    descanso: String,
    observacoes: String,
    onObservacoesChange: (String) -> Unit,
    tecnicas: String,
    onTecnicasChange: (String) -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF131B31)),
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
                Text(
                    text = titulo,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Remover exercício",
                        tint = Color(0xFFEF5350)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ItemMetrica("Séries", series, modifier = Modifier.weight(1f))
                ItemMetrica("Reps", reps, modifier = Modifier.weight(1f))
                ItemMetrica("Carga", carga, modifier = Modifier.weight(1f))
                ItemMetrica("Descanso", descanso, modifier = Modifier.weight(1f))
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = "Observações técnicas", color = Color(0xFF8B93A5), fontSize = 12.sp)
                OutlinedTextField(
                    value = observacoes,
                    onValueChange = onObservacoesChange,
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.small,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF1E2640),
                        unfocusedBorderColor = Color(0xFF1E2640),
                        focusedContainerColor = Color(0xFF0B1021),
                        unfocusedContainerColor = Color(0xFF0B1021),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = "Técnicas especiais", color = Color(0xFF8B93A5), fontSize = 12.sp)
                OutlinedTextField(
                    value = tecnicas,
                    onValueChange = onTecnicasChange,
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.small,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF1E2640),
                        unfocusedBorderColor = Color(0xFF1E2640),
                        focusedContainerColor = Color(0xFF0B1021),
                        unfocusedContainerColor = Color(0xFF0B1021),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )
            }
        }
    }
}

@Composable
fun ItemMetrica(label: String, valor: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(text = label, color = Color(0xFF8B93A5), fontSize = 11.sp)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0B1021), shape = MaterialTheme.shapes.small)
                .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text = valor, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0B1021)
@Composable
fun TelaNovaFichaTreinoPreview() {
    GyemaTheme {
        TelaNovaFichaTreino()
    }
}