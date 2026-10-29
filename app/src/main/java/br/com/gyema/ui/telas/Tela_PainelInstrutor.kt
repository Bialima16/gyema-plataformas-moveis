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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.gyema.ui.components.CardGyema
import br.com.gyema.ui.tema.*

@Composable
fun TelaPainelInstrutor(
    modifier: Modifier = Modifier,
    onMeusAlunosClick: () -> Unit = {},
    onFichasTreinoClick: () -> Unit = {},
    onEquipamentosClick: () -> Unit = {},
    onRelatosProblemasClick: () -> Unit = {},
    onNotificacoesClick: () -> Unit = {}
) {
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
                        text = "R",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Olá, Prof. Ricardo!",
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
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(SuperficieEscura)
                        .clickable(onClick = onNotificacoesClick),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = "Notificações",
                        tint = TextoSecundario,
                        modifier = Modifier.size(20.dp)
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
                    subtitulo = "Ver relatos dos alunos",
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
