package br.com.gyema.ui.telas

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val FundoGrupos = Color(0xFF0B1030)
private val CartaoGrupo = Color(0xFF171D40)
private val LaranjaGrupo = Color(0xFFF34B00)
private val MutadoGrupo = Color(0xFFA8A9C2)

@Composable
private fun BotaoGrupo(texto: String, aoClicar: () -> Unit, modifier: Modifier = Modifier) {
    Button(onClick = aoClicar, modifier = modifier.fillMaxWidth().height(50.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(containerColor = LaranjaGrupo)) {
        Text(texto, color = Color.White, fontSize = 14.sp)
    }
}

@Composable
fun TelaGrupos(onCriar: () -> Unit, onChatbot: () -> Unit) {
    val grupos = listOf(Triple("TF", "Treino de Força", "24 membros"),
        Triple("CU", "Corrida Unifor", "18 membros"),
        Triple("YA", "Yoga e Alongamento", "12 membros"))
    Column(Modifier.fillMaxSize().background(FundoGrupos).padding(20.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("Meus grupos", color = Color.White, fontSize = 18.sp,
                fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            TextButton(onClick = onChatbot) { Text("Assistente", color = LaranjaGrupo) }
        }
        Spacer(Modifier.height(16.dp))
        grupos.forEach { (sigla, nome, membros) ->
            Row(Modifier.fillMaxWidth().padding(bottom = 12.dp)
                .background(CartaoGrupo, RoundedCornerShape(12.dp)).padding(14.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(48.dp).border(2.dp, LaranjaGrupo, CircleShape),
                    contentAlignment = Alignment.Center) {
                    Text(sigla, color = LaranjaGrupo, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.width(14.dp))
                Column {
                    Text(nome, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Text(membros, color = MutadoGrupo, fontSize = 12.sp)
                }
            }
        }
        Spacer(Modifier.weight(1f))
        BotaoGrupo("Criar grupo +", onCriar)
    }
}

@Composable
fun TelaCriarGrupo(onVoltar: () -> Unit, iniciarComErro: Boolean = false) {
    var nome by rememberSaveable { mutableStateOf("Treino Avançado Unifor") }
    var descricao by rememberSaveable { mutableStateOf(if (iniciarComErro) "" else
        "Grupo para compartilhar treinos, dicas de execução e organizar encontros na academia.") }
    var publico by rememberSaveable { mutableStateOf(true) }
    var mostrarErro by rememberSaveable { mutableStateOf(iniciarComErro) }
    var confirmar by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxSize().background(FundoGrupos).padding(20.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = onVoltar) { Text("‹", color = Color.White, fontSize = 27.sp) }
            Text("Criar grupo", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(20.dp))
        Text("Nome do grupo", color = MutadoGrupo, fontSize = 13.sp)
        Spacer(Modifier.height(8.dp))
        CampoGrupo(nome, { nome = it; mostrarErro = false }, "Nome do grupo")
        Spacer(Modifier.height(22.dp))
        Text("Descrição", color = MutadoGrupo, fontSize = 13.sp)
        Spacer(Modifier.height(8.dp))
        CampoGrupo(descricao, { descricao = it; mostrarErro = false },
            "Descreva o grupo", altura = 106)
        Spacer(Modifier.height(22.dp))
        Text("Privacidade", color = MutadoGrupo, fontSize = 13.sp)
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            listOf(true to "Público", false to "Privado").forEach { (valor, titulo) ->
                Box(Modifier.weight(1f).height(44.dp)
                    .background(if (publico == valor) LaranjaGrupo else FundoGrupos,
                        RoundedCornerShape(9.dp))
                    .border(1.dp, if (publico == valor) LaranjaGrupo else CartaoGrupo,
                        RoundedCornerShape(9.dp))
                    .clickable { publico = valor }, contentAlignment = Alignment.Center) {
                    Text(titulo, color = if (publico == valor) Color.White else MutadoGrupo,
                        fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
        Spacer(Modifier.weight(1f))
        if (mostrarErro) {
            Text("Insira todos os dados do grupo antes de salvar.",
                color = Color(0xFFFF7C8A), fontSize = 12.sp,
                textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(12.dp))
        }
        BotaoGrupo("Criar grupo", {
            mostrarErro = nome.isBlank() || descricao.isBlank()
            if (!mostrarErro) confirmar = true
        })
    }
    if (confirmar) AlertDialog(onDismissRequest = { confirmar = false },
        title = { Text("Grupo preenchido") },
        text = { Text("O cadastro do grupo depende da integração com o servidor. Nenhum grupo foi criado.") },
        confirmButton = { TextButton(onClick = { confirmar = false }) { Text("Entendi") } })
}

@Composable
private fun CampoGrupo(valor: String, alterar: (String) -> Unit, dica: String, altura: Int = 52) {
    Box(Modifier.fillMaxWidth().height(altura.dp)
        .background(CartaoGrupo, RoundedCornerShape(10.dp))
        .border(1.dp, Color(0xFF292F55), RoundedCornerShape(10.dp))
        .padding(14.dp)) {
        BasicTextField(value = valor, onValueChange = alterar,
            modifier = Modifier.fillMaxSize(), singleLine = altura == 52,
            textStyle = TextStyle(color = Color.White, fontSize = 14.sp),
            cursorBrush = androidx.compose.ui.graphics.SolidColor(LaranjaGrupo),
            decorationBox = { inner ->
                if (valor.isEmpty()) Text(dica, color = MutadoGrupo, fontSize = 13.sp)
                inner()
            })
    }
}
