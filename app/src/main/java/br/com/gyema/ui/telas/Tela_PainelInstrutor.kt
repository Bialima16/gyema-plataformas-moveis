package br.com.gyema.ui.telas

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Business
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.TextButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.gyema.data.PerfilResumo
import br.com.gyema.data.RepositorioInstrutor
import br.com.gyema.data.mensagemDeErro
import br.com.gyema.data.tentar
import br.com.gyema.ui.components.CardGyema
import br.com.gyema.ui.tema.*

@Composable
fun TelaPainelInstrutor(
    modifier: Modifier = Modifier,
    onMeusAlunosClick: () -> Unit = {},
    onFichasTreinoClick: () -> Unit = {},
    onEquipamentosClick: () -> Unit = {},
    onRelatosProblemasClick: () -> Unit = {},
    onNotificacoesClick: () -> Unit = {},
    onSairClick: () -> Unit = {}
) {
    var perfil by remember { mutableStateOf<PerfilResumo?>(null) }
    var confirmarSaida by remember { mutableStateOf(false) }
    var relatosAbertos by remember { mutableIntStateOf(0) }
    var aviso by remember { mutableStateOf<String?>(null) }

    // Roda de novo sempre que o painel volta a aparecer (ex.: depois de resolver um relato)
    LaunchedEffect(Unit) {
        tentar { RepositorioInstrutor.meuPerfil() }
            .onSuccess {
                perfil = it
                aviso = if (it.perfil != "instrutor")
                    "Esta conta é de aluno. Para usar a área do instrutor, promova a conta no banco (veja supabase/README.md)." else null
            }
            .onFailure { aviso = mensagemDeErro(it) }
        tentar { RepositorioInstrutor.contarRelatosAbertos() }.onSuccess { relatosAbertos = it }
    }

    val primeiroNome = perfil?.nome?.trim()?.substringBefore(' ').orEmpty()

    if (confirmarSaida) {
        AlertDialog(
            onDismissRequest = { confirmarSaida = false },
            containerColor = Superficie,
            title = { Text("Sair da conta?", fontWeight = FontWeight.Bold, color = Color.White) },
            text = { Text("Você vai voltar para a tela de boas-vindas.", color = TextoSecundario) },
            confirmButton = {
                TextButton(onClick = { confirmarSaida = false; onSairClick() }) {
                    Text("Sair", color = Laranja, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmarSaida = false }) { Text("Cancelar", color = TextoSecundario) }
            }
        )
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // cabecalho instrutor
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Laranja),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = primeiroNome.take(1).uppercase().ifEmpty { "?" },
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (primeiroNome.isEmpty()) "Olá!" else "Olá, Prof. $primeiroNome!",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Área do Instrutor • Unifor",
                        fontSize = 12.sp,
                        color = TextoSecundario
                    )
                }
                SinoRelatos(quantidade = relatosAbertos, onClick = onNotificacoesClick)
                Spacer(modifier = Modifier.width(8.dp))
                // Sair da conta (pede confirmação)
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(SuperficieEscura)
                        .clickable { confirmarSaida = true },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.Logout,
                        contentDescription = "Sair da conta",
                        tint = TextoSecundario,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            aviso?.let { texto ->
                CardGyema(modifier = Modifier.fillMaxWidth(), fundo = LaranjaEscuro) {
                    Text(
                        text = texto,
                        fontSize = 13.sp,
                        color = Color.White,
                        modifier = Modifier.padding(14.dp)
                    )
                }
            }

            // acesso rapido
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "ACESSO RÁPIDO",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextoSecundario,
                    letterSpacing = 0.5.sp
                )

                CardAcessoRapido(
                    titulo = "Meus alunos",
                    subtitulo = "Ver lista e fichas ativas",
                    icone = Icons.Outlined.Group,
                    onClick = onMeusAlunosClick
                )

                CardAcessoRapido(
                    titulo = "Fichas de treino",
                    subtitulo = "Criar e editar templates de treinos",
                    icone = Icons.AutoMirrored.Outlined.Assignment,
                    onClick = onFichasTreinoClick
                )

                CardAcessoRapido(
                    titulo = "Equipamentos",
                    subtitulo = "Consultar e reportar manutenção",
                    icone = Icons.Outlined.FitnessCenter,
                    onClick = onEquipamentosClick
                )

                CardAcessoRapido(
                    titulo = "Relatos de problemas",
                    subtitulo = when (relatosAbertos) {
                        0 -> "Nenhum relato em aberto"
                        1 -> "1 relato em aberto"
                        else -> "$relatosAbertos relatos em aberto"
                    },
                    icone = Icons.Default.Warning,
                    iconeTint = Color.White,
                    onClick = onRelatosProblemasClick
                )
            }
        }

        // barra de navegacao do instrutor
        BarraNavegacaoInstrutor(
            itemSelecionado = 0,
            onItemSelecionado = { index ->
                when (index) {
                    1 -> onMeusAlunosClick()
                    2 -> onFichasTreinoClick()
                    3 -> onEquipamentosClick()
                }
            }
        )
    }
}

@Composable
private fun SinoRelatos(quantidade: Int, onClick: () -> Unit) {
    Box(contentAlignment = Alignment.TopEnd) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(SuperficieEscura)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Notifications,
                contentDescription = if (quantidade > 0) "$quantidade relatos em aberto" else "Notificações",
                tint = if (quantidade > 0) Color.White else TextoSecundario,
                modifier = Modifier.size(20.dp)
            )
        }
        if (quantidade > 0) {
            Box(
                modifier = Modifier
                    .offset(x = 4.dp, y = (-4).dp)
                    .defaultMinSize(minWidth = 18.dp, minHeight = 18.dp)
                    .clip(CircleShape)
                    .background(Laranja)
                    .padding(horizontal = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (quantidade > 9) "9+" else quantidade.toString(),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun CardAcessoRapido(
    titulo: String,
    subtitulo: String,
    icone: ImageVector,
    iconeTint: Color = Laranja,
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
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(SuperficieEscura),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icone,
                    contentDescription = null,
                    tint = iconeTint,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = titulo,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitulo,
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
private fun BarraNavegacaoInstrutor(
    itemSelecionado: Int = 0,
    onItemSelecionado: (Int) -> Unit = {}
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
            selecionado = itemSelecionado == 0,
            onClick = { onItemSelecionado(0) }
        )
        ItemBarraInstrutor(
            rotulo = "Alunos",
            icone = Icons.Outlined.Group,
            selecionado = itemSelecionado == 1,
            onClick = { onItemSelecionado(1) }
        )
        ItemBarraInstrutor(
            rotulo = "Fichas",
            icone = Icons.Outlined.FitnessCenter,
            selecionado = itemSelecionado == 2,
            onClick = { onItemSelecionado(2) }
        )
        ItemBarraInstrutor(
            rotulo = "Equipamentos",
            icone = Icons.Outlined.Business,
            selecionado = itemSelecionado == 3,
            onClick = { onItemSelecionado(3) }
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
private fun PreviewTelaPainelInstrutor() {
    GyemaTheme {
        TelaPainelInstrutor()
    }
}
