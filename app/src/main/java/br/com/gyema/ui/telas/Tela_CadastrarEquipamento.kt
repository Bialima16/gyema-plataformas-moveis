package br.com.gyema.ui.telas

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
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
fun TelaCadastrarEquipamento(
    onBackClick: () -> Unit = {},
    onSaveClick: () -> Unit = {}
) {
    var nomeEquipamento by remember { mutableStateOf("") }
    var identificacao by remember { mutableStateOf("") }
    var setorSelecionado by remember { mutableStateOf("Selecione o setor") }
    var grupoMuscular by remember { mutableStateOf("Selecione o grupo muscular") }
    var capacidadeSimultanea by remember { mutableStateOf("") }
    var tempoMaximo by remember { mutableStateOf("") }
    var statusFuncionamento by remember { mutableStateOf("Disponível") } // Disponível ou Em manutenção

    var exercicioPrincipal by remember { mutableStateOf("Supino reto") }
    var exercicioAlternativo by remember { mutableStateOf("Supino inclinado") }

    var instrucoesUso by remember { mutableStateOf("") }
    var videoLink by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Cadastrar equipamento",
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
            // Nome do equipamento
            CampoFormEquipamento(
                label = "Nome do equipamento",
                value = nomeEquipamento,
                onValueChange = { nomeEquipamento = it },
                placeholder = "Ex: Supino Reto"
            )

            // Identificação
            CampoFormEquipamento(
                label = "Identificação",
                value = identificacao,
                onValueChange = { identificacao = it },
                placeholder = "Ex: SUP-01"
            )

            // Setor
            CampoDropdownEquipamento(
                label = "Setor",
                value = setorSelecionado,
                onClick = { /* Lógica para abrir seletor */ }
            )

            // Grupo muscular relacionado
            CampoDropdownEquipamento(
                label = "Grupo muscular relacionado",
                value = grupoMuscular,
                onClick = { /* Lógica para abrir seletor */ }
            )

            // Capacidade simultânea
            CampoFormEquipamento(
                label = "Capacidade simultânea",
                value = capacidadeSimultanea,
                onValueChange = { capacidadeSimultanea = it },
                placeholder = "Ex: 1 pessoa"
            )

            // Tempo máximo de utilização
            CampoFormEquipamento(
                label = "Tempo máximo de utilização (min)",
                value = tempoMaximo,
                onValueChange = { tempoMaximo = it },
                placeholder = "Ex: 30"
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
                        selecionado = statusFuncionamento == "Disponível",
                        modifier = Modifier.weight(1f),
                        onClick = { statusFuncionamento = "Disponível" }
                    )
                    TabStatusEquipamento(
                        texto = "Em manutenção",
                        selecionado = statusFuncionamento == "Em manutenção",
                        modifier = Modifier.weight(1f),
                        onClick = { statusFuncionamento = "Em manutenção" }
                    )
                }
            }

            // Exercícios principais
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Exercícios principais",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    placeholder = { Text("Selecionar exercícios", color = Color(0xFF8B93A5)) },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF8B93A5))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    colors = estilocampos()
                )

                if (exercicioPrincipal.isNotEmpty()) {
                    CardItemRemovivel(
                        titulo = exercicioPrincipal,
                        onDelete = { exercicioPrincipal = "" }
                    )
                }
            }

            // Exercícios alternativos
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Exercícios alternativos",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    placeholder = { Text("Selecionar exercícios alternativos", color = Color(0xFF8B93A5)) },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF8B93A5))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    colors = estilocampos()
                )

                if (exercicioAlternativo.isNotEmpty()) {
                    CardItemRemovivel(
                        titulo = exercicioAlternativo,
                        onDelete = { exercicioAlternativo = "" }
                    )
                }
            }

            // Instruções e conteúdos relacionados
            Text(
                text = "Instruções e conteúdos relacionados",
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )

            // Instruções de uso
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
                        .height(110.dp),
                    shape = MaterialTheme.shapes.medium,
                    colors = estilocampos()
                )
            }

            // Vídeo demonstrativo (link)
            CampoFormEquipamento(
                label = "Vídeo demonstrativo (link)",
                value = videoLink,
                onValueChange = { videoLink = it },
                placeholder = "Ex: https://academia.com/supino-reto"
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Botão Salvar
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
                    text = "Salvar",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun CampoFormEquipamento(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String
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
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            colors = estilocampos()
        )
    }
}

@Composable
fun CampoDropdownEquipamento(
    label: String,
    value: String,
    onClick: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = label,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF131B31), shape = MaterialTheme.shapes.medium)
                .clickable { onClick() }
                .padding(horizontal = 16.dp, vertical = 14.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = value,
                    color = if (value.startsWith("Selecione")) Color(0xFF8B93A5) else Color.White,
                    fontSize = 14.sp
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = Color(0xFF8B93A5)
                )
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