package br.com.gyema.ui.telas

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.gyema.ui.components.CardGyema
import br.com.gyema.ui.tema.*

private data class ItemMenu(val titulo: String, val icone: ImageVector, val destaque: Boolean = false)

private val itensMenu = listOf(
    ItemMenu("Editar perfil", Icons.Outlined.Edit),
    ItemMenu("Notificações", Icons.Outlined.Notifications),
    ItemMenu("Privacidade e segurança", Icons.Outlined.Shield),
    ItemMenu("Acessibilidade", Icons.Outlined.Visibility),
    ItemMenu("Ajuda e suporte", Icons.AutoMirrored.Outlined.HelpOutline),
    ItemMenu("Sair da conta", Icons.AutoMirrored.Outlined.Logout, destaque = true)
)

@Composable
fun TelaPerfil(onSair: () -> Unit = {}, onSuporte: () -> Unit = {}, onNotificacoes: () -> Unit = {}) {
    var abaSelecionada by remember { mutableIntStateOf(0) }

    Column(
        Modifier
            .fillMaxSize()
            .background(Fundo)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Meu Perfil", fontSize = 18.sp, fontWeight = FontWeight.Bold)

        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(64.dp).border(2.dp, Laranja, CircleShape),
                contentAlignment = Alignment.Center
            ) { Text("JS", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Laranja) }
            Spacer(Modifier.width(14.dp))
            Column {
                Text("João Silva", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text("Eng. da Computação • Unifor", fontSize = 12.sp, color = TextoSecundario)
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
                // Estatísticas (troque o conteúdo conforme abaSelecionada quando precisar)
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Estatistica("Frequência", "12 treinos /mês", Modifier.weight(1f))
                    Estatistica("Tempo total", "18h treinadas", Modifier.weight(1f))
                }
            }
        }

        CardGyema(Modifier.fillMaxWidth()) {
            Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.MonitorHeart, null, tint = Laranja, modifier = Modifier.size(22.dp))
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text("Medidas corporais", fontSize = 12.sp, color = TextoSecundario)
                    Text("Peso: 72kg | Altura: 1,75m", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
                Box(
                    Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Borda)
                        .clickable { }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) { Text("Editar", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
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