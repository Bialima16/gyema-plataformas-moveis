package br.com.gyema.ui.telas

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.gyema.ui.components.CardGyema
import br.com.gyema.ui.tema.*

data class HistoricoTreinoItem(
    val id: String,
    val data: String,
    val titulo: String,
    val foco: String,
    val duracao: String,
    val exercicios: String
)

@Composable
fun TelaDetalhesAluno(
    modifier: Modifier = Modifier,
    alunoNome: String = "João Silva",
    alunoCurso: String = "Eng. Computação",
    alunoIniciais: String = "JS",
    onVoltarClick: () -> Unit = {},
    onFichaPrincipalClick: () -> Unit = {},
    onHistoricoItemClick: (historicoId: String) -> Unit = {},
    onCriarNovaFichaClick: () -> Unit = {}
) {
    val historicosMocados = listOf(
        HistoricoTreinoItem("1", "25/09", "Treino A", "Peito e Tríceps", "45m", "Supino reto, Supino inclinado, Crucifixo..."),
        HistoricoTreinoItem("2", "23/09", "Treino B", "Costas e Bíceps", "50m", "Puxada frontal, Remada curvada, Rosca direta..."),
        HistoricoTreinoItem("3", "20/09", "Treino C", "Inferior Completo", "40m", "Agachamento, Leg press, Cadeira extensora...")
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Fundo)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
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
                    text = "Detalhes do aluno",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            // card perfil aluno
            CardGyema(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(SuperficieEscura)
                            .border(1.dp, Borda, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = alunoIniciais,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = alunoNome,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = alunoCurso,
                            fontSize = 12.sp,
                            color = TextoSecundario
                        )
                    }
                }
            }

            // resumo da evolucao
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "RESUMO DA EVOLUÇÃO",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextoSecundario,
                    letterSpacing = 0.5.sp
                )

                CardGyema(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        BlocoMetrica(
                            rotulo = "Frequência",
                            valor = "12 treinos/mês",
                            modifier = Modifier.weight(1f)
                        )
                        BlocoMetrica(
                            rotulo = "Tempo total",
                            valor = "18h treinadas",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // card ficha principal
            CardGyema(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                onClick = onFichaPrincipalClick
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = "Treino A — Ficha Principal",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Peito e Tríceps",
                            fontSize = 12.sp,
                            color = TextoSecundario
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.FitnessCenter,
                                contentDescription = null,
                                tint = TextoSecundario,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "6 exercícios",
                                fontSize = 12.sp,
                                color = TextoSecundario
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(SuperficieEscura),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Abrir ficha",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            // historico de treinos
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "HISTÓRICO DE TREINOS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextoSecundario,
                    letterSpacing = 0.5.sp
                )

                historicosMocados.forEach { h ->
                    CardHistoricoTreino(
                        item = h,
                        onClick = { onHistoricoItemClick(h.id) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // botao criar nova ficha
        val shapeBotao = RoundedCornerShape(14.dp)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .clip(shapeBotao)
                .background(Laranja)
                .clickable(onClick = onCriarNovaFichaClick),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Criar nova ficha",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
private fun BlocoMetrica(
    rotulo: String,
    valor: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(SuperficieEscura)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = rotulo,
            fontSize = 11.sp,
            color = TextoSecundario
        )
        Text(
            text = valor,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

@Composable
private fun CardHistoricoTreino(
    item: HistoricoTreinoItem,
    onClick: () -> Unit
) {
    CardGyema(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(RoxoEscuro)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = item.data,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Laranja
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "${item.titulo} → ${item.foco}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.weight(1f)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.AccessTime,
                        contentDescription = null,
                        tint = TextoSecundario,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = item.duracao,
                        fontSize = 11.sp,
                        color = TextoSecundario
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = item.exercicios,
                    fontSize = 11.sp,
                    color = TextoSecundario,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Outlined.EditNote,
                    contentDescription = null,
                    tint = TextoSecundario,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

// preview da tela
@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
private fun PreviewTelaDetalhesAluno() {
    GyemaTheme {
        TelaDetalhesAluno()
    }
}
