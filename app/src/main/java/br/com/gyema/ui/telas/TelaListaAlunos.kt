package br.com.gyema.ui.telas

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Business
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.gyema.data.Aluno
import br.com.gyema.data.RepositorioInstrutor
import br.com.gyema.data.mensagemDeErro
import br.com.gyema.data.tentar
import br.com.gyema.ui.components.CardGyema
import br.com.gyema.ui.components.EstadoCarregando
import br.com.gyema.ui.components.EstadoErro
import br.com.gyema.ui.components.EstadoVazio
import br.com.gyema.ui.components.iniciais
import br.com.gyema.ui.tema.*

@Composable
fun TelaListaAlunos(
    modifier: Modifier = Modifier,
    onVoltarClick: () -> Unit = {},
    onAlunoClick: (alunoId: String) -> Unit = {},
    onPainelClick: () -> Unit = {},
    onFichasClick: () -> Unit = {},
    onEquipamentosClick: () -> Unit = {}
) {
    var busca by remember { mutableStateOf("") }
    var alunos by remember { mutableStateOf<List<Aluno>?>(null) }
    var erro by remember { mutableStateOf<String?>(null) }
    var tentativa by remember { mutableIntStateOf(0) }

    LaunchedEffect(tentativa) {
        erro = null
        tentar { RepositorioInstrutor.listarAlunos() }
            .onSuccess { alunos = it }
            .onFailure { erro = mensagemDeErro(it) }
    }

    val alunosFiltrados = remember(busca, alunos) {
        val termo = busca.trim()
        alunos.orEmpty().filter {
            termo.isEmpty() ||
                it.nome.contains(termo, ignoreCase = true) ||
                it.curso.orEmpty().contains(termo, ignoreCase = true) ||
                it.matricula.orEmpty().contains(termo)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Fundo)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // cabecalho
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(SuperficieEscura)
                        .clickable(onClick = onVoltarClick),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Voltar",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Meus Alunos",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            // campo de busca
            val shapeBusca = RoundedCornerShape(14.dp)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(shapeBusca)
                    .background(SuperficieEscura)
                    .border(1.dp, Borda, shapeBusca)
                    .padding(horizontal = 14.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = null,
                        tint = TextoSecundario,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    BasicTextField(
                        value = busca,
                        onValueChange = { busca = it },
                        modifier = Modifier.weight(1f),
                        textStyle = TextStyle(
                            color = Color.White,
                            fontSize = 13.sp
                        ),
                        cursorBrush = SolidColor(Laranja),
                        singleLine = true,
                        decorationBox = { innerTextField ->
                            if (busca.isEmpty()) {
                                Text(
                                    text = "Buscar por nome, curso ou matrícula...",
                                    color = TextoSecundario,
                                    fontSize = 13.sp
                                )
                            }
                            innerTextField()
                        }
                    )
                }
            }

            // secao lista de alunos
            Text(
                text = "Ativos no sistema (${alunosFiltrados.size})",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextoSecundario,
                letterSpacing = 0.5.sp
            )

            when {
                erro != null -> EstadoErro(erro!!, onTentarNovamente = { tentativa++ })
                alunos == null -> EstadoCarregando()
                alunos!!.isEmpty() -> EstadoVazio(
                    "Nenhum aluno cadastrado",
                    "Os alunos aparecem aqui depois de criarem a conta pelo app."
                )
                alunosFiltrados.isEmpty() -> EstadoVazio(
                    "Nenhum aluno encontrado",
                    "Confira o nome, o curso ou a matrícula digitados."
                )
                else -> LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(alunosFiltrados, key = { it.id }) { aluno ->
                        CardAluno(
                            aluno = aluno,
                            onClick = { onAlunoClick(aluno.id) }
                        )
                    }
                }
            }
        }

        // barra de navegacao do instrutor
        BarraNavegacaoInstrutorAlunos(
            onPainelClick = onPainelClick,
            onAlunosClick = { },
            onFichasClick = onFichasClick,
            onEquipamentosClick = onEquipamentosClick
        )
    }
}

@Composable
private fun CardAluno(
    aluno: Aluno,
    onClick: () -> Unit
) {
    CardGyema(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        onClick = onClick
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
                    .clip(CircleShape)
                    .background(SuperficieEscura)
                    .border(1.dp, Borda, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = iniciais(aluno.nome),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = aluno.nome,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = listOfNotNull(aluno.curso, aluno.matricula?.let { "Mat. $it" })
                        .joinToString(" • ").ifEmpty { "Curso não informado" },
                    fontSize = 12.sp,
                    color = TextoSecundario
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Outlined.ChevronRight,
                contentDescription = null,
                tint = TextoSecundario,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun BarraNavegacaoInstrutorAlunos(
    onPainelClick: () -> Unit = {},
    onAlunosClick: () -> Unit = {},
    onFichasClick: () -> Unit = {},
    onEquipamentosClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(SuperficieEscura)
            .padding(vertical = 10.dp, horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ItemBarraInstrutor(
            rotulo = "Painel",
            icone = Icons.Outlined.Home,
            selecionado = false,
            onClick = onPainelClick
        )
        ItemBarraInstrutor(
            rotulo = "Alunos",
            icone = Icons.Outlined.Group,
            selecionado = true,
            onClick = onAlunosClick
        )
        ItemBarraInstrutor(
            rotulo = "Fichas",
            icone = Icons.Outlined.FitnessCenter,
            selecionado = false,
            onClick = onFichasClick
        )
        ItemBarraInstrutor(
            rotulo = "Equipamentos",
            icone = Icons.Outlined.Business,
            selecionado = false,
            onClick = onEquipamentosClick
        )
    }
}

@Composable
private fun ItemBarraInstrutor(
    rotulo: String,
    icone: ImageVector,
    selecionado: Boolean,
    onClick: () -> Unit
) {
    val cor = if (selecionado) Laranja else TextoSecundario
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Icon(
            imageVector = icone,
            contentDescription = rotulo,
            tint = cor,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = rotulo,
            fontSize = 11.sp,
            color = cor,
            fontWeight = if (selecionado) FontWeight.Bold else FontWeight.Normal
        )
    }
}

// preview da tela
@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
private fun PreviewTelaListaAlunos() {
    GyemaTheme {
        TelaListaAlunos()
    }
}
