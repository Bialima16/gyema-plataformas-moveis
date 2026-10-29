package br.com.gyema.ui.telas

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.gyema.ui.tema.*

/** Protótipo demonstrativo de detalhes do equipamento, sem banco de dados. */
@Composable
fun TelaDetalhesEquipamento(
    onVoltar: () -> Unit = {},
    onIniciarUso: () -> Unit = {},
    onReportarProblema: () -> Unit = {}
) {
    Column(Modifier.fillMaxSize().background(Fundo)) {
        Row(
            Modifier.fillMaxWidth().height(56.dp).padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar",
                modifier = Modifier.size(24.dp).clickable(onClick = onVoltar), tint = Color.White)
            Spacer(Modifier.width(12.dp))
            Text("GYEMA", fontWeight = FontWeight.Bold, fontSize = 20.sp,
                color = Color.White, modifier = Modifier.weight(1f))
            Icon(Icons.Outlined.Notifications, "Notificações", tint = TextoSecundario)
        }
        Column(
            Modifier.weight(1f).verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Superficie),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.FitnessCenter, null, tint = Laranja,
                            modifier = Modifier.size(36.dp))
                        Spacer(Modifier.weight(1f))
                        Surface(color = Color(0xFF193E30), shape = RoundedCornerShape(8.dp)) {
                            Text("DISPONÍVEL", color = Color(0xFF7CE0AA),
                                fontWeight = FontWeight.Bold, fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp))
                        }
                    }
                    Text("Leg Press 45°", color = Color.White,
                        fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Text("Este equipamento está disponível para uso agora.",
                        color = TextoSecundario, fontSize = 13.sp)
                }
            }
            SecaoExercicios("Exercícios principais",
                listOf("Leg Press tradicional", "Leg Press unilateral", "Panturrilha no Leg Press"))
            SecaoExercicios("Exercícios alternativos",
                listOf("Agachamento livre", "Cadeira extensora"))
        }
        Column(Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = onIniciarUso, modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Laranja),
                shape = RoundedCornerShape(12.dp)) {
                Text("Iniciar uso", fontWeight = FontWeight.Bold, color = Color.White)
            }
            OutlinedButton(onClick = onReportarProblema,
                modifier = Modifier.fillMaxWidth().height(44.dp),
                shape = RoundedCornerShape(12.dp)) {
                Text("Reportar problema", color = Color.White)
            }
        }
    }
}

@Composable
private fun SecaoExercicios(titulo: String, itens: List<String>) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(titulo, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
        itens.forEach { nome ->
            Surface(color = Superficie, shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()) {
                Row(Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    Text("•", color = Laranja, fontSize = 18.sp)
                    Spacer(Modifier.width(12.dp))
                    Text(nome, color = Color.White, fontSize = 13.sp)
                }
            }
        }
    }
}
